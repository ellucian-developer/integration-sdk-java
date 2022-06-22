/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client;


import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class HttpProtocolClientBuilderTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private HttpProtocolClientBuilder spyHttpProtocolClientBuilder;
    // ==========================================================================
    // Methods
    // ==========================================================================

    @Test
    void buildHttpClientTest() {
        spyHttpProtocolClientBuilder = new HttpProtocolClientBuilder( null, null, null );
        HttpClient httpClient = spyHttpProtocolClientBuilder.buildHttpClient();
        assertTrue( httpClient != null );
        assertTrue( spyHttpProtocolClientBuilder.defaultHeaders != null );
        assertTrue( spyHttpProtocolClientBuilder.defaultHeaders.size() == 3 );
        assertTrue( spyHttpProtocolClientBuilder.sslConnectionSocketFactory != null );
        assertTrue( spyHttpProtocolClientBuilder.requestConfigBuilder != null );
    }

    @Test
    public void initHttpClientTest() {
        spyHttpProtocolClientBuilder = spy( new HttpProtocolClientBuilder(null, null, null) );
        // Test the method, which is actually run during the constructor, but is tested separately here after establishing the Mockito spy object for verification.
        spyHttpProtocolClientBuilder.doInitialization( null, null, null );
        // Verify the mocked init() method was called 1 time with the expected params.
        Mockito.verify(spyHttpProtocolClientBuilder, Mockito.times(1)).init(HttpProtocolClientBuilder.DEFAULT_CONNECTION_TIMEOUT,
                                                                                                    HttpProtocolClientBuilder.DEFAULT_CONNECTION_REQUEST_TIMEOUT,
                                                                                                    HttpProtocolClientBuilder.DEFAULT_SOCKET_TIMEOUT);
        // Run again with different input params.
        spyHttpProtocolClientBuilder.doInitialization( 20, null, null );
        // Verify the mocked init() method was called 1 time with the expected params.
        Mockito.verify(spyHttpProtocolClientBuilder, Mockito.times(1)).init(20000,
                                                                                                    HttpProtocolClientBuilder.DEFAULT_CONNECTION_REQUEST_TIMEOUT,
                                                                                                    HttpProtocolClientBuilder.DEFAULT_SOCKET_TIMEOUT);
        // Run again with different input params.
        spyHttpProtocolClientBuilder.doInitialization( null, 30, null );
        // Verify the mocked init() method was called 1 time with the expected params.
        Mockito.verify(spyHttpProtocolClientBuilder, Mockito.times(1)).init(HttpProtocolClientBuilder.DEFAULT_CONNECTION_TIMEOUT,
                                                                                                    30000,
                                                                                                    HttpProtocolClientBuilder.DEFAULT_SOCKET_TIMEOUT);
        // Run again with different input params.
        spyHttpProtocolClientBuilder.doInitialization( null, null, 10 );
        // Verify the mocked init() method was called 1 time with the expected params.
        Mockito.verify(spyHttpProtocolClientBuilder, Mockito.times(1)).init(HttpProtocolClientBuilder.DEFAULT_CONNECTION_TIMEOUT,
                                                                                                    HttpProtocolClientBuilder.DEFAULT_CONNECTION_REQUEST_TIMEOUT,
                                                                                                    10000);
        // Run again with different input params.
        spyHttpProtocolClientBuilder.doInitialization( 20, 30, null );
        // Verify the mocked init() method was called 1 time with the expected params.
        Mockito.verify(spyHttpProtocolClientBuilder, Mockito.times(1)).init(20000,
                                                                                                    30000,
                                                                                                    HttpProtocolClientBuilder.DEFAULT_SOCKET_TIMEOUT);
        // Run again with different input params.
        spyHttpProtocolClientBuilder.doInitialization( null, 30, 10 );
        // Verify the mocked init() method was called 1 time with the expected params.
        Mockito.verify(spyHttpProtocolClientBuilder, Mockito.times(1)).init(HttpProtocolClientBuilder.DEFAULT_CONNECTION_TIMEOUT,
                                                                                                    30000,
                                                                                                    10000);
        // Run again with different input params.
        spyHttpProtocolClientBuilder.doInitialization( 20, null, 10 );
        // Verify the mocked init() method was called 1 time with the expected params.
        Mockito.verify(spyHttpProtocolClientBuilder, Mockito.times(1)).init(20000,
                                                                                                    HttpProtocolClientBuilder.DEFAULT_CONNECTION_REQUEST_TIMEOUT,
                                                                                                    10000);
        // Run again with different input params.
        spyHttpProtocolClientBuilder.doInitialization( 20, 30, 10 );
        // Verify the mocked init() method was called 1 time with the expected params.
        Mockito.verify(spyHttpProtocolClientBuilder, Mockito.times(1)).init(20000,
                                                                                                    30000,
                                                                                                    10000);
    }
}