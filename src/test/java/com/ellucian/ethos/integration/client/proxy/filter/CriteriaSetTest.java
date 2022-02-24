/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

import org.junit.jupiter.api.Test;

public class CriteriaSetTest {

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
    public void criteriaSetSingleCriteriaToStringTest() {
        String expectedResult = "\"firstName\":\"John\"";
        CriteriaSet criteriaSet = new CriteriaSet();
        SimpleCriteria sc = new SimpleCriteria( "firstName", "John" );
        criteriaSet.addSimpleCriteria( sc );
        String result = criteriaSet.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }

    /**
     * The assertions that test whether the expectedResult JSON matches the result DOES NOT test whether
     * the expectedResult is valid JSON.  This is because these criteria objects may not generate complete JSON structures
     * in and of themselves.
     */
    @Test
    public void criteriaSetMultiCriteriaToStringTest() {
        String expectedResult = "\"firstName\":\"John\",\"lastName\":\"Smith\"";
        CriteriaSet criteriaSet = new CriteriaSet();
        SimpleCriteria sc = new SimpleCriteria( "firstName", "John" );
        criteriaSet.addSimpleCriteria( sc );
        sc = new SimpleCriteria( "lastName", "Smith" );
        criteriaSet.addSimpleCriteria( sc );
        String result = criteriaSet.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }

    /**
     * The assertions that test whether the expectedResult JSON matches the result DOES NOT test whether
     * the expectedResult is valid JSON.  This is because these criteria objects may not generate complete JSON structures
     * in and of themselves.
     */
    @Test
    public void criteriaSetMultiNamedCriteriaToStringTest() {
        String expectedResult = "\"type\":{\"id\":\"b4261779-2c68-4e08-a027-14bcfb64a72b\"},\"value\":\"123456\"";
        CriteriaSet criteriaSet = new CriteriaSet();
        SimpleCriteriaObject nsc = new SimpleCriteriaObject( "type", "id", "b4261779-2c68-4e08-a027-14bcfb64a72b" );
        criteriaSet.addSimpleCriteria( nsc );
        SimpleCriteria sc = new SimpleCriteria( "value", "123456" );
        criteriaSet.addSimpleCriteria( sc );
        String result = criteriaSet.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }

}