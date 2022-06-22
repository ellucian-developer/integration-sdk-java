/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import java.util.ArrayList;
import java.util.List;

/**
 * <b>This class is intended to be used internally by the SDK.</b>
 * <p>
 * NOTE: This class is not intended to be used directly, but is used by the CriteriaFilter.Builder when constructing a
 * CriteriaFilter.  CriteriaFilters should be built using the CriteriaFilter.Builder.
 * <p>
 * A set of criteria containing a list of SimpleCriteria used for building a CriteriaFilter.
 */
public class CriteriaSet {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /**
     * The list of SimpleCriteria, as there could be more than one.
     */
    protected List<SimpleCriteria> criteriaList;

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * No-arg constructor for this class.
     */
    protected CriteriaSet() {
        this.criteriaList = new ArrayList<>();
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Gets the list of SimpleCriteria.
     * @return The criteriaList of SimpleCriteria from this CriteriaSet.
     */
    public List<SimpleCriteria> getCriteriaList() {
        return criteriaList;
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Adds a SimpleCriteria to the criteriaList.
     * @param simpleCriteria A SimpleCriteria to add to the criteriaList of this CriteriaSet.
     */
    protected void addSimpleCriteria( SimpleCriteria simpleCriteria ) {
        criteriaList.add( simpleCriteria );
    }

    /**
     * A string representation in JSON format of this CriteriaSet, used when building the request URL for criteria filters.
     * @return A string representing this CriteriaSet in JSON format.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for( SimpleCriteria simpleCriteria : criteriaList ) {
            sb.append( simpleCriteria.toString() );
            sb.append( "," );
        }
        // Remove the last comma
        sb.deleteCharAt( sb.length() - 1 );
        return sb.toString();
    }

}