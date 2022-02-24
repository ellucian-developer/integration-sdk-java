/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

public class NamedQueryObjectTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Test
    public void namedQueryObjectTest() {
        String expectedResult = "?instructor={\"instructor\":{\"id\":\"someId\"}}";
        NamedQueryObject namedQueryObject = new NamedQueryObject( "instructor", "instructor", "id", "someId" );
        String result = namedQueryObject.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }
}