package com.ellucian.ethos.integration.client.messages;

/**
 * A Publisher object that is associated with a change-notification.  This holds information
 * about the application that published the change.
 *
 * @since 0.0.1
 */
public class Publisher {

    /** The ID of the publishing application.*/
    private String id;
    /** The name of the publishing application.*/
    private String applicationName;
    /** The tenant where the change occurred.*/
    private Tenant tenant;

    /**
     * Creates an instance of a Publisher.
     */
    public Publisher(){}

    /**
     * Gets the ID of the publishing application.
     * @return the application ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets tne name of the publishing application.
     * @return the application name
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Gets the tenant where the change occurred.
     * @return the tenant information
     */
    public Tenant getTenant() {
        return tenant;
    }
}
