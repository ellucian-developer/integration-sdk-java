/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.sample.commandline.recipes.colleague.eedm;


import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.EthosResponseConverter;
import com.ellucian.ethos.integration.client.proxy.EthosFilterQueryClient;
import com.ellucian.ethos.integration.client.proxy.filter.CriteriaFilter;
import com.ellucian.ethos.integration.client.proxy.filter.SimpleCriteria;
import com.fasterxml.jackson.databind.JsonNode;

public class A2PersonsCombinationFilterQuery {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private EthosClientBuilder ethosClientBuilder;

    public A2PersonsCombinationFilterQuery( EthosClientBuilder ethosClientBuilder ) {
        this.ethosClientBuilder = ethosClientBuilder;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    public JsonNode getPersonByLastAndFirstNameQuery( String lastName, String firstName ) throws Exception {
        SimpleCriteria.Builder scBuilder = new SimpleCriteria.Builder();
        CriteriaFilter cf = scBuilder.withMultiCriteriaObjectArray("names")
                .addMultiCriteriaObject(scBuilder.withMultiCriteriaObjectForArray("lastName", lastName).addSimpleCriteria("firstName", firstName))
                .buildCriteriaFilter();
        // The EthosClientBuilder is built as follows in the calling class using this service, and is used to build an EthosFilterQueryClient.
        // new EthosClientBuilder(apiKey).buildEthosProxyClient();
        EthosFilterQueryClient ethosFilterQueryClient = ethosClientBuilder.buildEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        EthosResponse ethosResponse = ethosFilterQueryClient.getWithCriteriaFilter( "persons", cf );
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }
}
