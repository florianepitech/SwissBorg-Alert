package com.swissborg.apybot.network;

import com.swissborg.apybot.exception.HttpClientException;
import com.swissborg.apybot.exception.HttpServerException;
import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

public class HttpClientSession {

    private HttpClient httpClient;
    private BasicCookieStore httpCookieStore = new BasicCookieStore();
    private RequestConfig.Builder requestConfig;
    private String userAgent = RandomUserAgent.getRandomUserAgent();
    private int timeout = 10 * 1000;

    private static final TrustStrategy trustAllStrategy = new TrustStrategy() {
        @Override
        public boolean isTrusted(X509Certificate[] chain, String authType) {
            return true;
        }
    };

    public HttpClientSession() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.httpClient = newHttpClient();
        this.requestConfig = setRequestConfig();
    }

    public RequestConfig.Builder setRequestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(timeout);
        builder.setSocketTimeout(timeout);
        builder.setConnectionRequestTimeout(timeout);
        builder.setRedirectsEnabled(false);
        builder.setCookieSpec(CookieSpecs.STANDARD_STRICT);
        return builder;
    }

    public void resetHttpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.httpClient = newHttpClient();
    }

    public HttpClient newHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.loadTrustMaterial(trustAllStrategy);
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());

        return HttpClientBuilder.create()
                .setDefaultCookieStore(httpCookieStore)
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
    }

    public HttpResponse sendGet(String url) throws IOException, HttpServerException, HttpClientException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(getRequestConfig().build());
        httpGet.addHeader(HttpHeaders.USER_AGENT, getUserAgent());
        httpGet.addHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
        HttpResponse httpResponse = getHttpClient().execute(httpGet);
        assertRequest(httpResponse);
        return httpResponse;
    }

    public HttpResponse sendPost(String url, String content, ContentType contentType) throws IOException, HttpServerException, HttpClientException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(getRequestConfig().build());
        httpPost.addHeader(HttpHeaders.USER_AGENT, getUserAgent());
        httpPost.addHeader(HttpHeaders.CONTENT_TYPE, contentType.getMimeType());
        httpPost.addHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
        httpPost.setEntity(new StringEntity(content));
        HttpResponse httpResponse = getHttpClient().execute(httpPost);
        assertRequest(httpResponse);
        return httpResponse;
    }

    public HttpResponse sendForm(String url, List<NameValuePair> form) throws IOException, HttpServerException, HttpClientException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(getRequestConfig().build());
        httpPost.addHeader(HttpHeaders.USER_AGENT, getUserAgent());
        httpPost.addHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        assertRequest(httpResponse);
        return httpResponse;
    }

    public HttpResponse sendDelete(String url) throws IOException, HttpServerException, HttpClientException {
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setConfig(getRequestConfig().build());
        httpDelete.addHeader(HttpHeaders.USER_AGENT, getUserAgent());
        httpDelete.addHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
        HttpResponse httpResponse = httpClient.execute(httpDelete);
        assertRequest(httpResponse);
        return httpResponse;
    }

    public HttpResponse sendPut(String url, String content, ContentType contentType) throws IOException, HttpServerException, HttpClientException, HttpClientException {
        HttpPut httpPut = new HttpPut(url);
        httpPut.setConfig(getRequestConfig().build());
        httpPut.addHeader(HttpHeaders.USER_AGENT, getUserAgent());
        httpPut.addHeader(HttpHeaders.CONTENT_TYPE, contentType.getMimeType());
        httpPut.addHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
        httpPut.setEntity(new StringEntity(content));
        HttpResponse httpResponse = getHttpClient().execute(httpPut);
        assertRequest(httpResponse);
        return httpResponse;
    }

    private void assertRequest(HttpResponse httpResponse) throws HttpClientException {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode >= 500 && statusCode <= 599)
            throw new HttpClientException(httpResponse);
        else if (statusCode >= 400 && statusCode <= 499)
            throw new HttpClientException(httpResponse);
    }

    /*
     *      GETTER
     */

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public BasicCookieStore getHttpCookieStore() {
        return httpCookieStore;
    }

    public static TrustStrategy getTrustAllStrategy() {
        return trustAllStrategy;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public RequestConfig.Builder getRequestConfig() {
        return requestConfig;
    }

    private int getTimeout() {
        return timeout;
    }
}