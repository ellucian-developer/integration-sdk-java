/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.config;


/**
 * Thrown when an unsupported version is requested for an Ethos resource.
 */
public class UnsupportedVersionException extends RuntimeException {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /**
     * The unsupported version that was requested for the some resource.
     */
    protected String unsupportedVersion;

    /**
     * The name of the resource that the unsupportedVersion was requested for.
     */
    protected String resourceName;

    /**
     * Constructs this exception with the given error message.
     * @param message The error message describing the error.
     */
    public UnsupportedVersionException( String message ) {
        super( message );
    }

    /**
     * Constructs this exception with the given error message and unsupported version that was requested for some resource.
     * @param message The error message describing the error.
     * @param unsupportedVersion the unsupported version of the requested resource.
     */
    public UnsupportedVersionException( String message, String unsupportedVersion ) {
        this( message );
        this.unsupportedVersion = unsupportedVersion;
    }

    /**
     * Constructs this exception with the given error message, resource name, and unsupported version of the resource.
     * @param message The error message describing the error.
     * @param resourceName The name of the Ethos resource.
     * @param unsupportedVersion The unsupported version of the resource.
     */
    public UnsupportedVersionException( String message, String resourceName, String unsupportedVersion ) {
        this( message, unsupportedVersion );
        this.resourceName = resourceName;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Gets the unsupported version.
     * @return The unsupported version value.
     */
    public String getUnsupportedVersion() {
        return this.unsupportedVersion;
    }

    /**
     * Gets the name of the Ethos resource.
     * @return The name of the Ethos resource for which an unsupported version was requested.
     */
    public String getResourceName() {
        return this.resourceName;
    }

}