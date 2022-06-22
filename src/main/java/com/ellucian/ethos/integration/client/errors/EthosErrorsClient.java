/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.errors;

import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.EthosIntegrationUrls;
import com.ellucian.ethos.integration.client.EthosClient;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.EthosResponseConverter;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.Header;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An EthosClient used to to perform Create, Read, and Delete operations for Error objects, using the Ethos
 * Integration errors service.
 * <p>
 *     The preferred way to instantiate this class is via the {@link EthosClientBuilder EthosClientBuilder}.
 * </p>
 *
 * @since 0.0.1
 */
public class EthosErrorsClient extends EthosClient {

    /** The version to use for the Ethos Errors API. */
    private static final String errorType = "application/vnd.hedtech.errors.v2+json";

    /** The default page size (limit) when paging for errors. */
    public static final int DEFAULT_ERROR_PAGE_SIZE = 10;

    /** The total count header found in the EthosResponse. */
    public static final String HDR_TOTAL_COUNT = "x-total-count";

    /**
     * The remaining count header found in the EthosResponse.  This value is equal to the total count minus the page size of the given request/response. */
    public static final String HDR_REMAINING_COUNT = "x-remaining-count";

    /**
     * Used to convert EthosResponses to EthosError objects.
     */
    private EthosResponseConverter ethosResponseConverter;

    /**
     * Constructs an EthosErrorClient using the given API key.
     * <p>
     * Note that the preferred way to get an instance of this class is through the {@link EthosClientBuilder EthosClientFactory}.
     * @param apiKey A valid API key from Ethos Integration.  This is required to be a valid 36 character GUID string.
     *               If it is null, empty, or not in a valid GUID format, then an <code>IllegalArgumentException</code> will be thrown.
     * @param connectionTimeout The timeout <b>in seconds</b> for a connection to be established.
     * @param connectionRequestTimeout The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager.
     * @param socketTimeout The timeout <b>in seconds</b> when waiting for data between consecutive data packets.
     */
    public EthosErrorsClient(String apiKey, Integer connectionTimeout, Integer connectionRequestTimeout, Integer socketTimeout ) {
        super(apiKey, connectionTimeout, connectionRequestTimeout, socketTimeout );
        this.ethosResponseConverter = new EthosResponseConverter();
    }

    /**
     * Get an initial array (page) of Errors from the tenant associated with the access token.
     * @return An EthosResponse containing an array of errors in the content body.
     * @throws IOException if there is an error making the HTTP request
     */
    public EthosResponse get() throws IOException {
        Map<String, String> headers = buildHeadersMap();
        return get(EthosIntegrationUrls.errors(getRegion()), headers);
    }

    /**
     * Gets an initial array (page) of Errors from the tenant associated with the access token, as a JsonNode.
     * @return A JsonNode containing child nodes for each error.
     * @throws IOException Propagated if it is thrown by containing methods.
     */
    public JsonNode getAsJsonNode() throws IOException {
        EthosResponse ethosResponse = get();
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }

    /**
     * Gets an initial array (page) of Errors from the tenant associated with the access token, as a string.
     * @return A string of errors in JSON format.
     * @throws IOException Propagated if it is thrown by containing methods.
     */
    public String getAsString() throws IOException {
        return ethosResponseConverter.toContentString( get() );
    }

    /**
     * Get an initial list of EthosErrors from the tenant associated with the access token.
     * @return An initial list of EthosErrors.
     * @throws IOException Thrown if there is an error making the HTTP request.
     */
    public List<EthosError> getAsEthosErrors() throws IOException {
        EthosResponse ethosResponse = get();
        return ethosResponseConverter.toEthosErrorList( ethosResponse );
    }

    /**
     * Get a single Error from your tenant, using the given ID.
     * @param id The ID of the Error to get.  This is a required parameter.
     *               If it is null or empty, then an <code>IllegalArgumentException</code> will be thrown.
     * @return An EthosResponse containing the error in the content body.
     * @throws IOException if there is an error making the HTTP request
     */
    public EthosResponse getById(String id) throws IOException {
        if( id == null || id.isBlank() ) {
            throw new IllegalArgumentException("The 'id' parameter is required.");
        }
        Map<String, String> headers = buildHeadersMap();
        return get(EthosIntegrationUrls.errors(getRegion()) + "/" + id, headers);
    }

    /**
     * Get a single error as an EthosError object, using the given ID.
     * @param id The ID of the Error to get.  This is a required parameter.
     *        If it is null or empty, then an <code>IllegalArgumentException</code> will be thrown.
     * @return An EthosError representing the error for the given ID.
     * @throws IOException Propagated from the methods called in this method.
     */
    public EthosError getByIdAsEthosError( String id ) throws IOException {
        EthosResponse ethosResponse = getById( id );
        return ethosResponseConverter.toSingleEthosError( ethosResponse );
    }

    /**
     * Get a single error as a JsonNode object, using the given ID.
     * @param id The ID of the Error to get.  This is a required parameter.
     *           If it is null or empty, then an <code>IllegalArgumentException</code> will be thrown.
     * @return An JsonNode containing the error for the given ID.
     * @throws IOException Propagated from the methods called in this method.
     */
    public JsonNode getByIdAsJsonNode( String id ) throws IOException {
        EthosResponse ethosResponse = getById( id );
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }

    /**
     * Get a single error as a string in JSON format, using the given ID.
     * @param id The ID of the Error to get.  This is a required parameter.
     *           If it is null or empty, then an <code>IllegalArgumentException</code> will be thrown.
     * @return A String for the given error in JSON format.
     * @throws IOException Propagated from the methods called in this method.
     */
    public String getByIdAsString( String id ) throws IOException {
        return ethosResponseConverter.toContentString( getById(id) );
    }

    /**
     * Gets all of the errors for the given tenant per access token.
     * @return A list of EthosResponses where each ethosResponse in the list contains a page of errors.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<EthosResponse> getAllErrors() throws IOException {
        int totalCount = getTotalErrorCount();
        return doPaging( totalCount, DEFAULT_ERROR_PAGE_SIZE, 0 );
    }


    /**
     * Gets all of the errors for the given tenant per access token, as a list of JsonNodes.
     * @return A list of JsonNodes where each JsonNode in the list contains a page of errors.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<JsonNode> getAllErrorsAsJsonNodes() throws IOException {
        return ethosResponseConverter.toPageBasedJsonNodeList( getAllErrors() );
    }

    /**
     * Gets all of the errors for the given tenant per access token, as a list of Strings in JSON format.
     * @return A list of strings where each string in the list contains a page of errors.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<String> getAllErrorsAsStrings() throws IOException {
        return ethosResponseConverter.toPageBasedStringList( getAllErrors() );
    }

    /**
     * Gets all of the errors for the given tenant per access token, as a list of EthosErrors.
     * @return A list of EthosErrors where each EthosError in the list is for an individual error.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<EthosError> getAllErrorsAsEthosErrors() throws IOException {
        List<EthosError> ethosErrorList = new ArrayList<>();
        List<EthosResponse> ethosResponseList = getAllErrors();
        for( EthosResponse ethosResponse : ethosResponseList ) {
            ethosErrorList.addAll( ethosResponseConverter.toEthosErrorList(ethosResponse) );
        }
        return ethosErrorList;
    }


    /**
     * Gets all of the errors for the given tenant per access token using the given page size.
     * @param pageSize The limit number of errors to include in each page of errors returned.
     * @return A list of EthosResponses where each ethosResponse in the list contains a page of errors and each page
     *         contains up to the number of errors specified as the page size.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<EthosResponse> getAllErrorsWithPageSize( int pageSize ) throws IOException {
        int totalCount = getTotalErrorCount();
        return doPaging( totalCount, pageSize, 0 );
    }


    /**
     * Gets all of the errors for the given tenant per access token using the given page size, as a list of JsonNodes.
     * @param pageSize The limit number of errors to include in each page of errors returned.
     * @return A list of JsonNodes where each JsonNode in the list contains a page of errors and each page
     *         contains up to the number of errors specified as the page size.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<JsonNode> getAllErrorsWithPageSizeAsJsonNodes( int pageSize ) throws IOException {
        return ethosResponseConverter.toPageBasedJsonNodeList( getAllErrorsWithPageSize(pageSize) );
    }


    /**
     * Gets all of the errors for the given tenant per access token using the given page size, as a list of strings in JSON format.
     * @param pageSize The limit number of errors to include in each page of errors returned.
     * @return A list of strings where each string in the list contains a page of errors and each page
     *         contains up to the number of errors specified as the page size.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<String> getAllErrorsWithPageSizeAsStrings( int pageSize ) throws IOException {
        return ethosResponseConverter.toPageBasedStringList( getAllErrorsWithPageSize(pageSize) );
    }


    /**
     * Gets all of the errors for the given tenant per access token from the given offset.  Uses the default page size.
     * @param offset The 0 based index from which to begin paging for errors.
     * @return A list of EthosResponses where each EthosResponse in the list contains a page of errors starting from the
     *         given offset.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<EthosResponse> getErrorsFromOffset( int offset ) throws IOException {
        int totalCount = getTotalErrorCount();
        return doPaging( totalCount, DEFAULT_ERROR_PAGE_SIZE, offset );
    }

    /**
     * Gets all of the errors for the given tenant per access token from the given offset, as a list of JsonNodes.
     * Uses the default page size.
     * @param offset The 0 based index from which to begin paging for errors.
     * @return A list of JsonNodes where each JsonNode in the list contains a page of errors starting from the
     *         given offset.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<JsonNode> getErrorsFromOffsetAsJsonNodes( int offset ) throws IOException {
        return ethosResponseConverter.toPageBasedJsonNodeList( getErrorsFromOffset(offset) );
    }


    /**
     * Gets all of the errors for the given tenant per access token from the given offset, as a list of strings in JSON format.
     * Uses the default page size.
     * @param offset The 0 based index from which to begin paging for errors.
     * @return A list of strings where each string in the list contains a page of errors starting from the
     *         given offset.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<String> getErrorsFromOffsetAsStrings( int offset ) throws IOException {
        return ethosResponseConverter.toPageBasedStringList( getErrorsFromOffset(offset) );
    }

    /**
     * Gets all of the errors for the given tenant per access token from the given offset, as a list of EthosErrors.
     * @param offset The 0 based index from which to begin paging for errors.
     * @return A list of EthosErrors starting from the given offset.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<EthosError> getErrorsFromOffsetAsEthosErrors( int offset ) throws IOException {
        List<EthosError> ethosErrorList = new ArrayList<>();
        List<EthosResponse> ethosResponseList = getErrorsFromOffset( offset );
        for( EthosResponse ethosResponse : ethosResponseList ) {
            ethosErrorList.addAll( ethosResponseConverter.toEthosErrorList(ethosResponse) );
        }
        return ethosErrorList;
    }


    /**
     * Gets all of the errors for the given tenant per access token from the given offset, using the given pageSize.
     * @param offset The 0 based index from which to begin paging for errors.
     * @param pageSize The limit number of errors to include in each page of errors returned.
     * @return A list of EthosResponses where each EthosResponse in the list contains a page of errors up to the given
     *         pageSize, starting from the given offset.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<EthosResponse> getErrorsFromOffsetWithPageSize( int offset, int pageSize ) throws IOException {
        int totalCount = getTotalErrorCount();
        return doPaging( totalCount, pageSize, offset );
    }


    /**
     * Gets all of the errors for the given tenant per access token from the given offset, using the given pageSize,
     * as a list of JsonNodes.
     * @param offset The 0 based index from which to begin paging for errors.
     * @param pageSize The limit number of errors to include in each page of errors returned.
     * @return A list of JsonNodes where each JsonNode in the list contains a page of errors up to the given
     *         pageSize, starting from the given offset.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<JsonNode> getErrorsFromOffsetWithPageSizeAsJsonNodes( int offset, int pageSize ) throws IOException {
        return ethosResponseConverter.toPageBasedJsonNodeList( getErrorsFromOffsetWithPageSize(offset, pageSize) );
    }


    /**
     * Gets all of the errors for the given tenant per access token from the given offset, using the given pageSize,
     * as a list of strings in JSON format.
     * @param offset The 0 based index from which to begin paging for errors.
     * @param pageSize The limit number of errors to include in each page of errors returned.
     * @return A list of strings where each string in the list contains a page of errors up to the given
     *         pageSize, starting from the given offset.
     * @throws IOException Propagated if thrown interacting with the Ethos Errors API.
     */
    public List<String> getErrorsFromOffsetWithPageSizeAsStrings( int offset, int pageSize ) throws IOException {
        return ethosResponseConverter.toPageBasedStringList( getErrorsFromOffsetWithPageSize(offset, pageSize) );
    }


    /**
     * <p><b>Intended to be used internally within the SDK.</b></p>
     * Handles paging for errors.  If the given pageSize is &lt;= 0, the default page size is used.  If the offset is &lt; 0,
     * 0 will be used for the offset.
     * @param totalCount The total count of errors for the given tenant per access token.
     * @param pageSize The number of errors to include in each page (EthosResponse) of the list returned.
     * @param offset The 0 based index from which to begin paging for errors.  To get all errors, the offset should be 0.
     * @return A list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page,
     *         beginning from the given offset index.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected List<EthosResponse> doPaging(int totalCount, int pageSize, int offset ) throws IOException {
        if( pageSize <= 0 ) {
            pageSize = DEFAULT_ERROR_PAGE_SIZE;
        }
        if( offset < 0 ) {
            offset = 0;
        }
        List<EthosResponse> ethosResponseList = new ArrayList();
        Map<String,String> headers = buildHeadersMap();
        double numPages = Math.ceil( (Double.valueOf(totalCount) - Double.valueOf(offset)) / Double.valueOf(pageSize) );
        for( int i = 0; i < numPages; i++ ) {
            String url = EthosIntegrationUrls.errorsPaging( getRegion(), offset, pageSize );
            EthosResponse response = get( url, headers );
            ethosResponseList.add( response );
            offset += pageSize;
        }
        return ethosResponseList;
    }


    /**
     * Calculates the number of pages given the input params.  Input param values are not validated, so specifying
     * invalid negative values could produce unpredictable results.  A 0 value is valid for the offset, but not for the other params.
     * <p>
     *     The calculation is as follows:
     *     <br>
     *     <i>double numPages = Math.ceil( (Double.valueOf(totalErrorCount) - Double.valueOf(offset)) / Double.valueOf(pageSize) );</i>
     *     <br>
     *     The numPages returned value is cast to an int.
     * </p>
     * @param totalErrorCount The total number of errors.
     * @param pageSize The limit number of errors to include in each page of errors returned.
     * @param offset The 0 based index from which to begin paging for errors.
     * @return The number of pages calculated from the given input params.
     */
    public int calculateNumberOfPages( int totalErrorCount, int pageSize, int offset ) {
        double numPages = Math.ceil( (Double.valueOf(totalErrorCount) - Double.valueOf(offset)) / Double.valueOf(pageSize) );
        return (int) numPages;
    }


    /**
     * Create the given Error in your tenant with a POST request to the Ethos Integration errors service.
     * @param error An Error object to create.  This is a required parameter.
     *              If it is null, then an <code>IllegalArgumentException</code> will be thrown.
     * @return An EthosResponse containing the response payload from the errors service in the content body.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse post(EthosError error) throws IOException {
        if( error == null ) {
            throw new IllegalArgumentException("The 'error' parameter is required.");
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", errorType);
        return post(EthosIntegrationUrls.errors(getRegion()), headers, error.toString());
    }

    /**
     * Delete the Error with the given ID from your tenant.
     * @param id The ID of the Error to delete.  This is a required parameter.
     *           If it is null or empty, then an <code>IllegalArgumentException</code> will be thrown.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public void delete(String id) throws IOException {
        if( id == null || id.isBlank() ) {
            throw new IllegalArgumentException("The 'id' parameter is required.");
        }
        delete(EthosIntegrationUrls.errors(getRegion()) + "/" + id, null);
    }

    /**
     * Gets the total number of errors according to the 'x-total-count' response header for the given tenant per access token.
     * @return The total number of errors for the given tenant, or 0 if the 'x-total-count' header is not found.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getTotalErrorCount() throws IOException {
        int totalErrorCount = 0;
        EthosResponse ethosResponse = get();
        Header totalCountHeader = ethosResponse.getHeader( "x-total-count" );
        if( totalCountHeader != null ) {
            totalErrorCount = Integer.valueOf( totalCountHeader.getValue() );
        }
        return totalErrorCount;
    }

    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * Builds the headers map used when making requests for errors.
     * @return A map containing the headers needed for making a request to the Ethos Integration Errors API service.
     */
    private Map<String,String> buildHeadersMap() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", errorType);
        return headers;
    }

}
