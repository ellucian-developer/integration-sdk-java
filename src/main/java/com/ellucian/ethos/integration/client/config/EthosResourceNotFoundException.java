/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.config;


/**
 * Thrown when the given resource is not found in the available resources response.
 */
public class EthosResourceNotFoundException extends RuntimeException {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** The name of the resource that was not found. */
    private String resourceName;

    /**
     * Constructor taking the error message.
     * @param message The error message for this exception.
     */
    public EthosResourceNotFoundException(String message ) {
        super( message );
    }

    /**
     * Constructor taking the error message and the name of the resource not found in the available resources response.
     * @param message The error message for this exception.
     * @param resourceName The name of the resource not found.
     */
    public EthosResourceNotFoundException(String message, String resourceName ) {
        this( message );
        this.resourceName = resourceName;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Gets the name of the resource that was not found.
     * @return The name of the resource not found.
     */
    public String getResourceName() {
        return resourceName;
    }
}