package com.yss.fsip;

import com.yss.fsip.config.EncryptPlaceholderConfig;
import com.yss.fsip.config.UserIDAuditorBean;
import com.yss.fsip.util.BeanUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FSIPAutoConfiguration {

    @Bean
    public EncryptPlaceholderConfig encryptPlaceholderConfig(){
        return new EncryptPlaceholderConfig();
    }

    @Bean
    public UserIDAuditorBean userIDAuditorBean(){
        return new UserIDAuditorBean();
    }

    @Bean
    public BeanUtil beanUtil(){
        return new BeanUtil();
    }

}