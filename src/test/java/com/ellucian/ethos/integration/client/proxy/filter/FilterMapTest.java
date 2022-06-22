/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

import java.util.List;

public class FilterMapTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Test
    public void withSingleParameterPairTest() {
        String expectedResult = "?firstName=John";
        FilterMap filterMap = new FilterMap.Builder()
                                  .withParameterPair("firstName", "John")
                                  .build();
        String filterStr = filterMap.toString();
        assert( filterStr != null );
        assert( filterStr.equals(expectedResult) );
    }

    @Test
    public void withMultiParameterPairTest() {
        String expectedResult1 = "?credentialValue=A00000718&credentialType=bannerId";
        String expectedResult2 = "?credentialType=bannerId&credentialValue=A00000718";
        FilterMap filterMap = new FilterMap.Builder()
                                  .withParameterPair("credentialValue", "A00000718")
                                  .withParameterPair("credentialType", "bannerId")
                                  .build();
        String filterStr = filterMap.toString();
        assert( filterStr != null );
        // Not sure of the order from the filterMap, so as long as it equals to one of the expected results.
        assert( filterStr.equals(expectedResult1) || filterStr.equals(expectedResult2) );
    }

    @Test
    public void getFilterMapKeysTest() {
        FilterMap filterMap = new FilterMap.Builder()
                .withParameterPair("credentialValue", "A00000718")
                .withParameterPair("credentialType", "bannerId")
                .build();
        List<String> keyList = filterMap.getFilterMapKeys();
        assert( keyList != null );
        assert( keyList.size() == 2 );
    }

    @Test
    public void getFilterMapValueTest() {
        String key1 = "credentialValue";
        String key2 = "credentialType";
        String value1 = "A00000718";
        String value2 = "bannerId";
        FilterMap filterMap = new FilterMap.Builder()
                .withParameterPair(key1, value1)
                .withParameterPair(key2, value2)
                .build();
        String valueOne = filterMap.getFilterMapValue( key1 );
        String valueTwo = filterMap.getFilterMapValue( key2 );
        assert( valueOne != null );
        assert( valueOne.equals(value1) );
        assert( valueTwo != null );
        assert( valueTwo.equals(value2) );
    }

}