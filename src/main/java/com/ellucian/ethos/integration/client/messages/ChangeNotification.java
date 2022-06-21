/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.messages;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An object representation of a change-notification.  A change-notification is the resource
 * that is published and consumed via subscriptions through the Ethos Integration messages service.
 *
 * @since 0.0.1
 */
public class ChangeNotification {

    /** The ID of the change-notification.*/
    private String id;
    /** The date and time that the change-notification was published.*/
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    private Date published;
    /** Information about the publishing application.*/
    private Publisher publisher;
    /** The resource to which the change occurred.*/
    private Resource resource;
    /** The operation that occurred on the resource.*/
    private String operation;
    /** The content-type associated with the content object.*/
    private String contentType;
    /** The content of the resource that was change.*/
    private JsonNode content;

    /**
     * Creates an instance of a change-notification.
     */
    public ChangeNotification(){}

    /**
     * Gets the ID of the change-notification
     * @return the ID of the change-notification
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the date and time that this change-notification was published.  This will be
     * in the UTC time zone.
     * @return the published date and time
     */
    public Date getPublished() {
        return published;
    }

    /**
     * Gets the publishing application information
     * @return the publisher info
     */
    public Publisher getPublisher() {
        return publisher;
    }

    /**
     * Gets the resource to which the change occurred.
     * @return the resource that changed
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Gets the operation that occurred to the resource.
     * @return the change operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Gets the content-type associated with the content object.
     * @return the resource content-type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the contentType.  Used to replace the contentType if needed.
     * @param contentType The contentType to replace the current contentType of this ChangeNotification.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the content of the resource that was changed.
     * @return the content of the changed resource
     */
    public JsonNode getContent() {
        return content;
    }

    /**
     * Sets the content.  Used to replace the content if needed.
     * @param content The content of the resource to replace the current content with.
     */
    public void setContent(JsonNode content) {
        this.content = content;
    }

    /**
     * Provides a string representation of this ChangeNotification in JSON format.
     * @return A JSON formatted string of this ChangeNotification.
     */
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            return super.toString();
        }
    }
}
