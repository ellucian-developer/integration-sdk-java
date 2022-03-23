/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client;


import com.ellucian.ethos.integration.client.errors.ErrorFactory;
import com.ellucian.ethos.integration.client.errors.EthosError;
import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.client.messages.ChangeNotificationFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Converter class extending the <code>EthosResponseBuilder</code>, the primary purpose of which is to handle manipulation
 * of the response body content for paging calculations, such as trimming the response body content for a given offset or
 * number of rows.  Also converts an <code>EthosResponse</code> to <code>String</code> or <code>com.fasterxml.jackson.databind.JsonNode</code>
 * formats.
 */
public class EthosResponseConverter extends EthosResponseBuilder {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /** The date format to use when handling date fields. */
//    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
//    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'TZD'";
//    private final String DATE_FORMAT = "dd/MM/yyyy'T'HH:mm:ss.SSS";
//    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
//    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * A Jackson objectMapper to count the number of rows in the response body content,
     * and to convert to the response body to a generic type object.
     */
    protected ObjectMapper objectMapper;

    /**
     * No-arg constructor which also builds the objectMapper.
     */
    public EthosResponseConverter() {
        super();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( DATE_FORMAT );
        this.objectMapper = new ObjectMapper();
//        this.objectMapper.setDateFormat( simpleDateFormat );
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Takes the given <code>EthosResponse</code> and trims it's content from the given offset.  For example,
     * if the given content contains 10 rows, and the offset is 3, the returned <code>EthosResponse</code> content will
     * contain rows 3 through the end of the <code>sourceResponse</code> content.
     * The returned <code>EthosResponse</code> will also contain the headers and Http status code from the given <code>sourceResponse</code>.
     * @param sourceResponse The <code>EthosResponse</code> to trim content for.
     * @param offset The offset (row num) from which to begin trimming content.
     * @return An <code>EthosResponse</code> containing the same content as the <code>sourceResponse</code>, minus the rows
     *         in the <code>sourceResponse</code> which occurred before the given offset.  The returned <code>EthosResponse</code>
     *         will also contain the headers and Http status code from the given <code>sourceResponse</code>.  If the <code>sourceResponse</code>
     *         is null, if the <code>sourceResponse</code> content is null or empty, or if the offset is negative, the original
     *         <code>sourceResponse</code> will be returned unchanged.
     * @throws JsonProcessingException Thrown if the Jackson objectMapper cannot read the response content body from the
     *         <code>sourceResponse</code>.
     */
    public EthosResponse trimContentFromOffset( EthosResponse sourceResponse, int offset )  throws JsonProcessingException {
        if( paramsValid(sourceResponse, offset) == false ) {
            return sourceResponse;
        }
        HashMap<String, Header> headersMap = copyHeaders( sourceResponse );
        String content = sourceResponse.getContent();
        JsonNode jsonNode = objectMapper.readTree( content );
        if( offset < jsonNode.size() ) {
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append( "[" );
            Iterator<JsonNode> iter = jsonNode.iterator();
            int counter = 0;
            while (iter.hasNext()) {
                JsonNode node = iter.next();
                if( counter >= offset ) {
                    contentBuilder.append( node.toString() );
                    contentBuilder.append( "," );
                }
                counter++;
            }
            if( contentBuilder.length() > 1 ) {
                contentBuilder.deleteCharAt( contentBuilder.length() - 1 ); // We added more than the opening bracket char so delete the last comma
            }
            contentBuilder.append( "]" );
            content = contentBuilder.toString();
        }
        EthosResponse targetResponse = new EthosResponse( headersMap, content, sourceResponse.getHttpStatusCode() );
        targetResponse.setRequestedUrl( sourceResponse.getRequestedUrl() );
        return targetResponse;
    }

    /**
     * Takes the given <code>EthosResponse</code> and trims it's content for the given number of rows.  For example,
     * if the given content contains 10 rows, and numRows is 7, the returned <code>EthosResponse</code> content will
     * contain rows 0 through 6 (for a total of 7) of the <code>sourceResponse</code> content.
     * The returned <code>EthosResponse</code> will also contain the headers and Http status code from the given <code>sourceResponse</code>.
     * @param sourceResponse The <code>EthosResponse</code> to trim content for.
     * @param numRows The number of rows to trim the content for.
     * @return An <code>EthosResponse</code> containing the same content as the <code>sourceResponse</code>, minus the rows
     *         in the <code>sourceResponse</code> which occur after numRows - 1.  The returned <code>EthosResponse</code>
     *         will also contain the headers and Http status code from the given <code>sourceResponse</code>.  If the <code>sourceResponse</code>
     *         is null, if the <code>sourceResponse</code> content is null or empty, or if the numRows is negative, the original
     *         <code>sourceResponse</code> will be returned unchanged.
     * @throws JsonProcessingException Thrown if the Jackson objectMapper cannot read the response content body from the
     *         <code>sourceResponse</code>.
     */
    public EthosResponse trimContentForNumRows( EthosResponse sourceResponse, int numRows ) throws JsonProcessingException {
        if( paramsValid(sourceResponse, numRows) == false ) {
            return sourceResponse;
        }
        HashMap<String, Header> headersMap = copyHeaders( sourceResponse );
        String content = sourceResponse.getContent();
        JsonNode jsonNode = objectMapper.readTree( content );
        if( numRows < jsonNode.size() ) {
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append( "[" );
            Iterator<JsonNode> iter = jsonNode.iterator();
            int counter = 0;
            while( iter.hasNext() ) {
                if( counter < numRows ) {
                    JsonNode node = iter.next();
                    contentBuilder.append(node.toString());
                    contentBuilder.append(",");
                }
                else {
                    break;
                }
                counter++;
            }
            contentBuilder.deleteCharAt( contentBuilder.length() - 1 ); // Delete the last comma
            contentBuilder.append( "]" );
            content = contentBuilder.toString();
        }
        EthosResponse targetResponse = new EthosResponse( headersMap, content, sourceResponse.getHttpStatusCode() );
        targetResponse.setRequestedUrl( sourceResponse.getRequestedUrl() );
        return targetResponse;
    }

    /**
     * Takes the given <code>EthosResponse</code> and trims it's content from the given offset for the given number of rows.  For example,
     * if the given content contains 10 rows, the offset is 3, and numRows is 7, the returned <code>EthosResponse</code> content will
     * contain rows 3 through 6 (for a total of 4) of the <code>sourceResponse</code> content.
     * The returned <code>EthosResponse</code> will also contain the headers and Http status code from the given <code>sourceResponse</code>.
     * @param sourceResponse The <code>EthosResponse</code> to trim content for.
     * @param offset The offset (row num) from which to begin trimming content.
     * @param numRows The number of rows to trim the content for.
     * @return An <code>EthosResponse</code> containing the same content as the <code>sourceResponse</code>, minus the rows
     *         in the <code>sourceResponse</code> which occur before the offset and after numRows - 1.  The returned <code>EthosResponse</code>
     *         will also contain the headers and Http status code from the given <code>sourceResponse</code>.  If the <code>sourceResponse</code>
     *         is null, if the <code>sourceResponse</code> content is null or empty, or if the offset is negative, the original
     *         <code>sourceResponse</code> will be returned unchanged.  If the offset is positive but the numRows is negative, then the
     *         <code>EthosResponse</code> will contain the same content as the <code>sourceResponse</code>, minus the rows in the
     *         <code>sourceResponse</code> which occur before the offset, just as what <code>trimContentFromOffset()</code> returns.
     * @throws JsonProcessingException Propagates this exception if thrown from the <code>trimContentFromOffset()</code> or
     *        <code>trimContentForNumRows()</code> methods.
     */
    public EthosResponse trimContentFromOffsetForNumRows( EthosResponse sourceResponse, int offset, int numRows ) throws JsonProcessingException {
        EthosResponse response = trimContentFromOffset( sourceResponse, offset );
        if( response == sourceResponse ) {
            return sourceResponse;
        }
        return trimContentForNumRows( response, numRows );
    }

    /**
     * Returns the content body of the given <code>EthosResponse</code>, or null if the <code>EthosResponse</code> is null.
     * @param ethosResponse the <code>EthosResponse</code> to get the content body from.
     * @return the content body of the given <code>EthosResponse</code>.
     */
    public String toContentString( EthosResponse ethosResponse ) {
        if( ethosResponse == null ) {
            return null;
        }
        return ethosResponse.getContent();
    }

    /**
     * Returns a <code>com.fasterxml.jackson.databind.JsonNode</code> representation of the content body of the given <code>EthosResponse</code>.
     * @param ethosResponse The given <code>EthosResponse</code> to convert to a <code>JsonNode</code>.
     * @return A <code>JsonNode</code> containing the content body of the given <code>EthosResponse</code>
     * @throws JsonProcessingException Thrown if the Jackson <code>objectMapper</code> cannot read the <code>EthosResponse</code>
     *         content body.
     */
    public JsonNode toJsonNode( EthosResponse ethosResponse ) throws JsonProcessingException {
        if( ethosResponse == null || ethosResponse.getContent() == null ) {
            return null;
        }
        return objectMapper.readTree( ethosResponse.getContent() );
    }

    /**
     * Converts the given list of <code>EthosResponse</code> objects into a list of <code>String</code>s, where
     * each <code>String</code> in the list is the content body of the given <code>EthosResponse</code> in the
     * <code>ethosResponseList</code>.  The expectation is that each EthosResponse in the given list contains a page
     * of resources, and therefore each String in the returned list contains that same page of resources in String format.
     * @param ethosResponseList The list of <code>EthosResponse</code> objects to convert to a list of <code>String</code>s.
     * @return a list of <code>String</code>s where each <code>String</code> in the list is the content body of the
     *         corresponding <code>EthosResponse</code> object in the <code>ethosResponseList</code>, or an empty list
     *         if the given <code>ethosResponseList</code> is null.  Each String in the returned list represents a page
     *         of resources from the corresponding ethosResponseList.
     */
    public List<String> toPageBasedStringList(List<EthosResponse> ethosResponseList ) {
        List<String> stringList = new ArrayList();
        if( ethosResponseList == null ) {
            return stringList;
        }
        for( EthosResponse ethosResponse : ethosResponseList ) {
            stringList.add( ethosResponse.getContent() );
        }
        return stringList;
    }


    /**
     * Converts the given list of <code>EthosResponse</code> objects into a list of <code>String</code>s, where
     * each <code>String</code> in the list is an individual row (instance) of a resource.  The expectation is that each
     * EthosResponse in the given list contains a page of resources, where each page is broken down into rows.  The rows
     * from each page are then all returned in a single list of JSON formatted Strings.
     * @param ethosResponseList The list of <code>EthosResponse</code> objects to convert to a list of <code>String</code>s.
     * @return a list of <code>String</code>s where each JSON formatted <code>String</code> in the list is an individual
     *         row (instance) of a resource.  The entirety of the returned list contains the same data in the given ethosResponseList,
     *         except by row instead of page, and in String format.  Returns an empty list if the given ethosResponseList is null.
     * @throws JsonProcessingException Thrown if the Jackson <code>objectMapper</code> cannot read the <code>EthosResponse</code>
     *                                 content body.
     */
    public List<String> toRowBasedStringList(List<EthosResponse> ethosResponseList ) throws JsonProcessingException {
        List<String> stringList = new ArrayList();
        if( ethosResponseList == null ) {
            return stringList;
        }
        List<JsonNode> jsonNodeRowList = toRowBasedJsonNodeList( ethosResponseList );
        for( JsonNode jsonNodeRow : jsonNodeRowList ) {
            stringList.add( jsonNodeRow.toString() );
        }
        return stringList;
    }

    /**
     * Converts the given list of <code>EthosResponse</code> objects into a list of <code>JsonNode</code>s, where
     * each <code>JsonNode</code> in the list contains the content body of the given <code>EthosResponse</code> in the
     * <code>ethosResponseList</code>.  The expectation is that each EthosResponse in the given list contains a page
     * of resources, and therefore each JsonNode in the returned list contains that same page of resources as a JsonNode.
     * @param ethosResponseList The list of <code>EthosResponse</code> objects to convert to a list of <code>JsonNode</code>s.
     * @return A list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list contains the content body of the
     *         corresponding <code>EthosResponse</code> object in the <code>ethosResponseList</code>, or an empty list
     *         if the given <code>ethosResponseList</code> is null.  Each JsonNode in the returned list represents a page
     *         of resources from the corresponding ethosResponseList.
     * @throws JsonProcessingException Thrown if the Jackson <code>objectMapper</code> cannot read the <code>EthosResponse</code>
     *         content body.
     */
    public List<JsonNode> toPageBasedJsonNodeList(List<EthosResponse> ethosResponseList ) throws JsonProcessingException {
        List<JsonNode> jsonNodeList = new ArrayList();
        if( ethosResponseList == null ) {
            return jsonNodeList;
        }
        for( EthosResponse ethosResponse : ethosResponseList ) {
            JsonNode jsonNodePage = toJsonNode( ethosResponse );
            jsonNodeList.add( jsonNodePage );
        }
        return jsonNodeList;
    }

    /**
     * Converts the given list of <code>EthosResponse</code> objects into a list of <code>JsonNode</code>s, where
     * each <code>JsonNode</code> in the list is an individual row (instance) of a resource.  The expectation is that each
     * EthosResponse in the given list contains a page of resources, where each page is broken down into rows.  The rows
     * from each page are then all returned in a single list of JsonNodes.
     * @param ethosResponseList The list of <code>EthosResponse</code> objects to convert to a list of row-based <code>JsonNode</code>s.
     * @return a list of <code>JsonNode</code>s where each <code>JsonNode</code> in the list is an individual
     *         row (instance) of a resource.  The entirety of the returned list contains the same data in the given ethosResponseList,
     *         except by row instead of page, and as JsonNodes.  Returns an empty list if the given ethosResponseList is null.
     * @throws JsonProcessingException Thrown if the Jackson <code>objectMapper</code> cannot read the <code>EthosResponse</code>
     *         content body.
     */
    public List<JsonNode> toRowBasedJsonNodeList(List<EthosResponse> ethosResponseList ) throws JsonProcessingException {
        List<JsonNode> jsonNodeRowList = new ArrayList();
        if( ethosResponseList == null ) {
            return jsonNodeRowList;
        }
        List<JsonNode> jsonNodePageList = toPageBasedJsonNodeList( ethosResponseList );
        for( JsonNode jsonNodePage : jsonNodePageList ) {
            if( jsonNodePage instanceof ArrayNode ) {
                Iterator<JsonNode> jsonNodeIterator = jsonNodePage.iterator();
                while (jsonNodeIterator.hasNext()) { // Iterate over the jsonNodePage to get each row within it.
                    JsonNode jsonNodeRow = jsonNodeIterator.next();
                    jsonNodeRowList.add(jsonNodeRow);
                }
            }
            else {
                jsonNodeRowList.add( jsonNodePage );
            }
        }
        return jsonNodeRowList;
    }


    /**
     * Converts an ethosResponse to an EthosError object.  The given ethosResponse.getContent() should contain
     * data for only a single error, and NOT an entire page of errors.
     * @param ethosResponse The EthosResponse to convert containing content for an EthosError.
     * @return An EthosError from the content of the given ethosResponse, or null if the ethosResponse is null or it's content is null or blank.
     * @throws JsonProcessingException Propagated if thrown by the ErrorFactory.
     */
    public EthosError toSingleEthosError(EthosResponse ethosResponse ) throws JsonProcessingException {
        EthosError ethosError = null;
        if( ethosResponse == null ||
            ethosResponse.getContent() == null ||
            ethosResponse.getContent().isBlank() ) {
            return ethosError;
        }
        return ErrorFactory.createErrorFromJson( ethosResponse.getContent() );
    }


    /**
     * Converts an ethosResponse to a list of EthosError objects.  The given ethosResponse.getContent() should contain
     * an entire page of errors as an errors array in JSON format.
     * @param ethosResponse The EthosResponse to convert containing content for a page of EthosErrors.
     * @return An list of EthosErrors from the content of the given ethosResponse, or an empty list if the ethosResponse
     *         is null or it's content is null or blank.
     * @throws JsonProcessingException Propagated if thrown by the ErrorFactory.
     */
    public List<EthosError> toEthosErrorList(EthosResponse ethosResponse ) throws JsonProcessingException {
        List<EthosError> ethosErrorList = new ArrayList<>();
        if( ethosResponse == null ||
            ethosResponse.getContent() == null ||
            ethosResponse.getContent().isBlank() ) {
            return ethosErrorList;
        }
        return ErrorFactory.createErrorListFromJson( ethosResponse.getContent() );
    }

    /**
     * Validates whether the given params are valid.  If the given <code>sourceResponse</code> is not null, and
     * contains content that is not null or empty, and if the positiveParam (which is intended to be either the offset
     * or numRows) is not less than 0 (positive), then returns true.  Otherwise returns false.
     * @param sourceResponse The <code>EthosResponse</code> to validate the content for.
     * @param positiveParam Should be either the offset or numRows as called from other methods.
     * @return False if the given <code>sourceResponse</code> is null, or contains null or empty content, or if the
     *         positive param is less than 0, true otherwise.
     */
    private boolean paramsValid( EthosResponse sourceResponse, int positiveParam ) {
        if( sourceResponse == null ||
            sourceResponse.getContent() == null ||
            sourceResponse.getContent().isEmpty() ) {
            return false;
        }
        if( positiveParam < 0 ) {
            return false;
        }
        return true;
    }

    /**
     * Returns a copy of the headers map from the given <code>EthosResponse</code>.  Uses the <code>headerList</code>
     * in the <code>EthosResponseBuilder</code> class to copy headers into the returned headers map.
     * @param sourceResponse The <code>sourceResponse</code> to copy headers from.
     * @return A <code>HashMap&lt;String,Header&gt;</code> containing the headers from the given <code>sourceResponse</code>
     *         as defined by the <code>headerList</code> in <code>EthosResponseBuilder</code>.
     */
    private HashMap<String,Header> copyHeaders( EthosResponse sourceResponse ) {
        HashMap<String, Header> headersMap = new HashMap<>();
        List<String> headerKeyList = sourceResponse.getHeaderMapKeys();
        for( String headerKey : headerKeyList ) {
            Header header = sourceResponse.getHeader( headerKey );
            headersMap.put( headerKey, header );
        }
        return headersMap;
    }

    /**
     * Converts the given ethosResponse to a list of ChangeNotification objects.
     * @param ethosResponse The EthosResponse to convert containing content for the ChangeNotifications.
     * @return A list of ChangeNotifications from the content of the given ethosResponse,
     *          or null if the ethosResponse is null or it's content is null or blank.
     * @throws JsonProcessingException Propagated if thrown by the ChangeNotificationFactory.
     */
    public List<ChangeNotification> toChangeNotificationList(EthosResponse ethosResponse ) throws JsonProcessingException {
        if( ethosResponse == null ||
            ethosResponse.getContent() == null ||
            ethosResponse.getContent().isBlank() ) {
            return null;
        }
        return ChangeNotificationFactory.createCNListFromJson( ethosResponse.getContent() );
    }

    /**
     * Converts the EthosResponse content into a list of generic typed objects based on the given class.
     * @param ethosResponse The EthosResponse containing content to convert.
     * @param classType A class reference of the generic type object to convert to.
     * @param <T> The generic type returned.
     * @return A list of generic type objects from the content of the EthosResponse, according to the given class.
     * @throws JsonProcessingException Thrown if the object mapper cannot read the content of the EthosResponse.
     */
    public <T> T toTypedList( EthosResponse ethosResponse, Class classType ) throws JsonProcessingException {
        List objList = new ArrayList();
        if( ethosResponse == null || classType == null ) {
            return (T) objList;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType( List.class, classType );
        objList = objectMapper.readValue( ethosResponse.getContent(), javaType );
        return (T) objList;
    }

    /**
     * Converts the EthosResponse content into a generic typed object based on the given class.
     * @param ethosResponse The EthosResponse containing content to convert.
     * @param classType A class reference of the generic type object to convert to.
     * @param <T> The generic type returned.
     * @return A generic type object from the content of the EthosResponse, according to the given class.
     * @throws JsonProcessingException Thrown if the object mapper cannot read the content of the EthosResponse.
     */
    public <T> T toTyped( EthosResponse ethosResponse, Class classType ) throws JsonProcessingException {
        JavaType javaType = objectMapper.getTypeFactory().constructType( classType );
        T genericType = objectMapper.readValue( ethosResponse.getContent(), javaType );
        return (T) genericType;
    }

}