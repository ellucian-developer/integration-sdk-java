/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

/**
 * Converts request body generic type objects into JSON formatted strings, for use when making POST or PUT API requests.
 * @param <T> The generic type to convert from.
 */
public class EthosRequestConverter<T> {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /** The date format to use when handling date fields. */
    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /** A Jackson object mapper to convert the request body to a JSON string format. */
    private ObjectMapper objectMapper;

    /**
     * No-arg constructor which also builds the objectMapper.
     */
    public EthosRequestConverter() {
        super();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( DATE_FORMAT );
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setDateFormat( simpleDateFormat );
    }


    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Converts the given generic object type to a JSON formatted string.
     * Intended to be used for converting a generic object type of a request body into a JSON formatted string.
     * @param genericType The generic object type of the request body to convert.
     * @return A JSON formatted request body string representing the given generic object type, or null of the genericType is null.
     * @throws JsonProcessingException Thrown if the objectMapper cannot write the generic type as a string.
     */
    public String toJsonString( T genericType ) throws JsonProcessingException {
        if( genericType != null ) {
            return objectMapper.writeValueAsString( genericType );
        }
        return null;
    }
}

