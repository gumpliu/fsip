package com.yss.fsip.generic;

import com.yss.fsip.constants.FSIPErrorCode;

/**
 * resultVO 工具类
 * 
 * @author LSP
 *
 */
public class ResultFactory {
	
	public static Result success(Object object) {
        return createResult(FSIPErrorCode.SUCCUESS.getErrorCode(), FSIPErrorCode.SUCCUESS.getErrorDesc(), object);
    }

    public static Result success() {
        return createResult(FSIPErrorCode.SUCCUESS.getErrorCode(), FSIPErrorCode.SUCCUESS.getErrorDesc());
    }
    
    public static Result error(String code,String msg) {
        return createResult(code, msg);
    }

    private static Result createResult(String code, String msg) {
        return createResult(code, msg, null);
    }

    private static Result createResult(String code, String msg, Object object) {
    	return new Result(code, msg, object);
    }
    
}
