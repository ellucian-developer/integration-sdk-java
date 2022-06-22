/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy;


import com.ellucian.ethos.integration.client.EthosResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PagerTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================

    @Test
    void buildPagerTest() {
        String resourceName = "someResource";
        String version = "someVersion";
        Pager.PagingType howToPage = Pager.PagingType.PAGE_FROM_OFFSET;
        int pageSize = 17;
        int numPages = 50;
        int numRows = 40;
        int totalCount = 200;
        int offset = 30;
        boolean shouldDoPaging = true;
        EthosResponse ethosResponse = new EthosResponse(new HashMap<>(), null, 200);

        // Build the pager.
        Pager pager = new Pager.Builder(resourceName)
                               .withTotalCount(totalCount)
                               .withPageSize(pageSize)
                               .withShouldDoPaging(shouldDoPaging)
                               .withEthosResponse(ethosResponse)
                               .forNumRows(numRows)
                               .forNumPages(numPages)
                               .forVersion(version)
                               .forPagingType(howToPage)
                               .fromOffset(offset)
                               .build();
        // Compare the values in the pager.
        assertTrue( pager.getResourceName() == resourceName );
        assertTrue( pager.getVersion() == version );
        assertTrue( pager.getEthosResponse() == ethosResponse );
        assertTrue( pager.getTotalCount() == totalCount );
        assertTrue( pager.getPageSize() == pageSize );
        assertTrue( pager.isShouldDoPaging() == shouldDoPaging );
        assertTrue( pager.getNumRows() == numRows );
        assertTrue( pager.getNumPages() == numPages );
        assertTrue( pager.getHowToPage() == howToPage );
        assertTrue( pager.getOffset() == offset);
    }
}