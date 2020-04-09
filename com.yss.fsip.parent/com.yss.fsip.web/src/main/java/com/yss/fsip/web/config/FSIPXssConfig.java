package com.yss.fsip.web.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yss.fsip.common.util.PropertiesFormat;
import com.yss.fsip.common.util.StringUtil;
import com.yss.fsip.exception.FSIPRuntimeException;
import com.yss.fsip.web.constants.FSIPWebErrorCode;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * XSS检查规则配置信息
 *
 * @Author: jingminy
 * @Date: 2020/3/25 15:23
 */
public class FSIPXssConfig {
    private static Logger logger = LoggerFactory.getLogger(FSIPXssConfig.class);

    private boolean securityScanEnabled;

    // 实际值是[`~!@#$%^*()+=|{}':;',"\[\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，\\、？]
    private static String SPECIAL_CHAR_REGEX = "[`~!@#$%^*()+=|{}':;',\"\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，\\\\、？]";

    private static String XSS_KEY = "XSSKEY";
    private static String XSS_PATH_KEY = "XSSPATH";

    private static String PREFIX_RULE = "rule";
    private static String PREFIX_URL = "url";

    // ------------ 前置xss规则	开始 ------------
    // 在前置xss关键字检查规则，默认关闭，若开启则在自定义xss关键字过滤前执行
    // 暂未使用
    private Map<Object,String> prefixMap;
    private static Pattern prefixKeyPattern;

    // 不需要走xss路径检查的路径匹配规则
    private static Pattern[] prefixExcludePathPatterns;
    // ------------ 前置xss规则	结束 ------------

    // ------------ 自定义xss过滤规则	开始 ------------
    // 自定义xss过滤路径map
    private Map<String, String[]> customUrlMap = new HashMap<String, String[]>();

    // 自定义xss关键字Map
    private Map<String, Map<Object, String>> customKeyMap = new HashMap<String, Map<Object, String>>();

    // 自定义xss关键字匹配规则Map
    private Map<String, Pattern> customKeyPatternMap = new HashMap<String, Pattern>();

    // ------------ 自定义xss过滤规则	结束 ------------

    public FSIPXssConfig(FSIPXssProperties xssProperties) {
        String securityScanEnabledStr = xssProperties.getSecurityScanEnabled();
        securityScanEnabled = "true".equals(securityScanEnabledStr)? true: false;
        if(securityScanEnabled) {
            logger.info("开始加载xss配置信息");
            // 加载前置检查规则
            loadXssPrefixConfig(xssProperties);
            // 加载自定义检查规则
            loadXssCustomConfig(xssProperties);
        }
    }

    /**
     * 自定义Xss规则过滤
     *
     * @param requestUri  请求uri地址
     * @param value       待过滤数据
     * @param servletFlag request/response标识;true为request;false为response;
     * @Author: jingminy
     * @Date: 2020/3/25 16:24
     */
    public String filter(String requestUri, String value, boolean servletFlag) {
        if(customUrlMap.isEmpty() || customKeyPatternMap.isEmpty()) {
            return value;
        }
        if (StringUtil.isEmpty(value)) {
            return value;
        }

        //自定义路径为空直接返回参数
        if (null == customUrlMap && customUrlMap.size() == 0) {
            return value;
        }
        String xssEncode = value;
        //TODO 待定，是否需要？
//        xssEncode = StringEscapeUtils.escapeHtml4(xssEncode);
        Set<Map.Entry<String, String[]>> entrySet = customUrlMap.entrySet();
        Iterator<Map.Entry<String, String[]>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String[]> next = iterator.next();
            Pattern p = Pattern.compile("^" + next.getKey());
            Matcher m = p.matcher(requestUri);
            //根据配置路径进行过滤
            if (m.find()) {
                String[] urls = next.getValue();
                // 判断路径在request/response下进行过滤
                if (servletFlag) {
                    if (urls[0] != null && urls[0].equals("response")) break;
                } else {
                    if (urls[0] != null && urls[0].equals("request")) break;
                }
                Pattern pattern = customKeyPatternMap.get(next.getKey());
                Map<Object, String> map = customKeyMap.get(next.getKey());
                //开始过滤
                xssEncode = xssEncode(xssEncode, pattern, map);
                break;
            }
        }
        logger.info("请求地址："+requestUri+",xss过滤前："+value+"，xss过滤后："+xssEncode);
        return xssEncode;
    }

    /**
     * 加载Xss前置配置信息
     */
    private void loadXssPrefixConfig(FSIPXssProperties xssProperties) {
        prefixMap = new HashMap<Object,String>();
        // TODO 待整理
        // 未配置prefixKey，则excludePaths无效
        String prefixKey = xssProperties.getPrefixKey();
        if (StringUtil.isEmpty(prefixKey)) {
            return;
        }
        // <|&lt;@>|&gt;@&|&amp;@\r@\n@\f@eval\((.*?)\)@script(.*?):(.*?)\((.*?)\)
        String[] keys = prefixKey.split("@");
        if (keys.length == 0) {
            return;
        }
        String[] xssPaths = null;
        //xsspath为空或注释时的判断
        String prefixExcludePaths = xssProperties.getPrefixExcludePaths();
        if (prefixExcludePaths != null && prefixExcludePaths.trim().length() > 0) {
            xssPaths = prefixExcludePaths.split("\\|");
        }
        //初始化Xss字符
        Map<String, Object> xssData = initXssCharacter(keys, prefixMap, xssPaths);
        prefixKeyPattern = (Pattern) xssData.get(FSIPXssConfig.XSS_KEY);
        prefixExcludePathPatterns = (Pattern[]) xssData.get(FSIPXssConfig.XSS_PATH_KEY);
    }

    /**
     * 加载Xss配置信息
     *
     * @Author: jingminy
     * @Date: 2020/3/27 14:24
     */
    private void loadXssCustomConfig(FSIPXssProperties xssProperties) {
        // 判断是否有xss规则文件，没配置就不走xss过滤
        String xssConfigPath = xssProperties.getConfig();
        if (StringUtil.isEmpty(xssConfigPath)) {
            return;
        }

        // 方法2：获取文件
        File file = null;
        try {
            file = ResourceUtils.getFile(xssConfigPath);
        } catch (FileNotFoundException e) {
            throw new FSIPRuntimeException(FSIPWebErrorCode.XSS_CUSTOM_CONFIG_FILE_NOT_FOUND.getErrorCode(), xssConfigPath);
        }
        // xss过滤规则参数映射
        Map<String, String> customRuleMap = new HashMap<String, String>();

        try {
            // 读取xss_custom.ini文件内容
            PropertiesFormat pf = new PropertiesFormat();
            pf.load(file, "UTF-8");
            Iterator<String> keys = pf.propertyNames().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String val = pf.getProperty(key);
                // key为rule.viewSource=view-source|xxx
                //或者url.*/sofa-sso/*@response=alert,toString
                String[] str = key.split("\\.");
                String prefix = str[0];
                String keyName = str[1];
                // 组装规则对应配置，rule.script=script|xxx对应的配置
                if (prefix.equals(PREFIX_RULE)) {
                    customRuleMap.put(keyName, val);
                }
                //组装路径对应的类型与规则，url.*/dictionary/*=XXX对应的配置
                else if (prefix.equals(PREFIX_URL)) {
                    // url.*/sofa-portal/*@request,*/sofa-basalinfo/*@request,*/sofa-sso/*@response=alert,toString
                    String[] urls = keyName.split(",");
                    for (String u : urls) {
                        String[] oneUrl = u.split("@");
                        String url = oneUrl[0];

                        String params[] = new String[2];
                        if (oneUrl.length > 1) {   //配置了@request或者@response，未配置则都请求响应都需要xss检查
                            params[0] = oneUrl[1];
                        } else {
                            params[0] = "";
                        }
                        params[1] = val;
                        //map的key为url值为String数组params[0]为request/response,params[1]为规则名称(可多个)
                        customUrlMap.put(url, params);
                    }
                }
            }

            // 根据xss_custom.ini内容生成对应的正则匹配Pattern
            Set<Map.Entry<String, String[]>> urlSet = customUrlMap.entrySet();
            Iterator<Map.Entry<String, String[]>> itUrl = urlSet.iterator();
            Map<Object, String> map = null;
            Pattern pattern = null;
            while (itUrl.hasNext()) {
                Map.Entry<String, String[]> next = itUrl.next();
                String key = next.getKey();
                String[] value = next.getValue();

                // 规则信息
                String[] rules = value[1].split(",");
                String detail[] = new String[rules.length];
                for (int i = 0; i < rules.length; i++) {
                    detail[i] = customRuleMap.get(rules[i]);
                }
                map = new LinkedHashMap<Object, String>();
                Map<String, Object> xssData = this.initCustomCharacter(detail, map);
                customKeyMap.put(key, map);
                customKeyPatternMap.put(key, (Pattern) xssData.get(FSIPXssConfig.XSS_KEY));
            }
        } catch (Exception e) {
            logger.error("load file[" + xssConfigPath + "] error.", e);
            throw new FSIPRuntimeException(FSIPWebErrorCode.XSS_CUSTOM_CONFIG_FILE_READ_ERROR.getErrorCode(), xssConfigPath);
        }
    }

    /**
     * 初始化Xss字符
     *
     * @param character  xss过滤关键字
     * @param excludeXssPaths
     * @return void
     */
    private Map<String, Object> initXssCharacter(String[] character, Map<Object,String> map, String[] excludeXssPaths) {

        // 匹配规则中的特殊字符
        Pattern p = Pattern.compile(SPECIAL_CHAR_REGEX, Pattern.CASE_INSENSITIVE);
        StringBuffer xsskeys = new StringBuffer();
        for (int i = 0; i < character.length; i++) {
            // characters为<|&lt;@>|&gt;@&|&amp;@\r@\n@\f@eval\((.*?)\)@script(.*?):(.*?)\((.*?)\) 用@分隔后的数据
            String[] temp = character[i].split("\\|");
            String value = "";
            if (temp.length > 1) {
                value = temp[1];
            }
            String origin = temp[0];
            if (origin.length() > 1) {
                Pattern scriptPattern = Pattern.compile(origin, Pattern.CASE_INSENSITIVE);
                map.put(scriptPattern, value);
                //替换特殊字符，替换为""(即直接过滤掉特殊字符)
                xsskeys.append(p.matcher(origin).replaceAll("")).append("|");
            } else {
                map.put(origin, value);
                xsskeys.append(origin).append("|");
            }
        }
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        if (xsskeys.length() > 1) {
            // 截掉最后拼接的|
            xsskeys.setLength(xsskeys.length() - 1);
            // 根据xsskeys的规则构建匹配正则的Pattern
            Pattern keyScripts = Pattern.compile(xsskeys.toString(), Pattern.CASE_INSENSITIVE);
            hashMap.put(XSS_KEY, keyScripts);
        }

        // 根据excludeXssPaths配置构建不走XSS过滤规则的路径的正则Pattern，只有前置xss检查有此配置
        if (null != excludeXssPaths && excludeXssPaths.length != 0) {
            Pattern[] xpps = new Pattern[excludeXssPaths.length];
            for (int i = 0; i < excludeXssPaths.length; i++) {
                xpps[i] = Pattern.compile("^" + excludeXssPaths[i]);
            }
            hashMap.put(XSS_PATH_KEY, xpps);
        }
        return hashMap;
    }

    /**
     * 初始化自定义Xss字符
     *
     * @param character
     * @return void
     */
    private Map<String, Object> initCustomCharacter(String[] character, Map<Object,String> map) {
        return this.initXssCharacter(character, map, null);
    }

    /**
     * 转义或替换HTML字符避免XSS
     *
     * @Author: jingminy
     * @Date: 2020/3/25 16:13
     */
    private String xssEncode(String source, Pattern keyScript, Map<Object, String> map) {

        if (source == null || map == null) {
            return source;
        }

        //source = WebUtil.dealParameterValue(source);

        //先去掉前置检查,执行正常的正则匹配,后期可能要优化
		/*Matcher m = keyScriptPattern.matcher(source);

		if(!m.find()){
			return source;
		}*/

        Iterator<Map.Entry<Object, String>> it = map.entrySet().iterator();
        String html = source.trim();
        while (it.hasNext()) {
            Map.Entry<Object, String> entry = it.next();
            Object key = entry.getKey();
            String value = entry.getValue();
            if (key instanceof String) {
                html = html.replaceAll((String) key, value);
            } else {
                Pattern scriptPattern = (Pattern) key;
                html = scriptPattern.matcher(html).replaceAll(value);
            }
        }

        return html;
    }
//    /**
//     * 处理url中的参数值，如果parameters.ini配置文件中的security-scan-switch配置为true，那么进行xss脚本编制过滤
//     * 如果未配置或者配置为false那么对参数值不进行处理
//     * add by zhengmd
//     *
//     * @DATE 2018-03-30
//     * @param val
//     */
//    public String dealParameterValue(String val) {
//
//        if (securityScanEnabled) {
//
//            if (val.contains("window") && val.contains("location")) {
//                val = "xxx";
//            } else if (val.contains("toString")) {
//                val = "xxx";
//            } else if (val.contains("alert")) {
//                val = "xxx";
//            } else if (val.contains("\\x61\\x6c\\x65\\x72\\x74")) {//alert
//                val = "xxx";
//            } else if (val.contains("&#97;&#108;&#101;&#114;&#116;")) {
//                val = "xxx";
//            } else if (val.contains("\\u61\\u6c\\u65\\u72\\u74")) {
//                val = "xxx";
//            } else if (val.contains("script")) {
//                val = val.replaceAll("script", "xxx");
//            }
//        }
//
//        return val;
//    }

    public Pattern[] getExcludeXssPathPatterns() {
        return prefixExcludePathPatterns;
    }

    public void setExcludeXssPathPatterns(Pattern[] excludeXssPathPatterns) {
        this.prefixExcludePathPatterns = excludeXssPathPatterns;
    }

}
