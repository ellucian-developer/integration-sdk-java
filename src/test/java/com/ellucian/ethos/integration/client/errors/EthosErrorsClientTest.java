package com.ellucian.ethos.integration.client.errors;

import com.ellucian.ethos.integration.EthosIntegrationUrls;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class EthosErrorsClientTest {

    private EthosErrorsClient spyEthosErrorsClient;

    private EthosResponse buildEthosResponse() {
        Map<String, Header> headerMap = new HashMap();
        headerMap.put( EthosErrorsClient.HDR_TOTAL_COUNT, new BasicHeader(EthosErrorsClient.HDR_TOTAL_COUNT, "1") );
        return new EthosResponse( headerMap, "{\"severity\":\"INFO\", \"description\":\"some description\"}", 200 );
    }

    private EthosResponse buildEthosResponsePage() {
        Map<String, Header> headerMap = new HashMap();
        headerMap.put( EthosErrorsClient.HDR_TOTAL_COUNT, new BasicHeader(EthosErrorsClient.HDR_TOTAL_COUNT, "1") );
        // Content body with an array size of 2.
        return new EthosResponse( headerMap, "[{\"severity\":\"INFO\", \"description\":\"some description\"}, {\"severity\":\"INFO\", \"description\":\"some description\"}]", 200 );
    }


    private List<EthosResponse> buildEthosResponsePageList() {
        List<EthosResponse> ethosResponseList = new ArrayList<>();
        ethosResponseList.add( buildEthosResponsePage() );
        ethosResponseList.add( buildEthosResponsePage() );
        return ethosResponseList;
    }

    @BeforeEach
    public void setup() {
        spyEthosErrorsClient = spy( new EthosErrorsClient("11111111-1111-1111-1111-111111111111", null, null, null) );
    }

    @Test
    void getByIdWithNullId() {
        EthosErrorsClient client = new EthosErrorsClient("11111111-1111-1111-1111-111111111111", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.getById(null);
        });
    }

    @Test
    void createWithNullError() {
        EthosErrorsClient client = new EthosErrorsClient("11111111-1111-1111-1111-111111111111", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.post(null);
        });
    }

    @Test
    void deleteWithNullId() {
        EthosErrorsClient client = new EthosErrorsClient("11111111-1111-1111-1111-111111111111", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.delete(null);
        });
    }

    @Test
    public void getTest() throws IOException  {
        EthosResponse expectedResponse = buildEthosResponsePage();
        // Return the expectedResponse when the method under test calls a mocked method.
        Mockito.doReturn(expectedResponse).when(spyEthosErrorsClient).get(anyString(), anyMap() );
        // Run the test.
        EthosResponse resultResponse = spyEthosErrorsClient.get();
        // Verify and assert the results.
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).get(anyString(), anyMap());
        assert( resultResponse != null );
    }

    @Test
    public void getAsJsonNodeTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponsePage();
        // Return the expectedResponse when the method under test calls a mocked method.
        Mockito.doReturn(expectedResponse).when(spyEthosErrorsClient).get();
        // Run the test.
        JsonNode resultNode = spyEthosErrorsClient.getAsJsonNode();
        // Verify and assert the results.
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).get();
        assert( resultNode != null );
    }

    @Test
    public void getAsStringTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponsePage();
        // Return the expectedResponse when the method under test calls a mocked method.
        Mockito.doReturn(expectedResponse).when(spyEthosErrorsClient).get();
        // Run the test.
        String resultStr = spyEthosErrorsClient.getAsString();
        // Verify and assert the results.
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).get();
        assert( resultStr != null );
        assert( resultStr.equals(expectedResponse.getContent()) );
    }

    @Test
    public void getAsEthosErrorsTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponsePage();
        // Return the expectedResponse when the method under test calls a mocked method.
        Mockito.doReturn(expectedResponse).when(spyEthosErrorsClient).get();
        // Run the test.
        List<EthosError> resultErrorList = spyEthosErrorsClient.getAsEthosErrors();
        // Verify and assert the results.
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).get();
        assert( resultErrorList != null );
        assert( resultErrorList.size() == 2 );
    }


    @Test
    public void getByIdTest() throws IOException  {
        String id = "someId";
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the expectedResponse when the method under test calls a mocked method.
        Mockito.doReturn(expectedResponse).when(spyEthosErrorsClient).get(anyString(), anyMap() );
        // Run the test.
        EthosResponse resultResponse = spyEthosErrorsClient.getById( id );
        // Verify and assert the results.
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).get(anyString(), anyMap());
        assert( resultResponse != null );
    }

    @Test
    public void getByIdAsEthosErrorTest() throws IOException  {
        String id = "someId";
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the expectedResponse when the method under test calls a mocked method.
        Mockito.doReturn(expectedResponse).when(spyEthosErrorsClient).getById( anyString() );
        // Run the test.
        EthosError ethosError = spyEthosErrorsClient.getByIdAsEthosError( id );
        // Verify and assert the results.
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getById( anyString() );
        assert( ethosError != null );
    }

    @Test
    public void getByIdAsJsonNodeTest() throws IOException  {
        String id = "someId";
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the expectedResponse when the method under test calls a mocked method.
        Mockito.doReturn(expectedResponse).when(spyEthosErrorsClient).getById( anyString() );
        // Run the test.
        JsonNode resultNode = spyEthosErrorsClient.getByIdAsJsonNode( id );
        // Verify and assert the results.
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getById( anyString() );
        assert( resultNode != null );
    }

    @Test
    public void getByIdAsStringTest() throws IOException  {
        String id = "someId";
        EthosResponse expectedResponse = buildEthosResponse();
        // Return the expectedResponse when the method under test calls a mocked method.
        Mockito.doReturn(expectedResponse).when(spyEthosErrorsClient).getById( anyString() );
        // Run the test.
        String resultStr = spyEthosErrorsClient.getByIdAsString( id );
        // Verify and assert the results.
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getById( anyString() );
        assert( resultStr != null );
        assert( resultStr.equals(expectedResponse.getContent()) );
    }

    @Test
    public void getAllErrorsTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int totalCount = 100;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(totalCount).when(spyEthosErrorsClient).getTotalErrorCount();
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).doPaging( totalCount, EthosErrorsClient.DEFAULT_ERROR_PAGE_SIZE, 0 );
        // Run the test.
        List<EthosResponse> resultList = spyEthosErrorsClient.getAllErrors();
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getTotalErrorCount();
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).doPaging( totalCount, EthosErrorsClient.DEFAULT_ERROR_PAGE_SIZE, 0 );
        assert( resultList != null );
        assert( resultList.size() == 2 );
    }


    @Test
    public void getAllErrorsAsJsonNodesTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int totalCount = 100;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).getAllErrors();
        // Run the test.
        List<JsonNode> resultNodeList = spyEthosErrorsClient.getAllErrorsAsJsonNodes();
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getAllErrors();
        assert( resultNodeList != null );
        assert( resultNodeList.size() == 2 );
    }

    @Test
    public void getAllErrorsAsStringsTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int totalCount = 100;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).getAllErrors();
        // Run the test.
        List<String> resultStringList = spyEthosErrorsClient.getAllErrorsAsStrings();
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getAllErrors();
        assert( resultStringList != null );
        assert( resultStringList.size() == 2 );
        assert( resultStringList.get(0).equals(expectedResponseList.get(0).getContent()) );
        assert( resultStringList.get(1).equals(expectedResponseList.get(1).getContent()) );
    }


    @Test
    public void getAllErrorsAsEthosErrorsTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int totalCount = 100;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).getAllErrors();
        // Run the test.
        List<EthosError> resultErrorList = spyEthosErrorsClient.getAllErrorsAsEthosErrors();
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getAllErrors();
        assert( resultErrorList != null );
        assert( resultErrorList.size() == (expectedResponseList.size() * 2) ); // Multiply the expectedResponseList size by 2 since there are 2 items in each response.
    }

    @Test
    public void getAllErrorsWithPageSizeTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int totalCount = 100;
        int pageSize = 15;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(totalCount).when(spyEthosErrorsClient).getTotalErrorCount();
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).doPaging( totalCount, pageSize, 0 );
        // Run the test.
        List<EthosResponse> resultList = spyEthosErrorsClient.getAllErrorsWithPageSize( pageSize );
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getTotalErrorCount();
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).doPaging( totalCount, pageSize, 0 );
        assert( resultList != null );
        assert( resultList.size() == 2 );
    }


    @Test
    public void getAllErrorsWithPageSizeAsJsonNodeTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int totalCount = 100;
        int pageSize = 15;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).getAllErrorsWithPageSize( pageSize );
        // Run the test.
        List<JsonNode> resultNodeList = spyEthosErrorsClient.getAllErrorsWithPageSizeAsJsonNodes( pageSize );
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getAllErrorsWithPageSize( pageSize );
        assert( resultNodeList != null );
        assert( resultNodeList.size() == 2 );
    }


    @Test
    public void getAllErrorsWithPageSizeAsStringsTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int pageSize = 15;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).getAllErrorsWithPageSize( pageSize );
        // Run the test.
        List<String> resultStringList = spyEthosErrorsClient.getAllErrorsWithPageSizeAsStrings( pageSize );
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getAllErrorsWithPageSize( pageSize );
        assert( resultStringList != null );
        assert( resultStringList.size() == 2 );
        assert( resultStringList.get(0).equals(expectedResponseList.get(0).getContent()) );
        assert( resultStringList.get(1).equals(expectedResponseList.get(1).getContent()) );
    }


    @Test
    public void getErrorsFromOffsetTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int totalCount = 100;
        int offset = 40;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(totalCount).when(spyEthosErrorsClient).getTotalErrorCount();
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).doPaging( totalCount, EthosErrorsClient.DEFAULT_ERROR_PAGE_SIZE, offset );
        // Run the test.
        List<EthosResponse> resultList = spyEthosErrorsClient.getErrorsFromOffset( offset );
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getTotalErrorCount();
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).doPaging( totalCount, EthosErrorsClient.DEFAULT_ERROR_PAGE_SIZE, offset );
        assert( resultList != null );
        assert( resultList.size() == 2 );
    }


    @Test
    public void getErrorsFromOffsetAsJsonNodeTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int offset = 40;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).getErrorsFromOffset( offset );
        // Run the test.
        List<JsonNode> resultNodeList = spyEthosErrorsClient.getErrorsFromOffsetAsJsonNodes( offset );
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getErrorsFromOffset( offset );
        assert( resultNodeList != null );
        assert( resultNodeList.size() == 2 );
    }


    @Test
    public void getErrorsFromOffsetAsStringsTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int offset = 40;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).getErrorsFromOffset( offset );
        // Run the test.
        List<String> resultStringList = spyEthosErrorsClient.getErrorsFromOffsetAsStrings( offset );
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getErrorsFromOffset( offset );
        assert( resultStringList != null );
        assert( resultStringList.size() == 2 );
        assert( resultStringList.get(0).equals(expectedResponseList.get(0).getContent()) );
        assert( resultStringList.get(1).equals(expectedResponseList.get(1).getContent()) );
    }

    @Test
    public void getErrorsFromOffsetAsEthosErrorsTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int offset = 40;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).getErrorsFromOffset( offset );
        // Run the test.
        List<EthosError> resultErrorList = spyEthosErrorsClient.getErrorsFromOffsetAsEthosErrors( offset );
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getErrorsFromOffset( offset );
        assert( resultErrorList != null );
        assert( resultErrorList.size() == (expectedResponseList.size() * 2) ); // Multiply the expectedResponseList size by 2 since there are 2 items in each response.
    }

    @Test
    public void getErrorsFromOffsetWithPageSizeTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int totalCount = 100;
        int offset = 40;
        int pageSize = 15;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(totalCount).when(spyEthosErrorsClient).getTotalErrorCount();
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).doPaging( totalCount, pageSize, offset );
        // Run the test.
        List<EthosResponse> resultList = spyEthosErrorsClient.getErrorsFromOffsetWithPageSize( offset, pageSize );
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getTotalErrorCount();
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).doPaging( totalCount, pageSize, offset );
        assert( resultList != null );
        assert( resultList.size() == 2 );
    }


    @Test
    public void getErrorsFromOffsetWithPageSizeAsJsonNodeTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int offset = 40;
        int pageSize = 15;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).getErrorsFromOffsetWithPageSize( offset, pageSize );
        // Run the test.
        List<JsonNode> resultNodeList = spyEthosErrorsClient.getErrorsFromOffsetWithPageSizeAsJsonNodes( offset, pageSize );
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getErrorsFromOffsetWithPageSize( offset, pageSize );
        assert( resultNodeList != null );
        assert( resultNodeList.size() == 2 );
    }

    @Test
    public void getErrorsFromOffsetWithPageSizeAsStringsTest() throws IOException {
        List<EthosResponse> expectedResponseList = buildEthosResponsePageList();
        int offset = 40;
        int pageSize = 15;
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(expectedResponseList).when(spyEthosErrorsClient).getErrorsFromOffsetWithPageSize( offset, pageSize );
        // Run the test.
        List<String> resultStringList = spyEthosErrorsClient.getErrorsFromOffsetWithPageSizeAsStrings( offset, pageSize );
        // Verify and assert the results.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).getErrorsFromOffsetWithPageSize( offset, pageSize );
        assert( resultStringList != null );
        assert( resultStringList.size() == 2 );
        assert( resultStringList.get(0).equals(expectedResponseList.get(0).getContent()) );
        assert( resultStringList.get(1).equals(expectedResponseList.get(1).getContent()) );
    }

    @Test
    public void doPagingTest() throws IOException {
        int totalErrorCount = 100;
        int offset = 0;
        int pageSize = 20;
        int expectedNumPages = spyEthosErrorsClient.calculateNumberOfPages( totalErrorCount, pageSize, offset );
        EthosResponse ethosResponse = buildEthosResponse();
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(ethosResponse).when(spyEthosErrorsClient).get( anyString(), anyMap() );
        // Run the test.
        List<EthosResponse> resultList = spyEthosErrorsClient.doPaging( totalErrorCount, pageSize, offset );
        // Verify and assert the results.
        assert( resultList != null );
        assert( resultList.isEmpty() == false );
        assert( resultList.size() == expectedNumPages );

        // Change input and test again.
        offset = -1;
        expectedNumPages = spyEthosErrorsClient.calculateNumberOfPages( totalErrorCount, pageSize, 0 );
        resultList = spyEthosErrorsClient.doPaging( totalErrorCount, pageSize, offset );
        // Verify and assert the results.
        assert( resultList != null );
        assert( resultList.isEmpty() == false );
        assert( resultList.size() == expectedNumPages );

        // Change input and test again.
        offset = -1;
        pageSize = -1;
        expectedNumPages = spyEthosErrorsClient.calculateNumberOfPages( totalErrorCount, EthosErrorsClient.DEFAULT_ERROR_PAGE_SIZE, 0 );
        resultList = spyEthosErrorsClient.doPaging( totalErrorCount, pageSize, offset );
        // Verify and assert the results.
        assert( resultList != null );
        assert( resultList.isEmpty() == false );
        assert( resultList.size() == expectedNumPages );
    }


    @Test
    public void calculateNumberOfPagesTest() {
        int totalErrorsCount = 100;
        int offset = 0;
        int pageSize = 20;
        int expectedNumPages = 5;
        int numPages = spyEthosErrorsClient.calculateNumberOfPages( totalErrorsCount, pageSize, offset );
        assert( numPages == expectedNumPages );
    }


    @Test
    public void createTest() throws IOException  {
        EthosResponse errorResponse = buildEthosResponse();
        EthosError ethosError = ErrorFactory.createErrorFromJson( errorResponse.getContent() );
        EthosResponse expectedResponse = new EthosResponse(new HashMap<>(), "{\"id\":\"someGUID\"}", 200);
        // Return the expectedResponse when the method under test calls a mocked method.
        Mockito.doReturn(expectedResponse).when(spyEthosErrorsClient).post( anyString(), anyMap(), anyString() );
        // Run the test.
        EthosResponse resultResponse = spyEthosErrorsClient.post( ethosError );
        // Verify and assert the results.
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).post( anyString(), anyMap(), anyString() );
        assert( resultResponse != null );
    }

    @Test
    public void deleteTest() throws IOException  {
        String resourceId = "someId";
        String url = EthosIntegrationUrls.errors(spyEthosErrorsClient.getRegion()) + "/" + resourceId;
        // Return the expectedResponse when the method under test calls a mocked method.
        Mockito.doNothing().when(spyEthosErrorsClient).delete( url, null );
        // Run the test.
        spyEthosErrorsClient.delete( resourceId );
        // Verify and assert the results.
        // Verify the mocked get() method was called 1 time.
        Mockito.verify(spyEthosErrorsClient, Mockito.times(1)).delete( url, null );
    }

    @Test
    public void getTotalErrorCountTest() throws IOException {
        EthosResponse ethosResponse = buildEthosResponse();
        // Return what is expected when calling a mocked method.
        Mockito.doReturn(ethosResponse).when(spyEthosErrorsClient).get();
        // Run the test.
        int totalCount = spyEthosErrorsClient.getTotalErrorCount();
        assert( totalCount == 1 );
    }

}
