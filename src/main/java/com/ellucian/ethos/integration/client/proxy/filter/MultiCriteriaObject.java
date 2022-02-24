/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * A SimpleCriteria object that can contain more than one criteria.  This generates a JSON object containing multiple
 * criteria for filter syntax.  The following is an example of filter JSON syntax this object generates:
 * <pre>
 * ?criteria={"startOn":{"year":2021,"month":08}}
 * </pre>
 * See {@link SimpleCriteria.Builder#withMultiCriteriaObject(String, String, String, boolean)} for how to build this object.
 * @since 0.2.0
 * @author David Kumar
 */
public class MultiCriteriaObject extends SimpleCriteria {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** The JSON label for the this object. */
    protected String label;

    /** A list of SimpleCriteria this object contains. */
    protected List<SimpleCriteria> simpleCriteriaList;

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given label.
     * @param label The JSON label to use for the filter syntax this object generates.
     */
    protected MultiCriteriaObject( String label ) {
        this.label = label;
        this.simpleCriteriaList = new ArrayList<>();
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given criteriaKey and criteriaValue.
     * @param criteriaKey The key (label) of the given criteriaValue.
     * @param criteriaValue The value to filter by.
     */
    protected MultiCriteriaObject( String criteriaKey, String criteriaValue ) {
        this( null, new SimpleCriteria(criteriaKey, criteriaValue) );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given label, criteriaKey and criteriaValue.
     * @param label The JSON label for this object.
     * @param criteriaKey The key (label) of the given criteriaValue.
     * @param criteriaValue The value to filter by.
     */
    protected MultiCriteriaObject( String label, String criteriaKey, String criteriaValue ) {
        this( label, new SimpleCriteria(criteriaKey, criteriaValue) );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given criteriaKey and criteriaValue.  Adds the given criteriaKey and criteriaValue
     * as a SimpleCriteria to this object.
     * @param label The JSON label for this object.
     * @param criteriaKey The key (label) of the given criteriaValue.
     * @param criteriaValue The value to filter by.
     * @param valueIsNumeric Indicates whether the criteriaValue is numeric (true) or not (false).  If true, the generate
     *                       JSON filter syntax will not have quotes around the value.
     */
    protected MultiCriteriaObject( String label, String criteriaKey, String criteriaValue, boolean valueIsNumeric ) {
        this( label, new SimpleCriteria(criteriaKey, criteriaValue, valueIsNumeric) );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates this object with the given label and simpleCriteria.
     * @param label The JSON label for this object.
     * @param simpleCriteria The SimpleCriteria this object will contain.
     */
    protected MultiCriteriaObject( String label, SimpleCriteria simpleCriteria ) {
        this( label );
        this.simpleCriteriaList.add( simpleCriteria );
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Adds a SimpleCriteria to this object.
     * @param simpleCriteria The SimpleCriteria to add to this object.
     * @return This object containing the newly added simpleCriteria.
     */
    public MultiCriteriaObject addSimpleCriteria( SimpleCriteria simpleCriteria ) {
        simpleCriteriaList.add( simpleCriteria );
        return this;
    }

    /**
     * Convenience method to add a SimpleCriteria to this object.  This is equivalent to:
     * <pre>addSimpleCriteria( new SimpleCriteria(label, value) );</pre>
     * @param label The JSON label for the given value to add as a SimpleCriteria.
     * @param value The value to filter by.
     * @return This object with the given label and value added as a SimpleCriteria.
     */
    public MultiCriteriaObject addSimpleCriteria( String label, String value ) {
        addSimpleCriteria( new SimpleCriteria(label, value) );
        return this;
    }

    /**
     * Convenience method to add the given label and value as a numeric value to this object.  Numeric values will not
     * contain quotes around them in the generated JSON filter syntax.  This is equivalent to:
     * <pre>addSimpleCriteria( new SimpleCriteria(label, value, true) );</pre>
     * @param label The JSON label for the given value to add as a SimpleCriteria.
     * @param value The value to filter by, generated as a numeric value.
     * @return This object with the given label and value added as a SimpleCriteria.
     */
    public MultiCriteriaObject addNumericSimpleCriteria( String label, String value ) {
        addSimpleCriteria( new SimpleCriteria(label, value, true) );
        return this;
    }

    /**
     * Nests this object within a new SimpleCriteriaObject with the given label.
     * @param label The JSON label of the new SimpleCriteriaObject with this as it's value.
     * @return A new SimpleCriteriaObject with this object nested within it by the given label.
     */
    public SimpleCriteriaObject nestInSimpleCriteriaObject( String label ) {
        SimpleCriteriaObject simpleCriteriaObject = new SimpleCriteriaObject( label, this );
        return simpleCriteriaObject;
    }

    /**
     * Gets the count of SimpleCriteria objects this MultiCriteriaObject contains.
     * @return The number of SimpleCriteria objects this object contains.
     */
    public int getSimpleCriteriaCount() {
        return simpleCriteriaList.size();
    }

    /**
     * Generates a portion of the JSON filter syntax this object contains.  May not return valid JSON if executed standalone.
     * @return A JSON formatted string of the objects this class contains for filter syntax.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if( label != null && label.isBlank() == false ) {
            sb.append("\"");
            sb.append(label);
            sb.append("\":{");
        }

        for( SimpleCriteria sc : simpleCriteriaList ) {
            sb.append( sc.toString() );
            sb.append( "," );
        }

        // If the last char is a comma, remove it.
        if( sb.charAt(sb.length() - 1) == ',' ) {
            sb.deleteCharAt(sb.length() - 1);
        }

        // Only append the '}' if there is a label.
        if( label != null && label.isBlank() == false ) {
            sb.append( "}" );
        }
        return sb.toString();

    }

}