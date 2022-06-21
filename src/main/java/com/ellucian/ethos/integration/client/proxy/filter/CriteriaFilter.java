/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to build CriteriaFilters, for use with Ethos resources using version 8 or later.
 * Follows the builder pattern for easy construction of criteria filters.
 * <p>
 * <b>
 *     This class should not be used directly to build criteria filters, but instead should be obtained by calling
 *     {@link SimpleCriteria#buildCriteriaFilter()} from a SimpleCriteria built from {@link SimpleCriteria.Builder}.
 * </b>
 * @since 0.0.1
 * @author David Kumar
 */
public class CriteriaFilter {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** A list of CriteriaSets contained within this filter. */
    private List<CriteriaSet> criteriaSetList;

    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * No-arg constructor.  This class should be built using the inner Builder class.
     */
    private CriteriaFilter() {
        this.criteriaSetList = new ArrayList<>();
    }

    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * This class should not be used directly by source code using this SDK.  Please see the links in the class description
     * of {@link CriteriaFilter} describing how to obtain a CriteriaFilter.
     * <p>
     * This is an inner-class used for building an instance of a CriteriaFilter, following the builder pattern.
     * This Builder class is used to build CriteriaFilters.
     * The following is a basic example of the constructs this class generates:
     * <pre>
     *                       CriteriaFilter
     *        ______________________/\______________________
     *       /                                              \
     *      /                          CriteriaSet           \
     *     /            ___________________/\_________________\
     *    /            /                                      \\
     *   /            /                 SimpleCriteria         \\
     *  /            /      __________________/\________________\\
     * /            /      /                                    \\\
     * ?criteria={"names":[{"firstName":"John","lastName":"Smith"}]}
     * </pre>
     */
    public static class Builder {

        // ==========================================================================
        // Attributes
        // ==========================================================================

        /** A list of CriteriaSets to put into the CriteriaFilter. */
        private List<CriteriaSet> criteriaSetList;

        /** The active CriteriaSet used when building a CriteriaFilter. */
        private CriteriaSet criteriaSet;

        // ==========================================================================
        // Methods
        // ==========================================================================

        /**
         * No-arg constructor for easy access to this Builder class.
         */
        public Builder() {
            this.criteriaSetList = new ArrayList<>();
            this.criteriaSet = new CriteriaSet();
        }

        /**
         * Specifies the criteriaSetName, key, and value used when building a CriteriaFilter.
         * This is a convenience method equivalent to: <pre>withCriteriaSet( new SimpleCriteria(criteriaKey, criteriaValue) );</pre>
         * @param criteriaKey The key for the SimpleCriteria.
         * @param criteriaValue The value of the SimpleCriteria.
         * @throws IllegalArgumentException Thrown if the criteriaKey or criteriaValue are null or blank.
         * @return this Builder object.
         */
        public Builder withCriteriaSet( String criteriaKey, String criteriaValue ) {
            if( criteriaKey == null || criteriaKey.isBlank() ) {
                throw new IllegalArgumentException( "Error: Cannot build criteria filter due to a null or blank criteriaKey." );
            }
            if( criteriaValue == null || criteriaValue.isBlank() ) {
                throw new IllegalArgumentException( "Error: Cannot build criteria filter due to a null criteriaValue." );
            }
            return withCriteriaSet( new SimpleCriteria(criteriaKey, criteriaValue) );
        }

        /**
         * Specifies the criteriaSetName used with the given SimpleCriteria when building a CriteriaFilter.
         * @param simpleCriteria A SimpleCriteria associated with current CriteriaSet.
         * @throws IllegalArgumentException Thrown if the simpleCriteria is null.
         * @return this Builder object.
         */
        public Builder withCriteriaSet( SimpleCriteria simpleCriteria ) {
            if( simpleCriteria == null ) {
                throw new IllegalArgumentException( "Error: Cannot build criteria filter due to a null simpleCriteria." );
            }
            criteriaSet.addSimpleCriteria( simpleCriteria );
            return this;
        }

        /**
         * Adds the criteriaKey and criteriaValue as a SimpleCriteria to the current criteriaSet.
         * This is a convenience method identical to: <pre>withSimpleCriteria( new SimpleCriteria(criteriaKey, criteriaValue) );</pre>
         * @param criteriaKey The key for the SimpleCriteria.
         * @param criteriaValue The value of the SimpleCriteria.
         * @return this Builder object.
         */
        public Builder withSimpleCriteria(String criteriaKey, String criteriaValue ) {
            return withSimpleCriteria( new SimpleCriteria(criteriaKey, criteriaValue) );
        }

        /**
         * Adds the simpleCriteria to the current criteriaSet.
         * @param simpleCriteria The SimpleCriteria to add.
         * @return this Builder object.
         */
        public Builder withSimpleCriteria( SimpleCriteria simpleCriteria ) {
            return withCriteriaSet( simpleCriteria );
        }


        /**
         * Establishes a new CriteriaSet within this filter.  The current criteriaSet is added to the criteriaSetList,
         * and then re-instantiated into a new criteriaSet so that it can contain different criteria.
         * @return this Builder object.
         */
        public Builder newCriteriaSet() {
            if( criteriaSet.getCriteriaList().isEmpty() == false ) {
                criteriaSetList.add( criteriaSet );
            }
            criteriaSet = new CriteriaSet();
            return this;
        }

        /**
         * Builds an instance of this CriteriaFilter class, setting the criteriaSetList from this Builder in the CriteriaFilter.
         * @return A CriteriaFilter containing a list of CriteriaSets.
         */
        public CriteriaFilter build() {
            CriteriaFilter criteriaFilter = new CriteriaFilter();
            newCriteriaSet();
            criteriaFilter.setCriteriaSetList( criteriaSetList );
            criteriaSetList = new ArrayList();
            return criteriaFilter;
        }

    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * Sets the criteriaSetList.
     * @param criteriaSetList The list of CriteriaSets to set in this filter.
     */
    private void setCriteriaSetList(List<CriteriaSet> criteriaSetList) {
        this.criteriaSetList = criteriaSetList;
    }

    /**
     * Provides a string representation of this CriteriaFilter.  This method should be called to produce the entire
     * CriteriaFilter string used when making a request containing a criteria filter.
     * @return A JSON formatted string containing the proper syntax of a CriteriaFilter.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "?criteria={" );
        for( CriteriaSet cs : criteriaSetList ) {
            sb.append( cs.toString() );
            sb.append( "," );
        }
        // If the last char is a comma, remove it.
        if( sb.charAt(sb.length() - 1) == ',' ) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append( "}" );
        return sb.toString();
    }
}