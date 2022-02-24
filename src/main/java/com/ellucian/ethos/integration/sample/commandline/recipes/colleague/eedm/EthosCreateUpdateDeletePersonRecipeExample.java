/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.sample.commandline.recipes.colleague.eedm;


import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.proxy.EthosProxyClient;

public class EthosCreateUpdateDeletePersonRecipeExample {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private String apiKey;

    public EthosCreateUpdateDeletePersonRecipeExample( String apiKey ) {
        this.apiKey = apiKey;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    public static void main( String[] args ) {
        if( args == null || args.length == 0 ) {
            System.out.println( "Please enter an API key as a program argument when running this example class.  An API key is required to run this example.");
            return;
        }
        String apiKey = args[ 0 ];

    }

    private EthosProxyClient getEthosProxyClient() {
        return new EthosClientBuilder(apiKey).buildEthosProxyClient();
    }
}
