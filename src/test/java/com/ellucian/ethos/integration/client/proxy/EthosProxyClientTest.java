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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class EthosProxyClientTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private EthosProxyClient spyEthosProxyClient;

    // ==========================================================================
    // Methods
    // ==========================================================================

    @BeforeEach
    void setup() throws IOException {
        // Not using the EthosClientFactory to build the EthosProxyClient because we have to spy it with Mockito.
        spyEthosProxyClient = spy( new EthosProxyClient("11111111-1111-1111-1111-111111111111", null, null, null) );
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
    void postTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String requestBody = "someRequestBody";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).post(resourceName, EthosProxyClient.DEFAULT_VERSION, requestBody );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.post( resourceName, requestBody );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).post(resourceName, EthosProxyClient.DEFAULT_VERSION, requestBody );
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void postWithInvalidInputTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = null;
            spyEthosProxyClient.post( null, "someVersion", requestBody );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = "";
            spyEthosProxyClient.post( "", "someVersion", requestBody );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = null;
            spyEthosProxyClient.post( "resourceName", "someVersion", requestBody );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = "";
            spyEthosProxyClient.post( "resourceName", "someVersion", requestBody );
        });
    }

    @Test
    void postWithVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String requestBody = "someRequestBody";
        String version = "someVersion";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).post( anyString(), anyMap(), anyString() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.post( resourceName, version, requestBody );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).post( anyString(), anyMap(), anyString() );
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void postWithJsonNodeTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        JsonNode requestBodyNode = null;
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).post(resourceName, EthosProxyClient.DEFAULT_VERSION, requestBodyNode );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.post( resourceName, requestBodyNode );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).post(resourceName, EthosProxyClient.DEFAULT_VERSION, requestBodyNode );
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void postWithJsonNodeWithInvalidInputTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            JsonNode requestBodyNode = null;
            spyEthosProxyClient.post( null, "someVersion", requestBodyNode );
        });
    }

    @Test
    void postWithVersionAndJsonNodeTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String version = "someVersion";
        JsonNode requestBodyNode = JsonLoader.fromString("{\"jsonLabel\": \"jsonValue\"}");
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).post(resourceName, version, requestBodyNode.toString() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.post( resourceName, version, requestBodyNode );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).post(resourceName, version, requestBodyNode.toString() );
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void putTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String resourceId = "someResourceId";
        String requestBody = "someRequestBody";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).put(resourceName, resourceId, EthosProxyClient.DEFAULT_VERSION, requestBody );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.put( resourceName, resourceId, requestBody );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).put(resourceName, resourceId, EthosProxyClient.DEFAULT_VERSION, requestBody );
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void putWithInvalidInputTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = null;
            spyEthosProxyClient.put( null, "someId","someVersion", requestBody );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = "";
            spyEthosProxyClient.put( "", "someId","someVersion", requestBody );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = null;
            spyEthosProxyClient.put( "resourceName", "someId", "someVersion", requestBody );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = "";
            spyEthosProxyClient.put( "resourceName", "someId", "someVersion", requestBody );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = null;
            spyEthosProxyClient.put( "resourceName", null, "someVersion", requestBody );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = "";
            spyEthosProxyClient.put( "resourceName", null, "someVersion", requestBody );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = null;
            spyEthosProxyClient.put( "resourceName", "", "someVersion", requestBody );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String requestBody = "";
            spyEthosProxyClient.put( "resourceName", "", "someVersion", requestBody );
        });
    }

    @Test
    void putWithVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String resourceId = "someResourceId";
        String requestBody = "someRequestBody";
        String version = "someVersion";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).put( anyString(), anyMap(), anyString() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.put( resourceName, resourceId, version, requestBody );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).put( anyString(), anyMap(), anyString() );
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void putWithJsonNodeTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String resourceId = "someResourceId";
        JsonNode requestBodyNode = null;
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).put(resourceName, resourceId, EthosProxyClient.DEFAULT_VERSION, requestBodyNode );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.put( resourceName, resourceId, requestBodyNode );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).put(resourceName, resourceId, EthosProxyClient.DEFAULT_VERSION, requestBodyNode );
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void putWithJsonNodeWithInvalidInputTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            JsonNode requestBodyNode = null;
            spyEthosProxyClient.put( null, "someResourceId", "someVersion", requestBodyNode );
        });
    }

    @Test
    void putWithVersionAndJsonNodeTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String resourceId = "someResourceId";
        String version = "someVersion";
        JsonNode requestBodyNode = JsonLoader.fromString("{\"jsonLabel\": \"jsonValue\"}");
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).put(resourceName, resourceId, version, requestBodyNode.toString() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.put( resourceName, resourceId, version, requestBodyNode );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).put(resourceName, resourceId, version, requestBodyNode.toString() );
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void deleteWithInvalidInputTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            String id = null;
            spyEthosProxyClient.delete( null, id );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String id = "";
            spyEthosProxyClient.delete( "", id );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String id = null;
            spyEthosProxyClient.delete( "resourceName", id );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String id = "";
            spyEthosProxyClient.delete( "resourceName", id );
        });
    }

    @Test
    void deleteTest() throws IOException {
        String resourceName = "someResource";
        String id = "someId";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doNothing().when(spyEthosProxyClient).delete( anyString(), anyMap() );
        // Run the test.
        spyEthosProxyClient.delete( resourceName, id );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).delete( anyString(), anyMap() );
    }

    @Test
    void getResourceTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(resourceName, EthosProxyClient.DEFAULT_VERSION );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.get( resourceName );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyString());
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void getResourceWithVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyMap());
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.get( "someResource", "someVersion" );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyMap());
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void getResourceAsStringTest() throws IOException {
        String resourceName = "someResource";
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(resourceName );
        // Run the test.
        String response = spyEthosProxyClient.getAsString( resourceName );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(resourceName);
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.equals(expectedResponse.getContent()) );
    }

    @Test
    void getResourceAsStringWithVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String version = "someVersion";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(resourceName, version );
        // Run the test.
        String response = spyEthosProxyClient.getAsString( resourceName, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(resourceName, version);
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.equals(expectedResponse.getContent()) );
    }

    @Test
    void getResourceAsJsonNodeTest() throws IOException {
        String resourceName = "someResource";
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(resourceName );
        // Run the test.
        JsonNode jsonNode = spyEthosProxyClient.getAsJsonNode( resourceName );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(resourceName);
        // Ensure the result matches the test ethos response.
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.toString() != null );
        assertTrue( jsonNode.toString().equals(expectedResponse.getContent()) );
    }

    @Test
    void getResourceAsJsonNodeWithVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String version = "someVersion";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(resourceName, version );
        // Run the test.
        JsonNode jsonNode = spyEthosProxyClient.getAsJsonNode( resourceName, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(resourceName, version);
        // Ensure the result matches the test ethos response.
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.toString() != null );
        assertTrue( jsonNode.toString().equals(expectedResponse.getContent()) );
    }

    @Test
    void getResourceWithOffsetAndPageSizeTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.get( "someResource", 20, 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void getResourceWithVersionOffsetAndPageSizeTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyMap());
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.get( "someResource", "someVersion", 20, 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyMap());
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void getResourceAsStringWithOffsetAndPageSizeTest() throws IOException {
        String expectedResponse = "someResponse";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getAsString(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        String response = spyEthosProxyClient.getAsString( "someResource", 20, 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAsString(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.equals(expectedResponse) );
    }

    @Test
    void getResourceAsStringWithVersionOffsetAndPageSizeTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        String response = spyEthosProxyClient.getAsString( "someResource", "someVersion", 20, 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.equals(expectedResponse.getContent()) );
    }

    @Test
    void getResourceAsJsonNodeWithOffsetAndPageSizeTest() throws IOException {
        JsonNode expectedResponse = buildJsonNodeResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getAsJsonNode(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        JsonNode jsonNode = spyEthosProxyClient.getAsJsonNode( "someResource", 20, 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAsJsonNode(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.toString() != null );
        assertTrue( jsonNode.toString().equals(expectedResponse.toString()) );
    }

    @Test
    void getResourceAsJsonNodeWithVersionOffsetAndPageSizeTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        JsonNode jsonNode = spyEthosProxyClient.getAsJsonNode( "someResource", "someVersion", 20, 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.toString() != null );
        assertTrue( jsonNode.toString().equals(expectedResponse.getContent()) );
    }

    @Test
    void getResourceFromOffsetWithOffsetTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getFromOffset(anyString(), anyString(), anyInt());
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.getFromOffset( "someResource", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getFromOffset(anyString(), anyString(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void getResourceFromOffsetWithVersionOffsetTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.getFromOffset( "someResource", "someVersion", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void getResourceFromOffsetAsStringTest() throws IOException {
        String expectedResponse = "someResponse";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getFromOffsetAsString(anyString(), anyString(), anyInt());
        // Run the test.
        String response = spyEthosProxyClient.getFromOffsetAsString( "someResource", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getFromOffsetAsString(anyString(), anyString(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.equals(expectedResponse) );
    }

    @Test
    void getResourceFromOffsetAsStringWithVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        String response = spyEthosProxyClient.getFromOffsetAsString( "someResource", "someVersion", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.equals(expectedResponse.getContent()) );
    }

    @Test
    void getResourceFromOffsetAsJsonNodeTest() throws IOException {
        JsonNode expectedResponse = buildJsonNodeResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getFromOffsetAsJsonNode(anyString(), anyString(), anyInt());
        // Run the test.
        JsonNode response = spyEthosProxyClient.getFromOffsetAsJsonNode( "someResource", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getFromOffsetAsJsonNode(anyString(), anyString(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.toString().equals(expectedResponse.toString()) );
    }

    @Test
    void getResourceFromOffsetAsJsonNodeWithVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        JsonNode response = spyEthosProxyClient.getFromOffsetAsJsonNode( "someResource", "someVersion", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.toString().equals(expectedResponse.getContent()) );
    }

    @Test
    void getResourceWithPageSizeTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getWithPageSize(anyString(), anyString(), anyInt());
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.getWithPageSize( "someResource", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getWithPageSize(anyString(), anyString(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void getResourceWithPageSizeAndVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.getWithPageSize( "someResource", "someVersion", 20 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void getResourceWithPageSizeAsStringTest() throws IOException {
        String expectedResponse = "someResponse";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getWithPageSizeAsString(anyString(), anyString(), anyInt());
        // Run the test.
        String response = spyEthosProxyClient.getWithPageSizeAsString( "someResource", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getWithPageSizeAsString(anyString(), anyString(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.equals(expectedResponse) );
    }

    @Test
    void getResourceWithPageSizeAsStringWithVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        String response = spyEthosProxyClient.getWithPageSizeAsString( "someResource", "someVersion", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.equals(expectedResponse.getContent()) );
    }

    @Test
    void getResourceWithPageSizeAsJsonNodeTest() throws IOException {
        JsonNode expectedResponse = buildJsonNodeResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getWithPageSizeAsJsonNode(anyString(), anyString(), anyInt());
        // Run the test.
        JsonNode response = spyEthosProxyClient.getWithPageSizeAsJsonNode( "someResource", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getWithPageSizeAsJsonNode(anyString(), anyString(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.toString().equals(expectedResponse.toString()) );
    }

    @Test
    void getResourceWithPageSizeAsJsonNodeWithVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyString(), anyInt(), anyInt());
        // Run the test.
        JsonNode response = spyEthosProxyClient.getWithPageSizeAsJsonNode( "someResource", "someVersion", 30 );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyString(), anyInt(), anyInt());
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.toString().equals(expectedResponse.getContent()) );
    }

    @Test
    void getAllPagesTest() throws IOException {
        String resourceName = "someResource";
        List<EthosResponse> expectedResponseList = buildEthosResponseList();
        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPages( resourceName );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the result matches the test ethos response.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        List<EthosResponse> expectedResponseList = buildEthosResponseList();
        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPages( resourceName, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();
        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPages( resourceName, pageSize );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesWithVersionAndPageSizeReturnsEmptyListTest() throws IOException {
        String resourceName = null;
        String version = "someVersion";
        int pageSize = 17;
        // Run the test with a null resourceName string.
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPages( resourceName, version, pageSize );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        responseList = spyEthosProxyClient.getAllPages( resourceName, version, pageSize );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );
    }

    @Test
    void getAllPagesWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int totalCount = 100;
        EthosResponse testEthosResponse = buildEthosResponse();
        List<EthosResponse> expectedResponseList = buildEthosResponseList();
        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .build();
        pager.setTotalCount( totalCount );
        pager.setEthosResponse( testEthosResponse );
        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(pager).when(spyEthosProxyClient).prepareForPaging( any() );
        // Return the testResponseList when the method under test calls the handlePaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).handlePaging( any() );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPages( resourceName, version, pageSize );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).prepareForPaging(any());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).shouldDoPaging(any(), anyBoolean());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).handlePaging(any());
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesAsStringsTest() throws IOException {
        String resourceName = "someResource";
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getAllPages( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getAllPagesAsStrings( resourceName );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getAllPagesAsStringsWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getAllPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getAllPagesAsStrings( resourceName, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getAllPagesAsStringsWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getAllPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getAllPagesAsStrings( resourceName, pageSize );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getAllPagesAsStringsWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getAllPages( resourceName, version, pageSize );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getAllPagesAsStrings( resourceName, version, pageSize );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getAllPagesAsJsonNodesTest() throws IOException {
        String resourceName = "someResource";
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getAllPagesAsJsonNodes( resourceName );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesAsJsonNodesWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getAllPagesAsJsonNodes( resourceName, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesAsJsonNodesWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getAllPagesAsJsonNodes( resourceName, pageSize );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesAsJsonNodesWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, version, pageSize );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getAllPagesAsJsonNodes( resourceName, version, pageSize );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPagesFromOffset( resourceName, offset );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, offset, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPagesFromOffset( resourceName, offset, pageSize );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetWithVersionSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPagesFromOffset( resourceName, version, offset );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE);
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
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPagesFromOffset( resourceName, version, offset, pageSize );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        responseList = spyEthosProxyClient.getAllPagesFromOffset( resourceName, version, offset, pageSize );
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
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, version, pageSize );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int pageSize = 17;
        int totalCount = 100;
        EthosResponse testEthosResponse = buildEthosResponse();
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        Pager pager = new Pager.Builder(resourceName)
                .forVersion(version)
                .withPageSize(pageSize)
                .fromOffset(offset)
                .build();
        pager.setTotalCount( totalCount );
        pager.setEthosResponse( testEthosResponse );
        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(pager).when(spyEthosProxyClient).prepareForPaging( any() );
        // Return the testResponseList when the method under test calls the handlePaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).handlePaging( any() );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).prepareForPaging(any());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).shouldDoPaging(any(), anyBoolean());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).handlePaging(any());
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsStringsTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResponseList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getAllPagesFromOffsetAsStrings( resourceName, offset );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, offset, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsStringsWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResponseList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getAllPagesFromOffsetAsStrings( resourceName, offset, pageSize );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsStringsWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResponseList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getAllPagesFromOffsetAsStrings( resourceName, version, offset );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsStringsWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int pageSize = 17;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResponseList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getAllPagesFromOffsetAsStrings( resourceName, version, offset, pageSize );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, version, offset, pageSize );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsJsonNodesTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getAllPagesFromOffsetAsJsonNodes( resourceName, offset );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, offset, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getAllPagesFromOffsetAsJsonNodesWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getAllPagesFromOffsetAsJsonNodes( resourceName, offset, pageSize );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, EthosProxyClient.DEFAULT_VERSION, offset, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getAllPagesFromOffsetAsJsonNodesWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getAllPagesFromOffsetAsJsonNodes( resourceName, version, offset );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, version, offset, EthosProxyClient.DEFAULT_PAGE_SIZE);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getAllPagesFromOffsetAsJsonNodesWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getAllPagesFromOffsetAsJsonNodes( resourceName, version, offset, pageSize );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset(resourceName, version, offset, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesTest() throws IOException {
        String resourceName = "someResource";
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, numPages );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPages( resourceName, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPages( resourceName, version, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numPages );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPages( resourceName, pageSize, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesWithVersionAndPageSizeReturnsEmptyListTest() throws IOException {
        String resourceName = null;
        String version = "someVersion";
        int pageSize = 17;
        int numPages = 40;
        // Run the test with a null resourceName string.
        List<EthosResponse> responseList = spyEthosProxyClient.getPages( resourceName, version, pageSize, numPages );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        responseList = spyEthosProxyClient.getPages( resourceName, version, pageSize, numPages );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );
    }


    @Test
    void getPagesWithVersionAndPageSizeWhenNumPagesIsNegativeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, version, pageSize );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPages( resourceName, version, pageSize, numPages );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getPagesWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = 25;
        int pageSize = 17;
        int totalCount = 100;
        EthosResponse testEthosResponse = buildEthosResponse();
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .forNumPages(numPages)
                               .build();
        pager.setTotalCount( totalCount );
        pager.setEthosResponse( testEthosResponse );
        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(pager).when(spyEthosProxyClient).prepareForPaging( any() );
        // Return the testResponseList when the method under test calls the handlePaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).handlePaging( any() );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPages( resourceName, version, pageSize, numPages );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).prepareForPaging(any());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).shouldDoPaging(any(), anyBoolean());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).handlePaging(any());
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesAsStringsTest() throws IOException {
        String resourceName = "someResource";
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, numPages );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getPagesAsStrings( resourceName, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesAsStringsWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getPagesAsStrings( resourceName, version, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesAsStringsWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numPages );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getPagesAsStrings( resourceName, pageSize, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesAsStringsWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getPages( resourceName, version, pageSize, numPages );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getPagesAsStrings( resourceName, version, pageSize, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, version, pageSize, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesAsJsonNodesTest() throws IOException {
        String resourceName = "someResource";
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, numPages );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getPagesAsJsonNodes( resourceName, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages(resourceName, EthosProxyClient.DEFAULT_VERSION, numPages);
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesAsJsonNodesWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getPagesAsJsonNodes( resourceName, version, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numPages );
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
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize , numPages );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getPagesAsJsonNodes( resourceName, pageSize, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize , numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesAsJsonNodesWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPages( resourceName, version, pageSize , numPages );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getPagesAsJsonNodes( resourceName, version, pageSize, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, version, pageSize , numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getPagesFromOffsetTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset, numPages );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPagesFromOffset( resourceName, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset, numPages );
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
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numPages );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPagesFromOffset( resourceName, pageSize, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testResponseList when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numPages );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPagesFromOffset( resourceName, version, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithVersionAndPageSizeReturnsEmptyListTest() throws IOException {
        String resourceName = null;
        String version = "someVersion";
        int pageSize = 17;
        int offset = 25;
        int numPages = 40;
        // Run the test with a null resourceName string.
        List<EthosResponse> responseList = spyEthosProxyClient.getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        responseList = spyEthosProxyClient.getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );
    }


    @Test
    void getPagesFromOffsetWithVersionAndPageSizeWhenOffsetAndNumPagesAreNegativeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = -1;
        int numPages = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, version, pageSize );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithVersionAndPageSizeWhenOffsetIsNegativeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = -1;
        int numPages = 40;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPages( resourceName, version, pageSize, numPages );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPages( resourceName, version, pageSize, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithVersionAndPageSizeWhenNumPagesIsNegativeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numPages = 40;
        int pageSize = 17;
        int offset = 25;
        int totalCount = 100;
        EthosResponse testEthosResponse = buildEthosResponse();
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        Pager pager = new Pager.Builder(resourceName)
                .forVersion(version)
                .withPageSize(pageSize)
                .fromOffset(offset)
                .forNumPages(numPages)
                .build();
        pager.setTotalCount( totalCount );
        pager.setEthosResponse( testEthosResponse );
        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(pager).when(spyEthosProxyClient).prepareForPaging( any() );
        // Return the testResponseList when the method under test calls the handlePaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).handlePaging( any() );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).prepareForPaging(any());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).shouldDoPaging(any(), anyBoolean());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).handlePaging(any());
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetAsStringsTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset, numPages );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getPagesFromOffsetAsStrings( resourceName, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getPagesFromOffsetAsStringsWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numPages );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getPagesFromOffsetAsStrings( resourceName, pageSize, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesFromOffsetAsStringsWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numPages );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getPagesFromOffsetAsStrings( resourceName, version, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numPages );
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
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getPagesFromOffsetAsStrings( resourceName, version, pageSize, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getPagesFromOffsetAsJsonNodesTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset , numPages );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getPagesFromOffsetAsJsonNodes( resourceName, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, offset , numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetAsJsonNodesWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset , numPages );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getPagesFromOffsetAsJsonNodes( resourceName, pageSize, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset , numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getPagesFromOffsetAsJsonNodesWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset , numPages );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getPagesFromOffsetAsJsonNodes( resourceName, version, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset , numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }

    @Test
    void getPagesFromOffsetAsJsonNodesWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int offset = 25;
        int numPages = 40;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getPagesFromOffsetAsJsonNodes( resourceName, version, pageSize, offset, numPages );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        // Ensure the responseList matches the expectedResponseList.
        evaluateJsonNodeListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsTest() throws IOException {
        String resourceName = "someResource";
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, numRows );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRows( resourceName, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRows( resourceName, version, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getRowsWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRows( resourceName, pageSize, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getRowsWithVersionAndPageSizeReturnsEmptyListTest() throws IOException {
        String resourceName = null;
        String version = "someVersion";
        int pageSize = 17;
        int numRows = 50;
        // Run the test with a null resourceName string.
        List<EthosResponse> responseList = spyEthosProxyClient.getRows( resourceName, version, pageSize, numRows );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        responseList = spyEthosProxyClient.getRows( resourceName, version, pageSize, numRows );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );
    }

    @Test
    void getRowsWithVersionAndPageSizeWhenNumRowsIsNegativeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, version, pageSize );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRows( resourceName, version, pageSize, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = 50;
        int pageSize = 17;
        int totalCount = 100;
        EthosResponse testEthosResponse = buildEthosResponse();
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        Pager pager = new Pager.Builder(resourceName)
                .forVersion(version)
                .withPageSize(pageSize)
                .forNumRows(numRows)
                .build();
        pager.setTotalCount( totalCount );
        pager.setEthosResponse( testEthosResponse );
        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(pager).when(spyEthosProxyClient).prepareForPaging( any() );
        // Return the testResponseList when the method under test calls the handlePaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).handlePaging( any() );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRows( resourceName, version, pageSize, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).prepareForPaging(any());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).shouldDoPaging(any(), anyBoolean());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).handlePaging(any());
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getRowsAsStringsTest() throws IOException {
        String resourceName = "someResource";
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, numRows );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getRowsAsStrings( resourceName, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getRowsAsStringsWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getRowsAsStrings( resourceName, version, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getRowsAsStringsWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getRowsAsStrings( resourceName, pageSize, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
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
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getRows( resourceName, version, pageSize, numRows );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getRowsAsStrings( resourceName, version, pageSize, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, version, pageSize, numRows );
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
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClient).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, numRows );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getRowsAsJsonNodes( resourceName, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }


    @Test
    void getRowsAsJsonNodesWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClient).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getRowsAsJsonNodes( resourceName, version, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }


    @Test
    void getRowsAsJsonNodesWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClient).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getRowsAsJsonNodes( resourceName, pageSize, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }

    @Test
    void getRowsAsJsonNodesWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClient).getRows( resourceName, version, pageSize, numRows );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getRowsAsJsonNodes( resourceName, version, pageSize, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, version, pageSize, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }


    @Test
    void getRowsFromOffsetTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRowsFromOffset( resourceName, offset, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
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
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numRows );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRowsFromOffset( resourceName, pageSize, offset, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsFromOffsetWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numRows = 50;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRowsFromOffset( resourceName, version, offset, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsFromOffsetWithVersionAndPageSizeReturnsEmptyListTest() throws IOException {
        String resourceName = null;
        String version = "someVersion";
        int pageSize = 17;
        int offset = 25;
        int numRows = 50;
        // Run the test with a null resourceName string.
        List<EthosResponse> responseList = spyEthosProxyClient.getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );

        // Run the test again with an empty resourceName string..
        resourceName = "";
        responseList = spyEthosProxyClient.getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        assertTrue( responseList != null );
        assertTrue( responseList.isEmpty() );
    }

    @Test
    void getRowsFromOffsetWithVersionAndPageSizeWhenOffsetAndNumRowsAreNegativeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = -1;
        int numRows = -1;
        int pageSize = 17;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the expected object when mocking the given method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPages( resourceName, version, pageSize );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPages(resourceName, version, pageSize);
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
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getRows( resourceName, version, pageSize, numRows );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRows( resourceName, version, pageSize, numRows );
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
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getAllPagesFromOffset( resourceName, version, offset, pageSize );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void getRowsFromOffsetWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int numRows = 50;
        int pageSize = 17;
        int offset = 25;
        int totalCount = 100;
        EthosResponse testEthosResponse = buildEthosResponse();
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .fromOffset(offset)
                               .forNumRows(numRows)
                               .build();
        pager.setTotalCount( totalCount );
        pager.setEthosResponse( testEthosResponse );
        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(pager).when(spyEthosProxyClient).prepareForPaging( any() );
        // Return the testResponseList when the method under test calls the handlePaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).handlePaging( any() );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).prepareForPaging(any());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).shouldDoPaging(any(), anyBoolean());
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).handlePaging(any());
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void getRowsFromOffsetAsStringsTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getRowsFromOffsetAsStrings( resourceName, offset, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getRowsFromOffsetAsStringsWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int pageSize = 17;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numRows );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getRowsFromOffsetAsStrings( resourceName, pageSize, offset, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getRowsFromOffsetAsStringsWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getRowsFromOffsetAsStrings( resourceName, version, offset, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }


    @Test
    void getRowsFromOffsetAsStringsWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int pageSize = 17;
        int numRows = 40;
        List<EthosResponse> testResponseList = buildEthosResponseList();
        List<String> expectedResultList = buildRowBasedStringResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(testResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        // Run the test.
        List<String> responseList = spyEthosProxyClient.getRowsFromOffsetAsStrings( resourceName, version, pageSize, offset, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateStringListResult( responseList, expectedResultList );
    }

    @Test
    void getRowsFromOffsetAsJsonNodesTest() throws IOException {
        String resourceName = "someResource";
        int offset = 25;
        int numRows = 50;
        List<EthosResponse> mockedEthosResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedEthosResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getRowsFromOffsetAsJsonNodes( resourceName, offset, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }


    @Test
    void getRowsFromOffsetAsJsonNodesWithPageSizeTest() throws IOException {
        String resourceName = "someResource";
        int pageSize = 17;
        int offset = 25;
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numRows );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getRowsFromOffsetAsJsonNodes( resourceName, pageSize, offset, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, EthosProxyClient.DEFAULT_VERSION, pageSize, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }

    @Test
    void getRowsFromOffsetAsJsonNodesWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getRowsFromOffsetAsJsonNodes( resourceName, version, offset, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, version, EthosProxyClient.DEFAULT_PAGE_SIZE, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }


    @Test
    void getRowsFromOffsetAsJsonNodesWithVersionAndPageSizeTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int offset = 25;
        int pageSize = 17;
        int numRows = 50;
        List<EthosResponse> mockedResponseList = buildEthosResponseList();
        List<JsonNode> expectedJsonNodeResponseList = buildRowBasedJsonNodeResponseList();

        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(mockedResponseList).when(spyEthosProxyClient).getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        // Run the test.
        List<JsonNode> responseList = spyEthosProxyClient.getRowsFromOffsetAsJsonNodes( resourceName, version, pageSize, offset, numRows );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateRowBasedJsonNodeListResult( responseList, expectedJsonNodeResponseList );
    }

    @Test
    void getResourceByIdTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String id = "someGUID";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getById(resourceName, id, EthosProxyClient.DEFAULT_VERSION );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.getById( resourceName, id );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getById(resourceName, id, EthosProxyClient.DEFAULT_VERSION );
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void getResourceByIdWithVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resourceName = "someResource";
        String id = "someGUID";
        String version = "someVersion";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get(anyString(), anyMap() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosProxyClient.getById( resourceName, id, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(anyString(), anyMap() );
        // Ensure the result matches the test ethos response.
        assertTrue( ethosResponse.getContent() != null );
        assertTrue( ethosResponse.getContent().equals(expectedResponse.getContent()) );
        assertTrue( ethosResponse.getHttpStatusCode() == expectedResponse.getHttpStatusCode() );
    }

    @Test
    void getByIdAsStringTest() throws IOException {
        String resourceName = "someResource";
        String id = "someGUID";
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getById( resourceName, id );
        // Run the test.
        String response = spyEthosProxyClient.getByIdAsString( resourceName, id );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getById( resourceName, id );
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.equals(expectedResponse.getContent()) );
    }

    @Test
    void getByIdAsStringWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String id = "someGUID";
        String version = "someVersion";
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getById( resourceName, id, version );
        // Run the test.
        String response = spyEthosProxyClient.getByIdAsString( resourceName, id, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getById( resourceName, id, version );
        // Ensure the result matches the test ethos response.
        assertTrue( response != null );
        assertTrue( response.equals(expectedResponse.getContent()) );
    }

    @Test
    void getByIdAsJsonNodeTest() throws IOException {
        String resourceName = "someResource";
        String id = "someGUID";
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getById( resourceName, id );
        // Run the test.
        JsonNode jsonNode = spyEthosProxyClient.getByIdAsJsonNode( resourceName, id );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getById( resourceName, id );
        // Ensure the result matches the test ethos response.
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.toString() != null );
        assertTrue( jsonNode.toString().equals(expectedResponse.getContent()) );
    }

    @Test
    void getByIdAsJsonNodeWithVersionTest() throws IOException {
        String resourceName = "someResource";
        String id = "someGUID";
        String version = "someVersion";
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).getById( resourceName, id, version );
        // Run the test.
        JsonNode jsonNode = spyEthosProxyClient.getByIdAsJsonNode( resourceName, id, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getById( resourceName, id, version );
        // Ensure the result matches the test ethos response.
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.toString() != null );
        assertTrue( jsonNode.toString().equals(expectedResponse.getContent()) );
    }

    @Test
    void prepareForPagingTest() throws IOException {
        String resourceName = "someResource";
        String version = null;
        int pageSize = -1;
        int offset = -1;
        HashMap<String, Header> headerMap = new HashMap<>();
        headerMap.put(EthosProxyClient.HDR_X_TOTAL_COUNT, new Header() {
            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }

            @Override
            public String getName() {
                return EthosProxyClient.HDR_X_TOTAL_COUNT;
            }

            @Override
            public String getValue() {
                return "100";
            }
        });
        EthosResponse testEthosResponse = new EthosResponse( headerMap, "{\"someLabel\":\"someValue\"}", 200 );

        // Run the test with a null pager.
        Pager resultPager = spyEthosProxyClient.prepareForPaging( null );
        assertTrue( resultPager == null);

        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .fromOffset(offset)
                               .build();
        // Return the expected object when mocking the given method.
        Mockito.doReturn(testEthosResponse).when(spyEthosProxyClient).get( resourceName, EthosProxyClient.DEFAULT_VERSION );
        Mockito.doReturn(40).when(spyEthosProxyClient).getPageSize( anyString(), anyString(), any() );
        // Run the test.
        resultPager = spyEthosProxyClient.prepareForPaging( pager );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get(resourceName, EthosProxyClient.DEFAULT_VERSION);
        // Evaluate the results.
        assertTrue( resultPager.getVersion() != null );
        assertTrue( pager.getVersion().equals(EthosProxyClient.DEFAULT_VERSION) );
        assertTrue( resultPager.getOffset() == 0 );
        assertTrue( resultPager.getPageSize() == 40 );
        assertTrue( resultPager.getTotalCount() == 100 );
        assertTrue( resultPager.getEthosResponse() == testEthosResponse );
    }

    @Test
    void shouldDoPagingTest() {
        String resourceName = "someResource";
        String version = "someVersion";
        int pageSize = 17;
        int totalCount = 100;
        int numRows = 200;
        // Test without numRows.
        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .withTotalCount(totalCount)
                               .build();
        // Run the test
        Pager resultPager = spyEthosProxyClient.shouldDoPaging( pager, false );
        assertTrue( resultPager != null );
        assertTrue( resultPager.isShouldDoPaging() == true );

        // Test with numRows.
        pager = new Pager.Builder(resourceName)
                        .forVersion(version)
                        .withPageSize(pageSize)
                        .withTotalCount(totalCount)
                        .forNumRows(numRows)
                        .build();

        // Run the test again
        resultPager = spyEthosProxyClient.shouldDoPaging( pager, true );
        assertTrue( resultPager != null );
        assertTrue( resultPager.isShouldDoPaging() == true );

    }

    @Test
    void needToPageWithResourceContentTest() throws JsonProcessingException {
        String resourceContent = "[{\"someLabel\":\"someValue\"}]";
        int totalCount = 5;
        // Run the test
        boolean result = spyEthosProxyClient.needToPage( resourceContent, totalCount );
        assertTrue( result == true );
    }

    @Test
    void needToPageWithJsonNodeTest() throws JsonProcessingException {
        String resourceContent = "[{\"someLabel\":\"someValue\"}]";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree( resourceContent );
        int totalCount = 5;
        // Run the test
        boolean result = spyEthosProxyClient.needToPage( jsonNode, totalCount );
        assertTrue( result == true );
    }

    @Test
    void needToPageTest() throws JsonProcessingException {
        int pageSize = 17;
        int totalCount = 50;
        // Run the test
        boolean result = spyEthosProxyClient.needToPage( pageSize, totalCount );
        assertTrue( result == true );

        pageSize = 50;
        totalCount = 17;
        // Run the test again
        result = spyEthosProxyClient.needToPage( pageSize, totalCount );
        assertTrue( result == false );
    }

    @Test
    void handlePagingTest() throws IOException {
        Pager pager = new Pager();
        pager.setShouldDoPaging( true );
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getDataFromPaging( pager );

        // Run the test
        List<EthosResponse> resultResponseList = spyEthosProxyClient.handlePaging( pager );
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getDataFromPaging(pager);
        assertTrue( resultResponseList != null );
        assertTrue( resultResponseList.size() == expectedResponseList.size() );

        // Run the test again
        expectedResponseList.clear();
        expectedResponseList.add( buildEthosResponse() );
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).getDataFromInitialContent( pager );
        pager.setShouldDoPaging( false );
        resultResponseList = spyEthosProxyClient.handlePaging( pager );
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getDataFromInitialContent(pager);
        assertTrue( resultResponseList != null );
        assertTrue( resultResponseList.size() == expectedResponseList.size() );
    }

    @Test
    void getDataFromPagingTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponseList();
        Pager pager = new Pager.Builder("someResource")
                               .forVersion("someVersion")
                               .forNumRows(40)
                               .forNumPages(30)
                               .fromOffset(25)
                               .withPageSize(17)
                               .withTotalCount(100)
                               .build();
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).doPagingForAll( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize() );
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).doPagingForNumPages( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getNumPages() );
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).doPagingFromOffset( pager.getResourceName(), pager.getVersion(), pager.getCriteriaFilter(), pager.getTotalCount(), pager.getPageSize(), pager.getOffset() );
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).doPagingFromOffsetForNumPages( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getNumPages(), pager.getOffset() );
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).doPagingForNumRows( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getNumRows() );
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).doPagingFromOffsetForNumRows( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getOffset(), pager.getNumRows() );

        // Run the tests for each PagerType.
        pager.setHowToPage(Pager.PagingType.PAGE_ALL_PAGES);
        List<EthosResponse> responseList = spyEthosProxyClient.getDataFromPaging( pager );
        // Verify that the correct method was called and examine the result.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).doPagingForAll( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize() );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == expectedResponseList.size() );

        pager.setHowToPage(Pager.PagingType.PAGE_TO_NUMPAGES);
        responseList = spyEthosProxyClient.getDataFromPaging( pager );
        // Verify that the correct method was called and examine the result.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).doPagingForNumPages( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getNumPages() );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == expectedResponseList.size() );

        pager.setHowToPage(Pager.PagingType.PAGE_FROM_OFFSET);
        responseList = spyEthosProxyClient.getDataFromPaging( pager );
        // Verify that the correct method was called and examine the result.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).doPagingFromOffset( pager.getResourceName(), pager.getVersion(), pager.getCriteriaFilter(), pager.getTotalCount(), pager.getPageSize(), pager.getOffset() );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == expectedResponseList.size() );

        pager.setHowToPage(Pager.PagingType.PAGE_FROM_OFFSET_FOR_NUMPAGES);
        responseList = spyEthosProxyClient.getDataFromPaging( pager );
        // Verify that the correct method was called and examine the result.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).doPagingFromOffsetForNumPages( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getNumPages(), pager.getOffset() );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == expectedResponseList.size() );

        pager.setHowToPage(Pager.PagingType.PAGE_TO_NUMROWS);
        responseList = spyEthosProxyClient.getDataFromPaging( pager );
        // Verify that the correct method was called and examine the result.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).doPagingForNumRows( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getNumRows() );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == expectedResponseList.size() );

        pager.setHowToPage(Pager.PagingType.PAGE_FROM_OFFSET_FOR_NUMROWS);
        responseList = spyEthosProxyClient.getDataFromPaging( pager );
        // Verify that the correct method was called and examine the result.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).doPagingFromOffsetForNumRows( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getOffset(), pager.getNumRows() );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == expectedResponseList.size() );
    }

    @Test
    void getDataFromInitialContentTest() throws IOException {
       EthosResponseConverter mockEthosResponseConverter = Mockito.mock(EthosResponseConverter.class);
       spyEthosProxyClient.ethosResponseConverter = mockEthosResponseConverter;
       EthosResponse expectedResponse = buildEthosResponse();
       Pager pager = new Pager.Builder("someResource").fromOffset(25).forNumRows(100).withEthosResponse(expectedResponse).build();

       Mockito.doReturn(expectedResponse).when(mockEthosResponseConverter).trimContentForNumRows(pager.getEthosResponse(), pager.getNumRows() );
       Mockito.doReturn(expectedResponse).when(mockEthosResponseConverter).trimContentFromOffset( pager.getEthosResponse(), pager.getOffset() );
       Mockito.doReturn(expectedResponse).when(mockEthosResponseConverter).trimContentFromOffsetForNumRows( pager.getEthosResponse(), pager.getOffset(), pager.getNumRows() );

       // Run the tests with different page types.
       pager.setHowToPage( Pager.PagingType.PAGE_ALL_PAGES );
       List<EthosResponse> responseList = spyEthosProxyClient.getDataFromInitialContent( pager );
       assertTrue( responseList != null );
       assertTrue( responseList.size() == 1 );
       assertTrue( responseList.get(0) == expectedResponse );

        pager.setHowToPage( Pager.PagingType.PAGE_TO_NUMPAGES );
        responseList = spyEthosProxyClient.getDataFromInitialContent( pager );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == 1 );
        assertTrue( responseList.get(0) == expectedResponse );

        pager.setHowToPage( Pager.PagingType.PAGE_TO_NUMROWS );
        responseList = spyEthosProxyClient.getDataFromInitialContent( pager );
        Mockito.verify(mockEthosResponseConverter, Mockito.times(1)).trimContentForNumRows( pager.getEthosResponse(), pager.getNumRows() );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == 1 );
        assertTrue( responseList.get(0) == expectedResponse );

        pager.setHowToPage( Pager.PagingType.PAGE_FROM_OFFSET );
        responseList = spyEthosProxyClient.getDataFromInitialContent( pager );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == 1 );
        assertTrue( responseList.get(0) == expectedResponse );

        pager.setHowToPage( Pager.PagingType.PAGE_FROM_OFFSET_FOR_NUMPAGES );
        responseList = spyEthosProxyClient.getDataFromInitialContent( pager );
        Mockito.verify(mockEthosResponseConverter, Mockito.times(2)).trimContentFromOffset( pager.getEthosResponse(), pager.getOffset() );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == 1 );
        assertTrue( responseList.get(0) == expectedResponse );

        pager.setHowToPage( Pager.PagingType.PAGE_FROM_OFFSET_FOR_NUMROWS );
        responseList = spyEthosProxyClient.getDataFromInitialContent( pager );
        Mockito.verify(mockEthosResponseConverter, Mockito.times(1)).trimContentFromOffsetForNumRows( pager.getEthosResponse(), pager.getOffset(), pager.getNumRows() );
        assertTrue( responseList != null );
        assertTrue( responseList.size() == 1 );
        assertTrue( responseList.get(0) == expectedResponse );
    }

    @Test
    void doPagingForAllTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        String nullFilter = null;
        int totalCount = 100;
        int pageSize = 17;
        int offset = 0;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).doPagingFromOffset( resourceName, version, nullFilter, totalCount, pageSize, offset );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.doPagingForAll( resourceName, version, totalCount, pageSize );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).doPagingFromOffset( resourceName, version, nullFilter, totalCount, pageSize, offset );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }


    @Test
    void doPagingFromOffsetTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        String filter = "someFilter";
        int totalCount = 100;
        int pageSize = 10;
        int offset = 30;
        EthosResponse expectedResponse = buildEthosResponse();

        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get( anyString(), anyMap() );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.doPagingFromOffset( resourceName, version, filter, totalCount, pageSize, offset );
        // Evaluate the results:
        // Verify the mocked methods were called 7 times.
        Mockito.verify(spyEthosProxyClient, Mockito.times(7)).get( anyString(), anyMap() );
        // There should be 7 pages given the totalCount, offset, and pageSize above.
        assertTrue( responseList != null );
        assertTrue( responseList.size() == 7);
    }

    @Test
    void doPagingForNumPagesTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int totalCount = 100;
        int pageSize = 17;
        int numPages = 4;
        int offset = 0;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).doPagingFromOffsetForNumPages( resourceName, version, totalCount, pageSize, numPages, offset );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.doPagingForNumPages( resourceName, version, totalCount, pageSize, numPages );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).doPagingFromOffsetForNumPages( resourceName, version, totalCount, pageSize, numPages, offset );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void doPagingFromOffsetForNumPagesTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int totalCount = 100;
        int pageSize = 10;
        int offset = 30;
        int numPages = 4;
        EthosResponse expectedResponse = buildEthosResponse();

        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get( anyString(), anyMap() );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.doPagingFromOffsetForNumPages( resourceName, version, totalCount, pageSize, numPages, offset );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(numPages)).get( anyString(), anyMap() );
        // There should be 7 pages given the totalCount, offset, and pageSize above.
        assertTrue( responseList != null );
        assertTrue( responseList.size() == numPages);

        // Run again with offset reaching a value > totalCount
        pageSize = 30;
        numPages = 5;
        responseList = spyEthosProxyClient.doPagingFromOffsetForNumPages( resourceName, version, totalCount, pageSize, numPages, offset );
        // Evaluate the results:
        // Now there should be 3 pages given the totalCount, offset, and pageSize above.
        assertTrue( responseList != null );
        assertTrue( responseList.size() == 3);
    }

    @Test
    void doPagingForNumRowsTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int totalCount = 100;
        int pageSize = 17;
        int numRows = 40;
        int offset = 0;
        List<EthosResponse> expectedResponseList = buildEthosResponseList();

        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(expectedResponseList).when(spyEthosProxyClient).doPagingFromOffsetForNumRows( resourceName, version, totalCount, pageSize, offset, numRows );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.doPagingForNumRows( resourceName, version, totalCount, pageSize, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).doPagingFromOffsetForNumRows( resourceName, version, totalCount, pageSize, offset, numRows );
        // Ensure the responseList matches the expectedResponseList.
        evaluateEthosResponseListResult( responseList, expectedResponseList );
    }

    @Test
    void doPagingFromOffsetForNumRowsTest() throws IOException {
        String resourceName = "someResource";
        String version = "someVersion";
        int totalCount = 100;
        int pageSize = 10;
        int offset = 30;
        int numRows = 40;
        EthosResponse expectedResponse = buildEthosResponse();

        // Return the pager when the method under test calls prepareForPaging() method.
        Mockito.doReturn(expectedResponse).when(spyEthosProxyClient).get( anyString(), anyMap() );
        // Run the test.
        List<EthosResponse> responseList = spyEthosProxyClient.doPagingFromOffsetForNumRows( resourceName, version, totalCount, pageSize, offset, numRows );
        // Evaluate the results:
        // Verify the mocked methods were called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(4)).get( anyString(), anyMap() );
        // There should be 7 pages given the totalCount, offset, and pageSize above.
        assertTrue( responseList != null );
        assertTrue( responseList.size() == 4);

        // Run again with numRows > totalCount
        numRows = 120;
        responseList = spyEthosProxyClient.doPagingFromOffsetForNumRows( resourceName, version, totalCount, pageSize, offset, numRows );
        // Evaluate the results:
        // Now there should be 7 pages given the totalCount, offset, and pageSize above.
        assertTrue( responseList != null );
        assertTrue( responseList.size() == 7);

        // Run again with a pageSize causing the offset to be > than totalCount.
        pageSize = 30;
        responseList = spyEthosProxyClient.doPagingFromOffsetForNumRows( resourceName, version, totalCount, pageSize, offset, numRows );
        // Evaluate the results:
        // Now there should be 3 pages given the totalCount, offset, and pageSize above.
        assertTrue( responseList != null );
        assertTrue( responseList.size() == 3);
    }

    @Test
    void getHeaderValueTest() {
        String someHeaderName = "someHeaderName";
        Header someHeader = new Header() {
            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }

            @Override
            public String getName() {
                return someHeaderName;
            }

            @Override
            public String getValue() {
                return "100";
            }
        };

        // Run the test with both a primary and secondary header.
        HashMap<String, Header> headerMap = new HashMap<>();
        headerMap.put(someHeaderName, someHeader);
        EthosResponse testEthosResponse = new EthosResponse( headerMap, "{\"someLabel\":\"someValue\"}", 200 );
        // Run the test with no secondary header.
        String headerValue = spyEthosProxyClient.getHeaderValue( testEthosResponse, someHeaderName );
        assertTrue( headerValue != null );
        assertTrue( headerValue.equals("100") );

        // Run the test again with no headers in the map.
        headerMap = new HashMap<>();
        testEthosResponse = new EthosResponse( headerMap, "{\"someLabel\":\"someValue\"}", 200 );
        headerValue = spyEthosProxyClient.getHeaderValue( testEthosResponse, someHeaderName );
        assertTrue( headerValue == null );

        // Run again with a null ethosResponse.
        headerValue = spyEthosProxyClient.getHeaderValue( null, someHeaderName );
        assertTrue( headerValue == null );
    }

    @Test
    void getPageSizeTest() throws IOException {
        int expectedPageSize = 17;
        String resourceName = "someResource";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedPageSize).when(spyEthosProxyClient).getPageSize( resourceName, EthosProxyClient.DEFAULT_VERSION );
        // Run the test.
        int pageSize = spyEthosProxyClient.getPageSize( resourceName );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPageSize( resourceName, EthosProxyClient.DEFAULT_VERSION );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );
    }

    @Test
    void getPageSizeWithVersionTest() throws IOException {
        int expectedPageSize = 17;
        String resourceName = "someResource";
        String version = "someVersion";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedPageSize).when(spyEthosProxyClient).getPageSize( resourceName, version, null );
        // Run the test.
        int pageSize = spyEthosProxyClient.getPageSize( resourceName, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getPageSize( resourceName, version, null );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );
    }

    @Test
    void getMaxPageSizeTest() throws IOException {
        int expectedPageSize = 17;
        String resourceName = "someResource";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedPageSize).when(spyEthosProxyClient).getMaxPageSize( resourceName, EthosProxyClient.DEFAULT_VERSION );
        // Run the test.
        int pageSize = spyEthosProxyClient.getMaxPageSize( resourceName );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getMaxPageSize( resourceName, EthosProxyClient.DEFAULT_VERSION );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );
    }

    @Test
    void getMaxPageSizeWithVersionTest() throws IOException {
        int expectedPageSize = 17;
        String resourceName = "someResource";
        String version = "someVersion";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedPageSize).when(spyEthosProxyClient).getMaxPageSize( resourceName, version, null );
        // Run the test.
        int pageSize = spyEthosProxyClient.getMaxPageSize( resourceName, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getMaxPageSize( resourceName, version, null );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );
    }

    @Test
    void getPageSizeWithVersionAndEthosResponseReturns0PageSizeTest() throws IOException {
        int expectedPageSize = 0;
        String resourceName = null;
        String version = "someVersion";
        // Run the test
        int pageSize = spyEthosProxyClient.getPageSize( resourceName, version );
        // Evaluate the results:
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );

        // Run again with an empty resourceName.
        resourceName = "";
        pageSize = spyEthosProxyClient.getPageSize( resourceName, version );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );
    }

    @Test
    void getPageSizeWithVersionAndEthosResponseTest() throws IOException {
        int expectedPageSize = 2;
        String resourceName = "someResource";
        String version = "someVersion";
        EthosResponse ethosResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(ethosResponse).when(spyEthosProxyClient).get( resourceName, version );
        // Run the test.
        int pageSize = spyEthosProxyClient.getPageSize( resourceName, version, null );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get( resourceName, version );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );

        // Run again with an empty ethosResponse content.
        expectedPageSize = 10;
        ethosResponse = new EthosResponse( new HashMap<>(), null, 200 );
        Mockito.doReturn(expectedPageSize).when(spyEthosProxyClient).getMaxPageSize( resourceName, version, ethosResponse );
        // Run the test again.
        pageSize = spyEthosProxyClient.getPageSize( resourceName, version, ethosResponse );
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getMaxPageSize( resourceName, version, ethosResponse );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );
    }

    @Test
    void getMaxPageSizeWithVersionAndEthosResponseReturns0PageSizeTest() throws IOException {
        int expectedPageSize = 0;
        String resourceName = null;
        String version = "someVersion";
        // Run the test
        int pageSize = spyEthosProxyClient.getMaxPageSize( resourceName, version );
        // Evaluate the results:
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );

        // Run again with an empty resourceName.
        resourceName = "";
        pageSize = spyEthosProxyClient.getMaxPageSize( resourceName, version );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );
    }

    @Test
    void getMaxPageSizeWithVersionAndEthosResponseTest() throws IOException {
        int expectedPageSize = 100;
        String resourceName = "someResource";
        String version = "someVersion";
        EthosResponse ethosResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(ethosResponse).when(spyEthosProxyClient).get( resourceName, version );
        Mockito.doReturn("100").when(spyEthosProxyClient).getHeaderValue( ethosResponse, EthosProxyClient.HDR_X_MAX_PAGE_SIZE );
        // Run the test.
        int pageSize = spyEthosProxyClient.getMaxPageSize( resourceName, version, null );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get( resourceName, version );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );

        // Run again as if getHeaderValue() returns a null maxPageSizeStr.
        expectedPageSize = EthosProxyClient.DEFAULT_MAX_PAGE_SIZE;
        ethosResponse = new EthosResponse( new HashMap<>(), null, 200 );
        Mockito.doReturn(null).when(spyEthosProxyClient).getHeaderValue( ethosResponse, EthosProxyClient.HDR_X_MAX_PAGE_SIZE );
        // Run the test again.
        pageSize = spyEthosProxyClient.getMaxPageSize( resourceName, version, ethosResponse );
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getHeaderValue( ethosResponse, EthosProxyClient.HDR_X_MAX_PAGE_SIZE );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedPageSize );
    }

    @Test
    void getTotalCountTest() throws IOException {
        int expectedTotalCount = 50;
        String resourceName = "someResource";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedTotalCount).when(spyEthosProxyClient).getTotalCount( resourceName, EthosProxyClient.DEFAULT_VERSION );
        // Run the test.
        int totalCount = spyEthosProxyClient.getTotalCount( resourceName );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getTotalCount( resourceName, EthosProxyClient.DEFAULT_VERSION );
        // Ensure the result matches the expected page size.
        assertTrue( totalCount == expectedTotalCount );
    }

    @Test
    void getTotalCountWithVersionTest() throws IOException {
        int expectedTotalCount = 50;
        String resourceName = "someResource";
        String version = "someVersion";
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedTotalCount).when(spyEthosProxyClient).getTotalCount( resourceName, version, null );
        // Run the test.
        int totalCount = spyEthosProxyClient.getTotalCount( resourceName, version );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getTotalCount( resourceName, version, null );
        // Ensure the result matches the expected page size.
        assertTrue( totalCount == expectedTotalCount );
    }

    @Test
    void getTotalCountWithVersionAndEthosResponseReturns0TotalCountTest() throws IOException {
        int expectedTotalCount = 0;
        String resourceName = null;
        String version = "someVersion";
        // Run the test
        int pageSize = spyEthosProxyClient.getTotalCount( resourceName, version );
        // Evaluate the results:
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedTotalCount );

        // Run again with an empty resourceName.
        resourceName = "";
        pageSize = spyEthosProxyClient.getTotalCount( resourceName, version );
        // Ensure the result matches the expected page size.
        assertTrue( pageSize == expectedTotalCount );
    }

    @Test
    void getTotalCountWithVersionAndEthosResponseTest() throws IOException {
        int expectedPageSize = 100;
        String resourceName = "someResource";
        String version = "someVersion";
        EthosResponse ethosResponse = buildEthosResponse();
        // Return the testEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(ethosResponse).when(spyEthosProxyClient).get( resourceName, version );
        Mockito.doReturn("100").when(spyEthosProxyClient).getHeaderValue( ethosResponse, EthosProxyClient.HDR_X_TOTAL_COUNT );
        // Run the test.
        int totalCount = spyEthosProxyClient.getTotalCount( resourceName, version, null );
        // Evaluate the results:
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).get( resourceName, version );
        Mockito.verify(spyEthosProxyClient, Mockito.times(1)).getHeaderValue( ethosResponse, EthosProxyClient.HDR_X_TOTAL_COUNT );
        // Ensure the result matches the expected page size.
        assertTrue( totalCount == expectedPageSize );
    }

}
