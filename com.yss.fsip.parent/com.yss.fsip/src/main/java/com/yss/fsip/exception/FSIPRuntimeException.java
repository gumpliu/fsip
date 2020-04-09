package com.yss.fsip.exception;

import java.text.MessageFormat;

/**
 * 基础异常类
 * 
 * @author LSP
 */
public class FSIPRuntimeException extends RuntimeException {

	private String code;
	
	private String msg;
	
	public FSIPRuntimeException() {
		super();
	}
	
	public FSIPRuntimeException(Throwable cause) {
		super(cause);
	}

	public FSIPRuntimeException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public FSIPRuntimeException(String code, String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}
	
	
	public FSIPRuntimeException(String code, String msg, String position) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}
		
	public FSIPRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
		this.msg = msg;
	}

	public FSIPRuntimeException(String code, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
		this.msg = msg;
	}

	public FSIPRuntimeException(String code, String msg, String... params) {
		super(msg);
		this.code = code;
		this.msg = MessageFormat.format(msg, params);
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
	
}
