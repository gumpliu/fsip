package com.yss.fsip.annotations;

import java.lang.annotation.*;

/**
 * 日志注解
 * 
 * @author Yangxg
 * @version 1.0, 2012-10-17
 * @since 1.0, 2012-10-17
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordLog {

	/**
	 * 操作编码
	 * 
	 * 只有在方法级别声明的@RecordLog注解，operationCode属性值才为有效。
	 * 
	 * @return
	 */
	String operationCode() default "";
	
	/**
	 * 日志内容
	 * 
	 * 日志描述内容信息
	 * 
	 * @return
	 */
	String content() default "";

}
