/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleCriteriaTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Test
    public void simpleCriteriaToStringTest() {
        String expectedResult = "\"firstName\":\"John\"";
        SimpleCriteria sc = new SimpleCriteria( "firstName", "John" );
        String result = sc.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );

        expectedResult = "\"year\":2021";
        sc = new SimpleCriteria( "year", "2021", true );
        result = sc.toString();
        assert( result != null );
        assert( result.equals(expectedResult) );
    }

    @Test
    public void buildCriteriaFilterTest() {
        SimpleCriteria sc = new SimpleCriteria( "firstName", "John" );
        CriteriaFilter criteriaFilter = sc.buildCriteriaFilter();
        assert( criteriaFilter != null );
        assert( criteriaFilter instanceof CriteriaFilter );
    }

    @Test
    public void simpleCriteriaBuilderTest() {
        SimpleCriteria simpleCriteria = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John" );
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof SimpleCriteria );

        simpleCriteria = new SimpleCriteria.Builder()
                        .withSimpleCriteriaObject("myLabel", "firstName", "John" );
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof SimpleCriteriaObject );

        SimpleCriteriaValueArray scva = new SimpleCriteria.Builder()
                                        .withSimpleCriteriaValueArray( "fruit", "oranges");
        simpleCriteria = new SimpleCriteria.Builder()
                        .withSimpleCriteriaObject( "myLabel", scva);
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof SimpleCriteriaObject );

        simpleCriteria = new SimpleCriteria.Builder()
                         .withSimpleCriteriaArray("myLabel");
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof SimpleCriteriaArray );

        simpleCriteria = new SimpleCriteria.Builder()
                         .withSimpleCriteriaArray("myLabel", "firstName", "John" );
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof SimpleCriteriaArray );

        simpleCriteria = new SimpleCriteria.Builder()
                         .withSimpleCriteriaObjectArray("myLabel" );
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof SimpleCriteriaObjectArray);

        SimpleCriteriaObject sco = new SimpleCriteria.Builder().withSimpleCriteriaObject("innerLabel", "myKey", "myValue" );
        simpleCriteria = new SimpleCriteria.Builder()
                        .withSimpleCriteriaObjectArray("myLabel", sco );
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof SimpleCriteriaObjectArray);

        simpleCriteria = new SimpleCriteria.Builder()
                         .withMultiCriteriaObjectForArray("firstName", "John" );
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof MultiCriteriaObject );

        simpleCriteria = new SimpleCriteria.Builder()
                         .withMultiCriteriaObject("myLabel", "year", "2021", true );
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof MultiCriteriaObject );

        simpleCriteria = new SimpleCriteria.Builder()
                         .withMultiCriteriaObjectArray( "myLabel" );
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof MultiCriteriaObjectArray );

        simpleCriteria = new SimpleCriteria.Builder()
                         .withSimpleCriteriaValueArray("firstName", "John" );
        assert( simpleCriteria != null );
        assert( simpleCriteria instanceof SimpleCriteriaValueArray);

        NamedQuery namedQuery = new SimpleCriteria.Builder()
                                .withNamedQuery("someName", "firstName", "John");
        assert( namedQuery != null );
        assert( namedQuery instanceof NamedQuery );

        NamedQueryObject namedQueryObject = new SimpleCriteria.Builder()
                .withNamedQueryObject("someName", "someLabel", "firstName", "John");
        assert( namedQueryObject != null );
        assert( namedQueryObject instanceof NamedQueryObject );

        NamedQueryCombination namedQueryCombination = new SimpleCriteria.Builder()
                .withNamedQueryCombination("someName", "firstName", "John");
        assert( namedQueryCombination != null );
        assert( namedQueryCombination instanceof NamedQueryCombination );

        NamedQueryObjectArrayCombination namedQueryObjectArrayCombination = new SimpleCriteria.Builder()
                .withNamedQueryObjectArrayCombination("someName", "someLabel", "firstName", "John");
        assert( namedQueryObjectArrayCombination != null );
        assert( namedQueryObjectArrayCombination instanceof NamedQueryObjectArrayCombination );

    }

    @Test
    public void simpleCriteriaBuilderTestsThrowExceptions() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteria(null, null );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteria("", "" );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaObject(null, "firstName", "John" );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaObject("myLabel", "", "" );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaObject("myLabel", null, null );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            SimpleCriteriaValueArray scva = new SimpleCriteria.Builder().withSimpleCriteriaValueArray("fruit", "oranges");
            new SimpleCriteria.Builder().withSimpleCriteriaObject(null, scva );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaObject("myLabel", null );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaArray(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaArray("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaObjectArray(null );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaObjectArray("" );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            SimpleCriteriaObject sco = new SimpleCriteria.Builder().withSimpleCriteriaObject("innerLabel", "myKey", "myValue" );
            new SimpleCriteria.Builder().withSimpleCriteriaObjectArray(null, sco );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            SimpleCriteriaObject sco = new SimpleCriteria.Builder().withSimpleCriteriaObject("innerLabel", "myKey", "myValue" );
            new SimpleCriteria.Builder().withSimpleCriteriaObjectArray("", sco );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaObjectArray("myLabel", null );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withMultiCriteriaObjectForArray(null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withMultiCriteriaObjectForArray("", "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withMultiCriteriaObject(null, null, "2021", true );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withMultiCriteriaObject("myLabel", null, "2021", true );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withMultiCriteriaObjectArray( null );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaValueArray(null, null );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withSimpleCriteriaValueArray("", "" );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withNamedQuery(null, null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withNamedQuery("", "", "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withNamedQueryObject(null, null, null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withNamedQueryObject("", "", "", "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withNamedQueryCombination(null, null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withNamedQueryCombination("", "", "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withNamedQueryObjectArrayCombination(null, null, null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCriteria.Builder().withNamedQueryObjectArrayCombination("", "", "", "");
        });
    }


}