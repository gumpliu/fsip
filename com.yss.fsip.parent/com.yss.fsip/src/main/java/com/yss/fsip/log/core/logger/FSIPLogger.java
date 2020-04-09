package com.yss.fsip.log.core.logger;

public abstract interface FSIPLogger {

    /**
     * 日志类型：系统日志
     */
    public final static int LOGTYPE_SYS = 0;

    /**
     * 日志类型：业务日志
     */
    public final static int LOGTYPE_BIZ = 1;

    /**
     * 调用方式 通过注解
     */
    public static final int LOG_INVOKED_TYPE_ANNOTATION = 1;

    /**
     * 调用方式 通过api
     */
    public static final int LOG_INVOKED_TYPE_API = 0;

    public static final int MAX_EXCEPTIONINFO_LENGTH = 4000;





}
