package com.fengcloud.papercheck.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    /**
     * http get请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param pmap    请求参数
     * @param ishttps 是否使用https  true:使用 false:不使用
     * @return
     */
    public static String sendHttpsGet(String url, Map<String, String> headers, Map<String, String> pmap, boolean ishttps) {
        CloseableHttpClient client = HttpClients.createDefault();
        if (ishttps) {
            client = wrapClient(client);
        }
        // 实例化HTTP方法
        HttpGet get = new HttpGet();
        if (headers != null && headers.size() > 0) {
            for (String keyh : headers.keySet()) {
                get.setHeader(keyh, headers.get(keyh));
            }
        }
        String params = "";
        if (pmap != null && pmap.size() > 0) {
            for (String keyp : pmap.keySet()) {
                params += "&" + keyp + "=" + pmap.get(keyp);
            }
        }
        url += params.replaceAll("^&", "?");
        String result = "";
        CloseableHttpResponse response = null;
        try {
            get.setURI(new URI(url));
            response = client.execute(get);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(client, response);
        }
        return result;

    }


    /**
     * http post请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param pmap    请求参数
     * @param ishttps 是否使用https  true:使用 false:不使用
     * @return HttpEntity  使用org.apache.http.util.EntityUtils.toString()、com.alibaba.fastjson.JSON.parseObject()解析
     */
    public static HttpEntity sendHttpsPost(String url, Map<String, String> headers, Map<String, String> pmap, boolean ishttps) {
        CloseableHttpClient client = HttpClients.createDefault();
        if (ishttps) {
            client = wrapClient(client);
        }
        HttpPost postrequest = new HttpPost(url);
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        try {
            if (headers != null && headers.size() > 0) {
                for (String keyh : headers.keySet()) {
                    postrequest.setHeader(keyh, headers.get(keyh));
                }
            }
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if (pmap != null && pmap.size() > 0) {
                for (String key : pmap.keySet()) {
                    nvps.add(new BasicNameValuePair(key, pmap.get(key)));
                }
            }
            postrequest.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            response = client.execute(postrequest);
            entity = response.getEntity();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(client, response);
        }
        return entity;
    }

    /**
     * 获取可信任https链接，以避免不受信任证书出现peer not authenticated异常
     *
     * @param base
     * @return
     */
    private static CloseableHttpClient wrapClient(CloseableHttpClient base) {
        try {
            // 在调用SSL之前需要重写验证方法，取消检测SSL
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String str) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String str) {
                }
            };
            SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            ctx.init(null, new TrustManager[]{trustManager}, null);
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
            // 创建Registry
            RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT)
                    .setExpectContinueEnabled(Boolean.TRUE).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", socketFactory).build();
            // 创建ConnectionManager，添加Connection配置信息
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(requestConfig).build();
            return closeableHttpClient;
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 关闭连接资源
     *
     * @param client
     * @param response
     */
    private static void closeResource(CloseableHttpClient client, CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
