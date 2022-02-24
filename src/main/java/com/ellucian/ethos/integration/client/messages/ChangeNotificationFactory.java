package com.ellucian.ethos.integration.client.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

/**
 * A Factory class to help with building ChangeNotification objects.
 *
 * @since 0.0.1
 */
public class ChangeNotificationFactory {

    /**
     * Create a ChangeNotification object using a JSON string.  This will attempt to parse the given string into a
     * ChangeNotification object.
     * @param json The JSON string representing a change-notification.
     * @return A ChangeNotification object created from the given JSON string.
     * @throws JsonProcessingException if the string cannot be parsed to create a ChangeNotification
     */
    public static ChangeNotification createCNFromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ChangeNotification.class);
    }

    /**
     * Create a ChangeNotification array using a JSON string.  This will attempt to parse the given string into a
     * ChangeNotification array.
     * @param json The JSON string representing an array of change-notifications.
     * @return A ChangeNotification array created from the given JSON string.
     * @throws JsonProcessingException if the string cannot be parsed to create a ChangeNotifications array
     */
    public static List<ChangeNotification> createCNListFromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ChangeNotification[] cns =  mapper.readValue(json, ChangeNotification[].class);
        return Arrays.asList(cns);
    }
}
