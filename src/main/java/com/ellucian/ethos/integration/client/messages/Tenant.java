/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.messages;

/**
 * A Tenant object that is associated with a change-notification.  This holds information
 * about the ethos tenant where the change occurred.
 *
 * @since 0.0.1
 */
public class Tenant {

    /** The ID of the tenant.*/
    private String id;
    /** The alias of the tenant.*/
    private String alias;
    /** The name of the tenant.*/
    private String name;
    /** The tenant environment.*/
    private String environment;

    /**
     * Creates an instance of a Tenant.
     */
    public Tenant(){}

    /**
     * Gets the ID of the tenant.
     * @return the tenant ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the alias of the tenant.
     * @return the tenant alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Gets the name of the tenant.
     * @return the tenant name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the tenant environment
     * @return the tenant environment
     */
    public String getEnvironment() {
        return environment;
    }

}
