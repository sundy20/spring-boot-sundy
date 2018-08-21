package com.sundy.boot.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 基于 httpclient 4.5.3版本的 HttpClient工具类
 *
 * @author sundy
 */
public class HttpClientUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String DEFAULT_ENCODING = "utf-8";

    /**
     * 最大连接数
     */
    private static final int MAX_CONNS = 200;

    private static final int SOCKET_TIMEOUT = 30000;

    private static final int CONNECTION_TIMEOUT = 30000;

    /**
     * 重试次数
     */
    private static final int RETRY_TIMES = 1;

    /**
     * 同步使用 volatile
     */
    private static HttpClientUtil instance;

    /**
     * 同步使用 volatile
     */
    private HttpClient client;

    private static List<NameValuePair> paramsConverter(Map<String, String> params) {

        List<NameValuePair> nvps = new LinkedList<>();

        Set<Entry<String, String>> paramsSet = params.entrySet();

        for (Entry<String, String> paramEntry : paramsSet) {

            nvps.add(new BasicNameValuePair(paramEntry.getKey(), paramEntry.getValue()));
        }

        return nvps;
    }

    private static String readStream(InputStream in, String encoding) {

        if (in == null) {
            return null;
        }
        InputStreamReader inReader = null;
        try {
            if (encoding == null) {
                inReader = new InputStreamReader(in, DEFAULT_ENCODING);
            } else {
                inReader = new InputStreamReader(in, encoding);
            }
            int bufferSize = 1024;
            char[] buffer = new char[bufferSize];
            int readLen;
            StringBuilder sb = new StringBuilder();
            while ((readLen = inReader.read(buffer)) != -1) {
                sb.append(buffer, 0, readLen);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inReader != null) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private HttpClientUtil() {
        //设置连接参数
        ConnectionConfig connConfig = ConnectionConfig.custom().setCharset(Charset.forName(DEFAULT_ENCODING)).build();
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(SOCKET_TIMEOUT).setSoKeepAlive(true).setTcpNoDelay(true).build();
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        //指定信任密钥存储对象和连接套接字工厂
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

            SSLContextBuilder builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(trustStore, (TrustStrategy) (x509Certificates, s) -> true);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

            registryBuilder.register("https", sslsf);

        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }

        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        //设置连接管理器
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
        connManager.setDefaultConnectionConfig(connConfig);
        connManager.setDefaultSocketConfig(socketConfig);
        connManager.setMaxTotal(MAX_CONNS);
        connManager.setDefaultMaxPerRoute(connManager.getMaxTotal());
        //指定cookie存储对象
        /*
      同步使用 volatile
     */
        BasicCookieStore cookieStore = new BasicCookieStore();
        //构建客户端重试控制器   并设置重试次数
        HttpRequestRetryHandler myRetryHandler = setRetryHandler(RETRY_TIMES);

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).setConnectionManager(connManager)
                .setRetryHandler(myRetryHandler);

        //启用gzip deflate压缩传输
        useGzip(httpClientBuilder);

        client = httpClientBuilder.build();

    }

    /**
     * 重试策略
     */
    private HttpRequestRetryHandler setRetryHandler(final int retryTimes) {

        return (IOException exception, int executionCount, HttpContext context) -> {

            if (executionCount >= retryTimes) {
                //如果已经重试了1次，就放弃
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                // Timeout
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // Unknown host
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL handshake exception
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            return !(request instanceof HttpEntityEnclosingRequest);
        };
    }

    /**
     * gzip解压缩策略
     */
    private void useGzip(HttpClientBuilder httpClientBuilder) {
        httpClientBuilder.addInterceptorFirst((HttpRequestInterceptor) (req, arg1) -> {
            if (!req.containsHeader("Accept-Encoding")) {
                req.addHeader("Accept-Encoding", "gzip,deflate,sdch");
            }
        });

        httpClientBuilder.addInterceptorFirst((HttpResponseInterceptor) (resp, arg1) -> {
            Header[] headers = resp.getHeaders("Content-Encoding");
            for (Header header : headers) {
                if ("gzip".equals(header.getValue())) {
                    resp.setEntity(new GzipDecompressingEntity(resp.getEntity()));
                    return;
                } else if ("deflate".equals(header.getValue())) {
                    resp.setEntity(new DeflateDecompressingEntity(resp.getEntity()));
                    return;
                }
            }

        });
    }

    public static HttpClientUtil getInstance() {
        if (HttpClientUtil.instance == null) {
            synchronized (HttpClientUtil.class) {
                if (HttpClientUtil.instance == null) {
                    instance = new HttpClientUtil();
                }
            }
        }
        return instance;
    }

    private InputStream doGetForStream(String url) throws Exception {
        HttpResponse response = this.doGet(url, null, null);
        return response != null ? response.getEntity().getContent() : null;
    }

    private InputStream doGetForStream(String url, Map<String, String> header) throws Exception {
        HttpResponse response = this.doGet(url, null, header);
        return response != null ? response.getEntity().getContent() : null;
    }

    public String doGetForString(String url, String charset) throws Exception {
        return HttpClientUtil.readStream(this.doGetForStream(url), charset);
    }

    public String doGetForString(String url, Map<String, String> header, String charset) throws Exception {
        return HttpClientUtil.readStream(this.doGetForStream(url, header), charset);
    }

    public Map doGetForMap(String url, String charset) throws Exception {
        HttpResponse response = doGet(url, null, null);
        if (200 == response.getStatusLine().getStatusCode()) {
            String data = HttpClientUtil.readStream(response.getEntity().getContent(), charset);
            return JSON.parseObject(data, Map.class);
        } else {
            throw new IOException("抓取" + url + "请求出现错误：" + response.getStatusLine().getStatusCode());
        }
    }

    public Map doGetForMap(String url, Map<String, String> header, String charset) throws Exception {
        HttpResponse response = doGet(url, null, header);
        if (200 == response.getStatusLine().getStatusCode()) {
            String data = HttpClientUtil.readStream(response.getEntity().getContent(), charset);
            return JSON.parseObject(data, Map.class);
        } else {
            throw new IOException("请求出现错误：" + response.getStatusLine().getStatusCode());
        }
    }

    /**
     * 基本的Get请求
     */
    private HttpResponse doGet(String url, Map<String, String> queryParams, Map<String, String> header) throws Exception {
        HttpGet gm = new HttpGet();
        RequestConfig.Builder requestConfig = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setConnectionRequestTimeout(CONNECTION_TIMEOUT).setSocketTimeout(CONNECTION_TIMEOUT);

        gm.setConfig(requestConfig.build());
        //如果有请求头参数，添加进header中
        if (null != header && !header.isEmpty()) {
            for (String key : header.keySet()) {
                gm.addHeader(key, header.get(key));
            }
        }
        URIBuilder builder = new URIBuilder(url);
        //填入查询参数
        if (queryParams != null && !queryParams.isEmpty()) {
            builder.setParameters(HttpClientUtil.paramsConverter(queryParams));
        }
        gm.setURI(builder.build());
        log.info("------httpclient get url:" + url);

        return client.execute(gm);
    }

    private InputStream doPostForStream(String url, Map<String, String> formParams) throws Exception {
        HttpResponse response = this.doPost(url, null, formParams, null);
        return response != null ? response.getEntity().getContent() : null;
    }

    private InputStream doPostForStream(String url, Map<String, String> formParams, Map<String, String> header) throws Exception {
        HttpResponse response = this.doPost(url, null, formParams, header);
        return response != null ? response.getEntity().getContent() : null;
    }

    private InputStream doJsonPostForStream(String url, String params, Map<String, String> header, String charset) throws Exception {
        HttpResponse response = this.doJsonPost(url, null, params, header, charset);
        return response != null ? response.getEntity().getContent() : null;
    }

    public String doPostForString(String url, Map<String, String> formParams) throws Exception {
        return HttpClientUtil.readStream(this.doPostForStream(url, formParams), DEFAULT_ENCODING);
    }

    public String doPostForString(String url, Map<String, String> formParams, String charset) throws Exception {
        return HttpClientUtil.readStream(this.doPostForStream(url, formParams), charset);
    }

    public String doPostForString(String url, Map<String, String> formParams, Map<String, String> header, String charset) throws Exception {
        return HttpClientUtil.readStream(this.doPostForStream(url, formParams, header), charset);
    }

    public String doJsonPostForString(String url, String params, String charset) throws Exception {
        return HttpClientUtil.readStream(this.doJsonPostForStream(url, params, null, charset), charset);
    }

    public String doJsonPostForString(String url, String params) throws Exception {
        return HttpClientUtil.readStream(this.doJsonPostForStream(url, params, null, DEFAULT_ENCODING), DEFAULT_ENCODING);
    }

    public String doJsonPostForString(String url, String params, Map<String, String> header, String charset) throws Exception {
        return HttpClientUtil.readStream(this.doJsonPostForStream(url, params, header, charset), charset);
    }

    public Map doPostForMap(String url, Map<String, String> formParams, String charset) throws Exception {
        HttpResponse response = doPost(url, null, formParams, null);
        if (200 == response.getStatusLine().getStatusCode()) {
            String data = HttpClientUtil.readStream(response.getEntity().getContent(), charset);
            return JSON.parseObject(data, Map.class);
        } else {
            throw new IOException("请求出现错误：" + response.getStatusLine().getStatusCode());
        }
    }

    public Map doPostForMap(String url, Map<String, String> formParams, Map<String, String> header, String charset) throws Exception {
        HttpResponse response = doPost(url, null, formParams, header);
        if (200 == response.getStatusLine().getStatusCode()) {
            String data = HttpClientUtil.readStream(response.getEntity().getContent(), charset);
            return JSON.parseObject(data, Map.class);
        } else {
            throw new IOException("请求出现错误：" + response.getStatusLine().getStatusCode());
        }
    }

    /**
     * 基本的Post请求
     */
    private HttpResponse doPost(String url, Map<String, String> queryParams, Map<String, String> formParams, Map<String, String> header) throws Exception {
        HttpPost pm = new HttpPost();
        URIBuilder builder = new URIBuilder(url);
        //填入查询参数
        if (queryParams != null && !queryParams.isEmpty()) {
            builder.setParameters(HttpClientUtil.paramsConverter(queryParams));
        }
        pm.setURI(builder.build());

        //如果有请求头参数，添加进header中
        if (null != header && !header.isEmpty()) {
            for (String key : header.keySet()) {
                pm.addHeader(key, header.get(key));
            }
        }

        //填入表单参数
        if (formParams != null && !formParams.isEmpty()) {
            pm.setEntity(new UrlEncodedFormEntity(HttpClientUtil.paramsConverter(formParams), DEFAULT_ENCODING));
        }

        //设置超时
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setConnectionRequestTimeout(CONNECTION_TIMEOUT).setSocketTimeout(CONNECTION_TIMEOUT).build();
        pm.setConfig(requestConfig);
        log.info("------httpclient post url: " + url + " formParams : " + formParams);
        return client.execute(pm);
    }


    /**
     * 基本json方式post请求
     */
    private HttpResponse doJsonPost(String url, Map<String, String> queryParams, String params, Map<String, String> header, String charset) throws Exception {
        HttpPost pm = new HttpPost();
        URIBuilder builder = new URIBuilder(url);
        //填入查询参数
        if (queryParams != null && !queryParams.isEmpty()) {
            builder.setParameters(HttpClientUtil.paramsConverter(queryParams));
        }
        pm.setURI(builder.build());

        //如果有请求头参数，添加进header中
        if (null != header && !header.isEmpty()) {
            for (String key : header.keySet()) {
                pm.addHeader(key, header.get(key));
            }
        }

        //填入json请求参数
        StringEntity s = new StringEntity(params, ContentType.create("application/json", charset));
        pm.setEntity(s);

        //设置超时
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setConnectionRequestTimeout(CONNECTION_TIMEOUT).setSocketTimeout(CONNECTION_TIMEOUT).build();
        pm.setConfig(requestConfig);
        log.info("------httpclient post url: " + url + "pa");
        return client.execute(pm);
    }

}
