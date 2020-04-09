package com.yss.fsip.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务上下文支持类，获取和移除MAP形式的上下文
 * 
 * @author LSP
 *
 */
public final class ContextSupport {
	/**
	 * 线程安全的Map集合
	 */
	private static ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<Map<String, String>>();

	// 登录用户保存的信息，key key，value 参数信息
	private static Map<String, String> parameters = new ConcurrentHashMap<String, String>();

	/**
	 * 设置Map上下文集合
	 * 
	 * @param map
	 */
	public static void setMap(Map<String, String> map) {

		threadLocal.set(map);
	}

	/**
	 * 获取Map上下文
	 * 
	 * @return
	 */
	public static Map<String, String> getMap() {

		Map<String, String> map = threadLocal.get();
		return map;
	}

	/**
	 * 移除Map上下文
	 */
	public static void removeMap() {

		threadLocal.remove();
	}

	/**
	 * 保存登录用户设置的参数
	 * 
	 * @param key
	 * @param value
	 */
	public static void setParameter(String key, String value) {

		
		parameters.put(key, value);
	}

	public static Map<String,String> getParameters(){

		return parameters;
	}
	/**
	 * 保存登录用户设置的参数
	 * 
	 * @param key
	 * @return
	 */
	public static String getParameter(String key) {

		Map<String, String> tmap = getMap();
		String val = null;
		if (tmap != null) {
			val = tmap.get(key);
		}
		if (val == null) {
			val = parameters.get(key);
		}
		return val;
	}

	/**
	 * 移除用户关联的参数
	 */
	public static void removeUserParameters(){
		parameters.clear();
	}

	/**
	 * 移除参数
	 * @param key
	 */
	public static void removeParameter(String key) {
		parameters.remove(key);
	}
}
