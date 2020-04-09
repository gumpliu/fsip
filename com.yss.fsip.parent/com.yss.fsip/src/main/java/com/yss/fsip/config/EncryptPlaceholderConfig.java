package com.yss.fsip.config;

import com.yss.fsip.common.util.EncryptUtils;
import com.yss.fsip.common.util.PropertiesEncrypt;
import com.yss.fsip.common.util.YamlEncrypt;
import com.yss.fsip.constants.FSIPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 需加密key value加密并写入文件
 *
 */
public class EncryptPlaceholderConfig extends PropertyPlaceholderConfigurer {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ConfigurableEnvironment environment;

	/**
	 * 获取需加密key，去掉key加密后缀，重新放入environment中
	 * ResourcePropertySource {name='class path resource [fsip.properties]'}
	 */
	@Override
	protected Properties mergeProperties() throws IOException {
		Properties result = super.mergeProperties();
		MutablePropertySources mutablePropertySources = environment.getPropertySources();
		Iterator<PropertySource<?>> iterator = mutablePropertySources.iterator();
		while (iterator.hasNext()) {
			PropertySource propertySource = iterator.next();
			if (propertySource instanceof OriginTrackedMapPropertySource
					|| propertySource instanceof ResourcePropertySource) {
				Map<String, Object> map = (Map<String, Object>) propertySource.getSource();
				List<String> keys = new ArrayList<String>();
				for (Entry<String, Object> entry : map.entrySet()) {
					if (entry.getKey().endsWith(EncryptUtils.SUFFFIX)) {
						keys.add(entry.getKey().replace(EncryptUtils.SUFFFIX, ""));
					}
				}
				/**
				 * 1、获取需加密key,是否需要解密，解密后重新放入environment中
				 * 2、获取需加密key,去掉key加密后缀，重新放入environment中
				 */
				for (String key : keys) {
					String value = map.get(key + EncryptUtils.SUFFFIX).toString();
					if (value.startsWith(EncryptUtils.ENCRYPTED_PREFIX)) {
						value =  EncryptUtils.decrypt(value);
						map.put(key + EncryptUtils.SUFFFIX, value);
		            }
					map.put(key, value);
				}
			}
		}
		return result;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		environment = beanFactory.getBean(ConfigurableEnvironment.class);
		super.postProcessBeanFactory(beanFactory); // 正常执行属性文件加载
		MutablePropertySources mutablePropertySources = environment.getPropertySources();
		Iterator<PropertySource<?>> iterator = mutablePropertySources.iterator();
		while (iterator.hasNext()) {
			PropertySource propertySource = iterator.next();
			if (propertySource instanceof OriginTrackedMapPropertySource
					|| propertySource instanceof ResourcePropertySource) {
				String name = propertySource.getName();
				try {
					final File file = ResourceUtils.getFile(getFileName(name));
					if (file.isFile()) { // 如果是一个普通文件
						if (file.canWrite()) { // 如果有写权限
							 encrypt(file); //调用文件加密方法
						} else {
							if (logger.isWarnEnabled()) {
								logger.warn("File '" + file.getPath() + "' can not be write!");
							}
						}
					} else {
						if (logger.isWarnEnabled()) {
							logger.warn("File '" + file.getPath() + "' is not a normal file!");
						}
					}
				} catch (IOException e) {
					if (logger.isWarnEnabled()) {
						logger.warn("File '" + name + "' is not a normal file!");
					}
				}
			}
		}
	}

	/**
	 * todo 需要判断大小写，需重新写查看springboot处理
	 *
	 * @param file
	 */
	private void encrypt(File file) {
		if(file.getName().endsWith(".properties")) {
			PropertiesEncrypt.encrypt(file);
		}
		if(file.getName().endsWith(".yaml")) {
			YamlEncrypt.encrypt(file);
		}
	}


	private String getFileName(String named) {
		// 匹配规则
        String regex  = "(?<=\\[)(\\S+)(?=\\])";
        Pattern pattern = Pattern.compile(regex);
        // 内容 与 匹配规则 的测试
        Matcher matcher = pattern.matcher(named);

        if(matcher.find()) {
        	if(matcher.group().startsWith(FSIPConstants.CLSSPATH_PREFIX)) {
            	return matcher.group().replace(":/", ":");
        	}else if(matcher.group().startsWith(FSIPConstants.FILE_PREFIX)) {
        		return System.getProperty("user.dir") + matcher.group().replace(FSIPConstants.FILE_PREFIX, "");
        	}else  {
        		return FSIPConstants.CLSSPATH_PREFIX + matcher.group();
        	}
        }
        return "";
	}

	public static void main(String []args) throws UnsupportedEncodingException {
		try {
			InputStream im = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("config/application.properties");
			System.out.print("111");
			File file = ResourceUtils.getFile(System.getProperty("user.dir") + "/config/application.properties");
			 //第一种
	        File path = new File(ResourceUtils.getURL("classpath:").getPath());
	        if(!path.exists()) path = new File("");
	        System.out.println(path.getAbsolutePath());
	        //第二种
	        System.out.println(System.getProperty("user.dir"));
	        //第三种
	        String path1 = ClassUtils.getDefaultClassLoader().getResource("").getPath();
	        System.out.println(URLDecoder.decode(path1, "utf-8"));
	        //第四种
	        String path2 = ResourceUtils.getURL("classpath:").getPath();
	        System.out.println(path2);
	        //第五种
	        ApplicationHome h = new ApplicationHome(EncryptPlaceholderConfig.class);
	        File jarF = h.getSource();
	        System.out.println(jarF.getParentFile().toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
