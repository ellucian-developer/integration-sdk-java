package com.ellucian.ethos.integration.client.messages;

import com.ellucian.ethos.integration.client.EthosResponse;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

public class EthosMessagesClientTest {

    @Test
    void consumeWithInvalidLimit() {
        EthosMessagesClient client = new EthosMessagesClient("11111111-1111-1111-1111-111111111111", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.consumeWithLimit(0);
        });
    }

    @Test
    void consumeWithInvalidLimit2() {
        EthosMessagesClient client = new EthosMessagesClient("11111111-1111-1111-1111-111111111111", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.consumeWithLimit(1001);
        });
    }

    @Test
    void consumeWithInvalidLimit3() {
        EthosMessagesClient client = new EthosMessagesClient("11111111-1111-1111-1111-111111111111", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.consume(1001, 0);
        });
    }

    @Test
    void consumeWithInvalidLimit4() {
        EthosMessagesClient client = new EthosMessagesClient("11111111-1111-1111-1111-111111111111", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.consume(1001, 0);
        });
    }

    @Test
    void getNumAvailableMessages() throws IOException {
        EthosMessagesClient ethosClient = Mockito.spy( new EthosMessagesClient("11111111-1111-1111-1111-111111111111", null, null, null) );
        HashMap<String, Header> headers = new HashMap<>();
        headers.put("x-remaining", new BasicHeader("x-remaining", "5"));
        EthosResponse testEthosResponse = new EthosResponse(headers,"someResponseBody", 200);
        doReturn(testEthosResponse).when(ethosClient).head(anyString());
        int num = ethosClient.getNumAvailableMessages();
        assertEquals(num, 5);
    }

    @Test
    void getNumMessagesMissingHeader() throws IOException {
        EthosMessagesClient ethosClient = Mockito.spy( new EthosMessagesClient("11111111-1111-1111-1111-111111111111", null, null, null) );
        HashMap<String, Header> headers = new HashMap<>();
        EthosResponse testEthosResponse = new EthosResponse(headers,"someResponseBody", 200);
        doReturn(testEthosResponse).when(ethosClient).head(anyString());
        int num = ethosClient.getNumAvailableMessages();
        assertEquals(num, 0);
    }

}
