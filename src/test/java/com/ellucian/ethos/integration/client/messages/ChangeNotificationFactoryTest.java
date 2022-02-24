package com.ellucian.ethos.integration.client.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChangeNotificationFactoryTest {

    private String cnJson = "{" +
            "    \"id\": 1," +
            "    \"published\": \"2020-10-30 12:00:00.000Z\"," +
            "    \"publisher\": {" +
            "        \"applicationName\": \"app1\"," +
            "        \"id\": \"97d381ac-97c6-439c-8799-525334f2d908\"," +
            "        \"tenant\": {" +
            "            \"id\": \"328704a4-5502-4fd2-b1be-80aff71d4241\"," +
            "            \"alias\": \"ellucian\"," +
            "            \"name\": \"university\"," +
            "            \"environment\": \"test\"" +
            "        }" +
            "    }," +
            "    \"resource\": {" +
            "        \"name\": \"persons\"," +
            "        \"id\": \"fee12eb6-dae1-456b-a7c4-063458617478\"," +
            "        \"version\": \"application/vnd.hedtech.integration.v12+json\"," +
            "        \"domain\": \"core\"" +
            "    }," +
            "    \"operation\": \"created\"," +
            "    \"contentType\": \"resource-representation\"," +
            "    \"content\": {" +
            "        \"id\": \"fee12eb6-dae1-456b-a7c4-063458617478\"" +
            "    }" +
            "}";

    @Test
    void testCreateCNFromJson() throws JsonProcessingException {
        ChangeNotification cn = ChangeNotificationFactory.createCNFromJson(cnJson);
        assertEquals(cn.getId(), "1");
        assertNotNull(cn.getPublisher());
        assertNotNull(cn.getPublisher().getTenant());
        assertNotNull(cn.getResource());
    }

    @Test
    void testCreateCNListFromJson() throws JsonProcessingException {
        String json = String.format("[%s,%s]", cnJson, cnJson);
        List<ChangeNotification> cnList = ChangeNotificationFactory.createCNListFromJson(json);
        assertEquals(cnList.size(), 2);
        ChangeNotification cn = cnList.get(0);
        assertEquals(cn.getId(), "1");
        assertNotNull(cn.getPublisher());
        assertNotNull(cn.getPublisher().getTenant());
        assertNotNull(cn.getResource());
    }

    @Test
    void testCreateEmptyCNListFromJson() throws JsonProcessingException {
        List<ChangeNotification> cnList = ChangeNotificationFactory.createCNListFromJson("[]");
        assertEquals(cnList.size(), 0);
    }

}
