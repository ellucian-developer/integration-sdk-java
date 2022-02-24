package com.ellucian.ethos.integration.client.messages;

/**
 * A Resource object that is associated with a change-notification.
 *
 * @since 0.0.1
 */
public class Resource {

    /** The ID of the resource.*/
    private String id;
    /** The name of the resource.*/
    private String name;
    /** The version of the resource.*/
    private String version;
    /** The domain to which the resource belongs.*/
    private String domain;

    /**
     * Creates an instance of a Resource.
     */
    public Resource(){}

    /**
     * Gets the ID of the resource
     * @return the resource ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the resource.
     * @return the resource name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the version of the resource.
     * @return the resource version
     */
    public String getVersion() { return version; }

    /**
     * Sets the version, used to replace the version if needed, as done in the EthosChangeNotificationService.
     * @param version The version of the resource to replace the current version with.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the domain to which the resource belongs.
     * @return the domain of the resource
     */
    public String getDomain() { return domain; }
}
