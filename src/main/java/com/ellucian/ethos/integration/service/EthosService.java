/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.service;


import com.ellucian.ethos.integration.client.EthosClientBuilder;

/**
 * Abstract base service class used by various subclasses.
 * @since 0.2.0
 * @author David Kumar
 */
public abstract class EthosService {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /**
     * The EthosClientBuilder used by the subclasses to build the Ethos clients used by this service.  All clients must use
     * the same API key and timeout values.
     */
    protected EthosClientBuilder ethosClientBuilder;

    /**
     * Constructs this service with the given API key.
     * @param apiKey The API key used by the EthosClients of this service when obtaining an access token per request.
     */
    protected EthosService( String apiKey ) {
        super();
        this.ethosClientBuilder = new EthosClientBuilder( apiKey );
    }

    /**
     * Constructs this service with the given {@link EthosClientBuilder EthosClientBuilder}.
     * @param ethosClientBuilder The EthosClientBuilder used to build the required clients that this service uses.
     */
    protected EthosService( EthosClientBuilder ethosClientBuilder ) {
        this.ethosClientBuilder = ethosClientBuilder;
    }


    // ==========================================================================
    // Methods
    // ==========================================================================

}