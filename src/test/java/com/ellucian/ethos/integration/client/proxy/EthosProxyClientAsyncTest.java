/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy;

import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.EthosResponseConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class EthosProxyClientAsyncTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private EthosProxyClientAsync spyEthosProxyClientAsync;

    // ==========================================================================
    // Methods
    // ==========================================================================

    @BeforeEach
    void setup() throws IOException {
        // Not using the EthosClientFactory to build the EthosProxyClient because we have to spy it with Mockito.
        spyEthosProxyClientAsync = spy(
                new EthosProxyClientAsync("11111111-1111-1111-1111-111111111111", null, null, null)
        );
    }

    private EthosResponse buildEthosResponse() {
        return new EthosResponse(new HashMap<>(), "[{\"someLabel1\":\"someValue1\"},{\"someLabel2\":\"someValue2\"}]", 200);
    }

    private JsonNode buildJsonNodeResponse() throws JsonProcessingException {
        EthosResponse ethosResponse = buildEthosResponse();
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }

    private List<EthosResponse> buildEthosResponseList() {
        List<EthosResponse> testResponseList = new ArrayList<>();
        testResponseList.add( buildEthosResponse() );
        testResponseList.add( buildEthosResponse() );
        return testResponseList;
    }

    private List<String> buildStringResponseList() {
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        List<EthosResponse> ethosResponseList = buildEthosResponseList();
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    private List<String> buildRowBasedStringResponseList() throws JsonProcessingException {
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        List<EthosResponse> ethosResponseList = buildEthosResponseList();
        return ethosResponseConverter.toRowBasedStringList( ethosResponseList );
    }

    private List<JsonNode> buildRowBasedJsonNodeResponseList() throws JsonProcessingException {
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        List<EthosResponse> ethosResponseList = buildEthosResponseList();
        return ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
    }

    private CompletableFuture<List<EthosResponse>> buildResponseListCompletableFuture() {
        CompletableFuture<List<EthosResponse>> promise = new CompletableFuture<>();
        promise.complete(this.buildEthosResponseList());
        return promise;
    }

    private CompletableFuture<List<String>> buildStringResponseListCompletableFuture() {
        CompletableFuture<List<String>> promise = new CompletableFuture<>();
        promise.complete(this.buildStringResponseList());
        return promise;
    }

    private void evaluateEthosResponseListResult( List<EthosResponse> responseList, List<EthosResponse> expectedResponseList ) {
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() == false );
        assertTrue( responseList.size() == expectedResponseList.size() );
        for( int i = 0; i < responseList.size(); i++ ) {
            assertTrue( responseList.get(i).getContent() != null );
            assertTrue( responseList.get(i).getContent().equals(expectedResponseList.get(i).getContent()) );
        }
    }

    private void evaluateStringListResult( List<String> responseList, List<String> expectedResponseList ) {
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() == false );
        assertTrue( responseList.size() == expectedResponseList.size() );
        for( int i = 0; i < responseList.size(); i++ ) {
            assertTrue( responseList.get(i).equals(expectedResponseList.get(i)) );
        }
    }

    private void evaluateJsonNodeListResult( List<JsonNode> responseList, List<EthosResponse> expectedResponseList ) {
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() == false );
        assertTrue( responseList.size() == expectedResponseList.size() );
        for( int i = 0; i < responseList.size(); i++ ) {
            assertTrue( responseList.get(i) != null );
            assertTrue( responseList.get(i).toString() != null );
            assertTrue( responseList.get(i).toString().equals(expectedResponseList.get(i).getContent()) );
        }
    }

    private void evaluateRowBasedJsonNodeListResult( List<JsonNode> responseList, List<JsonNode> expectedResponseList ) {
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() == false );
        assertTrue( responseList.size() == expectedResponseList.size() );
        for( int i = 0; i < responseList.size(); i++ ) {
            assertTrue( responseList.get(i) != null );
            assertTrue( responseList.get(i).toString() != null );
            assertTrue( responseList.get(i).toString().equals(expectedResponseList.get(i).toString()) );
        }
    }

    @Test
    void getAllPagesAsyncTest() throws IOException {
        String resourceName = "someResource";
        List<EthosResponse> expectedResponseList = buildEthosResponseList();
        CompletableFuture<List<EthosResponse>> promise = buildResponseListCompletableFuture();
        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(promise).when(spyEthosProxyClientAsync).getAllPagesAsync( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsync( resourceName );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesAsync(resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the result matches the test ethos response.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        List<EthosResponse> expectedResponseList = buildEthosResponseList();
        CompletableFuture<List<EthosResponse>> promise = buildResponseListCompletableFuture();
        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(promise).when(spyEthosProxyClientAsync).getAllPagesAsync( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsync( resourceName, version );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesAsync(resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();
        CompletableFuture<List<EthosResponse>> promise = buildResponseListCompletableFuture();
        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(promise).when(spyEthosProxyClientAsync).getAllPagesAsync( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsync( resourceName, pageSize );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesAsync(resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesAsStringsAsyncTest() throws IOException {
        String resourceName = "someResource";
        List<String> expectedResultList = buildStringResponseList();
        CompletableFuture<List<String>> promise = buildStringResponseListCompletableFuture();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(promise).when(spyEthosProxyClientAsync).getAllPagesAsStringsAsync( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsStringsAsync( resourceName );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesAsStringsAsync(resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getAllPagesAsStringsWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";

        List<String> expectedResultList = buildStringResponseList();
        CompletableFuture<List<String>> promise = buildStringResponseListCompletableFuture();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(promise).when(spyEthosProxyClientAsync).getAllPagesAsStringsAsync( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsStringsAsync( resourceName, version );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesAsStringsAsync(resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getAllPagesAsStringsWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;

        List<String> expectedResultList = buildStringResponseList();
        CompletableFuture<List<String>> promise = buildStringResponseListCompletableFuture();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(promise).when(spyEthosProxyClientAsync).getAllPagesAsStringsAsync( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsStringsAsync( resourceName, pageSize );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesAsStringsAsync(resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getAllPagesAsStringsWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getAllPages( resourceName, version, pageSize );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsStringsAsync( resourceName, version, pageSize );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getAllPagesAsJsonNodesAsyncTest() throws IOException {
        String resourceName = "someResource";
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPages( anyString(), anyString(), anyInt() );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsJsonNodesAsync( resourceName );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPages(resourceName, EthosProxyClient.DEFAULT_VERSION, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesAsJsonNodesWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsJsonNodesAsync( resourceName, version );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPages(resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesAsJsonNodesWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsJsonNodesAsync( resourceName, pageSize );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPages(resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesAsJsonNodesWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPages( resourceName, version, pageSize );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getAllPagesAsJsonNodesAsync( resourceName, version, pageSize );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsync( resourceName, offset );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsync( resourceName, offset, pageSize );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetWithVersionSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsync( resourceName, version, offset );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetWithVersionAndPageSizeReturnsEmptyListTest() throws IOException {
        String resourceName = null;
        String version = "someVersion";
        int pageSize = 17;
        int offset = 25;
        // Run the test with a null resourceName string.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsync( resourceName, version, offset, pageSize );
        List<EthosResponse> responseList = completableFuture.join();

        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsync( resourceName, version, offset, pageSize );
        responseList = completableFuture.join();
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );
    }

    @Test
    void getAllPagesFromOffsetWithVersionAndPageSizeWhenOffsetIsNegativeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPages(anyString(), anyString(), anyInt());
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsync( resourceName, version, offset, pageSize );
        List<EthosResponse> responseList = completableFuture.join();
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int pageSize = 17;
        EthosResponse testEthosResponse = buildEthosResponse();
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the handlePaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset(
                resourceName, version, offset, pageSize
        );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsync( resourceName, version, offset, pageSize );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(
                resourceName, version, offset, pageSize
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsStringsAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResponseList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsStringsAsync( resourceName, offset );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsStringsWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResponseList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsStringsAsync( resourceName, offset, pageSize );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsStringsWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResponseList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsStringsAsync( resourceName, version, offset );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsStringsWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int pageSize = 17;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResponseList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsStringsAsync( resourceName, version, offset, pageSize );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(resourceName, version, offset, pageSize );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsJsonNodesAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsJsonNodesAsync( resourceName, offset );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsJsonNodesWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsJsonNodesAsync( resourceName, offset, pageSize );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getAllPagesFromOffsetAsJsonNodesWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsJsonNodesAsync( resourceName, version, offset );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getAllPagesFromOffsetAsJsonNodesWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getAllPagesFromOffsetAsJsonNodesAsync( resourceName, version, offset, pageSize );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset(resourceName, version, offset, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesAsyncTest() throws IOException {
        String resourceName = "someResource";
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPages( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesAsync( resourceName, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( anyString(), anyString(), anyInt(), anyInt()  );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesAsync( resourceName, version, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numPages );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesAsync( resourceName, pageSize, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesWithVersionAndPageSizeReturnsEmptyListAsyncTest() throws IOException {
        String resourceName = null;
        String version = "someVersion";
        int pageSize = 17;
        int numPages = 40;
        // Run the test with a null resourceName string.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesAsync( resourceName, version, pageSize, numPages );
        List<EthosResponse> responseList = completableFuture.join();
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        completableFuture = spyEthosProxyClientAsync.getPagesAsync( resourceName, version, pageSize, numPages );
        responseList = completableFuture.join();
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );
    }


    @Test
    void getPagesWithVersionAndPageSizeWhenNumPagesIsNegativeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPages( resourceName, version, pageSize );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesAsync( resourceName, version, pageSize, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getPagesWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = 25;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the handlePaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPages(
                resourceName, version, pageSize, numPages
        );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesAsync( resourceName, version, pageSize, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages(
                resourceName, version, pageSize, numPages
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesAsStringsAsyncTest() throws IOException {
        String resourceName = "someResource";
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getPages( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getPagesAsStringsAsync( resourceName, numPages );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( anyString(), anyString(), anyInt(), anyInt() );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesAsStringsWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getPagesAsStringsAsync( resourceName, version, numPages );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesAsStringsWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numPages );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getPagesAsStringsAsync( resourceName, pageSize, numPages );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesAsStringsWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getPages( resourceName, version, pageSize, numPages );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getPagesAsStringsAsync( resourceName, version, pageSize, numPages );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( resourceName, version, pageSize, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesAsJsonNodesAsyncTest() throws IOException {
        String resourceName = "someResource";
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPages(
                anyString(), anyString(), anyInt(), anyInt()
        );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getPagesAsJsonNodesAsync( resourceName, numPages );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages(
                anyString(), anyString(), anyInt(), anyInt()
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesAsJsonNodesWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getPagesAsJsonNodesAsync( resourceName, version, numPages );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesAsJsonNodesWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize , numPages );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getPagesAsJsonNodesAsync( resourceName, pageSize, numPages );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize , numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesAsJsonNodesWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPages( resourceName, version, pageSize , numPages );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getPagesAsJsonNodesAsync( resourceName, version, pageSize, numPages );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( resourceName, version, pageSize , numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getPagesFromOffsetAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset(
            anyString(), anyString(), anyInt(), anyInt(), anyInt()
        );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsync( resourceName, offset, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset(
                anyString(), anyString(), anyInt(), anyInt(), anyInt()
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numPages );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsync( resourceName, pageSize, offset, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numPages );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsync( resourceName, version, offset, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithVersionAndPageSizeReturnsEmptyListAsyncTest() throws IOException {
        String resourceName = null;
        String version = "someVersion";
        int pageSize = 17;
        int offset = 25;
        int numPages = 40;
        // Run the test with a null resourceName string.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsync( resourceName, version, pageSize, offset, numPages );
        List<EthosResponse> responseList = completableFuture.join();
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsync( resourceName, version, pageSize, offset, numPages );
        responseList = completableFuture.join();
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );
    }


    @Test
    void getPagesFromOffsetWithVersionAndPageSizeWhenOffsetAndNumPagesAreNegativeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = -1;
        int numPages = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPages( resourceName, version, pageSize );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsync( resourceName, version, pageSize, offset, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithVersionAndPageSizeWhenOffsetIsNegativeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = -1;
        int numPages = 40;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPages( resourceName, version, pageSize, numPages );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsync( resourceName, version, pageSize, offset, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPages( resourceName, version, pageSize, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithVersionAndPageSizeWhenNumPagesIsNegativeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsync( resourceName, version, pageSize, offset, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = 40;
        int pageSize = 17;
        int offset = 25;

        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset(
                resourceName, version, pageSize, offset, numPages
        );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsync( resourceName, version, pageSize, offset, numPages );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset(
                resourceName, version, pageSize, offset, numPages
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetAsStringsAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsStringsAsync( resourceName, offset, numPages );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset(
                anyString(), anyString(), anyInt(), anyInt()
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getPagesFromOffsetAsStringsWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset(
                anyString(), anyString(), anyInt(), anyInt()
        );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsStringsAsync( resourceName, pageSize, offset, numPages );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset(
                anyString(), anyString(), anyInt(), anyInt()
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesFromOffsetAsStringsWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset(
                anyString(), anyString(), anyInt(), anyInt()
        );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsStringsAsync( resourceName, version, offset, numPages );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset(
                anyString(), anyString(), anyInt(), anyInt()
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesFromOffsetAsStringsWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsStringsAsync( resourceName, version, pageSize, offset, numPages );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesFromOffsetAsJsonNodesAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset( anyString(), anyString(), anyInt(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsJsonNodesAsync( resourceName, offset, numPages );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset( anyString(), anyString(), anyInt(), anyInt(), anyInt() );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetAsJsonNodesWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset , numPages );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsJsonNodesAsync( resourceName, pageSize, offset, numPages );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset , numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetAsJsonNodesWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset( anyString(), anyString(), anyInt(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsJsonNodesAsync( resourceName, version, offset, numPages );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset( anyString(), anyString(), anyInt(), anyInt(), anyInt() );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getPagesFromOffsetAsJsonNodesWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getPagesFromOffsetAsJsonNodesAsync( resourceName, version, pageSize, offset, numPages );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsAsyncTest() throws IOException {
        String resourceName = "someResource";
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getRows( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsAsync( resourceName, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( anyString(), anyString(), anyInt(), anyInt() );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsAsync( resourceName, version, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getRowsWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsAsync( resourceName, pageSize, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getRowsWithVersionAndPageSizeReturnsEmptyListAsyncTest() throws IOException {
        String resourceName = null;
        String version = "someVersion";
        int pageSize = 17;
        int numRows = 50;
        // Run the test with a null resourceName string.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsAsync( resourceName, version, pageSize, numRows );
        List<EthosResponse> responseList = completableFuture.join();
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        completableFuture = spyEthosProxyClientAsync.getRowsAsync( resourceName, version, pageSize, numRows );
        responseList = completableFuture.join();
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );
    }

    @Test
    void getRowsWithVersionAndPageSizeWhenNumRowsIsNegativeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPages( resourceName, version, pageSize );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsAsync( resourceName, version, pageSize, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = 50;
        int pageSize = 17;

        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getRows( resourceName, version, pageSize, numRows );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsAsync( resourceName, version, pageSize, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows(
                resourceName, version, pageSize, numRows
        );

        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getRowsAsStringsAsyncTest() throws IOException {
        String resourceName = "someResource";
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getRows( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getRowsAsStringsAsync( resourceName, numRows );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( anyString(), anyString(), anyInt(), anyInt() );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getRowsAsStringsWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getRowsAsStringsAsync( resourceName, version, numRows );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getRowsAsStringsWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getRowsAsStringsAsync( resourceName, pageSize, numRows );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getRowsAsStringsWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getRows( resourceName, version, pageSize, numRows );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getRowsAsStringsAsync( resourceName, version, pageSize, numRows );
        List<String> responseList = completableFuture.join();
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( resourceName, version, pageSize, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getRowsAsJsonNodesTest() throws IOException {
        String resourceName = "someResource";
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClientAsync).getRows( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getRowsAsJsonNodesAsync( resourceName, numRows );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( anyString(), anyString(), anyInt(), anyInt() );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }


    @Test
    void getRowsAsJsonNodesWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClientAsync).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getRowsAsJsonNodesAsync( resourceName, version, numRows );
        List<JsonNode> responseList = completableFuture.join();
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }


    @Test
    void getRowsAsJsonNodesWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClientAsync).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getRowsAsJsonNodesAsync( resourceName, pageSize, numRows );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }

    @Test
    void getRowsAsJsonNodesWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClientAsync).getRows( resourceName, version, pageSize, numRows );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getRowsAsJsonNodesAsync( resourceName, version, pageSize, numRows );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( resourceName, version, pageSize, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }


    @Test
    void getRowsFromOffsetAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset(
                anyString(), anyString(), anyInt(), anyInt(), anyInt()
        );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsync( resourceName, offset, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset(
                anyString(), anyString(), anyInt(), anyInt(), anyInt()
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsFromOffsetWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numRows );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsync( resourceName, pageSize, offset, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsFromOffsetWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsync( resourceName, version, offset, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsFromOffsetWithVersionAndPageSizeReturnsEmptyListAsyncTest() throws IOException {
        String resourceName = null;
        String version = "someVersion";
        int pageSize = 17;
        int offset = 25;
        int numRows = 50;
        // Run the test with a null resourceName string.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsync( resourceName, version, pageSize, offset, numRows );
        List<EthosResponse> responseList = completableFuture.join();
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsync( resourceName, version, pageSize, offset, numRows );
        responseList = completableFuture.join();
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );
    }

    @Test
    void getRowsFromOffsetWithVersionAndPageSizeWhenOffsetAndNumRowsAreNegativeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = -1;
        int numRows = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPages( resourceName, version, pageSize );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsync( resourceName, version, pageSize, offset, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsFromOffsetWithVersionAndPageSizeWhenOffsetIsNegativeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = -1;
        int numRows = 40;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getRows( resourceName, version, pageSize, numRows );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsync( resourceName, version, pageSize, offset, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRows( resourceName, version, pageSize, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsFromOffsetWithVersionAndPageSizeWhenNumRowsIsNegativeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numRows = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsync( resourceName, version, pageSize, offset, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getRowsFromOffsetWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = 50;
        int pageSize = 17;
        int offset = 25;

        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the handlePaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset(
                resourceName, version, pageSize, offset, numRows
        );
        // Run the test.
        CompletableFuture<List<EthosResponse>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsync( resourceName, version, pageSize, offset, numRows );
        List<EthosResponse> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset(
                resourceName, version, pageSize, offset, numRows);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsFromOffsetAsStringsAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset( anyString(), anyString(), anyInt(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsStringsAsync( resourceName, offset, numRows );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset(
                anyString(), anyString(), anyInt(), anyInt(), anyInt()
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getRowsFromOffsetAsStringsWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset(
                resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numRows
        );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsStringsAsync( resourceName, pageSize, offset, numRows );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getRowsFromOffsetAsStringsWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset(
                resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows
        );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsStringsAsync( resourceName, version, offset, numRows );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getRowsFromOffsetAsStringsWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int pageSize = 17;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        // Run the test.
        CompletableFuture<List<String>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsStringsAsync( resourceName, version, pageSize, offset, numRows );
        List<String> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getRowsFromOffsetAsJsonNodesAsyncTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int numRows = 50;
        List<EthosResponse> mockedEthosResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedEthosResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset(
                anyString(), anyString(), anyInt(), anyInt()
        );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsJsonNodesAsync( resourceName, offset, numRows );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset(
                anyString(), anyString(), anyInt(), anyInt()
        );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }


    @Test
    void getRowsFromOffsetAsJsonNodesWithPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int offset = 25;
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsJsonNodesAsync( resourceName, pageSize, offset, numRows );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }

    @Test
    void getRowsFromOffsetAsJsonNodesWithVersionAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsJsonNodesAsync( resourceName, version, offset, numRows );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Ensure the responseList matches the expectedResponseList.
//        evaluateJsonNodeListResult( responseList, expectedResponseList );
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }


    @Test
    void getRowsFromOffsetAsJsonNodesWithVersionAndPageSizeAsyncTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int pageSize = 17;
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClientAsync).getRowsFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Run the test.
        CompletableFuture<List<JsonNode>> completableFuture = spyEthosProxyClientAsync.getRowsFromOffsetAsJsonNodesAsync( resourceName, version, pageSize, offset, numRows );
        List<JsonNode> responseList = completableFuture.join();

        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClientAsync, Mockito.times(1)).getRowsFromOffset( anyString(), anyString(), anyInt(), anyInt() );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }

}
