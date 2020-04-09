package com.yss.fsip.web.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class FSIPWebConfigurer implements WebMvcConfigurer {

    private FSIPLogProperties fsipLogProperties;

    public FSIPWebConfigurer(FSIPLogProperties fsipLogProperties){
        this.fsipLogProperties = fsipLogProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        LoggerInterceptor loggerInterceptor = new LoggerInterceptor();
        loggerInterceptor.setFsipLogProperties(fsipLogProperties);

        registry.addInterceptor(loggerInterceptor).addPathPatterns("/**");
    }

}