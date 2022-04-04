package com.ellucian.ethos.integration.client.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Resource object that is associated with an Error.
 *
 * @since 0.0.1
 */
public class Resource {

    private String id;
    private String name;
    private String version;

    /**
     * Create an instance of a Resource.
     * @param id the ID of the resource
     * @param name the name of the resource
     * @param version the version of the resource
     */
    @JsonCreator
    public Resource(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("version") String version){
        this.id = id;
        this.name = name;
        this.version = version;
    }

    /**
     * Get the ID of the resource
     * @return the resource ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the name of the resource.
     * @return the resource name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the version of the resource that erred.
     * @return the resource version
     */
    public String getVersion() {
        return version;
    }
}
