/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

/**
 * This class supports the basic structure of key/value pairs for named query criteria proxy filter requests.
 * <p>
 * This class is not intended to be used directly by client application code using the SDK.
 * <p>
 * See {@link SimpleCriteria.Builder#withNamedQuery(String, String, String)} for how to use this class.
 * @since 0.3.0
 * @author David Kumar
 */
public class NamedQuery extends SimpleCriteria {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** The name of this named query. */
    protected String queryName;

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this class with the given params.
     * <p>
     * See {@link SimpleCriteria.Builder#withNamedQuery(String, String, String)} for how to build this object.
     * @param queryName The name of this query.
     * @param queryKey The JSON label for the named query value.
     * @param queryValue The value to query by.
     */
    protected NamedQuery( String queryName, String queryKey, String queryValue ) {
        super(queryKey, queryValue );
        this.queryName = queryName;
    }

    /**
     * Builds a NamedQueryFilter using this NamedQuery instance.  This NamedQuery instance could be any of the
     * named query classes containing any number of additional criteria, and should be built from the SimpleCriteria.Builder class.
     * @return A NamedQueryFilter used to generate the appropriate JSON syntax for making a named query proxy filter request.
     */
    public NamedQueryFilter buildNamedQueryFilter() {
        return new NamedQueryFilter( this );
    }

    /**
     * Generates the string for this NamedQuery used when making a named query filter request.
     * Example of the syntax this generates:
     * <pre>?keywordSearch={"keywordSearch":"someValue"}</pre>
     * @return A string containing the JSON formatted structure of this NamedQuery.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "?" );
        sb.append(queryName);
        sb.append( "={" );
        sb.append( super.toString() );
        sb.append( "}" );
        return sb.toString();
    }

}