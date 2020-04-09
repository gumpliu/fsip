package com.yss.fsip.util;

import com.yss.fsip.exception.ErrorCode;

import java.text.MessageFormat;

/**
 * @Description: 获取断言提示信息
 * @Author gumpLiu
 * @Date 2020-01-16
 * @Version V1.0
 **/
public class AssertMsgUtil {
    public static final String ASSERT_SEPARATOR = "~";

    /**
     * 组装提示信息
     * @param errorCode
     * @return
     */
    public static String getMsg(ErrorCode errorCode){
        return errorCode.getErrorCode()
                + ASSERT_SEPARATOR
                + errorCode.getErrorDesc();
    }

    /**
     * 组装提示信息
     * @param errorCode
     * @param parsms 占位符参数
     * @return
     */
    public static String getMsg(ErrorCode errorCode, String ... parsms){
        return errorCode.getErrorCode()
                + ASSERT_SEPARATOR
                + MessageFormat.format(errorCode.getErrorDesc(), parsms);
    }


}
