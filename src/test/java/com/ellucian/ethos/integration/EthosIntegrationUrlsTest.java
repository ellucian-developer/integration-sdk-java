package com.ellucian.ethos.integration;

import com.ellucian.ethos.integration.authentication.SupportedRegions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EthosIntegrationUrlsTest {

    @Test
    void testConsumeNoParameters() {
        String url = EthosIntegrationUrls.consume(SupportedRegions.US, -1, 0);
        assertEquals(url, "https://integrate.elluciancloud.com/consume");
    }

    @Test
    void testConsumeLastProcessedId() {
        String url = EthosIntegrationUrls.consume(SupportedRegions.CANADA, 5, -1);
        assertEquals(url, "https://integrate.elluciancloud.ca/consume?lastProcessedID=5");
    }

    @Test
    void testConsumeLastProcessedIdZero() {
        String url = EthosIntegrationUrls.consume(SupportedRegions.EUROPE, 0, -1);
        assertEquals(url, "https://integrate.elluciancloud.ie/consume?lastProcessedID=0");
    }

    @Test
    void testConsumeLimit() {
        String url = EthosIntegrationUrls.consume(SupportedRegions.AUSTRALIA, -1, 1);
        assertEquals(url, "https://integrate.elluciancloud.com.au/consume?limit=1");
    }

    @Test
    void testConsumeBothParameters() {
        String url = EthosIntegrationUrls.consume(SupportedRegions.US, 10, 100);
        assertEquals(url, "https://integrate.elluciancloud.com/consume?lastProcessedID=10&limit=100");
    }

    @Test
    void testQapi() {
        String url = EthosIntegrationUrls.qapis(SupportedRegions.US, "persons");
        assertEquals(url, "https://integrate.elluciancloud.com/qapi/persons" );
    }

    @Test
    void testQapiCanada() {
        String url = EthosIntegrationUrls.qapis(SupportedRegions.CANADA, "persons");
        assertEquals(url, "https://integrate.elluciancloud.ca/qapi/persons" );
    }

    @Test
    void testQapiPaging() {
        String url = EthosIntegrationUrls.qapiPaging(SupportedRegions.US, "persons", 20, 30);
        assertEquals(url, "https://integrate.elluciancloud.com/qapi/persons?offset=20&limit=30" );
    }

    @Test
    void testQapiPagingCanada() {
        String url = EthosIntegrationUrls.qapiPaging(SupportedRegions.CANADA, "persons", 20, 30);
        assertEquals(url, "https://integrate.elluciancloud.ca/qapi/persons?offset=20&limit=30" );
    }

}
