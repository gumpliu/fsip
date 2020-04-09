package com.yss.fsip.web.filter.xss;


import com.yss.fsip.web.config.FSIPXssConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * Xss响应包装器
 *
 * @Author: jingminy
 * @Date: 2020/3/31 15:08
 */
public class FSIPXssResponseWrapper extends HttpServletResponseWrapper
{
    private final static Logger logger = LoggerFactory.getLogger(FSIPXssResponseWrapper.class);

    private ByteArrayOutputStream buffer;

    private ServletOutputStream out;

    private HttpServletRequest request;

    private FSIPXssConfig xssConfig;

    private HttpServletResponse response;

    public FSIPXssResponseWrapper(HttpServletRequest request, HttpServletResponse response, FSIPXssConfig xssConfig) {
        super(response);
        this.request = request;
        this.response = response;
        this.xssConfig = xssConfig;
        buffer = new ByteArrayOutputStream();
        out = new WrapperOutputStream(buffer);
    }

    @Override
    public ServletOutputStream getOutputStream()
            throws IOException
    {
        return out;
    }

    @Override
    public void flushBuffer()
            throws IOException
    {
        if (out != null)
        {
            out.flush();
        }
    }

    public byte[] getContent()
            throws IOException
    {
        flushBuffer();
        return buffer.toByteArray();
    }

    class WrapperOutputStream extends ServletOutputStream
    {
        private ByteArrayOutputStream bos;

        public WrapperOutputStream(ByteArrayOutputStream bos)
        {
            this.bos = bos;
        }

        @Override
        public void write(int b)
                throws IOException
        {
            bos.write(b);
        }

//        @Override
//        public void write(byte[] b) throws IOException {
//            String data = new String(b, "UTF-8");
//            String xssEncode = xssConfig.filter(request.getRequestURI().toString(), data, true);
//            logger.info("原始响应数据："+data+"\nxss过滤后的响应数据："+xssEncode);
//            bos.write(b);
//        }

        @Override
        public boolean isReady()
        {

            // TODO Auto-generated method stub
            return false;

        }

        @Override
        public void setWriteListener(WriteListener arg0)
        {

            // TODO Auto-generated method stub

        }
    }

}