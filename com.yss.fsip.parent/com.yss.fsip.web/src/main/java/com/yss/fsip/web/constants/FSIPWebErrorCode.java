package com.yss.fsip.web.constants;

import com.yss.fsip.exception.ErrorCode;

/**
 * FSIP web异常编码
 *
 * @author jingminy
 * @date 2019/12/19 11:33
 */
public enum FSIPWebErrorCode implements ErrorCode {

    // --------------- XSS自定义过滤规则配置错误 --------------- //
    XSS_CUSTOM_CONFIG_FILE_NOT_FOUND("FSIP-001000C", "自定义XSS过滤规则配置文件【{0}】没有找到"),

    // --------------- 读取XSS自定义过滤规则配置文件异常 --------------- //
    XSS_CUSTOM_CONFIG_FILE_READ_ERROR("FSIP-001001C", "读取XSS自定义过滤规则配置文件[{0}]出现异常");

    protected String errorCode;
    protected String errorDesc;

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorDesc() {
        return errorDesc;
    }

    FSIPWebErrorCode(String errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }
}
