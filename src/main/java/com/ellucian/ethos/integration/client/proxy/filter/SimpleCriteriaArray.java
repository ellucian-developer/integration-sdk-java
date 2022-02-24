/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


/**
 * A MultiCriteriaObject as an array of SimpleCriteria.  This generates a JSON object containing an array of SimpleCriteria
 * for filter syntax.  The following is an example of filter JSON syntax this object generates:
 * <pre>
 * ?criteria={"names":[{"lastName":"Smith"}]}
 * </pre>
 * See {@link SimpleCriteria.Builder#withSimpleCriteriaArray(String, String, String)} for how to build this object.
 * @since 0.2.0
 * @author David Kumar
 */
public class SimpleCriteriaArray extends MultiCriteriaObject {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given JSON label for the array.
     * @param label The JSON label for the array of SimpleCriteria.
     */
    protected SimpleCriteriaArray( String label ) {
        super( label );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given JSON label for the array.
     * @param label The JSON label for the array of SimpleCriteria.
     * @param criteriaKey The key (label) of the criteriaValue.
     * @param criteriaValue The value to filter by.
     */
    protected SimpleCriteriaArray( String label, String criteriaKey, String criteriaValue ) {
        this( label, new SimpleCriteria(criteriaKey, criteriaValue) );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given JSON label for the array.
     * @param label The JSON label for the array of SimpleCriteria.
     * @param simpleCriteria The SimpleCriteria to add to this array.
     */
    protected SimpleCriteriaArray( String label, SimpleCriteria simpleCriteria ) {
        super( label, simpleCriteria );
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Adds a SimpleCriteriaObject to this array using the given label, criteriaKey, and criteriaValue.
     * @param label The (JSON) label of the SimpleCriteria to add.
     * @param criteriaKey The key (label) of the criteriaValue.
     * @param criteriaValue The value to filter by.
     * @return A SimpleCriteriaArray containing the newly added SimpleCriteriaObject from the given params.
     */
    public SimpleCriteriaArray addSimpleCriteriaObject( String label, String criteriaKey, String criteriaValue ) {
        this.simpleCriteriaList.add( new SimpleCriteriaObject(label, criteriaKey, criteriaValue) );
        return this;
    }

    /**
     * Generates a portion of the JSON filter syntax this object contains.  May not return valid JSON if executed standalone.
     * @return A JSON formatted string of the objects this class contains for filter syntax.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "\"" );
        sb.append(label);
        sb.append( "\":[" );
        for( SimpleCriteria sc : simpleCriteriaList ) {
            sb.append( "{" );
            sb.append( sc.toString() );
            sb.append( "}," );
        }
        // If the last char is a comma, remove it.
        if( sb.charAt(sb.length() - 1) == ',' ) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append( "]" );
        return sb.toString();
    }
}