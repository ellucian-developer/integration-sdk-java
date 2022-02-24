package com.ellucian.ethos.integration.sample.commandline;

import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.proxy.EthosProxyClient;
import com.ellucian.ethos.integration.client.proxy.EthosProxyClientAsync;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class EthosProxyClientAsyncExample {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private String apiKey;

    public EthosProxyClientAsyncExample(String apiKey) {
        this.apiKey = apiKey;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    public static void main(String[] args) {
        if( args == null || args.length == 0 ) {
            System.out.println("Please enter an API key as a program argument when running this example class. An API key is required to run these examples.");
            return;
        }
        String apiKey = args[ 0 ];

        EthosProxyClientAsyncExample ethosProxyClientExample = new EthosProxyClientAsyncExample( apiKey );

        // Asynchronous examples
        ethosProxyClientExample.doGetAllPagesFromOffsetAsStringsAsyncExample();
        ethosProxyClientExample.doGetAllPagesFromOffsetAsyncExample();
        ethosProxyClientExample.doGetRowsFromOffsetAsyncExample();
    }

    private EthosProxyClientAsync getEthosProxyClientAsync() {
        return new EthosClientBuilder(apiKey)
                .withConnectionTimeout(30)
                .withConnectionRequestTimeout(30)
                .withSocketTimeout(30)
                .buildEthosProxyAsyncClient();
    }

    public void doGetAllPagesFromOffsetAsyncExample() {
        EthosProxyClientAsync ethosProxyClientAsync = getEthosProxyClientAsync();
        try {
            String resourceName = "student-cohorts";
            int totalCount = ethosProxyClientAsync.getTotalCount( resourceName );
            // Calculate the offset to be 95% of the totalCount to avoid paging through potentially tons of pages.
            int offset = (int)(totalCount * 0.95);
            ObjectMapper objectMapper = new ObjectMapper();

            // Wait on the result with exception handling
            CompletableFuture<List<EthosResponse>> asyncResponse = ethosProxyClientAsync.getAllPagesFromOffsetAsync( resourceName, offset );

            // While the CompletableFuture thread is running, additional operations can be performed.  For the sake of
            // demonstrating this we are just running a few printlns to print out the current time.
            System.out.println(LocalDateTime.now());
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(LocalDateTime.now());

            // Using .get allows for InterruptedException and ExecutionException handling.
            List<EthosResponse> ethosResponseList = asyncResponse.get();

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
        } catch (CompletionException | IOException | InterruptedException | ExecutionException ioe) {
            // catching InterruptedException, and ExecutionException, is required when using .get to get the value.
            ioe.printStackTrace();
        }
    }

    public void doGetAllPagesFromOffsetAsStringsAsyncExample() {
        EthosProxyClientAsync ethosProxyClient = getEthosProxyClientAsync();
        try {
            String resourceName = "student-cohorts";
            int totalCount = ethosProxyClient.getTotalCount( resourceName );
            // Calculate the offset to be 95% of the totalCount to avoid paging through potentially tons of pages.
            int offset = (int)(totalCount * 0.95);
            ObjectMapper objectMapper = new ObjectMapper();

            CompletableFuture<List<String>> asyncResponse = ethosProxyClient.getAllPagesFromOffsetAsStringsAsync(resourceName, offset);

            // While the CompletableFuture thread is running, additional operations can be performed.  For the sake of
            // demonstrating this we are just running a few printlns to print out the current time.
            System.out.println(LocalDateTime.now());
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(LocalDateTime.now());

            // using .join here so there is no exception thrown - all exceptions will be unchecked.  This is the
            // same as in C# where the default is to use the async processing without checked exceptions.
            List<String> stringList = asyncResponse.join();

            System.out.println( "******* doGetAllPagesFromOffsetAsStringsAsyncExample() *******" );
            System.out.println(String.format("Get data for resource: %s", resourceName));
            System.out.println(String.format("Calculated offset of %s which is 95 percent of a total count of %s to avoid paging through potentially lots of pages.", offset, totalCount));
            System.out.println("To run with more paging, manually set the offset to a lower value, or reduce the percentage of the total count.");
            for( int i = 0; i < stringList.size(); i++ ) {
                JsonNode jsonNode = objectMapper.readTree( stringList.get(i) );
                System.out.println( String.format("PAGE %s: %s", (i+1), stringList.get(i)) );
                System.out.println( String.format("PAGE %s SIZE: %s", (i+1), jsonNode.size()) );
                System.out.println( String.format("OFFSET: %s", offset) );
            }
        } catch (CompletionException | IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void doGetRowsFromOffsetAsyncExample() {
        EthosProxyClientAsync ethosProxyClient = getEthosProxyClientAsync();
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

            CompletableFuture<List<EthosResponse>> asyncResponse =
                    ethosProxyClient.getRowsFromOffsetAsync( resourceName, version, pageSize, offset, numRows );

            // While the CompletableFuture thread is running, additional operations can be performed.  For the sake of
            // demonstrating this we are just running a few printlns to print out the current time.
            System.out.println(LocalDateTime.now());
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(LocalDateTime.now());

            // Now go back and block on the CompletableFuture until it returns a result.
            List<EthosResponse> ethosResponseList = asyncResponse.join();

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
        } catch (CompletionException | IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
