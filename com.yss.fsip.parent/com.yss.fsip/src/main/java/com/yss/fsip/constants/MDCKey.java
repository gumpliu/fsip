/**
 * 
 */
package com.yss.fsip.constants;

/**
 * @author Yangxg
 * @version 1.0, 2012-11-1
 * @since 1.0, 2012-11-1
 */
public final class MDCKey {

	public static final String SYMBOLIC_NAME = "symbolicName";
	public static final String VERSION = "version";
	public static final String FUNCTION_CODE = "functionCode";
	public static final String OPERATION_CODE = "operationCode";

	public static final String USER_ID = "userId";

	public static final String BIZ_ID = "bizId";

	public static final String CLASS_NAME = "className";
	public static final String METHOD_NAME = "methodName";

	public static final String LOG_TYPE = "logType";
	public static final String LOG_INVOKED_TYPE = "logInvokedType";
	public static final String CONTENT = "content"; // 只有注解日志才有此属性，且在MDC中

	public static final String SERVER_IP = "serverIp";
	public static final String CLIENT_IP = "clientIp";

	public static final String HAS_BIZDATA = "hasBizData";
	public static final String ORI_BIZDATAID = "oriBizDataId";

	public static final String BIZDATA = "bizData.data";
	public static final String BIZDATA_ID = "bizData.id";
	public static final String BIZDATA_CLASSNAME = "bizData.className";
	public static final String BIZDATA_KEYFIELDS = "bizData.keyFields";
	public static final String BIZDATA_KEYVALUES = "bizData.keyValues";
	public static final String USER_KEY = "user.key";
	// 日志轨迹ID
	public static final String TRACK_ID = "trackId";
	public static final int MAX_EXCEPTIONINFO_LENGTH = 4000;

}
