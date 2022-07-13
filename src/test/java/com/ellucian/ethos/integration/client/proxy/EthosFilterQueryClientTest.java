/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy;

import com.ellucian.ethos.integration.client.EthosRequestConverter;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.proxy.filter.CriteriaFilter;
import com.ellucian.ethos.integration.client.proxy.filter.FilterMap;
import com.ellucian.ethos.integration.client.proxy.filter.NamedQueryFilter;
import com.ellucian.ethos.integration.client.proxy.filter.SimpleCriteria;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EthosFilterQueryClientTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private EthosFilterQueryClient spyEthosFilterQueryClient;

    // ==========================================================================
    // Methods
    // ==========================================================================

    private EthosResponse buildEthosResponse() {
        BasicHeader header = new BasicHeader(EthosFilterQueryClient.HDR_X_TOTAL_COUNT, "100");
        Map<String,Header> headerMap = new HashMap<>();
        headerMap.put( EthosFilterQueryClient.HDR_X_TOTAL_COUNT, header );
        return new EthosResponse( headerMap, "{\"someLabel\":\"someValue\"}", 200 );
    }

    private List<EthosResponse> buildEthosResponseList() {
        List<EthosResponse> ethosResponseList = new ArrayList<>();
        ethosResponseList.add( buildEthosResponse() );
        ethosResponseList.add( buildEthosResponse() );
        return ethosResponseList;
    }

    @BeforeEach
    public void setup() {
        // Not using the EthosClientFactory to build the EthosFilterQueryClient because we have to spy it with Mockito.
        spyEthosFilterQueryClient = Mockito.spy( new EthosFilterQueryClient("11111111-1111-1111-1111-111111111111", null, null, null) );
    }

    @Test
    public void getWithCriteriaFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        String filter = "someFilter";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithCriteriaFilter( resource, version, filter );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithCriteriaFilter( resource, filter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithCriteriaFilter( resource, version, filter );
    }

    @Test
    public void getWithCriteriaFilterThrowsException() {
        String version = "someVersion";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = null;
            String filterStr = null;
            spyEthosFilterQueryClient.getWithCriteriaFilter( resourceName, version, filterStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String filterStr = null;
            spyEthosFilterQueryClient.getWithCriteriaFilter( resourceName, version, filterStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = null;
            String filterStr = "someFilter";
            spyEthosFilterQueryClient.getWithCriteriaFilter( resourceName, version, filterStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "";
            String filterStr = "";
            spyEthosFilterQueryClient.getWithCriteriaFilter( resourceName, version, filterStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String filterStr = "";
            spyEthosFilterQueryClient.getWithCriteriaFilter( resourceName, version, filterStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "";
            String filterStr = "someFilter";
            spyEthosFilterQueryClient.getWithCriteriaFilter( resourceName, version, filterStr );
        });
    }

    @Test
    public void getWithCriteriaFilterUsingVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        String filter = "someFilter";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).get( anyString(), anyMap() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithCriteriaFilter( resource, version, filter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).get( anyString(), anyMap() );
    }

    @Test
    public void getWithCriteriaFilterUsingCriteriaFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithCriteriaFilter( resource, version, criteriaFilter );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithCriteriaFilter( resource, criteriaFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithCriteriaFilter( resource, version, criteriaFilter );
    }

    @Test
    public void getWithCriteriaFilterUsingVersionCriteriaFilterThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String filterStr = "someVersion";
            CriteriaFilter criteriaFilter = null;
            spyEthosFilterQueryClient.getWithCriteriaFilter( resourceName, filterStr, criteriaFilter );
        });
    }

    @Test
    public void getWithCriteriaFilterUsingVersionCriteriaFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithCriteriaFilter( resource, version, criteriaFilter.toString() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithCriteriaFilter( resource, version, criteriaFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithCriteriaFilter( resource, version, criteriaFilter.toString() );
    }

    @Test
    public void getWithNamedQueryFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        String filter = "someFilter";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithNamedQueryFilter( resource, version, filter );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithNamedQueryFilter( resource, filter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithNamedQueryFilter( resource, version, filter );
    }

    @Test
    public void getWithNamedQueryFilterThrowsException() {
        String version = "someVersion";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = null;
            String filterStr = null;
            spyEthosFilterQueryClient.getWithNamedQueryFilter( resourceName, version, filterStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String filterStr = null;
            spyEthosFilterQueryClient.getWithNamedQueryFilter( resourceName, version, filterStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = null;
            String filterStr = "someFilter";
            spyEthosFilterQueryClient.getWithNamedQueryFilter( resourceName, version, filterStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "";
            String filterStr = "";
            spyEthosFilterQueryClient.getWithNamedQueryFilter( resourceName, version, filterStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String filterStr = "";
            spyEthosFilterQueryClient.getWithNamedQueryFilter( resourceName, version, filterStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "";
            String filterStr = "someFilter";
            spyEthosFilterQueryClient.getWithNamedQueryFilter( resourceName, version, filterStr );
        });
    }

    @Test
    public void getWithNamedQueryFilterUsingVersionTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        String filter = "someFilter";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).get( anyString(), anyMap() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithNamedQueryFilter( resource, version, filter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).get( anyString(), anyMap() );
    }

    @Test
    public void getWithNamedQueryFilterUsingNamedQueryFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery(queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithNamedQueryFilter( resource, version, namedQueryFilter );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithNamedQueryFilter( resource, namedQueryFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithNamedQueryFilter( resource, version, namedQueryFilter );
    }

    @Test
    public void getWithNamedQueryFilterUsingVersionNamedQueryFilterThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String filterStr = "someVersion";
            NamedQueryFilter namedQueryFilter = null;
            spyEthosFilterQueryClient.getWithNamedQueryFilter( resourceName, filterStr, namedQueryFilter );
        });
    }

    @Test
    public void getWithNamedQueryFilterUsingVersionNamedQueryFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery(queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithNamedQueryFilter( resource, version, namedQueryFilter.toString() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithNamedQueryFilter( resource, version, namedQueryFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithNamedQueryFilter( resource, version, namedQueryFilter.toString() );
    }

    @Test
    public void getWithSimpleCriteriaValuesCriteriaFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        String criteriaKey = "someCriteriaKey";
        String criteriaValue = "someCriteriaValue";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithSimpleCriteriaValues( resource, version, criteriaKey, criteriaValue );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithSimpleCriteriaValues( resource, criteriaKey, criteriaValue );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithSimpleCriteriaValues( resource, version, criteriaKey, criteriaValue );
    }

    @Test
    public void getWithSimpleCriteriaValuesThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaKey = null;
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaValues( resourceName, version, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaKey = "";
            String criteriaValue = "";
            spyEthosFilterQueryClient.getWithSimpleCriteriaValues( resourceName, version, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaKey = null;
            String criteriaValue = "";
            spyEthosFilterQueryClient.getWithSimpleCriteriaValues( resourceName, version, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaSetName = "someSetName";
            String criteriaKey = "";
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaValues( resourceName, version, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaKey = "someKey";
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaValues( resourceName, version, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaKey = "someKey";
            String criteriaValue = "";
            spyEthosFilterQueryClient.getWithSimpleCriteriaValues( resourceName, version, criteriaKey, criteriaValue );
        });
    }

    @Test
    public void getWithSimpleCriteriaValuesUsingVersionFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        String criteriaKey = "someKey";
        String criteriaValue = "someValue";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithCriteriaFilter( anyString(), anyString(), any(CriteriaFilter.class) );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithSimpleCriteriaValues( resource, version, criteriaKey, criteriaValue );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithCriteriaFilter( anyString(), anyString(), any(CriteriaFilter.class) );
    }


    @Test
    public void getWithSimpleCriteriaObjectValuesCriteriaFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        String criteriaSetName = "someCriteriaSet";
        String criteriaKey = "someCriteriaKey";
        String criteriaValue = "someCriteriaValue";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithSimpleCriteriaObjectValues( resource, version, criteriaSetName, criteriaKey, criteriaValue );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithSimpleCriteriaObjectValues( resource, criteriaSetName, criteriaKey, criteriaValue );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithSimpleCriteriaObjectValues( resource, version, criteriaSetName, criteriaKey, criteriaValue );
    }

    @Test
    public void getWithSimpleCriteriaObjectValuesThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaSetName = null;
            String criteriaKey = null;
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaObjectValues( resourceName, version, criteriaSetName, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaSetName = "";
            String criteriaKey = null;
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaObjectValues( resourceName, version, criteriaSetName, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaSetName = "someSetName";
            String criteriaKey = null;
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaObjectValues( resourceName, version, criteriaSetName, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaSetName = "someSetName";
            String criteriaKey = "";
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaObjectValues( resourceName, version, criteriaSetName, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaSetName = "someSetName";
            String criteriaKey = "someKey";
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaObjectValues( resourceName, version, criteriaSetName, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaSetName = "someSetName";
            String criteriaKey = "someKey";
            String criteriaValue = "";
            spyEthosFilterQueryClient.getWithSimpleCriteriaObjectValues( resourceName, version, criteriaSetName, criteriaKey, criteriaValue );
        });
    }

    @Test
    public void getWithSimpleCriteriaObjectValuesUsingVersionFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        String criteriaSetName = "someSetName";
        String criteriaKey = "someKey";
        String criteriaValue = "someValue";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithCriteriaFilter( anyString(), anyString(), any(CriteriaFilter.class) );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithSimpleCriteriaObjectValues( resource, version, criteriaSetName, criteriaKey, criteriaValue );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithCriteriaFilter( anyString(), anyString(), any(CriteriaFilter.class) );
    }

    @Test
    public void getWithSimpleCriteriaArrayValuesCriteriaFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        String criteriaLabel = "someCriteriaSet";
        String criteriaKey = "someCriteriaKey";
        String criteriaValue = "someCriteriaValue";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithSimpleCriteriaArrayValues( resource, version, criteriaLabel, criteriaKey, criteriaValue );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithSimpleCriteriaArrayValues( resource, criteriaLabel, criteriaKey, criteriaValue );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithSimpleCriteriaArrayValues( resource, version, criteriaLabel, criteriaKey, criteriaValue );
    }

    @Test
    public void getWithSimpleCriteriaArrayValuesThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaLabel = null;
            String criteriaKey = null;
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaArrayValues( resourceName, version, criteriaLabel, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaLabel = "";
            String criteriaKey = null;
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaArrayValues( resourceName, version, criteriaLabel, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaLabel = "someSetName";
            String criteriaKey = null;
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaArrayValues( resourceName, version, criteriaLabel, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaLabel = "someSetName";
            String criteriaKey = "";
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaArrayValues( resourceName, version, criteriaLabel, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaLabel = "someSetName";
            String criteriaKey = "someKey";
            String criteriaValue = null;
            spyEthosFilterQueryClient.getWithSimpleCriteriaArrayValues( resourceName, version, criteriaLabel, criteriaKey, criteriaValue );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            String criteriaLabel = "someSetName";
            String criteriaKey = "someKey";
            String criteriaValue = "";
            spyEthosFilterQueryClient.getWithSimpleCriteriaArrayValues( resourceName, version, criteriaLabel, criteriaKey, criteriaValue );
        });
    }

    @Test
    public void getWithSimpleCriteriaArrayValuesUsingVersionFilterTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        String criteriaSetName = "someSetName";
        String criteriaKey = "someKey";
        String criteriaValue = "someValue";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithCriteriaFilter( anyString(), anyString(), any(CriteriaFilter.class) );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithSimpleCriteriaArrayValues( resource, version, criteriaSetName, criteriaKey, criteriaValue );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithCriteriaFilter( anyString(), anyString(), any(CriteriaFilter.class) );
    }

    @Test
    public void getWithFilterMapThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = null;
            String filterMapStr = null;
            spyEthosFilterQueryClient.getWithFilterMap( resourceName, "someVersion", filterMapStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String filterMapStr = null;
            spyEthosFilterQueryClient.getWithFilterMap( resourceName, "someVersion", filterMapStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = null;
            String filterMapStr = "someFilter";
            spyEthosFilterQueryClient.getWithFilterMap( resourceName, "someVersion", filterMapStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "";
            String filterMapStr = "";
            spyEthosFilterQueryClient.getWithFilterMap( resourceName, "someVersion", filterMapStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String filterMapStr = "";
            spyEthosFilterQueryClient.getWithFilterMap( resourceName, "someVersion", filterMapStr );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "";
            String filterMapStr = "someFilter";
            spyEthosFilterQueryClient.getWithFilterMap( resourceName, "someVersion", filterMapStr );
        });
    }

    @Test
    public void getWithFilterMapTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        String filterMap = "someFilter";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).get( anyString(), anyMap() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithFilterMap( resource, version, filterMap );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).get( anyString(), anyMap() );
    }

    @Test
    public void getWithFilterMapUsingFilterMapThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            FilterMap filterMap = null;
            spyEthosFilterQueryClient.getWithFilterMap( resourceName, version, filterMap );
        });
    }

    @Test
    public void getWithFilterMapUsingFilterMapTest() throws IOException {
        EthosResponse expectedResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        FilterMap filterMap = new FilterMap.Builder()
                                  .withParameterPair("firstName", "John")
                                  .build();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedResponse).when(spyEthosFilterQueryClient).getWithFilterMap( resource, version, filterMap.toString() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithFilterMap( resource, version, filterMap );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithFilterMap( resource, version, filterMap.toString() );
    }

    @Test
    public void getPagesWithCriteriaFilterTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        int pageSize = EthosFilterQueryClient.DEFAULT_PAGE_SIZE;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesWithCriteriaFilter( resource, version, criteriaFilter, pageSize );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithCriteriaFilter( resource, criteriaFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesWithCriteriaFilter( resource, version, criteriaFilter, pageSize );
    }

    @Test
    public void getPagesWithCriteriaFilterUsingVersionTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int pageSize = EthosFilterQueryClient.DEFAULT_PAGE_SIZE;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesWithCriteriaFilter( resource, version, criteriaFilter, pageSize );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithCriteriaFilter( resource, version, criteriaFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesWithCriteriaFilter( resource, version, criteriaFilter, pageSize );
    }

    @Test
    public void getPagesWithCriteriaFilterUsingPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        int pageSize = 30;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesWithCriteriaFilter( resource, version, criteriaFilter, pageSize );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithCriteriaFilter( resource, criteriaFilter, pageSize );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesWithCriteriaFilter( resource, version, criteriaFilter, pageSize );
    }

    @Test
    public void getPagesWithCriteriaFilterUsingVersionAndPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int pageSize = 30;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, pageSize, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithCriteriaFilter( resource, version, criteriaFilter, pageSize );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, pageSize, 0 );
    }

    @Test
    public void getPagesFromOffsetWithCriteriaFilterTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        int offset = 20;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithCriteriaFilter( resource, criteriaFilter, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, offset );
    }

    @Test
    public void getPagesFromOffsetWithCriteriaFilterUsingVersionTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int pageSize = EthosFilterQueryClient.DEFAULT_PAGE_SIZE;
        int offset = 20;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, pageSize, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, pageSize, offset );
    }

    @Test
    public void getPagesFromOffsetWithCriteriaFilterUsingPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        int pageSize = 30;
        int offset = 20;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, pageSize, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithCriteriaFilter( resource, criteriaFilter, pageSize, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, pageSize, offset );
    }

    @Test
    public void getPagesFromOffsetWithCriteriaFilterUsingVersionAndPageSizeThrowsExceptionTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = null;
            String version = "someVersion";
            CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                            .withSimpleCriteria("firstName", "John")
                                            .buildCriteriaFilter();
            int pageSize = 20;
            int offset = 30;
            spyEthosFilterQueryClient.getPagesFromOffsetWithCriteriaFilter( resourceName, version, criteriaFilter, pageSize, offset );
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "";
            String version = "someVersion";
            CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                            .withSimpleCriteria("firstName", "John")
                                            .buildCriteriaFilter();
            int pageSize = 20;
            int offset = 30;
            spyEthosFilterQueryClient.getPagesFromOffsetWithCriteriaFilter( resourceName, version, criteriaFilter, pageSize, offset );
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            CriteriaFilter criteriaFilter = null;
            int pageSize = 20;
            int offset = 30;
            spyEthosFilterQueryClient.getPagesFromOffsetWithCriteriaFilter( resourceName, version, criteriaFilter, pageSize, offset );
        });
    }

    @Test
    public void getPagesFromOffsetWithCriteriaFilterUsingVersionAndPageSizeWithPagingTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int totalCount = 100;
        int pageSize = 30;
        int offset = 20;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        Pager pager = new Pager.Builder(resource)
                .forVersion(version)
                .withCriteriaFilter(criteriaFilter.toString())
                .withFilterMap(null)
                .withPageSize(pageSize)
                .fromOffset(offset)
                .withTotalCount(totalCount)
                .build();
        // Return the expected response when the method under test calls the overloaded get() method.
        Mockito.doReturn(pager).when(spyEthosFilterQueryClient).prepareForPaging( any(Pager.class) );
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).doPagingFromOffset( pager.getResourceName(),
                                                                                                        pager.getVersion(),
                                                                                                        pager.getCriteriaFilter(),
                                                                                                        pager.getTotalCount(),
                                                                                                        pager.getPageSize(),
                                                                                                        pager.getOffset() );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, pageSize, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).doPagingFromOffset( pager.getResourceName(),
                                                                                                                pager.getVersion(),
                                                                                                                pager.getCriteriaFilter(),
                                                                                                                pager.getTotalCount(),
                                                                                                                pager.getPageSize(),
                                                                                                                pager.getOffset() );
        assert( ethosResponseList != null );
        assert( ethosResponseList.size() == 2 );
    }

    @Test
    public void getPagesFromOffsetWithCriteriaFilterUsingVersionAndPageSizeWithoutPagingTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        int totalCount = 50;
        int pageSize = 60;
        int offset = 20;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        Pager pager = new Pager.Builder(resource)
                .forVersion(version)
                .withCriteriaFilter(criteriaFilter.toString())
                .withFilterMap(null)
                .withPageSize(pageSize)
                .fromOffset(offset)
                .withTotalCount(totalCount)
                .build();
        // Return the expected response when the method under test calls the overloaded get() method.
        Mockito.doReturn(pager).when(spyEthosFilterQueryClient).prepareForPaging( any(Pager.class) );
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithCriteriaFilter( pager.getResourceName(),
                                                                                                       pager.getVersion(),
                                                                                                       criteriaFilter );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithCriteriaFilter( resource, version, criteriaFilter, pageSize, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithCriteriaFilter( pager.getResourceName(),
                                                                                                                   pager.getVersion(),
                                                                                                                   criteriaFilter );
        assert( ethosResponseList != null );
        assert( ethosResponseList.size() == 1 );
    }

    @Test
    public void getPagesWithNamedQueryFilterTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        int pageSize = EthosFilterQueryClient.DEFAULT_PAGE_SIZE;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery(queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithNamedQueryFilter( resource, namedQueryFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize );
    }

    @Test
    public void getPagesWithNamedQueryFilterUsingVersionTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int pageSize = EthosFilterQueryClient.DEFAULT_PAGE_SIZE;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery(queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithNamedQueryFilter( resource, version, namedQueryFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize );
    }

    @Test
    public void getPagesWithNamedQueryFilterUsingPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        int pageSize = 30;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery(queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithNamedQueryFilter( resource, namedQueryFilter, pageSize );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize );
    }

    @Test
    public void getPagesWithNamedQueryFilterUsingVersionAndPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int pageSize = 30;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery(queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize, 0 );
    }

    @Test
    public void getPagesFromOffsetWithNamedQueryFilterTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        int offset = 20;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery(queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithNamedQueryFilter( resource, namedQueryFilter, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, offset );
    }

    @Test
    public void getPagesFromOffsetWithNamedQueryFilterUsingVersionTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int pageSize = EthosFilterQueryClient.DEFAULT_PAGE_SIZE;
        int offset = 20;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery(queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize, offset );
    }

    @Test
    public void getPagesFromOffsetWithNamedQueryFilterUsingPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        int pageSize = 30;
        int offset = 20;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery(queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithNamedQueryFilter( resource, namedQueryFilter, pageSize, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize, offset );
    }

    @Test
    public void getPagesFromOffsetWithNamedQueryFilterUsingVersionAndPageSizeThrowsExceptionTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = null;
            String version = "someVersion";
            String queryName = "someQuery";
            String queryKey = "someKey";
            String queryValue = "someValue";
            NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                                .withNamedQuery(queryName, queryKey, queryValue)
                                                .buildNamedQueryFilter();
            int pageSize = 20;
            int offset = 30;
            spyEthosFilterQueryClient.getPagesFromOffsetWithNamedQueryFilter( resourceName, version, namedQueryFilter, pageSize, offset );
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "";
            String version = "someVersion";
            String queryName = "someQuery";
            String queryKey = "someKey";
            String queryValue = "someValue";
            NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                                .withNamedQuery(queryName, queryKey, queryValue)
                                                .buildNamedQueryFilter();
            int pageSize = 20;
            int offset = 30;
            spyEthosFilterQueryClient.getPagesFromOffsetWithNamedQueryFilter( resourceName, version, namedQueryFilter, pageSize, offset );
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            NamedQueryFilter namedQueryFilter = null;
            int pageSize = 20;
            int offset = 30;
            spyEthosFilterQueryClient.getPagesFromOffsetWithNamedQueryFilter( resourceName, version, namedQueryFilter, pageSize, offset );
        });
    }

    @Test
    public void getPagesFromOffsetWithNamedQueryFilterUsingVersionAndPageSizeWithPagingTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int totalCount = 100;
        int pageSize = 30;
        int offset = 20;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                                            .withNamedQuery(queryName, queryKey, queryValue)
                                            .buildNamedQueryFilter();
        Pager pager = new Pager.Builder(resource)
                .forVersion(version)
                .withNamedQueryFilter(namedQueryFilter.toString())
                .withPageSize(pageSize)
                .fromOffset(offset)
                .withTotalCount(totalCount)
                .build();
        // Return the expected response when the method under test calls the overloaded get() method.
        Mockito.doReturn(pager).when(spyEthosFilterQueryClient).prepareForPaging( any(Pager.class) );
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).doPagingFromOffset( pager.getResourceName(),
                pager.getVersion(),
                pager.getNamedQueryFilter(),
                pager.getTotalCount(),
                pager.getPageSize(),
                pager.getOffset() );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).doPagingFromOffset( pager.getResourceName(),
                pager.getVersion(),
                pager.getNamedQueryFilter(),
                pager.getTotalCount(),
                pager.getPageSize(),
                pager.getOffset() );
        assert( ethosResponseList != null );
        assert( ethosResponseList.size() == 2 );
    }

    @Test
    public void getPagesFromOffsetWithNamedQueryFilterUsingVersionAndPageSizeWithoutPagingTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        int totalCount = 50;
        int pageSize = 60;
        int offset = 20;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                .withNamedQuery(queryName, queryKey, queryValue)
                .buildNamedQueryFilter();
        Pager pager = new Pager.Builder(resource)
                .forVersion(version)
                .withNamedQueryFilter(namedQueryFilter.toString())
                .withPageSize(pageSize)
                .fromOffset(offset)
                .withTotalCount(totalCount)
                .build();
        // Return the expected response when the method under test calls the overloaded get() method.
        Mockito.doReturn(pager).when(spyEthosFilterQueryClient).prepareForPaging( any(Pager.class) );
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithNamedQueryFilter( pager.getResourceName(),
                pager.getVersion(),
                namedQueryFilter );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithNamedQueryFilter( resource, version, namedQueryFilter, pageSize, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithNamedQueryFilter( pager.getResourceName(),
                pager.getVersion(),
                namedQueryFilter );
        assert( ethosResponseList != null );
        assert( ethosResponseList.size() == 1 );
    }

    @Test
    public void getPagesWithFilterMapTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int pageSize = EthosFilterQueryClient.DEFAULT_PAGE_SIZE;
        FilterMap filterMap = new FilterMap.Builder()
                                  .withParameterPair( "firstName", "John")
                                  .build();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesWithFilterMap( resource, version, filterMap, pageSize );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithFilterMap( resource, version, filterMap );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesWithFilterMap( resource, version, filterMap, pageSize );
    }

    @Test
    public void getPagesWithFilterMapUsingPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int pageSize = 30;
        int offset = 0;
        FilterMap filterMap = new FilterMap.Builder()
                .withParameterPair( "firstName", "John")
                .build();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesFromOffsetWithFilterMap( resource, version, filterMap, pageSize, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithFilterMap( resource, version, filterMap, pageSize );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithFilterMap( resource, version, filterMap, pageSize, offset );
    }

    @Test
    public void getPagesWithFilterMapUsingOffsetTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int pageSize = EthosFilterQueryClient.DEFAULT_PAGE_SIZE;
        int offset = 30;
        FilterMap filterMap = new FilterMap.Builder()
                .withParameterPair( "firstName", "John")
                .build();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).getPagesFromOffsetWithFilterMap( resource, version, filterMap, pageSize, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithFilterMap( resource, version, filterMap, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithFilterMap( resource, version, filterMap, pageSize, offset );
    }


    @Test
    public void getPagesFromOffsetWithFilterMapWithPagingTest() throws IOException {
        List<EthosResponse> expectedEthosResponseList = buildEthosResponseList();
        String resource = "someResource";
        String version = "someVersion";
        int totalCount = 100;
        int pageSize = 30;
        int offset = 20;
        FilterMap filterMap = new FilterMap.Builder()
                                  .withParameterPair("firstName", "John")
                                  .build();
        Pager pager = new Pager.Builder(resource)
                .forVersion(version)
                .withCriteriaFilter(null)
                .withFilterMap(filterMap.toString())
                .withPageSize(pageSize)
                .fromOffset(offset)
                .withTotalCount(totalCount)
                .build();
        // Return the expected response when the method under test calls the overloaded get() method.
        Mockito.doReturn(pager).when(spyEthosFilterQueryClient).prepareForPaging( any(Pager.class) );
        Mockito.doReturn(expectedEthosResponseList).when(spyEthosFilterQueryClient).doPagingFromOffset( pager.getResourceName(),
                                                                                                        pager.getVersion(),
                                                                                                        pager.getFilterMap(),
                                                                                                        pager.getTotalCount(),
                                                                                                        pager.getPageSize(),
                                                                                                        pager.getOffset() );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithFilterMap( resource, version, filterMap, pageSize, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).doPagingFromOffset( pager.getResourceName(),
                                                                                                                pager.getVersion(),
                                                                                                                pager.getFilterMap(),
                                                                                                                pager.getTotalCount(),
                                                                                                                pager.getPageSize(),
                                                                                                                pager.getOffset() );
        assert( ethosResponseList != null );
        assert( ethosResponseList.size() == 2 );
    }

    @Test
    public void getPagesFromOffsetWithFilterMapWithoutPagingTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        int totalCount = 50;
        int pageSize = 60;
        int offset = 20;
        FilterMap filterMap = new FilterMap.Builder()
                                  .withParameterPair("firstName", "John")
                                  .build();
        Pager pager = new Pager.Builder(resource)
                .forVersion(version)
                .withCriteriaFilter(null)
                .withFilterMap(filterMap.toString())
                .withPageSize(pageSize)
                .fromOffset(offset)
                .withTotalCount(totalCount)
                .build();
        // Return the expected response when the method under test calls the overloaded get() method.
        Mockito.doReturn(pager).when(spyEthosFilterQueryClient).prepareForPaging( any(Pager.class) );
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithFilterMap( pager.getResourceName(),
                                                                                                      pager.getVersion(),
                                                                                                      filterMap );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithFilterMap( resource, version, filterMap, pageSize, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithFilterMap( pager.getResourceName(),
                                                                                                              pager.getVersion(),
                                                                                                              filterMap );
        assert( ethosResponseList != null );
        assert( ethosResponseList.size() == 1 );
    }

    @Test
    public void getTotalCountWithCriteriaFilterTest() throws IOException {
        int expectedTotalCount = 75;
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedTotalCount).when(spyEthosFilterQueryClient).getTotalCount( resource, version, criteriaFilter );
        // Run the test.
        int totalCount = spyEthosFilterQueryClient.getTotalCount( resource, criteriaFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getTotalCount( resource, version, criteriaFilter );
        assert( totalCount == expectedTotalCount );
    }

    @Test
    public void getTotalCountWithCriteriaFilterIs0Test() throws IOException {
        int expectedTotalCount = 0;
        String resource = null;
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Run the test.
        int totalCount = spyEthosFilterQueryClient.getTotalCount( resource, criteriaFilter );
        // Verify the result.
        assert( totalCount == expectedTotalCount );

        // Run again with empty resource.
        resource = "";
        totalCount = spyEthosFilterQueryClient.getTotalCount( resource, criteriaFilter );
        // Verify the result.
        assert( totalCount == expectedTotalCount );

        // Run again with null criteriaFilter.
        resource = "someResource";
        criteriaFilter = null;
        totalCount = spyEthosFilterQueryClient.getTotalCount( resource, criteriaFilter );
        // Verify the result.
        assert( totalCount == expectedTotalCount );
    }

    @Test
    public void getTotalCountWithVersionAndCriteriaFilterTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithCriteriaFilter( resource, version, criteriaFilter.toString() );
        // Run the test.
        int totalCount = spyEthosFilterQueryClient.getTotalCount( resource, version, criteriaFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithCriteriaFilter( resource, version, criteriaFilter.toString() );
        Header header = expectedEthosResponse.getHeader( EthosFilterQueryClient.HDR_X_TOTAL_COUNT );
        int expectedTotalCount = Integer.valueOf( header.getValue() );
        assert( totalCount == expectedTotalCount );
    }

    @Test
    public void getTotalCountWithNamedQueryFilterTest() throws IOException {
        int expectedTotalCount = 75;
        String resource = "someResource";
        String version = EthosFilterQueryClient.DEFAULT_VERSION;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                .withNamedQuery(queryName, queryKey, queryValue)
                .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedTotalCount).when(spyEthosFilterQueryClient).getTotalCount( resource, version, namedQueryFilter );
        // Run the test.
        int totalCount = spyEthosFilterQueryClient.getTotalCount( resource, namedQueryFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getTotalCount( resource, version, namedQueryFilter );
        assert( totalCount == expectedTotalCount );
    }

    @Test
    public void getTotalCountWithNamedQueryFilterIs0Test() throws IOException {
        int expectedTotalCount = 0;
        String resource = null;
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                .withNamedQuery(queryName, queryKey, queryValue)
                .buildNamedQueryFilter();
        // Run the test.
        int totalCount = spyEthosFilterQueryClient.getTotalCount( resource, namedQueryFilter );
        // Verify the result.
        assert( totalCount == expectedTotalCount );

        // Run again with empty resource.
        resource = "";
        totalCount = spyEthosFilterQueryClient.getTotalCount( resource, namedQueryFilter );
        // Verify the result.
        assert( totalCount == expectedTotalCount );

        // Run again with null criteriaFilter.
        resource = "someResource";
        namedQueryFilter = null;
        totalCount = spyEthosFilterQueryClient.getTotalCount( resource, namedQueryFilter );
        // Verify the result.
        assert( totalCount == expectedTotalCount );
    }

    @Test
    public void getTotalCountWithVersionAndNamedQueryFilterTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        String queryName = "someQuery";
        String queryKey = "someKey";
        String queryValue = "someValue";
        NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
                .withNamedQuery(queryName, queryKey, queryValue)
                .buildNamedQueryFilter();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithNamedQueryFilter( resource, version, namedQueryFilter.toString() );
        // Run the test.
        int totalCount = spyEthosFilterQueryClient.getTotalCount( resource, version, namedQueryFilter );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithNamedQueryFilter( resource, version, namedQueryFilter.toString() );
        Header header = expectedEthosResponse.getHeader( EthosFilterQueryClient.HDR_X_TOTAL_COUNT );
        int expectedTotalCount = Integer.valueOf( header.getValue() );
        assert( totalCount == expectedTotalCount );
    }

    @Test
    public void getTotalCountWithFilterMapIs0Test() throws IOException {
        int expectedTotalCount = 0;
        String resource = null;
        String version = "someVersion";
        FilterMap filterMap = new FilterMap.Builder()
                                 .withParameterPair("firstName", "John")
                                 .build();
        // Run the test.
        int totalCount = spyEthosFilterQueryClient.getTotalCount( resource, version, filterMap );
        // Verify the result.
        assert( totalCount == expectedTotalCount );

        // Run again with empty resource.
        resource = "";
        totalCount = spyEthosFilterQueryClient.getTotalCount( resource, version, filterMap );
        // Verify the result.
        assert( totalCount == expectedTotalCount );

        // Run again with null criteriaFilter.
        resource = "someResource";
        filterMap = null;
        totalCount = spyEthosFilterQueryClient.getTotalCount( resource, version, filterMap );
        // Verify the result.
        assert( totalCount == expectedTotalCount );
    }

    @Test
    public void getTotalCountWithVersionAndFilterMapTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        FilterMap filterMap = new FilterMap.Builder()
                                  .withParameterPair("firstName", "John")
                                  .build();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithFilterMap( resource, version, filterMap.toString() );
        // Run the test.
        int totalCount = spyEthosFilterQueryClient.getTotalCount( resource, version, filterMap );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithFilterMap( resource, version, filterMap.toString() );
        Header header = expectedEthosResponse.getHeader( EthosFilterQueryClient.HDR_X_TOTAL_COUNT );
        int expectedTotalCount = Integer.valueOf( header.getValue() );
        assert( totalCount == expectedTotalCount );
    }

    @Test
    public void prepareForPagingReturnsNullTest() throws IOException {
        Pager pager = null;
        Pager resultPager = spyEthosFilterQueryClient.prepareForPaging( pager );
        assert( resultPager == null );
    }

    @Test
    public void prepareForPagingTest() throws IOException {
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        Pager pager = new Pager.Builder("someResource")
                          .forVersion(null)
                          .fromOffset(-1)
                          .withTotalCount(100)
                          .withCriteriaFilter(criteriaFilter.toString())
                          .withPageSize(30)
                          .build();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(pager).when(spyEthosFilterQueryClient).preparePagerForTotalCount( pager );
        Mockito.doReturn(pager).when(spyEthosFilterQueryClient).preparePagerForPageSize( pager );
        // Run the test.
        Pager resultPager = spyEthosFilterQueryClient.prepareForPaging( pager );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).preparePagerForTotalCount( pager );
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).preparePagerForPageSize( pager );
        assert( resultPager != null );
        assert( resultPager.getCriteriaFilter() != null );
        // The criteriaFilter should have been encoded, so decode it and compare to the criteriaFilter.toString().
        String encodedCriteriaFilter = resultPager.getCriteriaFilter();
        String decodedCriteriaFilter = URLDecoder.decode( encodedCriteriaFilter, "UTF-8" );
        assert( criteriaFilter.toString().equals(decodedCriteriaFilter) );
    }

    @Test
    public void preparePagerForTotalCountWithCriteriaFilterTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        CriteriaFilter criteriaFilter = new SimpleCriteria.Builder()
                                        .withSimpleCriteria("firstName", "John")
                                        .buildCriteriaFilter();
        Pager pager = new Pager.Builder("someResource")
                .forVersion("someVersion")
                .withCriteriaFilter(criteriaFilter.toString())
                .build();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithCriteriaFilter( pager.getResourceName(), pager.getVersion(), pager.getCriteriaFilter() );
        // Run the test.
        Pager resultPager = spyEthosFilterQueryClient.preparePagerForTotalCount( pager );
        // Verify the result.
        assert( resultPager != null );
        int expectedTotalCount = Integer.valueOf( expectedEthosResponse.getHeader(EthosFilterQueryClient.HDR_X_TOTAL_COUNT).getValue() );
        assert( resultPager.getTotalCount() == expectedTotalCount );
    }

    @Test
    public void preparePagerForTotalCountWithFilterMapTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        FilterMap filterMap = new FilterMap.Builder()
                                  .withParameterPair("lastName", "Smith")
                                  .build();
        Pager pager = new Pager.Builder("someResource")
                .forVersion("someVersion")
                .withCriteriaFilter(null)
                .withFilterMap(filterMap.toString())
                .build();
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithFilterMap( pager.getResourceName(), pager.getVersion(), pager.getFilterMap() );
        // Run the test.
        Pager resultPager = spyEthosFilterQueryClient.preparePagerForTotalCount( pager );
        // Verify the result.
        assert( resultPager != null );
        int expectedTotalCount = Integer.valueOf( expectedEthosResponse.getHeader(EthosFilterQueryClient.HDR_X_TOTAL_COUNT).getValue() );
        assert( resultPager.getTotalCount() == expectedTotalCount );
    }

    @Test
    public void preparePagerForPageSizeWhenNotUsingDefaultPageSizeTest() throws IOException {
        Pager pager = new Pager.Builder("someResource")
                .forVersion("someVersion")
                .withPageSize(20)
                .build();
        // Run the test.
        Pager resultPager = spyEthosFilterQueryClient.preparePagerForPageSize( pager );
        // Verify the result.
        assert( resultPager != null );
        assert( resultPager.getPageSize() == pager.getPageSize() );
    }

    @Test
    public void preparePagerForPageSizeWhenEthosResponseIsNullTest() throws IOException {
        Pager pager = new Pager.Builder("someResource")
                .forVersion("someVersion")
                .withPageSize(-1)
                .build();
        // Run the test.
        Pager resultPager = spyEthosFilterQueryClient.preparePagerForPageSize( pager );
        // Verify the result.
        assert( resultPager != null );
        assert( resultPager.getPageSize() == EthosFilterQueryClient.DEFAULT_MAX_PAGE_SIZE );
    }

    @Test
    public void preparePagerForPageSizeWhenEthosResponseContentIsNotNullTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        Pager pager = new Pager.Builder("someResource")
                .forVersion("someVersion")
                .withPageSize(EthosFilterQueryClient.DEFAULT_PAGE_SIZE)
                .withEthosResponse(expectedEthosResponse)
                .build();
        // Run the test.
        Pager resultPager = spyEthosFilterQueryClient.preparePagerForPageSize( pager );
        // Verify the result.
        assert( resultPager != null );
        assert( resultPager.getPageSize() == 1 );
    }

    @Test
    public void preparePagerForPageSizeWhenEthosResponseContentIsNullTest() throws IOException {
        String maxPageSize = "600";
        BasicHeader header = new BasicHeader( EthosFilterQueryClient.HDR_X_MAX_PAGE_SIZE, maxPageSize );
        Map<String,Header> headerMap = new HashMap<>();
        headerMap.put( EthosFilterQueryClient.HDR_X_MAX_PAGE_SIZE, header );
        EthosResponse expectedEthosResponse = new EthosResponse( headerMap, null, 200 );
        Pager pager = new Pager.Builder("someResource")
                .forVersion("someVersion")
                .withPageSize(EthosFilterQueryClient.DEFAULT_PAGE_SIZE)
                .withEthosResponse(expectedEthosResponse)
                .build();
        // Run the test.
        Pager resultPager = spyEthosFilterQueryClient.preparePagerForPageSize( pager );
        // Verify the result.
        assert( resultPager != null );
        assert( resultPager.getPageSize() == Integer.valueOf(maxPageSize) );

        // Run again with no HDR_X_MAX_PAGE_SIZE header, result should be the DEFAULT_MAX_PAGE_SIZE.
        headerMap = new HashMap<>();
        expectedEthosResponse = new EthosResponse( headerMap, null, 200 );
        pager = new Pager.Builder("someResource")
                .forVersion("someVersion")
                .withPageSize(EthosFilterQueryClient.DEFAULT_PAGE_SIZE)
                .withEthosResponse(expectedEthosResponse)
                .build();
        // Run the test.
        resultPager = spyEthosFilterQueryClient.preparePagerForPageSize( pager );
        // Verify the result.
        assert( resultPager != null );
        assert( resultPager.getPageSize() == EthosFilterQueryClient.DEFAULT_MAX_PAGE_SIZE );
    }

    @Test
    public void getWithQAPIThrowsExceptionTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = null;
            String version = "someVersion";
            String requestBody = "someBody";
            spyEthosFilterQueryClient.getWithQAPI( resourceName, version, requestBody );
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "";
            String version = "someVersion";
            String requestBody = "someBody";
            spyEthosFilterQueryClient.getWithQAPI( resourceName, version, requestBody );
        });
    }

    @Test
    public void getWithQAPIUsingVersionTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        String requestBody = "{\"someLabel\":\"someValue\"}";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).post( anyString(), anyMap(), anyString() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithQAPI( resource, version, requestBody );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).post( anyString(), anyMap(), anyString() );
    }

    @Test
    public void getWithQAPITest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        String requestBody = "{\"someLabel\":\"someValue\"}";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithQAPI( resource, EthosProxyClient.DEFAULT_VERSION, requestBody );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithQAPI( resource, requestBody );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithQAPI( resource, EthosProxyClient.DEFAULT_VERSION, requestBody );
    }

    @Test
    public void getWithQAPIUsingJsonNodeRequestBodyThrowsExceptionTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            JsonNode requestBody = null;
            spyEthosFilterQueryClient.getWithQAPI( resourceName, version, requestBody );
        });
    }

    @Test
    public void getWithQAPIUsingJsonNodeRequestBodyAndVersionTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        String version = "someVersion";
        ObjectNode requestBodyNode = JsonNodeFactory.instance.objectNode();
        requestBodyNode.put("someLabel", "someValue");
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithQAPI( resource, version, requestBodyNode.toString() );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithQAPI( resource, version, requestBodyNode );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithQAPI( resource, version, requestBodyNode.toString() );
    }

    @Test
    public void getWithQAPIUsingJsonNodeRequestBodyTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        ObjectNode requestBodyNode = JsonNodeFactory.instance.objectNode();
        requestBodyNode.put("someLabel", "someValue");
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithQAPI( resource, EthosProxyClient.DEFAULT_VERSION, requestBodyNode );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithQAPI( resource, requestBodyNode );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithQAPI( resource, EthosProxyClient.DEFAULT_VERSION, requestBodyNode );
    }

    @Test
    public void getWithQAPIUsingGenericObjectRequestBodyThrowsExceptionTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            SomeQAPIRequestBody requestBody = null;
            spyEthosFilterQueryClient.getWithQAPI( resourceName, version, requestBody );
        });
    }

    @Test
    public void getWithQAPIUsingGenericObjectRequestBodyAndVersionTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        SomeQAPIRequestBody someQAPIRequestBody = new SomeQAPIRequestBody();
        someQAPIRequestBody.setSomeProperty("someProperty");
        String version = "someVersion";
        EthosRequestConverter ethosRequestConverter = new EthosRequestConverter();
        String requestBodyStr = ethosRequestConverter.toJsonString( someQAPIRequestBody );
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithQAPI( resource, version, requestBodyStr );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithQAPI( resource, version, someQAPIRequestBody );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithQAPI( resource, version, requestBodyStr );
    }

    @Test
    public void getWithQAPIUsingGenericObjectRequestBodyTest() throws IOException {
        EthosResponse expectedEthosResponse = buildEthosResponse();
        String resource = "someResource";
        SomeQAPIRequestBody someQAPIRequestBody = new SomeQAPIRequestBody();
        someQAPIRequestBody.setSomeProperty("someProperty");
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getWithQAPI( resource, EthosProxyClient.DEFAULT_VERSION, someQAPIRequestBody );
        // Run the test.
        EthosResponse ethosResponse = spyEthosFilterQueryClient.getWithQAPI( resource, someQAPIRequestBody );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getWithQAPI( resource, EthosProxyClient.DEFAULT_VERSION, someQAPIRequestBody );
    }

    @Test
    public void getPagesWithQAPITest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        String requestBody = "{\"someLabel\":\"someValue\"}";
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, EthosProxyClient.DEFAULT_VERSION, requestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithQAPI(resourceName, requestBody);
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, EthosProxyClient.DEFAULT_VERSION, requestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
    }

    @Test
    public void getPagesWithQAPIUsingVersionTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        String requestBody = "{\"someLabel\":\"someValue\"}";
        String version = "someVersion";
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, version, requestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithQAPI(resourceName, version, requestBody);
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, version, requestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
    }

    @Test
    public void getPagesWithQAPIUsingVersionAndPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        String version = "someVersion";
        String requestBody = "{\"someLabel\":\"someValue\"}";
        int pageSize = 1;
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, version, requestBody, pageSize, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithQAPI(resourceName, version, requestBody, pageSize);
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, version, requestBody, pageSize, 0 );
    }

    @Test
    public void getPagesWithQAPIUsingJsonNodeRequestBody() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        ObjectNode requestBodyNode = JsonNodeFactory.instance.objectNode();
        requestBodyNode.put("someLabel", "someValue");
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, EthosProxyClient.DEFAULT_VERSION, requestBodyNode, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithQAPI(resourceName, requestBodyNode);
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, EthosProxyClient.DEFAULT_VERSION, requestBodyNode, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
    }

    @Test
    public void getPagesWithQAPIUsingJsonNodeRequestBodyAndVersionTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        ObjectNode requestBodyNode = JsonNodeFactory.instance.objectNode();
        requestBodyNode.put("someLabel", "someValue");
        String version = "someVersion";
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, version, requestBodyNode, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithQAPI(resourceName, version, requestBodyNode);
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, version, requestBodyNode, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
    }

    @Test
    public void getPagesWithQAPIUsingJsonNodeRequestBodyAndVersionAndPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        ObjectNode requestBodyNode = JsonNodeFactory.instance.objectNode();
        requestBodyNode.put("someLabel", "someValue");
        String version = "someVersion";
        int pageSize = 1;
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, version, requestBodyNode, pageSize, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithQAPI(resourceName, version, requestBodyNode, pageSize);
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, version, requestBodyNode, pageSize, 0 );
    }
    
    @Test
    public void getPagesWithQAPIUsingGenericObjectRequestBodyTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resource = "someResource";
        SomeQAPIRequestBody someQAPIRequestBody = new SomeQAPIRequestBody();
        someQAPIRequestBody.setSomeProperty("someProperty");
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resource, EthosProxyClient.DEFAULT_VERSION, someQAPIRequestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithQAPI( resource, someQAPIRequestBody );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resource, EthosProxyClient.DEFAULT_VERSION, someQAPIRequestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
    }

    @Test
    public void getPagesWithQAPIUsingGenericObjectRequestBodyUsingVersionTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resource = "someResource";
        SomeQAPIRequestBody someQAPIRequestBody = new SomeQAPIRequestBody();
        someQAPIRequestBody.setSomeProperty("someProperty");
        String version = "someVersion";
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resource, version, someQAPIRequestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithQAPI( resource, version, someQAPIRequestBody );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resource, version, someQAPIRequestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, 0 );
    }

    @Test
    public void getPagesWithQAPIUsingGenericObjectRequestBodyUsingVersionAndPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resource = "someResource";
        SomeQAPIRequestBody someQAPIRequestBody = new SomeQAPIRequestBody();
        someQAPIRequestBody.setSomeProperty("someProperty");
        String version = "someVersion";
        int pageSize = 1;
        // Return the expectedResponse when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resource, version, someQAPIRequestBody, pageSize, 0 );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesWithQAPI( resource, version, someQAPIRequestBody, pageSize );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resource, version, someQAPIRequestBody, pageSize, 0 );
    }

    @Test
    public void getPagesFromOffsetWithQAPIUsingVersionTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        String version = "someVersion";
        String requestBody = "{\"someLabel\":\"someValue\"}";
        int offset = 10;
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, version, requestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithQAPI(resourceName, version, offset, requestBody);
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, version, requestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, offset );
    }

    @Test
    public void getPagesFromOffsetWithQAPIUsingVersionAndPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        String version = "someVersion";
        String requestBody = "{\"someLabel\":\"someValue\"}";
        int pageSize = 1;
        int offset = 10;
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, version, requestBody, pageSize, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithQAPI(resourceName, version, requestBody, pageSize, offset);
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, version, requestBody, pageSize, offset );
    }

    @Test
    public void getPagesFromOffsetWithQAPIUsingJsonNodeRequestBodyAndVersionTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        ObjectNode requestBodyNode = JsonNodeFactory.instance.objectNode();
        requestBodyNode.put("someLabel", "someValue");
        String version = "someVersion";
        int offset = 100;
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, version, requestBodyNode, EthosProxyClient.DEFAULT_PAGE_SIZE, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithQAPI(resourceName, version, offset, requestBodyNode);
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, version, requestBodyNode, EthosProxyClient.DEFAULT_PAGE_SIZE, offset );
    }

    @Test
    public void getPagesFromOffsetWithQAPIUsingJsonNodeRequestBodyAndVersionAndPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        ObjectNode requestBodyNode = JsonNodeFactory.instance.objectNode();
        requestBodyNode.put("someLabel", "someValue");
        String version = "someVersion";
        int offset = 100;
        int pageSize = 1;
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, version, requestBodyNode, pageSize, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithQAPI(resourceName, version, requestBodyNode, pageSize, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, version, requestBodyNode, pageSize, offset );
    }

    @Test
    public void getPagesFromOffsetWithQAPIUsingJsonNodeRequestBodyAndVersionThrowsExceptionTest() throws IOException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            SomeQAPIRequestBody requestBody = null;
            int offset = 100;
            spyEthosFilterQueryClient.getPagesFromOffsetWithQAPI( resourceName, version, offset, requestBody );
        });
    }

    @Test
    public void getPagesFromOffsetWithQAPIUsingJsonNodeRequestBodyAndVersionAndPageSizeThrowsExceptionTest() throws IOException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String resourceName = "someResource";
            String version = "someVersion";
            SomeQAPIRequestBody requestBody = null;
            int offset = 100;
            int pageSize = 1;
            spyEthosFilterQueryClient.getPagesFromOffsetWithQAPI( resourceName, version, requestBody, pageSize, offset );
        });
    }

    @Test
    public void getPagesFromOffsetWithQAPIUsingGenericObjectRequestBodyAndVersionTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        SomeQAPIRequestBody someQAPIRequestBody = new SomeQAPIRequestBody();
        someQAPIRequestBody.setSomeProperty("someProperty");
        String version = "someVersion";
        int offset = 100;
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, version, someQAPIRequestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithQAPI(resourceName, version, offset, someQAPIRequestBody );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, version, someQAPIRequestBody, EthosProxyClient.DEFAULT_PAGE_SIZE, offset );
    }

    @Test
    public void getPagesFromOffsetWithQAPIUsingGenericObjectRequestBodyAndVersionAndPageSizeTest() throws IOException {
        List<EthosResponse> expectedEthosResponse = buildEthosResponseList();
        String resourceName = "someResource";
        SomeQAPIRequestBody someQAPIRequestBody = new SomeQAPIRequestBody();
        someQAPIRequestBody.setSomeProperty("someProperty");
        String version = "someVersion";
        int offset = 100;
        int pageSize = 1;
        // Return the expectedResponse List when the method under test calls the overloaded get() method.
        Mockito.doReturn(expectedEthosResponse).when(spyEthosFilterQueryClient).getPagesFromOffsetWithQAPI( resourceName, version, someQAPIRequestBody, pageSize, offset );
        // Run the test.
        List<EthosResponse> ethosResponseList = spyEthosFilterQueryClient.getPagesFromOffsetWithQAPI(resourceName, version, someQAPIRequestBody, pageSize, offset );
        // Verify the result.
        Mockito.verify(spyEthosFilterQueryClient, Mockito.times(1)).getPagesFromOffsetWithQAPI( resourceName, version, someQAPIRequestBody, pageSize, offset );
    }

}