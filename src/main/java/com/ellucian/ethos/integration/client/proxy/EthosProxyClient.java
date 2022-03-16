/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy;


import com.ellucian.ethos.integration.EthosIntegrationUrls;
import com.ellucian.ethos.integration.client.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An EthosClient used to retrieve data from the Ethos Integration Proxy API.
 * <p>
 * Supports (but not limited to) the following functionality:
 * <ul>
 *     <li>Getting data for a given Ethos resource</li>
 *     <li>Getting an Ethos resource by ID (GUID)</li>
 *     <li>Getting the page size and/or total count for a resource</li>
 * </ul>
 *
 * <p>
 * Supports paging for data in the following formats:
 * <ul>
 *     <li><code>List&lt;EthosResponse&gt;</code> a list of EthosResponse objects</li>
 *     <li><code>List&lt;String&gt;</code> a list of Strings</li>
 *     <li><code>List&lt;JsonNode&gt;</code> a list of Jackson JsonNode objects</li>
 * </ul>
 * Each item in the list represents one page of data.  Each page of data can contain many rows according to the page size.
 * <p>
 * Note that the preferred way to instantiate this class is through the {@link EthosClientBuilder EthosClientBuilder}.
 * </p>
 * <p>
 * <b>NOTE: None of the methods in this class should be used to bulk load Ethos data.  Such is NOT the intent of this SDK.
 * It is possible that long running process times could result and/or <code>OutOfMemoryError</code>s could occur if trying to get
 * a large quantity of data.  Instead, the Ethos bulk loading solution should be used for loading data in Ethos data model format in bulk.</b>
 * @since 0.0.1
 * @author David Kumar
 */
public class EthosProxyClient<T> extends EthosClient {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /**
     * The default version used if no version is specified.
     */
    public static final String DEFAULT_VERSION          = "application/json";

    /**
     * A default value used for the page size if no page size is specified.  The length of the response body will be
     * used as the page size if this default value is used.
     */
    public static final int    DEFAULT_PAGE_SIZE        = 0;

    /**
     * The value used for the max page size if the max page size header is not found in the response.
     */
    public static final int    DEFAULT_MAX_PAGE_SIZE    = 500;

    /**
     * Response header for the current date of the response.
     */
    public static final String HDR_DATE                                       = "Date";

    /**
     * Response header for the content-type.
     */
    public static final String HDR_CONTENT_TYPE                               = "Content-Type";

    /**
     * Response header for the total count (total number of rows) for the given resource.
     */
    public static final String HDR_X_TOTAL_COUNT                              = "x-total-count";

    /**
     * Response header for the application designation in Ethos Integration.
     */
    public static final String HDR_APPLICATION_CONTEXT                        = "x-application-context";

    /**
     * Response header for the max page size.  Specifies the maximum number of resources returned in a response.
     */
    public static final String HDR_X_MAX_PAGE_SIZE                            = "x-max-page-size";

    /**
     * Response header for the version of the Ethos resource (data-model).
     */
    public static final String HDR_X_MEDIA_TYPE                               = "x-media-type";

    /**
     * Response header for content restricted.
     */
    public static final String HDR_X_CONTENT_RESTRICTED                       = "x-content-restricted";

    /**
     * Response header for the application ID of the application used in Ethos Integration.
     */
    public static final String HDR_HEDTECH_ETHOS_INTEGRATION_APPLICATION_ID   = "hedtech-ethos-integration-application-id";

    /**
     * Response header for the application name of the application used in Ethos Integration.
     */
    public static final String HDR_HEDTECH_ETHOS_INTEGRATION_APPLICATION_NAME = "hedtech-ethos-integration-application-name";

    /**
     * A Jackson <code>ObjectMapper</code> used to convert the response string content body into a JsonNode for
     * calculating the response content length.
     */
    private ObjectMapper objectMapper;

    /**
     * The <code>EthosResponseConverter</code> used to convert EthosResponses into other supported formats (Strings
     * and JsonNodes).
     */
    protected EthosResponseConverter ethosResponseConverter;

    /**
     * The <code>EthosRequestConverter</code> used to convert generic typed objects into JSON formatted strings for request bodies.
     */
    protected EthosRequestConverter<T> ethosRequestConverter;

    /**
     * Instantiates this class using the given API key.
     * <p>
     * Note that the preferred way to get an instance of this class is through the {@link EthosClientBuilder EthosClientBuilder}.
     * @param apiKey A valid API key from Ethos Integration.  This is required to be a valid 36 character GUID string.
     *               If it is null, empty, or not in a valid GUID format, then an <code>IllegalArgumentException</code> will be thrown.
     * @param connectionTimeout The timeout <b>in seconds</b> for a connection to be established.
     * @param connectionRequestTimeout The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager.
     * @param socketTimeout The timeout <b>in seconds</b> when waiting for data between consecutive data packets.
     */
    public EthosProxyClient( String apiKey, Integer connectionTimeout, Integer connectionRequestTimeout, Integer socketTimeout ) {
        super( apiKey, connectionTimeout, connectionRequestTimeout, socketTimeout );
        this.objectMapper = new ObjectMapper();
        this.ethosRequestConverter = new EthosRequestConverter<T>();
        this.ethosResponseConverter = new EthosResponseConverter();
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Submits a POST request for the given resourceName with the given requestBody.  Uses the default version.
     * The requestBody should be a string in JSON format.
     * @param resourceName The name of the resource to add an instance of.
     * @param requestBody The body of the request to POST for the given resource.
     * @return An EthosResponse containing the instance of the resource that was added by this POST operation. 
     * @throws IllegalArgumentException Thrown if the given resourceName or requestBody is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse post( String resourceName, String requestBody ) throws IOException {
        return post( resourceName, DEFAULT_VERSION, requestBody );
    }

    
    /**
     * Submits a POST request for the given resourceName with the given requestBody.  The requestBody should be a string
     * in JSON format.
     * @param resourceName The name of the resource to add an instance of.
     * @param version The full version header value of the resource used for this POST request.
     * @param requestBody The body of the request to POST for the given resource.
     * @return An EthosResponse containing the instance of the resource that was added by this POST operation. 
     * @throws IllegalArgumentException Thrown if the given resourceName or requestBody is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse post( String resourceName, String version, String requestBody ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException( "Error: Cannot submit a POST request due to a null or blank resourceName param." );
        }
        if( requestBody == null || requestBody.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a POST request for resourceName \"%s\" due to a null or empty requestBody param.", resourceName) );
        }
        Map<String,String> headers = buildHeadersMap( version );
        String url = EthosIntegrationUrls.apis( getRegion(), resourceName, null );
        return post( url, headers, requestBody );
    }


    /**
     * Submits a POST request for the given resourceName with the given requestBodyNode.  Uses the default version.
     * This is a convenience method equivalent to <pre>post(resourceName, requestBodyNode.toString())</pre>.
     * @param resourceName The name of the resource to add an instance of.
     * @param requestBodyNode The body of the request to POST for the given resource as a JsonNode.
     * @return An EthosResponse containing the instance of the resource that was added by this POST operation.
     * @throws IllegalArgumentException Thrown if the given resourceName or requestBodyNode is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse post( String resourceName, JsonNode requestBodyNode ) throws IOException {
        return post( resourceName, DEFAULT_VERSION, requestBodyNode );
    }


    /**
     * Submits a POST request for the given resourceName with the given requestBodyNode, which is a JsonNode.  This is a
     * convenience method equivalent to calling <pre>post(resourceName, version, requestBodyNode.toString())</pre>.
     * @param resourceName The name of the resource to add an instance of.
     * @param version The full version header value of the resource used for this POST request.
     * @param requestBodyNode The body of the request to POST for the given resource as a JsonNode.
     * @return An EthosResponse containing the instance of the resource that was added by this POST operation.
     * @throws IllegalArgumentException Thrown if the given resourceName or requestBodyNode is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse post( String resourceName, String version, JsonNode requestBodyNode ) throws IOException {
        if( requestBodyNode == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a POST request for resourceName \"%s\" due to a null requestBody JsonNode param.", resourceName) );
        }
        return post( resourceName, version, requestBodyNode.toString() );
    }

    /**
     * Submits a POST request for the given resourceName using the given genericTypeBody.  Uses the default version.
     * This is a convenience method equivalent to <pre>post(resourceName, DEFAULT_VERSION, genericTypeBody)</pre>.
     * Converts the generic type body into a JSON formatted string when making the request.
     * @param resourceName The name of the resource to add an instance of.
     * @param genericTypeBody A generic type object representing the POST request body.
     * @return An EthosResponse containing the instance of the resource that was added by this POST operation.
     * @throws IllegalArgumentException Thrown if the given genericTypeBody is null.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse post( String resourceName, T genericTypeBody ) throws IOException {
        EthosResponse ethosResponse = post( resourceName, DEFAULT_VERSION, genericTypeBody );
        return ethosResponse;
    }

    /**
     * Submits a POST request for the given resourceName and version using the given genericTypeBody.
     * Converts the generic type body into a JSON formatted string when making the request.
     * @param resourceName The name of the resource to add an instance of.
     * @param version The full version header value of the resource used for this POST request.
     * @param genericTypeBody A generic type object representing the POST request body.
     * @return An EthosResponse containing the instance of the resource that was added by this POST operation.
     * @throws IllegalArgumentException Thrown if the given genericTypeBody is null.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse post( String resourceName, String version, T genericTypeBody ) throws IOException {
        if( genericTypeBody == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a POST request for resourceName \"%s\" and version \"%s\" due to a null generic type requestBody param.", resourceName, version) );
        }
        String jsonStr = ethosRequestConverter.toJsonString( genericTypeBody );
        EthosResponse ethosResponse = post( resourceName, version, jsonStr );
        return ethosResponse;
    }

    /**
     * Submits a PUT request for the given resourceName to update a resource with the given requestBody.  Uses the default version.
     * The requestBody should be a string in JSON format.
     * @param resourceName The name of the resource to add an instance of.
     * @param resourceId The unique id (GUID) for the given resource, as required when making a PUT/update request for EEDM APIs.
     *                   Can be null if the PUT request does not require this param.
     * @param requestBody The body of the request to PUT/update for the given resource.
     * @return An EthosResponse containing the instance of the resource that was added by this PUT operation.
     * @throws IllegalArgumentException Thrown if the given resourceName or requestBody is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse put( String resourceName, String resourceId, String requestBody ) throws IOException {
        return put( resourceName, resourceId, DEFAULT_VERSION, requestBody );
    }


    /**
     * Submits a PUT request for the given resourceName to update a resource with the given requestBody.  The requestBody should be a string
     * in JSON format.
     * @param resourceName The name of the resource to add an instance of.
     * @param resourceId The unique id (GUID) for the given resource, as required when making a PUT/update request for EEDM APIs.
     *                   Can be null if the PUT request does not require this param.
     * @param version The full version header value of the resource used for this PUT/update request.
     * @param requestBody The body of the request to PUT/update for the given resource.
     * @return An EthosResponse containing the instance of the resource that was added by this PUT operation.
     * @throws IllegalArgumentException Thrown if the given resourceName or requestBody is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse put( String resourceName, String resourceId, String version, String requestBody ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException( "Error: Cannot submit a PUT request due to a null or blank resourceName param." );
        }
//        if( resourceId == null || resourceId.isBlank() ) {
//            throw new IllegalArgumentException( String.format("Error: Cannot submit a PUT request due to a null or blank resourceId param for resource \"%s\".", resourceName) );
//        }
        if( requestBody == null || requestBody.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a PUT request for resourceName \"%s\" due to a null or empty requestBody param.", resourceName) );
        }
        Map<String,String> headers = buildHeadersMap( version );
        String url = EthosIntegrationUrls.apis( getRegion(), resourceName, resourceId );
        return put( url, headers, requestBody );
    }


    /**
     * Submits a PUT request for the given resourceName to update a resource with the given requestBodyNode.  Uses the default version.
     * This is a convenience method equivalent to <pre>put(resourceName, requestBodyNode.toString())</pre>.
     * @param resourceName The name of the resource to add an instance of.
     * @param resourceId The unique id (GUID) for the given resource, as required when making a PUT/update request for EEDM APIs.
     *                   Can be null if the PUT request does not require this param.
     * @param requestBodyNode The body of the request to PUT/update for the given resource as a JsonNode.
     * @return An EthosResponse containing the instance of the resource that was added by this PUT operation.
     * @throws IllegalArgumentException Thrown if the given resourceName or requestBodyNode is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse put( String resourceName, String resourceId, JsonNode requestBodyNode ) throws IOException {
        return put( resourceName, resourceId, DEFAULT_VERSION, requestBodyNode );
    }


    /**
     * Submits a PUT request for the given resourceName to update a resource with the given requestBodyNode, which is a JsonNode.
     * This is a convenience method equivalent to calling <pre>put(resourceName, version, requestBodyNode.toString())</pre>.
     * @param resourceName The name of the resource to add an instance of.
     * @param resourceId The unique id (GUID) for the given resource, as required when making a PUT/update request for EEDM APIs.
     *                   Can be null if the PUT request does not require this param.
     * @param version The full version header value of the resource used for this PUT/update request.
     * @param requestBodyNode The body of the request to PUT/update for the given resource as a JsonNode.
     * @return An EthosResponse containing the instance of the resource that was added by this PUT operation.
     * @throws IllegalArgumentException Thrown if the given resourceName or requestBodyNode is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse put( String resourceName, String resourceId, String version, JsonNode requestBodyNode ) throws IOException {
        if( requestBodyNode == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a PUT request for resourceName \"%s\" due to a null requestBody JsonNode param.", resourceName) );
        }
        return put( resourceName, resourceId, version, requestBodyNode.toString() );
    }

    /**
     * Submits a PUT request for the given resourceName to update a resource with the given genericTypeBody.  Uses the default version.
     * Does not support PUT requests requiring a resourceId (like EEDM API requests).
     * This is a convenience method equivalent to calling <pre>put(resourceName, null, genericTypeBody)</pre>.
     * @param resourceName The name of the resource to add an instance of.
     * @param genericTypeBody A generic type object representing the PUT request body.
     * @return An EthosResponse containing the instance of the resource that was added by this PUT operation.
     * @throws IllegalArgumentException Thrown if the given resourceName or genericTypeBody is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse put( String resourceName, T genericTypeBody ) throws IOException {
        return put( resourceName, null, genericTypeBody );
    }


    /**
     * Submits a PUT request for the given resourceName to update a resource with the given genericTypeBody.  Uses the default version.
     * This is a convenience method equivalent to calling <pre>put(resourceName, resourceId, DEFAULT_VERSION, genericTypeBody)</pre>.
     * @param resourceName The name of the resource to add an instance of.
     * @param resourceId The unique id (GUID) for the given resource, as required when making a PUT/update request.
     * @param genericTypeBody A generic type object representing the PUT request body.
     * @return An EthosResponse containing the instance of the resource that was added by this PUT operation.
     * @throws IllegalArgumentException Thrown if the given resourceName or genericTypeBody is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse put( String resourceName, String resourceId, T genericTypeBody ) throws IOException {
        return put( resourceName, resourceId, DEFAULT_VERSION, genericTypeBody );
    }

    /**
     * Submits a PUT request for the given resourceName to update a resource with the given genericTypeBody.
     * Does not support PUT requests requiring a resourceId (like EEDM API requests).
     * This is a convenience method equivalent to calling <pre>put(resourceName, null, version, genericTypeBody)</pre>.
     * @param genericTypeBody A generic type object representing the PUT request body.
     * @param resourceName The name of the resource to add an instance of.
     * @param version The full version header value of the resource used for this PUT/update request.
     * @return An EthosResponse containing the instance of the resource that was added by this PUT operation.
     * @throws IllegalArgumentException Thrown if the given resourceName or genericTypeBody is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse put( T genericTypeBody, String resourceName, String version ) throws IOException {
        return put( resourceName, null, version, genericTypeBody );
    }

    /**
     * Submits a PUT request for the given resourceName to update a resource with the given genericTypeBody.
     * @param resourceName The name of the resource to add an instance of.
     * @param resourceId The unique id (GUID) for the given resource, as required when making a PUT/update request for EEDM APIs.
     *                   Can be null if the PUT request does not require this param.
     * @param version The full version header value of the resource used for this PUT/update request.
     * @param genericTypeBody A generic type object representing the PUT request body.
     * @return An EthosResponse containing the instance of the resource that was added by this PUT operation.
     * @throws IllegalArgumentException Thrown if the given resourceName or genericTypeBody is null or blank.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse put( String resourceName, String resourceId, String version, T genericTypeBody ) throws IOException {
        if( genericTypeBody == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a PUT request for resourceName \"%s\" due to a null generic type body param.", resourceName) );
        }
        String jsonStr = ethosRequestConverter.toJsonString( genericTypeBody );
        return put( resourceName, resourceId, version, jsonStr );
    }


    /**
     * Deletes an instance of the given resource by the given id.
     * @param resourceName The name of the resource to delete an instance of identified by the given id.
     * @param id The unique ID (GUID) of the resource to delete.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public void delete( String resourceName, String id ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException( "Error: Cannot submit a DELETE request due to a null or blank resourceName param." );
        }
        if( id == null || id.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a DELETE request for resourceName \"%s\" due to a null or empty ID param.", resourceName) );
        }
        Map<String,String> headers = buildHeadersMap( null );
        String url = EthosIntegrationUrls.apis( getRegion(), resourceName, id );
        delete( url, headers );
    }


    /**
     * Gets a page of resource data for the given resource by name.  Uses the default version.
     * @param resourceName The name of the resource to get data for.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the current version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse get( String resourceName ) throws IOException {
        return this.get( resourceName, DEFAULT_VERSION);
    }

    /**
     * Gets a page of data for the given resource by name and version.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse get( String resourceName, String version ) throws IOException {
        Map<String,String> headers = buildHeadersMap( version );
        EthosResponse response = get( EthosIntegrationUrls.apis(getRegion(), resourceName, null), headers );
        return response;
    }

    /**
     * Gets a page of resource data for the given resource by name.  Uses the default version.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the current version of the resource.
     * @throws IllegalArgumentException Thrown if the given classType is null.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse get( String resourceName, Class classType ) throws IOException {
        return get( resourceName, DEFAULT_VERSION, classType );
    }

    /**
     * Gets a page of resource data for the given resource by name.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the current version of the resource.
     * @throws IllegalArgumentException Thrown if the given classType is null.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse get( String resourceName, String version, Class classType ) throws IOException {
        if( classType == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a GET request for resourceName \"%s\" due to a null class type param.", resourceName) );
        }
        EthosResponse ethosResponse = this.get( resourceName, version );
        ethosResponse = convertResponseContentToTypedList( ethosResponse, classType );
        return ethosResponse;
    }
    /**
     * Gets a page of resource data for the given resource by name.  Uses the default version.
     * @param resourceName The name of the resource to get data for.
     * @return A <code>String</code> containing an initial page (EthosResponse content) of resource data according
     *         to the current version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getAsString( String resourceName ) throws IOException {
        EthosResponse ethosResponse = get( resourceName );
        return ethosResponseConverter.toContentString( ethosResponse );
    }

    /**
     * Gets a page of data for the given resource by name and version.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return A <code>String</code> containing an initial page (EthosResponse content) of resource data according
     *         to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getAsString( String resourceName, String version ) throws IOException {
        EthosResponse ethosResponse = get( resourceName, version );
        return ethosResponseConverter.toContentString( ethosResponse );
    }

    /**
     * Gets resource data for the given resource by name.  Uses the default version.
     * @param resourceName The name of the resource to get data for.
     * @return A <code>JsonNode</code> containing an initial page (EthosResponse content) of resource data according
     *         to the current version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getAsJsonNode( String resourceName ) throws IOException {
        EthosResponse ethosResponse = get( resourceName );
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }

    /**
     * Gets data for the given resource by name and version.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return A <code>JsonNode</code> containing an initial page (EthosResponse content) of resource data according
     *         to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getAsJsonNode( String resourceName, String version ) throws IOException {
        EthosResponse ethosResponse = get( resourceName, version );
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }

    /**
     * Gets a page of data for the given resource by name, offset, and pageSize.  A page of data is returned from the
     * given offset index containing the number of rows (pageSize) specified.  The default version is used.
     * @param resourceName The name of the resource to get data for.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A page of data for the given resource from the given offset with the given page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse get( String resourceName, int offset, int pageSize ) throws IOException {
        return get( resourceName, DEFAULT_VERSION, offset, pageSize );
    }

    /**
     * Gets a page of data for the given resource by name, version, offset, and pageSize.  A page of data is returned from the
     * given offset index containing the number of rows (pageSize) specified.  If the given offset is negative, it will
     * not be used.  If the given pageSize is 0 or negative, it will not be used.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A page of data for the given resource from the given offset with the given page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse get( String resourceName, String version, int offset, int pageSize ) throws IOException {
        Map<String,String> headers = buildHeadersMap( version );
        return get( EthosIntegrationUrls.apiPaging(getRegion(), resourceName, offset, pageSize), headers );
    }

    /**
     * Gets a page of data for the given resource by name, offset, and pageSize.  A page of data is returned from the
     * given offset index containing the number of rows (pageSize) specified.  The default version is used.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A page of data for the given resource from the given offset with the given page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse get( String resourceName, int offset, int pageSize, Class classType ) throws IOException {
        return get( resourceName, DEFAULT_VERSION, offset, pageSize, classType );
    }

    /**
     * Gets a page of data for the given resource by name, version, offset, and pageSize.  A page of data is returned from the
     * given offset index containing the number of rows (pageSize) specified.  If the given offset is negative, it will
     * not be used.  If the given pageSize is 0 or negative, it will not be used.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A page of data for the given resource from the given offset with the given page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse get( String resourceName, String version, int offset, int pageSize, Class classType ) throws IOException {
        if( classType == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a GET request for resourceName \"%s\" with version \"%s\", offset \"%s\", and page size \"%s\" due to a null class type param.", resourceName, version, offset, pageSize) );
        }
        EthosResponse ethosResponse = get( resourceName, version, offset, pageSize );
        ethosResponse = convertResponseContentToTypedList( ethosResponse, classType );
        return ethosResponse;
    }

    /**
     * Gets a page of data for the given resource by name, offset, and pageSize.  A page of data is returned from the
     * given offset index containing the number of rows (pageSize) specified.  The default version is used.
     * @param resourceName The name of the resource to get data for.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A <code>String</code> containing a page (EthosResponse content) of resource data from the given offset with the
     *         given page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getAsString( String resourceName, int offset, int pageSize ) throws IOException {
        return getAsString( resourceName, DEFAULT_VERSION, offset, pageSize );
    }

    /**
     * Gets a page of data for the given resource by name, version, offset, and pageSize.  A page of data is returned from the
     * given offset index containing the number of rows (pageSize) specified.  If the given offset is negative, it will
     * not be used.  If the given pageSize is 0 or negative, it will not be used.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A <code>String</code> containing a page (EthosResponse content) of resource data from the given offset with the
     *         given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getAsString( String resourceName, String version, int offset, int pageSize ) throws IOException {
        EthosResponse ethosResponse = get( resourceName, version, offset, pageSize );
        return ethosResponseConverter.toContentString( ethosResponse );
    }

    /**
     * Gets a page of data for the given resource by name, offset, and pageSize.  A page of data is returned from the
     * given offset index containing the number of rows (pageSize) specified.  The default version is used.
     * @param resourceName The name of the resource to get data for.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A <code>JsonNode</code> containing a page (EthosResponse content) of resource data from the given offset with the
     *         given page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getAsJsonNode( String resourceName, int offset, int pageSize ) throws IOException {
        return getAsJsonNode( resourceName, DEFAULT_VERSION, offset, pageSize );
    }

    /**
     * Gets a page of data for the given resource by name, version, offset, and pageSize.  A page of data is returned from the
     * given offset index containing the number of rows (pageSize) specified.  If the given offset is negative, it will
     * not be used.  If the given pageSize is 0 or negative, it will not be used.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A <code>JsonNode</code> containing a page (EthosResponse content) of resource data from the given offset with the
     *         given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getAsJsonNode( String resourceName, String version, int offset, int pageSize ) throws IOException {
        EthosResponse ethosResponse = get( resourceName, version, offset, pageSize );
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }

    /**
     * Gets a page of data for the given resource by name and from the given offset index.
     * The default version and page size are used.
     * @param resourceName The name of the resource to get data for.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @return A page of data for the given resource from the given offset using the default page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getFromOffset( String resourceName, int offset ) throws IOException {
        return getFromOffset( resourceName, DEFAULT_VERSION, offset );
    }

    /**
     * Gets a page of data for the given resource by name and version, from the given offset index.
     * The default page size is used.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @return A page of data for the given resource from the given offset using the default page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getFromOffset( String resourceName, String version, int offset ) throws IOException {
        return get( resourceName, version, offset, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets a page of data for the given resource by name and from the given offset index.
     * The default version and page size are used.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A page of data for the given resource from the given offset using the default page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getFromOffset( String resourceName, int offset, Class classType ) throws IOException {
        return getFromOffset( resourceName, DEFAULT_VERSION, offset, classType );
    }

    /**
     * Gets a page of data for the given resource by name and version, from the given offset index.
     * The default page size is used.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A page of data for the given resource from the given offset using the default page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getFromOffset( String resourceName, String version, int offset, Class classType ) throws IOException {
        if( classType == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a GET request for resourceName \"%s\" with version \"%s\" and offset \"%s\" due to a null class type param.", resourceName, version, offset) );
        }
        EthosResponse ethosResponse = getFromOffset( resourceName, version, offset );
        ethosResponse = convertResponseContentToTypedList( ethosResponse, classType );
        return ethosResponse;
    }

    /**
     * Gets a page of data for the given resource by name and from the given offset index.
     * The default version and page size are used.
     * @param resourceName The name of the resource to get data for.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @return A <code>String</code> containing a page (EthosResponse content) of resource data from the given offset with the
     *         default page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getFromOffsetAsString( String resourceName, int offset ) throws IOException {
        return getFromOffsetAsString( resourceName, DEFAULT_VERSION, offset );
    }

    /**
     * Gets a page of data for the given resource by name and version, from the given offset index.
     * The default page size is used.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @return A <code>String</code> containing a page (EthosResponse content) of resource data from the given offset with the
     *         default page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getFromOffsetAsString( String resourceName, String version, int offset ) throws IOException {
        EthosResponse ethosResponse = get( resourceName, version, offset, DEFAULT_PAGE_SIZE );
        return ethosResponseConverter.toContentString( ethosResponse );
    }

    /**
     * Gets a page of data for the given resource by name and from the given offset index.
     * The default version and page size are used.
     * @param resourceName The name of the resource to get data for.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @return A <code>JsonNode</code> containing a page (EthosResponse content) of resource data from the given offset with the
     *         default page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getFromOffsetAsJsonNode( String resourceName, int offset ) throws IOException {
        return getFromOffsetAsJsonNode( resourceName, DEFAULT_VERSION, offset );
    }

    /**
     * Gets a page of data for the given resource by name and version, from the given offset index.
     * The default page size is used.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to get a page of data for the given resource.
     * @return A <code>JsonNode</code> containing a page (EthosResponse content) of resource data from the given offset with the
     *         default page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getFromOffsetAsJsonNode( String resourceName, String version, int offset ) throws IOException {
        EthosResponse ethosResponse = get( resourceName, version, offset, DEFAULT_PAGE_SIZE );
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }

    /**
     * Gets a page of data for the given resource by name using the given page size.  The default version is used from offset index 0.
     * @param resourceName The name of the resource to get data for.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A page of data for the given resource from the given offset using the default page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getWithPageSize( String resourceName, int pageSize ) throws IOException {
        return getWithPageSize( resourceName, DEFAULT_VERSION, pageSize );
    }

    /**
     * Gets a page of data for the given resource by name and version, using the given page size.  Offset index 0 is used.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A page of data for the given resource from the given offset using the default page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getWithPageSize( String resourceName, String version, int pageSize ) throws IOException {
        return get( resourceName, version, 0, pageSize );
    }

    /**
     * Gets a page of data for the given resource by name using the given page size.  The default version is used from offset index 0.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A page of data for the given resource from the given offset using the default page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getWithPageSize( String resourceName, int pageSize, Class classType ) throws IOException {
        return getWithPageSize( resourceName, DEFAULT_VERSION, pageSize );
    }

    /**
     * Gets a page of data for the given resource by name and version, using the given page size.  Offset index 0 is used.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A page of data for the given resource from the given offset using the default page size.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getWithPageSize( String resourceName, String version, int pageSize, Class classType ) throws IOException {
        if( classType == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a GET request for resourceName \"%s\" with version \"%s\" and page size \"%s\" due to a null class type param.", resourceName, version, pageSize) );
        }
        EthosResponse ethosResponse = getWithPageSize( resourceName, version, pageSize );
        ethosResponse = convertResponseContentToTypedList( ethosResponse, classType );
        return ethosResponse;
    }


    /**
     * Gets a page of data for the given resource by name using the given page size.  The default version is used from offset index 0.
     * @param resourceName The name of the resource to get data for.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A <code>String</code> containing a page (EthosResponse content) of resource data from offset index 0 with the
     *         given page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getWithPageSizeAsString( String resourceName, int pageSize ) throws IOException {
        return getWithPageSizeAsString( resourceName, DEFAULT_VERSION, pageSize );
    }

    /**
     * Gets a page of data for the given resource by name and version, using the given page size.  Offset index 0 is used.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A <code>String</code> containing a page (EthosResponse content) of resource data from offset index 0 with the
     *         given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getWithPageSizeAsString( String resourceName, String version, int pageSize ) throws IOException {
        EthosResponse ethosResponse = get( resourceName, version, 0, pageSize );
        return ethosResponseConverter.toContentString( ethosResponse );
    }

    /**
     * Gets a page of data for the given resource by name using the given page size.  The default version is used from offset index 0.
     * @param resourceName The name of the resource to get data for.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A <code>JsonNode</code> containing a page (EthosResponse content) of resource data from offset index 0 with the
     *         given page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getWithPageSizeAsJsonNode( String resourceName, int pageSize ) throws IOException {
        return getWithPageSizeAsJsonNode( resourceName, DEFAULT_VERSION, pageSize );
    }

    /**
     * Gets a page of data for the given resource by name and version, using the given page size.  Offset index 0 is used.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in the returned page (EthosResponse).
     * @return A <code>JsonNode</code> containing a page (EthosResponse content) of resource data from offset index 0 with the
     *         given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getWithPageSizeAsJsonNode( String resourceName, String version, int pageSize ) throws IOException {
        EthosResponse ethosResponse = get( resourceName, version, 0, pageSize );
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }


    /**
     * Gets all pages for the given resource.  Uses the default page size of the response body content length, and the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @return A list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPages( String resourceName ) throws IOException {
        return getAllPages( resourceName, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets all pages for the given resource and version.  Uses the default page size of the response body content length.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return A list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPages( String resourceName, String version ) throws IOException {
        return getAllPages( resourceName, version, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets all pages for the given resource and page size.  Uses the default version of the resource.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @return A list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the current version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPages( String resourceName, int pageSize ) throws IOException {
        return getAllPages( resourceName, DEFAULT_VERSION, pageSize );
    }

    /**
     * Gets all pages for the given resource, version, and page size.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @return A list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPages( String resourceName, String version, int pageSize ) throws IOException {
        List<EthosResponse> ethosResponseList = new ArrayList();
        if( resourceName == null || resourceName.trim().isEmpty() ) {
            return ethosResponseList;
        }
        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .build();

        pager = prepareForPaging( pager );

        pager = shouldDoPaging( pager, false );

        pager.setHowToPage( Pager.PagingType.PAGE_ALL_PAGES );
        ethosResponseList = handlePaging( pager );
        return ethosResponseList;
    }

    /**
     * Gets all pages for the given resource, version, and page size.
     * The response body is returned within each EthosResponse from the returned list as a list of objects of the given class type.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPages( String resourceName, String version, int pageSize, Class classType ) throws IOException {
        if( classType == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot submit a GET request for all pages with resourceName \"%s\", version \"%s\", and page size \"%s\" due to a null class type param.", resourceName, version, pageSize) );
        }
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, version, pageSize );
        ethosResponseList = convertResponsesContentToTypedList( ethosResponseList, classType );
        return ethosResponseList;
    }

    /**
     * Gets all pages for the given resource and page size.  Uses the default version of the resource.
     * The response body is returned within each EthosResponse from the returned list as a list of objects of the given class type.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the current version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPages( String resourceName, int pageSize, Class classType ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, pageSize );
        ethosResponseList = convertResponsesContentToTypedList( ethosResponseList, classType );
        return ethosResponseList;
    }

    /**
     * Gets all pages for the given resource and version.  Uses the default page size of the response body content length.
     * The response body is returned within each EthosResponse from the returned list as a list of objects of the given class type.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPages( String resourceName, String version, Class classType ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, version );
        ethosResponseList = convertResponsesContentToTypedList( ethosResponseList, classType );
        return ethosResponseList;
    }

    /**
     * Gets all pages for the given resource.  Uses the default page size of the response body content length, and the default version.
     * The response body is returned within each EthosResponse from the returned list as a list of objects of the given class type.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPages( String resourceName, Class classType ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName );
        ethosResponseList = convertResponsesContentToTypedList( ethosResponseList, classType );
        return ethosResponseList;
    }

    /**
     * Gets all pages for the given resource.  Uses the default page size of the response body content length, and the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @return A list of <code>String</code>s where each <code>String</code> in the list
     *         represents a page of data according to the default version of the resource.  The page size of each page
     *         is determined by the response body content length.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getAllPagesAsStrings( String resourceName ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, DEFAULT_PAGE_SIZE );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource and version.  Uses the default page size of the response body content length.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return A list of <code>String</code>s where each <code>String</code> in the list
     *         represents a page of data according to the requested version of the resource.  The page size of each page
     *         is determined by the response body content length.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getAllPagesAsStrings(String resourceName, String version ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, version, DEFAULT_PAGE_SIZE );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource and page size.  Uses the default version of the resource.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @return A list of <code>String</code>s where each <code>String</code> in the list
     *         represents a page of data with the given page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getAllPagesAsStrings( String resourceName, int pageSize ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, DEFAULT_VERSION, pageSize );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource, version, and page size.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @return A list of <code>String</code>s where each <code>String</code> in the list
     *         represents a page of data with the given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getAllPagesAsStrings( String resourceName, String version, int pageSize ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, version, pageSize );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource.  Uses the default page size of the response body content length, and the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list
     *         represents a page of data according to the default version of the resource.  The page size of each page
     *         is determined by the response body content length.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getAllPagesAsJsonNodes( String resourceName ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, DEFAULT_PAGE_SIZE );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource and version.  Uses the default page size of the response body content length.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list
     *         represents a page of data according to the requested version of the resource.  The page size of each page
     *         is determined by the response body content length.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getAllPagesAsJsonNodes( String resourceName, String version ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, version, DEFAULT_PAGE_SIZE );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource and page size.  Uses the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list
     *         represents a page of data with the given page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getAllPagesAsJsonNodes( String resourceName, int pageSize ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, DEFAULT_VERSION, pageSize );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource, version, and page size.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list
     *         represents a page of data with the given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getAllPagesAsJsonNodes( String resourceName, String version, int pageSize ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPages( resourceName, version, pageSize );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource from the given offset.  Uses the default page size of the response body content length,
     * and the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPagesFromOffset( String resourceName, int offset ) throws IOException {
        return getAllPagesFromOffset( resourceName, offset, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets all pages for the given resource, offset, and page size.  Uses the default version of the resource.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @return A list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPagesFromOffset( String resourceName, int offset, int pageSize ) throws IOException {
        return getAllPagesFromOffset( resourceName, DEFAULT_VERSION, offset, pageSize );
    }


    /**
     * Gets all pages for the given resource, version, and offset.  Uses the default page size of the response body content length.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the response content body length as the page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPagesFromOffset( String resourceName, String version, int offset ) throws IOException {
        return getAllPagesFromOffset( resourceName, version, offset, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets all pages for the given resource, version, and page size, from the offset.  If the offset is negative, all pages
     * will be returned.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @return A list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPagesFromOffset( String resourceName, String version, int offset, int pageSize ) throws IOException {
        List<EthosResponse> ethosResponseList = new ArrayList();
        if( resourceName == null || resourceName.trim().isEmpty() ) {
            return ethosResponseList;
        }
        if( offset < 1 ) {
            // Just get all pages if offset is < 1.
            return getAllPages( resourceName, version, pageSize );
        }
        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .fromOffset(offset)
                               .build();

        pager = prepareForPaging( pager );

        pager = shouldDoPaging( pager, false );

        pager.setHowToPage( Pager.PagingType.PAGE_FROM_OFFSET );

        ethosResponseList = handlePaging( pager );

        return ethosResponseList;
    }

    /**
     * Gets all pages for the given resource, version, and page size, from the offset.  If the offset is negative, all pages
     * will be returned.
     * The response body is returned within each EthosResponse from the returned list as a list of objects of the given class type.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPagesFromOffset( String resourceName, String version, int offset, int pageSize, Class classType ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, version, offset, pageSize );
        ethosResponseList = convertResponsesContentToTypedList( ethosResponseList, classType );
        return ethosResponseList;
    }

    /**
     * Gets all pages for the given resource, version, and offset.  Uses the default page size of the response body content length.
     * The response body is returned within each EthosResponse from the returned list as a list of objects of the given class type.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the response content body length as the page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPagesFromOffset( String resourceName, String version, int offset, Class classType ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, version, offset );
        ethosResponseList = convertResponsesContentToTypedList( ethosResponseList, classType );
        return ethosResponseList;
    }

    /**
     * Gets all pages for the given resource, offset, and page size.  Uses the default version of the resource.
     * The response body is returned within each EthosResponse from the returned list as a list of objects of the given class type.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPagesFromOffset( String resourceName, int offset, int pageSize, Class classType ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, offset, pageSize );
        ethosResponseList = convertResponsesContentToTypedList( ethosResponseList, classType );
        return ethosResponseList;
    }

    /**
     * Gets all pages for the given resource from the given offset.  Uses the default page size of the response body content length,
     * and the default version.
     * The response body is returned within each EthosResponse from the returned list as a list of objects of the given class type.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return A list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getAllPagesFromOffset( String resourceName, int offset, Class classType ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, offset );
        ethosResponseList = convertResponsesContentToTypedList( ethosResponseList, classType );
        return ethosResponseList;
    }

    /**
     * Gets all pages for the given resource from the given offset.  Uses the default page size of the response body content length,
     * and the default version of the resource.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data from
     *         the given offset with the response content body length as the page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getAllPagesFromOffsetAsStrings( String resourceName, int offset ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, offset, DEFAULT_PAGE_SIZE );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource from the given offset with the given page size.  Uses the default version of the resource.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data from
     *         the given offset with the given page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getAllPagesFromOffsetAsStrings( String resourceName, int offset, int pageSize ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, DEFAULT_VERSION, offset, pageSize );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource and version from the given offset.  Uses the default page size of the response body content length.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data from
     *         the given offset with the response content body length as the page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getAllPagesFromOffsetAsStrings( String resourceName, String version, int offset ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, version, offset, DEFAULT_PAGE_SIZE );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource, version, and page size from the given offset.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data from
     *         the given offset with the given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getAllPagesFromOffsetAsStrings( String resourceName, String version, int offset, int pageSize ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, version, offset, pageSize );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }


    /**
     * Gets all pages for the given resource from the given offset.  Uses the default page size of the response body content length,
     * and the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data from
     *         the given offset with the response content body length as the page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getAllPagesFromOffsetAsJsonNodes( String resourceName, int offset ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, offset, DEFAULT_PAGE_SIZE );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource and page size from the given offset.  Uses the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data from
     *         the given offset with the given page size according to the default version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getAllPagesFromOffsetAsJsonNodes( String resourceName, int offset, int pageSize ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, DEFAULT_VERSION, offset, pageSize );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }


    /**
     * Gets all pages for the given resource and version from the given offset.  Uses the default page size of the response body content length.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data from
     *         the given offset with the response content body length as the page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getAllPagesFromOffsetAsJsonNodes( String resourceName, String version, int offset ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, version, offset, DEFAULT_PAGE_SIZE );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets all pages for the given resource, version, and page size, from the given offset.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data from
     *         the given offset with the given page size according to the requested version of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getAllPagesFromOffsetAsJsonNodes( String resourceName, String version, int offset, int pageSize ) throws IOException {
        List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, version, offset, pageSize );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource.  Uses the default page size of the response body content length,
     * and default version.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource, up to the
     *         number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPages( String resourceName, int numPages ) throws IOException {
        return getPages( resourceName, DEFAULT_VERSION, numPages );
    }

    /**
     * Gets some number of pages for the given resource and version.  Uses the default page size of the response body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource, up to the
     *         number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPages( String resourceName, String version, int numPages ) throws IOException {
        return getPages( resourceName, version, DEFAULT_PAGE_SIZE, numPages );
    }


    /**
     * Gets some number of pages for the given resource and page size.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource, up to the
     *         number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPages( String resourceName, int pageSize, int numPages ) throws IOException {
        return getPages( resourceName, DEFAULT_VERSION, pageSize, numPages );
    }

    /**
     * Gets some number of pages for the given resource, version, and page size.  If numPages is negative, all pages
     * will be returned.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource, up to the
     *         number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPages( String resourceName, String version, int pageSize, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = new ArrayList();
        if( resourceName == null || resourceName.trim().isEmpty() ) {
            return ethosResponseList;
        }
        if( numPages < 1 ) {
            // Just get all pages if numPages is < 1.
            return getAllPages( resourceName, version, pageSize );
        }
        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .forNumPages(numPages)
                               .build();

        pager = prepareForPaging( pager );

        pager = shouldDoPaging( pager, false );

        pager.setHowToPage( Pager.PagingType.PAGE_TO_NUMPAGES );

        ethosResponseList = handlePaging( pager );

        return ethosResponseList;
    }

    /**
     * Gets some number of pages for the given resource.  Uses the default page size of the response body content length,
     * and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getPagesAsStrings( String resourceName, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPages( resourceName, DEFAULT_VERSION, numPages );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource.  Uses the default page size of the response body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getPagesAsStrings( String resourceName, String version, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPages( resourceName, version, DEFAULT_PAGE_SIZE, numPages );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource and page size.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getPagesAsStrings( String resourceName, int pageSize, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPages( resourceName, DEFAULT_VERSION, pageSize, numPages );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource, version, and page size.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getPagesAsStrings( String resourceName, String version, int pageSize, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPages( resourceName, version, pageSize, numPages );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource.  Uses the default page size of the response body content length,
     * and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getPagesAsJsonNodes( String resourceName, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPages( resourceName, DEFAULT_VERSION, numPages );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource and version.  Uses the default page size of the response body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getPagesAsJsonNodes( String resourceName, String version, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPages( resourceName, version, DEFAULT_PAGE_SIZE, numPages );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource and page size.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getPagesAsJsonNodes( String resourceName, int pageSize, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPages( resourceName, DEFAULT_VERSION, pageSize, numPages );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource, version, and page size.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getPagesAsJsonNodes( String resourceName, String version, int pageSize, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPages( resourceName, version, pageSize, numPages );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource from the given offset.  Uses the default page size of the response
     * body content length, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffset( String resourceName, int offset, int numPages ) throws IOException {
        return getPagesFromOffset( resourceName, DEFAULT_VERSION, offset, numPages );
    }

    /**
     * Gets some number of pages for the given resource and page size from the given offset.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffset( String resourceName, int pageSize, int offset, int numPages ) throws IOException {
        return getPagesFromOffset( resourceName, DEFAULT_VERSION, pageSize, offset, numPages );
    }

    /**
     * Gets some number of pages for the given resource and version from the given offset.  Uses the default page size of the response
     * body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffset( String resourceName, String version, int offset, int numPages ) throws IOException {
        return getPagesFromOffset( resourceName, version, DEFAULT_PAGE_SIZE, offset, numPages );
    }

    /**
     * Gets some number of pages for the given resource, version, and page size, from the given offset.  If both the offset
     * and numPages are negative, all pages will be returned.  If the offset is negative, pages up to the numPages will
     * be returned.  If numPages is negative, all pages from the offset will be returned.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffset( String resourceName, String version, int pageSize, int offset, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = new ArrayList();
        if( resourceName == null || resourceName.trim().isEmpty() ) {
            return ethosResponseList;
        }
        if( offset < 1  &&  numPages < 1 ) {
            // Just get all pages if offset is < 1 and numPages < 1.
            return getAllPages( resourceName, version, pageSize );
        }
        if( offset < 1 ) {
            // If offset is < 1, get up to the num pages because numPages is >= 1.
            return getPages( resourceName, version, pageSize, numPages );
        }
        if( numPages < 1 ) {
            // If numPages < 1, get from the offset because the offset is >= 1.
            return getAllPagesFromOffset( resourceName, version, offset, pageSize );
        }
        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .forNumPages(numPages)
                               .fromOffset(offset)
                               .build();

        pager = prepareForPaging( pager );

        pager = shouldDoPaging( pager, false );

        pager.setHowToPage( Pager.PagingType.PAGE_FROM_OFFSET_FOR_NUMPAGES );

        ethosResponseList = handlePaging( pager );

        return ethosResponseList;
    }

    /**
     * Gets some number of pages for the given resource from the given offset.  Uses the default page size of the response
     * body content length, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getPagesFromOffsetAsStrings( String resourceName, int offset, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPagesFromOffset( resourceName, DEFAULT_VERSION, offset, numPages );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource and page size from the given offset.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getPagesFromOffsetAsStrings( String resourceName, int pageSize, int offset, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPagesFromOffset( resourceName, DEFAULT_VERSION, pageSize, offset, numPages );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource and version from the given offset.  Uses the default page size of the response
     * body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getPagesFromOffsetAsStrings( String resourceName, String version, int offset, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPagesFromOffset( resourceName, version, DEFAULT_PAGE_SIZE, offset, numPages );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource, version, and page size, from the given offset.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getPagesFromOffsetAsStrings( String resourceName, String version, int pageSize, int offset, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        return ethosResponseConverter.toPageBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource from the given offset.  Uses the default page size of the response
     * body content length, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getPagesFromOffsetAsJsonNodes( String resourceName, int offset, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPagesFromOffset( resourceName, DEFAULT_VERSION, offset, numPages );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource and page size, from the given offset.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getPagesFromOffsetAsJsonNodes( String resourceName, int pageSize, int offset, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPagesFromOffset( resourceName, DEFAULT_VERSION, pageSize, offset, numPages );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource and version, from the given offset.  Uses the default page size of the response
     * body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getPagesFromOffsetAsJsonNodes( String resourceName, String version, int offset, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPagesFromOffset( resourceName, version, DEFAULT_PAGE_SIZE, offset, numPages );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of pages for the given resource, version, and page size, from the given offset.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with given page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getPagesFromOffsetAsJsonNodes( String resourceName, String version, int pageSize, int offset, int numPages ) throws IOException {
        List<EthosResponse> ethosResponseList = getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
        return ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource.  The number of rows is returned as a
     * paged-based list of EthosResponses, altogether containing the number of rows.  Uses the default page size
     * of the response body content length, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getRows( String resourceName, int numRows ) throws IOException {
        return getRows( resourceName, DEFAULT_VERSION, numRows );
    }

    /**
     * Gets some number of rows for the given resource and version.  The number of rows is returned as a
     * paged-based list of EthosResponses, altogether containing the number of rows.  Uses the default page size of the
     * response body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getRows( String resourceName, String version, int numRows ) throws IOException {
        return getRows( resourceName, version, DEFAULT_PAGE_SIZE, numRows );
    }

    /**
     * Gets some number of rows for the given resource and page size.  The number of rows is returned as a
     * paged-based list of EthosResponses, altogether containing the number of rows.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getRows( String resourceName, int pageSize, int numRows ) throws IOException {
        return getRows( resourceName, DEFAULT_VERSION, pageSize, numRows );
    }

    /**
     * Gets some number of rows for the given resource, version, and page size.  The number of rows is returned as a
     * paged-based list of EthosResponses, altogether containing the number of rows.  If numRows is negative, all pages will be returned.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getRows( String resourceName, String version, int pageSize, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = new ArrayList();
        if( resourceName == null || resourceName.trim().isEmpty() ) {
            return ethosResponseList;
        }
        if( numRows < 1 ) {
            // If numRows < 1, just get all pages.
            return getAllPages( resourceName, version, pageSize );
        }
        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .forNumRows(numRows)
                               .build();

        pager = prepareForPaging( pager );

        pager = shouldDoPaging( pager, true );

        pager.setHowToPage( Pager.PagingType.PAGE_TO_NUMROWS );

        ethosResponseList = handlePaging( pager );

        return ethosResponseList;
    }

    /**
     * Gets some number of rows for the given resource.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default version of the
     * resource, and the default page size of the response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getRowsAsStrings( String resourceName, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRows( resourceName, DEFAULT_VERSION, numRows );
        return ethosResponseConverter.toRowBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getRowsAsStrings( String resourceName, String version, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRows( resourceName, version, DEFAULT_PAGE_SIZE, numRows );
        return ethosResponseConverter.toRowBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource and page size.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *                 internally by the SDK during paging operations.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getRowsAsStrings( String resourceName, int pageSize, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRows( resourceName, DEFAULT_VERSION, pageSize, numRows );
        return ethosResponseConverter.toRowBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource, version, and page size.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *        internally by the SDK during paging operations.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getRowsAsStrings( String resourceName, String version, int pageSize, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRows( resourceName, version, pageSize, numRows );
        return ethosResponseConverter.toRowBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.   Uses the default version of the
     * resource, and the default page size of the response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getRowsAsJsonNodes( String resourceName, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRows( resourceName, DEFAULT_VERSION, numRows );
        return ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource and version.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getRowsAsJsonNodes( String resourceName, String version, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRows( resourceName, version, DEFAULT_PAGE_SIZE, numRows );
        return ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource and page size.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *        internally by the SDK during paging operations.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getRowsAsJsonNodes( String resourceName, int pageSize, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRows( resourceName, DEFAULT_VERSION, pageSize, numRows );
        return ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource, version, and page size.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *        internally by the SDK during paging operations.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getRowsAsJsonNodes( String resourceName, String version, int pageSize, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRows( resourceName, version, pageSize, numRows );
        return ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource from the given offset.  The number of rows is returned in a list of
     * pages altogether containing the number of rows.  Uses the default page size of the response body content length
     * and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getRowsFromOffset( String resourceName, int offset, int numRows ) throws IOException {
        return getRowsFromOffset( resourceName, DEFAULT_PAGE_SIZE, offset, numRows );
    }

    /**
     * Gets some number of rows for the given resource and page size from the given offset.  The number of rows is returned in a list of
     * pages altogether containing the number of rows.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getRowsFromOffset( String resourceName, int pageSize, int offset, int numRows ) throws IOException {
        return getRowsFromOffset( resourceName, DEFAULT_VERSION, pageSize, offset, numRows );
    }

    /**
     * Gets some number of rows for the given resource and version from the given offset.  The number of rows is returned in a list of
     * pages altogether containing the number of rows.  Uses the default page size of the response body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getRowsFromOffset( String resourceName, String version, int offset, int numRows ) throws IOException {
        return getRowsFromOffset( resourceName, version, DEFAULT_PAGE_SIZE, offset, numRows );
    }

    /**
     * Gets some number of rows for the given resource, version, and page size, from the given offset.  The number of rows is returned in a list of
     * pages altogether containing the number of rows.  If both the offset and numRows are negative, all pages will be returned.
     * If the offset is negative, pages up to the numRows will be returned.  If numRows is negative, all pages from the offset will be returned.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getRowsFromOffset( String resourceName, String version, int pageSize, int offset, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = new ArrayList();
        if( resourceName == null || resourceName.trim().isEmpty() ) {
            return ethosResponseList;
        }
        if( offset < 1  &&  numRows < 1 ) {
            // Just get all pages if offset is < 1 and numRows < 1.
            return getAllPages( resourceName, version, pageSize );
        }
        if( offset < 1 ) {
            // If offset is < 1, get up to the num rows because numRows is >= 1.
            return getRows( resourceName, version, pageSize, numRows );
        }
        if( numRows < 1 ) {
            // If numRows < 1, get all pages from the offset because the offset is >= 1.
            return getAllPagesFromOffset( resourceName, version, offset, pageSize );
        }

        Pager pager = new Pager.Builder(resourceName)
                               .forVersion(version)
                               .withPageSize(pageSize)
                               .fromOffset(offset)
                               .forNumRows(numRows)
                               .build();

        pager = prepareForPaging( pager );

        pager = shouldDoPaging( pager, true );

        pager.setHowToPage( Pager.PagingType.PAGE_FROM_OFFSET_FOR_NUMROWS );

        ethosResponseList = handlePaging( pager );

        return ethosResponseList;
    }

    /**
     * Gets some number of rows for the given resource from the given offset.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getRowsFromOffsetAsStrings( String resourceName, int offset, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRowsFromOffset( resourceName, DEFAULT_PAGE_SIZE, offset, numRows );
        return ethosResponseConverter.toRowBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource and page size, from the given offset.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *                 internally by the SDK during paging operations.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getRowsFromOffsetAsStrings( String resourceName, int pageSize, int offset, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRowsFromOffset( resourceName, DEFAULT_VERSION, pageSize, offset, numRows );
        return ethosResponseConverter.toRowBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource and version, from the given offset.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getRowsFromOffsetAsStrings( String resourceName, String version, int offset, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRowsFromOffset( resourceName, version, DEFAULT_PAGE_SIZE, offset, numRows );
        return ethosResponseConverter.toRowBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource, version, and page size, from the given offset.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize  The number of rows to include in each page during execution of paging operations.  This value is used
     *        internally by the SDK during paging operations.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getRowsFromOffsetAsStrings( String resourceName, String version, int pageSize, int offset, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        return ethosResponseConverter.toRowBasedStringList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource from the given offset.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getRowsFromOffsetAsJsonNodes( String resourceName, int offset, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRowsFromOffset( resourceName, DEFAULT_PAGE_SIZE, offset, numRows );
        return ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource and page size, from the given offset.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *        internally by the SDK during paging operations.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getRowsFromOffsetAsJsonNodes( String resourceName, int pageSize, int offset, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRowsFromOffset( resourceName, DEFAULT_VERSION, pageSize, offset, numRows );
        return ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource and version, from the given offset.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getRowsFromOffsetAsJsonNodes( String resourceName, String version, int offset, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRowsFromOffset( resourceName, version, DEFAULT_PAGE_SIZE, offset, numRows );
        return ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets some number of rows for the given resource, version, and page size, from the given offset.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *        internally by the SDK during paging operations.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<JsonNode> getRowsFromOffsetAsJsonNodes( String resourceName, String version, int pageSize, int offset, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
        return ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
    }

    /**
     * Gets a resource by ID (GUID) for the given resource name.  Uses the default version.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param id The unique ID (GUID) of the resource to get.
     * @return The data for a given resource in an <code>EthosResponse</code> according to the default version of the resource.  The
     *         <code>EthosResponse</code> contains the content body of the resource data as well as headers and the Http status code.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getById( String resourceName, String id ) throws IOException {
        return getById(resourceName, id, DEFAULT_VERSION);
    }

    /**
     * Gets a resource by ID (GUID) for the given resource name and version.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param id The unique ID (GUID) of the resource to get.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return The data for a given resource in an <code>EthosResponse</code> according to the requested version of the resource.
     *         The <code>EthosResponse</code> contains the content body of the resource data as well as headers and
     *         the Http status code.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getById( String resourceName, String id, String version ) throws IOException {
        Map<String,String> headersMap = buildHeadersMap( version );
        String url = EthosIntegrationUrls.apis(getRegion(), resourceName, id);
        EthosResponse ethosResponse = get( url, headersMap );
        return ethosResponse;
    }

    /**
     * Gets a resource by ID (GUID) for the given resource name.  Uses the default version.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param id The unique ID (GUID) of the resource to get.
     * @return The data for a given resource as a <code>String</code> according to the default version of the resource.
     *         Only returns the content body of the <code>EthosResponse</code>.  Does not return header information or the Http status code.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getByIdAsString(String resourceName, String id ) throws IOException {
        EthosResponse ethosResponse = getById( resourceName, id );
        return ethosResponseConverter.toContentString( ethosResponse );
    }

    /**
     * Gets a resource by ID (GUID) for the given resource name and version.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param id The unique ID (GUID) of the resource to get.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return The data for a given resource as a <code>String</code> according to the requested version of the resource.
     *         Only returns the content body of the <code>EthosResponse</code>.  Does not return header information or the Http status code.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getByIdAsString(String resourceName, String id, String version ) throws IOException {
        EthosResponse ethosResponse = getById( resourceName, id, version );
        return ethosResponseConverter.toContentString( ethosResponse );
    }

    /**
     * Gets a resource by ID (GUID) for the given resource name.  Uses the default version.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param id The unique ID (GUID) of the resource to get.
     * @return The data for a given resource as a <code>JsonNode</code> according to the default version of the resource.
     *         Only returns the content body of the <code>EthosResponse</code>.  Does not return header information or the Http status code.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getByIdAsJsonNode(String resourceName, String id ) throws IOException {
        EthosResponse ethosResponse = getById( resourceName, id );
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }

    /**
     * Gets a resource by ID (GUID) for the given resource name and version.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param id The unique ID (GUID) of the resource to get.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return The data for a given resource as a <code>JsonNode</code> according to the requested version of the resource.
     *         Only returns the content body of the <code>EthosResponse</code>.  Does not return header information or the Http status code.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getByIdAsJsonNode(String resourceName, String id, String version ) throws IOException {
        EthosResponse ethosResponse = getById( resourceName, id, version );
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Builds a map of headers with the given version.  If the version is null or empty, uses the default version.
     * @param version the version to use for the Accept and Content-Type headers, as supplied in the returned map.
     * @return a <code>Map&lt;String,String&gt;</code> of header values including Accept and Content-Type (both set to
     *         the given version.
     */
    protected Map<String,String> buildHeadersMap( String version ) {
        Map<String, String> headers = new HashMap();
        if( version == null || version.isBlank() ) {
            version = DEFAULT_VERSION;
        }
        headers.put( "Accept", version );
        headers.put( "Content-Type", version );
        return headers;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Uses the given pager object to prepare for paging operations.  The pager object is used to contain various
     * fields required for paging.  If the given pager is null, returns the same pager.  Sets default values for the
     * version and offset within the pager as needed and makes an initial call for the pager resource to get metadata
     * about the resource used for paging.  Also sets the page size and the total count within the pager.
     * @param pager The Pager object used holding the required fields for paging.
     * @return The same pager object with the version and offset validated, and the page size and total count set.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected Pager prepareForPaging( Pager pager ) throws IOException {
        if( pager == null ) {
            return pager;
        }
        if( pager.getVersion() == null || pager.getVersion().trim().isEmpty() ) {
            pager.setVersion(DEFAULT_VERSION);
        }
        if( pager.getOffset() < 1 ) {
            pager.setOffset( 0 );
        }

        // First make a GET list call without filters to get the x-total-count.
        EthosResponse ethosResponse = get( pager.getResourceName(), pager.getVersion() );
        pager.setEthosResponse( ethosResponse );

        // Set the pageSize.
        if( pager.getPageSize() <= DEFAULT_PAGE_SIZE ) {
            // Set the pageSize from the response body length, if pageSize is <= DEFAULT_PAGE_SIZE.
            int pageSize = getPageSize( pager.getResourceName(), pager.getVersion(), pager.getEthosResponse() );
            pager.setPageSize( pageSize );
        }
        else {
            // Ensure the page size is not greater than the max page size.  If it is, set the page size to be the max page size.
            int maxPageSize = getMaxPageSize( pager.getResourceName(), pager.getVersion(), pager.getEthosResponse() );
            if( pager.getPageSize() > maxPageSize ) {
                pager.setPageSize( maxPageSize );
            }
        }

        String totalCount = getHeaderValue( ethosResponse, HDR_X_TOTAL_COUNT );
        pager.setTotalCount( Integer.valueOf(totalCount) );
        return pager;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Determines whether paging should be done for the resource within the given pager.
     * Supports normal paging (by total count) or paging for number of rows.  If normal paging, then the need to page
     * is determined by comparing the total count with the page size.  If paging for number of rows, the need to page
     * is determined by comparing the number of rows with the page size.
     * @param pager The Pager object containing the total count or numRows, and page size to determine the need to page.
     * @param forNumRows If true, then paging is by numRows.  If false, then paging is by total count.
     * @return the given pager object with the shouldDoPaging flag set to true when paging is needed, or false when not.
     */
    protected Pager shouldDoPaging( Pager pager, boolean forNumRows ) {
        int total = forNumRows ? pager.getNumRows() : pager.getTotalCount();
        boolean shouldPage = needToPage( pager.getPageSize(), total );
        pager.setShouldDoPaging( shouldPage );
        return pager;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Determines the need to page by comparing the proxyApiResponseBody as a JsonNode to the given total count.
     * @param proxyApiResponseBody A Json array of rows (as a single String) equivalent to a single page of some resource.
     * @param totalCount the total count of rows for some resource.
     * @return true if paging is needed, false otherwise.
     * @throws JsonProcessingException Thrown if the Jackson <code>objectMapper</code> cannot read the proxyApiResponseBody.
     */
    protected boolean needToPage( String proxyApiResponseBody, int totalCount ) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree( proxyApiResponseBody );
        return needToPage( jsonNode, totalCount );
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Determines the need to page given a JsonNode and the total count of rows for some resource.
     * The size of the JsonNode is compared to the given total count to determine if paging is needed.
     * @param jsonNode A <code>JsonNode</code> containing a page of resource data.
     * @param totalCount The total count of rows for some resource.
     * @return true if paging is needed, false otherwise.
     */
    protected boolean needToPage( JsonNode jsonNode, int totalCount ) {
        boolean needPaging = false;
        if( jsonNode == null || jsonNode.isArray() == false ) {
            return needPaging;
        }
        return needToPage( jsonNode.size(), totalCount );
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * A simple comparison which is used by the other needToPage() methods, comparing the given page size to the
     * total count.  If the page size is less than the total count, then paging is needed.  If not, paging is not needed.
     * @param pageSize The page size for some resource.
     * @param totalCount The total count for some resource.
     * @return true if the given page size is less than the total count, false if not.
     */
    protected boolean needToPage( int pageSize, int totalCount ) {
        return pageSize < totalCount;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * For some resource defined within the given pager, determines whether to page for data, or to use the data
     * obtained during paging preparation.
     * @param pager A pager previously prepared for paging (see <code>prepareForPaging()</code> and <code>shouldDoPaging()</code>.
     * @return a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list contains a page of data.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected List<EthosResponse> handlePaging( Pager pager ) throws IOException {
        List<EthosResponse> ethosResponseList;
        if( pager.isShouldDoPaging() ) {
            ethosResponseList = getDataFromPaging( pager );
        }
        else {
            ethosResponseList = getDataFromInitialContent( pager );
        }
        return ethosResponseList;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Determines how to page given the "howToPage" attribute of the pager.  The howToPage attribute must be one of the
     * defined PagingTypes.
     * @param pager A previously prepared pager with the howToPage attribute set appropriately.
     * @return a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list contains a page of data.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected List<EthosResponse> getDataFromPaging( Pager pager ) throws IOException {
        List<EthosResponse> ethosResponseList = new ArrayList();
        switch( pager.getHowToPage() ) {
            case PAGE_ALL_PAGES:
                ethosResponseList = doPagingForAll( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize() );
                break;
            case PAGE_TO_NUMPAGES:
                ethosResponseList = doPagingForNumPages( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getNumPages() );
                break;
            case PAGE_FROM_OFFSET:
                ethosResponseList = doPagingFromOffset( pager.getResourceName(), pager.getVersion(), null, pager.getTotalCount(), pager.getPageSize(), pager.getOffset() );
                break;
            case PAGE_FROM_OFFSET_FOR_NUMPAGES:
                ethosResponseList = doPagingFromOffsetForNumPages( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getNumPages(), pager.getOffset() );
                break;
            case PAGE_TO_NUMROWS:
                ethosResponseList = doPagingForNumRows( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getNumRows() );
                break;
            case PAGE_FROM_OFFSET_FOR_NUMROWS:
                ethosResponseList = doPagingFromOffsetForNumRows( pager.getResourceName(), pager.getVersion(), pager.getTotalCount(), pager.getPageSize(), pager.getOffset(), pager.getNumRows() );
                break;
        }
        return ethosResponseList;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Gets resource data from the initial content obtained when preparing for paging.  This method does not page for data.
     * Supports trimming the data content according to the given PagingType (howToPage attribute).
     * @param pager A pager previously prepared for paging.
     * @return A list of <code>EthosResponse</code>s containing a single page of data, which may be trimmed according to the
     *         howToPage PagingType specified in the pager.
     * @throws JsonProcessingException Propagated from the ethosResponseConverter if an error occurs when trimming the data.
     */
    protected List<EthosResponse> getDataFromInitialContent( Pager pager ) throws JsonProcessingException {
        List<EthosResponse> responseList = new ArrayList();
        switch( pager.getHowToPage() ) {
            case PAGE_ALL_PAGES:
            case PAGE_TO_NUMPAGES:
                responseList.add( pager.getEthosResponse() );
                break;
            case PAGE_TO_NUMROWS:  // Truncate the existing ethosResponse content to the numRows.
                responseList.add( ethosResponseConverter.trimContentForNumRows(pager.getEthosResponse(), pager.getNumRows()) );
                break;
            case PAGE_FROM_OFFSET: // Get data from the existing ethosResponse content from the offset row.
            case PAGE_FROM_OFFSET_FOR_NUMPAGES:
                responseList.add( ethosResponseConverter.trimContentFromOffset(pager.getEthosResponse(), pager.getOffset()) );
                break;
            case PAGE_FROM_OFFSET_FOR_NUMROWS:
                responseList.add( ethosResponseConverter.trimContentFromOffsetForNumRows(pager.getEthosResponse(), pager.getOffset(), pager.getNumRows()) );
                break;
        }
        return responseList;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Used within the SDK to page for all pages for a given resource by using an offset of 0.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param totalCount The total count of rows for the given resource.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected List<EthosResponse> doPagingForAll( String resourceName, String version, int totalCount, int pageSize ) throws IOException {
        return doPagingFromOffset( resourceName, version, null, totalCount, pageSize, 0 );
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param filter The string resource filter in JSON format contained in the URL, e.g: <pre>?criteria={"names":[{"firstName":"John"}]}</pre>
     * @param totalCount The total count of rows for the given resource.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page,
     *         beginning from the given offset index.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected List<EthosResponse> doPagingFromOffset( String resourceName, String version, String filter, int totalCount, int pageSize, int offset ) throws IOException {
        List<EthosResponse> ethosResponseList = new ArrayList();
        Map<String,String> headers = buildHeadersMap( version );
        double numPages = Math.ceil( (Double.valueOf(totalCount) - Double.valueOf(offset)) / Double.valueOf(pageSize) );
        for( int i = 0; i < numPages; i++ ) {
            String url = "";
            if( filter == null || filter.isBlank() ) {
                url = EthosIntegrationUrls.apiPaging(getRegion(), resourceName, offset, pageSize);
            }
            else {
                url = EthosIntegrationUrls.apiFilterPaging(getRegion(), resourceName, filter, offset, pageSize);
            }
            EthosResponse response = get( url, headers );
            ethosResponseList.add( response );
            offset += pageSize;
        }
        return ethosResponseList;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Used within the SDK to page for some number of pages for a given resource by using an offset of 0.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param totalCount The total count of rows for the given resource.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numPages The number of pages to page for.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page,
     *         up to some number of pages.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected List<EthosResponse> doPagingForNumPages( String resourceName, String version, int totalCount, int pageSize, int numPages ) throws IOException {
        return doPagingFromOffsetForNumPages( resourceName, version, totalCount, pageSize, numPages, 0 );
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Used within the SDK to page for some number of pages for a given resource from the given offset.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param totalCount The total count of rows for the given resource.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numPages The number of pages to page for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page,
     *         from the given offset (inclusive) and up to some number of pages (exclusive).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected List<EthosResponse> doPagingFromOffsetForNumPages( String resourceName, String version, int totalCount, int pageSize, int numPages, int offset  ) throws IOException {
        List<EthosResponse> ethosResponseList = new ArrayList();
        Map<String,String> headers = buildHeadersMap( version );
        for( int i = 0; i < numPages; i++ ) {
            if( offset >= totalCount ) {
                break;
            }
            String url = EthosIntegrationUrls.apiPaging( getRegion(), resourceName, offset, pageSize );
            EthosResponse response = get( url, headers );
            ethosResponseList.add( response );
            offset += pageSize;
        }
        return ethosResponseList;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Used within the SDK to page for some number of rows for a given resource, using an offset of 0.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param totalCount The total count of rows for the given resource.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numRows The overall number of rows to page for.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page,
     *         up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected List<EthosResponse> doPagingForNumRows( String resourceName, String version, int totalCount, int pageSize, int numRows ) throws IOException {
        return doPagingFromOffsetForNumRows( resourceName, version, totalCount, pageSize, 0, numRows );
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Used within the SDK to page for some number of rows for a given resource, using an offset of 0.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param totalCount The total count of rows for the given resource.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The overall number of rows to page for.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page,
     *         from the given offset (inclusive) and up to the number of rows specified (exclusive) or the total count of the resource (whichever is less).
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected List<EthosResponse> doPagingFromOffsetForNumRows( String resourceName, String version, int totalCount, int pageSize, int offset, int numRows ) throws IOException {
        List<EthosResponse> ethosResponseList = new ArrayList();
        Map<String,String> headers = buildHeadersMap( version );
        if( numRows > totalCount ) {
            numRows = totalCount; // Ensure the numRows requested is not more than the totalCount.
        }
        double numPages = Math.ceil( Double.valueOf(numRows) / Double.valueOf(pageSize) );
        int totalNum = numRows + offset;
        for( int i = 0; i < numPages; i++ ) {
            if( offset >= totalCount ) {
                break;
            }
            int rowsRemaining = totalNum - offset;
            if( rowsRemaining < pageSize ) {
                pageSize = rowsRemaining;
            }
            String url = EthosIntegrationUrls.apiPaging( getRegion(), resourceName, offset, pageSize );
            EthosResponse response = get( url, headers );
            ethosResponseList.add( response );
            offset += pageSize;
        }
        return ethosResponseList;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Returns the header value from the given ethosResponse.
     * @param ethosResponse The <code>EthosResponse</code> to get a header value from.
     * @param headerName A header name following Ethos standards (such as "x-media-type").
     * @return The value of the given header.  Returns null if the given ethosResponse is null or the header is not found.
     */
    protected String getHeaderValue( EthosResponse ethosResponse, String headerName ) {
        String headerValue = null;
        if( ethosResponse == null ) {
            return headerValue;
        }
        Header header = ethosResponse.getHeader( headerName );

        if( header != null ) {
            headerValue = header.getValue();
        }
        return headerValue;
    }

    /**
     * Gets the page size for the given Ethos resource.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get the page size for.
     * @return The page size of the given resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getPageSize( String resourceName ) throws IOException {
        return getPageSize( resourceName, DEFAULT_VERSION );
    }

    /**
     * Gets the page size for the given Ethos resource and version.
     * @param resourceName The name of the Ethos resource to get the page size for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return The page size of the given resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getPageSize( String resourceName, String version ) throws IOException {
        return getPageSize( resourceName, version, null );
    }

    /**
     * Gets the max page size for the given Ethos resource.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get the page size for.
     * @return The max page size for the resource as found using the x-max-page-size header.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getMaxPageSize( String resourceName ) throws IOException {
        return getMaxPageSize( resourceName, DEFAULT_VERSION );
    }

    /**
     * Gets the max page size for the given Ethos resource and version.
     * @param resourceName The name of the Ethos resource to get the page size for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return The max page size for the resource as found using the x-max-page-size header.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getMaxPageSize( String resourceName, String version ) throws IOException {
        return getMaxPageSize( resourceName, version, null );
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Gets the page size for the given resourceName, version, and ethosResponse.  If the ethosResponse is null, it
     * will make a call to get the data for the given resource to then calculate the page size from the response body content.
     * @param resourceName The name of the Ethos resource to get the page size for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param ethosResponse An <code>EthosResponse</code> from which to calculate the page size using it's content body length, or null.
     * @return The page size for the given resource, or 0 if the resourceName is null or empty.  Attempts to return the max page size
     *         for the given resource if the resource content body is null.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected int getPageSize( String resourceName, String version, EthosResponse ethosResponse ) throws IOException {
        int pageSize = 0;
        if( resourceName == null || resourceName.trim().isEmpty() ) {
            return pageSize;
        }
        if( version == null || version.trim().isEmpty() ) {
            version = DEFAULT_VERSION;
        }
        if( ethosResponse == null ) {
            ethosResponse = get( resourceName, version );
        }
        if( ethosResponse.getContent() != null && ethosResponse.getContent().trim().isEmpty() == false ) {
            JsonNode jsonNode = objectMapper.readTree( ethosResponse.getContent() );
            pageSize = jsonNode.size();
        }
        else {
            pageSize = getMaxPageSize( resourceName, version, ethosResponse );
        }
        return pageSize;
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Gets the max page size for the given resourceName, version, and ethosResponse.  If the ethosResponse is null, it
     * will make a call to get the data for the given resource to then calculate the page size from the response body content.
     * @param resourceName The name of the Ethos resource to get the page size for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param ethosResponse An <code>EthosResponse</code> from which to calculate the page size using it's content body length, or null.
     * @return The max page size for the given resource, or 0 if the resourceName is null or empty.  Returns the default max page size of 500
     *         for the given resource if the x-max-page-size header is not found.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected int getMaxPageSize( String resourceName, String version, EthosResponse ethosResponse ) throws IOException {
        int maxPageSize = 0;
        if( resourceName == null || resourceName.trim().isEmpty() ) {
            return maxPageSize;
        }
        if( version == null || version.trim().isEmpty() ) {
            version = DEFAULT_VERSION;
        }
        if( ethosResponse == null ) {
            ethosResponse = get( resourceName, version );
        }
        String maxPageSizeStr = getHeaderValue( ethosResponse, HDR_X_MAX_PAGE_SIZE );
        if( maxPageSizeStr != null && maxPageSizeStr.trim().isEmpty() == false ) {
            maxPageSize = Integer.valueOf( maxPageSizeStr );
        }
        else {
            maxPageSize = DEFAULT_MAX_PAGE_SIZE;
        }
        return maxPageSize;
    }

    /**
     * Gets the total count of rows for the given Ethos resource.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get the total count for.
     * @return The total count of rows for the resource as found using the x-total-count header.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getTotalCount( String resourceName ) throws IOException {
        return getTotalCount( resourceName, DEFAULT_VERSION );
    }

    /**
     * Gets the total count of rows for the given Ethos resource and version.
     * @param resourceName The name of the Ethos resource to get the total count for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return The total count of rows for the resource as found using the x-total-count header.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getTotalCount( String resourceName, String version ) throws IOException {
        return getTotalCount( resourceName, version, null );
    }

    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Gets the total count of rows for the given Ethos resource, version, and ethosResponse.  If the ethosResponse is null,
     * a call will be made to get the resource data to then get the total count from the x-total-count header.
     * @param resourceName The name of the Ethos resource to get the page size for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param ethosResponse An <code>EthosResponse</code> from which to get the total count, or null.
     * @return The total count for the given resource, or 0 if the resourceName is null or empty.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected int getTotalCount( String resourceName, String version, EthosResponse ethosResponse ) throws IOException {
        if( resourceName == null || resourceName.trim().isEmpty() ) {
            return 0;
        }
        if( version == null || version.trim().isEmpty() ) {
            version = DEFAULT_VERSION;
        }
        if( ethosResponse == null ) {
            ethosResponse = get( resourceName, version );
        }
        String totalCountStr = getHeaderValue( ethosResponse, HDR_X_TOTAL_COUNT );
        return Integer.valueOf( totalCountStr );
    }

    /**
     * Converts the response body content within the given EthosResponse to a list of objects of the given class type.
     * The response body list is set within the returned EthosResponse.
     * @param ethosResponse The EthosResponse to convert the response body for.
     * @param classType The class to use when converting the response body into a list of objects.
     * @return The EthosResponse containing a list of objects for the response body.
     * @throws JsonProcessingException Propagated from the EthosResponseConverter if thrown.
     */
    private EthosResponse convertResponseContentToTypedList( EthosResponse ethosResponse, Class classType ) throws JsonProcessingException {
        Object responseBody = ethosResponseConverter.toTypedList( ethosResponse, classType );
        ethosResponse.setContentAsType( responseBody );
        return ethosResponse;
    }

    /**
     * Converts the response body content of each EthosResponse within the given ethosResponseList to a list of objects of the given class type.
     * The response body list is set within each EthosResponse in the ethosResponseList.
     * @param ethosResponseList The list of EthosResponses for which to convert the EthosResponse content into a list of objects of the given class type for each EthosResponse in the list.
     * @param classType The class to use when converting the response body into a list of objects.
     * @return The EthosResponse containing a list of objects for the response body.
     * @throws JsonProcessingException Propagated from the EthosResponseConverter if thrown.
     */
    private List<EthosResponse> convertResponsesContentToTypedList( List<EthosResponse> ethosResponseList, Class classType ) throws JsonProcessingException {
        if( ethosResponseList == null || ethosResponseList.isEmpty() ) {
            return ethosResponseList;
        }
        for( EthosResponse ethosResponse : ethosResponseList ) {
            ethosResponse = convertResponseContentToTypedList( ethosResponse, classType );
        }
        return ethosResponseList;
    }

}
