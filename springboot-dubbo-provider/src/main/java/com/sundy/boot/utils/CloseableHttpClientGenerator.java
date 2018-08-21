package com.sundy.boot.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;


public class CloseableHttpClientGenerator {

    private static volatile CloseableHttpClientGenerator instance;

    private PoolingHttpClientConnectionManager connectionManager;

    private ConnectionSocketFactory plainSF;

    private KeyStore trustStore;

    private SSLContext sslContext;

    private LayeredConnectionSocketFactory sslSF;

    private Registry<ConnectionSocketFactory> registry;

    public static String defaultEncoding = "utf-8";

    private final static String USERAGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36";

    public CloseableHttpClientGenerator() {

        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
        plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        //指定信任密钥存储对象和连接套接字工厂
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            SSLContextBuilder builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(trustStore, (TrustStrategy) (x509Certificates, s) -> true);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

            registryBuilder.register("https", sslsf);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        registry = registryBuilder.build();
        //设置连接管理器
        connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(connectionManager.getMaxTotal());
    }

    public static CloseableHttpClientGenerator getInstance() {
        if (CloseableHttpClientGenerator.instance == null) {
            synchronized (CloseableHttpClientGenerator.class) {
                if (CloseableHttpClientGenerator.instance == null) {
                    instance = new CloseableHttpClientGenerator();
                }
            }
        }
        return instance;
    }

    public CloseableHttpClientGenerator setPoolSize(int poolSize) {
        connectionManager.setMaxTotal(poolSize);
        return this;
    }

    public CloseableHttpClient generateClient() {
        return generateClient(USERAGENT, "", false, 0, null);
    }

    public CloseableHttpClient generateClient(String domain, Map<String, String> cookieMap) {
        return generateClient(USERAGENT, domain, false, 0, cookieMap);
    }

    /**
     * @param userAgent
     * @param domain
     * @param isUseGzip
     * @param retryTimes 网络重试次数
     * @param cookieMap  cookie键值对
     * @return
     */
    public CloseableHttpClient generateClient(String userAgent, String domain, boolean isUseGzip, int retryTimes, Map<String, String> cookieMap) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
        if (StringUtils.isNotEmpty(userAgent)) {
            httpClientBuilder.setUserAgent(userAgent);
        } else {
            httpClientBuilder.setUserAgent("");
        }
        if (isUseGzip) {

            httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {

                @Override
                public void process(org.apache.http.HttpRequest req, HttpContext arg1) throws HttpException, IOException {
                    if (!req.containsHeader("Accept-Encoding")) {
                        req.addHeader("Accept-Encoding", "gzip,deflate,sdch");
                    }
                }

            });

            httpClientBuilder.addInterceptorFirst(new HttpResponseInterceptor() {
                @Override
                public void process(org.apache.http.HttpResponse resp, HttpContext arg1) throws HttpException, IOException {
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

                }
            });

        }

        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        ConnectionConfig connConfig = ConnectionConfig.custom().setCharset(Charset.forName(defaultEncoding)).build();
        httpClientBuilder.setDefaultConnectionConfig(connConfig);
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(retryTimes, true));
        CookieStore cookieStore = new BasicCookieStore();
        if (cookieMap != null) {
            for (Map.Entry<String, String> cookieEntry : cookieMap.entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(domain);
                cookieStore.addCookie(cookie);
            }
        }
        httpClientBuilder.disableCookieManagement();
        httpClientBuilder.setDefaultCookieStore(cookieStore);
        return httpClientBuilder.build();
    }

    class AnyTrustStrategy implements TrustStrategy {

        @Override
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            return true;
        }

    }
}
