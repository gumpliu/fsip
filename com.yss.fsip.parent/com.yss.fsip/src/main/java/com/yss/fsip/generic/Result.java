package com.yss.fsip.generic;

import com.yss.fsip.constants.FSIPErrorCode;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * web返回信息
 * 
 * @author LSP
 *
 */
public class Result implements Serializable {
	
    private static final long serialVersionUID = 4564124491192825748L;

	private boolean success;

	private String code;

	private String msg;

	private Object data;
	
	public Result() {}
	
	public Result(String code, String message) {
		this(code, message, null);
	}

	public Result(String code, String message, Object data) {
		this.data = data;
		this.code = code;
		this.msg = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public boolean isSuccess() {
		if(StringUtils.isEmpty(code)) {
			return false;
		}
		
		return code.equals(FSIPErrorCode.SUCCUESS.getErrorCode());
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
