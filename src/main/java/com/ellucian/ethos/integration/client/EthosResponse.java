/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import org.apache.http.Header;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Response object used by the Ethos SDK to contain specific response headers, response body content, and
 * the HTTP response status code.  This class contains mostly getter methods on attributes to reduce the
 * possibility of response values being changed.
 */
public class EthosResponse<T> {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /**
     * HashMap with String keys of the header constants listed in this class, and Header values from the Http response.
     */
    private Map<String,Header> headerMap;

    /**
     * The Http status code of the Http response.
     */
    private int httpStatusCode;

    /**
     * The response body content.
     */
    private String content;

    /**
     * The response body content generic type object.  Used if generic types are specified with this class.
     */
    private T typedContent;

    /**
     * The URL that the corresponding request was made for.
     */
    private String requestedUrl;

    /**
     * Instantiates an EthosResponse object with the given parameters.
     * The parameters are intended to be supplied from the <code>EthosResponseBuilder</code> to build this object
     * from an <code>org.apache.http.HttpResponse</code>.
     * @param headerMap A map of headers containing the defined header constants in this class as keys.
     * @param content The response body content.
     * @param statusCode The Http status code of the response.
     */
    public EthosResponse(Map<String, Header> headerMap, String content, int statusCode ) {
        this.headerMap = headerMap;
        this.httpStatusCode = statusCode;
        this.content = content;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Returns the header for the given key.
     * @param headerKey the key used to get the header from the headerMap.
     * @return An <code>org.apache.http.Header</code> object for the given key.
     */
    public Header getHeader(String headerKey ) {
        if( headerKey == null || headerKey.trim().isEmpty() ) {
            return null;
        }
        return this.headerMap.get( headerKey );
    }

    /**
     * Gets the keys in the header map, which can be used to retrieve specific header values from {@link #getHeader(String) getHeader(String)}.
     * @return A list of header map keys.
     */
    public List<String> getHeaderMapKeys() {
        Set<String> keySet = headerMap.keySet();
        List<String> keyList = new ArrayList<>();
        keyList.addAll( keySet );
        return keyList;
    }

    /**
     * Gets the Http status code of the response.
     * @return The Http status code.
     */
    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }

    /**
     * Gets the response body as a String value.
     * @return The response body as a String.
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Gets the response body as a <code>com.fasterxml.jackson.databind.JsonNode</code>.
     * @return The response body as a JsonNode
     * @throws IOException Thrown if the string could not be read into a JsonNode.
     */
    public JsonNode getContentAsJson() throws IOException {
        return JsonLoader.fromString( this.content );
    }

    /**
     * Gets the content as a generic type, if specified with this class.
     * @return The response body content as a generic type.
     */
    public T getContentAsType() {
        return typedContent;
    }

    /**
     * Gets the URL which the corresponding request was made for.
     * @return the requested URL.
     */
    public String getRequestedUrl() {
        return requestedUrl;
    }

    /**
     * Sets the requested URL of the corresponding request.
     * @param requestedUrl the requestedUrl to set.
     */
    public void setRequestedUrl(String requestedUrl) {
        this.requestedUrl = requestedUrl;
    }

    /**
     * Sets the content as a generic type object.
     * @param contentAsType The generic type object to set for content.
     */
    public void setContentAsType( T contentAsType ) {
        if( contentAsType != null ) {
            this.typedContent = contentAsType;
            this.content = null;  // Null out the content to reduce memory bloat.
        }
    }
}