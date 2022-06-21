/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;

import java.util.HashSet;

/**
 * Builds an org.apache.http.client.HttpClient used for making secure API calls over HTTP.
 * Uses TLSv1.2 protocol.
 */
public class HttpProtocolClientBuilder {

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * The default connection timeout value to establish a connection specified <b>in milliseconds</b>, set to 60 seconds.
     */
    protected static final int DEFAULT_CONNECTION_TIMEOUT = 60000;

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * The default connection timeout value for requesting a connection from the Apache connection manager specified
     * <b>in milliseconds</b>, set to 60 seconds.
     */
    protected static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 60000;

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * The default socket timeout value when waiting for data between data packets specified <b>in milliseconds</b>, set to 60 seconds.
     */
    protected static final int DEFAULT_SOCKET_TIMEOUT = 60000;

    /**
     * The user agent header used for requests from the SDK.
     */
    protected static final String HDR_USER_AGENT = "User-Agent";

    /**
     * The value of the user agent header.
     */
    protected static final String HDR_VALUE_USER_AGENT = "EllucianEthosIntegrationSdk-java/1.0.0";

    /**
     * Uses the TLSv1.2 protocol.
     */
    private static final String PROTOCOL = "TLSv1.2";

    /**
     * The Ssl connection socket factory.
     */
    protected SSLConnectionSocketFactory sslConnectionSocketFactory;
    /**
     * The Request config builder.
     */
    protected RequestConfig.Builder requestConfigBuilder;

    /**
     * The default headers.  Header values contain PRAGMA and CACHE_CONTROL both set to a "no-cache" option to ensure
     * a fresh version of the resource data.
     */
    protected HashSet<Header> defaultHeaders;

    /**
     * Instantiates a new Http client builder service and initializes it with the default configurations for the
     * sslConnectionSocketFactory, and defaultHeaders.  Uses the given timeout values to initialize the requestConfigBuilder.
     * <p>
     * The timeout params are specified as Integer values, which can be null since Integer is an object.  If a given timeout
     * param is null, the corresponding default timeout value will be used in it's place.
     * <p>
     * <i>The timeout parameter values are in seconds, and converted to milliseconds when initializing the requestConfigBuilder.</i>
     * @param connectionTimeout The timeout <b>in seconds</b> for a connection to be established.
     * @param connectionRequestTimeout The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager.
     * @param socketTimeout The timeout <b>in seconds</b> when waiting for data between consecutive data packets.
     */
    public HttpProtocolClientBuilder( Integer connectionTimeout, Integer connectionRequestTimeout, Integer socketTimeout ) {
        super();
        doInitialization( connectionTimeout, connectionRequestTimeout, socketTimeout );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Initializes the request config builder, SSL connection socket factory, and the default headers used when building the
     * Http client.
     * @param connectionTimeout The timeout <b>in seconds</b> for a connection to be established, or null to use the default
     *                          value of 60 seconds..
     * @param connectionRequestTimeout The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager,
     *                                 or null to use the default value of 60 seconds.
     * @param socketTimeout The timeout <b>in seconds</b> when waiting for data between consecutive data packets, or null to use the default
     *                      value of 60 seconds.
     */
    protected void doInitialization(  Integer connectionTimeout, Integer connectionRequestTimeout, Integer socketTimeout ) {
        connectionTimeout        = (connectionTimeout == null ? DEFAULT_CONNECTION_TIMEOUT : convertToMilliSeconds(connectionTimeout));
        connectionRequestTimeout = (connectionRequestTimeout == null ? DEFAULT_CONNECTION_REQUEST_TIMEOUT : convertToMilliSeconds(connectionRequestTimeout));
        socketTimeout            = (socketTimeout == null ? DEFAULT_SOCKET_TIMEOUT : convertToMilliSeconds(socketTimeout));
        init( connectionTimeout, connectionRequestTimeout, socketTimeout );
    }


    /**
     * Builds and returns an HttpClient using the pre-initialized configurations.
     * Each time this method is called a new HttpClient is built and returned.
     * @return HttpClient
     */
    public HttpClient buildHttpClient() {
        final RequestConfig requestConfig = requestConfigBuilder.build();
        final org.apache.http.impl.client.HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultHeaders( defaultHeaders );
        return httpClientBuilder.setDefaultRequestConfig(requestConfig).useSystemProperties().setSSLSocketFactory(sslConnectionSocketFactory).build();
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Initialize various configurations used for making HTTP calls.
     * Instantiates the sslConnectionSocketFactory, the requestConfigBuilder, and sets the defaultHeaders.
     * @param connectionTimeout The timeout in milliseconds for a connection to be established.
     * @param connectionRequestTimeout The timeout in milliseconds when requesting a connection from the Apache connection manager.
     * @param socketTimeout The timeout in milliseconds when waiting for data between consecutive data packets.
     */
    protected void init( int connectionTimeout, int connectionRequestTimeout, int socketTimeout ) {
        String[] protocolArray = new String[1];
        protocolArray[0] = PROTOCOL;
        sslConnectionSocketFactory = new SSLConnectionSocketFactory(SSLContexts.createDefault(), protocolArray,null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        requestConfigBuilder = RequestConfig.custom().setConnectTimeout(connectionTimeout).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout);

        defaultHeaders = new HashSet<Header>();
        defaultHeaders.add(new BasicHeader(HttpHeaders.PRAGMA, "no-cache"));
        defaultHeaders.add(new BasicHeader(HttpHeaders.CACHE_CONTROL, "no-cache"));
        defaultHeaders.add(new BasicHeader(HDR_USER_AGENT, HDR_VALUE_USER_AGENT));
    }

    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * Converts a timeout value to milliseconds multiplying it by 1000.
     * @param timeoutInSeconds The timeout value in seconds to convert to milliseconds.
     * @return The given timeout value, converted to milliseconds.
     */
    private int convertToMilliSeconds( int timeoutInSeconds ) {
        return timeoutInSeconds * 1000;
    }
}