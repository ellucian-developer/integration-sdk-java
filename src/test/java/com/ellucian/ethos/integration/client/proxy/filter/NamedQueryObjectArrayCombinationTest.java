/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

public class NamedQueryObjectArrayCombinationTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Test
    public void namedQueryObjectArrayCombinationTest() {
        String expectedResult = "?someQueryName={\"someQueryLabel\":{\"someQueryKey\":\"someQueryValue\"}}";
        NamedQueryObjectArrayCombination nqoac = new NamedQueryObjectArrayCombination("someQueryName", "someQueryLabel", "someQueryKey", "someQueryValue");
        assert( nqoac.toString().equals(expectedResult) );

        expectedResult = "?someQueryName={\"someQueryLabel\":{\"someQueryKey\":\"someQueryValue\"},\"someArrayLabel\":[]}";
        nqoac = new NamedQueryObjectArrayCombination("someQueryName", "someQueryLabel", "someQueryKey", "someQueryValue")
                .withArrayLabel("someArrayLabel");
        assert( nqoac.toString().equals(expectedResult) );

        expectedResult = "?someQueryName={\"someQueryLabel\":{\"someQueryKey\":\"someQueryValue\"},\"someArrayLabel\":[{\"anotherQueryLabel\":{\"anotherQueryKey\":\"anotherQueryValue\"}}]}";
        nqoac = new NamedQueryObjectArrayCombination("someQueryName", "someQueryLabel", "someQueryKey", "someQueryValue")
                .withArrayLabel("someArrayLabel")
                .addToNamedQueryObjectArray("anotherQueryLabel", "anotherQueryKey", "anotherQueryValue");
        assert( nqoac.toString().equals(expectedResult) );
    }
}