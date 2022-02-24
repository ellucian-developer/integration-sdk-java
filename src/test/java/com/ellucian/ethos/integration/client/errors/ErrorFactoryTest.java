package com.ellucian.ethos.integration.client.errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorFactoryTest {

    private String errorJson = "{\"dateTime\": \"2020-09-01T12:00:00Z\", \"severity\": \"Error\", \"applicationName\": \"Colleague\", " +
            "\"responseCode\": 500, \"description\": \"Internal Server Error\", " +
            "\"details\": \"This is a more info on the info error\", \"id\": \"b1a3fc8f-d0cd-4a8b-a6c6-af252f4e49f7\", " +
            "\"applicationId\": \"67b462f2-c554-4c15-91fa-e1194a85553b\", \"correlationId\": \"2468UserMade3242134\", " +
            "\"applicationSubtype\": \"EMA\", " +
            "\"resource\": { \"id\": \"b1a3fc8f-d0cd-4a8b-a6c6-af252f4e49f7\", \"name\": \"persons\" }, " +
            "\"request\": { \"URI\": \"www.papa-johns.com\", \"payload\": \"Order me a pizza\", " +
            "\"headers\": [ \"contentType\", \"secondHeader\", \"third header\" ] }}";

    @Test
    void testCreateErrorFromJson() throws JsonProcessingException {
        EthosError error = ErrorFactory.createErrorFromJson(errorJson);
        assertEquals(error.getSeverity(), "Error");
        assertNotNull(error.getResource());
        assertNotNull(error.getRequest());
        assertEquals(error.getRequest().getHeaders().length, 3);
    }

    @Test
    void testCreateErrorArrayFromJson() throws JsonProcessingException {
        String json = String.format("[%s,%s]", errorJson, errorJson);
        List<EthosError> errors = ErrorFactory.createErrorListFromJson(json);
        assertEquals(errors.size(), 2);
        assertEquals(errors.get(0).getSeverity(), "Error");
        assertNotNull(errors.get(0).getResource());
        assertNotNull(errors.get(0).getRequest());
        assertEquals(errors.get(0).getRequest().getHeaders().length, 3);
    }

}
