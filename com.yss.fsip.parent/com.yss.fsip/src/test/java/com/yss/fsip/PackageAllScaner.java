package com.yss.fsip;

import com.yss.fsip.util.BeanUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PackageAllScaner {
    @Bean
    public BeanUtil beanUtil(){
        return new BeanUtil();
    }
} 