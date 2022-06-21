/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

/**
 * This class generates the named query structure used when making named query proxy filter requests.
 * <p>
 * This class is not intended to be used directly by client application code using the SDK.
 * <p>
 * See {@link NamedQuery#buildNamedQueryFilter()} for how to use this class.
 * @since 0.3.0
 * @author David Kumar
 */
public class NamedQueryFilter {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** A SimpleCriteria reference used to hold the named query criteria. */
    protected NamedQuery namedQuery;

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Builds an instance of this NamedQueryFilter class used when making named query proxy filter requests.
     * See {@link NamedQuery#buildNamedQueryFilter()} for how to use this class.
     * @param namedQuery Should be one of the named query objects extending SimpleCriteria containing the criteria for
     *                   this filter.
     */
    protected NamedQueryFilter( NamedQuery namedQuery ) {
        this.namedQuery = namedQuery;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Generates the string for this NamedQueryFilter used when making a named query filter request.
     * @return A string containing the JSON formatted structure of this NamedQueryFilter.
     */
    @Override
    public String toString() {
        if( namedQuery != null ) {
            return namedQuery.toString();
        }
        return "";
    }
}