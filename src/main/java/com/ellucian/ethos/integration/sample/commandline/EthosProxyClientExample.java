/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.sample.commandline;


import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.EthosResponseConverter;
import com.ellucian.ethos.integration.client.proxy.EthosProxyClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EthosProxyClientExample {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private String apiKey;
    private String getByIdGUID;

    public EthosProxyClientExample(String apiKey, String guid ) {
        this.apiKey = apiKey;
        this.getByIdGUID = guid;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    public static void main(String[] args) {
        if( args == null || args.length == 0 ) {
            System.out.println( "Please enter an API key and a valid student-cohorts GUID value (in that order) as program arguments when running this example class.  " +
                                "An API key is required to run these examples.\nIf a valid GUID value for student-cohorts is not provided, this class will still run but the " +
                                "example methods that get a resource by ID will be skipped.");
            return;
        }
        String apiKey = args[ 0 ];
        String getByIdGUID = null;
        if( args.length == 2 ) {
            getByIdGUID = args[ 1 ];
        }
        EthosProxyClientExample ethosProxyClientExample = new EthosProxyClientExample( apiKey, getByIdGUID );
        ethosProxyClientExample.doGetResourceByIdExample();
        ethosProxyClientExample.doGetResourceAsStringByIdExample();
        ethosProxyClientExample.doGetResourceAsJsonNodeByIdExample();
        ethosProxyClientExample.doGetResourcePageSizeExample();
        ethosProxyClientExample.doGetResourceMaxPageSizeExample();
        ethosProxyClientExample.doGetResourceExample();
        ethosProxyClientExample.doGetResourceAsStringExample();
        ethosProxyClientExample.doGetResourceAsJsonNodeExample();
        ethosProxyClientExample.doGetResourceFromOffsetExample();
        ethosProxyClientExample.doGetResourceFromOffsetAsStringExample();
        ethosProxyClientExample.doGetResourceWithPageSizeExample();
        ethosProxyClientExample.doGetResourceWithPageSizeAsJsonNodeExample();
        /*************************************************************
        Commenting the following methods, see block comment below.
        ethosProxyClientExample.doGetAllPagesExample();
        ethosProxyClientExample.doGetAllPagesAsStringsExample();
        ethosProxyClientExample.doGetAllPagesAsJsonNodesExample();
        *************************************************************/
        ethosProxyClientExample.doGetAllPagesFromOffsetExample();
        ethosProxyClientExample.doGetAllPagesFromOffsetAsStringsExample();
        ethosProxyClientExample.doGetAllPagesFromOffsetAsJsonNodesExample();
        ethosProxyClientExample.doGetPagesExample();
        ethosProxyClientExample.doGetPagesAsStringsExample();
        ethosProxyClientExample.doGetPagesAsJsonNodesExample();
        ethosProxyClientExample.doGetPagesFromOffsetExample();
        ethosProxyClientExample.doGetPagesFromOffsetAsStringsExample();
        ethosProxyClientExample.doGetPagesFromOffsetAsJsonNodesExample();
        ethosProxyClientExample.doGetRowsExample();
        ethosProxyClientExample.doGetRowsAsStringsExample();
        ethosProxyClientExample.doGetRowsAsJsonNodesExample();
        ethosProxyClientExample.doGetRowsFromOffsetExample();
        ethosProxyClientExample.doGetRowsFromOffsetAsStringsExample();
        ethosProxyClientExample.doGetRowsFromOffsetAsJsonNodesExample();
        ethosProxyClientExample.doCRUDExample();
    }


    private EthosProxyClient getEthosProxyClient() {
        return new EthosClientBuilder(apiKey)
                   .withConnectionTimeout(30)
                   .withConnectionRequestTimeout(30)
                   .withSocketTimeout(30)
                   .buildEthosProxyClient();
    }


    public void printHeaders(EthosResponse ethosResponse) {
        System.out.println(String.format("Header: %s", ethosResponse.getHeader(EthosProxyClient.HDR_DATE)));
        System.out.println(String.format("Header: %s", ethosResponse.getHeader(EthosProxyClient.HDR_CONTENT_TYPE)));
        System.out.println(String.format("Header: %s", ethosResponse.getHeader(EthosProxyClient.HDR_X_TOTAL_COUNT)));
        System.out.println(String.format("Header: %s", ethosResponse.getHeader(EthosProxyClient.HDR_APPLICATION_CONTEXT)));
        System.out.println(String.format("Header: %s", ethosResponse.getHeader(EthosProxyClient.HDR_X_MAX_PAGE_SIZE)));
        System.out.println(String.format("Header: %s", ethosResponse.getHeader(EthosProxyClient.HDR_X_MEDIA_TYPE)));
        System.out.println(String.format("Header: %s", ethosResponse.getHeader(EthosProxyClient.HDR_HEDTECH_ETHOS_INTEGRATION_APPLICATION_ID)));
        System.out.println(String.format("Header: %s", ethosResponse.getHeader(EthosProxyClient.HDR_HEDTECH_ETHOS_INTEGRATION_APPLICATION_NAME)));
    }


    public void doGetResourceExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        EthosResponse ethosResponse = null;
        try {
            String resourceName = "student-cohorts";
            ethosResponse = ethosProxyClient.get(resourceName);
            Header totalCountHdr = ethosResponse.getHeader( EthosProxyClient.HDR_X_TOTAL_COUNT );
            System.out.println( "******* doGetResourceExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            printHeaders(ethosResponse);
            System.out.println("get() TOTAL COUNT: " + totalCountHdr.getValue());
            System.out.println("get() RESPONSE: " + ethosResponse.getContent());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetResourceAsStringExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String resourceName = "student-cohorts";
            String response = ethosProxyClient.getAsString(resourceName);
            JsonNode jsonNode = objectMapper.readTree( response );
            System.out.println( "******* doGetResourceAsStringExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println("getAsString() PAGE SIZE: " + jsonNode.size());
            System.out.println("getAsString() RESPONSE: " + response );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetResourceAsJsonNodeExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            JsonNode jsonNode = ethosProxyClient.getAsJsonNode(resourceName);
            System.out.println( "******* doGetResourceAsJsonNodeExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println("getAsJsonNode() PAGE SIZE: " + jsonNode.size());
            System.out.println("getAsJsonNode() RESPONSE: " + jsonNode.toString() );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetResourceFromOffsetExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String resourceName = "student-cohorts";
            int offset = 20;
            EthosResponse response = ethosProxyClient.getFromOffset( resourceName, offset );
            JsonNode jsonNode = objectMapper.readTree( response.getContent() );
            System.out.println( "******* doGetResourceFromOffsetExample *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println("getFromOffset() PAGE SIZE: " + jsonNode.size());
            System.out.println("getFromOffset() RESPONSE: " + response.getContent() );
            System.out.println( String.format("OFFSET: %s", offset) );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetResourceFromOffsetAsStringExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String resourceName = "student-cohorts";
            int offset = 20;
            String response = ethosProxyClient.getFromOffsetAsString( resourceName, offset );
            JsonNode jsonNode = objectMapper.readTree( response );
            System.out.println( "******* doGetResourceFromOffsetAsStringExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println("getFromOffsetAsString() PAGE SIZE: " + jsonNode.size());
            System.out.println("getFromOffsetAsString() RESPONSE: " + response );
            System.out.println( String.format("OFFSET: %s", offset) );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetResourceWithPageSizeExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String resourceName = "student-cohorts";
            int pageSize = 30;
            EthosResponse response = ethosProxyClient.getWithPageSize( resourceName, pageSize );
            JsonNode jsonNode = objectMapper.readTree( response.getContent() );
            System.out.println( "******* doGetResourceWithPageSizeExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println("getWithPageSize() PAGE SIZE: " + jsonNode.size());
            System.out.println("getWithPageSize() RESPONSE: " + response.getContent() );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetResourceWithPageSizeAsJsonNodeExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            int pageSize = 30;
            JsonNode response = ethosProxyClient.getWithPageSizeAsJsonNode( resourceName, pageSize );
            System.out.println( "******* doGetResourceWithPageSizeAsJsonNodeExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println("getWithPageSizeAsJsonNode() PAGE SIZE: " + response.size());
            System.out.println("getWithPageSizeAsJsonNode() RESPONSE: " + response.toString() );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /************************************************************************************************************
     * Commenting out the getAllPages*() methods due to the potential for a large quantity of data retrieved
     * at runtime.  But if desired, this code can be uncommented and executed as part of these example methods.
     * Be aware that these commented methods may take some time to run depending on the quantity of data,
     * since all pages are retrieved.
     ************************************************************************************************************/

//    public void doGetAllPagesExample() {
//        EthosProxyClient ethosProxyClient = getEthosProxyClient();
//        try {
//            String resourceName = "student-cohorts";
//            int pageSize = 15;
//            ObjectMapper objectMapper = new ObjectMapper();
//            List<EthosResponse> ethosResponseList = ethosProxyClient.getAllPages( resourceName, pageSize );
//            System.out.println( "******* doGetAllPagesExample() *******" );
//            System.out.println(String.format("Get data for resource: %s", resourceName));
//            for( int i = 0; i < ethosResponseList.size(); i++ ) {
//                JsonNode jsonNode = objectMapper.readTree( ethosResponseList.get(i).getContent() );
//                System.out.println( String.format("PAGE %s: %s", (i+1), ethosResponseList.get(i).getContent()) );
//                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
//                System.out.println( String.format("PAGE %s REQUESTED URL: %s ", (i+1), ethosResponseList.get(i).getRequestedUrl()) );
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }
//
//    public void doGetAllPagesAsStringsExample() {
//        EthosProxyClient ethosProxyClient = getEthosProxyClient();
//        try {
//            String resourceName = "student-cohorts";
//            int pageSize = 15;
//            ObjectMapper objectMapper = new ObjectMapper();
//            List<String> stringList = ethosProxyClient.getAllPagesAsStrings( resourceName, pageSize );
//            System.out.println( "******* doGetAllPagesAsStringsExample() *******" );
//            System.out.println(String.format("Get data for resource: %s", resourceName));
//            for( int i = 0; i < stringList.size(); i++ ) {
//                JsonNode jsonNode = objectMapper.readTree( stringList.get(i) );
//                System.out.println( String.format("PAGE %s: %s", (i+1), stringList.get(i)) );
//                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }
//
//    public void doGetAllPagesAsJsonNodesExample() {
//        EthosProxyClient ethosProxyClient = getEthosProxyClient();
//        try {
//            String resourceName = "student-cohorts";
//            int pageSize = 15;
//            List<JsonNode> jsonNodeList = ethosProxyClient.getAllPagesAsJsonNodes( resourceName, pageSize );
//            System.out.println( "******* doGetAllPagesAsJsonNodesExample() *******" );
//            System.out.println(String.format("Get data for resource: %s", resourceName));
//            for( int i = 0; i < jsonNodeList.size(); i++ ) {
//                JsonNode jsonNode = jsonNodeList.get( i );
//                System.out.println( String.format("PAGE %s: %s", (i+1), jsonNode.toString()) );
//                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }

    public void doGetAllPagesFromOffsetExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            int totalCount = ethosProxyClient.getTotalCount( resourceName );
            // Calculate the offset to be 95% of the totalCount to avoid paging through potentially tons of pages.
            int offset = (int)(totalCount * 0.95);
            ObjectMapper objectMapper = new ObjectMapper();
            List<EthosResponse> ethosResponseList = ethosProxyClient.getAllPagesFromOffset( resourceName, offset );
            System.out.println( "******* doGetAllPagesFromOffsetExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println(String.format("Calculated offset of %s which is 95 percent of a total count of %s to avoid paging through potentially lots of pages.", offset, totalCount));
            System.out.println("To run with more paging, manually set the offset to a lower value, or reduce the percentage of the total count.");
            for( int i = 0; i < ethosResponseList.size(); i++ ) {
                JsonNode jsonNode = objectMapper.readTree( ethosResponseList.get(i).getContent() );
                System.out.println( String.format("PAGE %s: %s", (i+1), ethosResponseList.get(i).getContent()) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
                System.out.println( String.format("OFFSET: %s", offset) );
                System.out.println( String.format("PAGE %s REQUESTED URL: %s ", (i+1), ethosResponseList.get(i).getRequestedUrl()) );
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetAllPagesFromOffsetAsStringsExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            int totalCount = ethosProxyClient.getTotalCount( resourceName );
            // Calculate the offset to be 95% of the totalCount to avoid paging through potentially tons of pages.
            int offset = (int)(totalCount * 0.95);
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> stringList = ethosProxyClient.getAllPagesFromOffsetAsStrings( resourceName, offset );
            System.out.println( "******* doGetAllPagesFromOffsetAsStringsExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println(String.format("Calculated offset of %s which is 95 percent of a total count of %s to avoid paging through potentially lots of pages.", offset, totalCount));
            System.out.println("To run with more paging, manually set the offset to a lower value, or reduce the percentage of the total count.");
            for( int i = 0; i < stringList.size(); i++ ) {
                JsonNode jsonNode = objectMapper.readTree( stringList.get(i) );
                System.out.println( String.format("PAGE %s: %s", (i+1), stringList.get(i)) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
                System.out.println( String.format("OFFSET: %s", offset) );
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public void doGetAllPagesFromOffsetAsJsonNodesExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            int totalCount = ethosProxyClient.getTotalCount( resourceName );
            // Calculate the offset to be 95% of the totalCount to avoid paging through potentially tons of pages.
            int offset = (int)(totalCount * 0.95);
            List<JsonNode> jsonNodeList = ethosProxyClient.getAllPagesFromOffsetAsJsonNodes( resourceName, offset );
            System.out.println( "******* doGetAllPagesFromOffsetAsJsonNodesExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println(String.format("Calculated offset of %s which is 95 percent a total count of %s to avoid paging through potentially lots of pages.", offset, totalCount));
            System.out.println("To run with more paging, manually set the offset to a lower value, or reduce the percentage of the total count.");
            for( int i = 0; i < jsonNodeList.size(); i++ ) {
                JsonNode jsonNode = jsonNodeList.get( i );
                System.out.println( String.format("PAGE %s: %s", (i+1), jsonNode.toString()) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
                System.out.println( String.format("OFFSET: %s", offset) );
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetPagesExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            int pageSize = 15;
            int numPages = 3;
            ObjectMapper objectMapper = new ObjectMapper();
            List<EthosResponse> ethosResponseList = ethosProxyClient.getPages( resourceName, pageSize, numPages );
            System.out.println( "******* doGetPagesExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            for( int i = 0; i < ethosResponseList.size(); i++ ) {
                JsonNode jsonNode = objectMapper.readTree( ethosResponseList.get(i).getContent() );
                System.out.println( String.format("PAGE %s: %s", (i+1), ethosResponseList.get(i).getContent()) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
                System.out.println( String.format("PAGE %s REQUESTED URL: %s ", (i+1), ethosResponseList.get(i).getRequestedUrl()) );
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetPagesAsStringsExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            int pageSize = 15;
            int numPages = 3;
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> stringList = ethosProxyClient.getPagesAsStrings( resourceName, pageSize, numPages );
            System.out.println( "******* doGetPagesAsStringsExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            for( int i = 0; i < stringList.size(); i++ ) {
                JsonNode jsonNode = objectMapper.readTree( stringList.get(i) );
                System.out.println( String.format("PAGE %s: %s", (i+1), stringList.get(i)) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public void doGetPagesAsJsonNodesExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            int pageSize = 15;
            int numPages = 3;
            List<JsonNode> jsonNodeList = ethosProxyClient.getPagesAsJsonNodes( resourceName, pageSize, numPages );
            System.out.println( "******* doGetPagesAsJsonNodesExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            for( int i = 0; i < jsonNodeList.size(); i++ ) {
                JsonNode jsonNode = jsonNodeList.get( i );
                System.out.println( String.format("PAGE %s: %s", (i+1), jsonNode.toString()) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetPagesFromOffsetExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            int pageSize = 15;
            int offset = 10;
            int numPages = 3;
            ObjectMapper objectMapper = new ObjectMapper();
            List<EthosResponse> ethosResponseList = ethosProxyClient.getPagesFromOffset( resourceName, pageSize, offset, numPages );
            System.out.println( "******* doGetPagesFromOffsetExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println( String.format("OFFSET: %s", offset) );
            for( int i = 0; i < ethosResponseList.size(); i++ ) {
                JsonNode jsonNode = objectMapper.readTree( ethosResponseList.get(i).getContent() );
                System.out.println( String.format("PAGE %s: %s", (i+1), ethosResponseList.get(i).getContent()) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
                System.out.println( String.format("PAGE %s REQUESTED URL: %s ", (i+1), ethosResponseList.get(i).getRequestedUrl()) );
            }
            System.out.println( String.format("NUM PAGES: %s", ethosResponseList.size()) );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetPagesFromOffsetAsStringsExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            int pageSize = 15;
            int offset = 10;
            int numPages = 3;
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> stringList = ethosProxyClient.getPagesFromOffsetAsStrings( resourceName, pageSize, offset, numPages );
            System.out.println( "******* doGetPagesFromOffsetAsStringsExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println( String.format("OFFSET: %s", offset) );
            for( int i = 0; i < stringList.size(); i++ ) {
                JsonNode jsonNode = objectMapper.readTree( stringList.get(i) );
                System.out.println( String.format("PAGE %s: %s", (i+1), stringList.get(i)) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
            }
            System.out.println( String.format("NUM PAGES: %s", stringList.size()) );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public void doGetPagesFromOffsetAsJsonNodesExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            int pageSize = 15;
            int offset = 10;
            int numPages = 3;
            List<JsonNode> jsonNodeList = ethosProxyClient.getPagesFromOffsetAsJsonNodes( resourceName, pageSize, offset, numPages );
            System.out.println( "******* doGetPagesFromOffsetAsJsonNodesExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println( String.format("OFFSET: %s", offset) );
            for( int i = 0; i < jsonNodeList.size(); i++ ) {
                JsonNode jsonNode = jsonNodeList.get( i );
                System.out.println( String.format("PAGE %s: %s", (i+1), jsonNode.toString()) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
            }
            System.out.println( String.format("NUM PAGES: %s", jsonNodeList.size()) );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetRowsExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            String version = "application/vnd.hedtech.integration.v7.2.0+json";
            // The List<EthosResponse> returned by the getRows() method is page-based, not row-based, even though it is
            // returning some number of rows because of the ancillary header information, requested URL, etc.
            // that is pertinent to each page contained in each EthosResponse.
            // This specifies the page size to use when getting the number of rows as a list of EthosResponses.
            int pageSize = 15;
            int numRows = 40;
            int rowCount = 0;
            ObjectMapper objectMapper = new ObjectMapper();
            List<EthosResponse> ethosResponseList = ethosProxyClient.getRows( resourceName, version, pageSize, numRows );
            System.out.println( "******* doGetRowsExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            for( int i = 0; i < ethosResponseList.size(); i++ ) {
                EthosResponse ethosResponse = ethosResponseList.get( i );
                JsonNode jsonNode = objectMapper.readTree( ethosResponse.getContent() );
                rowCount += jsonNode.size();
                System.out.println( String.format("PAGE %s: %s", (i+1), ethosResponse.getContent()) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
                System.out.println( String.format("PAGE %s REQUESTED URL: %s ", (i+1), ethosResponseList.get(i).getRequestedUrl()) );
            }
            System.out.println( String.format("NUM ROWS: %s", rowCount) );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public void doGetRowsAsStringsExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            String version = "application/vnd.hedtech.integration.v7.2.0+json";
            // The List<String> returned by the getRowsAsStrings() method is row-based, not page-based, because it only
            // contains the resource data itself, and not the ancillary header information, requested URL, etc. found
            // in the EthosResponse.
            // This specifies the page size for the SDK to use internally when getting the rows.
            int pageSize = 15;
            int numRows = 17;
            List<String> stringList = ethosProxyClient.getRowsAsStrings( resourceName, version, pageSize, numRows );
            System.out.println( "******* doGetRowsAsStringsExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println( String.format("NUM ROWS: %s", stringList.size()) );
            for( int i = 0; i < stringList.size(); i++ ) {
                System.out.println( String.format("ROW %s: %s", (i+1), stringList.get(i)) );
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetRowsAsJsonNodesExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            String version = "application/vnd.hedtech.integration.v7.2.0+json";
            // The List<JsonNode> returned by the getRowsAsJsonNodes() method is row-based, not page-based, because it only
            // contains the resource data itself, and not the ancillary header information, requested URL, etc. found
            // in the EthosResponse.
            // Notice that the page size is NOT specified here so the SDK will use the default page size when paging internally
            // to get the specified number of rows.
            int numRows = 17;
            List<JsonNode> jsonNodeList = ethosProxyClient.getRowsAsJsonNodes( resourceName, version, numRows );
            System.out.println( "******* doGetRowsAsJsonNodesExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println( String.format("NUM ROWS: %s", jsonNodeList.size()) );
            for( int i = 0; i < jsonNodeList.size(); i++ ) {
                JsonNode jsonNode = jsonNodeList.get( i );
                System.out.println( String.format("ROW %s: %s", (i+1), jsonNode.toString()) );
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetRowsFromOffsetExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            String version = "application/json";
            // The List<EthosResponse> returned by the getRowsFromOffset() method is page-based, not row-based, even
            // though it is returning some number of rows because of the ancillary header information, requested URL, etc.
            // that is pertinent to each page contained in each EthosResponse.
            // This specifies the page size to use when getting the number of rows as a list of EthosResponses.
            int pageSize = 15;
            int offset = 10;
            int numRows = 24;
            int rowCount = 0;
            ObjectMapper objectMapper = new ObjectMapper();
            List<EthosResponse> ethosResponseList = ethosProxyClient.getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
            System.out.println( "******* doGetRowsFromOffsetExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println( String.format("OFFSET: %s", offset) );
            for( int i = 0; i < ethosResponseList.size(); i++ ) {
                EthosResponse ethosResponse = ethosResponseList.get( i );
                JsonNode jsonNode = objectMapper.readTree( ethosResponse.getContent() );
                rowCount += jsonNode.size();
                System.out.println( String.format("PAGE %s: %s", (i+1), ethosResponse.getContent()) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
                System.out.println( String.format("PAGE %s REQUESTED URL: %s ", (i+1), ethosResponseList.get(i).getRequestedUrl()) );
            }
            System.out.println( String.format("NUM ROWS: %s", rowCount) );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public void doGetRowsFromOffsetAsStringsExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            String version = "application/json";
            // The List<String> returned by the getRowsFromOffsetAsStrings() method is row-based, not page-based, because it only
            // contains the resource data itself, and not the ancillary header information, requested URL, etc. found
            // in the EthosResponse.
            // Notice that the page size is NOT specified here so the SDK will use the default page size when paging internally
            // to get the specified number of rows.
            int offset = 10;
            int numRows = 17;
            List<String> stringList = ethosProxyClient.getRowsFromOffsetAsStrings( resourceName, version, offset, numRows );
            System.out.println( "******* doGetRowsFromOffsetAsStringsExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println( String.format("NUM ROWS: %s", stringList.size()) );
            System.out.println( String.format("OFFSET: %s", offset) );
            for( int i = 0; i < stringList.size(); i++ ) {
                System.out.println( String.format("ROW %s: %s", (i+1), stringList.get(i)) );
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetRowsFromOffsetAsJsonNodesExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        try {
            String resourceName = "student-cohorts";
            String version = "application/json";
            // The List<JsonNode> returned by the getRowsAsJsonNodes() method is row-based, not page-based, because it only
            // contains the resource data itself, and not the ancillary header information, requested URL, etc. found
            // in the EthosResponse.
            // This specifies the page size for the SDK to use internally when getting the rows.
            int pageSize = 15;
            int offset = 10;
            int numRows = 17;
            List<JsonNode> jsonNodeList = ethosProxyClient.getRowsFromOffsetAsJsonNodes( resourceName, version, pageSize, offset, numRows );
            System.out.println( "******* doGetRowsFromOffsetAsJsonNodesExample() *******" );
            System.out.println( String.format("Get data for resource: %s", resourceName) );
            System.out.println( String.format("NUM ROWS: %s", jsonNodeList.size()) );
            System.out.println( String.format("OFFSET: %s", offset) );
            for( int i = 0; i < jsonNodeList.size(); i++ ) {
                JsonNode jsonNode = jsonNodeList.get( i );
                System.out.println( String.format("ROW %s: %s", (i+1), jsonNode.toString()) );
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetResourceByIdExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        String resource = "student-cohorts";
        try {
            if( getByIdGUID != null && getByIdGUID.isBlank() == false ) {
                EthosResponse ethosResponse = ethosProxyClient.getById(resource, getByIdGUID);
                System.out.println("******* doGetResourceByIdExample() *******");
                System.out.println(String.format("RESOURCE: %s", resource));
                System.out.println(String.format("RESOURCE ID: %s", getByIdGUID));
                System.out.println(String.format("RESPONSE: %s", ethosResponse.getContent()));
                System.out.println(String.format("REQUESTED URL: %s ", ethosResponse.getRequestedUrl()));
            }
            else {
                System.out.println( "******* Skipping doGetResourceByIdExample() because the getByIdGUID was not set.  Please pass in a valid GUID value as a 2nd program argument to run this method. *******" );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void doGetResourceAsStringByIdExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        String resource = "student-cohorts";
        try {
            if( getByIdGUID != null && getByIdGUID.isBlank() == false ) {
                String response = ethosProxyClient.getByIdAsString(resource, getByIdGUID);
                System.out.println("******* doGetResourceAsStringByIdExample() *******");
                System.out.println(String.format("RESOURCE: %s", resource));
                System.out.println(String.format("RESOURCE ID: %s", getByIdGUID));
                System.out.println(String.format("RESPONSE: %s", response));
            }
            else {
                System.out.println( "******* Skipping doGetResourceAsStringByIdExample() because the getByIdGUID was not set.  Please pass in a valid GUID value as a 2nd program argument to run this method. *******" );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }


    public void doGetResourceAsJsonNodeByIdExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        String resource = "student-cohorts";
        try {
            if( getByIdGUID != null && getByIdGUID.isBlank() == false ) {
                JsonNode jsonNode = ethosProxyClient.getByIdAsJsonNode(resource, getByIdGUID);
                System.out.println("******* doGetResourceAsJsonNodeByIdExample() *******");
                System.out.println(String.format("RESOURCE: %s", resource));
                System.out.println(String.format("RESOURCE ID: %s", getByIdGUID));
                System.out.println(String.format("RESPONSE: %s", jsonNode.toString()));
            }
            else {
                System.out.println( "******* Skipping doGetResourceAsJsonNodeByIdExample() because the getByIdGUID was not set.  Please pass in a valid GUID value as a 2nd program argument to run this method. *******" );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void doGetResourcePageSizeExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        String resource = "student-cohorts";
        try {
            int pageSize = ethosProxyClient.getPageSize( resource );
            System.out.println("******* doGetResourcePageSizeExample() *******");
            System.out.println( String.format("RESOURCE: %s", resource) );
            System.out.println( String.format("PAGE SIZE: %s", pageSize) );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void doGetResourceMaxPageSizeExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        String resource = "student-cohorts";
        try {
            int pageSize = ethosProxyClient.getMaxPageSize( resource );
            System.out.println("******* doGetResourceMaxPageSizeExample() *******");
            System.out.println( String.format("RESOURCE: %s", resource) );
            System.out.println( String.format("MAX PAGE SIZE: %s", pageSize) );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void doCRUDExample() {
        EthosProxyClient ethosProxyClient = getEthosProxyClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        System.out.println("******* doCRUDExample() *******");

        try {
            // get a single person record
            List<EthosResponse> responses = ethosProxyClient.getRows("persons", 1);
//            JsonNode person = responses.get(0).getContentAsJson();
            JsonNode personNode = ethosResponseConverter.toJsonNode( responses.get(0) );
            String personId = personNode.elements().next().get("id").asText();

            // build a person-holds resource
            Instant rightNow = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            ObjectNode personHoldNode = JsonNodeFactory.instance.objectNode();
            personHoldNode.put("id", "00000000-0000-0000-0000-000000000000");
            personHoldNode.put("startOn", rightNow.toString());
            // add a person object with 'id'
            personHoldNode.putObject("person").put("id", personId);
            // add a type object with 'category'
            personHoldNode.putObject("type").put("category", "financial");

            // send a POST request to create a new person-holds record
            EthosResponse response = ethosProxyClient.post("person-holds", personHoldNode);
            System.out.println("Created a 'person-holds' record:");
            System.out.println(response.getContent());

            // get the 'id' of the new record
//            String newId = response.getContentAsJson().get("id").asText();
            String newId = ethosResponseConverter.toJsonNode(response).get("id").asText();

            // change the date on the person-holds record and send a PUT request to update the record
            personHoldNode.remove("id");
            personHoldNode.put("startOn", rightNow.plus(1, ChronoUnit.DAYS).toString());
            response = ethosProxyClient.put("person-holds", newId, personHoldNode);
            System.out.println(String.format("Successfully updated person-holds record %s.", newId));

            // delete the record
            ethosProxyClient.delete("person-holds", newId);
            System.out.println(String.format("Successfully deleted person-holds record %s.", newId));

            // attempt to get the record that was created and make sure that fails
            try{
                ethosProxyClient.getById("person-holds", newId);
            } catch (HttpResponseException ex) {
                System.out.println(String.format("Failed to get person-holds record %s.  The delete was successful.", newId));
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
}
