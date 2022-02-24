/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * A SimpleCriteriaValueArray containing multiple values.  This generates a JSON object containing
 * an array of string values for filter syntax.  The following is an example of filter JSON syntax this object generates:
 * <pre>
 * ?criteria={"credentials":["bannerId","colleagueId","someOtherId"]}
 * </pre>
 * See {@link SimpleCriteria.Builder#withSimpleCriteriaValueArray(String, String)} for how to build this object.
 * @since 0.2.0
 * @author David Kumar
 */
public class SimpleCriteriaValueArray extends SimpleCriteria {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /** A list of String values in this array. */
    protected List<String> valueList;

    // ==========================================================================
    // Methods
    // ==========================================================================
    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates an instance of this class with the given params.
     * @param criteriaKey The key (JSON label) for the criteria value array.
     * @param criteriaValue A value to filter by, added to the valueList for this array.
     */
    protected SimpleCriteriaValueArray( String criteriaKey, String criteriaValue ) {
        this.valueList = new ArrayList<>();
        this.key = criteriaKey;
        this.valueList.add( criteriaValue );
    }

    /**
     * Adds a value to this array.
     * @param value The String value to add.
     * @return This SimpleCriteriaValueArray with the newly added value.
     */
    public SimpleCriteriaValueArray addValue( String value ) {
        if( value == null || value.isBlank() ) {
            throw new IllegalArgumentException( "ERROR: Cannot add a value to a SimpleCriteriaValueArray due to a null or blank value." );
        }
        valueList.add( value );
        return this;
    }

    /**
     * Gets the number of values in the valueList for this array.
     * @return The number of values listed for this array.
     */
    public int getValueCount() {
        return valueList.size();
    }

    /**
     * Generates a portion of the JSON filter syntax this object contains.  May not return valid JSON if executed standalone.
     * @return A JSON formatted string of the objects this class contains for filter syntax.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "\"" );
        sb.append( key );
        sb.append( "\":[" );
        for( String value : valueList ) {
            sb.append( "\"" );
            sb.append( value );
            sb.append( "\",");
        }
        // If the last char is a comma, remove it.
        if( sb.charAt(sb.length() - 1) == ',' ) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append( "]" );
        return sb.toString();
    }
}