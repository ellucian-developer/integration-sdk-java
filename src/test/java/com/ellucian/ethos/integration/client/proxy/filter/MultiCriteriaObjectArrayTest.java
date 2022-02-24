/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

public class MultiCriteriaObjectArrayTest {

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
    public void multiCriteriaObjectArrayToStringIsCorrectTest() {
        String expectedResult = "\"myLabel\":[{}]";
        MultiCriteriaObjectArray mcoa = new MultiCriteriaObjectArray("myLabel");
        assert( mcoa.toString() != null );
        assert( mcoa.toString().equals(expectedResult) );

        expectedResult = "\"myLabel\":[{\"firstName\":\"John\"}]";
        mcoa = new MultiCriteriaObjectArray("myLabel", "firstName", "John");
        assert( mcoa.toString() != null );
        assert( mcoa.toString().equals(expectedResult) );

        expectedResult = "\"myLabel\":[\"names\":{\"firstName\":\"John\"}]";
        mcoa = new MultiCriteriaObjectArray("myLabel", "names", "firstName", "John");
        assert( mcoa.toString() != null );
        assert( mcoa.toString().equals(expectedResult) );

        expectedResult = "\"myLabel\":[{\"firstName\":\"John\"}]";
        MultiCriteriaObject mco = new MultiCriteriaObject("firstName", "John" );
        mcoa = new MultiCriteriaObjectArray("myLabel", mco );
        assert( mcoa.toString() != null );
        assert( mcoa.toString().equals(expectedResult) );
    }

    /**
     * The assertions that test whether the expectedResult JSON matches the result DOES NOT test whether
     * the expectedResult is valid JSON.  This is because these criteria objects may not generate complete JSON structures
     * in and of themselves.
     */
    @Test
    public void addMultiCriteriaObjectTest() {
        String expectedResult = "\"myLabel\":[{\"lastName\":\"Smith\"}]";
        MultiCriteriaObjectArray mcoa = new MultiCriteriaObjectArray( "myLabel" );
        mcoa.addMultiCriteriaObject( "lastName", "Smith" );
        assert( mcoa.toString() != null );
        assert( mcoa.toString().equals(expectedResult) );

        expectedResult = "\"myLabel\":[\"innerLabel\":{\"lastName\":\"Smith\"}]";
        mcoa = new MultiCriteriaObjectArray( "myLabel" );
        mcoa.addMultiCriteriaObject( "innerLabel", "lastName", "Smith" );
        assert( mcoa.toString() != null );
        assert( mcoa.toString().equals(expectedResult) );

        expectedResult = "\"myLabel\":[{\"firstName\":\"John\"}]";
        MultiCriteriaObject mco = new MultiCriteriaObject( "firstName", "John" );
        mcoa = new MultiCriteriaObjectArray( "myLabel" );
        mcoa = mcoa.addMultiCriteriaObject( mco );
        assert( mcoa.toString() != null );
        assert( mcoa.toString().equals(expectedResult) );
    }
}