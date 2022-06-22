/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy;

/**
 * Used for unit testing QAPI requests.
 */
public class SomeQAPIRequestBody {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private String someProperty;
    // ==========================================================================
    // Methods
    // ==========================================================================

    public String getSomeProperty() {
        return someProperty;
    }

    public void setSomeProperty(String someProperty) {
        this.someProperty = someProperty;
    }
}
