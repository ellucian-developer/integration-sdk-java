/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

/**
 * A SimpleCriteriaObjectArray containing multiple SimpleCriteriaObjects.  This generates a JSON object containing
 * an array of SimpleCriteriaObjects for filter syntax.  The following is an example of filter JSON syntax this object generates:
 * <pre>
 * ?criteria={"authors":[{"person":{"id":"11111111-1111-1111-1111-111111111111"}}]}
 * </pre>
 * See {@link SimpleCriteria.Builder#withSimpleCriteriaObjectArray(String)} for how to build this object.
 * @since 0.2.0
 * @author David Kumar
 */
public class SimpleCriteriaObjectArray extends MultiCriteriaObject {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given JSON label for the array.
     * @param label The JSON label for the array of SimpleCriteriaObjects.
     */
    protected SimpleCriteriaObjectArray( String label ) {
        super( label );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given JSON label adding the given simpleCriteriaObject to the array.
     * @param label The JSON label for the array of SimpleCriteriaObjects.
     * @param simpleCriteriaObject A SimpleCriteriaObject to add to this array.
     */
    protected SimpleCriteriaObjectArray( String label, SimpleCriteriaObject simpleCriteriaObject ) {
        super( label, simpleCriteriaObject );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given JSON label and criteria for this array.  Constructs a SimpleCriteriaObject
     * from the given simpleCriteriaObjectLabel, criteriaKey, and criteriaValue and adds it to this array.
     * @param label The JSON label for the array of SimpleCriteria.
     * @param simpleCriteriaObjectLabel The label for the SimpleCriteriaObject to add to this array.
     * @param criteriaKey The key (JSON label) for the criteria of the added SimpleCriteriaObject.
     * @param criteriaValue The value to filter by of the added SimpleCriteriaObject.
     */
    protected SimpleCriteriaObjectArray( String label, String simpleCriteriaObjectLabel, String criteriaKey, String criteriaValue ) {
        this( label, new SimpleCriteriaObject(simpleCriteriaObjectLabel, criteriaKey, criteriaValue) );
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Adds a SimpleCriteriaObject to this array from the given params.
     * @param label The label for the SimpleCriteriaObject to add to this array.
     * @param criteriaKey The key (JSON label) for the criteria of the added SimpleCriteriaObject.
     * @param criteriaValue The value to filter by of the added SimpleCriteriaObject.
     * @return This SimpleCriteriaObjectArray containing the newly added SimpleCriteriaObject.
     */
    public SimpleCriteriaObjectArray addSimpleCriteriaObject( String label, String criteriaKey, String criteriaValue ) {
        addSimpleCriteria( new SimpleCriteriaObject(label, criteriaKey, criteriaValue) );
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
            sb.append( "}" );
            sb.append( "," );
        }
        // If the last char is a comma, remove it.
        if( sb.charAt(sb.length() - 1) == ',' ) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append( "]" );
        return sb.toString();
    }

}