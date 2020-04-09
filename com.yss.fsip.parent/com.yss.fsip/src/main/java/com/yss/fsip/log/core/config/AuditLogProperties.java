package com.yss.fsip.log.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fsip.web.auditlog")
public class AuditLogProperties {

    private Boolean request=true;

    private int connectTimeOut=10 * 60 * 1000;

    private int socektTimeOut=10 * 60 * 1000;

    private String applicationName="default";

    public Boolean getRequest() {
        return request;
    }

    public void setRequest(Boolean request) {
        this.request = request;
    }
    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public int getSocektTimeOut() {
        return socektTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public void setSocektTimeOut(int socektTimeOut) {
        this.socektTimeOut = socektTimeOut;
    }


    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
