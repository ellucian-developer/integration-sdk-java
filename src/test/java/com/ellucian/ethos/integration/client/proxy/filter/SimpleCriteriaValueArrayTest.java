/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleCriteriaValueArrayTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Test
    public void simpleCriteriaValueArrayToStringTest() {
        String expectedResult = "\"id\":[\"123456\"]";
        SimpleCriteriaValueArray scva = new SimpleCriteriaValueArray(  "id", "123456" );
        String result = scva.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );

        expectedResult = "\"id\":[\"123456\",\"apples\"]";
        scva = new SimpleCriteriaValueArray("id", "123456").addValue("apples");
        result = scva.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }

    @Test
    public void addValueThrowsExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteriaValueArray( "id", "123456").addValue( null );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteriaValueArray( "id", "123456").addValue( "" );
        });
    }

    @Test
    public void getValueCountTest() {
        SimpleCriteriaValueArray scva = new SimpleCriteriaValueArray("id", "123456").addValue("apples");
        assert( scva.getValueCount() == 2 );
    }
}