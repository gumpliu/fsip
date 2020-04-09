package com.yss.fsip.annotations;

@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE,java.lang.annotation.ElementType.METHOD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Inherited
public @interface Function {

    /**
     * 功能编码
     * @return 功能编码，字符串.
     */
    String code();

}
