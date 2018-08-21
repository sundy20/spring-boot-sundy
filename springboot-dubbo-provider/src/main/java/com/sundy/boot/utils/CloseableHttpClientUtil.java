package com.sundy.boot.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于 httpclient 4.3版本的 CloseableHttpClient工具类
 *
 * @author sundy
 */
public class CloseableHttpClientUtil {

    private static ThreadLocal<CloseableHttpClient> httpClientContainer = new ThreadLocal<CloseableHttpClient>();

    private static final String CHARSET = "UTF-8";

    /**
     * HTTP Get 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> params, String charset, Map<String, String> header) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            HttpGet httpGet = new HttpGet(url);
            RequestConfig.Builder requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000);
            httpGet.setConfig(requestConfig.build());
            //如果有请求头参数，添加进header中
            if (null != header && !header.isEmpty()) {
                for (String key : header.keySet()) {
                    httpGet.addHeader(key, header.get(key));
                }
            }
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeHttpClient(response);
        }
        return null;
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params, String charset, Map<String, String> header) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            RequestConfig.Builder requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000);
            httpPost.setConfig(requestConfig.build());
            //如果有请求头参数，添加进header中
            if (null != header && !header.isEmpty()) {
                for (String key : header.keySet()) {
                    httpPost.addHeader(key, header.get(key));
                }
            }
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeHttpClient(response);
        }
        return null;
    }

    /**
     * JSON方式  post请求
     *
     * @param url
     * @param params
     * @param charset
     * @param header
     * @return
     */
    public static String doPost(String url, String params, String charset, Map<String, String> header) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig.Builder requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000);
            httpPost.setConfig(requestConfig.build());

            StringEntity s = new StringEntity(params, ContentType.create("application/json", charset));

            httpPost.setEntity(s);
            //如果有请求头参数，添加进header中
            if (null != header && !header.isEmpty()) {
                for (String key : header.keySet()) {
                    httpPost.addHeader(key, header.get(key));
                }
            }
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeHttpClient(response);
        }
        return null;
    }

    private static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = httpClientContainer.get();
        if (httpClient == null) {
            //采取连接池实例化
            httpClient = CloseableHttpClientGenerator.getInstance().generateClient();

            //或者自定义构建
//            RequestConfig config = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).setConnectionRequestTimeout(10000).build();
//            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        }
        httpClientContainer.set(httpClient);
        return httpClient;
    }

    private static void closeHttpClient(CloseableHttpResponse response) {
        CloseableHttpClient httpClient = httpClientContainer.get();
        try {
            if (null != response) {
                response.close();
            }
            if (null != httpClient) {
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClientContainer.remove();
        }
    }

    /**
     * 模拟浏览器表单上传文件
     *
     * @param file
     * @param url
     * @param params
     * @param header
     * @return
     */
    public static String doUploadFile(File file, String filename, String url, Map<String, String> params, Map<String, String> header) {
        if (StringUtils.isBlank(url) || null == file) {
            return null;
        }
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.setCharset(Charset.defaultCharset());
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntityBuilder.setBoundary("----------ThIs_Is_tHe_bouNdaRY_$");
            //multipartEntityBuilder.addPart("file", new FileBody(file, ContentType.DEFAULT_BINARY));
            multipartEntityBuilder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, filename);
            //如果有额外的请求参数
            if (!params.isEmpty()) {
                for (String key : params.keySet()) {
                    multipartEntityBuilder.addPart(key, new StringBody(params.get(key), ContentType.DEFAULT_TEXT));
                }
            }

            HttpPost httpPost = new HttpPost(url);
            RequestConfig.Builder requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000);
            httpPost.setConfig(requestConfig.build());
            httpPost.setEntity(multipartEntityBuilder.build());
            httpPost.addHeader("Content-Type", "multipart/form-data; boundary=----------ThIs_Is_tHe_bouNdaRY_$");
            //如果有请求头参数，添加进header中
            if (null != header && !header.isEmpty()) {
                for (String key : header.keySet()) {
                    httpPost.addHeader(key, header.get(key));
                }
            }

            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeHttpClient(response);
        }
        return null;
    }

}
