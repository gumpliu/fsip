package com.yss.fsip.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
* FSIPBeanUtil.java中属性拷贝时，若属性不一致，则使用此注解做关联
* 
* @Author: jingminy
* @Date: 2020/3/9 14:55
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FSIPBeanPropertyTarget {
    String target();
}
