package com.ellucian.ethos.integration.client;

import com.ellucian.ethos.integration.authentication.AccessToken;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EthosClientTest {

    private EthosClient spyEthosClient;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private EthosResponseBuilder mockEthosResponseBuilder;

    @BeforeEach
    void setup() throws IOException {
        spyEthosClient = Mockito.spy( new EthosClient("11111111-1111-1111-1111-111111111111", null, null, null) );
        spyEthosClient.ethosResponseBuilder = mockEthosResponseBuilder;
    }

    @Test
    void testValidApiKey() {
        EthosClient client = new EthosClient("11111111-1111-1111-1111-111111111111", null, null, null);
        assertTrue(client.getAutoRefresh());
        assertEquals(client.getExpirationMinutes(), 60);
    }

    @Test
    void testInvalidApiKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EthosClient("1234", null, null, null);
        });
    }

    @Test
    void testInvalidSessionMinutes() {
        EthosClient client = new EthosClient("11111111-1111-1111-1111-111111111111", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.setExpirationMinutes(121);
        });
    }

    @Test
    void testGetAccessToken() throws IOException {
        // return a mock HttpClient from the EthosClient
        doReturn(mockHttpClient).when(spyEthosClient).getHttpClient();
        // Tell the mockHttpClient to return the testEthosResponse when any HttpRequestBase and responseHandler is used on the execute() method.
        EthosResponse testEthosResponse = new EthosResponse(new HashMap<>(), "jwt_string", 200);
        when(mockHttpClient.execute(any(HttpRequestBase.class), same(spyEthosClient.responseHandler))).thenReturn( testEthosResponse );

        AccessToken token = spyEthosClient.getAccessToken();
        verify(mockHttpClient).execute(any(HttpRequestBase.class), same(spyEthosClient.responseHandler));
        assertEquals(token.getToken(), "jwt_string");
    }

    @Test
    void testAutoRefreshToken() throws IOException {
        // return a mock HttpClient from the EthosClient
        doReturn(mockHttpClient).when(spyEthosClient).getHttpClient();
        // Tell the mockHttpClient to return the testEthosResponse when any HttpRequestBase and responseHandler is used on the execute() method.
        EthosResponse testEthosResponse = new EthosResponse(new HashMap<>(), "new_jwt_string", 200);
        when(mockHttpClient.execute(any(HttpRequestBase.class), same(spyEthosClient.responseHandler))).thenReturn( testEthosResponse );
        // set the current token to be expired
        spyEthosClient.token = new AccessToken("old_jwt_string", LocalDateTime.now().minusMinutes(1));

        AccessToken token = spyEthosClient.getAccessToken();
        verify(mockHttpClient).execute(any(HttpRequestBase.class), same(spyEthosClient.responseHandler));
        assertEquals(token.getToken(), "new_jwt_string");
    }

    @Test
    void testNonRefreshToken() throws IOException {
        // set the token to be expired
        spyEthosClient.token = new AccessToken("old_jwt_string", LocalDateTime.now().minusMinutes(1));
        // set autoRefresh to false
        spyEthosClient.setAutoRefresh(false);

        AccessToken token = spyEthosClient.getAccessToken();
        // verify the request to get a new token was never called
        verify(mockHttpClient, never()).execute(any(HttpRequestBase.class), same(spyEthosClient.responseHandler));
        assertEquals(token.getToken(), "old_jwt_string");
    }

    @Test
    void testSubmitRequest() throws Exception  {
        AccessToken accessToken = new AccessToken( "jwt_string", LocalDateTime.now() );
        String url = "someUrl/test";
        HttpGet httpGet = new HttpGet( url );
        Map<String, String> headers = new HashMap();
        headers.put( "Accept", "someVersion" );
        EthosResponse testEthosResponse = new EthosResponse(new HashMap<>(), "someResponseBody", 200);
        // Tell the spyEthosClient to return the testEthosResponse when any HttpRequestBase and headers map is used on the submitRequest() method.
        doReturn(accessToken).when(spyEthosClient).getAccessToken();
        doReturn(mockHttpClient).when(spyEthosClient).getHttpClient();
        doReturn(testEthosResponse).when(mockHttpClient).execute( httpGet, spyEthosClient.responseHandler );
        // Run the test.
        EthosResponse ethosResponse = spyEthosClient.submitRequest( httpGet, headers );
        assertEquals(ethosResponse.getContent(), testEthosResponse.getContent());
        assertEquals(ethosResponse.getHttpStatusCode(), testEthosResponse.getHttpStatusCode());
        assertEquals(ethosResponse.getRequestedUrl(), httpGet.getURI().toString());
        // Run the test again with a null headers map.
        headers = null;
        ethosResponse = spyEthosClient.submitRequest( httpGet, headers );
        assertEquals(ethosResponse.getContent(), testEthosResponse.getContent());
        assertEquals(ethosResponse.getHttpStatusCode(), testEthosResponse.getHttpStatusCode());
        assertEquals(ethosResponse.getRequestedUrl(), httpGet.getURI().toString());
    }

    @Test
    void testGetWithInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.get( null, new HashMap<>() );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.get( "", new HashMap<>() );
        });
    }


    @Test
    void testGetWithUrlAndHeaders() throws Exception  {
        String url = "someUrl/test";
        Map<String, String> headers = new HashMap();
        headers.put( "Accept", "someVersion" );
        EthosResponse testEthosResponse = new EthosResponse(new HashMap<>(), "someResponseBody", 200);
        // Tell the spyEthosClient to return the testEthosResponse when any HttpRequestBase and headers map is used on the submitRequest() method.
        doReturn(testEthosResponse).when(spyEthosClient).submitRequest(any(HttpRequestBase.class), anyMap() );

        // Run the test.
        EthosResponse ethosResponse = spyEthosClient.get( url, headers );
        Mockito.verify(spyEthosClient, Mockito.times(1)).submitRequest( any(HttpRequestBase.class), anyMap() );
        assertEquals(ethosResponse.getContent(), testEthosResponse.getContent());
        assertEquals(ethosResponse.getHttpStatusCode(), testEthosResponse.getHttpStatusCode());
    }

    @Test
    void testGetWithUrl() throws Exception  {
        EthosResponse testEthosResponse = new EthosResponse(new HashMap<>(), "someResponseBody", 200);
        String url = "someUrl/test";
        // return the test response when the get method is called with null headers
        doReturn(testEthosResponse).when(spyEthosClient).get( url, null );

        EthosResponse ethosResponse = spyEthosClient.get( url );
        assertEquals(ethosResponse.getContent(), testEthosResponse.getContent());
        assertEquals(ethosResponse.getHttpStatusCode(), testEthosResponse.getHttpStatusCode());
    }

    @Test
    void testPostWithInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.post( null, new HashMap<>(), null );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.post( null, new HashMap<>(), "requestBodyStr" );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.post( null, new HashMap<>(), "" );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.post( "", new HashMap<>(), null );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.post( "", new HashMap<>(), "requestBodyStr" );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.post( "", new HashMap<>(), "" );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.post( "someUrl", new HashMap<>(), null );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.post( "someUrl", new HashMap<>(), "" );
        });
    }

    @Test
    void testPost() throws Exception  {
        Map<String, String> headers = new HashMap();
        headers.put( "Accept", "someVersion" );
        String url = "someUrl/test";
        EthosResponse testEthosResponse = new EthosResponse(new HashMap<>(), "someResponseBody", 200);
        // Tell the spyEthosClient to return the testEthosResponse when any HttpRequestBase and headers map is used on the submitRequest() method.
        doReturn(testEthosResponse).when(spyEthosClient).submitRequest(any(HttpRequestBase.class), anyMap() );

        // Run the test.
        EthosResponse ethosResponse = spyEthosClient.post( url, headers, "requestBody" );
        Mockito.verify(spyEthosClient, Mockito.times(1)).submitRequest( any(HttpRequestBase.class), anyMap() );
        assertEquals(ethosResponse.getContent(), testEthosResponse.getContent());
        assertEquals(ethosResponse.getHttpStatusCode(), testEthosResponse.getHttpStatusCode());
    }

    @Test
    void testPutWithInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.put( null, new HashMap<>(), null );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.put( null, new HashMap<>(), "requestBodyStr" );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.put( null, new HashMap<>(), "" );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.put( "", new HashMap<>(), null );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.put( "", new HashMap<>(), "requestBodyStr" );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.put( "", new HashMap<>(), "" );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.put( "someUrl", new HashMap<>(), null );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.put( "someUrl", new HashMap<>(), "" );
        });
    }

    @Test
    void testPut() throws Exception  {
        Map<String, String> headers = new HashMap();
        headers.put( "Accept", "someVersion" );
        String url = "someUrl/test";
        EthosResponse testEthosResponse = new EthosResponse(new HashMap<>(), "someResponseBody", 200);
        // Tell the spyEthosClient to return the testEthosResponse when any HttpRequestBase and headers map is used on the submitRequest() method.
        doReturn(testEthosResponse).when(spyEthosClient).submitRequest(any(HttpRequestBase.class), anyMap() );

        // Run the test.
        EthosResponse ethosResponse = spyEthosClient.put( url, headers, "requestBody" );
        Mockito.verify(spyEthosClient, Mockito.times(1)).submitRequest( any(HttpRequestBase.class), anyMap() );
        assertEquals(ethosResponse.getContent(), testEthosResponse.getContent());
        assertEquals(ethosResponse.getHttpStatusCode(), testEthosResponse.getHttpStatusCode());
    }

    @Test
    void testHeadWithInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.head( null );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.head( "" );
        });
    }


    @Test
    void testHead() throws Exception  {
        String url = "someUrl/test";
        EthosResponse testEthosResponse = new EthosResponse(new HashMap<>(), "someResponseBody", 200);
        // Tell the spyEthosClient to return the testEthosResponse when any HttpRequestBase and headers map is used on the submitRequest() method.
        doReturn(testEthosResponse).when(spyEthosClient).submitRequest(any(HttpRequestBase.class), any() );

        // Run the test.
        EthosResponse ethosResponse = spyEthosClient.head( url );
        Mockito.verify(spyEthosClient, Mockito.times(1)).submitRequest( any(HttpRequestBase.class), any() );
        assertEquals(ethosResponse.getContent(), testEthosResponse.getContent());
        assertEquals(ethosResponse.getHttpStatusCode(), testEthosResponse.getHttpStatusCode());
    }

    @Test
    void testDeleteWithInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.delete( null, new HashMap<>() );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            spyEthosClient.delete( "", new HashMap<>() );
        });
    }

    @Test
    void testDelete() throws Exception  {
        Map<String, String> headers = new HashMap();
        headers.put( "Accept", "someVersion" );
        String url = "someUrl/test";
        EthosResponse testEthosResponse = new EthosResponse(new HashMap<>(), "someResponseBody", 200);
        // Tell the spyEthosClient to return the testEthosResponse when any HttpRequestBase and headers map is used on the submitRequest() method.
        doReturn(testEthosResponse).when(spyEthosClient).submitRequest(any(HttpRequestBase.class), anyMap() );
        // Run the test.
        spyEthosClient.delete( url, headers );
        Mockito.verify(spyEthosClient, Mockito.times(1)).submitRequest( any(HttpRequestBase.class), anyMap() );
    }

}
