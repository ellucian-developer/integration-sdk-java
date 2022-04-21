/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.sample.commandline;


import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.EthosResponseConverter;
import com.ellucian.ethos.integration.client.errors.ErrorFactory;
import com.ellucian.ethos.integration.client.errors.EthosError;
import com.ellucian.ethos.integration.client.errors.EthosErrorsClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.Header;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class EthosErrorsClientExample {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private String apiKey;
    private String getByIdGUID;

    public EthosErrorsClientExample( String apiKey, String guid ) {
        this.apiKey = apiKey;
        this.getByIdGUID = guid;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    public static void main( String[] args ) {
        if( args == null || args.length == 0 ) {
            System.out.println( "Please enter an API key and a valid errors GUID value for an existing error ID (in that order) as program arguments when running this example class.  " +
                    "An API key is required to run these examples.\nIf a valid GUID value for errors is not provided, this class will still run but the " +
                    "example methods that get an error by ID will be skipped.");
            return;
        }
        String apiKey = args[ 0 ];
        String getByIdGUID = null;
        if( args.length == 2 ) {
            getByIdGUID = args[ 1 ];
        }
        EthosErrorsClientExample ethosErrorsClientExample = new EthosErrorsClientExample( apiKey, getByIdGUID );
        ethosErrorsClientExample.getErrorHeaders();
        ethosErrorsClientExample.getErrors();
        ethosErrorsClientExample.getErrorsAsJsonNode();
        ethosErrorsClientExample.getErrorsAsString();
        ethosErrorsClientExample.getErrorsAsEthosErrors();
        ethosErrorsClientExample.getErrorById();
        ethosErrorsClientExample.getErrorByIdAsEthosError();
        ethosErrorsClientExample.getErrorByIdAsJsonNode();
        ethosErrorsClientExample.getErrorByIdAsString();
        ethosErrorsClientExample.createError();
        ethosErrorsClientExample.getTotalErrorCount();
        /*************************************************************
         Commenting the following methods, see block comment below.
        ethosErrorsClientExample.getAllErrors();
        ethosErrorsClientExample.getAllErrorsAsJsonNodes();
        ethosErrorsClientExample.getAllErrorsAsStrings();
        ethosErrorsClientExample.getAllErrorsWithPageSizeAsJsonNodes();
        *************************************************************/
        ethosErrorsClientExample.getErrorsFromOffsetWithPageSize();
    }

    private EthosErrorsClient getEthosErrorsClient() {
        return new EthosClientBuilder(apiKey)
                   .withConnectionTimeout(30)
                   .withConnectionRequestTimeout(30)
                   .withSocketTimeout(30)
                   .buildEthosErrorsClient();
    }

    public void getErrorHeaders() {
        System.out.println( "******* ethosErrorClient.getErrorHeaders() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        try {
            EthosResponse ethosResponse = ethosErrorsClient.get();
            List<String> headerKeyList = ethosResponse.getHeaderMapKeys();
            for( String headerKey : headerKeyList ) {
                Header header = ethosResponse.getHeader( headerKey );
                System.out.println( String.format("HEADER KEY: %s, HEADER VALUE: %s", headerKey, header.getValue()) );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getErrors() {
        System.out.println( "******* ethosErrorClient.get() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            EthosResponse ethosResponse = ethosErrorsClient.get();
            Header totalCountHeader = ethosResponse.getHeader( EthosErrorsClient.HDR_TOTAL_COUNT );
            Header remainingCountHeader = ethosResponse.getHeader( EthosErrorsClient.HDR_REMAINING_COUNT );
            System.out.println( String.format("TOTAL ERROR COUNT: %s", totalCountHeader.getValue()) );
            System.out.println( String.format("REMAINING ERROR COUNT: %s", remainingCountHeader.getValue()) );
            JsonNode errorsNode = ethosResponseConverter.toJsonNode( ethosResponse );
            Iterator<JsonNode> errorsIter = errorsNode.iterator();
            while( errorsIter.hasNext() ) {
                JsonNode errNode = errorsIter.next();
                System.out.println( errNode.toPrettyString() );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getErrorsAsJsonNode() {
        System.out.println( "******* ethosErrorClient.getAsJsonNode() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        try {
            JsonNode errorsNode = ethosErrorsClient.getAsJsonNode();
            Iterator<JsonNode> errorsIter = errorsNode.iterator();
            while( errorsIter.hasNext() ) {
                JsonNode errNode = errorsIter.next();
                System.out.println( errNode.toPrettyString() );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getErrorsAsString() {
        System.out.println( "******* ethosErrorClient.getAsString() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        try {
            String errorsStr = ethosErrorsClient.getAsString();
            System.out.println( errorsStr );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getErrorsAsEthosErrors() {
        System.out.println( "******* ethosErrorClient.getAsEthosError() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        try {
            List<EthosError> ethosErrorList = ethosErrorsClient.getAsEthosErrors();
            System.out.println( "NUMBER OF ERRORS: " + ethosErrorList.size() );
            for( EthosError ethosError : ethosErrorList ) {
                System.out.println( ethosError.toString() );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }

    }

    public void getErrorById() {
        System.out.println( "******* ethosErrorClient.getById() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        try {
            if( getByIdGUID != null && getByIdGUID.isBlank() == false ) {
                EthosResponse ethosResponse = ethosErrorsClient.getById( getByIdGUID );
                System.out.println( ethosResponse.getContent() );
            }
            else {
                System.out.println( "******* Skipping ethosErrorClient.getById() because the getByIdGUID was not set.  Please pass in a valid GUID value as a 2nd program argument to run this method. *******" );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getErrorByIdAsEthosError() {
        System.out.println( "******* ethosErrorClient.getByIdAsEthosError() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        try {
            if( getByIdGUID != null && getByIdGUID.isBlank() == false ) {
                EthosError ethosError = ethosErrorsClient.getByIdAsEthosError( getByIdGUID );
                System.out.println( ethosError.toString() );
            }
            else {
                System.out.println( "******* Skipping ethosErrorClient.getByIdAsEthosError() because the getByIdGUID was not set.  Please pass in a valid GUID value as a 2nd program argument to run this method. *******" );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getErrorByIdAsJsonNode() {
        System.out.println( "******* ethosErrorClient.getByIdAsJsonNode() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        try {
            if( getByIdGUID != null && getByIdGUID.isBlank() == false ) {
                JsonNode errorNode = ethosErrorsClient.getByIdAsJsonNode( getByIdGUID );
                System.out.println( errorNode.toPrettyString() );
            }
            else {
                System.out.println( "******* Skipping ethosErrorClient.getByIdAsJsonNode() because the getByIdGUID was not set.  Please pass in a valid GUID value as a 2nd program argument to run this method. *******" );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getErrorByIdAsString() {
        System.out.println( "******* ethosErrorClient.getByIdAsString() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        try {
            if( getByIdGUID != null && getByIdGUID.isBlank() == false ) {
                String errorStr = ethosErrorsClient.getByIdAsString( getByIdGUID );
                System.out.println( errorStr );
            }
            else {
                System.out.println( "******* Skipping ethosErrorClient.getByIdAsString() because the getByIdGUID was not set.  Please pass in a valid GUID value as a 2nd program argument to run this method. *******" );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }


    public void createError() {
        System.out.println( "******* ethosErrorClient.create() *******" );
        String errorStr = "{" +
                "          \"id\": \"00000000-0000-0000-0000-000000000000\"," +
                "          \"dateTime\": \"2020-10-27T03:10:44.827Z\"," +
                "          \"severity\": \"error\"," +
                "          \"responseCode\": 500," +
                "          \"description\": \"Internal Server Error\"," +
                "          \"details\": \"This is a more info on the info error\"," +
                "          \"applicationId\": \"00000000-0000-0000-0000-000000000000\"," +
                "          \"applicationName\": \"Banner\"," +
                "          \"correlationId\": \"2468UserMade3242134\"," +
                "          \"resource\": {" +
                "            \"id\": \"00000000-0000-0000-0000-000000000000\"," +
                "            \"name\": \"persons\"" +
                "          }," +
                "          \"applicationSubtype\": \"EMA\"" +
                "}";

        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        try {
            EthosError ethosError = ErrorFactory.createErrorFromJson( errorStr );
            System.out.println( "CREATING ETHOS ERROR: " + ethosError.toString() );
            EthosResponse errorResponse = ethosErrorsClient.post( ethosError );
            System.out.println( String.format("Created Ethos Error: %s", errorResponse.getContent()) );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getTotalErrorCount() {
        System.out.println( "******* ethosErrorClient.getTotalErrorCount() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        try {
            int totalErrorCount = ethosErrorsClient.getTotalErrorCount();
            System.out.println( String.format("TOTAL ERROR COUNTER: %s", totalErrorCount) );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    /************************************************************************************************************
     * Commenting out the getAllErrors*() methods due to the potential for a large quantity of errors retrieved
     * at runtime.  But if desired, this code can be uncommented and executed as part of these example methods.
     * Be aware that these commented methods may take some time to run depending on the quantity of errors,
     * since all errors are retrieved.
     ************************************************************************************************************/

//    public void getAllErrors() {
//        System.out.println( "******* ethosErrorClient.getAllErrors() *******" );
//        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
//        try {
//            List<EthosResponse> ethosResponseList = ethosErrorsClient.getAllErrors();
//            for( int i = 0; i < ethosResponseList.size(); i++ ) {
//                System.out.println( String.format("ERROR PAGE %s, REQUESTED URL: %s, ERROR PAGE DETAILS: %s", (i+1), ethosResponseList.get(i).getRequestedUrl(), ethosResponseList.get(i).getContent()) );
//            }
//        }
//        catch( IOException ioe ) {
//            ioe.printStackTrace();
//        }
//    }
//
//    public void getAllErrorsAsJsonNodes() {
//        System.out.println( "******* ethosErrorClient.getAllErrorsAsJsonNodes() *******" );
//        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
//        try {
//            List<JsonNode> jsonNodePageList = ethosErrorsClient.getAllErrorsAsJsonNodes();
//            for( int pageCount = 0; pageCount < jsonNodePageList.size(); pageCount++ ) {
//                JsonNode jsonNodePage = jsonNodePageList.get( pageCount );
//                for( int errorCount = 0; errorCount < jsonNodePage.size(); errorCount++ ) {
//                    JsonNode errorNode = jsonNodePage.get( errorCount );
//                    System.out.println( String.format("ERROR PAGE %s, ERROR COUNT: %s, ERROR ID: %s, SEVERITY: %s, DESCRIPTION: %s", (pageCount + 1), (errorCount +1), errorNode.at("/id").asText(), errorNode.at("/severity").asText(), errorNode.at("/description").asText()) );
//                }
//            }
//        }
//        catch( IOException ioe ) {
//            ioe.printStackTrace();
//        }
//    }
//
//    public void getAllErrorsAsStrings() {
//        System.out.println( "******* ethosErrorClient.getAllErrorsAsStrings() *******" );
//        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
//        try {
//            List<String> errorsStringList = ethosErrorsClient.getAllErrorsAsStrings();
//            for( int i = 0; i < errorsStringList.size(); i++ ) {
//                System.out.println( String.format("ERROR PAGE %s, ERROR PAGE DETAILS: %s", (i+1), errorsStringList.get(i)) );
//            }
//        }
//        catch( IOException ioe ) {
//            ioe.printStackTrace();
//        }
//    }
//
//    public void getAllErrorsWithPageSizeAsJsonNodes() {
//        System.out.println( "******* ethosErrorClient.getAllErrorsWithPageSizeAsJsonNodes() *******" );
//        int pageSize = 15;
//        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
//        try {
//            List<JsonNode> jsonNodePageList = ethosErrorsClient.getAllErrorsWithPageSizeAsJsonNodes( pageSize );
//            for( int pageCount = 0; pageCount < jsonNodePageList.size(); pageCount++ ) {
//                JsonNode jsonNodePage = jsonNodePageList.get( pageCount );
//                for( int errorCount = 0; errorCount < jsonNodePage.size(); errorCount++ ) {
//                    JsonNode errorNode = jsonNodePage.get( errorCount );
//                    System.out.println( String.format("ERROR PAGE %s, SPECIFIED PAGE SIZE: %s, RETURNED PAGE SIZE: %s, ERROR COUNT: %s, ERROR ID: %s, SEVERITY: %s, DESCRIPTION: %s", (pageCount + 1), pageSize, jsonNodePage.size(), (errorCount +1), errorNode.at("/id").asText(), errorNode.at("/severity").asText(), errorNode.at("/description").asText()) );
//                }
//            }
//        }
//        catch( IOException ioe ) {
//            ioe.printStackTrace();
//        }
//    }

    public void getErrorsFromOffsetWithPageSize() {
        System.out.println( "******* ethosErrorClient.getErrorsFromOffsetWithPageSize() *******" );
        EthosErrorsClient ethosErrorsClient = getEthosErrorsClient();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        try {
            int pageSize = 15;
            int totalCount = ethosErrorsClient.getTotalErrorCount();
            // Calculate the offset to be 95% of the totalCount to avoid paging through potentially tons of errors.
            int offset = (int)(totalCount * 0.95);
            double expectedNumPages = Math.ceil( (Double.valueOf(totalCount) - Double.valueOf(offset)) / Double.valueOf(pageSize) );
            System.out.println(String.format("Calculated offset of %s which is 95 percent of a total count of %s to avoid paging through potentially lots of errors.", offset, totalCount));
            System.out.println("To run with more paging, manually set the offset to a lower value, or reduce the percentage of the total count.");
            List<EthosResponse> ethosResponseList = ethosErrorsClient.getErrorsFromOffsetWithPageSize( offset, pageSize );
            System.out.println( String.format("FROM OFFSET: %s, EXPECTED NUMBER OF PAGES: %s, NUMBER OF PAGES RETURNED: %s", offset, expectedNumPages, ethosResponseList.size()) );
            for( int pageCount = 0; pageCount < ethosResponseList.size(); pageCount++ ) {
                EthosResponse ethosResponse = ethosResponseList.get( pageCount );
                System.out.println( String.format("REQUESTED URL: %s", ethosResponse.getRequestedUrl()) );
                List<EthosError> ethosErrorList = ethosResponseConverter.toEthosErrorList( ethosResponse );
                for( int errorCount = 0; errorCount < ethosErrorList.size(); errorCount++ ) {
                    EthosError ethosError = ethosErrorList.get( errorCount );
                    System.out.println( String.format("ERROR PAGE %s, SPECIFIED PAGE SIZE: %s, RETURNED PAGE SIZE: %s, ERROR COUNT: %s, ERROR ID: %s, SEVERITY: %s, DESCRIPTION: %s", (pageCount + 1), pageSize, ethosErrorList.size(), (errorCount +1), ethosError.getId(), ethosError.getSeverity(), ethosError.getDescription()) );
                }
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

}