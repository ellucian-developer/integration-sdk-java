package com.ellucian.ethos.integration.client.errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

/**
 * A Factory class to help with building Error objects.
 *
 * @since 0.0.1
 */
public class ErrorFactory {

    /**
     * Create an Error object using a JSON string.  This will attempt to parse the given string into an Error object.
     * @param json the JSON string representing an error
     * @return an Error object created from the given JSON string.
     * @throws JsonProcessingException if the string cannot be parsed to create an Error
     */
    public static EthosError createErrorFromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, EthosError.class);
    }

    /**
     * Create an Error array using a JSON string.  This will attempt to parse the given string into a list of Errors.
     * @param json the JSON string representing an array of errors.
     * @return a List of Errors created from the given JSON string.
     * @throws JsonProcessingException Thrown if the string cannot be parsed to create a list of Errors.
     */
    public static List<EthosError> createErrorListFromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        EthosError[] ethosErrors = mapper.readValue(json, EthosError[].class);
        return Arrays.asList(ethosErrors);
    }
}
