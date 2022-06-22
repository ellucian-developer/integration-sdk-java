/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

public class SimpleCriteriaObjectTest {

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
    public void simpleCriteriaToStringTest() {
        String expectedResult = "\"type\":{\"id\":\"123456\"}";
        SimpleCriteriaObject nsc = new SimpleCriteriaObject( "type", "id", "123456" );
        String result = nsc.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );

        SimpleCriteria sc = new SimpleCriteria( "id", "123456" );
        nsc = new SimpleCriteriaObject( "type", sc );
        result = nsc.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );

        expectedResult = "\"outerLabel\":{\"type\":{\"id\":\"123456\"}}";
        nsc = new SimpleCriteriaObject("type", "id", "123456").nestCriteria("outerLabel");
        result = nsc.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }
}