package com.yss.fsip.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class YamlUtil {

	private static Logger logger = LoggerFactory.getLogger(YamlUtil.class.getName());

	private static Map<String, Object> config;

	private File file;

	/**
	 * Returns the config
	 *
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getConfig() {
		return config;
	}

	public YamlUtil(File yamlFile) throws IOException {
		InputStream yamlData = null;

		try {
			if (logger.isInfoEnabled()) {
				logger.info("YAML File path:" + yamlFile.getAbsolutePath());
			}
			file = yamlFile;
			yamlData = new FileInputStream(yamlFile);
			Yaml yaml = new Yaml();
			config = (Map<String, Object>) (yaml.load(yamlData));
		} finally {
			if (yamlData != null) {
				yamlData.close();
			}
		}
	}

	/**
	 * 批量替换并写入文件中
	 * 
	 * @param replaceMap
	 * @throws IOException
	 */
	public void valReplaceAndDump(Map<String, Object> replaceMap) throws IOException {
		for (Entry<String, Object> entry : replaceMap.entrySet()) {
			replace(config, entry.getKey(), entry.getValue());
		}
		dumpYaml();
	}

	/**
	 * value值替换并写入文件中
	 * 
	 * @param path
	 * @param val
	 * @throws IOException
	 */
	public void valReplaceAndDump(String path, Object val) throws IOException {
		replace(config, path, val);
		dumpYaml();
	}

	/**
	 * yaml 文件反写
	 * 
	 * @throws IOException
	 */
	public void dumpYaml() throws IOException {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml rewrite = new Yaml(options);
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			rewrite.dump(config, writer);
		} catch (Exception e) {
			logger.error("yaml writer fail, message={}", e.getMessage());
			e.fillInStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * 替换值
	 * 
	 * @param yamlPage
	 * @param elementPath
	 * @param replacement
	 */
	private void replace(Map<String, Object> yamlPage, String elementPath, Object replacement) {
		String[] path = elementPath.split("\\.");
		if (path.length == 1) {
			yamlPage.put(path[0], replacement);
			return;
		}
		replace((Map<String, Object>) yamlPage.get(path[0]), elementPath.substring(elementPath.indexOf(".") + 1),
				replacement);
	}

	public Object elementPath(String elementPath) {
		return elementValue(elementPath, config);
	}

	private Object elementValue(String elementPath, Map<String, Object> yamlPage) {
		Object o = null;
		String[] path = elementPath.split("\\.");
		for (String p : path) {
			if (isPrimitive(yamlPage.get(p).getClass()))
				return yamlPage.get(p);

			yamlPage = (Map<String, Object>) yamlPage.get(p);
			o = yamlPage;
		}
		return o;
	}

	/**
	 * 基础类型判断
	 * 
	 * @param type
	 * @return
	 */
	private static boolean isPrimitive(Class<?> type) {

		return type.isPrimitive() || type == String.class || type == Character.class || type == Boolean.class
				|| type == Byte.class || type == Short.class || type == Integer.class || type == Long.class
				|| type == Float.class || type == Double.class || type == Object.class;
	}

	/**
	 * ymal 转化为properties
	 */
	public static Properties mapToProperties() {
		Properties properties = new Properties();

		mapToProperties(config, null, properties);

		return properties;
	}

	/**
	 * ymal 转化为properties
	 * 
	 * @param map
	 * @param perfix
	 * @param properties
	 */
	private static void mapToProperties(Map<String, Object> map, String perfix, Properties properties) {
		for (String key : map.keySet()) {
			if (map.get(key) instanceof Map) {
				if (perfix == null || perfix.isEmpty()) {
					mapToProperties((Map<String, Object>) map.get(key), key, properties);
				} else {
					mapToProperties((Map<String, Object>) map.get(key), perfix + "." + key, properties);
				}
			} else {
				String value = StringUtil.isEmpty(map.get(key)) ? "" : map.get(key).toString();
				if (key.equals(perfix)) {
					properties.setProperty(key, value);
				} else {
					properties.setProperty(perfix + "." + key, value);
				}
			}

		}
	}

	public static void main(String[] args) throws IOException {
		YamlUtil yamlReader = new YamlUtil(new File("application.yaml"));
		Properties properties = new Properties();
		mapToProperties(yamlReader.config, null, properties);

		System.out.println(yamlReader.elementPath("spring.datasource.sofa.password.pwd"));
		yamlReader.valReplaceAndDump("spring.datasource.sofa.password.pwd", 1);
		System.out.println(yamlReader.elementPath("spring.datasource.sofa.password.pwd"));
	}

}