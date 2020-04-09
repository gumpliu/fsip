package com.yss.fsip.constants;

import com.yss.fsip.exception.ErrorCode;

/**
 * @Description: FSIP 异常定义
 * @Author gumpLiu
 * @Date 2020-02-18
 * @Version V1.0
 **/
public enum FSIPErrorCode implements ErrorCode {

    /**
     * 参数异常
     */
    SUCCUESS("200", "操作成功！"),


    FAIL_ERR("FSIP-999999P","系统异常，请联系管理人员！"),

    /**
     * 参数异常
     */
    PARAM_ERR("FSIP-000001", "参数异常：{0}"),

    INVALID_UNIQUE_CONFIG("FSIP-000096", "无效的唯一性校验规则，请在【{0}】下的getUniqueProperty方法中正确配置唯一性校验规则"),

    SAVE_GROUP_UNIQUE_ERR("FSIP-000097", "【{0}】为【{1}】下已存在【{2}】为【{3}】的数据，唯一性校验失败"),

    SAVE_UNIQUE_ERR("FSIP-000098", "已存在【{0}】为【{1}】的数据，唯一性校验失败"),

    /**
     * 其他异常
     */
    UNKNOWN_ERR("FSIP-000099", "其他异常：{0}");

    protected String errorCode;
    protected String errorDesc;

    FSIPErrorCode(String errorCode, String errorDesc){
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorDesc() {
        return errorDesc;
    }
}
