/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class supports the basic structure of a JSON object containing a combination of a NamedQueryObject and an array of NamedQueryObjects
 * used for named query criteria proxy filter requests.
 * <p>
 * This class is not intended to be used directly by client application code using the SDK.
 * <p>
 * See {@link SimpleCriteria.Builder#withNamedQueryObjectArrayCombination(String, String, String, String)} for how to use this class.
 * @since 0.4.0
 * @author David Kumar
 */
public class NamedQueryObjectArrayCombination extends NamedQueryObject {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /** The JSON label for the array of NamedQueryObjects */
    protected String arrayLabel;

    /** The list of SimpleCriteriaObjects used to generate the syntax for the array of NamedQueryObjects. */
    protected List<SimpleCriteriaObject> simpleCriteriaObjectList;

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this class with the given params.
     * <p>
     * @param queryName The name of this query.
     * @param queryLabel The JSON label of the initial NamedQueryObject outside of the array.
     * @param queryKey The JSON label for the named query value in the NamedQueryObject outside of the array.
     * @param queryValue The value in the NamedQueryObject outside of the array to query by.
     */
    protected NamedQueryObjectArrayCombination( String queryName, String queryLabel, String queryKey, String queryValue ) {
        super( queryName, queryLabel, queryKey, queryValue );
        this.arrayLabel = null;
        this.simpleCriteriaObjectList = new ArrayList<>();
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Sets the JSON label for the NamedQueryObject array.
     * @param label the JSON label to use for the array of NamedQueryObjects.
     * @return this object.
     */
    public NamedQueryObjectArrayCombination withArrayLabel( String label ) {
        this.arrayLabel = label;
        return this;
    }

    /**
     * Adds the values as a SimpleCriteriaObject to a list used within this class to generate the JSON structure as an
     * array of NamedQueryObjects.
     * @param queryLabel The JSON label for the NamedQueryObject to add to the array.
     * @param queryKey The JSON label for the value in the NamedQueryObject to add to the array.
     * @param queryValue The JSON value in the NamedQueryObject added to the array to query by.
     * @return this object containing the given values added as a SimpleCriteriaObject to the internal list for array generation.
     */
    public NamedQueryObjectArrayCombination addToNamedQueryObjectArray( String queryLabel, String queryKey, String queryValue ) {
        SimpleCriteriaObject simpleCriteriaObject = new SimpleCriteria.Builder()
                                                        .withSimpleCriteriaObject( queryLabel, queryKey, queryValue );
        simpleCriteriaObjectList.add( simpleCriteriaObject );
        return this;
    }

    /**
     * Generates the string for this NamedQueryObjectArrayCombination used when making a named query filter request.
     * Example of the syntax this generates:
     * <pre>?registrationStatusesByAcademicPeriod={"academicPeriod":{"id":"11111111-1111-1111-1111-111111111111"},"statuses":[{"detail":{"id":"22222222-2222-2222-2222-222222222222"}}]}</pre>
     * @return A string containing the JSON formatted structure of this NamedQueryObjectArrayCombination.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( super.toString() );
        sb.deleteCharAt( sb.length() - 1 ); // Delete the last char which should be a '}' bracket, and replace it with what this needs.
        if( arrayLabel != null  &&  arrayLabel.isBlank() == false ) {
            sb.append( ",\"" );
            sb.append( arrayLabel );
            sb.append( "\":[" );
            for( SimpleCriteriaObject sco : simpleCriteriaObjectList ) {
                sb.append( "{" );
                sb.append( sco.toString() );
                sb.append( "}," );
            }
            if( sb.charAt(sb.length() - 1) == ',' ) {
                sb.deleteCharAt(sb.length() - 1); // Delete the last char which should be a ',' comma, and replace it with what this needs.
            }
            sb.append( "]" );
        }
        sb.append( "}" );
        return sb.toString();
    }
}