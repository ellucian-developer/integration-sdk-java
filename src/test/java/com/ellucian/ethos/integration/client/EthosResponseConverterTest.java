/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client;

import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EthosResponseConverterTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    private EthosResponseConverter ethosResponseConverter;
    private ObjectMapper objectMapper;

    // ==========================================================================
    // Methods
    // ==========================================================================

    private String buildEthosResponseContent( int size ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for( int i = 0; i < size; i++ ) {
            String label = "{\"someLabel" + i + "\":";
            String value = "\"someValue" + i + "\"}";
            String contentRow = label + value;
            sb.append( contentRow );
            sb.append( ",");
        }
        sb.deleteCharAt( sb.length() - 1 ); // Delete the last char which is a comma.
        sb.append( "]" );
        return sb.toString();
    }

    private EthosResponse buildEthosResponse( int size ) {
        return new EthosResponse(new HashMap<>(), buildEthosResponseContent(size), 200);
    }


    @BeforeEach
    void setup() {
        this.ethosResponseConverter = new EthosResponseConverter();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void trimContentFromOffsetTest() throws JsonProcessingException {
        EthosResponse testEthosResponse = buildEthosResponse( 10 );
        int offset = 3;
        String label = "someLabel" + offset;
        String expectedBeginningValue = "someValue" + offset;

        // Run the test.
        EthosResponse resultEthosResponse = ethosResponseConverter.trimContentFromOffset( testEthosResponse, offset );
        // Examine the results.
        assertTrue( resultEthosResponse != null );
        assertTrue( resultEthosResponse.getContent() != null );
        assertTrue( resultEthosResponse.getContent().isEmpty() == false );
        JsonNode jsonNode = objectMapper.readTree( resultEthosResponse.getContent() );
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.size() > 0 );
        // For the 1st element, ensure that the value is the correct one from the same offset.
        JsonNode node = jsonNode.get( 0 );
        List<String> valueList = node.findValuesAsText( label );
        assertTrue( valueList != null );
        assertTrue( valueList.isEmpty() == false );
        assertTrue( valueList.get(0).equals(expectedBeginningValue) );
    }


    @Test
    void trimContentFromOffsetWhenOffsetGreaterThanContentSizeTest() throws JsonProcessingException {
        EthosResponse testEthosResponse = buildEthosResponse( 10 );
        int offset = 15;

        // Run the test.
        EthosResponse resultEthosResponse = ethosResponseConverter.trimContentFromOffset( testEthosResponse, offset );
        // Examine the results.
        assertTrue( resultEthosResponse != null );
        assertTrue( resultEthosResponse.getContent() != null );
        assertTrue( resultEthosResponse.getContent().isEmpty() == false );
        JsonNode jsonNode = objectMapper.readTree( resultEthosResponse.getContent() );
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.size() == 10 );
    }

    @Test
    void trimContentForNumRowsTest() throws JsonProcessingException {
        EthosResponse testEthosResponse = buildEthosResponse( 10 );
        int numRows = 7;
        String beginningLabel = "someLabel0";
        String endingLabel = "someLabel" + (numRows - 1);
        String expectedBeginningValue = "someValue0";
        String expectedEndingValue = "someValue" + (numRows - 1);

        // Run the test.
        EthosResponse resultEthosResponse = ethosResponseConverter.trimContentForNumRows( testEthosResponse, numRows );
        // Examine the results.
        assertTrue( resultEthosResponse != null );
        assertTrue( resultEthosResponse.getContent() != null );
        assertTrue( resultEthosResponse.getContent().isEmpty() == false );
        JsonNode jsonNode = objectMapper.readTree( resultEthosResponse.getContent() );
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.size() == numRows );

        // For the 1st element, ensure that the value is the correct, should start at 0.
        JsonNode node = jsonNode.get( 0 );
        List<String> valueList = node.findValuesAsText( beginningLabel );
        assertTrue( valueList != null );
        assertTrue( valueList.isEmpty() == false );
        assertTrue( valueList.get(0).equals(expectedBeginningValue) );

        // For the last element, ensure that the value is the correct for numRows.
        node = jsonNode.get( numRows - 1 );
        valueList = node.findValuesAsText( endingLabel );
        assertTrue( valueList != null );
        assertTrue( valueList.isEmpty() == false );
        assertTrue( valueList.get(0).equals(expectedEndingValue) );
    }

    @Test
    void trimContentForNumRowsWhenNumRowsGreaterThanContentSizeTest() throws JsonProcessingException {
        EthosResponse testEthosResponse = buildEthosResponse( 10 );
        int numRows = 15;

        // Run the test.
        EthosResponse resultEthosResponse = ethosResponseConverter.trimContentForNumRows( testEthosResponse, numRows );
        // Examine the results.
        assertTrue( resultEthosResponse != null );
        assertTrue( resultEthosResponse.getContent() != null );
        assertTrue( resultEthosResponse.getContent().isEmpty() == false );
        JsonNode jsonNode = objectMapper.readTree( resultEthosResponse.getContent() );
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.size() == 10 );
    }

    @Test
    void trimContentFromOffsetForNumRowsTest() throws JsonProcessingException {
        EthosResponse testEthosResponse = buildEthosResponse( 10 );
        int offset = 3;
        int numRows = 5;
        String beginningLabel = "someLabel" + offset;
        String endingLabel = "someLabel" + (offset + numRows - 1);
        String expectedBeginningValue = "someValue" + offset;
        String expectedEndingValue = "someValue" + (offset + numRows - 1);

        // Run the test.
        EthosResponse resultEthosResponse = ethosResponseConverter.trimContentFromOffsetForNumRows( testEthosResponse, offset, numRows );
        // Examine the results.
        assertTrue( resultEthosResponse != null );
        assertTrue( resultEthosResponse.getContent() != null );
        assertTrue( resultEthosResponse.getContent().isEmpty() == false );
        JsonNode jsonNode = objectMapper.readTree( resultEthosResponse.getContent() );
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.size() == numRows );

        // For the 1st element, ensure that the value is the correct, should start at 0.
        JsonNode node = jsonNode.get( 0 );
        List<String> valueList = node.findValuesAsText( beginningLabel );
        assertTrue( valueList != null );
        assertTrue( valueList.isEmpty() == false );
        assertTrue( valueList.get(0).equals(expectedBeginningValue) );

        // For the last element, ensure that the value is the correct for numRows.
        node = jsonNode.get( jsonNode.size() - 1 );
        valueList = node.findValuesAsText( endingLabel );
        assertTrue( valueList != null );
        assertTrue( valueList.isEmpty() == false );
        assertTrue( valueList.get(0).equals(expectedEndingValue) );
    }


    @Test
    void trimContentFromOffsetForNumRowsWhenInvalidParamsTest() throws JsonProcessingException {
        EthosResponse testEthosResponse = null;
        int offset = 3;
        int numRows = 5;

        // Run the test.
        EthosResponse resultEthosResponse = ethosResponseConverter.trimContentFromOffsetForNumRows( testEthosResponse, offset, numRows );
        // Examine the results.
        assertTrue( resultEthosResponse == testEthosResponse );

        // Run the test again with a negative offset.
        testEthosResponse = buildEthosResponse( 10 );
        offset = -1;
        resultEthosResponse = ethosResponseConverter.trimContentFromOffsetForNumRows( testEthosResponse, offset, numRows );
        // Examine the results.
        assertTrue( resultEthosResponse == testEthosResponse );

        // Run the test again with negative numRows.  With negative numRows, we should trim from the offset.
        testEthosResponse = buildEthosResponse( 10 );
        offset = 3;
        numRows = -1;
        String label = "someLabel" + offset;
        String expectedBeginningValue = "someValue" + offset;
        resultEthosResponse = ethosResponseConverter.trimContentFromOffsetForNumRows( testEthosResponse, offset, numRows );
        // Examine the results, which should be trimmed from the offset.
        assertTrue( resultEthosResponse != null );
        assertTrue( resultEthosResponse.getContent() != null );
        assertTrue( resultEthosResponse.getContent().isEmpty() == false );
        JsonNode jsonNode = objectMapper.readTree( resultEthosResponse.getContent() );
        assertTrue( jsonNode != null );
        assertTrue( jsonNode.size() > 0 );
        // For the 1st element, ensure that the value is the correct one from the same offset.
        JsonNode node = jsonNode.get( 0 );
        List<String> valueList = node.findValuesAsText( label );
        assertTrue( valueList != null );
        assertTrue( valueList.isEmpty() == false );
        assertTrue( valueList.get(0).equals(expectedBeginningValue) );
    }

    @Test
    void toContentStringTest() {
        // Run the test with a null response.
        EthosResponse expectedEthosResponse = null;
        String response = ethosResponseConverter.toContentString( expectedEthosResponse );
        assertTrue( response == null );

        // Run the test again with a valid response.
        expectedEthosResponse = buildEthosResponse( 1 );
        response = ethosResponseConverter.toContentString( expectedEthosResponse );
        assertTrue( response != null );
        assertTrue( response == expectedEthosResponse.getContent() );
    }


    @Test
    void toJsonNodeTest() throws JsonProcessingException {
        // Run the test with a null response.
        EthosResponse expectedEthosResponse = null;
        JsonNode node = ethosResponseConverter.toJsonNode( expectedEthosResponse );
        assertTrue( node == null );

        // Run again with null content.
        expectedEthosResponse = new EthosResponse( new HashMap<>(), null, 200 );
        node = ethosResponseConverter.toJsonNode( expectedEthosResponse );
        assertTrue( node == null );

        // Run the test again with a valid response.
        int size = 2;
        expectedEthosResponse = buildEthosResponse( size );
        node = ethosResponseConverter.toJsonNode( expectedEthosResponse );
        assertTrue( node != null );
        assertTrue( node.size() == size );
        assertTrue( node.toString() != null );
        assertTrue( node.toString().equals(expectedEthosResponse.getContent()) );
    }

    @Test
    void toStringListTest() {
        // Run the test with a null ethosResponseList.
        List<String> stringList = ethosResponseConverter.toPageBasedStringList( null );
        assertTrue( stringList != null );
        assertTrue( stringList.isEmpty() );

        // Run the test with an empty ethosResponseList.
        stringList = ethosResponseConverter.toPageBasedStringList( new ArrayList<>() );
        assertTrue( stringList != null );
        assertTrue( stringList.isEmpty() );

        // Run the test with a valid ethosResponseList, 2 responses in the list, each with a page of size 2.
        List<EthosResponse> responseList = new ArrayList<>();
        responseList.add( buildEthosResponse(2) );
        responseList.add( buildEthosResponse(2) );
        List<String> expectedStringList = ethosResponseConverter.toPageBasedStringList( responseList );
        assertTrue( expectedStringList != null );
        assertTrue( expectedStringList.size() == responseList.size() );
    }

    @Test
    void toJsonNodeListTest() throws JsonProcessingException {
        // Run the test with a null ethosResponseList.
        List<JsonNode> jsonNodeList = ethosResponseConverter.toPageBasedJsonNodeList( null );
        assertTrue( jsonNodeList != null );
        assertTrue( jsonNodeList.isEmpty() );

        // Run the test with an empty ethosResponseList.
        jsonNodeList = ethosResponseConverter.toPageBasedJsonNodeList( new ArrayList<>() );
        assertTrue( jsonNodeList != null );
        assertTrue( jsonNodeList.isEmpty() );

        // Run the test with a valid ethosResponseList, 2 responses in the list, each with a page of size 2.
        List<EthosResponse> responseList = new ArrayList<>();
        responseList.add( buildEthosResponse(2) );
        responseList.add( buildEthosResponse(2) );
        List<JsonNode> expectedJsonNodeList = ethosResponseConverter.toPageBasedJsonNodeList( responseList );
        assertTrue( expectedJsonNodeList != null );
        assertTrue( expectedJsonNodeList.size() == responseList.size() );
    }

    @Test
    void toCNListNullInput() throws JsonProcessingException {
        List<ChangeNotification> result = ethosResponseConverter.toChangeNotificationList(null);
        assertNull(result);
    }

    @Test
    void toCNListNullContent() throws JsonProcessingException {
        EthosResponse response = new EthosResponse(new HashMap<>(), null, 200);
        List<ChangeNotification> result = ethosResponseConverter.toChangeNotificationList(response);
        assertNull(result);
    }

    @Test
    void toCNListEmptyContent() throws JsonProcessingException {
        EthosResponse response = new EthosResponse(new HashMap<>(), "", 200);
        List<ChangeNotification> result = ethosResponseConverter.toChangeNotificationList(response);
        assertNull(result);
    }

    @Test
    void toRowBasedStringListReturnsEmptyListTest() throws JsonProcessingException {
        List<String> stringList = ethosResponseConverter.toRowBasedStringList( null );
        assert( stringList != null );
        assert( stringList.isEmpty() );
    }

    @Test
    void toRowBasedStringListTest() throws JsonProcessingException {
        List<EthosResponse> ethosResponseList = new ArrayList<>();
        int listSize = 3;
        ethosResponseList.add( buildEthosResponse(listSize) );
        List<String> stringList = ethosResponseConverter.toRowBasedStringList( ethosResponseList );
        assert( stringList != null );
        assert( stringList.size() == listSize );
    }

    @Test
    void toRowBasedJsonNodeListReturnsEmptyListTest() throws JsonProcessingException {
        List<JsonNode> jsonNodeList = ethosResponseConverter.toRowBasedJsonNodeList( null );
        assert( jsonNodeList != null );
        assert( jsonNodeList.isEmpty() );
    }

    @Test
    void toRowBasedJsonNodeListTest() throws JsonProcessingException {
        List<EthosResponse> ethosResponseList = new ArrayList<>();
        int listSize = 3;
        ethosResponseList.add( buildEthosResponse(listSize) );
        List<JsonNode> jsonNodeList = ethosResponseConverter.toRowBasedJsonNodeList( ethosResponseList );
        assert( jsonNodeList != null );
        assert( jsonNodeList.size() == listSize );
    }

}