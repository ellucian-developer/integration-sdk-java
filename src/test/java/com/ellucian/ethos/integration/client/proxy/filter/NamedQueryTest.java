/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

public class NamedQueryTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Test
    public void namedQueryTest() {
        String expectedResult = "?keywordSearch={\"keywordSearch\":\"someKeyword\"}";
        NamedQuery namedQuery = new NamedQuery( "keywordSearch", "keywordSearch", "someKeyword" );
        String result = namedQuery.toString();
        assert( result != null );
        assert( namedQuery.toString().equals(expectedResult) );
    }
}