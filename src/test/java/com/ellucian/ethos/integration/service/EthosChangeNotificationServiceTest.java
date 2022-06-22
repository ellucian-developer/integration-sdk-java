/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.service;


import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.EthosResponseConverter;
import com.ellucian.ethos.integration.client.config.EthosConfigurationClient;
import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.client.messages.ChangeNotificationFactory;
import com.ellucian.ethos.integration.client.messages.EthosMessagesClient;
import com.ellucian.ethos.integration.client.proxy.EthosFilterQueryClient;
import com.ellucian.ethos.integration.client.proxy.EthosProxyClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class EthosChangeNotificationServiceTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    @Mock
    private EthosMessagesClient mockEthosMessagesClient;

    @Mock
    private EthosProxyClient mockEthosProxyClient;

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Test
    public void builderTest() {
        String resource1 = "someResource";
        String resource2 = "anotherResource";
        String version1  = "someVersion1";
        String version2  = "someVersion2";
        String apiKey = "11111111-1111-1111-1111-111111111111";
        EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(apiKey)
                                                     .withConnectionTimeout(30)
                                                     .withConnectionRequestTimeout(20)
                                                     .withSocketTimeout(10)
                                                     .withResourceVersionOverride(resource1, version1)
                                                     .withResourceVersionOverride(resource2, version2)
                                                     .build();
        List<String> resourceOverrides = service.getOverriddenResources();
        assert( resourceOverrides.size() == 2 );
        assert( resourceOverrides.contains(resource1) );
        assert( resourceOverrides.contains(resource2) );
        assert( service.getOverriddenResourceVersion(resource1).equals(version1) );
        assert( service.getOverriddenResourceVersion(resource2).equals(version2) );
    }

    @Test
    public void builderTestThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            EthosClientBuilder ethosClientBuilder = null;
            EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(ethosClientBuilder).build();
        });

        assertThrows(IllegalArgumentException.class, () -> {
            String apiKey = "11111111-1111-1111-1111-111111111111";
            EthosClientBuilder ethosClientBuilder = new EthosClientBuilder(apiKey);
            EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(ethosClientBuilder)
                                                     .withResourceVersionOverride(null, "someVersion")
                                                     .build();
        });

        assertThrows(IllegalArgumentException.class, () -> {
            String apiKey = "11111111-1111-1111-1111-111111111111";
            EthosClientBuilder ethosClientBuilder = new EthosClientBuilder(apiKey);
            EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(ethosClientBuilder)
                    .withResourceVersionOverride("someResource", null)
                    .build();
        });

        assertThrows(IllegalArgumentException.class, () -> {
            String apiKey = "11111111-1111-1111-1111-111111111111";
            EthosClientBuilder ethosClientBuilder = new EthosClientBuilder(apiKey);
            EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(ethosClientBuilder)
                    .withResourceVersionOverride(null, null)
                    .build();
        });

        assertThrows(IllegalArgumentException.class, () -> {
            String apiKey = "11111111-1111-1111-1111-111111111111";
            EthosClientBuilder ethosClientBuilder = new EthosClientBuilder(apiKey);
            EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(ethosClientBuilder)
                    .withResourceAbbreviatedVersionOverride("someResource", null)
                    .build();
        });
    }

    @Test
    public void addResourceVersionOverrideTest() {
        String resource1 = "someResource";
        String resource2 = "anotherResource";
        String version1  = "someVersion1";
        String version2  = "someVersion2";
        String apiKey    = "11111111-1111-1111-1111-111111111111";
        EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(apiKey)
                                                .build();
        service.addResourceVersionOverride( resource1, version1 );
        service.addResourceVersionOverride( resource2, version2 );
        List<String> resourceOverrides = service.getOverriddenResources();
        assert( resourceOverrides.size() == 2 );
        assert( resourceOverrides.contains(resource1) );
        assert( resourceOverrides.contains(resource2) );
        assert( service.getOverriddenResourceVersion(resource1).equals(version1) );
        assert( service.getOverriddenResourceVersion(resource2).equals(version2) );
    }

    @Test
    public void addResourceAbbreviatedVersionOverrideTest() {
        String resource1 = "someResource";
        String resource2 = "anotherResource";
        String version1  = "v1";
        String version2  = "2";
        String expectedVersion1 = EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, "1" );
        String expectedVersion2 = EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, "2" );
        String apiKey    = "11111111-1111-1111-1111-111111111111";
        EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(apiKey)
                                                 .build();
        service.addResourceAbbreviatedVersionOverride( resource1, version1 );
        service.addResourceAbbreviatedVersionOverride( resource2, version2 );
        List<String> resourceOverrides = service.getOverriddenResources();
        assert( resourceOverrides.size() == 2 );
        assert( resourceOverrides.contains(resource1) );
        assert( resourceOverrides.contains(resource2) );
        assert( service.getOverriddenResourceVersion(resource1).equals(expectedVersion1) );
        assert( service.getOverriddenResourceVersion(resource2).equals(expectedVersion2) );
    }

    @Test
    public void addResourceVersionOverrideThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            String apiKey = "11111111-1111-1111-1111-111111111111";
            EthosClientBuilder ethosClientBuilder = new EthosClientBuilder(apiKey);
            EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(ethosClientBuilder)
                                                    .build();
            service.addResourceVersionOverride( null, "someVersion" );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            String apiKey = "11111111-1111-1111-1111-111111111111";
            EthosClientBuilder ethosClientBuilder = new EthosClientBuilder(apiKey);
            EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(ethosClientBuilder)
                                                     .build();
            service.addResourceVersionOverride( "someResource", null );
        });
    }

    @Test
    public void addResourceAbbreviatedVersionOverrideThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            String apiKey = "11111111-1111-1111-1111-111111111111";
            EthosClientBuilder ethosClientBuilder = new EthosClientBuilder(apiKey);
            EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(ethosClientBuilder)
                    .build();
            service.addResourceAbbreviatedVersionOverride( "someResource", null );
        });
    }


    @Test
    public void removeResourceVersionOverrideTest() {
        String resource1 = "someResource";
        String resource2 = "anotherResource";
        String version1  = "someVersion1";
        String version2  = "someVersion2";
        String apiKey = "11111111-1111-1111-1111-111111111111";
        EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(apiKey)
                                                .withResourceVersionOverride(resource1, version1)
                                                .withResourceVersionOverride(resource2, version2)
                                                .build();
        List<String> resourceOverrides = service.getOverriddenResources();
        assert( resourceOverrides.size() == 2 );
        assert( resourceOverrides.contains(resource1) );
        assert( resourceOverrides.contains(resource2) );
        assert( service.getOverriddenResourceVersion(resource1).equals(version1) );
        assert( service.getOverriddenResourceVersion(resource2).equals(version2) );
        service.removeResourceVersionOverride( resource2 );
        resourceOverrides = service.getOverriddenResources();
        assert( resourceOverrides.contains(resource2) == false );
    }

    @Test
    public void getChangeNotificationsTest() throws Exception {
        // Setup the EthosChangeNotificationService to test.
        String apiKey = "11111111-1111-1111-1111-111111111111";
        EthosChangeNotificationService spyECNService = Mockito.spy( new EthosChangeNotificationService.Builder(apiKey).build() );
        spyECNService.ethosMessagesClient = mockEthosMessagesClient;

        // Setup the test data...
        List<ChangeNotification> cnList = getChangeNotificationsList();
        // Mock the ethosMessagesClient.
        doReturn(cnList).when(mockEthosMessagesClient).consume();

        // Execute the test.
        List<ChangeNotification> resultList = spyECNService.getChangeNotifications();

        // Examine the results.
        Mockito.verify(spyECNService, Mockito.times(cnList.size())).processChangeNotificationOverrides( any(ChangeNotification.class) );
        assert( cnList.size() == resultList.size() );
    }

    @Test
    public void getChangeNotificationsWithLimitTest() throws Exception {
        // Setup the EthosChangeNotificationService to test.
        String apiKey = "11111111-1111-1111-1111-111111111111";
        int limit = 40;
        EthosChangeNotificationService spyECNService = Mockito.spy( new EthosChangeNotificationService.Builder(apiKey).build() );
        spyECNService.ethosMessagesClient = mockEthosMessagesClient;

        // Setup the test data...
        List<ChangeNotification> cnList = getChangeNotificationsList();
        // Mock the ethosMessagesClient.
        doReturn(cnList).when(mockEthosMessagesClient).consumeWithLimit( limit );

        // Execute the test.
        List<ChangeNotification> resultList = spyECNService.getChangeNotifications( limit );

        // Examine the results.
        Mockito.verify(spyECNService, Mockito.times(cnList.size())).processChangeNotificationOverrides( any(ChangeNotification.class) );
        assert( cnList.size() == resultList.size() );
    }

    @Test
    public void processChangeNotificationOverridesWithoutOverridesTest() throws Exception {
        String apiKey = "11111111-1111-1111-1111-111111111111";
        // Setup the service without overriding any resources.
        EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(apiKey).build();
        ChangeNotification cn = ChangeNotificationFactory.createCNFromJson( getChangeNotification() );
        // Execute the test.
        ChangeNotification cnResult = service.processChangeNotificationOverrides( cn );
        // Examine the results.
        assert( cnResult != null );
        // Should not have been overridden, so should match.
        assert( cnResult.getContentType().equals(cn.getContentType()) );
        assert( cnResult.getContent().equals(cn.getContent()) );
        assert( cnResult.getResource().getVersion().equals(cn.getResource().getVersion()) );
    }

    @Test
    public void processChangeNotificationOverridesWithoutMatchingOverridesTest() throws Exception {
        String apiKey = "11111111-1111-1111-1111-111111111111";
        String resource = "sections";
        String version  = "v7";
        // Setup the service without overriding any resources.
        EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(apiKey)
                                                 .withResourceVersionOverride(resource, version)
                                                 .build();
        ChangeNotification cn = ChangeNotificationFactory.createCNFromJson( getChangeNotification() );
        // Execute the test.
        ChangeNotification cnResult = service.processChangeNotificationOverrides( cn );
        // Examine the results.
        assert( cnResult != null );
        // Should not have been overridden, so should match.
        assert( cnResult.getContentType().equals(cn.getContentType()) );
        assert( cnResult.getContent().equals(cn.getContent()) );
        assert( cnResult.getResource().getVersion().equals(cn.getResource().getVersion()) );
    }

    @Test
    public void processChangeNotificationOverridesWithDeletedOverrideTest() throws Exception {
        String apiKey = "11111111-1111-1111-1111-111111111111";
        String resource = "persons";
        String version  = "v7";
        // Setup the service without overriding any resources.
        EthosChangeNotificationService service = new EthosChangeNotificationService.Builder(apiKey)
                .withResourceVersionOverride(resource, version)
                .build();
        ChangeNotification cn = ChangeNotificationFactory.createCNFromJson( getDeletedChangeNotification() );
        // Execute the test.
        ChangeNotification cnResult = service.processChangeNotificationOverrides( cn );
        // Examine the results.
        assert( cnResult != null );
        // Should not have been overridden, so should match.
        assert( cnResult.getContentType().equals(cn.getContentType()) );
        assert( cnResult.getContent().equals(cn.getContent()) );
        assert( cnResult.getResource().getVersion().equals(cn.getResource().getVersion()) );
    }

    @Test
    public void processChangeNotificationOverridesWithOverridesTest() throws Exception {
        String apiKey = "11111111-1111-1111-1111-111111111111";
        String resource = "persons";
        String version  = "v14";
        // Setup the service spy overriding a resource.
        EthosChangeNotificationService spyECNService = Mockito.spy( new EthosChangeNotificationService.Builder(apiKey)
                                                                    .withResourceAbbreviatedVersionOverride(resource, version)
                                                                    .build() );
        spyECNService.ethosProxyClient = mockEthosProxyClient;

        ChangeNotification cn = ChangeNotificationFactory.createCNFromJson( getChangeNotification() );
        EthosResponse ethosResponse = buildEthosResponse();
        // Mock the ethosProxyClient.
        doReturn(ethosResponse).when(mockEthosProxyClient).getById( cn.getResource().getName(),
                                                                    cn.getResource().getId(),
                                                                    ethosResponse.getHeader(EthosProxyClient.HDR_X_MEDIA_TYPE).getValue() );
        // Execute the test.
        ChangeNotification cnResult = spyECNService.processChangeNotificationOverrides( cn );
        // Examine the results.
        EthosResponseConverter ethosResponseConverter = new EthosResponseConverter();
        assert( cnResult != null );
        assert( cnResult.getContentType().equals("resource-representation") );
        assert( cnResult.getContent().toString().equals(ethosResponseConverter.toJsonNode(ethosResponse).toString()) );
        assert( cnResult.getResource().getVersion().equals(ethosResponse.getHeader(EthosProxyClient.HDR_X_MEDIA_TYPE).getValue()) );
    }


    private List<ChangeNotification> getChangeNotificationsList() {
        List<ChangeNotification> cnList = new ArrayList<>();
        try {
            ChangeNotification cn = ChangeNotificationFactory.createCNFromJson(getChangeNotification());
            cnList.add(cn);
            cn = ChangeNotificationFactory.createCNFromJson(getChangeNotification());
            cnList.add(cn);
            cn = ChangeNotificationFactory.createCNFromJson(getChangeNotification());
            cnList.add(cn);
        }
        catch( JsonProcessingException jpe ) {
            jpe.printStackTrace();
        }
        return cnList;
    }

    private String getChangeNotification() {
        return "{" +
                "    \"id\": 1," +
                "    \"published\": \"2020-10-30 12:00:00.000Z\"," +
                "    \"publisher\": {" +
                "        \"applicationName\": \"app1\"," +
                "        \"id\": \"" + UUID.randomUUID().toString() + "\"," +
                "        \"tenant\": {" +
                "            \"id\": \"" + UUID.randomUUID().toString() + "\"," +
                "            \"alias\": \"ellucian\"," +
                "            \"name\": \"university\"," +
                "            \"environment\": \"test\"" +
                "        }" +
                "    }," +
                "    \"resource\": {" +
                "        \"name\": \"persons\"," +
                "        \"id\": \"" + UUID.randomUUID().toString() + "\"," +
                "        \"version\": \"application/vnd.hedtech.integration.v12+json\"," +
                "        \"domain\": \"core\"" +
                "    }," +
                "    \"operation\": \"created\"," +
                "    \"contentType\": \"resource-representation\"," +
                "    \"content\": {" +
                "        \"id\": \"" + UUID.randomUUID().toString() + "\"" +
                "    }" +
                "}";
    }


    private String getDeletedChangeNotification() {
        return "{" +
                "    \"id\": 1," +
                "    \"published\": \"2020-10-30 12:00:00.000Z\"," +
                "    \"publisher\": {" +
                "        \"applicationName\": \"app1\"," +
                "        \"id\": \"" + UUID.randomUUID().toString() + "\"," +
                "        \"tenant\": {" +
                "            \"id\": \"" + UUID.randomUUID().toString() + "\"," +
                "            \"alias\": \"ellucian\"," +
                "            \"name\": \"university\"," +
                "            \"environment\": \"test\"" +
                "        }" +
                "    }," +
                "    \"resource\": {" +
                "        \"name\": \"persons\"," +
                "        \"id\": \"" + UUID.randomUUID().toString() + "\"," +
                "        \"version\": \"application/vnd.hedtech.integration.v12+json\"," +
                "        \"domain\": \"core\"" +
                "    }," +
                "    \"operation\": \"deleted\"," +
                "    \"contentType\": \"resource-representation\"," +
                "    \"content\": {" +
                "        \"id\": \"" + UUID.randomUUID().toString() + "\"" +
                "    }" +
                "}";
    }

    private EthosResponse buildEthosResponse() {
        BasicHeader header = new BasicHeader(EthosFilterQueryClient.HDR_X_MEDIA_TYPE, "application/vnd.hedtech.integration.v14+json");
        Map<String, Header> headerMap = new HashMap<>();
        headerMap.put( EthosFilterQueryClient.HDR_X_MEDIA_TYPE, header );
        return new EthosResponse( headerMap, "{\"someLabel\":\"someValue\"}", 200 );
    }

}
