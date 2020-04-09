package com.yss.fsip.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * yaml 加密value写入文件中
 * @author LSP
 *
 */
public class YamlEncrypt {
	
	private static Logger logger = LoggerFactory.getLogger(YamlEncrypt.class);

	/**
	 * yaml 加密
	 * 
	 * @param file
	 */
	public static void encrypt(File file) {		
		try {
			YamlUtil yamlBuilder = new YamlUtil(file);
			Map<String, Object> encryptMap = new HashMap<String, Object>();
			Properties properties = yamlBuilder.mapToProperties();
			for(Entry<Object, Object> entry : properties.entrySet()) {
				if(EncryptUtils.isEncrypt(entry.getKey().toString())) {
					if (!EncryptUtils.isDecrypt(entry.getValue().toString())) {
						encryptMap.put(entry.getKey().toString(), EncryptUtils.encrypt(entry.getValue().toString()));
					}
				}
			}
			yamlBuilder.valReplaceAndDump(encryptMap);
				
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
}
