package com.ellucian.ethos.integration.client.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An Ellucian Ethos Integration Error object.
 *
 * @since 0.0.1
 */
@JsonIgnoreProperties({ "metadata" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EthosError {

    /** The Info level severity for an Error object. */
    public static final String INFO = "info";

    /** The Warning level severity for an Error object. */
    public static final String WARNING = "warning";

    /** The Error level severity for an Error object. */
    public static final String ERROR = "error";

    /** A GUID for this error. **/
    private String id;

    /** (Required) The severity of this error.  This should be one of info, warning, or error. */
    private String severity;

    /** (Required) An integer response code associated with this error. */
    private int responseCode;

    /** (Required) A description for this error. */
    private String description;

    /** A detailed message for this error. */
    private String details;

    /** The ID of the application reporting this error. */
    private String applicationId;

    /** The name of the application reporting this error. */
    private String applicationName;

    /** The Date that the error occurred. */
    private Date dateTime;

    /** The id of the original operation (event or message) to which this error can be traced. */
    private String correlationId;

    /** The sub-type of the application reporting this error.  This could be used to describe a sub-system. */
    private String applicationSubtype;

    /** The Resource associated with this error. */
    private Resource resource;

    /** The Request that caused this error. */
    private Request request;


    /** No-arg constructor */
    public EthosError() {}

    /**
     * Get the ID for this error
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the Severity for the error.  This should be one of info, warning, or error.
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Get the response code associated with this error.
     * @return the response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Get the description for this error.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the detailed message for this error.
     * @return the detailed error message
     */
    public String getDetails() {
        return details;
    }

    /**
     * Get the ID of the application that reported the error.
     * @return the application ID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Get the name of the application that reported the error.
     * @return the application name
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Get the date and time that the error was reported.
     * @return the Date that the error was reported.
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * Get the correlation ID for this error.  This is the ID of the original operation (event or message) to which
     * this error can be traced.
     * @return the correlation ID
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Get the sub-type of the application that reported the error.  This could be used to describe a sub-system.
     * @return a String value for the application sub-type.
     */
    public String getApplicationSubtype() {
        return applicationSubtype;
    }

    /**
     * Get the resource associated with this error.
     * @return a Resource object
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Get original request that caused the error.
     * @return a Request object
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Sets the ID (GUID)
     * @param id The unique GUID value for this EthosError.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the severity for this error.  This should be one of info, warning, or error.
     * @param severity The severity for this error.
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Sets the response code for this error.
     * @param responseCode The response code to set.
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Set the description for this error.
     * @param description The error description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the detailed message for this error.
     * @param details The detailed error message.
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Set the ID of the application that reported the error.
     * @param applicationId The application ID.
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * Set the name of the application that reported the error.
     * @param applicationName the application name.
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Set the date and time that the error was reported.
     * @param dateTime the date the error was reported.
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     *  Set the correlation ID for this error.  This is the ID of the original operation (event or message) to which
     *  this error can be traced.
     * @param correlationId The correlation ID to set.
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Set the sub-type of the application that reported the error.  This could be used to describe a sub-system.
     * @param applicationSubtype The application sub-type.
     */
    public void setApplicationSubtype(String applicationSubtype) {
        this.applicationSubtype = applicationSubtype;
    }

    /**
     * Get the resource associated with this error.
     * @param resource The resource for this error.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Set original request that caused the error.
     * @param request The request causing this error.
     */
    public void setRequest(Request request) {
        this.request = request;
    }

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
