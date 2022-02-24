/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CriteriaFilterTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================

    @Test
    public void withCriteriaSetThrowsExceptionTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CriteriaFilter criteriaFilter = new CriteriaFilter.Builder()
                    .withCriteriaSet( null, "John")
                    .build();
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CriteriaFilter criteriaFilter = new CriteriaFilter.Builder()
                    .withCriteriaSet( "", "John")
                    .build();
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CriteriaFilter criteriaFilter = new CriteriaFilter.Builder()
                    .withCriteriaSet( "firstName", null)
                    .build();
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CriteriaFilter criteriaFilter = new CriteriaFilter.Builder()
                    .withCriteriaSet( "firstName", "")
                    .build();
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CriteriaFilter criteriaFilter = new CriteriaFilter.Builder()
                    .withCriteriaSet( null)
                    .build();
        });
    }

    /**
     * The assertions that test whether the expectedResult JSON matches the result DOES NOT test whether
     * the expectedResult is valid JSON.  This is because these criteria objects may not generate complete JSON structures
     * in and of themselves.
     */
    @Test
    public void withCriteriaSetValuesTest() {
        String expectedResult = "?criteria={\"firstName\":\"John\"}";
        CriteriaFilter criteriaFilter = new CriteriaFilter.Builder()
                                            .withCriteriaSet( "firstName", "John")
                                            .build();
        String result = criteriaFilter.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }

    /**
     * The assertions that test whether the expectedResult JSON matches the result DOES NOT test whether
     * the expectedResult is valid JSON.  This is because these criteria objects may not generate complete JSON structures
     * in and of themselves.
     */
    @Test
    public void withCriteriaSetSimpleCriteriaTest() {
        String expectedResult = "?criteria={\"firstName\":\"John\"}";
        CriteriaFilter criteriaFilter = new CriteriaFilter.Builder()
                .withCriteriaSet(new SimpleCriteria("firstName", "John"))
                .build();
        String result = criteriaFilter.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }

    /**
     * The assertions that test whether the expectedResult JSON matches the result DOES NOT test whether
     * the expectedResult is valid JSON.  This is because these criteria objects may not generate complete JSON structures
     * in and of themselves.
     */
    @Test
    public void withCriteriaSetMultipleCriteriaTest() {
        String expectedResult = "?criteria={\"firstName\":\"John\",\"lastName\":\"Smith\"}";
        CriteriaFilter criteriaFilter = new CriteriaFilter.Builder()
                .withCriteriaSet("firstName", "John")
                .withSimpleCriteria("lastName", "Smith")
                .build();
        String result = criteriaFilter.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }

    /**
     * The assertions that test whether the expectedResult JSON matches the result DOES NOT test whether
     * the expectedResult is valid JSON.  This is because these criteria objects may not generate complete JSON structures
     * in and of themselves.
     */
    @Test
    public void withCriteriaSetMultipleCriteriaSetsTest() {
        String expectedResult = "?criteria={\"firstName\":\"John\",\"lastName\":\"Smith\",\"role\":\"student\"}";
        CriteriaFilter criteriaFilter = new CriteriaFilter.Builder()
                .withCriteriaSet("firstName", "John")
                .withSimpleCriteria("lastName", "Smith")
                .newCriteriaSet()
                .withSimpleCriteria("role", "student")
                .build();
        String result = criteriaFilter.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }

}