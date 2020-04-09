package com.yss.fsip.web.filter;

import com.yss.fsip.common.util.StringUtil;
import com.yss.fsip.web.config.FSIPXssConfig;
import com.yss.fsip.web.config.FSIPXssProperties;
import com.yss.fsip.web.filter.xss.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字典类目Controller控制器
 *
 * @author jingminy
 * @date 2019/12/17 10:23
 */
@WebFilter(filterName = "fsipXssFilter", urlPatterns = "/*")
@Component
public class FSIPXssFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(FSIPXssFilter.class);

    // xss配置信息，取自yaml
    private FSIPXssProperties xssProperties;

    // 根据FSIPXssProperties初始化后的xss配置信息
    private FSIPXssConfig xssConfig;

    private volatile static boolean isLoaded = false;

    private static Lock lock = new ReentrantLock();

    public FSIPXssFilter(FSIPXssProperties xssProperties) {
        this.xssProperties = xssProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String requestUri = req.getRequestURI().toString();

        // 第一次请求时加载xss过滤规则配置信息，只加载一次
        if (!isLoaded) {
            loadXssConfig();
        }

        // 判断是否需要xss过滤数据，默认需要过滤，不想过滤则在yaml的excludeXssPaths参数中逗号隔开追加记录
        FSIPXssResponseWrapper wrapperResponse = null;
        if (!isExcludeXssPath(requestUri)) {
            // 第一次需要xss请求过来时加载xssapplication.yaml中的xss配置
            request = new FSIPXssRequestWraper(req, xssConfig);
            wrapperResponse = new FSIPXssResponseWrapper(req, res, xssConfig);//转换成代理类
            response = wrapperResponse;
        }

        // 这里只拦截返回，直接让请求过去，如果在请求前有处理，可以在这里处理
        chain.doFilter(request, wrapperResponse);

        if(wrapperResponse != null) {
            byte[] content = wrapperResponse.getContent();//获取返回值
            //判断是否有值
            if (content.length > 0) {

                String data = new String(content, "UTF-8");
                String xssEncode = xssConfig.filter(requestUri, data, true);
                logger.info("原始响应数据：" + data + "，xss过滤后的响应数据：" + xssEncode);
                //把返回值输出到客户端
                ServletOutputStream out = res.getOutputStream();
                //我测试了需要加上这行代码,才能对 response的内容 修改生效，
                //好像是需要保持长度一致，不然请求就一直会处于等待，希望对后来的小伙伴有点帮助
                res.setContentLength(xssEncode.length());
                out.write(xssEncode.getBytes());
                out.flush();
            }
        }
    }

    //兼容filter 低版本，低版本没有默认实现
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }


    /**
     * 加载XSS配置信息
     *
     * @Author: jingminy
     * @Date: 2020/3/27 15:30
     */
    private void loadXssConfig() {
        try {
            lock.lock();
            xssConfig = new FSIPXssConfig(xssProperties);
            isLoaded = true;
        } finally {
            lock.unlock();
        }

    }

    /**
     * 判断当前请求路径是否需要进行xss过滤
     *
     * @param uri 请求路径uri
     * @return Boolean
     */
    private boolean isExcludeXssPath(String uri) {
        boolean result = false;
        Pattern[] excludeXssPathPatterns = xssConfig.getExcludeXssPathPatterns();
        if (excludeXssPathPatterns == null || uri == null) {//路径的过滤
            return result;
        }
        for (Pattern xsspathPattern : excludeXssPathPatterns) {
            Matcher m = xsspathPattern.matcher(uri);
            if (m.find()) {
                result = true;
                break;
            }
        }
        return result;
    }

}
