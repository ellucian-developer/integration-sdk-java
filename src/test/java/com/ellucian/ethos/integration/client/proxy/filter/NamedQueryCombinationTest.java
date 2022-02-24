/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

public class NamedQueryCombinationTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Test
    public void namedQueryCombinationTest() {
        String expectedResult = "?advancedSearch={\"keyword\":\"someValue\"}";
        NamedQueryCombination namedQueryCombination = new NamedQueryCombination( "advancedSearch", "keyword", "someValue" );
        String result = namedQueryCombination.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );

        expectedResult = "?advancedSearch={\"keyword\":\"someValue\",\"defaultSettings\":{\"id\":\"someId\"}}";
        namedQueryCombination = new NamedQueryCombination("advancedSearch", "keyword", "someValue")
                                .addNamedQueryObject("defaultSettings", "id", "someId" );
        result = namedQueryCombination.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }
}