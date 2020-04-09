package com.yss.fsip.log.core;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.CoreConstants;
import com.alibaba.fastjson.JSON;
import com.yss.fsip.common.util.StringUtil;
import com.yss.fsip.constants.MDCKey;
import com.yss.fsip.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 批量业务日志Appender，Appender负责接收日志API的日志信息<br>
 * 内部再起一个执行器Executor线程，负责批量持久化日志信息<br>
 * 关于日志队列缓冲区阈值配置，需在sofa容器sofa-container/config/logback.xml中进行如下配置：<br>
 * flushSize范围在[1-10000]，为1表示记录每一条发生的日志 <br>
 * &lt;appender name="DB" class="com.yss.sofa.container.util.log.DataBaseAppender"&gt;
 * &lt;flushSize&gt;100&lt;/flushSize&gt;<br>
 * &lt;/appender&gt;
 * @author satan
 * @since 2014-5-20
 */
public class BatchDBLoggerAppender extends AppenderBase<ILoggingEvent> {

	private Queue<ILoggingEvent> queue;
	
	private BatchDBExcetor executor;
	
	private Thread writer;

	private static int EXCEPTIONSTR_OVERFLOW_MSG_BYTE_LENGTH = 0;

	private static String EXCEPTIONSTR_OVERFLOW_MSG = CoreConstants.LINE_SEPARATOR + CoreConstants.TAB + "更多异常信息，请查看系统日志文件...";

	public static final int MAX_EXCEPTIONINFO_LENGTH = 4000;

	Logger log = LoggerFactory.getLogger(BatchDBLoggerAppender.class);
	
	public BatchDBLoggerAppender(){
		queue = new ConcurrentLinkedQueue<ILoggingEvent>();
		executor = new BatchDBExcetor();
		writer = new Thread(executor, "LogWriter_Worker");
		writer.setDaemon(true);
	}

	@Override
	protected void append(ILoggingEvent iLoggingEvent) {

		Map<String,String> map=iLoggingEvent.getMDCPropertyMap();
		if (queue == null) {
			log.error("日志队列未实例化");
			/*throw new SOFARuntimeException("日志队列未实例化");*/
		}
		if (!executor.isRunning()) {
			/* throw new SOFARuntimeException("日志【BatchDBExcetor】未启动或已停止。");*/
		}
		queue.add(iLoggingEvent);
	}

	public void start(){
		super.start();
		if (writer != null) {
			writer.start();
			log.info("业务日志Appender已启动...");
		} else {
			log.error("业务日志Appender启动失败：writer未实例化");
			/*throw new SOFARuntimeException("业务日志Appender启动失败：writer未实例化");*/
		}
	}
	
	public void stop(){
		super.stop();
		if (executor != null) {
			executor.stop();
		}
	}
	
	public void subAppend(ILoggingEvent event) {

	}
	
	public boolean isEmpty(){
		if (queue != null) {
			return queue.isEmpty();
		}
		return true;
	}
	/**
	 * 批量日志数据执行器
	 * @author satan
	 *
	 */
	class BatchDBExcetor implements Runnable {
		
		private volatile boolean RUNNING = true;
		
		private boolean isRunning() {
		  return RUNNING == true;
		}

		public void run() {
		  try {
   			while (RUNNING) {
   				try {
   					if (!queue.isEmpty()) {
   						flush();
   					}
   					// 每1秒后自动执行一次flush，以保证当日志并发较少时，日志记录迟迟不能刷新入库的问题
   					Thread.sleep(1000);
   				} catch (InterruptedException e) {
   					e.printStackTrace();
   				} catch(Error e) {
   				  e.printStackTrace();
   				} catch(Throwable e) {
   				  e.printStackTrace();
   				}
   			}
		  } finally {
		    RUNNING = false;
		  }
		}
		
		/**
		 * 刷新缓冲队列日志到数据库方法
		 */
		public int flush(){

			try {
				int flushSize = 1000;
				// 这里对配置的flushSize对校验，防止配置超出范围情况[1, 10000]
				if (flushSize < 1 || flushSize > 10000) {
					flushSize = 1000;
				}
				ArrayList<Map> maps = new ArrayList<Map>();
				// 一次flush，只刷新flushSize个日志
				for (int i = 0; i < flushSize; i++) {
					ILoggingEvent event = queue.poll();
					if (event != null) {
						maps.add(buildMap(event));
					}
					// 如果队列为空，则跳出循环
					if (queue.isEmpty()) {
						break;
					}
				}
				String logJson=JSON.toJSONString(maps);
				HttpClientUtil.HttpPostWithJson("http://localhost:8999/auditlog/saveAuditLogInfo",logJson);
				if (log != null && log.isDebugEnabled()) {
					log.debug("批量保存日志记录成功::"+ maps.size());
				}
				return maps.size();
			} catch(Exception e) {
				log.error("批量保存日志失败", e);
			}
			return 0;
		}


		protected Map<String,Object> buildMap(ILoggingEvent event){

			Map paramMap = new HashMap<String,Object>();
			paramMap.put("level",event.getLevel().toString());
			paramMap.put("threadName",event.getThreadName());
			paramMap.put("formattedMessage",event.getFormattedMessage());
			paramMap.put("recordTime",new Date(event.getTimeStamp()));
			IThrowableProxy tp = event.getThrowableProxy();
		    if (tp != null) {
				paramMap.put("exception",buildExceptionStr(tp, MAX_EXCEPTIONINFO_LENGTH));
		    }
			paramMap.putAll(event.getMDCPropertyMap());
            return paramMap;
		}


		private String buildExceptionStr(IThrowableProxy tp, int strMaxLength) {

			StringBuilder buf = new StringBuilder();

			while (tp != null) {
				ThrowableProxyUtil.subjoinFirstLine(buf, tp);
				buf.append(CoreConstants.LINE_SEPARATOR);

				StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
				int commonFrames = tp.getCommonFrames();

				for (int i = 0; i < stepArray.length - commonFrames; i++) {
					StackTraceElementProxy step = stepArray[i];
					ThrowableProxyUtil.subjoinSTEP(buf, step);
					buf.append(CoreConstants.LINE_SEPARATOR).append(CoreConstants.TAB);
				}

				if (commonFrames > 0) {
					for (int j = 0; j < commonFrames; j++) {
						buf.append(CoreConstants.TAB);
					}
					buf.append(CoreConstants.TAB).append("...").append(commonFrames).append(" common frames omitted")
							.append(CoreConstants.LINE_SEPARATOR);
				}

				tp = tp.getCause();

				if (cutByMaxLength(buf, strMaxLength)) {
					break;
				}
			}

			return buf.toString();
		}

		private boolean cutByMaxLength(StringBuilder buf, int strMaxLength) {

			if (null == buf) {
				return false;
			}

			if (strMaxLength > 0 && buf.toString().getBytes().length > strMaxLength) {

				while (true) {
					int index = buf.lastIndexOf("\n");

					buf.delete(index, buf.length());
					if (buf.toString().getBytes().length <= strMaxLength - EXCEPTIONSTR_OVERFLOW_MSG_BYTE_LENGTH) {
						break;
					}
				}

				buf.append(EXCEPTIONSTR_OVERFLOW_MSG);

				return true;
			}

			return false;
		}

		/**
		 * 当阻断或停止日志Appender时，需要先将缓冲队列可能剩余日志记录刷新至数据库
		 */
		public void stop(){
			try {
				flush();
			} finally {
				RUNNING = false;
			}
		}
		
	}
}
