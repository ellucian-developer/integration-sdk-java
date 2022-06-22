/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

public class SimpleCriteriaArrayTest {

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
    public void simpleCriteriaArrayToStringTest() {
        String expectedResult = "\"myLabel\":[]";
        SimpleCriteriaArray sca = new SimpleCriteriaArray( "myLabel" );
        assert( sca != null );
        assert( sca.toString().equals(expectedResult) );

        expectedResult = "\"myLabel\":[{\"myKey\":\"myValue\"}]";
        sca = new SimpleCriteriaArray( "myLabel", "myKey", "myValue" );
        assert( sca != null );
        assert( sca.toString().equals(expectedResult) );

        expectedResult = "\"myLabel\":[{\"myKey\":\"myValue\"}]";
        SimpleCriteria sc = new SimpleCriteria( "myKey", "myValue" );
        sca = new SimpleCriteriaArray( "myLabel", sc );
        assert( sca != null );
        assert( sca.toString().equals(expectedResult) );

        expectedResult = "\"myLabel\":[{\"myKey\":\"myValue\"},{\"someLabel\":{\"someKey\":\"someValue\"}}]";
        sca = new SimpleCriteriaArray( "myLabel", "myKey", "myValue" ).addSimpleCriteriaObject( "someLabel", "someKey", "someValue");
        assert( sca != null );
        assert( sca.toString().equals(expectedResult) );
    }
}