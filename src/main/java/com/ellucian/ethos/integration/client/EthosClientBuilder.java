/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client;


import com.ellucian.ethos.integration.client.config.EthosConfigurationClient;
import com.ellucian.ethos.integration.client.errors.EthosErrorsClient;
import com.ellucian.ethos.integration.client.messages.EthosMessagesClient;
import com.ellucian.ethos.integration.client.proxy.EthosFilterQueryClient;
import com.ellucian.ethos.integration.client.proxy.EthosProxyClient;
import com.ellucian.ethos.integration.client.proxy.EthosProxyClientAsync;

/**
 * Builder used for building Ethos clients.  This is the primary means of building the desired Ethos client class.
 * This class supports building clients specified by the various client types, using a builder pattern.
 */
public class EthosClientBuilder {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /**
     * The API key used to build the AccessToken within the given EthosClient.
     */
    private String apiKey;

    /**
     * The timeout <b>in seconds</b> for a connection to be established.
     */
    private Integer connectionTimeout;

    /**
     * The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager.
     */
    private Integer connectionRequestTimeout;

    /**
     * The timeout <b>in seconds</b> when waiting for data during a period of inactivity between consecutive data packets.
     */
    private Integer socketTimeout;

    /**
     * Constructs this class with the given apiKey.
     * @param apiKey The API key used to build the access token for the desired Ethos client.
     */
    public EthosClientBuilder( String apiKey ) {
        this.apiKey = apiKey;
        this.connectionTimeout = null;
        this.connectionRequestTimeout = null;
        this.socketTimeout = null;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Sets the connection timeout value to be used.  Can be a null value.
     * @param connectionTimeout The connection timeout value to use, can be null.
     * @return This builder with the connection timeout value set.
     */
    public EthosClientBuilder withConnectionTimeout( Integer connectionTimeout ) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    /**
     * Sets the connection request timeout value to be used.  Can be a null value.
     * @param connectionRequestTimeout The connection request timeout value to use, can be null.
     * @return This builder with the connection request timeout value set.
     */
    public EthosClientBuilder withConnectionRequestTimeout( Integer connectionRequestTimeout ) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    /**
     * Sets the socket timeout value to be used.  Can be a null value.
     * @param socketTimeout The socket timeout value to use, can be null.
     * @return This builder with the socket timeout value set.
     */
    public EthosClientBuilder withSocketTimeout( Integer socketTimeout ) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    /**
     * Builds an {@link EthosErrorsClient EthosErrorsClient} that will use the given API key to authenticate, and the specified
     * timeout values to connect with.
     * @return an EthosErrorsClient using the given apiKey and timeout values.
     */
    public EthosErrorsClient buildEthosErrorsClient() {
        return new EthosErrorsClient( this.apiKey, this.connectionTimeout, this.connectionRequestTimeout, this.socketTimeout );
    }

    /**
     * Builds an {@link EthosConfigurationClient EthosConfigurationClient} that will use the given API key to authenticate, and the specified
     * timeout values to connect with.
     * @return an EthosConfigurationClient using the given apiKey and timeout values.
     */
    public EthosConfigurationClient buildEthosConfigurationClient() {
        return new EthosConfigurationClient( this.apiKey, this.connectionTimeout, this.connectionRequestTimeout, this.socketTimeout );
    }

    /**
     * Builds an {@link EthosProxyClient EthosProxyClient} that will use the given API key to authenticate, and the specified
     * timeout values to connect with.
     * @return an EthosProxyClient using the given apiKey and timeout values.
     */
    public EthosProxyClient buildEthosProxyClient() {
        return new EthosProxyClient( this.apiKey, this.connectionTimeout, this.connectionRequestTimeout, this.socketTimeout );
    }

    /**
     * Builds an {@link EthosProxyClientAsync EthosProxyClientAsync} that will use the given API key to authenticate, and the specified
     * timeout values to connect with.   This client can perform all of the operations that {@link EthosProxyClient EthosProxyClient} can.
     * Additionally, it can also perform paging operations wrapped in {@link java.util.concurrent.CompletableFuture} CompletableFutures for
     * asynchronous paging.
     * @return an EthosProxyClient using the given apiKey and timeout values.
     */
    public EthosProxyClientAsync buildEthosProxyAsyncClient() {
        return new EthosProxyClientAsync( this.apiKey, this.connectionTimeout, this.connectionRequestTimeout, this.socketTimeout );
    }

    /**
     * Builds an {@link EthosMessagesClient EthosMessagesClient} that will use the given API key to authenticate, and the specified timeout values to
     * connect with.
     * @return an EthosMessagesClient using the given apiKey and timeout values.
     */
    public EthosMessagesClient buildEthosMessagesClient() {
        return new EthosMessagesClient( this.apiKey, this.connectionTimeout, this.connectionRequestTimeout, this.socketTimeout );
    }

    /**
     * Builds an {@link EthosFilterQueryClient EthosFilterQueryClient} that will use the given API key to authenticate, and the specified timeout values to
     * connect with.
     * @return an EthosFilterQueryClient using the given apiKey and timeout values.
     */
    public EthosFilterQueryClient buildEthosFilterQueryClient() {
        return new EthosFilterQueryClient( this.apiKey, this.connectionTimeout, this.connectionRequestTimeout, this.socketTimeout );
    }

}