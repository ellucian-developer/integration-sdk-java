/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

/**
 * An array of MultiCriteriaObjects. This generates a JSON object containing an array of MultiCriteriaObjects.
 * The following is an example of filter JSON syntax this object generates:
 * <pre>
 * ?criteria={"credentials":[{"type":"bannerId","value":"myBannerId"}]}
 * </pre>
 * See {@link SimpleCriteria.Builder#withMultiCriteriaObjectArray(String)} for how to build this object.
 * @since 0.2.0
 * @author David Kumar
 */
public class MultiCriteriaObjectArray extends MultiCriteriaObject {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** Flag to determine whether to generate squirly braces when going toString() based on the existence of a criteria label. **/
    protected boolean criteriaHasLabel = false;

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given JSON label for the array.
     * @param arrayLabel The JSON label for the array of MultiCriteriaObjects.
     */
    protected MultiCriteriaObjectArray( String arrayLabel ) {
        super( arrayLabel );
        this.criteriaHasLabel = false;
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this class with the given arrayLabel.  The given criteriaKey and criteriaValue are added to this array
     * as a MultiCriteriaObject.
     * @param arrayLabel The JSON label for the array of MultiCriteriaObjects.
     * @param criteriaKey The key (label) of the criteriaValue.
     * @param criteriaValue The value to filter by.
     */
    protected MultiCriteriaObjectArray( String arrayLabel, String criteriaKey, String criteriaValue ) {
        this( arrayLabel, new MultiCriteriaObject( criteriaKey, criteriaValue) );
        this.criteriaHasLabel = false;
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this class with the given arrayLabel.  The given criteriaLabel, criteriaKey, and criteriaValue are added to this array
     * as a MultiCriteriaObject.
     * @param arrayLabel The JSON label for the array of MultiCriteriaObjects.
     * @param criteriaLabel The JSON label for the MultiCriteriaObject added containing the given criteriaKey and criteriaValue.
     * @param criteriaKey The key (label) of the criteriaValue.
     * @param criteriaValue The value to filter by.
     */
    protected MultiCriteriaObjectArray( String arrayLabel, String criteriaLabel, String criteriaKey, String criteriaValue ) {
        this( arrayLabel, new MultiCriteriaObject( criteriaLabel, criteriaKey, criteriaValue ) );
        if( criteriaLabel != null && criteriaLabel.isBlank() == false ) {
            this.criteriaHasLabel = true;
        }
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this class with the given arrayLabel and multiCriteriaObject.  The multiCriteriaObject is added to this array.
     * @param arrayLabel The JSON label for the array of MultiCriteriaObjects.
     * @param multiCriteriaObject A MultiCriteriaObject this array will contain.
     */
    protected MultiCriteriaObjectArray( String arrayLabel, MultiCriteriaObject multiCriteriaObject ) {
        super( arrayLabel, multiCriteriaObject );
        if( multiCriteriaObject.label != null && multiCriteriaObject.label.isBlank() == false ) {
            this.criteriaHasLabel = true;
        }
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Adds a MultiCriteriaObject to this array using the given criteriaKey and criteriaValue.
     * @param criteriaKey The JSON key (label) for the given criteriaValue.
     * @param criteriaValue The value to filter by.
     * @return The MultiCriteriaObject added to this array.
     */
    public MultiCriteriaObject addMultiCriteriaObject( String criteriaKey, String criteriaValue ) {
        MultiCriteriaObject multiCriteriaObject = new MultiCriteriaObject( criteriaKey, criteriaValue );
        criteriaHasLabel = false;
        return addSimpleCriteria( multiCriteriaObject );
    }

    /**
     * Adds a MultiCriteriaObject to this array using the given label, criteriaKey and criteriaValue.
     * @param label The (JSON) label of the MultiCriteriaObject added to this array.
     * @param criteriaKey The JSON key (label) for the given criteriaValue.
     * @param criteriaValue The value to filter by.
     * @return The MultiCriteriaObject added to this array.
     */
    public MultiCriteriaObject addMultiCriteriaObject( String label, String criteriaKey, String criteriaValue ) {
        MultiCriteriaObject multiCriteriaObject = new MultiCriteriaObject( label, criteriaKey, criteriaValue );
        criteriaHasLabel = false;
        if( label != null && label.isBlank() == false ) {
            criteriaHasLabel = true;
        }
        return addSimpleCriteria( multiCriteriaObject );
    }

    /**
     * Adds the given multiCriteriaObject to this array.
     * @param multiCriteriaObject The MultiCriteriaObject to add to this array.
     * @return This MultiCriteriaObjectArray containing the newly added multiCriteriaObject.
     */
    public MultiCriteriaObjectArray addMultiCriteriaObject( MultiCriteriaObject multiCriteriaObject ) {
        criteriaHasLabel = false;
        if( multiCriteriaObject.label != null &&  multiCriteriaObject.label.isBlank() == false ) {
            criteriaHasLabel = true;
        }
        addSimpleCriteria( multiCriteriaObject );
        return this;
    }

    /**
     * Generates a portion of the JSON filter syntax this object contains.  May not return valid JSON if executed standalone.
     * @return A JSON formatted string of the objects this class contains for filter syntax.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(label);
        if (criteriaHasLabel == false) {
            sb.append( "\":[{" );
        }
        else {
            sb.append( "\":[" );
        }

        for( SimpleCriteria sc : simpleCriteriaList ) {
            sb.append( sc.toString() );
            sb.append( "," );
        }
        // If the last char is a comma, remove it.
        if( sb.charAt(sb.length() - 1) == ',' ) {
            sb.deleteCharAt(sb.length() - 1);
        }

        if( criteriaHasLabel == false ) {
            sb.append( "}]" );
        }
        else {
            sb.append( "]" );
        }
        return sb.toString();
    }

}