package com.yss.fsip.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * fsip xss跨站攻击过滤规则配置
 *
 * @Author: jingminy
 * @Date: 2020/3/25 9:55
 */
@ConfigurationProperties("fsip.web.xss")
public class FSIPXssProperties {

    // 安全扫描开关，默认为关闭状态
    private String securityScanEnabled;

    // xss过滤转义字符及关键字
    // 规则："@" 为间隔项，"|" 间隔字符和对应替换值,因报表1.0的关系暂时不能对&进行转义
    private String prefixKey;

    // 不需要xss过滤的路径, 多路径以"|" 间隔，未配置prefixKey，则此项无效
    private String prefixExcludePaths;

    // xss校验规则.ini文件，如xss_custom.ini，未配置则不校验
    private String config;

    public String getSecurityScanEnabled() {
        return securityScanEnabled;
    }

    public void setSecurityScanEnabled(String securityScanEnabled) {
        this.securityScanEnabled = securityScanEnabled;
    }

    public String getPrefixKey() {
        return prefixKey;
    }

    public void setPrefixKey(String prefixKey) {
        this.prefixKey = prefixKey;
    }

    public String getPrefixExcludePaths() {
        return prefixExcludePaths;
    }

    public void setPrefixExcludePaths(String prefixExcludePaths) {
        this.prefixExcludePaths = prefixExcludePaths;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
