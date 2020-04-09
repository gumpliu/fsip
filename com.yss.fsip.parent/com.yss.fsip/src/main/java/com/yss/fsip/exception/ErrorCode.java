package com.yss.fsip.exception;

/**
 * 统一异常信息
 */
public interface ErrorCode {

    /**
     * 异常编码
     * @return
     */
    String getErrorCode();

    /**
     * 异常详细内容
     * @return
     */
    String getErrorDesc();
}