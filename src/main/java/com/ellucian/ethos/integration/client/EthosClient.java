package com.ellucian.ethos.integration.client;

import com.ellucian.ethos.integration.EthosIntegrationUrls;
import com.ellucian.ethos.integration.authentication.AccessToken;
import com.ellucian.ethos.integration.authentication.SupportedRegions;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Base HTTP client to interact with Ellucian Ethos API.  This class is mostly used internally by the SDK to support making
 * generic GET, POST, PUT, and DELETE requests to the Ethos Integration API via HTTP(S).  It lightly wraps the the Apache HttpClient library for simplicity.
 * <p>
 * This class requires an Ethos Integration API key that is used to call the /auth endpoint.  If the API key is valid, the auth endpoint will return
 * a signed JWT (access token) that will be used to make all other calls to Ethos Integration.
 * <p>
 * Access Tokens have an expiration, and by default, this client will automatically get a new access token before the current one expires.
 * This will prevent getting <code>401 Unauthorized</code> responses from Ethos Integration for trying to use an expired token.
 * This setting can be overridden by calling <code>setAutoRefresh</code> with a false boolean value.
 * <p>
 * Another default authentication behavior of this client is that it will request a token that expires in 60 minutes.  The
 * default duration for an access token from Ethos Integration is 5 minutes, but the /auth endpoint allows an <code>expirationMinutes</code>
 * query parameter to change that behavior.  The /auth endpoint accepts an expirationMinutes value as an integer between 1 and 120.  This setting can be
 * overridden by calling this client's <code>setExpirationMinutes</code> method.
 * <p>
 * This class has a region property.  The region is associated with an AWS region, and this determines how the URL's will be built when making
 * requests to ensure the request is being routed to the correct Ethos Integration deployment.  The region is one of the {@link SupportedRegions} enumeration
 * values.  By default, it is set to <code>US</code>, but it can be overridden by calling the <code>setRegion</code> method.
 * @since 0.0.1
 */
public class EthosClient {

    /**
     * The GUID pattern that the API keys must adhere to.
     */
    private static final Pattern GUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}$");

    /**
     * The application API key used to establish an AccessToken.
     */
    private String apiKey;

    /**
     * The number of minutes before the AccessToken expires.
     */
    private int expirationMinutes = 60;

    /**
     * Indicates whether to automatically refresh the AccessToken to prevent it from expiring (true),
     * or not (false) in which case the AccessToken will eventually expire allowing subsequent requests to fail authentication.
     */
    private boolean autoRefresh = true;

    /**
     * The AccessToken used to authenticate each request.
     */
    protected AccessToken token;

    /**
     * The supported region, defaulting to the US.  Can be reset to another region.
     */
    private SupportedRegions region = SupportedRegions.US;

    /**
     * The Http client builder which builds an HttpClient used for making secure calls to the Ethos Integration API.
     */
    protected HttpProtocolClientBuilder httpProtocolClientBuilder;

    /**
     * Used to build an EthosResponse from the given HttpResponse in the responseHandler
     * of each call made.
     */
    protected EthosResponseBuilder ethosResponseBuilder;


    /**
     * Instantiates this client using the given API key and timeout values.
     * @param apiKey A valid API key from Ethos Integration.  This is required to be a valid 36 character GUID string.
     *               If it is null, empty, or not in a valid GUID format, then an <code>IllegalArgumentException</code> will
     *               be thrown.
     * @param connectionTimeout The timeout <b>in seconds</b> for a connection to be established, or null to use the
     *                          HttpProtocolClientBuilder.DEFAULT_CONNECTION_TIMEOUT value.
     * @param connectionRequestTimeout The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager,
     *                                 or null to use the HttpProtocolClientBuilder.DEFAULT_CONNECTION_REQUEST_TIMEOUT value.
     * @param socketTimeout The timeout <b>in seconds</b> when waiting for data between consecutive data packets, or null
     *                      to use the HttpProtocolClientBuilder.DEFAULT_SOCKET_TIMEOUT value.
     */
    public EthosClient( String apiKey, Integer connectionTimeout, Integer connectionRequestTimeout, Integer socketTimeout ) {
        if( apiKey == null || apiKey.isBlank() || !GUID_PATTERN.matcher(apiKey).matches()) {
            throw new IllegalArgumentException("The 'apiKey' parameter must be a valid GUID string.");
        }
        this.apiKey = apiKey;
        this.httpProtocolClientBuilder = new HttpProtocolClientBuilder( connectionTimeout, connectionRequestTimeout, socketTimeout );
        this.ethosResponseBuilder = new EthosResponseBuilder();
    }


    /**
     * The responseHandler used for each request made.
     * Either supplies an EthosResponse, or throws an HttpResponseException if the HttpStatus code is not a successful code.
     */
    protected ResponseHandler<EthosResponse> responseHandler = response -> {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            return ethosResponseBuilder.buildEthosResponse( response );
        } else {
            String responseBody = EntityUtils.toString(response.getEntity());
            if (responseBody == null || responseBody.isBlank()) {
                responseBody = response.getStatusLine().getReasonPhrase();
            }
            throw new HttpResponseException(status, responseBody);
        }
    };


    /**
     * Attaches headers to a request.
     * @param request The request being made. 
     * @param headers A map of headers to attach to the request.  
     */
    private void attachHeaders(HttpRequestBase request, Map<String, String> headers) {
        for (String headerName : headers.keySet()) {
            request.addHeader(headerName, headers.getOrDefault(headerName, ""));
        }
    }


    /**
     * Gets an access token.  If this client does not currently have an access token, it will make a call to the Ethos Integration
     * /auth endpoint to get one.  Additionally, if autoRefresh is set to true, this will check to see if the current token is
     * expired and, if so, get a new one.
     * @return an access token
     * @throws IOException If it fails to get an access token
     */
    public AccessToken getAccessToken() throws IOException {
        // If the current token is null, or it is expired with auto refresh enabled, then
        // generate a new token before returning it.
        if (token == null || (!token.isValid() && autoRefresh)) {
            token = getNewToken();
        }
        return token;
    }


    // takes care of calling the authentication endpoint to get a new access token
    private AccessToken getNewToken() throws IOException {
        String authUrl = EthosIntegrationUrls.auth(this.getRegion()) + "?expirationMinutes=" + expirationMinutes;
        HttpPost httpPost = new HttpPost(authUrl);
        httpPost.addHeader("Authorization", "Bearer " + apiKey);

        // set the expiration time before retrieving the token to be sure we refresh it before it expires
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expirationMinutes);

        // send POST request to the authentication endpoint
        HttpClient httpClient = getHttpClient();
        EthosResponse ethosResponse = httpClient.execute( httpPost, responseHandler );
        return new AccessToken(ethosResponse.getContent(), expirationTime);
    }


    /**
     * Make an HTTP GET request with the given headers. This will return the response as an EthosResponse, so any other
     * desired response format will need to convert the EthosResponse in the subclass extending this.
     * Also note that HTTP status codes that throw errors will come back as an exception via the responseHandler.
     * 
     * @param url The URL to GET.
     * @param headers The headers to send to the request.
     * @return The response as an EthosResponse containing headers, response body (content), and HTTP status code.
     * @throws IllegalArgumentException If the url is null or blank.
     * @throws IOException If there is an error connecting or if there is an HTTP error code.
     */
    public EthosResponse get(String url, Map<String,String> headers) throws IOException {
        if( url == null || url.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot submit a GET request due to a null or blank request URL." );
        }
        HttpRequestBase httpGet = new HttpGet(url);
        EthosResponse ethosResponse = submitRequest( httpGet, headers );
        return ethosResponse;
    }


    /**
     * Convenience method to make an HTTP call without headers.
     * 
     * @param url The URL to GET.
     * @return The response as an EthosResponse containing response headers, response body (content), and the HTTP status code.
     * @throws IOException If there is an error connecting or if there is an HTTP error code.
     */
    public EthosResponse get(String url) throws IOException {
        return get(url, null);
    }


    /**
     * Make an HTTP HEAD request against the given URL.  This will return an EthosResponse object containing the response
     * headers.  Note that the HTTP status codes that throw errors will come back as an exception via the responseHandler.
     *
     * @param url The request URL.
     * @return The response as an EthosResponse containing headers and HTTP status code.
     * @throws IllegalArgumentException If the url is null or blank.
     * @throws IOException If there is an error connecting or if there is an HTTP error code.
     */
    public EthosResponse head( String url ) throws IOException {
        if( url == null || url.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot submit a HEAD request due to a null or blank request URL." );
        }
        HttpHead httpHead = new HttpHead( url );
        EthosResponse ethosResponse = submitRequest( httpHead, null );
        return ethosResponse;
    }



    /**
     * Makes an HTTP POST request with the given headers and body to the specified URL.
     * @param url The URL to call.
     * @param headers The headers to send in the request.
     * @param body The contents to send in the body of the request.
     * @return The response as an EthosResponse containing headers, response body (content), and HTTP status code.
     * @throws IllegalArgumentException If the url or body is null or blank.
     * @throws IOException If there is an error connecting or if there is an HTTP error code.
     */
    public EthosResponse post(String url, Map<String, String> headers, String body) throws IOException {
        if( url == null || url.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot submit a POST request due to a null or blank request URL." );
        }
        if( body == null || body.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot submit a POST request due to a null or blank request body." );
        }

        HttpPost httpPost = new HttpPost( url );

        // Add the body
        StringEntity entity = new StringEntity( body );
        httpPost.setEntity( entity );

        EthosResponse ethosResponse = submitRequest( httpPost, headers );
        return ethosResponse;
    }


    /**
     * Makes an HTTP PUT request with the given headers and body to the specified URL.
     * @param url The URL to call.
     * @param headers The headers to send in the request.
     * @param body The contents to send in the body of the request.
     * @return The response as an EthosResponse containing headers, response body (content), and HTTP status code.
     * @throws IllegalArgumentException If the url or body is null or blank.
     * @throws IOException If there is an error connecting or if there is an HTTP error code.
     */
    public EthosResponse put(String url, Map<String,String> headers, String body) throws IOException {
        if( url == null || url.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot submit a PUT request due to a null or blank request URL." );
        }
        if( body == null || body.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot submit a PUT request due to a null or blank request body." );
        }

        HttpPut httpPut = new HttpPut( url );

        // Add the body
        StringEntity entity = new StringEntity( body );
        httpPut.setEntity( entity );

        EthosResponse ethosResponse = submitRequest( httpPut, headers );
        return ethosResponse;
    }


    /**
     * Makes an HTTP DELETE request with the given headers.
     * @param url The URL to call.
     * @param headers The headers to send in the request.
     * @throws IOException If there is an error connecting or if there is an HTTP error code.
     */
    public void delete(String url, Map<String, String> headers) throws IOException {
        if( url == null || url.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot submit a DELETE request due to a null or blank request URL." );
        }
        HttpDelete httpDelete = new HttpDelete( url );
        submitRequest( httpDelete, headers );
    }


    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Submits all HTTP requests.
     * @param httpRequestBase The base HTTP request to submit, could be a GET, PUT, POST, or DELETE, etc. request.
     * @param headers The request headers map.  If null, a new map is built to contain the auth token header.
     * @return An EthosResponse built from the response handler.
     * @throws IOException If there is an error connecting or if there is an HTTP error code.
     */
    protected EthosResponse submitRequest( HttpRequestBase httpRequestBase, Map<String,String> headers ) throws IOException {
        AccessToken token = getAccessToken();
        // add headers to the request
        if( headers == null ) {
            headers = new HashMap<>();
        }
        headers.putAll( token.getAuthHeader() );
        attachHeaders( httpRequestBase, headers );

        HttpClient httpClient = getHttpClient();
        EthosResponse ethosResponse = httpClient.execute( httpRequestBase, responseHandler );
        ethosResponse.setRequestedUrl( httpRequestBase.getURI().toString() );
        return ethosResponse;
    }


    /**
     * Returns the HttpClient built by the httpProtocolClientBuilder.  Used primarily by this class.
     * @return the HttpClient used to make Http calls.
     */
    protected HttpClient getHttpClient() {
        return httpProtocolClientBuilder.buildHttpClient();
    }

    /**
     * Gets the automatic refresh behavior for access tokens.
     * @return the auto refresh status of the token.
     */
    public boolean getAutoRefresh() {
        return autoRefresh;
    }

    /**
     * Sets the automatic refresh behavior for access tokens.
     * @param autoRefresh <code>true</code> if this provider should automatically get a new token before the current one expires, or
     *                    <code>false</code> if it should let the token expire.
     */
    public void setAutoRefresh(boolean autoRefresh) { this.autoRefresh = autoRefresh; }

    /**
     * Gets the number of minutes that a new access token will be valid.
     * @return the number of minutes a token is valid before it expires
     */
    public int getExpirationMinutes() { return expirationMinutes; };

    /**
     * Sets the number of minutes that a new access token will be valid.
     * @param expirationMinutes The number of minutes before a token should expire.  This is required to be an integer between
     *                          1 and 120, otherwise an <code>IllegalArgumentException</code> will be thrown.
     */
    public void setExpirationMinutes(int expirationMinutes) {
        if( expirationMinutes < 1 || expirationMinutes > 120 ) {
            throw new IllegalArgumentException("The 'expirationMinutes' parameter has to be between 1 and 120.");
        }
        this.expirationMinutes = expirationMinutes;
    }

    /**
     * Gets the region to get the data from.  This must be one of the supported regions, which can be found in the SupportedRegions
     * enumeration.   This is ultimately used to retrieve data from the proper place.
     * @return The region to get data from.
     */
    public SupportedRegions getRegion() {
        return region;
    }

    /**
     * Sets the region to get the data from.  This must be one of the supported regions, which can be found in the SupportedRegions
     * enumeration.   This is ultimately used to retrieve data from the proper place.
     *
     * @param region the region where data will be retrieved from for this session's duration.
     */
    public void setRegion(SupportedRegions region) {
        this.region = region;
    }

}
