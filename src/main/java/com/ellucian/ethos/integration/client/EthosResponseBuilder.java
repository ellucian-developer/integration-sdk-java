/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Intended to be used to more easily build an EthosResponse object from the given <code>org.apache.http.HttpResponse</code>.
 */
public class EthosResponseBuilder {

    /**
     * No-arg constructor for instantiating this class.  Builds the headerList.
     */
    public EthosResponseBuilder() {}


    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Builds an <code>EthosResponse</code> object from the given <code>org.apache.http.HttpResponse</code>.
     * The headerList is built taking header values from the given <code>org.apache.http.HttpResponse</code> for the
     * headers listed in <code>EthosResponse</code>.  Also copies the response body content and Http status code.
     * @param httpResponse The <code>org.apache.http.HttpResponse</code> to build an <code>EthosResponse</code> from.
     * @return An <code>EthosResponse</code> containing the headers values from the given <code>org.apache.http.HttpResponse</code>
     *         for the headers defined in <code>EthosResponse</code>, the response body content, and the response Http status code,
     *         or null if the given <code>org.apache.http.HttpResponse</code> is null.
     * @throws IOException Thrown if the response body (entity) cannot be converted to a String value.
     */
    public EthosResponse buildEthosResponse( HttpResponse httpResponse ) throws IOException {
        if( httpResponse == null ) {
            return null;
        }
        HashMap<String, Header> headersMap = new HashMap<>();
        for( Header header : httpResponse.getAllHeaders() ){
            headersMap.put(header.getName(), header);
        }
        HttpEntity entity = httpResponse.getEntity();
        String content = entity != null ? EntityUtils.toString( entity ) : "";
        // if we cannot get the status code, set it to 500 (Internal Server Error)
        int status = httpResponse.getStatusLine() != null ? httpResponse.getStatusLine().getStatusCode() : 500;
        EthosResponse ethosResponse = new EthosResponse( headersMap, content, status );
        return ethosResponse;
    }

}