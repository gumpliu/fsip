package com.yss.fsip.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * bean获取工具类
 *
 * @Author: gumpLiu
 * @Date: 2019-11-14 11:27
 */
public class BeanUtil implements ApplicationContextAware  {
	
	Logger logger = LoggerFactory.getLogger(BeanUtil.class);
	
    private static ApplicationContext context;  
    
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }
    
    public static <T> T getBean(Class<T> clazz) {
    	
        return getBean(clazz, null);
    }
    
    public static <T> T getBean(Class<T> clazz, String beanName) {
		Map<String, T> beanMap = context.getBeansOfType(clazz);
		if(beanMap != null && !beanMap.isEmpty()) {
			if(beanName != null) {
		        return beanMap.get(beanName);
			}
			return (T) beanMap.values().toArray()[0];
		}
		return null;
    }

    public static <T>   Map<String, T> getBeans(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }
 
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return clazz.cast(getBean(beanName));
    }

    public void setApplicationContext(ApplicationContext applicationContext){
        context = applicationContext;  
    }  
      
    public static ApplicationContext getApplicationContext() {  
        return context;  
    }
}