package com.yss.fsip.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 唯一性校验注解
 *
 * @author jingminy
 * @date 2020/1/21 15:36
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueValidate {

    /**
     * 分组code：只在同一分组下不允许有重复数据，不同分组下可以重复数据，未配置则不按分组校验
     *
     * @return
     */
    String group() default "";

    /**
     * 分组名称（目前后端写死，后期前端国际化则返回分组的类全路径名）,未配置则取group的值
     *
     * @return
     */
    String groupName() default "";

    /**
     * 分组名称（目前后端写死，后期前端国际化则返回分组的类全路径名）,未配置则取group的值
     *
     * @return
     */
    String groupNameCode() default "";

    /**
     * 唯一性校验不通过时，显示此name的值，未配置时取方法的返回值
     *
     * @return
     */
    String name() default "";

}
