/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

public class SimpleCriteriaObjectArrayTest {

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
    public void simpleCriteriaObjectArrayToStringTest() {
        String expectedResult = "\"myLabel\":[]";
        SimpleCriteriaObjectArray scoa = new SimpleCriteriaObjectArray( "myLabel" );
        assert( scoa != null );
        assert( scoa.toString().equals(expectedResult) );

        expectedResult = "\"outerLabel\":[{\"myLabel\":{\"myKey\":\"myValue\"}}]";
        SimpleCriteriaObject sco = new SimpleCriteriaObject("myLabel", "myKey", "myValue" );
        scoa = new SimpleCriteriaObjectArray( "outerLabel", sco );
        assert( scoa != null );
        assert( scoa.toString().equals(expectedResult) );

        expectedResult = "\"outerLabel\":[{\"myLabel\":{\"myKey\":\"myValue\"}}]";
        scoa = new SimpleCriteriaObjectArray( "outerLabel",  "myLabel", "myKey", "myValue"  );
        assert( scoa != null );
        assert( scoa.toString().equals(expectedResult) );

        expectedResult = "\"outerLabel\":[{\"myLabel\":{\"myKey\":\"myValue\"}},{\"someLabel\":{\"someKey\":\"someValue\"}}]";
        scoa = new SimpleCriteriaObjectArray( "outerLabel",  "myLabel", "myKey", "myValue"  )
               .addSimpleCriteriaObject( "someLabel", "someKey", "someValue" );
        assert( scoa != null );
        assert( scoa.toString().equals(expectedResult) );
    }
}