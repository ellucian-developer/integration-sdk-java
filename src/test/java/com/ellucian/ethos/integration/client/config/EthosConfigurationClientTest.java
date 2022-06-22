/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.config;

import com.ellucian.ethos.integration.client.EthosResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class EthosConfigurationClientTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private EthosConfigurationClient spyEthosConfigurationClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    // ==========================================================================
    // Methods
    // ==========================================================================

    private EthosResponse buildEthosResponse() {
        return new EthosResponse(new HashMap<>(), "{\"someLabel\":\"someValue\"}", 200);
    }

    private JsonNode buildAvailableResourcesNode() throws IOException {
        String availableResourcesStr = "[\n" +
                "    {\n" +
                "        \"id\": \"1d6bd816-7018-49ff-8eea-af696688472e\",\n" +
                "        \"name\": \"Banner Integration Main\",\n" +
                "        \"resources\": [\n" +
                "            {\n" +
                "                \"name\": \"account-funds-available\",\n" +
                "                \"representations\": [\n" +
                "                    {\n" +
                "                        \"X-Media-Type\": \"application/vnd.hedtech.integration.v8+json\",\n" +
                "                        \"methods\": [\n" +
                "                            \"get\"\n" +
                "                        ],\n" +
                "                        \"version\": \"v8\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"X-Media-Type\": \"application/json\",\n" +
                "                        \"methods\": [\n" +
                "                            \"get\"\n" +
                "                        ]\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "\t\t\t{\n" +
                "                \"name\": \"accounting-strings\",\n" +
                "                \"representations\": [\n" +
                "                    {\n" +
                "                        \"X-Media-Type\": \"application/vnd.hedtech.integration.v7+json\",\n" +
                "                        \"methods\": [\n" +
                "                            \"get\"\n" +
                "                        ],\n" +
                "                        \"version\": \"v8\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"X-Media-Type\": \"application/json\",\n" +
                "                        \"methods\": [\n" +
                "                            \"get\"\n" +
                "                        ]\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "\t\t]\n" +
                "\t}\n" +
                "]";
        return objectMapper.readTree( availableResourcesStr );
    }

    @BeforeEach
    public void setup() {
        // Not using the EthosClientFactory to build the EthosConfigurationClient because we have to spy it with Mockito.
        spyEthosConfigurationClient = Mockito.spy( new EthosConfigurationClient("11111111-1111-1111-1111-111111111111", null, null, null) );
    }

    @Test
    public void getAppConfigTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosConfigurationClient).get(anyString(), anyMap() );
        // Run the test.
        String result = spyEthosConfigurationClient.getAppConfig();
        // Examine the results.
        assert( result !=  null );
        assert( result.isBlank() == false );
        assert( result.equals(expectedResponse.getContent()) );
    }

    @Test
    public void getAppConfigJsonTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the expectedEthosResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosConfigurationClient).get(anyString(), anyMap() );
        // Run the test.
        JsonNode resultNode = spyEthosConfigurationClient.getAppConfigJson();
        // Examine the results.
        assert( resultNode !=  null );
        assert( resultNode.toString() != null );
        assert( resultNode.toString().isBlank() == false );
        assert( resultNode.toString().equals(expectedResponse.getContent()) );
    }

    @Test
    public void getAllAvailableResourcesTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosConfigurationClient).get(anyString(), anyMap() );
        // Run the test.
        String result = spyEthosConfigurationClient.getAllAvailableResources();
        // Examine the results.
        assert( result !=  null );
        assert( result.isBlank() == false );
        assert( result.equals(expectedResponse.getContent()) );
    }

    @Test
    public void getAllAvailableResourcesAsJsonTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosConfigurationClient).get(anyString(), anyMap() );
        // Run the test.
        JsonNode resultNode = spyEthosConfigurationClient.getAllAvailableResourcesAsJson();
        // Examine the results.
        assert( resultNode !=  null );
        assert( resultNode.toString() != null );
        assert( resultNode.toString().isBlank() == false );
        assert( resultNode.toString().equals(expectedResponse.getContent()) );
    }

    @Test
    public void getAllAvailableResourcesForAppAsJsonTest() throws IOException {
        String ownerOverridesStr = "{\"ownerOverrides\": [\n" +
                                   "        {\n" +
                                   "            \"resourceName\": \"account-funds-available\",\n" +
                                   "            \"applicationId\": \"1d6bd816-7018-49ff-8eea-af696688472e\"\n" +
                                   "        }\n" +
                                   "    ]}";

        JsonNode ownerOverridesNode = objectMapper.readTree( ownerOverridesStr );
        JsonNode availableResourcesNode = buildAvailableResourcesNode();

        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(ownerOverridesNode).when(spyEthosConfigurationClient).getAppConfigJson();
        Mockito.doReturn(availableResourcesNode).when(spyEthosConfigurationClient).getAllAvailableResourcesAsJson();

        // Run the test.
        JsonNode resultNode = spyEthosConfigurationClient.getAvailableResourcesForAppAsJson();
        // Examine the results.
        assert( resultNode !=  null );
        assert( resultNode.toString() != null );
        assert( resultNode.toString().isBlank() == false );
        assert( resultNode.size() == 1 );
        Iterator<JsonNode> overridesNodeIterator = ownerOverridesNode.at("/ownerOverrides").iterator();
        while( overridesNodeIterator.hasNext() ) {
            JsonNode jsonNode = overridesNodeIterator.next();
            String resourceName = jsonNode.at("/resourceName").asText();
            boolean resourceFound = false;
            Iterator<JsonNode> resourceNodeIter = resultNode.iterator();
            while( resourceNodeIter.hasNext() ) {
                JsonNode resourceNode = resourceNodeIter.next();
                if( resourceName.equals(resourceNode.at("/name").asText()) ) {
                    resourceFound = true;
                    break;
                }
            }
            assert( resourceFound );
        }
    }

    @Test
    public void getAvailableResourcesForAppTest() throws Exception {
        String availableResourcesStr = "[{\"id\":\"1d6bd816-7018-49ff-8eea-af696688472e\",\"name\":\"Banner Integration Main\",\"resources\":[{\"name\":\"account-funds-available\"}]}]";
        JsonNode availableResourcesNode = objectMapper.readTree( availableResourcesStr );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(availableResourcesNode).when(spyEthosConfigurationClient).getAvailableResourcesForAppAsJson();
        // Run the test.
        String result = spyEthosConfigurationClient.getAvailableResourcesForApp();
        // Examine the results.
        assert( result != null );
        assert( result.isBlank() == false );
        assert( result.equals(availableResourcesStr) );
    }

    @Test
    public void getResourceDetailsThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
           spyEthosConfigurationClient.getResourceDetails( null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getResourceDetails( "" );
        });
    }

    @Test
    public void getResourceDetailsTest() throws Exception {
        String resourceName = "account-funds-available";
        String availableResourcesStr = "[{\"id\":\"1d6bd816-7018-49ff-8eea-af696688472e\",\"name\":\"Banner Integration Main\",\"resources\":[{\"name\":\"account-funds-available\"}]}]";
        JsonNode availableResourcesNode = objectMapper.readTree( availableResourcesStr );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(availableResourcesNode).when(spyEthosConfigurationClient).getResourceDetailsAsJson( resourceName );
        // Run the test.
        String result = spyEthosConfigurationClient.getResourceDetails( resourceName );
        // Examine the results.
        assert( result != null );
        assert( result.isBlank() == false );
        assert( result.equals(availableResourcesStr) );
    }

    @Test
    public void getResourceDetailsAsJsonThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getResourceDetailsAsJson( null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getResourceDetailsAsJson( "" );
        });
        Assertions.assertThrows(EthosResourceNotFoundException.class, () -> {
            String resourceName = "someResource";
            JsonNode emptyResourceNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
            ArrayNode emptyFilteredArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
            // Return the mock node objects when the method under test calls the methods to be mocked.
            Mockito.doReturn(emptyResourceNode).when(spyEthosConfigurationClient).getAllAvailableResourcesAsJson();
            Mockito.doReturn(emptyFilteredArrayNode).when(spyEthosConfigurationClient).filterAvailableResources( emptyResourceNode, resourceName );
            // Run the test.
            spyEthosConfigurationClient.getResourceDetailsAsJson( resourceName );
        });
    }

    @Test
    public void getResourceDetailsAsJsonTest() throws Exception {
        String resourceName = "accounting-strings";
        JsonNode availableResourcesNode = buildAvailableResourcesNode();
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(availableResourcesNode).when(spyEthosConfigurationClient).getAllAvailableResourcesAsJson();
        // Run the test.
        ArrayNode resultNode = spyEthosConfigurationClient.getResourceDetailsAsJson( resourceName );
        // Examine the results.
        assert( resultNode != null );
        assert( resultNode.toString() != null );
        assert( resultNode.toString().isBlank() == false );
        assert( resultNode.size() == 1 );
        JsonNode jsonNode = resultNode.iterator().next(); // Do not need a while loop because the size of the arrayNode is 1.
        assert( resourceName.equals(jsonNode.at("/resource/name").asText()) );
    }

    @Test
    public void getMajorVersionsThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getMajorVersionsOfResource( null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getMajorVersionsOfResource( "" );
        });
    }

    @Test
    public void getMajorVersionsTest() throws IOException {
        String resourceName = "someResource";
        List<String> verList = new ArrayList<>();
        verList.add( "v1.0.0" );
        verList.add( "v1.1.0" );
        verList.add( "v1.1.1" );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(verList).when(spyEthosConfigurationClient).getVersionsOfResourceAsStrings( resourceName );
        // Run the test.
        List<String> resultList = spyEthosConfigurationClient.getMajorVersionsOfResource( resourceName );
        // Examine the results.
        assert( resultList != null );
        assert( resultList.isEmpty() == false );
        assert( resultList.size() == 1 );
        assert( resultList.get(0) != null );
        assert( resultList.get(0).equals(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1")) );
    }

//    @Test
//    public void getMajorSemVersThrowsExceptionForInvalidInputTest() {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            spyEthosConfigurationClient.getMajorSemVers( null );
//        });
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            spyEthosConfigurationClient.getMajorSemVers( "" );
//        });
//    }

//    @Test
//    public void getMajorSemVersTest() throws IOException {
//        String resourceName = "someResource";
//        List<String> versionList = new ArrayList<>();
//        versionList.add( "v3" );
//        versionList.add( "v3.4" );
//        versionList.add( "v5.2.1" );
//        // Return the mock node objects when the method under test calls the methods to be mocked.
//        Mockito.doReturn(versionList).when(spyEthosConfigurationClient).getVersionsOfResource( resourceName );
//        // Run the test.
//        List<SemVer> resultList = spyEthosConfigurationClient.getMajorSemVers( resourceName );
//        // Examine the results.
//        assert( resultList != null );
//        assert( resultList.size() == 2 );
//        assert( resultList.get(0).equals(new SemVer.Builder("3.0.0").build()) );
//        assert( resultList.get(1).equals(new SemVer.Builder("5.0.0").build()) );
//    }

//    @Test
//    public void getCurrentVersionThrowsExceptionForInvalidInputTest() {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            spyEthosConfigurationClient.getCurrentVersion( null );
//        });
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            spyEthosConfigurationClient.getCurrentVersion( "" );
//        });
//    }
//
//    @Test
//    public void getCurrentVersionTest() throws IOException {
//        String resourceName = "someResource";
//        String semVerStr = "1.1.1";
//        String expectedVersion = EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, semVerStr);
//        // Return the mock node objects when the method under test calls the methods to be mocked.
//        Mockito.doReturn(semVerStr).when(spyEthosConfigurationClient).getCurrentSemVerAsString( resourceName );
//        // Run the test.
//        String result = spyEthosConfigurationClient.getCurrentVersion( resourceName );
//        // Examine the results.
//        assert( result != null );
//        assert( result.equals(expectedVersion) );
//    }

//    @Test
//    public void getCurrentSemVerThrowsExceptionForInvalidInputTest() {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            spyEthosConfigurationClient.getCurrentSemVer( null );
//        });
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            spyEthosConfigurationClient.getCurrentSemVer( "" );
//        });
//    }
//
//    @Test
//    public void getCurrentSemVerTest() throws IOException {
//        String resourceName = "someResource";
//        SemVer expectedSemVer = new SemVer.Builder("5.2.1").build();
//        List<String> versionList = new ArrayList<>();
//        versionList.add( "v3" );
//        versionList.add( "v3.4" );
//        versionList.add( "v5.2.1" );
//        // Return the mock node objects when the method under test calls the methods to be mocked.
//        Mockito.doReturn(versionList).when(spyEthosConfigurationClient).getVersionsOfResource( resourceName );
//        // Run the test.
//        SemVer result = spyEthosConfigurationClient.getCurrentSemVer( resourceName );
//        assert( result != null );
//        assert( result.equals(expectedSemVer) );
//    }

//    @Test
//    public void getCurrentSemVerAsStringThrowsExceptionForInvalidInputTest() {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            spyEthosConfigurationClient.getCurrentSemVerAsString( null );
//        });
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            spyEthosConfigurationClient.getCurrentSemVerAsString( "" );
//        });
//    }
//
//    @Test
//    public void getCurrentSemVerAsStringTest() throws IOException {
//        String resourceName = "someReource";
//        SemVer expectedSemVer = new SemVer.Builder("5.2.1").build();
//        // Return the mock node objects when the method under test calls the methods to be mocked.
//        Mockito.doReturn(expectedSemVer).when(spyEthosConfigurationClient).getCurrentSemVer( resourceName );
//        // Run the test.
//        String result = spyEthosConfigurationClient.getCurrentSemVerAsString( resourceName );
//        assert( result != null );
//        assert( result.equals(expectedSemVer.toString()) );
//    }

    @Test
    public void isResourceVersionSupportedMajorVersionThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( null, 1 );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "", 1 );
        });
    }

    @Test
    public void isResourceVersionSupportedMajorVersionTest() throws IOException {
        String resourceName = "someResource";
        List<String> versionList = new ArrayList<>();
        versionList.add( "v3" );
        versionList.add( "v3.4" );
        versionList.add( "v5.2.1" );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(versionList).when(spyEthosConfigurationClient).getVersionsOfResourceAsStrings( resourceName );
        // Run the test.
        boolean isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, 3 );
        assert( isSupported );
        // Run again with a version not supported.
        isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, 5 );
        assert( !isSupported );
    }

    @Test
    public void isResourceVersionSupportedMajorMinorVersionThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( null, 1, 2 );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "", 1, 2 );
        });
    }

    @Test
    public void isResourceVersionSupportedMajorMinorVersionTest() throws IOException {
        String resourceName = "someResource";
        List<String> versionList = new ArrayList<>();
        versionList.add( "v3" );
        versionList.add( "v3.4" );
        versionList.add( "v5.2.1" );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(versionList).when(spyEthosConfigurationClient).getVersionsOfResourceAsStrings( resourceName );
        // Run the test.
        boolean isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, 3, 4 );
        assert( isSupported );
        // Run again with a version not supported.
        isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, 5, 2 );
        assert( !isSupported );
    }

    @Test
    public void isResourceVersionSupportedMajorMinorPatchVersionThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( null, 1, 2, 3 );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "", 1, 2, 3 );
        });
    }

    @Test
    public void isResourceVersionSupportedMajorMinorPatchVersionTest() throws IOException {
        String resourceName = "someResource";
        List<String> versionList = new ArrayList<>();
        versionList.add( "v3" );
        versionList.add( "v3.4" );
        versionList.add( "v5.2.1" );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(versionList).when(spyEthosConfigurationClient).getVersionsOfResourceAsStrings( resourceName );
        // Run the test.
        boolean isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, 5, 2, 1 );
        assert( isSupported );
        // Run again with a version not supported.
        isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, 3, 4, 0 );
        assert( !isSupported );
    }

    @Test
    public void isResourceVersionSupportedFullVersionThrowsExceptionForInvalidInputTest() {
        String nullfullVersion = null;
        String emptyFullVersion = "";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( null, "someFullVersion" );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "", "someFullVersion" );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "someResource", nullfullVersion );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "someResource", "" );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( null, nullfullVersion );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "", "" );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( null, emptyFullVersion );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "", nullfullVersion );
        });
    }

    @Test
    public void isResourceVersionSupportedFullVersionTest() throws IOException {
        String resourceName = "someResource";
        String someFullVersionHeader = EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, "3.4" );
        List<String> versionHeaderList = new ArrayList<>();
        versionHeaderList.add( EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, "3" ) );
        versionHeaderList.add( EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, "3.4" ) );
        versionHeaderList.add( EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, "5.2.1" ) );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(versionHeaderList).when(spyEthosConfigurationClient).getVersionHeadersOfResourceAsStrings( resourceName );
        // Run the test.
        boolean isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, someFullVersionHeader );
        assert( isSupported );
        // Run again with a version not supported.
        someFullVersionHeader = EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, "5.2" );
        isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, someFullVersionHeader );
        assert( !isSupported );
    }

    @Test
    public void isResourceVersionSupportedWithSemVerThrowsExceptionForInvalidInputTest() {
        SemVer nullSemVer = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( null, new SemVer.Builder("1.1.1").build() );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "", new SemVer.Builder("1.1.1").build() );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "someResource", nullSemVer );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "", nullSemVer );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( null, nullSemVer );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.isResourceVersionSupported( "", nullSemVer );
        });
    }

    @Test
    public void isResourceVersionSupportedWithSemVerTest() throws IOException {
        String resourceName = "someResource";
        List<String> versionValueList = new ArrayList<>();
        versionValueList.add( "3.0.1" );
        versionValueList.add( "3.4" );
        versionValueList.add( "5.2.1" );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(versionValueList).when(spyEthosConfigurationClient).getVersionsOfResourceAsStrings( resourceName );
        // Run the test.
        boolean isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, new SemVer.Builder("3.4.1").build() );
        assert( !isSupported );
        // Run again testing a whole version to a major semVer, should not be supported.
        isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, new SemVer.Builder("3").build() );
        assert( !isSupported );
        // Run again testing a SemVer version that should be supported.
        isSupported = spyEthosConfigurationClient.isResourceVersionSupported( resourceName, new SemVer.Builder("5.2.1").build() );
        assert( isSupported );
    }

    @Test
    public void getVersionHeaderForMajorVersionThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( null, 1 );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( "", 1 );
        });
    }

    @Test
    public void getVersionHeaderForMajorVersionTest() throws IOException {
        String resourceName = "someResource";
        int unsupportedVersion = 2;
        int supportedVersion = 1;
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(false).when(spyEthosConfigurationClient).isResourceVersionSupported( resourceName, unsupportedVersion );
        Mockito.doReturn(true).when(spyEthosConfigurationClient).isResourceVersionSupported( resourceName, supportedVersion );
        // Run the test.
        Assertions.assertThrows(UnsupportedVersionException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( resourceName, unsupportedVersion );
        });
        // Run again with a supported version.
        String versionHeader = spyEthosConfigurationClient.getVersionHeader( resourceName, supportedVersion );
        assert( versionHeader != null );
        assert( versionHeader.equals(EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, String.valueOf(supportedVersion))) );
    }

    @Test
    public void getVersionHeaderForMajorMinorVersionThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( null, 1, 1 );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( "", 1, 1 );
        });
    }

    @Test
    public void getVersionHeaderForMajorMinorVersionTest() throws IOException {
        String resourceName = "someResource";
        int unsupportedMajorVersion = 2;
        int unsupportedMinorVersion = 1;
        int supportedMajorVersion = 1;
        int supportedMinorVersion = 1;
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(false).when(spyEthosConfigurationClient).isResourceVersionSupported( resourceName, unsupportedMajorVersion, unsupportedMinorVersion );
        Mockito.doReturn(true).when(spyEthosConfigurationClient).isResourceVersionSupported( resourceName, supportedMajorVersion, unsupportedMinorVersion );
        // Run the test.
        Assertions.assertThrows(UnsupportedVersionException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( resourceName, unsupportedMajorVersion, unsupportedMinorVersion );
        });
        // Run again with a supported version.
        String versionHeader = spyEthosConfigurationClient.getVersionHeader( resourceName, supportedMajorVersion, supportedMinorVersion );
        assert( versionHeader != null );
        assert( versionHeader.equals(EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, "1.1")) );
    }

    @Test
    public void getVersionHeaderForMajorMinorPatchVersionThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( null, 1, 1, 1 );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( "", 1, 1, 1 );
        });
    }

    @Test
    public void getVersionHeaderForMajorMinorPatchVersionTest() throws IOException {
        String resourceName = "someResource";
        int unsupportedMajorVersion = 2;
        int unsupportedMinorVersion = 1;
        int unsupportedPatchVersion = 1;
        int supportedMajorVersion = 1;
        int supportedMinorVersion = 1;
        int supportedPatchVersion = 1;
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(false).when(spyEthosConfigurationClient).isResourceVersionSupported( resourceName, unsupportedMajorVersion, unsupportedMinorVersion, unsupportedPatchVersion );
        Mockito.doReturn(true).when(spyEthosConfigurationClient).isResourceVersionSupported( resourceName, supportedMajorVersion, unsupportedMinorVersion, supportedPatchVersion );
        // Run the test.
        Assertions.assertThrows(UnsupportedVersionException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( resourceName, unsupportedMajorVersion, unsupportedMinorVersion, unsupportedPatchVersion );
        });
        // Run again with a supported version.
        String versionHeader = spyEthosConfigurationClient.getVersionHeader( resourceName, supportedMajorVersion, supportedMinorVersion, supportedPatchVersion );
        assert( versionHeader != null );
        assert( versionHeader.equals(EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, "1.1.1")) );
    }

    @Test
    public void getVersionHeaderForSemVerThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( null, null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( "", null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( "someResource", null );
        });
    }

    @Test
    public void getVersionHeaderForSemVerVersionTest() throws IOException {
        String resourceName = "someResource";
        SemVer unsupportedSemVer = new SemVer.Builder("2.1.1" ).build();
        SemVer supportedSemVer = new SemVer.Builder("1.1.1" ).build();
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(false).when(spyEthosConfigurationClient).isResourceVersionSupported( resourceName, unsupportedSemVer );
        Mockito.doReturn(true).when(spyEthosConfigurationClient).isResourceVersionSupported( resourceName, supportedSemVer );
        // Run the test.
        Assertions.assertThrows(UnsupportedVersionException.class, () -> {
            spyEthosConfigurationClient.getVersionHeader( resourceName, unsupportedSemVer );
        });
        // Run again with a supported version.
        String versionHeader = spyEthosConfigurationClient.getVersionHeader( resourceName, supportedSemVer );
        assert( versionHeader != null );
        assert( versionHeader.equals(EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, supportedSemVer.toString())) );
    }

    @Test
    public void getVersionsOfResourceThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionsOfResource( null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionsOfResource( "" );
        });
    }

    @Test
    public void getVersionsOfResourceVersionTest() throws IOException {
        String resourceName = "someResource";
        ArrayNode expectedArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        ObjectNode filteredNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        ArrayNode versionArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_APPID, "myAppId");
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_APPNAME, "myAppName");
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_RESOURCENAME, "someResourceName");
        versionArrayNode = filteredNode.putArray( EthosConfigurationClient.JSON_SETTER_VERSIONS );
        versionArrayNode.add( "v1" );
        versionArrayNode.add( "v1.1" );
        versionArrayNode.add( "v1.1.1" );
        expectedArrayNode.add( filteredNode );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(expectedArrayNode).when(spyEthosConfigurationClient).getResourceVersionsByRepresentationType( resourceName, EthosConfigurationClient.JSON_ACCESSOR_VERSION );
        // Run the test.
        ArrayNode resultNode = spyEthosConfigurationClient.getVersionsOfResource( resourceName );
        assert( resultNode != null );
        assert( resultNode.size() == 1 );
        JsonNode jsonNode = resultNode.iterator().next();
        assert( jsonNode != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPID).asText() != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPID).asText().equals("myAppId") );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPNAME).asText() != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPNAME).asText().equals("myAppName") );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS) != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).size() == 3 );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(0).asText() != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(0).asText().equals("v1") );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(1).asText() != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(1).asText().equals("v1.1") );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(2).asText() != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(2).asText().equals("v1.1.1") );
    }

    @Test
    public void getVersionHeadersOfResourceThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeadersOfResource( null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getVersionHeadersOfResource( "" );
        });
    }

    @Test
    public void getVersionHeadersOfResourceVersionTest() throws IOException {
        String resourceName = "someResource";
        ArrayNode expectedArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        ObjectNode filteredNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        ArrayNode versionArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_APPID, "myAppId");
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_APPNAME, "myAppName");
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_RESOURCENAME, "someResourceName");
        versionArrayNode = filteredNode.putArray( EthosConfigurationClient.JSON_SETTER_VERSIONS );
        versionArrayNode.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1") );
        versionArrayNode.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1") );
        versionArrayNode.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1.1") );
        expectedArrayNode.add( filteredNode );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(expectedArrayNode).when(spyEthosConfigurationClient).getResourceVersionsByRepresentationType( resourceName, EthosConfigurationClient.JSON_ACCESSOR_VERSION );
        // Run the test.
        ArrayNode resultNode = spyEthosConfigurationClient.getVersionsOfResource( resourceName );
        assert( resultNode != null );
        assert( resultNode.size() == 1 );
        JsonNode jsonNode = resultNode.iterator().next();
        assert( jsonNode != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPID).asText() != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPID).asText().equals("myAppId") );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPNAME).asText() != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_APPNAME).asText().equals("myAppName") );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS) != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).size() == 3 );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(0).asText() != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(0).asText().equals(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1")) );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(1).asText() != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(1).asText().equals(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1")) );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(2).asText() != null );
        assert( jsonNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSIONS).get(2).asText().equals(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1.1")) );
    }

    @Test
    public void getVersionOfResourceAsStringsVersionTest() throws IOException {
        String resourceName = "someResource";
        ArrayNode expectedArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        ObjectNode filteredNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        ArrayNode versionArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_APPID, "myAppId");
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_APPNAME, "myAppName");
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_RESOURCENAME, "someResourceName");
        versionArrayNode = filteredNode.putArray( EthosConfigurationClient.JSON_SETTER_VERSIONS );
        versionArrayNode.add( "v1" );
        versionArrayNode.add( "v1.1" );
        versionArrayNode.add( "v1.1" ); // Add a duplicate value, which should be removed in the result.
        versionArrayNode.add( "v1.1.1" );
        expectedArrayNode.add( filteredNode );
        List<String> expectedVersionList = new ArrayList<>();
        expectedVersionList.add( "v1" );
        expectedVersionList.add( "v1.1" );
        expectedVersionList.add( "v1.1.1" );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(expectedArrayNode).when(spyEthosConfigurationClient).getVersionsOfResource( resourceName );
        Mockito.doReturn(expectedVersionList).when(spyEthosConfigurationClient).getVersionList( expectedArrayNode, EthosConfigurationClient.JSON_ACCESSOR_VERSIONS );
        // Run the test.
        List<String> resultList = spyEthosConfigurationClient.getVersionsOfResourceAsStrings( resourceName );
        assert( resultList != null );
        assert( resultList.size() == 3 );
        assert( resultList.contains("v1") );
        assert( resultList.contains("v1.1") );
        assert( resultList.contains("v1.1.1") );
    }

    @Test
    public void getVersionHeadersOfResourceAsStringsVersionTest() throws IOException {
        String resourceName = "someResource";
        ArrayNode expectedArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        ObjectNode filteredNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        ArrayNode versionArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_APPID, "myAppId");
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_APPNAME, "myAppName");
        filteredNode.put(EthosConfigurationClient.JSON_SETTER_RESOURCENAME, "someResourceName");
        versionArrayNode = filteredNode.putArray( EthosConfigurationClient.JSON_SETTER_VERSIONS );
        versionArrayNode.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1") );
        versionArrayNode.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1") );
        // Add a duplicate value, which should be removed in the result.
        versionArrayNode.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1") );
        versionArrayNode.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1.1") );
        expectedArrayNode.add( filteredNode );
        List<String> expectedVersionList = new ArrayList<>();
        expectedVersionList.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1") );
        expectedVersionList.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1") );
        expectedVersionList.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1.1") );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(expectedArrayNode).when(spyEthosConfigurationClient).getVersionHeadersOfResource( resourceName );
        Mockito.doReturn(expectedVersionList).when(spyEthosConfigurationClient).getVersionList( expectedArrayNode, EthosConfigurationClient.JSON_ACCESSOR_VERSIONS );
        // Run the test.
        List<String> resultList = spyEthosConfigurationClient.getVersionHeadersOfResourceAsStrings( resourceName );
        assert( resultList != null );
        assert( resultList.size() == 3 );
        assert( resultList.contains(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1")) );
        assert( resultList.contains(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1")) );
        assert( resultList.contains(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1.1")) );
    }


    @Test
    public void getVersionListTest() throws IOException {
        ArrayNode expectedArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        ObjectNode objectNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        objectNode.put(EthosConfigurationClient.JSON_SETTER_APPID, "myAppId");
        objectNode.put(EthosConfigurationClient.JSON_SETTER_APPNAME, "myAppName");
        objectNode.put(EthosConfigurationClient.JSON_SETTER_RESOURCENAME, "someResourceName");
        ArrayNode versionArrayNode = objectNode.putArray( EthosConfigurationClient.JSON_SETTER_VERSIONS );
        versionArrayNode.add( "1" );
        versionArrayNode.add( "1.1" );
        versionArrayNode.add( "1.1.1" );
        expectedArrayNode.add( objectNode );
        // Run the test.
        List<String> resultList = spyEthosConfigurationClient.getVersionList( expectedArrayNode, EthosConfigurationClient.JSON_ACCESSOR_VERSIONS );
        assert( resultList != null );
        assert( resultList.size() == 3 );
        assert( resultList.get(0).equals("1") );
        assert( resultList.get(1).equals("1.1") );
        assert( resultList.get(2).equals("1.1.1") );

        // Rebuild the input, remove the versions array and add one for X-Media-Type, and run again for version headers.
        objectNode.remove( EthosConfigurationClient.JSON_SETTER_VERSIONS );
        versionArrayNode = objectNode.putArray( "X-Media-Type" );
        versionArrayNode.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1") );
        versionArrayNode.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1") );
        versionArrayNode.add( EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1.1") );
        // Run again for version headers.
        resultList = spyEthosConfigurationClient.getVersionList( expectedArrayNode, EthosConfigurationClient.JSON_ACCESSOR_XMEDIATYPE );
        assert( resultList != null );
        assert( resultList.size() == 3 );
        assert( resultList.get(0).equals(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1") ) );
        assert( resultList.get(1).equals(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1")) );
        assert( resultList.get(2).equals(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1.1.1")) );
    }


    @Test
    public void getLatestVersionThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getLatestVersion( null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getLatestVersion( "" );
        });
    }


    @Test
    public void getLatestVersionTest() throws IOException {
        String resourceName = "someResource";
        List<String> versionList = new ArrayList<>();

        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(versionList).when(spyEthosConfigurationClient).getVersionsOfResourceAsStrings( resourceName );

        // Run again with no versions.
        String expectedResult = "application/json";
        versionList.clear();
        String latestVersion = spyEthosConfigurationClient.getLatestVersion( resourceName );
        // Assert the results.
        assert( latestVersion != null );
        assert( latestVersion.isBlank() == false );
        assert( latestVersion.equals(expectedResult) );

        // Run again without semantic versions.
        expectedResult = "4";
        versionList.clear();
        versionList.add( "v1" );
        versionList.add( "v2" );
        versionList.add( "v4" );
        versionList.add( "v3" );
        latestVersion = spyEthosConfigurationClient.getLatestVersion( resourceName );
        // Assert the results.
        assert( latestVersion != null );
        assert( latestVersion.isBlank() == false );
        assert( latestVersion.equals(expectedResult) );

        // Run again without non-semantic versions.
        expectedResult = "1.1.1";
        versionList.clear();
        versionList.add( "v1.0.0" );
        versionList.add( "v1.1.1" );
        versionList.add( "v1.0.1" );
        versionList.add( "v1.1.0" );
        latestVersion = spyEthosConfigurationClient.getLatestVersion( resourceName );
        // Assert the results.
        assert( latestVersion != null );
        assert( latestVersion.isBlank() == false );
        assert( latestVersion.equals(expectedResult) );

        // Run the test where semantic version is the latest.
        expectedResult = "2.1.1";
        versionList.add( "v1" );
        versionList.add( "v2" );
        versionList.add( "v2.0.0" );
        versionList.add( "v2.0.1" );
        versionList.add( "v2.1.1" );
        versionList.add( "v2.1.0" );
        latestVersion = spyEthosConfigurationClient.getLatestVersion( resourceName );
        // Assert the results.
        assert( latestVersion != null );
        assert( latestVersion.isBlank() == false );
        assert( latestVersion.equals(expectedResult) );

        // Run the test where non-semantic version is the latest.
        expectedResult = "3";
        versionList.add( "v1" );
        versionList.add( "v3" );
        versionList.add( "v2.0.0" );
        versionList.add( "v2.0.1" );
        versionList.add( "v2.1.1" );
        versionList.add( "v2.1.0" );
        latestVersion = spyEthosConfigurationClient.getLatestVersion( resourceName );
        // Assert the results.
        assert( latestVersion != null );
        assert( latestVersion.isBlank() == false );
        assert( latestVersion.equals(expectedResult) );
    }


    @Test
    public void getLatestVersionHeaderThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getLatestVersionHeader( null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getLatestVersionHeader( "" );
        });
    }


    @Test
    public void getLatestVersionHeaderTest() throws IOException {
        String resourceName = "someResource";
        String expectedLatestVersion = "2.1.1";
        String expectedResult = EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, expectedLatestVersion );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(expectedLatestVersion).when(spyEthosConfigurationClient).getLatestVersion( resourceName );
        // Run the test.
        String latestVersion = spyEthosConfigurationClient.getLatestVersionHeader( resourceName );
        // Assert and verify the results.
        Mockito.verify(spyEthosConfigurationClient, Mockito.times(1)).getLatestVersion( resourceName );
        assert( latestVersion != null );
        assert( latestVersion.isBlank() == false );
        assert( latestVersion.equals(expectedResult) );
    }


    @Test
    public void getFiltersAndNamedQueriesTest() throws IOException {
        String resourceName = "someResource";
        JsonNode expectedNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(expectedNode).when(spyEthosConfigurationClient).getFiltersAndNamedQueries( resourceName, null );
        // Run the test.
        JsonNode resultNode = spyEthosConfigurationClient.getFiltersAndNamedQueries( resourceName );
        // Assert and verify the results.
        Mockito.verify(spyEthosConfigurationClient, Mockito.times(1)).getFiltersAndNamedQueries( resourceName, null );
        assert( resultNode != null );
    }


    @Test
    public void getFiltersAndNamedQueriesThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getFiltersAndNamedQueries( null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getFiltersAndNamedQueries( "" );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getFiltersAndNamedQueries( null, "someVersionHeader" );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getFiltersAndNamedQueries( "", "someVersionHeader" );
        });
    }


    @Test
    public void getFiltersAndNamedQueriesForResourceAndVersionTest() throws IOException {
        String resourceName = "someResource";
        String versionHeader = "application/vnd.hedtech.integration.v8+json";
        String appResourceStr = "[ {" +
                "  \"appId\" : \"b7bc3d67-5d69-4191-9744-36eb1eb4ba72\"," +
                "  \"appName\" : \"Banner Integration API\"," +
                "  \"resource\" : {" +
                "    \"name\" : \"persons\"," +
                "    \"representations\" : [ {" +
                "      \"filters\" : [ \"title\", \"firstName\", \"lastName\", \"role\" ]," +
                "      \"X-Media-Type\" : \"application/vnd.hedtech.integration.v6+json\"," +
                "      \"methods\" : [ \"get\", \"post\", \"put\" ]," +
                "      \"version\" : \"v6\"" +
                "    }, {" +
                "      \"filters\" : [ \"names.title\", \"names.firstName\", \"names.lastName\", \"roles.role\" ]," +
                "      \"X-Media-Type\" : \"application/vnd.hedtech.integration.v8+json\"," +
                "      \"methods\" : [ \"get\", \"post\", \"put\" ]," +
                "      \"namedQueries\" : [ {" +
                "        \"filters\" : [ \"personFilter\" ]," +
                "        \"name\" : \"personFilter\"" +
                "      } ]," +
                "      \"version\" : \"v8\"" +
                "    }" +
                "    ]" +
                "   }" +
                "  }" +
                "]";
        JsonNode appResourceArrayNode = objectMapper.readTree( appResourceStr );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(appResourceArrayNode).when(spyEthosConfigurationClient).getResourceDetailsAsJson( resourceName );
        Mockito.doReturn(versionHeader).when(spyEthosConfigurationClient).getLatestVersionHeader( resourceName );
        // Run the test.
        JsonNode resultNode = spyEthosConfigurationClient.getFiltersAndNamedQueries( resourceName, versionHeader );
        // Assert and verify the result.
        // Verify that getLatestVersionHeader was not called.
        Mockito.verify(spyEthosConfigurationClient, Mockito.times(0)).getLatestVersionHeader( resourceName );
        assert( resultNode != null );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_RESOURCENAME).asText() != null );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_RESOURCENAME).asText().equals(resourceName) );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSION).asText() != null );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSION).asText().equals(versionHeader) );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_NAMEDQUERIES).size() == 1 );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_NAMEDQUERIES).get(0).at(EthosConfigurationClient.JSON_ACCESSOR_NAME).asText() != null );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_NAMEDQUERIES).get(0).at(EthosConfigurationClient.JSON_ACCESSOR_NAME).asText().equals("personFilter") );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_NAMEDQUERIES).get(0).at(EthosConfigurationClient.JSON_ACCESSOR_FILTERS).size() == 1 );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_FILTERS).size() == 4 );

        // Run again but do not pass in a version header to use the latest version.
        resultNode = spyEthosConfigurationClient.getFiltersAndNamedQueries( resourceName );
        // Verify that getLatestVersionHeader was called 1 time.
        Mockito.verify(spyEthosConfigurationClient, Mockito.times(1)).getLatestVersionHeader( resourceName );
        assert( resultNode != null );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_RESOURCENAME).asText() != null );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_RESOURCENAME).asText().equals(resourceName) );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSION).asText() != null );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_VERSION).asText().equals(versionHeader) );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_NAMEDQUERIES).size() == 1 );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_NAMEDQUERIES).get(0).at(EthosConfigurationClient.JSON_ACCESSOR_NAME).asText() != null );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_NAMEDQUERIES).get(0).at(EthosConfigurationClient.JSON_ACCESSOR_NAME).asText().equals("personFilter") );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_NAMEDQUERIES).get(0).at(EthosConfigurationClient.JSON_ACCESSOR_FILTERS).size() == 1 );
        assert( resultNode.at(EthosConfigurationClient.JSON_ACCESSOR_FILTERS).size() == 4 );
    }


    @Test
    public void getFiltersTest() throws IOException {
        String resourceName = "someResource";
        List<String> filterList = new ArrayList();
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(filterList).when(spyEthosConfigurationClient).getFilters( resourceName, null );
        // Run the test.
        List<String> resultList = spyEthosConfigurationClient.getFilters( resourceName );
        // Assert and verify the results.
        Mockito.verify(spyEthosConfigurationClient, Mockito.times(1)).getFilters( resourceName, null );
        assert( resultList != null );
    }


    @Test
    public void getFiltersThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getFilters( null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getFilters( "" );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getFilters( null, "someVersionHeader" );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getFilters( "", "someVersionHeader" );
        });
    }


    @Test
    public void getFiltersForResourceAndVersionTest() throws IOException {
        String resourceName = "someResource";
        String versionHeader = "someVersionHeader";
        String resourceFiltersStr = "{\n" +
                                    "  \"resourceName\" : \"someResource\",\n" +
                                    "  \"version\" : \"application/vnd.hedtech.integration.v8+json\",\n" +
                                    "  \"namedQueries\" : [ {\n" +
                                    "    \"filters\" : [ \"personFilter\" ],\n" +
                                    "    \"name\" : \"personFilter\"\n" +
                                    "  } ],\n" +
                                    "  \"filters\" : [ \"names.title\", \"names.firstName\", \"names.lastName\", \"roles.role\" ]\n" +
                                    "}";
        JsonNode resourceFiltersNode = objectMapper.readTree( resourceFiltersStr );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(resourceFiltersNode).when(spyEthosConfigurationClient).getFiltersAndNamedQueries( resourceName, versionHeader );
        // Run the test.
        List<String> resultList = spyEthosConfigurationClient.getFilters( resourceName, versionHeader );
        // Assert and verify the result.
        // Verify that getLatestVersionHeader was not called.
        Mockito.verify(spyEthosConfigurationClient, Mockito.times(0)).getLatestVersionHeader( resourceName );
        assert( resultList != null );
        assert( resultList.size() == 4 );
        assert( resultList.contains("names.title") );
        assert( resultList.contains("names.firstName") );
        assert( resultList.contains("names.lastName") );
        assert( resultList.contains("roles.role") );

        // Run again without a version header.
        Mockito.doReturn(versionHeader).when(spyEthosConfigurationClient).getLatestVersionHeader( resourceName );
        // Run the test.
        resultList = spyEthosConfigurationClient.getFilters( resourceName, null );
        // Assert and verify the result.
        // Verify that getLatestVersionHeader was called once.
        Mockito.verify(spyEthosConfigurationClient, Mockito.times(1)).getLatestVersionHeader( resourceName );
        assert( resultList != null );
        assert( resultList.size() == 4 );
        assert( resultList.contains("names.title") );
        assert( resultList.contains("names.firstName") );
        assert( resultList.contains("names.lastName") );
        assert( resultList.contains("roles.role") );
    }


    @Test
    public void getNamedQueriesTest() throws IOException {
        String resourceName = "someResource";
        Map<String,List<String>> namedQueryMap = new HashMap<>();
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(namedQueryMap).when(spyEthosConfigurationClient).getNamedQueries( resourceName, null );
        // Run the test.
        Map<String,List<String>> resultMap = spyEthosConfigurationClient.getNamedQueries( resourceName );
        // Assert and verify the results.
        Mockito.verify(spyEthosConfigurationClient, Mockito.times(1)).getNamedQueries( resourceName, null );
        assert( resultMap != null );
    }


    @Test
    public void getNamedQueriesThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getNamedQueries( null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getNamedQueries( "" );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getNamedQueries( null, "someVersionHeader" );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getNamedQueries( "", "someVersionHeader" );
        });
    }


    @Test
    public void getNamedQueriesForResourceAndVersionTest() throws IOException {
        String resourceName = "someResource";
        String versionHeader = "someVersionHeader";
        String resourceFiltersStr = "{\n" +
                "  \"resourceName\" : \"someResource\",\n" +
                "  \"version\" : \"application/vnd.hedtech.integration.v8+json\",\n" +
                "  \"namedQueries\" : [ {\n" +
                "    \"filters\" : [ \"personFilter\" ],\n" +
                "    \"name\" : \"personFilter\"\n" +
                "  } ],\n" +
                "  \"filters\" : [ \"names.title\", \"names.firstName\", \"names.lastName\", \"roles.role\" ]\n" +
                "}";
        JsonNode resourceFiltersNode = objectMapper.readTree( resourceFiltersStr );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(resourceFiltersNode).when(spyEthosConfigurationClient).getFiltersAndNamedQueries( resourceName, versionHeader );
        // Run the test.
        Map<String, List<String>> resultMap = spyEthosConfigurationClient.getNamedQueries( resourceName, versionHeader );
        // Assert and verify the result.
        // Verify that getLatestVersionHeader was not called.
        Mockito.verify(spyEthosConfigurationClient, Mockito.times(0)).getLatestVersionHeader( resourceName );
        assert( resultMap != null );
        assert( resultMap.size() == 1 );
        assert( resultMap.containsKey("personFilter") );
        assert( resultMap.get("personFilter").size() == 1 );

        // Run again without a version header.
        Mockito.doReturn(versionHeader).when(spyEthosConfigurationClient).getLatestVersionHeader( resourceName );
        // Run the test.
        resultMap = spyEthosConfigurationClient.getNamedQueries( resourceName, null );
        // Assert and verify the result.
        // Verify that getLatestVersionHeader was called once.
        Mockito.verify(spyEthosConfigurationClient, Mockito.times(1)).getLatestVersionHeader( resourceName );
        assert( resultMap != null );
        assert( resultMap.size() == 1 );
        assert( resultMap.containsKey("personFilter") );
        assert( resultMap.get("personFilter").size() == 1 );
    }


    @Test
    public void filterAvailableResourcesTest() {
        // Build the availableResources node.
        ArrayNode availableResourcesNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        ObjectNode objectNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        ArrayNode resourcesNode = objectNode.putArray( "resources" );
        ObjectNode rscNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        rscNode.put( "name", "resource1" );
        resourcesNode.add( rscNode );
        rscNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        rscNode.put( "name", "resource2" );
        resourcesNode.add( rscNode );
        objectNode.put( "id", "someAppId" );
        availableResourcesNode.add( objectNode );
        // Build the desiredResourcesNode
        ArrayNode desiredResourcesNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        ObjectNode resourceAppNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        resourceAppNode.put( "resourceName", "resourceA" );
        resourceAppNode.put( "applicationId", "someOtherAppId" );
        desiredResourcesNode.add( resourceAppNode );
        resourceAppNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        resourceAppNode.put( "resourceName", "resource1" );
        resourceAppNode.put( "applicationId", "someAppId" );
        desiredResourcesNode.add( resourceAppNode );
        // Run the test.
        ArrayNode resultNode = spyEthosConfigurationClient.filterAvailableResources( availableResourcesNode, desiredResourcesNode );
        assert( resultNode != null );
        assert( resultNode.size() == 1 );
        assert( resultNode.get(0).at("/name") != null );
        assert( resultNode.get(0).at("/name").asText() != null );
        assert( resultNode.get(0).at("/name").asText().equals("resource1") );
        // Rebuild the desired node to have resources that should not be found.
        desiredResourcesNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        resourceAppNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        resourceAppNode.put( "resourceName", "resourceA" );
        resourceAppNode.put( "applicationId", "someOtherAppId" );
        desiredResourcesNode.add( resourceAppNode );
        resourceAppNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        resourceAppNode.put( "resourceName", "resourceB" );
        resourceAppNode.put( "applicationId", "someAppId" );
        desiredResourcesNode.add( resourceAppNode );

        // Run the test again, with a desired node that should not be found.
        resultNode = spyEthosConfigurationClient.filterAvailableResources( availableResourcesNode, desiredResourcesNode );
        assert( resultNode != null );
        assert( resultNode.isEmpty() );
    }


    @Test
    public void filterAvailableResourcesByResourceNameTest() {
        // Build the availableResources node.
        ArrayNode availableResourcesNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        ObjectNode objectNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        ArrayNode resourcesNode = objectNode.putArray( "resources" );
        ObjectNode rscNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        rscNode.put( "name", "resource1" );
        resourcesNode.add( rscNode );
        rscNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        rscNode.put( "name", "resource2" );
        resourcesNode.add( rscNode );
        objectNode.put( "id", "someAppId" );
        objectNode.put( "name", "someAppName" );
        availableResourcesNode.add( objectNode );
        // Run the test.
        ArrayNode resultNode = spyEthosConfigurationClient.filterAvailableResources( availableResourcesNode, "resource2" );
        assert( resultNode != null );
        assert( resultNode.size() == 1 );
        assert( resultNode.get(0).at("/appId") != null );
        assert( resultNode.get(0).at("/appId").asText() != null );
        assert( resultNode.get(0).at("/appId").asText().equals("someAppId") );
        assert( resultNode.get(0).at("/appName") != null );
        assert( resultNode.get(0).at("/appName").asText() != null );
        assert( resultNode.get(0).at("/appName").asText().equals("someAppName") );
        assert( resultNode.get(0).at("/resource/name") != null );
        assert( resultNode.get(0).at("/resource/name").asText() != null );
        assert( resultNode.get(0).at("/resource/name").asText().equals("resource2") );
        // Run again to find a resource not there.
        resultNode = spyEthosConfigurationClient.filterAvailableResources( availableResourcesNode, "resourceA" );
        assert( resultNode != null );
        assert( resultNode.isEmpty() );
    }

    @Test
    public void getResourceVersionsByRepresentationTypeThrowsExceptionForInvalidInputTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getResourceVersionsByRepresentationType( null, null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getResourceVersionsByRepresentationType( "", null );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getResourceVersionsByRepresentationType( null, EthosConfigurationClient.JSON_ACCESSOR_XMEDIATYPE );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getResourceVersionsByRepresentationType( "", EthosConfigurationClient.JSON_ACCESSOR_XMEDIATYPE );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            spyEthosConfigurationClient.getResourceVersionsByRepresentationType( "someResource", "someRepresentationType" );
        });
    }

    @Test
    public void getResourceVersionsByRepresentationTypeTest() throws IOException {
        String resourceName = "someResource";
        // Build the availableResources node.
        ArrayNode appResourceNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        ObjectNode objectNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        ObjectNode rscNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        ObjectNode versionNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        objectNode.put( EthosConfigurationClient.JSON_SETTER_APPID, "someAppId" );
        objectNode.put( EthosConfigurationClient.JSON_SETTER_APPNAME, "someAppName" );
        rscNode.put( "name", resourceName );
        ArrayNode representationsNode = rscNode.putArray( "representations" );
        versionNode.put( "X-Media-Type", EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1") );
        versionNode.put( "version", "v1" );
        representationsNode.add( versionNode );
        versionNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        versionNode.put( "X-Media-Type", EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "2") );
        versionNode.put( "version", "v2" );
        representationsNode.add( versionNode );
        objectNode.set( EthosConfigurationClient.JSON_SETTER_RESOURCE, rscNode );
        appResourceNode.add( objectNode );
        // Return the mock node objects when the method under test calls the methods to be mocked.
        Mockito.doReturn(appResourceNode).when(spyEthosConfigurationClient).getResourceDetailsAsJson( resourceName );
        // Run the test.
        ArrayNode resultNode = spyEthosConfigurationClient.getResourceVersionsByRepresentationType( resourceName, EthosConfigurationClient.JSON_ACCESSOR_XMEDIATYPE );
        assert( resultNode != null );
        assert( resultNode.size() == 1 );
        assert( resultNode.get(0).at("/appId") != null );
        assert( resultNode.get(0).at("/appId").asText() != null );
        assert( resultNode.get(0).at("/appId").asText().equals("someAppId") );
        assert( resultNode.get(0).at("/appName") != null );
        assert( resultNode.get(0).at("/appName").asText() != null );
        assert( resultNode.get(0).at("/appName").asText().equals("someAppName") );
        assert( resultNode.get(0).at("/resourceName") != null );
        assert( resultNode.get(0).at("/resourceName").asText() != null );
        assert( resultNode.get(0).at("/resourceName").asText().equals(resourceName) );
        assert( resultNode.get(0).at("/versions").size() == 2 );
        assert( resultNode.get(0).at("/versions").get(0).asText() != null );
        assert( resultNode.get(0).at("/versions").get(0).asText().equals(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "1")) );
        assert( resultNode.get(0).at("/versions").get(1).asText() != null );
        assert( resultNode.get(0).at("/versions").get(1).asText().equals(EthosConfigurationClient.FULL_VERSION.replace(EthosConfigurationClient.FULL_VERSION_TAG, "2")) );
        // Run the test again with version representationType.
        resultNode = spyEthosConfigurationClient.getResourceVersionsByRepresentationType( resourceName, EthosConfigurationClient.JSON_ACCESSOR_VERSION );
        assert( resultNode.get(0).at("/versions").size() == 2 );
        assert( resultNode.get(0).at("/versions").get(0).asText() != null );
        assert( resultNode.get(0).at("/versions").get(0).asText().equals("v1") );
        assert( resultNode.get(0).at("/versions").get(1).asText() != null );
        assert( resultNode.get(0).at("/versions").get(1).asText().equals("v2") );
    }



}