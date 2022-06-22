/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client;


import com.ellucian.ethos.integration.client.proxy.EthosProxyClient;
import org.apache.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EthosResponseBuilderTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private EthosResponseBuilder ethosResponseBuilder;

    // ==========================================================================
    // Methods
    // ==========================================================================


    @BeforeEach
    void setup() {
        ethosResponseBuilder = new EthosResponseBuilder();
    }

    @Test
    void buildEthosResponseReturnsNullForNullInputTest() throws IOException {
        EthosResponse ethosResponse = ethosResponseBuilder.buildEthosResponse( null );
        assertTrue( ethosResponse == null);
    }

    @Test
    void buildEthosResponseTest() throws IOException {
        HttpResponse mockHttpResponse = Mockito.mock( HttpResponse.class );
        HttpEntity mockHttpEntity = Mockito.mock( HttpEntity.class );
        StatusLine mockStatusLine = Mockito.mock( StatusLine.class );

        Header header1 = new Header() {
            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }

            @Override
            public String getName() {
                return EthosProxyClient.HDR_X_MEDIA_TYPE;
            }

            @Override
            public String getValue() {
                return "100";
            }
        };
        Mockito.doReturn(new Header[]{header1}).when(mockHttpResponse).getAllHeaders();
        Mockito.doReturn(mockHttpEntity).when(mockHttpResponse).getEntity();
        Mockito.doReturn(mockStatusLine).when(mockHttpResponse).getStatusLine();
        Mockito.doReturn(200).when(mockStatusLine).getStatusCode();
        // Run the test.
        EthosResponse ethosResponse = ethosResponseBuilder.buildEthosResponse( mockHttpResponse );
        assertTrue( ethosResponse != null );
        assertTrue( ethosResponse.getHeader(EthosProxyClient.HDR_X_MEDIA_TYPE) == header1 );
        assertTrue( ethosResponse.getHttpStatusCode() == 200 );
    }
}