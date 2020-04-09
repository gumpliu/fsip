package com.yss.fsip.util;

import com.yss.fsip.log.core.config.AuditLogProperties;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpClientUtil {

    public static String HttpPostWithJson(String url, String json) {

        AuditLogProperties auditLogProperties=BeanUtil.getBean(AuditLogProperties.class);
        String returnValue = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try{
            //第一步：创建HttpClient对象
            httpClient = HttpClients.createDefault();
            //第二步：创建httpPost对象
            HttpPost httpPost = new HttpPost(url);
            //第三步：给httpPost设置JSON格式的参数
            StringEntity requestEntity = new StringEntity(json,"utf-8");
            requestEntity.setContentEncoding("UTF-8");

            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(auditLogProperties.getConnectTimeOut()).setSocketTimeout(auditLogProperties.getSocektTimeOut()).build();
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(requestEntity);
            httpPost.setConfig(requestConfig);
            //第四步：发送HttpPost请求，获取返回值
            returnValue = httpClient.execute(httpPost,responseHandler); //调接口获取返回值时，必须用此方法
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //第五步：处理返回值
        return returnValue;
    }
}
