package com.yss.fsip.context;

import java.util.Map.Entry;
import java.util.Set;

/**
 * FSIP上下文API
 * 
 * @author LSP
 *
 */
public class FSIPContext {
	/**
	 * 用户ID
	 */
	private String userId;

	/** 用户名 */
	private String userName = "";
	/**
	 * 登录名称
	 */
	private String loginName = null;
	
	/**
	 * 日志轨迹ID
	 */
	private String loggerTrackId = null;
	
	private String userCode;

	private String functionCode = "N";


	public String getAttribute(String key) {
		return ContextSupport.getParameter(key);
	}

	public void removeAttribute(String key) {
		ContextSupport.removeParameter(key);
	}


	public String getUserId() {
		return this.userId;
	}


	public void setAttribute(String key, String value) {

		ContextSupport.setParameter(key, value);
	}

	public Set<Entry<String, String>> getAttributes() {

		return ContextSupport.getParameters().entrySet();
	}

	public String getFunctionCode() {

		return functionCode;
	}

	public void setFunctionCode(String functionCode) {

		this.functionCode = functionCode;
	}


	public void setUserName(String userName) {

		this.userName = userName;
	}

	public String getUserName() {

		return this.userName;
	}

	public String getLoginName() {

		if (loginName == null) loginName = getAttribute("FLOGIN_CODE");

		return loginName;
	}

	public void setLoginName(String loginName) {

		this.loginName = loginName;
	}

	public void setUserId(String userId) {

		this.userId = userId;

	}

	public String getUserCode() {

		return this.userCode;
	}
	
	/**
	 * @param userCode the userCode to set
	 */
	public void setUserCode(String userCode) {
	
		this.userCode = userCode;
	}

	
	public String getLoggerTrackId() {
	
		return loggerTrackId;
	}
	
	public void setLoggerTrackId(String loggerTrackId) {
	
		this.loggerTrackId = loggerTrackId;
	}


    @Override
    public String toString() {
        return "SOFAContextImpl [userId=" + userId + ", userName=" + userName
				+ ", loginName=" + loginName + ", loggerTrackId=" + loggerTrackId
				+ ", userCode=" + userCode + ", functionCode=" + functionCode;
    }
}
