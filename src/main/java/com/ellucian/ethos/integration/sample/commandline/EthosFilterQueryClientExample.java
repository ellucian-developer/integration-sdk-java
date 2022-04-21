/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.sample.commandline;


import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.EthosResponseConverter;
import com.ellucian.ethos.integration.client.proxy.EthosFilterQueryClient;
import com.ellucian.ethos.integration.client.proxy.filter.CriteriaFilter;
import com.ellucian.ethos.integration.client.proxy.filter.FilterMap;
import com.ellucian.ethos.integration.client.proxy.filter.NamedQueryFilter;
import com.ellucian.ethos.integration.client.proxy.filter.SimpleCriteria;

import java.io.IOException;
import java.util.List;

public class EthosFilterQueryClientExample {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private String apiKey;

    public EthosFilterQueryClientExample( String apiKey ) {
        this.apiKey = apiKey;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    public static void main( String[] args ) {
        if( args == null || args.length == 0 ) {
            System.out.println( "Please enter an API key as a program argument to run this sample program." );
            return;
        }
        String apiKey = args[ 0 ];
        EthosFilterQueryClientExample ethosFilterQueryClientExample = new EthosFilterQueryClientExample( apiKey );
//        ethosFilterQueryClientExample.getUsingCriteriaFilterString();
//        ethosFilterQueryClientExample.getUsingCriteriaFilter();
//        ethosFilterQueryClientExample.getUsingNamedQueryFilter();
//        ethosFilterQueryClientExample.getWithSimpleCriteriaArrayFilter();
//        ethosFilterQueryClientExample.getUsingFilterMap();
//        ethosFilterQueryClientExample.getPagesUsingCriteriaFilter();
//        ethosFilterQueryClientExample.getPagesFromOffsetUsingCriteriaFilter();
//        ethosFilterQueryClientExample.getPagesUsingNamedQueryFilter();
//        ethosFilterQueryClientExample.getPagesUsingFilterMapValues();
//        ethosFilterQueryClientExample.getPagesFromOffsetUsingFilterMapValues();
        ethosFilterQueryClientExample.getAccountCodesWithCriteriaFilter();
    }

    public EthosFilterQueryClient getEthosFilterQueryClient() {
        return new EthosClientBuilder(apiKey)
                   .withConnectionTimeout(60)
                   .withConnectionRequestTimeout(60)
                   .withSocketTimeout(60)
                   .buildEthosFilterQueryClient();
    }

    public void getUsingCriteriaFilterString() {
        System.out.println( "******* getWithCriteriaFilter() using filter string *******" );
        String resource = "persons";
        String version = "application/vnd.hedtech.integration.v12.1.0+json";
        String criteriaFilterStr = "?criteria={\"names\":[{\"firstName\":\"John\",\"lastName\":\"Smith\"}]}";
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            EthosResponse ethosResponse = ethosFilterQueryClient.getWithCriteriaFilter(resource, version, criteriaFilterStr);
            System.out.println( "REQUESTED URL: " + ethosResponse.getRequestedUrl() );
            System.out.println( "Number of resources returned: " + ethosResponseConverter.toJsonNode(ethosResponse).size() );
            System.out.println( ethosResponseConverter.toJsonNode(ethosResponse).toPrettyString() );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getWithSimpleCriteriaArrayFilter() {
        System.out.println( "******* getWithSimpleCriteriaArrayFilter() using values *******" );
        String resource = "persons";
        String criteriaLabel = "names";
        String criteriaKey = "firstName";
        String criteriaValue = "John";
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            EthosResponse ethosResponse = ethosFilterQueryClient.getWithSimpleCriteriaArrayValues( resource, criteriaLabel, criteriaKey, criteriaValue );
            System.out.println( "REQUESTED URL: " + ethosResponse.getRequestedUrl() );
            System.out.println( "Number of resources returned: " + ethosResponseConverter.toJsonNode(ethosResponse).size() );
            System.out.println( ethosResponseConverter.toJsonNode(ethosResponse).toPrettyString() );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getUsingCriteriaFilter() {
        System.out.println( "******* getWithCriteriaFilter() using CriteriaFilter *******" );
        String resource = "persons";
        // Build a SimpleCriteriaArray filter because the persons resource supports that filter syntax.
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteriaArray("names", "firstName", "John")
                                        .buildCriteriaFilter();
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            EthosResponse ethosResponse = ethosFilterQueryClient.getWithCriteriaFilter( resource, criteriaFilter );
            System.out.println( "REQUESTED URL: " + ethosResponse.getRequestedUrl() );
            System.out.println( "Number of resources returned: " + ethosResponseConverter.toJsonNode(ethosResponse).size() );
            System.out.println( ethosResponseConverter.toJsonNode(ethosResponse).toPrettyString() );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getUsingNamedQueryFilter() {
        System.out.println( "******* getWithNamedQueryFilter() using NamedQueryFilter *******" );
        String resource = "sections";
        String queryName = "keywordSearch";
        String queryKey = "keywordSearch";
        String queryValue = "Culture";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery( queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            EthosResponse ethosResponse = ethosFilterQueryClient.getWithNamedQueryFilter( resource, namedQueryFilter );
            System.out.println( "REQUESTED URL: " + ethosResponse.getRequestedUrl() );
            System.out.println( "Number of resources returned: " + ethosResponseConverter.toJsonNode(ethosResponse).size() );
            System.out.println( ethosResponseConverter.toJsonNode(ethosResponse).toPrettyString() );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getUsingFilterMap() {
        System.out.println( "******* getWithFilterMap() using filterMap *******" );
        String resource = "persons";
        String version = "application/vnd.hedtech.integration.v6+json";
        String filterKey = "firstName";
        String filterValue = "John";
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            FilterMap filterMap = new FilterMap.Builder()
                                      .withParameterPair(filterKey, filterValue)
                                      .withParameterPair("lastName", "Smith")
                                      .build();
            EthosResponse ethosResponse = ethosFilterQueryClient.getWithFilterMap( resource, version, filterMap );
            System.out.println( "REQUESTED URL: " + ethosResponse.getRequestedUrl() );
            System.out.println( "Number of resources returned: " + ethosResponseConverter.toJsonNode(ethosResponse).size() );
            System.out.println( ethosResponseConverter.toJsonNode(ethosResponse).toPrettyString() );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getPagesUsingCriteriaFilter() {
        System.out.println( "******* getPagesWithCriteriaFilter() using criteria filter *******" );
        String resource = "persons";
        String criteriaLabel = "names";
        String criteriaKey = "firstName";
        String criteriaValue = "John";
        int pageSize = 50;
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                            .withSimpleCriteriaArray(criteriaLabel, criteriaKey, criteriaValue)
                                            .buildCriteriaFilter();
            List<EthosResponse> ethosResponseList = ethosFilterQueryClient.getPagesWithCriteriaFilter( resource, criteriaFilter, pageSize );
            System.out.println( "Number of pages returned: " + ethosResponseList.size() );
            for( EthosResponse ethosResponse : ethosResponseList ) {
                System.out.println( "REQUESTED URL: " + ethosResponse.getRequestedUrl() );
                System.out.println( "PAGE SIZE: " + ethosResponseConverter.toJsonNode(ethosResponse).size() );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getPagesFromOffsetUsingCriteriaFilter() {
        System.out.println( "******* getPagesFromOffsetWithCriteriaFilter() using criteria filter *******" );
        String resourceName = "persons";
        String criteriaLabel = "names";
        String criteriaKey = "firstName";
        String criteriaValue = "John";
        int pageSize = 50;
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                            .withSimpleCriteriaArray(criteriaLabel, criteriaKey, criteriaValue)
                                            .buildCriteriaFilter();
            int totalCount = ethosFilterQueryClient.getTotalCount( resourceName, criteriaFilter );
            // Calculate the offset to be 95% of the totalCount to avoid paging through potentially tons of pages.
            int offset = (int)(totalCount * 0.95);
            List<EthosResponse> ethosResponseList = ethosFilterQueryClient.getPagesFromOffsetWithCriteriaFilter( resourceName, criteriaFilter, pageSize, offset );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println(String.format("Calculated offset of %s which is 95 percent of a total count of %s to avoid paging through potentially lots of pages.", offset, totalCount));
            System.out.println("To run with more paging, manually set the offset to a lower value, or reduce the percentage of the total count.");
            System.out.println( "Number of pages returned: " + ethosResponseList.size() );
            System.out.println( "OFFSET: " + offset );
            for( EthosResponse ethosResponse : ethosResponseList ) {
                System.out.println( "REQUESTED URL: " + ethosResponse.getRequestedUrl() );
                System.out.println( "PAGE SIZE: " + ethosResponseConverter.toJsonNode(ethosResponse).size() );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getPagesUsingNamedQueryFilter() {
        System.out.println( "******* getPagesUsingNamedQueryFilter() using named query filter *******" );
        String resourceName = "sections";
        String queryName = "keywordSearch";
        String queryKey = "keywordSearch";
        String queryValue = "Culture";
        int pageSize = 50;
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                                .withNamedQuery( queryName, queryKey, queryValue)
                                                .buildNamedQueryFilter();
            int totalCount = ethosFilterQueryClient.getTotalCount( resourceName, namedQueryFilter );
            // Calculate the offset to be 95% of the totalCount to avoid paging through potentially tons of pages.
            int offset = (int)(totalCount * 0.95);
            List<EthosResponse> ethosResponseList = ethosFilterQueryClient.getPagesFromOffsetWithNamedQueryFilter( resourceName, namedQueryFilter, pageSize, offset );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println(String.format("Calculated offset of %s which is 95 percent of a total count of %s to avoid paging through potentially lots of pages.", offset, totalCount));
            System.out.println("To run with more paging, manually set the offset to a lower value, or reduce the percentage of the total count.");
            System.out.println( "Number of pages returned: " + ethosResponseList.size() );
            System.out.println( "OFFSET: " + offset );
            for( EthosResponse ethosResponse : ethosResponseList ) {
                System.out.println( "REQUESTED URL: " + ethosResponse.getRequestedUrl() );
                System.out.println( "PAGE SIZE: " + ethosResponseConverter.toJsonNode(ethosResponse).size() );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getPagesUsingFilterMapValues() {
        System.out.println( "******* getPagesWithFilterMap() *******" );
        String resource = "persons";
        String version = "application/vnd.hedtech.integration.v6+json";
        String filterMapKey = "firstName";
        String filterMapValue = "John";
        int pageSize = 50;
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            FilterMap filterMap = new FilterMap.Builder()
                                      .withParameterPair(filterMapKey, filterMapValue)
                                      .build();
            List<EthosResponse> ethosResponseList = ethosFilterQueryClient.getPagesWithFilterMap( resource, version, filterMap, pageSize );
            System.out.println( "Number of pages returned: " + ethosResponseList.size() );
            for( EthosResponse ethosResponse : ethosResponseList ) {
                System.out.println( "REQUESTED URL: " + ethosResponse.getRequestedUrl() );
                System.out.println( "PAGE SIZE: " + ethosResponseConverter.toJsonNode(ethosResponse).size() );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }


    public void getPagesFromOffsetUsingFilterMapValues() {
        System.out.println( "******* getPagesFromOffsetWithFilterMap() *******" );
        String resourceName = "persons";
        String version = "application/vnd.hedtech.integration.v6+json";
        String filterMapKey = "firstName";
        String filterMapValue = "John";
        int pageSize = 50;
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            FilterMap filterMap = new FilterMap.Builder()
                    .withParameterPair(filterMapKey, filterMapValue)
                    .build();
            int totalCount = ethosFilterQueryClient.getTotalCount( resourceName, version, filterMap );
            // Calculate the offset to be 95% of the totalCount to avoid paging through potentially tons of pages.
            int offset = (int)(totalCount * 0.95);
            List<EthosResponse> ethosResponseList = ethosFilterQueryClient.getPagesFromOffsetWithFilterMap( resourceName, version, filterMap, pageSize, offset );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println(String.format("Calculated offset of %s which is 95 percent of a total count of %s to avoid paging through potentially lots of pages.", offset, totalCount));
            System.out.println("To run with more paging, manually set the offset to a lower value, or reduce the percentage of the total count.");
            System.out.println( "Number of pages returned: " + ethosResponseList.size() );
            System.out.println( "OFFSET: " + offset );
            for( EthosResponse ethosResponse : ethosResponseList ) {
                System.out.println( "REQUESTED URL: " + ethosResponse.getRequestedUrl() );
                System.out.println( "PAGE SIZE: " + ethosResponseConverter.toJsonNode(ethosResponse).size() );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    /**
     * This is an example of using a criteria filter request with multiple criteria in
     * a MultiCriteriaObject.  This criteria filter structure is used with (Banner) business API requests,
     * and not typically with Ethos API requests.
     */
    public void getAccountCodesWithCriteriaFilter() {
        System.out.println( "******* getAccountCodesWithCriteriaFilter() *******" );
        String resourceName = "account-codes";
        String version = "application/json";
        EthosFilterQueryClient ethosFilterQueryClient = getEthosFilterQueryClient();
        try {
            CriteriaFilter cf = new SimpleCriteria.Builder()
                                .withMultiCriteriaObject(null,"acctCode", "04", false)
                                .addSimpleCriteria("statusInd", "A")
                                .buildCriteriaFilter();
            EthosResponse ethosResponse = ethosFilterQueryClient.getWithCriteriaFilter( resourceName, version, cf );
            System.out.println( "REQUEST URL: " + ethosResponse.getRequestedUrl() );
            System.out.println( "RESPONSE: " + ethosResponse.getContent());
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

}