/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

public class MultiCriteriaObjectTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * The assertions that test whether the expectedResult JSON matches the result DOES NOT test whether
     * the expectedResult is valid JSON.  This is because these criteria objects may not generate complete JSON structures
     * in and of themselves.
     */
    @Test
    public void multiCriteriaObjectToStringIsCorrectTest() {
        String expectedResult = "\"myLabel\":{}";
        MultiCriteriaObject mco = new MultiCriteriaObject( "myLabel" );
        assert( mco.toString() != null );
        assert( mco.toString().equals(expectedResult) );

        expectedResult = "\"lastName\":\"Smith\"";
        mco = new MultiCriteriaObject( "lastName", "Smith" );
        assert( mco.toString() != null );
        assert( mco.toString().equals(expectedResult) );

        expectedResult = "\"myLabel\":{\"lastName\":\"Smith\"}";
        mco = new MultiCriteriaObject("myLabel", "lastName", "Smith" );
        assert( mco.toString() != null );
        assert( mco.toString().equals(expectedResult) );

        expectedResult = "\"myLabel\":{\"year\":1999}";
        mco = new MultiCriteriaObject("myLabel", "year", "1999", true );
        assert( mco.toString() != null );
        assert( mco.toString().equals(expectedResult) );
    }

    @Test
    public void simpleCriteriaCountTest() {
        MultiCriteriaObject mco = new MultiCriteriaObject("myLabel", "lastName", "Smith" )
                                  .addSimpleCriteria( "firstName", "John" );
        assert( mco.toString() != null );
        assert( mco.getSimpleCriteriaCount() == 2 );

        mco = new MultiCriteriaObject("myLabel", "lastName", "Smith" )
                .addSimpleCriteria( "firstName", "John" );
        assert( mco.toString() != null );
        assert( mco.getSimpleCriteriaCount() == 2 );
    }

    /**
     * The assertions that test whether the expectedResult JSON matches the result DOES NOT test whether
     * the expectedResult is valid JSON.  This is because these criteria objects may not generate complete JSON structures
     * in and of themselves.
     */
    @Test
    public void nestSimpleCriteriaTest() {
        String expectedResult = "\"outerLabel\":{\"myLabel\":{\"lastName\":\"Smith\"}}";
        MultiCriteriaObject mco = new MultiCriteriaObject("myLabel", "lastName", "Smith" );
        SimpleCriteriaObject sco = mco.nestInSimpleCriteriaObject( "outerLabel" );
        assert( sco.toString() != null );
        assert( sco.toString().equals(expectedResult) );
    }

}