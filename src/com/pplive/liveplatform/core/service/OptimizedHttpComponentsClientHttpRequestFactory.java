package com.pplive.liveplatform.core.service;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class OptimizedHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;

    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;

    private static final int DEFAULT_SOCKET_TIMEOUT = (10 * 1000);

    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

    public OptimizedHttpComponentsClientHttpRequestFactory() {
        SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

        HttpParams params = new BasicHttpParams();
        ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        ConnManagerParams.setMaxTotalConnections(params, DEFAULT_MAX_TOTAL_CONNECTIONS);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(DEFAULT_MAX_CONNECTIONS_PER_ROUTE));

        HttpConnectionParams.setConnectionTimeout(params, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setStaleCheckingEnabled(params, true);
        HttpConnectionParams.setTcpNoDelay(params, true);

        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        HttpClient httpClient = new DefaultHttpClient(connectionManager, null);
        setReadTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS);

        setHttpClient(httpClient);
    }
}
