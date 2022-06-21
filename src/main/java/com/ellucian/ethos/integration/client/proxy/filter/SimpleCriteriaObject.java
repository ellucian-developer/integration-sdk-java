/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

/**
 * A SimpleCriteriaObject containing a label for the given SimpleCriteria it contains.  This generates a JSON object containing
 * a label, criteriaKey, and criteriaValue for filter syntax.  The following is an example of filter JSON syntax this object generates:
 * <pre>
 * ?criteria={"names":{"lastName":"Smith"}}
 * </pre>
 * See {@link SimpleCriteria.Builder#withSimpleCriteriaObject(String, String, String)} for how to build this object.
 * @since 0.2.0
 * @author David Kumar
 */
public class SimpleCriteriaObject extends SimpleCriteria {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** The label associated with a key/value criteria pair. */
    protected String label;

    /** A reference to a SimpleCriteria which can be nested within this object. */
    protected SimpleCriteria nestedSimpleCriteria;

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates an instance of this class with the given params.
     * @param label The (JSON) label for this SimpleCriteria object containing the key/value criteria pair.
     * @param criteriaKey The key (JSON label) for the criteria.
     * @param criteriaValue The value to filter by.
     */
    protected SimpleCriteriaObject(String label, String criteriaKey, String criteriaValue ) {
        super( criteriaKey, criteriaValue );
        this.label = label;
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates an instance of this class with the given params.
     * @param label The (JSON) label for this SimpleCriteria object.
     * @param nestedSimpleCriteria A reference to a SimpleCriteria which will be nested within this object by the given label.
     */
    protected SimpleCriteriaObject(String label, SimpleCriteria nestedSimpleCriteria ) {
        this.label = label;
        this.nestedSimpleCriteria = nestedSimpleCriteria;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Nests this SimpleCriteriaObject within a new one using the given label.
     * @param label The label of this nested SimpleCriteriaObject.
     * @return A new SimpleCriteriaObject containing the given label nesting this object.
     */
    public SimpleCriteriaObject nestCriteria( String label ) {
        return new SimpleCriteriaObject( label, this );
    }

    /**
     * Gets the name of this SimpleCriteria.
     * @return The name (or property) associated with they key/value criteria pair.
     */
    public String getLabel() {
        return label;
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
        sb.append( "\":{" );
        if( nestedSimpleCriteria == null ) {
            sb.append( super.toString() );
        }
        else {
            sb.append( nestedSimpleCriteria.toString() );
        }
        sb.append( "}" );
        return sb.toString();
    }
}