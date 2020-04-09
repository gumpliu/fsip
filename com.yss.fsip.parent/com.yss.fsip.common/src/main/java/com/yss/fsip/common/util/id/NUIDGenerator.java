package com.yss.fsip.common.util.id;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yss.fsip.common.util.NetUtils;
import com.yss.fsip.common.util.StringUtil;

//import com.yss.sofa.container.util.StringUtil;
//import com.yss.sofa.container.util.config.ContainerConfig;
//import com.yss.sofa.container.util.osgi.OsgiServiceUtil;
//import com.yss.sofa.framework.context.SOFARuntime;
//import com.yss.sofa.framework.util.NetUtils;
//import com.yss.sofa.framework.util.SOFAEnvionmentUtil;

/**
 * 生成SOFA规则的数值型ID NUID（可在集群、单机下保障唯一性、顺序性，但不保证连续性），格式为：{yymmddhhmmss}{aa}{bbbbbb},由三段组成:
 * yymmddhhmmss：12位日期，当前应用服务器时间精度到秒。
 * aa：应用服务器编号，用于标示应用服务器，根据应用服务器的ip地址和进程号作为随机数生成因子。
 * bbbbbb：后6位：本应用服务器生产nuid的计数器，当计数到999999后重新开始计数。
 * 如：16051016093057000058标示，2016年5月10号16点09分30秒标示本应用服务器的标号为57本应用服务器生成id的序号为000058
 * @author jiangjin
 * @version 1.0, 2011-3-19
 * @since 1.0, 2011-3-19
 * @author lenglinyong
 */
public class NUIDGenerator extends AbsIDGenerator implements IDGenerator {
		
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private String appNodeCode;

    static protected  int casheSize = 100;
    
    static protected ArrayBlockingQueue<String> cache = new ArrayBlockingQueue<String>(casheSize);
    // 顺序号最大数
    protected long seqMaxVal = 999990;
    // 顺序号部分长度
    protected int seqValLen = 6;

    // 生产的随机码最大数
    protected int randomMaxVal = 90;
    protected int randomValLen = 2;
    // 用于生成随机数的种子，避免在多服务器下生成重复的号。
    // 用于生成随机数的种子，随机2位id
    static protected int APPSERVERID = 0;
    final static SimpleDateFormat dateTimeFormaterInit = new SimpleDateFormat("yyMMddHHmmss");

    static protected String currentTime = dateTimeFormaterInit.format(System.currentTimeMillis());
    protected static AtomicInteger instanceCount = new AtomicInteger(1);
    
    static {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
            	currentTime = dateTimeFormaterInit.format(System.currentTimeMillis());
            }
        }, 0 , 1000);
    }
    // 顺序号计数器
    protected static AtomicLong globalCount = new AtomicLong(APPSERVERID);

    // 12位日期格式
    protected SimpleDateFormat dateTimeFormater = new SimpleDateFormat("yyMMddHHmmss");
    
    static String dateTime = "";
    
	public NUIDGenerator() {
		this(null);
    }
    
	public NUIDGenerator(String appNodeCode) {
		this.appNodeCode = appNodeCode;    	
		if(!StringUtil.isEmpty(appNodeCode)){
			APPSERVERID = Integer.parseInt(appNodeCode);
		}else{
			try {
				String pid = ManagementFactory.getRuntimeMXBean().getName();
				APPSERVERID = new Random(pid.hashCode() + instanceCount.incrementAndGet()).nextInt(99);
			} catch (Throwable e) {
				APPSERVERID = new Random(NetUtils.getLocalHost().hashCode() + instanceCount.incrementAndGet()).nextInt(99);
			}
		}
    }
	
	
	public String nextId() {
		synchronized(NUIDGenerator.class){
			while (true) {
				String id = this.cache.poll();
				if (id != null) {
					return id;
				}
				this.batchCache();
			}
		}
    }
	
	private void batchCache() {

        cache.clear();

        for (int i = 0; i < this.casheSize; i++) {

            String id = generate(i);
            if (!cache.offer(id)) {
                break;
            }
        }
    }

    @Override
    protected String generate(int order) {
        long seq = globalCount.incrementAndGet();
        if (seq >= seqMaxVal) {
        	checkIllegal();
        	dateTime = dateTimeFormater.format(System.currentTimeMillis());
            globalCount.set(APPSERVERID);
            seq = globalCount.incrementAndGet();
        }
        
//        String date = null;
//        date = dateTimeFormater.format(System.currentTimeMillis());// 12位日期
        
        String randStr = "";
        if (randomMaxVal > 0) {
            randStr = format(this.randomValLen, APPSERVERID, "0");
        }

        String seqStr = format(this.seqValLen, seq, "0");
        
        return currentTime + randStr + seqStr;
    }
    
    /**
     * 根据id生成器的设计思路，每秒的id生成数量最大支持将近百万，如果超过这个数量，会造成id生成重复的情况
     * 当序列号达到最大值后，重新开始计数时，判断当前秒内是否已经生成过id
     */
    private void checkIllegal(){
    	
    	String currentDateTime = dateTimeFormater.format(System.currentTimeMillis());
    	if(!StringUtil.isEmpty(dateTime)&&dateTime.equals(currentDateTime)){
    		logger.warn("NUIDGenerator生成器在当前时间【"+currentDateTime+"】已生成ID超过百万,会造成ID号重复");
    	}
    }
    
    /**
     * 将指定value转换成约定长度
     * @param valLength 返回值长度
     * @param value 初始值
     * @param str 补全变量
     * @return  666->000666
     */
	private String format(int valLength, long value, String str) {

		String returnValue = "";

		int randLength = (value + "").length();
		if (randLength < valLength) {
			String tmp = "";
			for (int i = 0; i < valLength - randLength; i++) {
				tmp = tmp + str;
			}
			returnValue = tmp + value;
		} else {
			returnValue = value + "";
		}

		return returnValue;
	}

    public static void main(String[] args) {

        final NUIDGenerator nuid = new NUIDGenerator();
        System.out.println(nuid.nextId());
//        final Hashtable<String, String> ids = new Hashtable<String, String>();
//
//        final int idMax = 50000;
//        final CountDownLatch cntdown = new CountDownLatch(4);
//
//        long startTime=System.currentTimeMillis();   //获取开始时间
//        Thread t1 = new Thread(new Runnable() {
//
//            public void run() {
//
//                for (int i = 0; i < idMax; i++) {
//                    ids.put(nuid.nextId(), i + "");
//                }
//                System.out.println("ok1");
//                cntdown.countDown();
//            }
//        });
//
//        Thread t2 = new Thread(new Runnable() {
//
//            public void run() {
//
//                for (int i = 0; i < idMax; i++) {
//                    ids.put(nuid.nextId(), i + "");
//                }
//                System.out.println("ok2");
//                cntdown.countDown();
//            }
//        });
//
//        Thread t3 = new Thread(new Runnable() {
//
//            public void run() {
//
//                for (int i = 0; i < idMax; i++) {
//                    ids.put(nuid.nextId(), i + "");
//                }
//                System.out.println("ok3");
//                cntdown.countDown();
//            }
//        });
//        Thread t4 = new Thread(new Runnable() {
//
//            public void run() {
//
//                for (int i = 0; i < idMax; i++) {
//                    ids.put(nuid.nextId(), i + "");
//                }
//                System.out.println("ok4");
//                cntdown.countDown();
//            }
//        });
//
//        t1.start();
//        t2.start();
//        t3.start();
//        t4.start();
//        int i = 0;
//
//        System.out.println("检查id是否重复....");
//        try {
//            cntdown.await();
//        } catch (InterruptedException e1) {
//            e1.printStackTrace();
//        }
//
//        if (ids.size() < 4 * idMax) {
//            System.err.println("id生成重复");
//        } else {
//            System.out.println("无重复id， size＝" + ids.size());
//        }
//
//
//        long endTime=System.currentTimeMillis(); //获取结束时间
//
//        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }

}
