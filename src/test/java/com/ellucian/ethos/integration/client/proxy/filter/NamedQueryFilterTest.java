/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;


import org.junit.jupiter.api.Test;

public class NamedQueryFilterTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Test
    public void namedQueryFilterTest() {
        NamedQuery namedQuery = new SimpleCriteria.Builder()
                                .withNamedQuery("keywordSearch", "keywordSearch", "Culture");
        NamedQueryFilter namedQueryFilter = new NamedQueryFilter( namedQuery );
        assert( namedQueryFilter.toString() != null );
    }
}