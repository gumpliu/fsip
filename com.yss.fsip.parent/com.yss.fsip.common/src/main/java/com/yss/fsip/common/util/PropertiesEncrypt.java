package com.yss.fsip.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PropertiesEncrypt {
	
	private static Logger logger = LoggerFactory.getLogger(PropertiesEncrypt.class);

	/**
	 * properties  加密value写入文件中
	 * 
	 * @param file
	 */
	public static void encrypt(File file) {
		List<String> outputLine = new ArrayList<String>(); // 定义输出行缓存
		boolean doEncrypt = false; // 是否加密属性文件标识
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = null;
			do {
				line = bufferedReader.readLine(); // 按行读取属性文件
				if (line != null) { // 判断是否文件结束
					if (isNotBlank(line)) { // 是否为空行
						line = line.trim(); // 取掉左右空格
						if (!line.startsWith("#")) {// 如果是非注释行
							String key = line.substring(0, line.indexOf("=")); // 属性名
							String value = line.substring(line.indexOf("=") + 1, line.length()); // 属性值
							if (key != null && value != null) {
								if (EncryptUtils.isEncrypt(key)) { // 如果是非加密格式，则`进行加密
									if (!EncryptUtils.isDecrypt(value)) {
										value = EncryptUtils.encrypt(value); // 进行加密，SEC_KEY与属性名联合做密钥更安全
										line = key + "=" + value; // 生成新一行的加密串
										doEncrypt = true; // 设置加密属性文件标识
										if (logger.isDebugEnabled()) {
											logger.debug("encrypt property:" + key);
										}
									}
								}
							}
						}
					}
					outputLine.add(line);
				}
			} while (line != null);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		if (doEncrypt) { // 判断属性文件加密标识
			BufferedWriter bufferedWriter = null;
			File tmpFile = null;
			try {
				tmpFile = File.createTempFile(file.getName(), null, file.getParentFile()); // 创建临时文件
				if (logger.isDebugEnabled()) {
					logger.debug("Create tmp file '" + tmpFile.getAbsolutePath() + "'.");
				}
				bufferedWriter = new BufferedWriter(new FileWriter(tmpFile));
				final Iterator<String> iterator = outputLine.iterator();
				while (iterator.hasNext()) { // 将加密后内容写入临时文件
					bufferedWriter.write(iterator.next());
					if (iterator.hasNext()) {
						bufferedWriter.newLine();
					}
				}
				bufferedWriter.flush();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} finally {
				if (bufferedWriter != null) {
					try {
						bufferedWriter.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}

			File backupFile = new File(file.getAbsoluteFile() + "_" + System.currentTimeMillis()); // 准备备份文件名
			// 以下为备份，异常恢复机制
			if (!file.renameTo(backupFile)) { // 重命名原properties文件，（备份）
				logger.error("Could not encrypt the file '" + file.getAbsoluteFile() + "'! Backup the file failed!");
				tmpFile.delete(); // 删除临时文件
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Backup the file '" + backupFile.getAbsolutePath() + "'.");
				}
				if (!tmpFile.renameTo(file)) { // 临时文件重命名失败 （加密文件替换原失败）
					
					logger.error(
							"Could not encrypt the file '" + file.getAbsoluteFile() + "'! Rename the tmp file failed!");
					if (backupFile.renameTo(file)) { // 恢复备份
						if (logger.isInfoEnabled()) {
							logger.info("Restore the backup, success.");
						}
					} else {
						logger.error("Restore the backup, failed!");
					}
				} else { // （加密文件替换原成功）

					if (logger.isDebugEnabled()) {
						logger.debug("Rename the file '" + tmpFile.getAbsolutePath() + "' -> '" + file.getAbsoluteFile()
								+ "'.");
					}

					boolean dBackup = backupFile.delete();// 删除备份文件

					if (logger.isDebugEnabled()) {
						logger.debug("Delete the backup '" + backupFile.getAbsolutePath() + "'.(" + dBackup + ")");
					}
				}
			}

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
