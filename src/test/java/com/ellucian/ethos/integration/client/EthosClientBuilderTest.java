/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client;


import com.ellucian.ethos.integration.client.config.EthosConfigurationClient;
import com.ellucian.ethos.integration.client.errors.EthosErrorsClient;
import com.ellucian.ethos.integration.client.proxy.EthosProxyClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EthosClientBuilderTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private EthosClientBuilder ethosClientBuilder;

    // ==========================================================================
    // Methods
    // ==========================================================================

    @Test
    void getErrorsClientThrowsExceptionWhenNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EthosClientBuilder(null).buildEthosErrorsClient();
        });
    }

    @Test
    void getErrorsClientTest() {
        EthosErrorsClient ethosErrorsClient = new EthosClientBuilder("11111111-1111-1111-1111-111111111111").buildEthosErrorsClient();
        assertNotNull(ethosErrorsClient);
    }

    @Test
    void getProxyClientThrowsExceptionWhenNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EthosClientBuilder(null).buildEthosProxyClient();
        });
    }

    @Test
    void getProxyClientTest() {
        EthosProxyClient ethosProxyClient = new EthosClientBuilder("11111111-1111-1111-1111-111111111111").buildEthosProxyClient();
        assertNotNull(ethosProxyClient);
    }

    @Test
    void getConfigurationClientThrowsExceptionWhenNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EthosClientBuilder(null).buildEthosConfigurationClient();
        });
    }

    @Test
    void getConfigurationClientTest() {
        EthosConfigurationClient ethosConfigurationClient = new EthosClientBuilder("11111111-1111-1111-1111-111111111111").buildEthosConfigurationClient();
        assertNotNull(ethosConfigurationClient);
    }

}