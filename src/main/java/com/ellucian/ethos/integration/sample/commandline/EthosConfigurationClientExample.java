/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.sample.commandline;

import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.config.EthosConfigurationClient;
import com.ellucian.ethos.integration.client.config.UnsupportedVersionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EthosConfigurationClientExample {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    private String apiKey;

    public EthosConfigurationClientExample( String apiKey ) {
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
        try {
            EthosConfigurationClientExample ethosConfigurationClientExample = new EthosConfigurationClientExample(apiKey);
            ethosConfigurationClientExample.filterByOwnerOverrides();
            ethosConfigurationClientExample.getResourceDetailsAsJson();
            ethosConfigurationClientExample.getMajorFullVersions();
            ethosConfigurationClientExample.getSupportedVersion();
            ethosConfigurationClientExample.isVersionSupported();
            ethosConfigurationClientExample.getVersionHeadersFromOwnerOverrides();
            ethosConfigurationClientExample.getVersionsFromOwnerOverrides();
            ethosConfigurationClientExample.getVersionsOfResource();
            ethosConfigurationClientExample.getVersionsHeadersOfResource();
            ethosConfigurationClientExample.getVersionHeader();
            ethosConfigurationClientExample.getVersionsOfResourceAsStrings();
            ethosConfigurationClientExample.getVersionHeadersOfResourceAsStrings();
            ethosConfigurationClientExample.getLatestVersionOfResource();
            ethosConfigurationClientExample.getFiltersAndNamedQueries();
            ethosConfigurationClientExample.getFiltersForResource();
            ethosConfigurationClientExample.getNamedQueriesForResource();
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    private EthosConfigurationClient getEthosConfigurationClient() {
        EthosConfigurationClient ethosConfigurationClient = new EthosClientBuilder( this.apiKey )
                                                                  .withConnectionTimeout(30)
                                                                  .withConnectionRequestTimeout(30)
                                                                  .withSocketTimeout(30)
                                                                  .buildEthosConfigurationClient();
        return ethosConfigurationClient;
    }


    public void filterByOwnerOverrides() throws IOException {
        System.out.println( "******* ethosErrorClient.getAvailableResourcesForAppAsJson() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        ArrayNode arrayNode = ethosConfigurationClient.getAvailableResourcesForAppAsJson();
        Iterator<JsonNode> nodeIterator = arrayNode.iterator();
        while( nodeIterator.hasNext() ) {
            JsonNode jsonNode = nodeIterator.next();
            System.out.println( jsonNode.toPrettyString() );
        }
    }


    public void getResourceDetailsAsJson() throws IOException {
        System.out.println( "******* ethosErrorClient.getResourceDetailsAsJson() *******" );
        String resourceName = "persons";
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        ArrayNode arrayNode = ethosConfigurationClient.getResourceDetailsAsJson( resourceName );
        Iterator<JsonNode> nodeIterator = arrayNode.iterator();
        while( nodeIterator.hasNext() ) {
            JsonNode jsonNode = nodeIterator.next();
            System.out.println( jsonNode.toPrettyString() );
        }
    }


    public void getMajorFullVersions() throws IOException {
        System.out.println( "******* ethosErrorClient.getMajorVersionsOfResource() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        List<String> filteredVersionList = ethosConfigurationClient.getMajorVersionsOfResource("general-ledger-transactions");
        System.out.println( "FILTERED MAJOR FULL VERSION LIST: " + filteredVersionList.toString() );
    }


    public void getSupportedVersion() throws IOException {
        System.out.println( "******* ethosErrorClient.getVersionHeader() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "general-ledger-transactions";
        String fullVersion = "";
        try {
            fullVersion = ethosConfigurationClient.getVersionHeader(resourceName, 3);
        }
        catch( UnsupportedVersionException uve ) {
            System.out.println( uve.getMessage() );
            System.out.println( String.format("RESOURCE NAME: %s, UNSUPPORTED VERSION: %s", uve.getResourceName(), uve.getUnsupportedVersion()) );
            uve.printStackTrace();
            if( ethosConfigurationClient.isResourceVersionSupported(resourceName, 8) ) {
                fullVersion = ethosConfigurationClient.getVersionHeader( resourceName, 8 );
            }
        }
        System.out.println( String.format("RESOURCE: %s, REQUESTED FULL VERSION: %s", resourceName, fullVersion) );
    }


    public void isVersionSupported() throws IOException {
        System.out.println( "******* ethosErrorClient.isResourceVersionSupported() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "general-ledger-transactions";
        boolean isSupported = ethosConfigurationClient.isResourceVersionSupported(resourceName, 3);
        System.out.println( String.format("RESOURCE: %s, VERSION: %s, IS SUPPORTED? %s", resourceName, "3", isSupported) );
        if( ! isSupported ) {
            isSupported = ethosConfigurationClient.isResourceVersionSupported( resourceName, 12, 1 );
            System.out.println( String.format("RESOURCE: %s, VERSION: %s, IS SUPPORTED? %s", resourceName, "12.1", isSupported) );
            if( ! isSupported ) {
                isSupported = ethosConfigurationClient.isResourceVersionSupported( resourceName, 12, 1, 0 );
                System.out.println( String.format("RESOURCE: %s, VERSION: %s, IS SUPPORTED? %s", resourceName, "12.1.0", isSupported) );
            }
        }
    }

    public void getVersionHeader() throws IOException {
        System.out.println( "******* ethosErrorClient.getVersionHeader() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "general-ledger-transactions";
        String versionHeader = null;
        try {
            versionHeader = ethosConfigurationClient.getVersionHeader( resourceName, 12, 2 );
        }
        catch( UnsupportedVersionException uve ) {
            System.out.println( String.format("Unsupported version %s for resource %s", uve.getUnsupportedVersion(), uve.getResourceName()) );
            uve.printStackTrace();
            versionHeader = ethosConfigurationClient.getVersionHeader( resourceName, 12, 1, 0 );
            System.out.println( String.format("VERSION HEADER: %s", versionHeader) );
        }
    }

    public void getVersionHeadersFromOwnerOverrides() throws IOException {
        System.out.println( "******* ethosErrorClient.getVersionHeadersForApp() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "account-funds-available";
        List<String> versionHeaderList = ethosConfigurationClient.getVersionHeadersForApp( resourceName );
        for( String versionHeader : versionHeaderList ) {
            System.out.println( String.format("RESOURCE: %s, VERSION HEADER: %s", resourceName, versionHeader) );
        }
    }

    public void getVersionsFromOwnerOverrides() throws IOException {
        System.out.println( "******* ethosErrorClient.getVersionsForApp() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "account-funds-available";
        List<String> versionList = ethosConfigurationClient.getVersionsForApp( resourceName );
        for( String version : versionList ) {
            System.out.println( String.format("RESOURCE: %s, VERSION: %s", resourceName, version) );
        }
    }

    public void getVersionsOfResource() throws IOException {
        System.out.println( "******* ethosErrorClient.getVersionsOfResource() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "general-ledger-transactions";
        ArrayNode arrayNode = ethosConfigurationClient.getVersionsOfResource( resourceName );
        System.out.println( arrayNode.toPrettyString() );
        Iterator<JsonNode> appResourceNodeIter = arrayNode.iterator();
        while( appResourceNodeIter.hasNext() ) {
            JsonNode appResourceNode = appResourceNodeIter.next();
            System.out.println( String.format("APP ID: %s, APP NAME: %s, RESOURCE NAME: %s",
                                appResourceNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPID).asText(),
                                appResourceNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPNAME).asText(),
                                appResourceNode.at(EthosConfigurationClient.JSON_ACCESSOR_RESOURCENAME).asText()) );
            System.out.println( String.format("VERSIONS: %s", appResourceNode.at("/versions").toString()) );
            Iterator<JsonNode> versionNodeIter = appResourceNode.at("/versions").iterator();
            while( versionNodeIter.hasNext() ) {
                JsonNode versionNode = versionNodeIter.next();
                System.out.println( String.format("VERSION: %s", versionNode.asText()) );
            }
        }
    }

    public void getVersionsHeadersOfResource() throws IOException {
        System.out.println( "******* ethosErrorClient.getVersionHeadersOfResource() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "general-ledger-transactions";
        ArrayNode arrayNode = ethosConfigurationClient.getVersionHeadersOfResource( resourceName );
        System.out.println( arrayNode.toPrettyString() );
        Iterator<JsonNode> appResourceNodeIter = arrayNode.iterator();
        while( appResourceNodeIter.hasNext() ) {
            JsonNode appResourceNode = appResourceNodeIter.next();
            System.out.println( String.format("APP ID: %s, APP NAME: %s, RESOURCE NAME: %s",
                                appResourceNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPID).asText(),
                                appResourceNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPNAME).asText(),
                                appResourceNode.at(EthosConfigurationClient.JSON_ACCESSOR_RESOURCENAME).asText()) );
            System.out.println( String.format("VERSIONS: %s", appResourceNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).toString()) );
            Iterator<JsonNode> versionNodeIter = appResourceNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).iterator();
            while( versionNodeIter.hasNext() ) {
                JsonNode versionNode = versionNodeIter.next();
                System.out.println( String.format("VERSION: %s", versionNode.asText()) );
            }
        }
    }

    public void getVersionsOfResourceAsStrings() throws IOException {
        System.out.println( "******* ethosErrorClient.getVersionsOfResourceAsStrings() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "persons";
        List<String> versionList = ethosConfigurationClient.getVersionsOfResourceAsStrings( resourceName );
        System.out.println( String.format("VERSION LIST: %s", versionList.toString()) );
    }

    public void getVersionHeadersOfResourceAsStrings() throws IOException {
        System.out.println( "******* ethosErrorClient.getVersionHeadersOfResourceAsStrings() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "persons";
        List<String> versionHeaderList = ethosConfigurationClient.getVersionHeadersOfResourceAsStrings( resourceName );
        System.out.println( String.format("VERSION HEADER LIST: %s", versionHeaderList.toString()) );
    }

    public void getLatestVersionOfResource() throws IOException {
        System.out.println( "******* ethosErrorClient.getLatestVersion() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "persons";
        String latestVersion = ethosConfigurationClient.getLatestVersion( resourceName );
        System.out.println( String.format("LATEST VERSION FOR RESOURCE %s is: %s", resourceName, latestVersion) );
    }

    public void getFiltersAndNamedQueries() throws IOException {
        System.out.println( "******* ethosErrorClient.getFiltersAndNamedQueries() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "persons";
        String versionHeader = "application/vnd.hedtech.integration.v12+json";
        JsonNode resourceFiltersNode = ethosConfigurationClient.getFiltersAndNamedQueries( resourceName, versionHeader );
        System.out.println( resourceFiltersNode.toPrettyString() );
        JsonNode filtersNode = resourceFiltersNode.at( EthosConfigurationClient.JSON_ACCESSOR_FILTERS );
        if( filtersNode.isMissingNode() ) {
            System.out.println( "NO FILTERS FOUND." );
        }
        else {
            System.out.println( "FILTERS:");
            Iterator<JsonNode> filtersIter = filtersNode.iterator();
            while( filtersIter.hasNext() ) {
                JsonNode filterNode = filtersIter.next();
                System.out.println( String.format("    FILTER: %s", filterNode.asText()) );
            }
        }
        JsonNode namedQueriesNode = resourceFiltersNode.at( EthosConfigurationClient.JSON_ACCESSOR_NAMEDQUERIES );
        if( namedQueriesNode.isMissingNode() ) {
            System.out.println( "NO NAMED QUERIES FOUND." );
        }
        else {
            System.out.println( "NAMED QUERIES:");
            Iterator<JsonNode> namedQueriesIter = namedQueriesNode.iterator();
            while( namedQueriesIter.hasNext() ) {
                JsonNode namedQueryNode = namedQueriesIter.next();
                System.out.println( String.format("    NAMED QUERY NAME: %s", namedQueryNode.at(EthosConfigurationClient.JSON_ACCESSOR_NAME).asText()) );
                Iterator<JsonNode> namedQueryFiltersIter = namedQueryNode.iterator();
                while( namedQueryFiltersIter.hasNext() ) {
                    JsonNode namedQueryFiltersNode = namedQueryFiltersIter.next();
                    Iterator<JsonNode> filtersIter = namedQueryFiltersNode.iterator();
                    while( filtersIter.hasNext() ) {
                        JsonNode filterNode = filtersIter.next();
                        System.out.println( String.format("    NAMED QUERY FILTERS: %s", filterNode.asText()) );
                    }
                }
            }
        }
    }

    public void getFiltersForResource() throws IOException {
        System.out.println( "******* ethosErrorClient.getFilters() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "persons";
        List<String> filterList = ethosConfigurationClient.getFilters( resourceName );
        System.out.println( String.format("FILTERS FOR RESOURCE %s are: %s", resourceName, filterList.toString()) );
    }

    public void getNamedQueriesForResource() throws IOException {
        System.out.println( "******* ethosErrorClient.getNamedQueries() *******" );
        EthosConfigurationClient ethosConfigurationClient = getEthosConfigurationClient();
        String resourceName = "persons";
        Map<String,List<String>> namedQueriesMap = ethosConfigurationClient.getNamedQueries( resourceName );
        System.out.println( String.format("NAMED QUERIES FOR RESOURCE %s are: %s", resourceName, namedQueriesMap.toString()) );
        Set<String> keys = namedQueriesMap.keySet();
        for( String namedQueryName: keys ) {
            List<String> filterList = namedQueriesMap.get( namedQueryName );
            for( String filter: filterList ) {
                System.out.println( String.format("NAME: %s, FILTER: %s", namedQueryName, filter) );
            }
        }
    }

}