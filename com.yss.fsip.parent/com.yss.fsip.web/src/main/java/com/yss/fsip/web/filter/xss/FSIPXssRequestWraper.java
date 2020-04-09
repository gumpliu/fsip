package com.yss.fsip.web.filter.xss;/**
 * @author jingminy
 * @date 2020/3/11 14:46
 **/

import com.yss.fsip.common.util.StringUtil;
import com.yss.fsip.web.config.FSIPXssConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * Xss请求包装器
 *
 * @author: jingminy
 * @create: 2020/3/11 14:46
 **/
public class FSIPXssRequestWraper extends HttpServletRequestWrapper {
    private final static Logger logger = LoggerFactory.getLogger(FSIPXssRequestWraper.class);

    private String body;

    private HttpServletRequest request;

    private FSIPXssConfig xssConfig;

    private String requestUri;

    public FSIPXssRequestWraper(HttpServletRequest request, FSIPXssConfig xssConfig) {
        super(request);
        this.request = request;
        this.xssConfig = xssConfig;

        requestUri = request.getRequestURI().toString();
        initBody();
    }

//    @Override
//    public String getHeader(String name) {
//        try {
//            request.getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String value = super.getHeader(name);
//        return HtmlUtils.htmlEscape(value);
//    }

    @Override
    public String getParameter(String name) {
        String value = request.getParameter(name);
        if (StringUtils.isEmpty(value)) {
            return value;
        }

        //做xss过滤处理
        return xssCustomFilter(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = request.getParameterValues(name);
        if (parameterValues == null) {
            return null;
        }
        //做xss过滤处理
        int length = parameterValues.length;
        String[] xssEncodes = new String[length];
        for (int i = 0; i < length; i++) {
            String value = parameterValues[i];
            xssEncodes[i] = xssCustomFilter(value);
        }
        return xssEncodes;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        String _body = body;
        if (StringUtils.isNotEmpty(body)) {
            //做xss过滤处理
            _body = xssCustomFilter(_body);
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(_body.getBytes());
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;

    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    private String xssCustomFilter(String value) {
        if(StringUtil.isEmpty(value)) {
            return value;
        }
        return xssConfig.filter(requestUri, value, true);
    }

    private void initBody() {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.body = stringBuilder.toString();
    }

}
