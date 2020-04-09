package com.yss.fsip.log.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yss.fsip.annotations.Function;
import com.yss.fsip.annotations.RecordLog;
import com.yss.fsip.common.util.DateUtil;
import com.yss.fsip.common.util.NetUtils;
import com.yss.fsip.common.util.StringUtil;
import com.yss.fsip.constants.MDCKey;
import com.yss.fsip.context.FSIPContext;
import com.yss.fsip.context.FSIPContextFactory;
import com.yss.fsip.generic.entity.BaseEntity;
import com.yss.fsip.log.core.logger.FSIPLogger;
import com.yss.fsip.util.IDGeneratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class DBLogger implements FSIPLogger {

    private Logger logger = LoggerFactory.getLogger(DBLogger.class);

    ObjectMapper objectMapper = new ObjectMapper();
    public void success(Class<?> targetClass, Method targetMethod, Object param,Function function,String appName) {

        Map<String, String> contextMap = getBizLogMDCContextMap(targetClass, targetMethod);

        String msg = contextMap.get(MDCKey.CONTENT);
        contextMap.remove(MDCKey.CONTENT);
        contextMap.put(MDCKey.LOG_INVOKED_TYPE, FSIPLogger.LOG_INVOKED_TYPE_ANNOTATION + "");
        List<Map<String, String>> bizDatas = null;
        if(function!=null){
            contextMap.put(MDCKey.FUNCTION_CODE,function.code());
        }
        if(StringUtil.isNotEmpty(appName)){
            contextMap.put(MDCKey.SYMBOLIC_NAME,appName);
        }
        bizDatas = parseInputParameters(targetMethod,targetClass, param);
        if (null == bizDatas || bizDatas.size() == 0) {
            logger.warn("获取业务数据失败，方法{}#{}中声明的日志注解未获取到业务参数。", targetClass.toString(),
                    targetMethod.toString());
        }
        if (null != bizDatas && bizDatas.size() > 0) {
            contextMap.put(MDCKey.HAS_BIZDATA, "true");
            for (Map<String, String> bizData : bizDatas) {
                contextMap.putAll(bizData);
                MDC.setContextMap(contextMap);
                getLogger(targetClass.getName()).info(msg);
            }
        } else {
            contextMap.put(MDCKey.HAS_BIZDATA, "false");
            MDC.setContextMap(contextMap);
            getLogger(targetClass.getName()).info(msg);
        }
        clearMDC();
    }


    private Map<String, String> getBizLogMDCContextMap(Class<?> targetClass, Method targetMethod) {

        Map<String, String> map = new HashMap<String, String>();
        RecordLog classAnnotation = targetClass.getAnnotation(RecordLog.class);
        RecordLog methodAnnotation = targetMethod.getAnnotation(RecordLog.class);

        map.put(MDCKey.USER_ID, FSIPContextFactory.getContext().getUserId());
        map.put(MDCKey.TRACK_ID, getTrackId());
        map.put(MDCKey.CLASS_NAME, targetClass.getName());
        map.put(MDCKey.METHOD_NAME, targetMethod.getName());
        map.put(MDCKey.LOG_TYPE, FSIPLogger.LOGTYPE_BIZ + "");
        map.put(MDCKey.CLIENT_IP, null);
        //现场要求把server_ip放入数据库中
        map.put(MDCKey.SERVER_IP, NetUtils.getLocalHost());
        if(methodAnnotation!=null){
            if(StringUtil.isNotEmpty(methodAnnotation.operationCode())){
                map.put(MDCKey.OPERATION_CODE,methodAnnotation.operationCode());
            }
            if(StringUtil.isNotEmpty(methodAnnotation.content())){
                map.put(MDCKey.OPERATION_CODE,methodAnnotation.content());
            }
        }
        return map;
    }

    static Object mutex = new Object();

    public String getTrackId() {
        FSIPContext ctx = FSIPContextFactory.getContext();
        String trackId = FSIPContextFactory.getContext().getLoggerTrackId();
        if (StringUtil.isEmpty(trackId)) {
            synchronized (mutex) {
                if ((trackId = ctx.getLoggerTrackId()) == null) {
                    trackId = IDGeneratorFactory.getIDGenerator().nextId();
                    ctx.setLoggerTrackId(trackId);
                }
            }
        }
        return trackId;
    }


    /**
     *
     * @param method
     * @param param
     * @return
     */
    private List<Map<String, String>> parseInputParameters(Method method,Class<?> targetClass,Object param) {

        return getBizDatas(param,targetClass);
    }

    /**
     *
     * @param param
     * @return
     */
    private List<Map<String, String>> getBizDatas(Object param,Class<?> targetClass) {

        if (null == param) {
            return null;
        }

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        if (param.getClass().isArray()) {
            int arrayLength = Array.getLength(param);
            for (int i = 0; i < arrayLength; i++) {
                Map<String, String> map = getBizDataMap(Array.get(param, i),targetClass);
                if (null != map) {
                    list.add(map);
                }
            }
        }/*
         * else if(bizDatas instanceof List){
         * for(Object bizData : (List)bizDatas){
         * Map<String, String> map = getBizDataMap(bizData, keyFields);
         * if(null != map){
         * list.add(map);
         * }
         * }
         * }
         */else if (param instanceof Collection) {
            Iterator<?> it = ((Collection<?>) param).iterator();
            while (it.hasNext()) {
                Object bizData = it.next();
                Map<String, String> map = getBizDataMap(bizData,targetClass);
                if (null != map) {
                    list.add(map);
                }
            }
        } else {
            Map<String, String> map = getBizDataMap(param,targetClass);
            if (null != map) {
                list.add(map);
            }
        }

        return list;
    }


    private Map<String, String> getBizDataMap(Object param,Class<?> targetClass) {

        if (null == param) {
            return null;
        }

        Map<String, String> map = new HashMap<String, String>();
        try {
            if (param instanceof BaseEntity) {
                try {
                    map.put(MDCKey.BIZDATA, objectMapper.writeValueAsString(param));
                } catch (Exception e) {
                    logger.error("转换业务数据json失败",e);
                }
                String dataId = getIdFromObject(param);
                dataId = (dataId == null || dataId.trim().equals("")) ? "0" : dataId.trim();
                if (!StringUtil.isNumber(dataId)) {
                    dataId = "0";
                    logger.error("值对象[" + param.getClass() + "]的id值[" + dataId + "]为非数字类型，不能保存到业务日志中。");
                }
                map.put(MDCKey.BIZDATA_ID, dataId);
                map.put(MDCKey.BIZDATA_CLASSNAME, param.getClass().getName());
            } else if (param instanceof String) {
                if(StringUtil.isEmpty((String) param)){
                    //当dataId的字符串为""时,db2环境报错
                    map.put(MDCKey.BIZDATA_ID, null);
                }else if (isNumeric((String) param)) {
                    map.put(MDCKey.BIZDATA_ID, (String) param);
                } else {
                    String jsonString = "{\"string\":" + "\"" + param + "\"}";
                    map.put(MDCKey.BIZDATA, jsonString);
                }
            } else if (isBaseDataType(param.getClass())) {
                StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
                StackTraceElement ele = null;
                String[] str = new String[2];
                for (int i = 3; i < stacks.length; i++) {
                    ele = stacks[i];
                    String tempString = ele.getClassName();
                    String className = ele.getClassName();
                    int index = tempString.indexOf('$');
                    if(index >=0){
                        className = tempString.substring(0, index);
                    }
                    if (targetClass.getName().equals(className)) {
                        str[0] = className;
                        str[1] = ele.getMethodName();

                        break;
                    }
                }
                String jsonPro = "";
                if(str[1] != null){
                    Method[] methods = targetClass.getMethods();
                    for (Method method : methods) {

                        String methodName = method.getName();
                        if(str[1].equals(methodName)){
                            LocalVariableTableParameterNameDiscoverer u =
                                    new LocalVariableTableParameterNameDiscoverer();
                            String[] params = u.getParameterNames(method);
                            if(params.length > 0){
                                jsonPro = params[0];
                            }
                        }
                    }
                }
                String jsonString = "{\"" + jsonPro + "\":" + "\"" + param + "\"}";
                map.put(MDCKey.BIZDATA, jsonString);
            } else {
                // 如果既没有实现ValueObject接口，也不是String类型，也不是基础类型，那么将数据转换成
                map.put(MDCKey.BIZDATA, objectMapper.writeValueAsString(param));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 如果没有指定KeyFields，则默认为第一个不是id的属性
        String[] keyFields = processKeyFields(param.getClass());

        map.put(MDCKey.BIZDATA_KEYFIELDS, convertArray2String(keyFields, ","));

        // 获取keyValues
        if (null != keyFields && keyFields.length > 0) {
            map.put(MDCKey.BIZDATA_KEYVALUES, getKeyValue(param, keyFields));
        }

        return map;
    }

    /**
     * 判断一个类是否为基本数据类型。
     * @param clazz 要判断的类。
     * @return true 表示为基本数据类型。
     */
    private static boolean isBaseDataType(Class clazz)  throws Exception
    {
        return
                (
                        clazz.equals(Integer.class)||
                                clazz.equals(Byte.class) ||
                                clazz.equals(Long.class) ||
                                clazz.equals(Double.class) ||
                                clazz.equals(Float.class) ||
                                clazz.equals(Character.class) ||
                                clazz.equals(Short.class) ||
                                clazz.equals(BigDecimal.class) ||
                                clazz.equals(BigInteger.class) ||
                                clazz.equals(Boolean.class) ||
                                clazz.isPrimitive()
                );
    }

    private boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0;) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private Logger getLogger(String targetClassName) {

        Logger log =  log = LoggerFactory.getLogger("bizLogger."+targetClassName);
        return log;
    }

    private String[] processKeyFields(Class clazz) {



        if (clazz.isPrimitive() || clazz.getName().equals("java.lang.String")) {
            return null;
        }

        String defaultField = null;
        try {
            Method method = clazz.getMethod("getName", null);
            defaultField = "name";
        } catch (NoSuchMethodException e) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (!f.getName().equals("id")) {
                    defaultField = f.getName();
                    break;
                }
            }
        }

        if (null != defaultField) {
            return new String[] { defaultField };
        } else {
            return new String[0];
        }
    }

    private String getKeyValue(Object bizData, String[] keyFields) {

        StringBuilder values = new StringBuilder();

        if (null == keyFields || bizData instanceof String) {
            return values.toString();
        }

        Class clazz = bizData.getClass();
        Field field = null;
        for (String keyField : keyFields) {
            try {
                Object val = null;
                // try{
                // field = clazz.getDeclaredField(keyField);
                // field.setAccessible(true);
                // val = field.get(bizData);
                // }catch(AccessControlException ae){
                // warn(SOFALogger.LOGTYPE_SYS, "获取业务数据[" + clazz.getName() + "]中关键属性[" + keyField +
                // "]出错，调用getter方法重新获取属性值。");

                if (keyField.equalsIgnoreCase("serialVersionUID")) {
                    continue;
                }
                String f = keyField.substring(0, 1).toUpperCase() + keyField.substring(1);
                Method method = null;
                try {
                    method = clazz.getMethod("get" + f, null);
                } catch (NoSuchMethodException e) {
                    try {
                        method = clazz.getMethod("is" + f, null);
                    } catch (Throwable ee) {

                    }
                }
                if (null != method) {
                    val = method.invoke(bizData, null);
                } else {
                    continue;
                }
                // }

                if (null != val) {
                    if (values.length() > 0) {
                        values.append(",");
                    }
                    values.append(formatValueStr(val));
                }
            } catch (Exception e) {
                logger.error("获取业务数据[" + clazz.getName() + "]中关键属性[" + keyField + "]失败", e);
            }
        }

        return values.toString();
    }

    private String formatValueStr(Object value) {

        if (null == value) {
            return "空";
        } else if (value instanceof Date) {
            return DateUtil.dateToString((Date) value, DateUtil.FORMAT_ONE);
        }
        return value.toString();
    }

    private String convertArray2String(Object[] array, String seperator) {

        if (null == array) {
            return "";
        }

        if (null == seperator) {
            seperator = "";
        }

        StringBuilder sb = new StringBuilder();
        for (Object obj : array) {
            if (sb.length() > 0) {
                sb.append(seperator);
            }
            sb.append(obj);
        }

        return sb.toString();
    }

    /**
     * 清空MDC
     */
    private void clearMDC() {

        MDC.clear();
    }

    public String getIdFromObject(Object param) throws IllegalAccessException {
        String value="";
        Field[] fs = param.getClass().getDeclaredFields();
        for(int i = 0 ; i < fs.length; i++){
            Field f = fs[i];
            f.setAccessible(true); //设置些属性是可以访问的
            if(f.getName().equals("id")){
                value= (String) f.get(param);
            }
        }
        return value;
    }


}
