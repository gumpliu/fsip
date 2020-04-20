package com.yss.fsip.web.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yss.fsip.context.FSIPContext;
import com.yss.fsip.context.FSIPContextFactory;
import com.yss.fsip.util.IDGeneratorFactory;
import com.yss.fsip.web.utils.FSIPWebConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 打印日志
 *
 */
public class LoggerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    private static final String ERROR_PATH = "/error";

    private static final ObjectMapper mapper = new ObjectMapper();


    private FSIPLogProperties fsipLogProperties;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        try {

            if(request.getRequestURI().contains(ERROR_PATH)){
                return true;
            }

            Method method = ((HandlerMethod)handler).getMethod();

            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            //创建一个 json 对象，用来存放 http 日志信息

            //请求方式 Get Post
            String requestMethod =  requestWrapper.getMethod();
            JsonNode newNode;
            if (requestMethod.equals("GET")) {
                newNode = mapper.valueToTree(requestWrapper.getParameterMap());
            } else {
                newNode =  mapper.readTree(requestWrapper.getContentAsByteArray());
            }
            /**
             * 满足以下条件之一，不统一打印log：
             *  1、配置文件fsip.log.request=false 打印request开关为false
             *  2、请求参数使用了@RequestBody注解
             *  3、请求参数大于设置fsip.log.requestMax值。
             */
            if(!fsipLogProperties.isRequest()
                    ||!isPrintRequestLog(method)
                    || (newNode != null && newNode.size() > fsipLogProperties.getRequestMax())){
                return true;
            }

            ObjectNode rootNode = mapper.createObjectNode();
            //todo 设置序号值
            FSIPContext context = FSIPContextFactory.getContext();

            String serialNo = IDGeneratorFactory.getIDGenerator().nextId() + "";

            context.setAttribute(FSIPWebConstants.CONTEXT_KEY_SERIALNO, serialNo);
            context.setAttribute(FSIPWebConstants.CONTEXT_KEY_STARTTIME, System.currentTimeMillis() + "");

            rootNode.put(FSIPWebConstants.CONTEXT_KEY_SERIALNO, context.getAttribute(FSIPWebConstants.CONTEXT_KEY_SERIALNO));
            rootNode.put("uri", requestWrapper.getRequestURI());
            rootNode.put("requestMethod", requestMethod);
            rootNode.put("className", ((HandlerMethod)handler).getBeanType().getName());
            rootNode.put("method", method.getName());

            rootNode.set("request", newNode);


            logger.info(rootNode.toString());
        }catch (Exception e){
            logger.error(e.getMessage());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//            throws Exception {
//        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
//
//        Method method = ((HandlerMethod)handler).getMethod();
//        JsonNode newNode = mapper.readTree(responseWrapper.getContentAsByteArray());
//
//        /**
//         * 满足以下条件之一，不统一打印log：
//         *  1、配置文件fsip.log.response=false 打印response开关为false
//         *  2、请求参数使用了@RequestBody注解
//         *  3、请求参数大于设置fsip.log.responseMax值。
//         */
//        if(!fsipLogProperties.isResponse()
//                ||!isPrintRequestLog(method)
//                || newNode.size() > fsipLogProperties.getResponseMax()){
//            return;
//        }
//
//        ObjectNode rootNode = mapper.createObjectNode();
//
//        rootNode.put("status", responseWrapper.getStatus());
//        rootNode.set("response", newNode);
//
//        responseWrapper.copyBodyToResponse();
//
//        logger.info(rootNode.toString());
//    }


    private Map<String, Object> getRequestHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;

    }

    private Map<String, Object> getResponsetHeaders(ContentCachingResponseWrapper response) {
        Map<String, Object> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            headers.put(headerName, response.getHeader(headerName));
        }
        return headers;
    }

    /**
     * 是否打印request 请求信息，当为RequestBody时不打印信息
     * @param method
     * @return
     */
    private boolean isPrintRequestLog(Method method){
        Annotation[][] annotations =  method.getParameterAnnotations();

        if(annotations != null && annotations.length > 0){
            for(Annotation[] annotations1 : annotations){
                if(annotations1 != null && annotations1.length > 0){
                    for(Annotation annotation : annotations1){
                        if(annotation.annotationType().isAssignableFrom(RequestBody.class)){
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public void setFsipLogProperties(FSIPLogProperties fsipLogProperties) {
        this.fsipLogProperties = fsipLogProperties;
    }
}