package com.yss.fsip.web;

import com.yss.fsip.web.config.*;
import com.yss.fsip.web.exception.Error404Controller;
import com.yss.fsip.web.exception.FSIPExceptionHandler;
import com.yss.fsip.web.filter.ContextFilter;
import com.yss.fsip.web.filter.FSIPXssFilter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class})
@EnableConfigurationProperties({FSIPLogProperties.class, FSIPJndiProperties.class, FSIPXssProperties.class})
public class FSIPWebAutoConfiguration {

    @Bean
    public Error404Controller error404Controller(){
        return new Error404Controller();
    }

    @Bean
    public FSIPWebConfigurer fsipWebConfigurer(FSIPLogProperties fsipLogProperties){
        return new FSIPWebConfigurer(fsipLogProperties);
    }

    @Bean
    public ResponseAdvisor responseAdvisor(FSIPLogProperties fsipLogProperties){
        return new ResponseAdvisor(fsipLogProperties);
    }

    @Bean
    public FSIPExceptionHandler fsipExceptionHandler(){
        return new FSIPExceptionHandler();
    }

    @Bean
    public ContextFilter contextFilter(){
        return new ContextFilter();
    }

    @Bean
    public FSIPXssFilter fsipXssFilter(FSIPXssProperties fsipXssProperties){
        return new FSIPXssFilter(fsipXssProperties);
    }

}