package com.yss.fsip.common.util;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author lenglinyong
 * @version 1.0, 2015年10月14日
 * @since 1.6, 2015年10月14日
 */
public class HttpClient {
    static Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private boolean isLoggerError = true;

    private int connectionTimeout = 3000;
    private int readtimeout = 6000;

    public HttpClient() {
    }

    public HttpClient(boolean isLoggerError) {
        this.isLoggerError = isLoggerError;
    }

    /**
     * @param url
     * @param file
     * @return
     */
    public String upload(String url, File file) {

        final int blockSize = 4096 * 10;
        BufferedReader read = null;
        StringBuffer ret = new StringBuffer();
        HttpURLConnection con = null;
        try {
            if (url.toLowerCase().startsWith("https")) {
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                TrustManager[] tm = { new X509TrustManagerNone() };
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tm, null);
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                con = (HttpsURLConnection) new URL(url).openConnection();
                ((HttpsURLConnection) con).setSSLSocketFactory(ssf);
                ((HttpsURLConnection) con).setHostnameVerifier(new AllowAllHostnameVerifier());
            } else {
                con = (HttpURLConnection) new URL(url).openConnection();
            }
            // 设置连接参数属性
            con.setDoOutput(true);
            con.setConnectTimeout(1000 * 60 * 60);
            con.setReadTimeout(1000 * 60 * 60);
            con.setRequestProperty("Accept", "*/*");
            con.setRequestMethod("POST");

            con.setRequestProperty("Accept-Language", "zh-cn");
            con.setRequestProperty("_internalAccess", "true");
            // Map<String,String> headers = RequestHeader.getInstance().getHeader();
            // if(headers !=null){
            // Iterator<Entry<String, String>> it = headers.entrySet().iterator();
            // while(it.hasNext()){
            // Entry<String,String> en = it.next();
            // con.setRequestProperty(en.getKey(), en.getValue());
            // }
            // }
            con.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=---------------------------8d71b5d6290e4");
            con.setRequestProperty("Accept-Encoding", "gzip, deflate");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Cache-Control", "no-cache");

            // 设置流式输出的请求头和请求尾
            String header = "-----------------------------8d71b5d6290e4\r\n"
                    + "Content-Disposition: form-data; name=\"file\"; filename=" + file.getName() + "\r\n"
                    + "Content-Type: application/octet-stream\r\n\r\n";

            byte[] tail = "\r\n-----------------------------8d71b5d6290e4--\r\n".getBytes();
            con.setRequestProperty("Content-Length", String.valueOf(header.length() + file.length() + tail.length));

            byte[] headByte = String.format(header, file.getName()).getBytes();
            // 设置请求体的长度(直接输出,避免在本地进行缓存),同时连接服务器
            con.setFixedLengthStreamingMode((int) file.length() + headByte.length + tail.length);
            // con.connect();
            // 写文件流到请求体中
            OutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.write(headByte); // 写文件头
            FileInputStream fis = new FileInputStream(file);
            int len = 0;
            byte b[] = new byte[blockSize];
            while (len != -1) {
                len = fis.read(b);
                if (len > 0) {
                    dos.write(b, 0, len);
                    dos.flush();
                }
            }

            dos.write(tail);
            dos.flush();
            dos.close();
            fis.close();

            read = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch (Throwable e) {
            handeException(con, url, e);
        } finally {
            String line = "";
            if (read != null) {
                try {
                    while ((line = read.readLine()) != null) {
                        ret.append(line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("读取响应数据出错！", e);
                }
            }

            releaseHttpConnection(con);
        }

        return ret.toString();
    }
    
    public String sendGet(String url) {
      
        return this.sendGet(url, null,null);
    }
    

    public String sendGet(String url, Map<String, String> parameters, Map<String, String> heards) {
        String content = null;
        if (parameters != null && !parameters.isEmpty()) {
            Iterator<Entry<String, String>> it = parameters.entrySet().iterator();
            StringBuffer str = new StringBuffer();
            int i = 0;
            while (it.hasNext()) {
                Entry<String, String> en = it.next();
                if (i != 0) {
                    str.append("&");
                }
                str.append(en.getKey());
                str.append("=");
                str.append(en.getValue());
                i++;
            }
            content = str.toString();
        }
        return this.send(url, content, heards, "GET");
    }

    public String sendPost(String url, String content, Map<String, String> heards) {
        return this.send(url, content, heards, "POST");
    }

    public String send(String url, String content, Map<String, String> heards, String method) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        method = (method == null || method.trim().equals("")) ? "POST" : method;
        HttpURLConnection conn = null;
        try {
            if ("GET".equalsIgnoreCase(method) && content != null) {
                url = url + "?" + content;
            }
            if (url.toLowerCase().startsWith("https")) {
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                TrustManager[] tm = { new X509TrustManagerNone() };
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tm, null);
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                conn = (HttpsURLConnection) new URL(url).openConnection();
                ((HttpsURLConnection) conn).setSSLSocketFactory(ssf);
                ((HttpsURLConnection) conn).setHostnameVerifier(new AllowAllHostnameVerifier());
            } else {
                conn = (HttpURLConnection) new URL(url).openConnection();
            }
            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readtimeout);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("ContentType", "utf-8");
            conn.setRequestProperty("_internalAccess", "true");

            if (heards != null) {
                Iterator<Entry<String, String>> it = heards.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, String> en = it.next();
                    conn.setRequestProperty(en.getKey(), en.getValue());
                }
            }

            conn.setRequestMethod(method);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            if (method.equalsIgnoreCase("POST") && content != null) {
                out = new PrintWriter(conn.getOutputStream());
                out.print(content);
                out.flush();
            }
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Throwable e) {
            handeException(conn, url, e);
        } finally {

            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            releaseHttpConnection(conn);
        }
        return result;
    }

    private void handeException(HttpURLConnection conn, String url, Throwable e) {
        BufferedInputStream bis = null;
        String result = e.getMessage();
        try {
            if (e instanceof MalformedURLException) {
                result = "地址[" + url + "]格式不正确！";
            } else if (conn != null) {
                int code = conn.getResponseCode();
                if (code >= 500) {

                    bis = new BufferedInputStream(conn.getErrorStream());
                    result = inputStream2String(bis);
                } else if (code == 404) {
                    result = "404-[地址" + url + "不正确,或服务端相关功能模块未启动]！";
                }
            }
        } catch (Throwable e1) {
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e1) {
                }
            }
        }
        throw new RuntimeException("请求http地址[" + url + "]失败：" + result, e);
    }

    private String inputStream2String(InputStream is) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadtimeout() {
        return readtimeout;
    }

    public void setReadtimeout(int readtimeout) {
        this.readtimeout = readtimeout;
    }

    /**
     * 正确释放httpconnection
     * @param con
     */
    public static void releaseHttpConnection(HttpURLConnection con) {
        if (con != null) {
            try {
                con.getInputStream().close();
            } catch (Throwable e) {
            }
            try {
                con.getErrorStream().close();
            } catch (Throwable e) {
            }
            try {
                con.disconnect();
            } catch (Throwable e) {
            }
        }
    }
}

class AllowAllHostnameVerifier implements HostnameVerifier {
    public boolean verify(String host, SSLSession session) {
        try {
            Certificate[] certs = session.getPeerCertificates();

            return ((certs != null) && (certs[0] instanceof X509Certificate));
        } catch (SSLException e) {
        }
        return false;
    }
}
class X509TrustManagerNone implements X509TrustManager {
    /*
     * The default X509TrustManager returned by SunX509. We'll delegate
     * decisions to it, and fall back to the logic in this class if the
     * default X509TrustManager doesn't trust it.
     */
    X509TrustManager sunJSSEX509TrustManager;

    public X509TrustManagerNone() throws Exception {
        // create a "default" JSSE X509TrustManager.
        // KeyStore ks = KeyStore.getInstance("JKS");
        // ks.load(new FileInputStream("trustedCerts"), "passphrase".toCharArray());
        // TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
        // tmf.init(ks);
        // TrustManager tms[] = tmf.getTrustManagers();
        // /*
        // * Iterate over the returned trustmanagers, look
        // * for an instance of X509TrustManager. If found,
        // * use that as our "default" trust manager.
        // */
        // for (int i = 0; i < tms.length; i++) {
        // if (tms[i] instanceof X509TrustManager) {
        // sunJSSEX509TrustManager = (X509TrustManager) tms[i];
        // return;
        // }
        // }
        // /*
        // * Find some other way to initialize, or else we have to fail the
        // * constructor.
        // */
        // throw new Exception("Couldn't initialize");
    }

    /*
     * Delegate to the default trust manager.
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // try {
        // sunJSSEX509TrustManager.checkClientTrusted(chain, authType);
        // } catch (CertificateException excep) {
        // // do any special handling here, or rethrow exception.
        // }
    }

    /*
     * Delegate to the default trust manager.
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // try {
        // sunJSSEX509TrustManager.checkServerTrusted(chain, authType);
        // } catch (CertificateException excep) {
        // /*
        // * Possibly pop up a dialog box asking whether to trust the
        // * cert chain.
        // */
        // }
    }

    /*
     * Merely pass this through.
     */
    public X509Certificate[] getAcceptedIssuers() {
        // return sunJSSEX509TrustManager.getAcceptedIssuers();
        return null;
    }
}