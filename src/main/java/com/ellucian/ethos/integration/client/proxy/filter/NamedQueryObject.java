/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

/**
 * This class supports the basic structure of a JSON object containing a label for a named query key/value pair used for
 * named query criteria proxy filter requests.
 * <p>
 * This class is not intended to be used directly by client application code using the SDK.
 * <p>
 * See {@link SimpleCriteria.Builder#withNamedQueryObject(String, String, String, String)} for how to use this class.
 * @since 0.3.0
 * @author David Kumar
 */
public class NamedQueryObject extends NamedQuery {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** The name of this named query. */
    protected String queryLabel;

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this class with the given params.
     * <p>
     * See {@link SimpleCriteria.Builder#withNamedQueryObject(String, String, String, String)} for how to build this object.
     * @param queryName The name of this query.
     * @param queryLabel The JSON label for this named query object.
     * @param queryKey The JSON label for the named query value.
     * @param queryValue The value to query by.
     */
    protected NamedQueryObject( String queryName, String queryLabel, String queryKey, String queryValue ) {
        super( queryName, queryKey, queryValue );
        this.queryLabel = queryLabel;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Generates the string for this NamedQueryObject used when making a named query filter request.
     * Example of the syntax this generates:
     * <pre>?instructor={"instructor":{"id":"11111111-1111-1111-1111-111111111111"}}</pre>
     * @return A string containing the JSON formatted structure of this NamedQueryObject.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "?" );
        sb.append(queryName);
        sb.append( "={\"" );
        sb.append( queryLabel );
        sb.append( "\":{\"" );
        sb.append( key );
        sb.append( "\":\"" );
        sb.append( value );
        sb.append( "\"}}" );
        return sb.toString();
    }

}