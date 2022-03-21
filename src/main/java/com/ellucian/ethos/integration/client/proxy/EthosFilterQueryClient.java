/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy;

import com.ellucian.ethos.integration.EthosIntegrationUrls;
import com.ellucian.ethos.integration.client.EthosClient;
import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.proxy.filter.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An EthosProxyClient that provides the ability to submit GET requests supporting filters and/or named queries with
 * support for paging.
 * @since 0.0.1
 * @author David Kumar
 */
public class EthosFilterQueryClient extends EthosProxyClient {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** Prefix value used when specifying criteria filter syntax. */
    public static final String CRITERIA_FILTER_PREFIX = "?criteria=";

    /**
     * Instantiates this class using the given API key and timeout values.
     * <p>
     * Note that the preferred way to get an instance of this class is through the {@link EthosClientBuilder EthosClientBuilder}.
     * @param apiKey A valid API key from Ethos Integration.  This is required to be a valid 36 character GUID string.
     *               If it is null, empty, or not in a valid GUID format, then an <code>IllegalArgumentException</code> will be thrown.
     * @param connectionTimeout The timeout <b>in seconds</b> for a connection to be established.
     * @param connectionRequestTimeout The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager.
     * @param socketTimeout The timeout <b>in seconds</b> when waiting for data between consecutive data packets.
     */
    public EthosFilterQueryClient( String apiKey, Integer connectionTimeout, Integer connectionRequestTimeout, Integer socketTimeout ) {
        super( apiKey, connectionTimeout, connectionRequestTimeout, socketTimeout );
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    /**
     * Gets a page of data for the given resource with the given filter.  Uses the default version of the resource.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaFilterStr The string resource filter in JSON format contained in the URL, e.g: <pre>?criteria={"names":[{"firstName":"John"}]}</pre>
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException Thrown if the given resoureName or criteriaFilterStr is null or blank.
     */
    public EthosResponse getWithCriteriaFilter( String resourceName, String criteriaFilterStr ) throws IOException {
        return getWithCriteriaFilter( resourceName, DEFAULT_VERSION, criteriaFilterStr );
    }

    /**
     * Gets a page of data for the given resource by name and version with the given filter.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaFilterStr The string resource filter in JSON format contained in the URL, e.g: <pre>?criteria={"names":[{"firstName":"John"}]}</pre>
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException If the given resourceName is null or blank, or if the given criteriaFilterStr is null or blank.
     */
    public EthosResponse getWithCriteriaFilter( String resourceName, String version, String criteriaFilterStr ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot get resource with criteria filter due to a null or blank resource name." );
        }
        if( criteriaFilterStr == null || criteriaFilterStr.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" with criteria filter due to a null or blank criteria filter string.", resourceName) );
        }
        criteriaFilterStr = encodeCriteriaFilterStr( criteriaFilterStr );
        Map<String,String> headers = buildHeadersMap( version );
        EthosResponse response = get( EthosIntegrationUrls.apiFilter(getRegion(), resourceName, criteriaFilterStr), headers );
        return response;
    }

    /**
     * Gets a page of data for the given resource with the given filter.  Uses the default version of the resource.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaFilterStr The string resource filter in JSON format contained in the URL, e.g: <pre>?criteria={"names":[{"firstName":"John"}]}</pre>
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException Thrown if the given resoureName or criteriaFilterStr is null or blank.
     */
    public EthosResponse getWithCriteriaFilter( String resourceName, String criteriaFilterStr, Class classType ) throws IOException {
        return getWithCriteriaFilter( resourceName, DEFAULT_VERSION, criteriaFilterStr, classType );
    }

    /**
     * Gets a page of data for the given resource by name and version with the given filter.
     * The response body is returned within the EthosResponse as a list of objects of the given class type, if the classType
     * is not null.  If the classType is null, the returned EthosResponse will not contain a generic type object response body,
     * but only a JSON formatted string response body.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaFilterStr The string resource filter in JSON format contained in the URL, e.g: <pre>?criteria={"names":[{"firstName":"John"}]}</pre>
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException If the given resourceName is null or blank, or if the given criteriaFilterStr is null or blank.
     */
    public EthosResponse getWithCriteriaFilter( String resourceName, String version, String criteriaFilterStr, Class classType ) throws IOException {
        EthosResponse ethosResponse = getWithCriteriaFilter( resourceName, version, criteriaFilterStr );
        if( classType != null ) {
            ethosResponse = convertResponseContentToTypedList( ethosResponse, classType );
        }
        return ethosResponse;
    }

    /**
     * Gets a page of data for the given resource with the given named query filter.  Uses the default version of the resource.
     * @param resourceName The name of the resource to get data for.
     * @param namedQueryFilterStr The string resource filter in JSON format contained in the URL, e.g: <pre>?criteria={"names":[{"firstName":"John"}]}</pre>
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException Thrown if the resourceName or namedQueryFilterStr is null or blank.
     */
    public EthosResponse getWithNamedQueryFilter( String resourceName, String namedQueryFilterStr ) throws IOException {
        return getWithNamedQueryFilter( resourceName, DEFAULT_VERSION, namedQueryFilterStr );
    }

    /**
     * Gets a page of data for the given resource by name and version with the given named query filter.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param namedQueryFilterStr The string resource filter in JSON format contained in the URL, e.g: <pre>?instructor={"instructor": {"id": "11111111-1111-1111-1111-111111111111"}}"</pre>
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException If the given resourceName is null or blank, or if the given namedQueryFilterStr is null or blank.
     */
    public EthosResponse getWithNamedQueryFilter( String resourceName, String version, String namedQueryFilterStr ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException( "Error: Cannot get resource with named query filter due to a null or blank resource name." );
        }
        if( namedQueryFilterStr == null || namedQueryFilterStr.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" with named query filter due to a null or blank named query filter string.", resourceName) );
        }
        namedQueryFilterStr = encodeNamedQueryStr( namedQueryFilterStr );
        Map<String,String> headers = buildHeadersMap( version );
        EthosResponse response = get( EthosIntegrationUrls.apiFilter(getRegion(), resourceName, namedQueryFilterStr), headers );
        return response;
    }

    /**
     * Gets a page of data for the given resource by name and version with the given named query filter.
     * The response body is returned within the EthosResponse as a list of objects of the given class type, if the classType
     * is not null.  If the classType is null, the returned EthosResponse will not contain a generic type object response body,
     * but only a JSON formatted string response body.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param namedQueryFilterStr The string resource filter in JSON format contained in the URL, e.g: <pre>?instructor={"instructor": {"id": "11111111-1111-1111-1111-111111111111"}}"</pre>
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException If the given resourceName is null or blank, or if the given namedQueryFilterStr is null or blank.
     */
    public EthosResponse getWithNamedQueryFilter( String resourceName, String version, String namedQueryFilterStr, Class classType ) throws IOException {
        EthosResponse ethosResponse = getWithNamedQueryFilter( resourceName, version, namedQueryFilterStr );
        if( classType != null ) {
            ethosResponse = convertResponseContentToTypedList( ethosResponse, classType );
        }
        return ethosResponse;
    }

    /**
     * Gets a page of data for the given resource with the given named query filter.  Uses the default version of the resource.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param namedQueryFilterStr The string resource filter in JSON format contained in the URL, e.g: <pre>?criteria={"names":[{"firstName":"John"}]}</pre>
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException Thrown if the resourceName or namedQueryFilterStr is null or blank.
     */
    public EthosResponse getWithNamedQueryFilter( String resourceName, String namedQueryFilterStr, Class classType ) throws IOException {
        return getWithNamedQueryFilter( resourceName, DEFAULT_VERSION, namedQueryFilterStr, classType );
    }

    /**
     * Gets a page of data for the given resource by name with the given filter.  Uses the default version of the resource.
     * Makes a non-filter API request if the given criteriaFilter is null.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getWithCriteriaFilter(String resourceName, CriteriaFilter criteriaFilter ) throws IOException {
        return getWithCriteriaFilter( resourceName, DEFAULT_VERSION, criteriaFilter );
    }

    /**
     * Gets a page of data for the given resource by name and version with the given filter.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException If the given criteriaFilter is null.
     */
    public EthosResponse getWithCriteriaFilter(String resourceName, String version, CriteriaFilter criteriaFilter ) throws IOException {
        if( criteriaFilter == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" with criteria filter due to a null criteria filter reference.", resourceName) );
        }
        return getWithCriteriaFilter( resourceName, version, criteriaFilter.toString() );
    }

    /**
     * Gets a page of data for the given resource by name and version with the given filter.
     * The response body is returned within the EthosResponse as a list of objects of the given class type, if the classType
     * is not null.  If the classType is null, the returned EthosResponse will not contain a generic type object response body,
     * but only a JSON formatted string response body.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException If the given criteriaFilter is null.
     */
    public EthosResponse getWithCriteriaFilter(String resourceName, String version, CriteriaFilter criteriaFilter, Class classType ) throws IOException {
        EthosResponse ethosResponse = getWithCriteriaFilter( resourceName, version, criteriaFilter );
        if( classType != null ) {
            ethosResponse = convertResponseContentToTypedList( ethosResponse, classType );
        }
        return ethosResponse;
    }

    /**
     * Gets a page of data for the given resource by name with the given filter.  Uses the default version of the resource.
     * Makes a non-filter API request if the given criteriaFilter is null.
     * The response body is returned within the EthosResponse as a list of objects of the given class type
     * @param resourceName The name of the resource to get data for.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getWithCriteriaFilter(String resourceName, CriteriaFilter criteriaFilter, Class classType ) throws IOException {
        return getWithCriteriaFilter( resourceName, DEFAULT_VERSION, criteriaFilter, classType );
    }

    /**
     * Gets a page of data for the given resource by name with the given named query filter.  Uses the default version of the resource.
     * Makes a non-filter API request if the given namedQueryFilter is null.
     * @param resourceName The name of the resource to get data for.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                       A simple call to namedQueryFilter.toString() should output the named query filter portion of the request URL,
     *                       e.g: <code>?instructor={"instructor": {"id": "11111111-1111-1111-1111-111111111111"}}"</code>.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getWithNamedQueryFilter(String resourceName, NamedQueryFilter namedQueryFilter ) throws IOException {
        return getWithNamedQueryFilter( resourceName, DEFAULT_VERSION, namedQueryFilter );
    }

    /**
     * Gets a page of data for the given resource by name and version with the given named query filter.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                         A simple call to namedQueryFilter.toString() should output the named query filter portion of the request URL,
     *                         e.g: <code>?instructor={"instructor": {"id": "11111111-1111-1111-1111-111111111111"}}"</code>.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException If the given namedQueryFilter is null.
     */
    public EthosResponse getWithNamedQueryFilter(String resourceName, String version, NamedQueryFilter namedQueryFilter ) throws IOException {
        if( namedQueryFilter == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" with named query filter due to a null named query filter reference.", resourceName) );
        }
        return getWithNamedQueryFilter( resourceName, version, namedQueryFilter.toString() );
    }

    /**
     * Gets a page of data for the given resource by name and version with the given named query filter.
     * The response body is returned within the EthosResponse as a list of objects of the given class type, if the classType
     * is not null.  If the classType is null, the returned EthosResponse will not contain a generic type object response body,
     * but only a JSON formatted string response body.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                         A simple call to namedQueryFilter.toString() should output the named query filter portion of the request URL,
     *                         e.g: <code>?instructor={"instructor": {"id": "11111111-1111-1111-1111-111111111111"}}"</code>.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @throws IllegalArgumentException If the given namedQueryFilter is null.
     */
    public EthosResponse getWithNamedQueryFilter(String resourceName, String version, NamedQueryFilter namedQueryFilter, Class classType ) throws IOException {
        EthosResponse ethosResponse = getWithNamedQueryFilter( resourceName, version, namedQueryFilter );
        if( classType != null ) {
            ethosResponse = convertResponseContentToTypedList( ethosResponse, classType );
        }
        return ethosResponse;
    }

    /**
     * Gets a page of data for the given resource by name with the given named query filter.  Uses the default version of the resource.
     * Makes a non-filter API request if the given namedQueryFilter is null.
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * @param resourceName The name of the resource to get data for.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                       A simple call to namedQueryFilter.toString() should output the named query filter portion of the request URL,
     *                       e.g: <code>?instructor={"instructor": {"id": "11111111-1111-1111-1111-111111111111"}}"</code>.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @return An <code>EthosResponse</code> containing an initial page (EthosResponse content) of resource data according
     * to the requested version and filter of the resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public EthosResponse getWithNamedQueryFilter(String resourceName, NamedQueryFilter namedQueryFilter, Class classType ) throws IOException {
        return getWithNamedQueryFilter( resourceName, DEFAULT_VERSION, namedQueryFilter, classType );
    }

    /**
     * Convenience method to submit a GET request with a single set of criteria filter for a SimpleCriteria.  This is intended only to be used
     * for a single set of criteria filter, e.g: <code>?criteria={"lastName":"Smith"}</code>, where <b>firstName</b> is the criteriaKey,
     * and <b>John</b> is the criteriaValue.  Requests requiring a more complex criteria filter should first build the CriteriaFilter
     * with the necessary criteria, and then call <code>getWithCriteriaFilter(resourceName, version, criteriaFilter)</code>.
     * <p>
     * The parameters criteriaKey and criteriaValue should only specify the values within quotes of the
     * JSON filter syntax.  No JSON syntax (square or squirly brackets, etc) should be contained within those parameter values.
     * <p>
     * Uses the default version of the resource.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaKey The JSON label key for the criteria.
     * @param criteriaValue The value associated with the criteriaKey.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An EthosResponse containing a page of data for the given resource criteria filter GET request.
     */
    public EthosResponse getWithSimpleCriteriaValues(String resourceName, String criteriaKey, String criteriaValue ) throws IOException {
        return getWithSimpleCriteriaValues( resourceName, DEFAULT_VERSION, criteriaKey, criteriaValue );
    }

    /**
     * Convenience method to submit a GET request with a single set of criteria filter for a SimpleCriteria.  This is intended only to be used
     * for a single set of criteria filter, e.g: <code>?criteria={"lastName":"Smith"}</code>, where <b>firstName</b> is the criteriaKey,
     * and <b>John</b> is the criteriaValue.  Requests requiring a more complex criteria filter should first build the CriteriaFilter
     * with the necessary criteria, and then call <code>getWithCriteriaFilter(resourceName, version, criteriaFilter)</code>.
     * <p>
     * The parameters criteriaKey and criteriaValue should only specify the values within quotes of the
     * JSON filter syntax.  No JSON syntax (square or squirly brackets, etc) should be contained within those parameter values.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaKey The JSON label key for the criteria.
     * @param criteriaValue The value associated with the criteriaKey.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An EthosResponse containing a page of data for the given resource criteria filter GET request.
     */
    public EthosResponse getWithSimpleCriteriaValues(String resourceName, String version, String criteriaKey, String criteriaValue ) throws IOException {
        if( criteriaKey == null || criteriaKey.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" using SimpleCriteria values due to a null or empty criteria key parameter.", resourceName) );
        }
        if( criteriaValue == null || criteriaValue.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" using SimpleCriteria values due to a null or empty criteria value parameter.", resourceName) );
        }
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria(criteriaKey, criteriaValue)
                                        .buildCriteriaFilter();
        return getWithCriteriaFilter( resourceName, version, criteriaFilter );
    }

    /**
     * Convenience method to submit a GET request with a single set of criteria filter for a SimpleCriteria.  This is intended only to be used
     * for a single set of criteria filter, e.g: <code>?criteria={"lastName":"Smith"}</code>, where <b>firstName</b> is the criteriaKey,
     * and <b>John</b> is the criteriaValue.  Requests requiring a more complex criteria filter should first build the CriteriaFilter
     * with the necessary criteria, and then call <code>getWithCriteriaFilter(resourceName, version, criteriaFilter)</code>.
     * <p>
     * The parameters criteriaKey and criteriaValue should only specify the values within quotes of the
     * JSON filter syntax.  No JSON syntax (square or squirly brackets, etc) should be contained within those parameter values.
     * <p>
     * The response body is returned within the EthosResponse as a list of objects of the given class type, if the classType
     * is not null.  If the classType is null, the returned EthosResponse will not contain a generic type object response body,
     * but only a JSON formatted string response body.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaKey The JSON label key for the criteria.
     * @param criteriaValue The value associated with the criteriaKey.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An EthosResponse containing a page of data for the given resource criteria filter GET request.
     */
    public EthosResponse getWithSimpleCriteriaValues(String resourceName, String version, String criteriaKey, String criteriaValue, Class classType ) throws IOException {
        EthosResponse ethosResponse = getWithSimpleCriteriaValues( resourceName, version, criteriaKey, criteriaValue );
        if( classType != null ) {
            ethosResponse = convertResponseContentToTypedList( ethosResponse, classType );
        }
        return ethosResponse;
    }

    /**
     * Convenience method to submit a GET request with a single set of criteria filter for a SimpleCriteria.  This is intended only to be used
     * for a single set of criteria filter, e.g: <code>?criteria={"lastName":"Smith"}</code>, where <b>firstName</b> is the criteriaKey,
     * and <b>John</b> is the criteriaValue.  Requests requiring a more complex criteria filter should first build the CriteriaFilter
     * with the necessary criteria, and then call <code>getWithCriteriaFilter(resourceName, version, criteriaFilter)</code>.
     * <p>
     * The parameters criteriaKey and criteriaValue should only specify the values within quotes of the
     * JSON filter syntax.  No JSON syntax (square or squirly brackets, etc) should be contained within those parameter values.
     * <p>
     * The response body is returned within the EthosResponse as a list of objects of the given class type.
     * <p>
     * Uses the default version of the resource.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaKey The JSON label key for the criteria.
     * @param criteriaValue The value associated with the criteriaKey.
     * @param classType The class of the generic type object containing the response body to return within the EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An EthosResponse containing a page of data for the given resource criteria filter GET request.
     */
    public EthosResponse getWithSimpleCriteriaValues(String resourceName, String criteriaKey, String criteriaValue, Class classType ) throws IOException {
        return getWithSimpleCriteriaValues( resourceName, DEFAULT_VERSION, criteriaKey, criteriaValue, classType );
    }

    /**
     * Convenience method to submit a GET request with a single set of criteria filter for a SimpleCriteriaObject.  This is intended only to be used
     * for a single set of criteria filter, e.g: <code>?criteria={"names":{"lastName":"Smith"}}</code>, where <b>names</b> is the
     * criteriaLabel, <b>firstName</b> is the criteriaKey, and <b>John</b> is the criteriaValue.  Requests requiring
     * a more complex criteria filter should first build the CriteriaFilter with the necessary criteria, and then call
     * <code>getWithCriteriaFilter(resourceName, criteriaFilter)</code>.
     * <p>
     * The parameters criteriaLabel, criteriaKey, and criteriaValue should only specify the values within quotes of the
     * JSON filter syntax.  No JSON syntax (square or squirly brackets, etc) should be contained within those parameter values.
     * <p>
     * Uses the default version of the resource.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaLabel The label of the criteria set that the given criteriaKey and criteriaValue are associated with,
     *                      e.g: "<b>names</b>":{"firstName":"John"}, where <b>names</b> is the criteriaLabel associated to the
     *                      criteriaKey (firstName) and criteriaValue (John).
     * @param criteriaKey The JSON label key for the criteria.
     * @param criteriaValue The value associated with the criteriaKey.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An EthosResponse containing a page of data for the given resource criteria filter GET request.
     */
    public EthosResponse getWithSimpleCriteriaObjectValues(String resourceName, String criteriaLabel, String criteriaKey, String criteriaValue ) throws IOException {
        return getWithSimpleCriteriaObjectValues( resourceName, DEFAULT_VERSION, criteriaLabel, criteriaKey, criteriaValue );
    }

    /**
     * Convenience method to submit a GET request with a single set of criteria filter for a SimpleCriteriaObject.  This is intended only to be used
     * for a single set of criteria filter, e.g: <code>?criteria={"names":{"lastName":"Smith"}}</code>, where <b>names</b> is the
     * criteriaLabel, <b>firstName</b> is the criteriaKey, and <b>John</b> is the criteriaValue.  Requests requiring
     * a more complex criteria filter should first build the CriteriaFilter with the necessary criteria, and then call
     * <code>getWithCriteriaFilter(resourceName, version, criteriaFilter)</code>.
     * <p>
     * The parameters criteriaLabel, criteriaKey, and criteriaValue should only specify the values within quotes of the
     * JSON filter syntax.  No JSON syntax (square or squirly brackets, etc) should be contained within those parameter values.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaLabel The label of the criteria that the given criteriaKey and criteriaValue are associated with,
     *                      e.g: "<b>names</b>":{"firstName":"John"}, where <b>names</b> is the criteriaLabel associated with the
     *                      criteriaKey (firstName) and criteriaValue (John).
     * @param criteriaKey The JSON label key for the criteria.
     * @param criteriaValue The value associated with the criteriaKey.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An EthosResponse containing a page of data for the given resource criteria filter GET request.
     */
    public EthosResponse getWithSimpleCriteriaObjectValues(String resourceName, String version, String criteriaLabel, String criteriaKey, String criteriaValue ) throws IOException {
        if( criteriaLabel == null || criteriaLabel.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" using SimpleCriteriaObject values due to a null or empty criteria label parameter.", resourceName) );
        }
        if( criteriaKey == null || criteriaKey.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" using SimpleCriteriaObject values due to a null or empty criteria key parameter.", resourceName) );
        }
        if( criteriaValue == null || criteriaValue.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" using SimpleCriteriaObject values due to a null or empty criteria value parameter.", resourceName) );
        }
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteriaObject(criteriaLabel, criteriaKey, criteriaValue)
                                        .buildCriteriaFilter();
        return getWithCriteriaFilter( resourceName, version, criteriaFilter );
    }

    /**
     * Convenience method to submit a GET request with a single set of criteria filter for a SimpleCriteriaArray.  This is intended only to be used
     * for a single set of criteria filter, e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>, where <b>names</b> is the
     * criteriaLabel, <b>firstName</b> is the criteriaKey, and <b>John</b> is the criteriaValue.  Requests requiring
     * a more complex criteria filter should first build the CriteriaFilter with the necessary criteria, and then call
     * <code>getWithCriteriaFilter(resourceName, criteriaFilter)</code>.
     * <p>
     * The parameters criteriaLabel, criteriaKey, and criteriaValue should only specify the values within quotes of the
     * JSON filter syntax.  No JSON syntax (square or squirly brackets, etc) should be contained within those parameter values.
     * <p>
     * Uses the default version of the resource.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaLabel The label of the criteria set that the given criteriaKey and criteriaValue are associated with,
     *                      e.g: {<b>names</b>":[{"firstName":"John"}]}, where <b>names</b> is the criteriaLabel associated to the
     *                      criteriaKey (firstName) and criteriaValue (John).
     * @param criteriaKey The JSON label key for the criteria.
     * @param criteriaValue The value associated with the criteriaKey.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An EthosResponse containing a page of data for the given resource criteria filter GET request.
     */
    public EthosResponse getWithSimpleCriteriaArrayValues(String resourceName, String criteriaLabel, String criteriaKey, String criteriaValue ) throws IOException {
        return getWithSimpleCriteriaArrayValues( resourceName, DEFAULT_VERSION, criteriaLabel, criteriaKey, criteriaValue );
    }

    /**
     * Convenience method to submit a GET request with a single set of criteria filter for a SimpleCriteriaArray.  This is intended only to be used
     * for a single set of criteria filter, e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>, where <b>names</b> is the
     * criteriaLabel, <b>firstName</b> is the criteriaKey, and <b>John</b> is the criteriaValue.  Requests requiring
     * a more complex criteria filter should first build the CriteriaFilter with the necessary criteria, and then call
     * <code>getWithCriteriaFilter(resourceName, version, criteriaFilter)</code>.
     * <p>
     * The parameters criteriaLabel, criteriaKey, and criteriaValue should only specify the values within quotes of the
     * JSON filter syntax.  No JSON syntax (square or squirly brackets, etc) should be contained within those parameter values.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaLabel The label of the criteria that the given criteriaKey and criteriaValue are associated with,
     *                      e.g: "{<b>names</b>":[{"firstName":"John"}]}, where <b>names</b> is the criteriaLabel associated with the
     *                      criteriaKey (firstName) and criteriaValue (John).
     * @param criteriaKey The JSON label key for the criteria.
     * @param criteriaValue The value associated with the criteriaKey.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An EthosResponse containing a page of data for the given resource criteria filter GET request.
     */
    public EthosResponse getWithSimpleCriteriaArrayValues(String resourceName, String version, String criteriaLabel, String criteriaKey, String criteriaValue ) throws IOException {
        if( criteriaLabel == null || criteriaLabel.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" using SimpleCriteriaArray values due to a null or empty criteria label parameter.", resourceName) );
        }
        if( criteriaKey == null || criteriaKey.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" using SimpleCriteriaArray values due to a null or empty criteria key parameter.", resourceName) );
        }
        if( criteriaValue == null || criteriaValue.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get resource \"%s\" using SimpleCriteriaArray values due to a null or empty criteria value parameter.", resourceName) );
        }
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteriaArray(criteriaLabel, criteriaKey, criteriaValue)
                                        .buildCriteriaFilter();
        return getWithCriteriaFilter( resourceName, version, criteriaFilter );
    }

    /**
     * Submits a GET request for the given resource and version using the given filterMapStr.  The filterMapStr
     * is intended to support the filter syntax for resources versions 7 and older.  An example of a filterMapStr is:
     * <code>?firstName=James</code>.
     * <p>
     * This is NOT intended to be used for resource versions after version 7 and/or for criteria filters.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request,
     *                supporting only version 7 or older.
     * @param filterMapStr A string containing the filter syntax used for request URL filters with resource versions 7 or older.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An EthosResponse containing a page of data for the given resource filter map GET request.
     */
    public EthosResponse getWithFilterMap( String resourceName, String version, String filterMapStr ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot get resource with filter map due to a null or blank resource name." );
        }
        if( filterMapStr == null || filterMapStr.isBlank() ) {
            throw new IllegalArgumentException(String.format("Error: Cannot get resource \"%s\" with filter map due to a null or blank filter map string.", resourceName) );
        }
        Map<String,String> headers = buildHeadersMap( version );
        EthosResponse response = get( EthosIntegrationUrls.apiFilter(getRegion(), resourceName, filterMapStr), headers );
        return response;
    }


    /**
     * Submits a GET request for the given resource and version using the given filterMap.  The filterMap
     * is intended to support the filter syntax for resources versions 7 and older.  A FilterMap contains a map of
     * one or many filter parameter pair(s).  An example of a filterMap string indicating the contents of the map is:
     * <code>?firstName=James</code>.
     * <p>
     * This is NOT intended to be used for resource versions after version 7 and/or for criteria filters.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request,
     *                supporting only version 7 or older.
     * @param filterMap A string containing the filter syntax used for request URL filters with resource versions 7 or older.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An EthosResponse containing a page of data for the given resource filter map GET request.
     */
    public EthosResponse getWithFilterMap( String resourceName, String version, FilterMap filterMap ) throws IOException {
        if( filterMap == null ) {
            throw new IllegalArgumentException(String.format("Error: Cannot get resource \"%s\" with filter map due to a null filter map.", resourceName) );
        }
        return getWithFilterMap( resourceName, version, filterMap.toString() );
    }


    /**
     * Gets all the pages for a given resource using the specified criteria filter.  Uses the default version of the resource,
     * and the page size is derived from the length of the returned response of the request using the criteria filter.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesWithCriteriaFilter( String resourceName, CriteriaFilter criteriaFilter ) throws IOException {
        return getPagesWithCriteriaFilter( resourceName, DEFAULT_VERSION, criteriaFilter, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets all the pages for a given resource using the specified criteria filter for the given version.  Uses the default
     * page size, which is the length of the returned response of the request using the criteria filter.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesWithCriteriaFilter( String resourceName, String version, CriteriaFilter criteriaFilter ) throws IOException {
        return getPagesWithCriteriaFilter( resourceName, version, criteriaFilter, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets all the pages for a given resource using the specified criteria filter and page size.  The default version
     * of the resource is used.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @param pageSize The size (number of rows) of each page returned in the list.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesWithCriteriaFilter( String resourceName, CriteriaFilter criteriaFilter, int pageSize ) throws IOException {
        return getPagesWithCriteriaFilter( resourceName, DEFAULT_VERSION, criteriaFilter, pageSize );
    }

    /**
     * Gets all the pages for a given resource using the specified criteria filter and page size for the given version.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @param pageSize The size (number of rows) of each page returned in the list.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesWithCriteriaFilter( String resourceName, String version, CriteriaFilter criteriaFilter, int pageSize ) throws IOException {
        return getPagesFromOffsetWithCriteriaFilter( resourceName, version, criteriaFilter, pageSize, 0 );
    }

    /**
     * Gets all the pages for a given resource beginning at the given offset index, using the specified criteria filter.
     * The page size is determined to be the length of the returned response of the request using the criteria filter.
     * The default version of the resource is used.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffsetWithCriteriaFilter( String resourceName, CriteriaFilter criteriaFilter, int offset ) throws IOException {
        return getPagesFromOffsetWithCriteriaFilter( resourceName, DEFAULT_VERSION, criteriaFilter, offset );
    }


    /**
     * Gets all the pages for a given resource beginning at the given offset index, using the specified criteria filter
     * for the given version.  The page size is determined to be the length of the returned response of the request using
     * the criteria filter.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffsetWithCriteriaFilter( String resourceName, String version, CriteriaFilter criteriaFilter, int offset ) throws IOException {
        return getPagesFromOffsetWithCriteriaFilter( resourceName, version, criteriaFilter, DEFAULT_PAGE_SIZE, offset );
    }


    /**
     * Gets all the pages for a given resource beginning at the given offset index, using the specified criteria filter
     * and page size for the given version.  The default version of the resource is used.
     * @param resourceName The name of the resource to get data for.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @param pageSize The size (number of rows) of each page returned in the list.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffsetWithCriteriaFilter( String resourceName, CriteriaFilter criteriaFilter, int pageSize, int offset ) throws IOException {
        return getPagesFromOffsetWithCriteriaFilter( resourceName, DEFAULT_VERSION, criteriaFilter, pageSize, offset );
    }

    /**
     * Gets all the pages for a given resource beginning at the given offset index, using the specified criteria filter
     * and page size for the given version.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaFilter A previously built CriteriaFilter containing the filter criteria used in the request URL.
     *                       A simple call to criteriaFilter.toString() should output the criteria filter portion of the request URL,
     *                       e.g: <code>?criteria={"names":[{"firstName":"John"}]}</code>.
     * @param pageSize The size (number of rows) of each page returned in the list.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffsetWithCriteriaFilter( String resourceName, String version, CriteriaFilter criteriaFilter, int pageSize, int offset ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot get pages of resource from offset with criteria filter due to a null or blank resource name." );
        }
        if( criteriaFilter == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get pages of resource \"%s\" from offset with criteria filter due to a null criteria filter reference.", resourceName) );
        }
        List<EthosResponse> ethosResponseList = new ArrayList<>();
        Pager pager = new Pager.Builder(resourceName)
                .forVersion(version)
                .withCriteriaFilter(criteriaFilter.toString())
                .withPageSize(pageSize)
                .fromOffset(offset)
                .build();
        pager = prepareForPaging( pager );
        pager = shouldDoPaging( pager, false );
        if( pager.isShouldDoPaging() ) {
            ethosResponseList = doPagingFromOffset( pager.getResourceName(), pager.getVersion(), pager.getCriteriaFilter(), pager.getTotalCount(), pager.getPageSize(), offset );
        }
        else {
            ethosResponseList.add( getWithCriteriaFilter(resourceName, version, criteriaFilter) );
        }
        return ethosResponseList;
    }

    /**
     * Gets all the pages for a given resource using the specified named query filter.  Uses the default version of the resource,
     * and the page size is derived from the length of the returned response of the request using the named query filter.
     * @param resourceName The name of the resource to get data for.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                         A simple call to namedQueryFilter.toString() should output the filter portion of the request URL,
     *                         e.g: <code>?keywordSearch={"keywordSearch": "someKeyword"}</code>.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesWithNamedQueryFilter( String resourceName, NamedQueryFilter namedQueryFilter ) throws IOException {
        return getPagesWithNamedQueryFilter( resourceName, DEFAULT_VERSION, namedQueryFilter, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets all the pages for a given resource using the specified named query filter for the given version.  Uses the default
     * page size, which is the length of the returned response of the request using the named query filter.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                         A simple call to namedQueryFilter.toString() should output the filter portion of the request URL,
     *                         e.g: <code>?keywordSearch={"keywordSearch": "someKeyword"}</code>.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesWithNamedQueryFilter( String resourceName, String version, NamedQueryFilter namedQueryFilter ) throws IOException {
        return getPagesWithNamedQueryFilter( resourceName, version, namedQueryFilter, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets all the pages for a given resource using the specified named query filter and page size.  The default version
     * of the resource is used.
     * @param resourceName The name of the resource to get data for.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                         A simple call to namedQueryFilter.toString() should output the filter portion of the request URL,
     *                         e.g: <code>?keywordSearch={"keywordSearch": "someKeyword"}</code>.
     * @param pageSize The size (number of rows) of each page returned in the list.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesWithNamedQueryFilter( String resourceName, NamedQueryFilter namedQueryFilter, int pageSize ) throws IOException {
        return getPagesWithNamedQueryFilter( resourceName, DEFAULT_VERSION, namedQueryFilter, pageSize );
    }

    /**
     * Gets all the pages for a given resource using the specified named query filter and page size for the given version.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                         A simple call to namedQueryFilter.toString() should output the filter portion of the request URL,
     *                         e.g: <code>?keywordSearch={"keywordSearch": "someKeyword"}</code>.
     * @param pageSize The size (number of rows) of each page returned in the list.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesWithNamedQueryFilter( String resourceName, String version, NamedQueryFilter namedQueryFilter, int pageSize ) throws IOException {
        return getPagesFromOffsetWithNamedQueryFilter( resourceName, version, namedQueryFilter, pageSize, 0 );
    }

    /**
     * Gets all the pages for a given resource beginning at the given offset index, using the specified named query filter.
     * The page size is determined to be the length of the returned response of the request using the named query filter.
     * The default version of the resource is used.
     * @param resourceName The name of the resource to get data for.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                         A simple call to namedQueryFilter.toString() should output the filter portion of the request URL,
     *                         e.g: <code>?keywordSearch={"keywordSearch": "someKeyword"}</code>.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffsetWithNamedQueryFilter( String resourceName, NamedQueryFilter namedQueryFilter, int offset ) throws IOException {
        return getPagesFromOffsetWithNamedQueryFilter( resourceName, DEFAULT_VERSION, namedQueryFilter, offset );
    }

    /**
     * Gets all the pages for a given resource beginning at the given offset index, using the specified named query filter
     * for the given version.  The page size is determined to be the length of the returned response of the request using
     * the named query filter.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                         A simple call to namedQueryFilter.toString() should output the filter portion of the request URL,
     *                         e.g: <code>?keywordSearch={"keywordSearch": "someKeyword"}</code>.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffsetWithNamedQueryFilter(String resourceName, String version, NamedQueryFilter namedQueryFilter, int offset ) throws IOException {
        return getPagesFromOffsetWithNamedQueryFilter( resourceName, version, namedQueryFilter, DEFAULT_PAGE_SIZE, offset );
    }

    /**
     * Gets all the pages for a given resource beginning at the given offset index, using the specified named query filter
     * and page size for the given version.  The default version of the resource is used.
     * @param resourceName The name of the resource to get data for.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                         A simple call to namedQueryFilter.toString() should output the criteria filter portion of the request URL,
     *                         e.g: <code>?keywordSearch={"keywordSearch": "someKeyword"}</code>.
     * @param pageSize The size (number of rows) of each page returned in the list.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffsetWithNamedQueryFilter( String resourceName, NamedQueryFilter namedQueryFilter, int pageSize, int offset ) throws IOException {
        return getPagesFromOffsetWithNamedQueryFilter( resourceName, DEFAULT_VERSION, namedQueryFilter, pageSize, offset );
    }

    /**
     * Gets all the pages for a given resource beginning at the given offset index, using the specified named query filter
     * and page size for the given version.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param namedQueryFilter A previously built NamedQueryFilter containing the filter criteria used in the request URL.
     *                         A simple call to namedQueryFilter.toString() should output the filter portion of the request URL,
     *                         e.g: <code>?keywordSearch={"keywordSearch": "someKeyword"}</code>.
     * @param pageSize The size (number of rows) of each page returned in the list.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffsetWithNamedQueryFilter( String resourceName, String version, NamedQueryFilter namedQueryFilter, int pageSize, int offset ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot get pages of resource from offset with named query filter due to a null or blank resource name." );
        }
        if( namedQueryFilter == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get pages of resource \"%s\" from offset with named query filter due to a null named query filter reference.", resourceName) );
        }
        List<EthosResponse> ethosResponseList = new ArrayList<>();
        Pager pager = new Pager.Builder(resourceName)
                .forVersion(version)
                .withNamedQueryFilter(namedQueryFilter.toString())
                .withPageSize(pageSize)
                .fromOffset(offset)
                .build();
        pager = prepareForPaging( pager );
        pager = shouldDoPaging( pager, false );
        if( pager.isShouldDoPaging() ) {
            ethosResponseList = doPagingFromOffset( pager.getResourceName(), pager.getVersion(), pager.getNamedQueryFilter(), pager.getTotalCount(), pager.getPageSize(), offset );
        }
        else {
            ethosResponseList.add( getWithNamedQueryFilter(resourceName, version, namedQueryFilter) );
        }
        return ethosResponseList;
    }

    /**
     * Gets all the pages for a given resource using the specified filter map and page size for the given version.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param filterMap A previously built FilterMap containing the filter parameters used in the request URL.
     *                  A simple call to filterMap.toString() should output the criteria filter portion of the request URL,
     *                  e.g: <code>?firstName=John&amp;lastName=Smith</code>.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesWithFilterMap( String resourceName, String version, FilterMap filterMap ) throws IOException {
        return getPagesWithFilterMap( resourceName, version, filterMap, DEFAULT_PAGE_SIZE );
    }


    /**
     * Gets all the pages for a given resource using the specified filter map and page size for the given version.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param filterMap A previously built FilterMap containing the filter parameters used in the request URL.
     *                  A simple call to filterMap.toString() should output the criteria filter portion of the request URL,
     *                  e.g: <code>?firstName=John&amp;lastName=Smith</code>.
     * @param pageSize The size (number of rows) of each page returned in the list.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesWithFilterMap( String resourceName, String version, FilterMap filterMap, int pageSize ) throws IOException {
        return getPagesFromOffsetWithFilterMap( resourceName, version, filterMap, pageSize, 0 );
    }


    /**
     * Gets all the pages for a given resource beginning at the given offset index, using the specified filter map and
     * page size for the given version.  The page size is determined to be the length of the returned response of the request using
     * the filter map.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param filterMap A previously built FilterMap containing the filter parameters used in the request URL.
     *                  A simple call to filterMap.toString() should output the criteria filter portion of the request URL,
     *                  e.g: <code>?firstName=John&amp;lastName=Smith</code>.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffsetWithFilterMap( String resourceName, String version, FilterMap filterMap, int offset ) throws IOException {
        return getPagesFromOffsetWithFilterMap( resourceName, version, filterMap, DEFAULT_PAGE_SIZE, offset );
    }


    /**
     * Gets all the pages for a given resource beginning at the given offset index, using the specified filter map and
     * page size for the given version.
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param filterMap A previously built FilterMap containing the filter parameters used in the request URL.
     *                  A simple call to filterMap.toString() should output the criteria filter portion of the request URL,
     *                  e.g: <code>?firstName=John&amp;lastName=Smith</code>.
     * @param pageSize The size (number of rows) of each page returned in the list.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @return A list of EthosResponses where each EthosResponse contains a page of data.  If paging is not required based on the
     *         given pageSize and total count (from using the filter), the returned list will only contain one EthosResponse.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<EthosResponse> getPagesFromOffsetWithFilterMap( String resourceName, String version, FilterMap filterMap, int pageSize, int offset ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException("Error: Cannot get pages of resource with filter map due to a null or blank resource name." );
        }
        if( filterMap == null ) {
            throw new IllegalArgumentException( String.format("Error: Cannot get pages of resource \"%s\" with filter map due to a null filter map reference.", resourceName) );
        }
        List<EthosResponse> ethosResponseList = new ArrayList<>();
        Pager pager = new Pager.Builder(resourceName)
                          .forVersion(version)
                          .withFilterMap(filterMap.toString())
                          .withPageSize(pageSize)
                          .fromOffset(offset)
                          .build();
        pager = prepareForPaging( pager );
        pager = shouldDoPaging( pager, false );
        if( pager.isShouldDoPaging() ) {
            ethosResponseList = doPagingFromOffset( pager.getResourceName(), pager.getVersion(), pager.getFilterMap(), pager.getTotalCount(), pager.getPageSize(), offset );
        }
        else {
            ethosResponseList.add( getWithFilterMap(resourceName, version, filterMap) );
        }
        return ethosResponseList;
    }

    /**
     * Gets the total count of resources available using the given criteriaFilter.
     * @param resourceName The name of the Ethos resource to get a total count for.
     * @param criteriaFilter The criteria filter to use when determining how many instances of the resource are available using that filter.
     * @return The number of resource instances available when making a GET request using the given criteriaFilter, or 0 if the
     *         given resourceName or criteriaFilter is null.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getTotalCount( String resourceName, CriteriaFilter criteriaFilter ) throws IOException {
        return getTotalCount( resourceName, DEFAULT_VERSION, criteriaFilter );
    }


    /**
     * Gets the total count of resources available using the given criteriaFilter.
     * @param resourceName The name of the Ethos resource to get a total count for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param criteriaFilter The criteria filter to use when determining how many instances of the resource are available using that filter.
     * @return The number of resource instances available when making a GET request using the given criteriaFilter, or 0 if the
     *         given resourceName or criteriaFilter is null.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getTotalCount( String resourceName, String version, CriteriaFilter criteriaFilter ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            return 0;
        }
        if( criteriaFilter == null ) {
            return 0;
        }
        EthosResponse ethosResponse = getWithCriteriaFilter( resourceName, version, criteriaFilter.toString() );
        String totalCount = getHeaderValue( ethosResponse, HDR_X_TOTAL_COUNT );
        return Integer.valueOf( totalCount );
    }

    /**
     * Gets the total count of resources available using the given namedQueryFilter.
     * @param resourceName The name of the Ethos resource to get a total count for.
     * @param namedQueryFilter The named query filter to use when determining how many instances of the resource are available using that filter.
     * @return The number of resource instances available when making a GET request using the given namedQueryFilter, or 0 if the
     *         given resourceName or namedQueryFilter is null.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getTotalCount( String resourceName, NamedQueryFilter namedQueryFilter ) throws IOException {
        return getTotalCount( resourceName, DEFAULT_VERSION, namedQueryFilter );
    }

    /**
     * Gets the total count of resources available using the given namedQueryFilter.
     * @param resourceName The name of the Ethos resource to get a total count for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param namedQueryFilter The named query filter to use when determining how many instances of the resource are available using that filter.
     * @return The number of resource instances available when making a GET request using the given namedQueryFilter, or 0 if the
     *         given resourceName or namedQueryFilter is null.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getTotalCount( String resourceName, String version, NamedQueryFilter namedQueryFilter ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            return 0;
        }
        if( namedQueryFilter == null ) {
            return 0;
        }
        EthosResponse ethosResponse = getWithNamedQueryFilter( resourceName, version, namedQueryFilter.toString() );
        String totalCount = getHeaderValue( ethosResponse, HDR_X_TOTAL_COUNT );
        return Integer.valueOf( totalCount );
    }

    /**
     * Gets the total count of resources available using the given filterMap.
     * @param resourceName The name of the Ethos resource to get a total count for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param filterMap The filter map to use when determining how many instances of the resource are available using that filter.
     * @return The number of resource instances available when making a GET request using the given filterMap, or 0 if the
     *         given resourceName or filterMap is null.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public int getTotalCount( String resourceName, String version, FilterMap filterMap ) throws IOException {
        if( resourceName == null || resourceName.isBlank() ) {
            return 0;
        }
        if( filterMap == null ) {
            return 0;
        }
        EthosResponse ethosResponse = getWithFilterMap( resourceName, version, filterMap.toString() );
        String totalCount = getHeaderValue( ethosResponse, HDR_X_TOTAL_COUNT );
        return Integer.valueOf( totalCount );
    }


    /**
     * <p><b>Intended to be used internally within the SDK.</b>
     * </p>
     * Overrides the <code>prepareForPaging()</code> method in the EthosProxyClient super class.
     * <p>
     * Uses the given pager object to prepare for paging operations.  The pager object is used to contain various
     * fields required for paging.  If the given pager is null, returns the same pager.  Sets default values for the
     * version and offset within the pager as needed and makes an initial resource call using the provided pager filter
     * to get metadata about the resource used for paging.  Also sets the page size and the total count within the pager,
     * and encodes the filter within the pager.
     * @param pager The Pager object used holding the required fields for paging.
     * @return The same pager object with the version and offset validated, the page size and total count set, and the filter encoded.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    @Override
    protected Pager prepareForPaging( Pager pager ) throws IOException {
        if( pager == null ) {
            return pager;
        }
        if( pager.getVersion() == null || pager.getVersion().trim().isEmpty() ) {
            pager.setVersion( DEFAULT_VERSION );
        }
        if( pager.getOffset() < 1 ) {
            pager.setOffset( 0 );
        }

        pager = preparePagerForTotalCount( pager );

        // Set the pageSize.
        pager = preparePagerForPageSize( pager );

        // Encode the criteriaFilter if it is not null.
        if( pager.getCriteriaFilter() != null ) {
            pager.setCriteriaFilter( encodeCriteriaFilterStr(pager.getCriteriaFilter()) );
        }
        // Encode the namedQueryFilter if it is not null.
        if( pager.getNamedQueryFilter() != null ) {
            pager.setNamedQueryFilter( encodeNamedQueryStr(pager.getNamedQueryFilter()) );
        }
        return pager;
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Prepares the pager object for the total count required for paging calculations.
     * The total count is derived from the response x-total-count header after making an initial request using filters
     * (in this case).
     * @param pager The pager object used to contain the total count for paging operations.
     * @return The pager object containing the total count.  If neither a criteria filter nor a filter map is specified
     *         in the pager, then the total count wil be 0.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    protected Pager preparePagerForTotalCount( Pager pager ) throws IOException {
        EthosResponse ethosResponse = null;
        if( pager.getCriteriaFilter() != null ) {
            ethosResponse = getWithCriteriaFilter( pager.getResourceName(), pager.getVersion(), pager.getCriteriaFilter() );
            pager.setEthosResponse( ethosResponse );
        }
        else if( pager.getNamedQueryFilter() != null ) {
            ethosResponse = getWithNamedQueryFilter( pager.getResourceName(), pager.getVersion(), pager.getNamedQueryFilter() );
            pager.setEthosResponse( ethosResponse );
        }
        else if( pager.getFilterMap() != null ) {
            ethosResponse = getWithFilterMap( pager.getResourceName(), pager.getVersion(), pager.getFilterMap() );
            pager.setEthosResponse( ethosResponse );
        }
        else {
            super.prepareForPaging( pager ); // Call super.prepareForPaging() if no criteria filter, named query, or filter map is specified.
        }

        if( ethosResponse != null ) {
            String totalCount = getHeaderValue(ethosResponse, HDR_X_TOTAL_COUNT);
            pager.setTotalCount(Integer.valueOf(totalCount));
        }
        return pager;
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * If the page size specified in the pager is &lt;= DEFAULT_PAGE_SIZE, then this method prepares the pager object for the
     * page size required for paging calculations. The page size is derived from the response body length after making an initial request using filters
     * (in this case). If the response is null, the DEFAULT_MAX_PAGE_SIZE is used.  If the response body is null, the
     * x-max-page-size header is used.
     * <p>
     * If the page size specified in the pager is &gt; DEFAULT_PAGE_SIZE, this method does nothing and just returns the given pager.
     * @param pager The pager object used to contain the page size for paging operations.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return The pager object containing the page size.
     */
    protected Pager preparePagerForPageSize( Pager pager ) throws IOException {
        if( pager.getPageSize() <= DEFAULT_PAGE_SIZE ) {
            if( pager.getEthosResponse() == null ) {
                pager.setPageSize( DEFAULT_MAX_PAGE_SIZE ); // Set the page size to the MAX default because there is no ethosResponse.
            }
            // Set the pageSize from the response body length, if pageSize is <= DEFAULT_PAGE_SIZE.
            else if( pager.getEthosResponse().getContent() != null && pager.getEthosResponse().getContent().isBlank() == false ) {
                int pageSize = pager.getEthosResponse().getContentAsJson().size();
                pager.setPageSize( pageSize );
            }
            else {
                String maxPageSizeStr = getHeaderValue( pager.getEthosResponse(), HDR_X_MAX_PAGE_SIZE );
                if( maxPageSizeStr != null && maxPageSizeStr.isBlank() == false ) {
                    pager.setPageSize(Integer.valueOf(maxPageSizeStr));
                }
                else {
                    pager.setPageSize( DEFAULT_MAX_PAGE_SIZE );
                }
            }
        }
        return pager;
    }

    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * Encodes the given criteriaFilterStr.  Supports criteria filter strings that begin with the CRITERIA_FILTER_PREFIX
     * value "?criteria=".  Encodes only the JSON criteria portion of the filter string, removing
     * the CRITERIA_FILTER_PREFIX portion if the filter string starts with it.  If the filter string does not start with
     * the CRITERIA_FILTER_PREFIX, the criteriaFilterStr string is simply encoded.
     * <p>
     * Returns a criteria filter string that begins with the CRITERIA_FILTER_PREFIX, with the JSON filter portion of the string
     * encoded.  Uses UTF-8 encoding.
     * @param criteriaFilterStr The criteria filter string to encode.
     * @return A criteria filter string beginning with the CRITERIA_FILTER_PREFIX with the JSON filter syntax portion of the
     *         string encoded in UTF-8.
     * @throws UnsupportedEncodingException Thrown if the named encoding is not supported.
     */
    private String encodeCriteriaFilterStr( String criteriaFilterStr ) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        String jsonCriteriaStr = "";
        if( criteriaFilterStr.startsWith(CRITERIA_FILTER_PREFIX) ) {
            // It starts with "?criteria=", so substring the rest of the filter and encode it.
            jsonCriteriaStr = criteriaFilterStr.substring( criteriaFilterStr.indexOf("=") + 1 );
        }
        else {
            jsonCriteriaStr = criteriaFilterStr;
        }
        jsonCriteriaStr = URLEncoder.encode( jsonCriteriaStr, "UTF-8" );
        sb.append( CRITERIA_FILTER_PREFIX );
        sb.append( jsonCriteriaStr );
        return sb.toString();
    }

    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * Encodes the given namedQueryStr.
     * <p>
     * Returns a UTF-8 encoded named query filter string.
     * @param namedQueryStr The named query filter string to encode.
     * @return A named query filter string encoded in UTF-8.
     * @throws UnsupportedEncodingException Thrown if the named encoding is not supported.
     */
    private String encodeNamedQueryStr( String namedQueryStr ) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if( namedQueryStr.contains("=") == false ) {
            sb.append( URLEncoder.encode(namedQueryStr, "UTF-8") );
        }
        else {
            String prefix = namedQueryStr.substring( 0, namedQueryStr.indexOf('=') + 1 );
            String strToEncode = namedQueryStr.substring( namedQueryStr.indexOf('=') + 1 );
            sb.append( prefix );
            sb.append( URLEncoder.encode(strToEncode, "UTF-8") );
        }
        return sb.toString();
    }

}