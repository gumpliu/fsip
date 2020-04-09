package com.yss.fsip.common.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncryptUtils {  
	
	public static final String SUFFFIX = ".pwd";
    public static final String ENCRYPTED_PREFIX = "Encrypted:{";
    public static final String ENCRYPTED_SUFFIX = "}";
    private static Pattern encryptedPattern = Pattern.compile("Encrypted:\\{(.*?)\\}");  //加密属性特征正则
    // 密钥
    private static final String KEY = "1234567a?";
      
    public static void main(String[] args) {  
        String ciphertext1 = encrypt("123456"); // Wu11fsC0gpgSET5aU8GXUA==  
        String ciphertext2 = encrypt("123456"); // ESXlHsVk2YM7mGcHy2ccGg==  
        System.out.println(ciphertext1);  
        System.out.println(ciphertext2);  
          
        String text1 = decrypt(ciphertext1);  
        String text2 = decrypt(ciphertext2);  
        System.out.println(text1);               // abcdefg  
        System.out.println(text2);               // abcdefg  
    } 
    
    /**
     * 是否需要加密
     * @param key
     * @return
     */
    public static boolean isEncrypt(String key) {
    	return key.endsWith(SUFFFIX);
    }
    
    /**
     * 是否需要解密
     * 
     * @param value
     * @return
     */
    public static boolean isDecrypt(String value) {
        Matcher matcher = encryptedPattern.matcher(value);
    	return matcher.matches();
    }
      
    /** 
     * 加密 
     * @param text 明文 
     * @return     密文 
     */  
    public static String encrypt(String text) {  
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();  
        encryptor.setPassword(KEY);  
        return EncryptUtils.ENCRYPTED_PREFIX + encryptor.encrypt(text) + EncryptUtils.ENCRYPTED_SUFFIX;
    }  
      
    /** 
     * 解密 
     * @param ciphertext 密文 
     * @return           明文 
     */  
    public static String decrypt(String ciphertext) {  
    	if (ciphertext.startsWith(EncryptUtils.ENCRYPTED_PREFIX)) {
    		ciphertext = ciphertext.replace(EncryptUtils.ENCRYPTED_PREFIX, "").replace(EncryptUtils.ENCRYPTED_SUFFIX, "");
        }
    	
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();  
        encryptor.setPassword(KEY);  
        return encryptor.decrypt(ciphertext);  
    }  
}  