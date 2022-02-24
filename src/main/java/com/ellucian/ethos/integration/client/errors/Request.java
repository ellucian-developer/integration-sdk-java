package com.ellucian.ethos.integration.client.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Request object that is associated with an Error.
 *
 * @since 0.0.1
 */
public class Request {

    private String URI;
    private String[] headers;
    private String payload;

    /**
     * Create a Request object.
     * @param URI the URI of the request
     * @param headers an array of headers that were sent in the request
     * @param payload the payload that was sent in the request
     */
    @JsonCreator
    public Request(@JsonProperty("URI") String URI, @JsonProperty("headers") String[] headers,
                   @JsonProperty("payload") String payload) {
        this.URI = URI;
        this.headers = headers;
        this.payload = payload;
    }

    /**
     * Get the URI to which the request was sent.
     * @return the URI string
     */
    public String getURI() {
        return URI;
    }

    /**
     * Get the headers that were sent in the request.
     * @return a String array of headers
     */
    public String[] getHeaders() {
        return headers;
    }

    /**
     * Get the payload that was sent in the request.
     * @return the payload of the request
     */
    public String getPayload() {
        return payload;
    }
}
