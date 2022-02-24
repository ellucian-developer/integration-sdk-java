package com.ellucian.ethos.integration.client.proxy;

import com.ellucian.ethos.integration.client.EthosClient;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * An EthosProxyClient used to retrieve data from the Ethos Integration Proxy API, asynchronously.
 *
 * This class has the same function and purpose as {@link EthosProxyClient}, but wraps returns in CompletableFutures
 * so that the calls can be made in an asynchronous way.
 * <p>
 * CompletableFutures can be handled as follows:
 *
 * <pre>
 * CompletableFuture&lt;List&lt;String&gt;&gt; asyncResponse = ethosProxyClient.getAllPagesFromOffsetAsStringsAsync(resourceName, offset);
 *
 * // using .join here so there is no exception thrown - all exceptions will be unchecked.  This is the
 * // same as in C# where the default is to use the async processing without checked exceptions.
 * List&lt;String&gt; stringList = asyncResponse.join();
 *
 * // or use .get or .getNow to handle with checked exceptions:
 * List&lt;String&gt; anotherStringList = asyncResponse.get();
 * </pre>
 *
 */
public class EthosProxyClientAsync extends EthosProxyClient {

    /**
     * Instantiates this class using the given API key.
     * <p>
     * Note that the preferred way to get an instance of this class is through the {@link com.ellucian.ethos.integration.client.EthosClientBuilder}.
     *
     * @param apiKey                   A valid API key from Ethos Integration.  This is required to be a valid 36 character GUID string.
     *                                 If it is null, empty, or not in a valid GUID format, then an <code>IllegalArgumentException</code> will be thrown.
     * @param connectionTimeout        The timeout <b>in seconds</b> for a connection to be established.
     * @param connectionRequestTimeout The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager.
     * @param socketTimeout            The timeout <b>in seconds</b> when waiting for data between consecutive data packets.
     */
    public EthosProxyClientAsync(String apiKey, Integer connectionTimeout, Integer connectionRequestTimeout, Integer socketTimeout) {
        super(apiKey, connectionTimeout, connectionRequestTimeout, socketTimeout);
    }

    /**
     * Gets all pages for the given resource.  Uses the default page size of the response body content length, and the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getAllPagesAsync(String resourceName ) throws CompletionException {
        return getAllPagesAsync( resourceName, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets all pages for the given resource and version.  Uses the default page size of the response body content length.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data according to the requested version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getAllPagesAsync( String resourceName, String version ) throws CompletionException {
        return getAllPagesAsync( resourceName, version, DEFAULT_PAGE_SIZE );
    }

    /**
     * Gets all pages for the given resource and page size.  Uses the default version of the resource.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the current version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getAllPagesAsync( String resourceName, int pageSize ) throws CompletionException {
        return getAllPagesAsync( resourceName, DEFAULT_VERSION, pageSize );
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
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the requested version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getAllPagesAsync(String resourceName, String version, int pageSize)
            throws CompletionException {
        CompletableFuture<List<EthosResponse>> asyncResponse = CompletableFuture.supplyAsync(() -> {
            List<EthosResponse> response;
            try {
                response = getAllPages(resourceName, version, pageSize);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return asyncResponse;
    }

    /**
     * Gets all pages for the given resource.  Uses the default page size of the response body content length, and the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the resource to get data for.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list
     *         represents a page of data according to the default version of the resource.  The page size of each page
     *         is determined by the response body content length.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getAllPagesAsStringsAsync( String resourceName ) throws CompletionException {
        return this.getAllPagesAsStringsAsync(resourceName, DEFAULT_PAGE_SIZE);
    }

    /**
     * Gets all pages for the given resource and version.  Uses the default page size of the response body content length.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list
     *         represents a page of data according to the requested version of the resource.  The page size of each page
     *         is determined by the response body content length.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getAllPagesAsStringsAsync(String resourceName, String version ) throws CompletionException {
        return this.getAllPagesAsStringsAsync(resourceName, version, DEFAULT_PAGE_SIZE);
    }

    /**
     * Gets all pages for the given resource and page size.  Uses the default version of the resource.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list
     *         represents a page of data with the given page size according to the default version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getAllPagesAsStringsAsync( String resourceName, int pageSize ) throws CompletionException {
        return this.getAllPagesAsStringsAsync(resourceName, DEFAULT_VERSION, pageSize);
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
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list
     *         represents a page of data with the given page size according to the requested version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getAllPagesAsStringsAsync( String resourceName, String version, int pageSize ) throws CompletionException {
        CompletableFuture<List<String>> asyncResponse = CompletableFuture.supplyAsync(() -> {
            List<String> pagedStrings = null;
            try {
                List<EthosResponse> ethosResponseList = getAllPages( resourceName, version, pageSize );
                pagedStrings = ethosResponseConverter.toPageBasedStringList( ethosResponseList );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return pagedStrings;
        });
        return asyncResponse;
    }

    /**
     * Gets all pages for the given resource.  Uses the default page size of the response body content length, and the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list
     *         represents a page of data according to the default version of the resource.  The page size of each page
     *         is determined by the response body content length.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getAllPagesAsJsonNodesAsync(String resourceName ) throws CompletionException {
        return this.getAllPagesAsJsonNodesAsync(resourceName, DEFAULT_PAGE_SIZE);
    }

    /**
     * Gets all pages for the given resource and version.  Uses the default page size of the response body content length.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list
     *         represents a page of data according to the requested version of the resource.  The page size of each page
     *         is determined by the response body content length.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getAllPagesAsJsonNodesAsync( String resourceName, String version ) throws CompletionException {
        return this.getAllPagesAsJsonNodesAsync(resourceName, version, DEFAULT_PAGE_SIZE);
    }

    /**
     * Gets all pages for the given resource and page size.  Uses the default version.
     * <p><b>NOTE: This method could result in a long running process and return a large volume of data.  It is possible that
     * an <code>OutOfMemoryError</code> could occur if trying to get a large quantity of data.  This is NOT intended to be
     * used for any kind of resource bulk loading of data.  The Ethos bulk loading solution should be used for loading
     * data in Ethos data model format in bulk.</b></p>
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list
     *         represents a page of data with the given page size according to the default version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getAllPagesAsJsonNodesAsync( String resourceName, int pageSize ) throws CompletionException {
        return this.getAllPagesAsJsonNodesAsync(resourceName, DEFAULT_VERSION, pageSize);
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
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list
     *         represents a page of data with the given page size according to the requested version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getAllPagesAsJsonNodesAsync( String resourceName, String version, int pageSize )
            throws CompletionException {
        CompletableFuture<List<JsonNode>> asyncResponse = CompletableFuture.supplyAsync(() -> {
            List<JsonNode> pagedNodes = null;
            try {
                List<EthosResponse> ethosResponseList = getAllPages( resourceName, version, pageSize );
                pagedNodes = ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return pagedNodes;
        });
        return asyncResponse;
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
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data according to the default version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getAllPagesFromOffsetAsync( String resourceName, int offset ) throws CompletionException {
        return this.getAllPagesFromOffsetAsync(resourceName, offset, DEFAULT_MAX_PAGE_SIZE);
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
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the default version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getAllPagesFromOffsetAsync( String resourceName, int offset, int pageSize ) throws CompletionException {
        return this.getAllPagesFromOffsetAsync( resourceName, DEFAULT_VERSION, offset, pageSize );
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
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the response content body length as the page size according to the requested version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getAllPagesFromOffsetAsync( String resourceName, String version, int offset ) throws CompletionException {
        return this.getAllPagesFromOffsetAsync(resourceName, version, offset, DEFAULT_PAGE_SIZE );
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
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s from the given offset where the content of each <code>EthosResponse</code> in the list
     *         represents a page of data with the given page size according to the requested version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getAllPagesFromOffsetAsync( String resourceName, String version, int offset, int pageSize ) throws CompletionException {
        CompletableFuture<List<EthosResponse>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<EthosResponse> response = null;
            try {
                response = getAllPagesFromOffset(resourceName, version, offset, pageSize);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
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
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data from
     *         the given offset with the response content body length as the page size according to the default version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getAllPagesFromOffsetAsStringsAsync( String resourceName, int offset ) throws CompletionException {
        return this.getAllPagesFromOffsetAsStringsAsync(resourceName, offset, DEFAULT_PAGE_SIZE);
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
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data from
     *         the given offset with the given page size according to the default version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getAllPagesFromOffsetAsStringsAsync( String resourceName, int offset, int pageSize ) throws CompletionException {
        return this.getAllPagesFromOffsetAsStringsAsync(resourceName, DEFAULT_VERSION, offset, pageSize);
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
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data from
     *         the given offset with the response content body length as the page size according to the requested version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getAllPagesFromOffsetAsStringsAsync( String resourceName, String version, int offset )
            throws CompletionException {
        return this.getAllPagesFromOffsetAsStringsAsync(resourceName, version, offset, DEFAULT_PAGE_SIZE);
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
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data from
     *         the given offset with the given page size according to the requested version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getAllPagesFromOffsetAsStringsAsync( String resourceName, String version, int offset, int pageSize ) throws CompletionException {
        CompletableFuture<List<String>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<String> response = null;
            try {
                List<EthosResponse> ethosResponseList =
                        getAllPagesFromOffset( resourceName, version, offset, pageSize );
                response = ethosResponseConverter.toPageBasedStringList( ethosResponseList );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
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
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data from
     *         the given offset with the response content body length as the page size according to the default version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getAllPagesFromOffsetAsJsonNodesAsync( String resourceName, int offset ) throws CompletionException {
        return this.getAllPagesFromOffsetAsJsonNodesAsync(resourceName, offset, DEFAULT_PAGE_SIZE);
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
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data from
     *         the given offset with the given page size according to the default version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getAllPagesFromOffsetAsJsonNodesAsync( String resourceName, int offset, int pageSize ) throws CompletionException {
        return this.getAllPagesFromOffsetAsJsonNodesAsync(resourceName, DEFAULT_VERSION, offset, pageSize);
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
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data from
     *         the given offset with the response content body length as the page size according to the requested version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getAllPagesFromOffsetAsJsonNodesAsync( String resourceName, String version, int offset ) throws CompletionException {
        return this.getAllPagesFromOffsetAsJsonNodesAsync(resourceName, version, offset, DEFAULT_PAGE_SIZE);
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
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data from
     *         the given offset with the given page size according to the requested version of the resource.
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getAllPagesFromOffsetAsJsonNodesAsync( String resourceName, String version, int offset, int pageSize ) throws CompletionException {
        CompletableFuture<List<JsonNode>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<JsonNode> response = null;
            try {
                List<EthosResponse> ethosResponseList = getAllPagesFromOffset( resourceName, version, offset, pageSize );
                response = ethosResponseConverter.toPageBasedJsonNodeList( ethosResponseList );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
    }

    /**
     * Gets some number of pages for the given resource.  Uses the default page size of the response body content length,
     * and default version.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource, up to the
     *         number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getPagesAsync( String resourceName, int numPages ) throws CompletionException {
        return getPagesAsync( resourceName, DEFAULT_VERSION, numPages );
    }

    /**
     * Gets some number of pages for the given resource and version.  Uses the default page size of the response body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource, up to the
     *         number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getPagesAsync( String resourceName, String version, int numPages ) throws CompletionException {
        return getPagesAsync( resourceName, version, DEFAULT_PAGE_SIZE, numPages );
    }


    /**
     * Gets some number of pages for the given resource and page size.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource, up to the
     *         number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getPagesAsync( String resourceName, int pageSize, int numPages ) throws CompletionException {
        return getPagesAsync( resourceName, DEFAULT_VERSION, pageSize, numPages );
    }

    /**
     * Gets some number of pages for the given resource, version, and page size.  If numPages is negative, all pages
     * will be returned.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource, up to the
     *         number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getPagesAsync( String resourceName, String version, int pageSize, int numPages ) throws CompletionException {
        CompletableFuture<List<EthosResponse>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<EthosResponse> response = null;
            try {
                response = getPages(resourceName, version, pageSize, numPages);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
    }

    /**
     * Gets some number of pages for the given resource.  Uses the default page size of the response body content length,
     * and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getPagesAsStringsAsync( String resourceName, int numPages ) throws CompletionException {
        return getPagesAsStringsAsync(resourceName, DEFAULT_VERSION, numPages);
    }

    /**
     * Gets some number of pages for the given resource.  Uses the default page size of the response body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getPagesAsStringsAsync( String resourceName, String version, int numPages ) throws CompletionException {
        return getPagesAsStringsAsync(resourceName, version, DEFAULT_PAGE_SIZE, numPages);
    }

    /**
     * Gets some number of pages for the given resource and page size.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getPagesAsStringsAsync( String resourceName, int pageSize, int numPages ) throws CompletionException {
         return getPagesAsStringsAsync(resourceName, DEFAULT_VERSION, pageSize, numPages);
    }

    /**
     * Gets some number of pages for the given resource, version, and page size.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getPagesAsStringsAsync( String resourceName, String version, int pageSize, int numPages ) throws CompletionException {
        CompletableFuture<List<String>> asyncResponse = CompletableFuture.supplyAsync(() -> {
            List<String> pagedStrings = null;
            try {
                pagedStrings = getPagesAsStrings( resourceName, version, pageSize, numPages );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return pagedStrings;
        });
        return asyncResponse;
    }

    /**
     * Gets some number of pages for the given resource.  Uses the default page size of the response body content length,
     * and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getPagesAsJsonNodesAsync( String resourceName, int numPages ) throws CompletionException {
        return getPagesAsJsonNodesAsync(resourceName, DEFAULT_VERSION, numPages);
    }

    /**
     * Gets some number of pages for the given resource and version.  Uses the default page size of the response body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getPagesAsJsonNodesAsync( String resourceName, String version, int numPages ) throws CompletionException {
        return getPagesAsJsonNodesAsync(resourceName, version, DEFAULT_PAGE_SIZE, numPages);
    }

    /**
     * Gets some number of pages for the given resource and page size.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getPagesAsJsonNodesAsync( String resourceName, int pageSize, int numPages ) throws CompletionException {
        return getPagesAsJsonNodesAsync(resourceName, DEFAULT_VERSION, pageSize, numPages);
    }

    /**
     * Gets some number of pages for the given resource, version, and page size.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getPagesAsJsonNodesAsync( String resourceName, String version, int pageSize, int numPages ) throws CompletionException {
         CompletableFuture<List<JsonNode>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<JsonNode> response = null;
            try {
                response = getPagesAsJsonNodes(resourceName, version, pageSize, numPages);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
    }

    /**
     * Gets some number of pages for the given resource from the given offset.  Uses the default page size of the response
     * body content length, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getPagesFromOffsetAsync( String resourceName, int offset, int numPages ) throws CompletionException {
        return getPagesFromOffsetAsync( resourceName, DEFAULT_VERSION, offset, numPages );
    }

    /**
     * Gets some number of pages for the given resource and page size from the given offset.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getPagesFromOffsetAsync( String resourceName, int pageSize, int offset, int numPages ) throws CompletionException {
        return getPagesFromOffsetAsync( resourceName, DEFAULT_VERSION, pageSize, offset, numPages );
    }

    /**
     * Gets some number of pages for the given resource and version from the given offset.  Uses the default page size of the response
     * body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getPagesFromOffsetAsync( String resourceName, String version, int offset, int numPages ) throws CompletionException {
        return getPagesFromOffsetAsync( resourceName, version, DEFAULT_PAGE_SIZE, offset, numPages );
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
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getPagesFromOffsetAsync( String resourceName, String version, int pageSize,
        int offset, int numPages ) throws CompletionException {

        CompletableFuture<List<EthosResponse>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<EthosResponse> response = null;
            try {
                response = this.getPagesFromOffset(resourceName, version, pageSize, offset, numPages);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
    }

    /**
     * Gets some number of pages for the given resource from the given offset.  Uses the default page size of the response
     * body content length, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getPagesFromOffsetAsStringsAsync( String resourceName, int offset, int numPages ) throws CompletionException {
        return this.getPagesFromOffsetAsStringsAsync(resourceName, DEFAULT_VERSION, offset, numPages);
    }

    /**
     * Gets some number of pages for the given resource and page size from the given offset.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getPagesFromOffsetAsStringsAsync( String resourceName, int pageSize, int offset, int numPages ) throws CompletionException {
        return this.getPagesFromOffsetAsStringsAsync(resourceName, DEFAULT_VERSION, pageSize, offset, numPages);
    }

    /**
     * Gets some number of pages for the given resource and version from the given offset.  Uses the default page size of the response
     * body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getPagesFromOffsetAsStringsAsync( String resourceName, String version, int offset, int numPages ) throws CompletionException {
        return this.getPagesFromOffsetAsStringsAsync(resourceName, version, DEFAULT_PAGE_SIZE, offset, numPages);
    }

    /**
     * Gets some number of pages for the given resource, version, and page size, from the given offset.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (String) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>String</code>s where each <code>String</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getPagesFromOffsetAsStringsAsync( String resourceName, String version, int pageSize, int offset, int numPages ) throws CompletionException {
        CompletableFuture<List<String>> asyncResponse = CompletableFuture.supplyAsync(() -> {
            List<String> response = null;
            try {
                List<EthosResponse> ethosResponseList =
                        getPagesFromOffset( resourceName, version, offset, pageSize );
                response = ethosResponseConverter.toPageBasedStringList( ethosResponseList );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return asyncResponse;
    }

    /**
     * Gets some number of pages for the given resource from the given offset.  Uses the default page size of the response
     * body content length, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getPagesFromOffsetAsJsonNodesAsync(String resourceName, int offset, int numPages)
            throws CompletionException {
        return getPagesFromOffsetAsJsonNodesAsync(resourceName, DEFAULT_VERSION, offset, numPages);
    }

    /**
     * Gets some number of pages for the given resource and page size, from the given offset.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getPagesFromOffsetAsJsonNodesAsync(String resourceName, int pageSize, int offset, int numPages ) throws CompletionException {
        return getPagesFromOffsetAsJsonNodesAsync(resourceName, DEFAULT_VERSION, pageSize, offset, numPages);
    }

    /**
     * Gets some number of pages for the given resource and version, from the given offset.  Uses the default page size of the response
     * body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getPagesFromOffsetAsJsonNodesAsync( String resourceName, String version, int offset, int numPages )
            throws CompletionException {
        return this.getPagesFromOffsetAsJsonNodesAsync(resourceName, DEFAULT_VERSION, DEFAULT_PAGE_SIZE, offset, numPages);
    }

    /**
     * Gets some number of pages for the given resource, version, and page size, from the given offset.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (JsonNode) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numPages The number of pages of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a page of data
     *         with given page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of pages specified or the max number of pages (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getPagesFromOffsetAsJsonNodesAsync( String resourceName, String version, int pageSize, int offset, int numPages ) throws CompletionException {
        CompletableFuture<List<JsonNode>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<JsonNode> response = null;
            try {
                List<EthosResponse> ethosResponses = getPagesFromOffset( resourceName, version, pageSize, offset, numPages );
                response = ethosResponseConverter.toPageBasedJsonNodeList(ethosResponses);
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
            return response;
        });
        return responseCompletableFuture;
    }

    /**
     * Gets some number of rows for the given resource.  The number of rows is returned as a
     * paged-based list of EthosResponses, altogether containing the number of rows.  Uses the default page size
     * of the response body content length, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getRowsAsync( String resourceName, int numRows ) throws CompletionException {
        return getRowsAsync( resourceName, DEFAULT_VERSION, numRows );
    }

    /**
     * Gets some number of rows for the given resource and version.  The number of rows is returned as a
     * paged-based list of EthosResponses, altogether containing the number of rows.  Uses the default page size of the
     * response body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getRowsAsync( String resourceName, String version, int numRows ) throws CompletionException {
        return getRowsAsync( resourceName, version, DEFAULT_PAGE_SIZE, numRows );
    }

    /**
     * Gets some number of rows for the given resource and page size.  The number of rows is returned as a
     * paged-based list of EthosResponses, altogether containing the number of rows.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getRowsAsync( String resourceName, int pageSize, int numRows ) throws CompletionException {
        return getRowsAsync( resourceName, DEFAULT_VERSION, pageSize, numRows );
    }

    /**
     * Gets some number of rows for the given resource, version, and page size.  The number of rows is returned as a
     * paged-based list of EthosResponses, altogether containing the number of rows.  If numRows is negative, all pages will be returned.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getRowsAsync( String resourceName, String version, int pageSize, int numRows ) throws CompletionException {
        CompletableFuture<List<EthosResponse>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<EthosResponse> response = null;
            try {
                response = getRows(resourceName, version, pageSize, numRows);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
    }

    /**
     * Gets some number of rows for the given resource.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default version of the
     * resource, and the default page size of the response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getRowsAsStringsAsync( String resourceName, int numRows ) throws CompletionException {
        return this.getRowsAsStringsAsync( resourceName, DEFAULT_VERSION, numRows );
    }

    /**
     * Gets some number of rows for the given resource.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getRowsAsStringsAsync( String resourceName, String version, int numRows ) throws CompletionException {
        return this.getRowsAsStringsAsync( resourceName, version, DEFAULT_PAGE_SIZE, numRows );
    }

    /**
     * Gets some number of rows for the given resource and page size.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *                 internally by the SDK during paging operations.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getRowsAsStringsAsync( String resourceName, int pageSize, int numRows ) throws CompletionException {
        return this.getRowsAsStringsAsync(resourceName, DEFAULT_VERSION, pageSize, numRows);
    }

    /**
     * Gets some number of rows for the given resource, version, and page size.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *        internally by the SDK during paging operations.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getRowsAsStringsAsync( String resourceName, String version, int pageSize, int numRows ) throws CompletionException {
        CompletableFuture<List<String>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<String> response = null;
            try {
                List<EthosResponse> ethosResponseList = getRows( resourceName, version, pageSize, numRows );
                response = ethosResponseConverter.toRowBasedStringList( ethosResponseList );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
    }

    /**
     * Gets some number of rows for the given resource.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.   Uses the default version of the
     * resource, and the default page size of the response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getRowsAsJsonNodesAsync( String resourceName, int numRows ) throws CompletionException {
        return getRowsAsJsonNodesAsync(resourceName, DEFAULT_VERSION, numRows);
    }

    /**
     * Gets some number of rows for the given resource and version.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getRowsAsJsonNodesAsync( String resourceName, String version, int numRows ) throws CompletionException {
        return getRowsAsJsonNodesAsync( resourceName, version, DEFAULT_PAGE_SIZE, numRows );
    }

    /**
     * Gets some number of rows for the given resource and page size.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *        internally by the SDK during paging operations.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getRowsAsJsonNodesAsync( String resourceName, int pageSize, int numRows ) throws CompletionException {
        return getRowsAsJsonNodesAsync( resourceName, DEFAULT_VERSION, pageSize, numRows );
    }

    /**
     * Gets some number of rows for the given resource, version, and page size.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *        internally by the SDK during paging operations.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getRowsAsJsonNodesAsync( String resourceName, String version, int pageSize, int numRows ) throws CompletionException {
        CompletableFuture<List<JsonNode>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<JsonNode> response = null;
            try {
                List<EthosResponse> ethosResponseList = getRows( resourceName, version, pageSize, numRows );
                response = ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
    }

    /**
     * Gets some number of rows for the given resource from the given offset.  The number of rows is returned in a list of
     * pages altogether containing the number of rows.  Uses the default page size of the response body content length
     * and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getRowsFromOffsetAsync( String resourceName, int offset, int numRows ) throws CompletionException {
        return getRowsFromOffsetAsync( resourceName, DEFAULT_PAGE_SIZE, offset, numRows );
    }

    /**
     * Gets some number of rows for the given resource and page size from the given offset.  The number of rows is returned in a list of
     * pages altogether containing the number of rows.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page (EthosResponse) of the list returned.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the default version of the resource,
     *         beginning at the given offset index and up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getRowsFromOffsetAsync( String resourceName, int pageSize, int offset, int numRows ) throws CompletionException {
        return getRowsFromOffsetAsync( resourceName, DEFAULT_VERSION, pageSize, offset, numRows );
    }

    /**
     * Gets some number of rows for the given resource and version from the given offset.  The number of rows is returned in a list of
     * pages altogether containing the number of rows.  Uses the default page size of the response body content length.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the response content body length as the page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getRowsFromOffsetAsync( String resourceName, String version, int offset, int numRows ) throws CompletionException {
        return this.getRowsFromOffsetAsync( resourceName, version, DEFAULT_PAGE_SIZE, offset, numRows );
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
     * @return A CompletableFuture wrapping a list of <code>EthosResponse</code>s where each <code>EthosResponse</code> in the list represents a page of data
     *         with the given page size according to the requested version of the resource,
     *         beginning at the given offset index and up to the number of rows specified or the total count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<EthosResponse>> getRowsFromOffsetAsync( String resourceName, String version, int pageSize, int offset, int numRows ) throws CompletionException {
        CompletableFuture<List<EthosResponse>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<EthosResponse> response = null;
            try {
                response = getRowsFromOffset(resourceName, version, pageSize, offset, numRows);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
    }

    /**
     * Gets some number of rows for the given resource from the given offset.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getRowsFromOffsetAsStringsAsync( String resourceName, int offset, int numRows ) throws CompletionException {
        return getRowsFromOffsetAsStringsAsync(resourceName, DEFAULT_PAGE_SIZE, offset, numRows);
    }

    /**
     * Gets some number of rows for the given resource and page size, from the given offset.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *                 internally by the SDK during paging operations.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getRowsFromOffsetAsStringsAsync( String resourceName, int pageSize, int offset, int numRows ) throws CompletionException {
        return getRowsFromOffsetAsStringsAsync(resourceName, DEFAULT_VERSION, pageSize, offset, numRows);
    }

    /**
     * Gets some number of rows for the given resource and version, from the given offset.  The number of rows is returned as a
     * row-based list of Strings, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getRowsFromOffsetAsStringsAsync( String resourceName, String version, int offset, int numRows ) throws CompletionException {
        return this.getRowsFromOffsetAsStringsAsync(resourceName, version, DEFAULT_PAGE_SIZE, offset, numRows);
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
     * @return A CompletableFuture wrapping a list of JSON formatted <code>String</code>s where each <code>String</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<String>> getRowsFromOffsetAsStringsAsync( String resourceName, String version, int pageSize, int offset, int numRows ) throws CompletionException {
        CompletableFuture<List<String>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<String> response = null;
            try {
                List<EthosResponse> ethosResponseList = getRowsFromOffset( resourceName, version, pageSize, offset, numRows );
                response = ethosResponseConverter.toRowBasedStringList( ethosResponseList );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
    }

    /**
     * Gets some number of rows for the given resource from the given offset.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations, and the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getRowsFromOffsetAsJsonNodesAsync( String resourceName, int offset, int numRows ) throws CompletionException {
        return this.getRowsFromOffsetAsJsonNodesAsync(resourceName, DEFAULT_PAGE_SIZE, offset, numRows);
    }

    /**
     * Gets some number of rows for the given resource and page size, from the given offset.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.  Uses the default version of the resource.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param pageSize The number of rows to include in each page during execution of paging operations.  This value is used
     *        internally by the SDK during paging operations.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getRowsFromOffsetAsJsonNodesAsync( String resourceName, int pageSize, int offset, int numRows ) throws CompletionException {
        return this.getRowsFromOffsetAsJsonNodesAsync(resourceName, DEFAULT_VERSION, pageSize, offset, numRows);
    }

    /**
     * Gets some number of rows for the given resource and version, from the given offset.  The number of rows is returned as a
     * row-based list of JsonNodes, the size of which is the number of rows requested.  Uses the default page size of the
     * response body content length during internal paging operations.
     * @param resourceName The name of the Ethos resource to get data for.
     * @param version The desired resource version header to use, as provided in the HTTP Accept Header of the request.
     * @param offset The 0 based index from which to begin paging for the given resource.
     * @param numRows The number of rows of the given resource to return.
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getRowsFromOffsetAsJsonNodesAsync( String resourceName, String version, int offset, int numRows ) throws CompletionException {
        return getRowsFromOffsetAsJsonNodesAsync(resourceName, version, DEFAULT_PAGE_SIZE, offset, numRows);
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
     * @return A CompletableFuture wrapping a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list represents a row (or individual
     *         instance) of a resource, where the length of the list returned is the number of rows requested or the total
     *         count of the resource (whichever is less).
     * @throws CompletionException Propagates a wrapped IOException if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public CompletableFuture<List<JsonNode>> getRowsFromOffsetAsJsonNodesAsync( String resourceName, String version, int pageSize, int offset, int numRows ) throws CompletionException {
        CompletableFuture<List<JsonNode>> responseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<JsonNode> response = null;
            try {
                List<EthosResponse> ethosResponseList = getRowsFromOffset( resourceName, version, pageSize, numRows );
                response = ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return response;
        });
        return responseCompletableFuture;
    }
}
