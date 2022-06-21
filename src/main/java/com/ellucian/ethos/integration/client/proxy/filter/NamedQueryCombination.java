/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

/**
 * This class supports the basic structure of a JSON object containing a combination of a NamedQuery and a NamedQueryObject
 * used for named query criteria proxy filter requests.
 * <p>
 * This class is not intended to be used directly by client application code using the SDK.
 * <p>
 * See {@link SimpleCriteria.Builder#withNamedQueryCombination(String, String, String)} for how to use this class.
 * @since 0.3.0
 * @author David Kumar
 */
public class NamedQueryCombination extends NamedQuery {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /**
     * A reference to a SimpleCriteriaObject for the structure of the NamedQueryObject minus the queryName, since
     * this class is built with a queryName already.
     */
    protected SimpleCriteriaObject simpleCriteriaObject;

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this class with the given params.
     * <p>
     * See {@link SimpleCriteria.Builder#withNamedQueryCombination(String, String, String)} for how to build this object.
     * @param queryName The name of this query.
     * @param queryKey The JSON label for the named query value.
     * @param queryValue The value to query by.
     */
    protected NamedQueryCombination( String queryName, String queryKey, String queryValue ) {
        super( queryName, queryKey, queryValue );
        this.simpleCriteriaObject = null;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Adds a NamedQueryObject to this NamedQueryCombination using the given params.
     * @param queryLabel The JSON label for the NamedQueryObject to add.
     * @param queryKey The JSON label for the named query value.
     * @param queryValue The value to query by.
     * @return This NamedQueryCombination containing a SimpleCriteriaObject built from the given param values to generate
     *         the NamedQueryObject portion of this structure.
     */
    public NamedQueryCombination addNamedQueryObject( String queryLabel, String queryKey, String queryValue ) {
        this.simpleCriteriaObject = new SimpleCriteria.Builder()
                                   .withSimpleCriteriaObject( queryLabel, queryKey, queryValue );
        return this;
    }

    /**
     * Generates the string for this NamedQueryCombination used when making a named query filter request.
     * Example of the syntax this generates:
     * <pre>?advancedSearch={"keyword":"someKeyword","defaultSettings":{"id":"11111111-1111-1111-1111-111111111111"}}</pre>
     * @return A string containing the JSON formatted structure of this NamedQueryCombination.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( super.toString() );
        sb.deleteCharAt( sb.length() - 1 ); // Delete the last char which should be a '}' bracket, and replace it with what this needs.
        if( simpleCriteriaObject != null ) {
            sb.append( "," );
            sb.append( simpleCriteriaObject );
        }
        sb.append( "}" );
        return sb.toString();
    }
}