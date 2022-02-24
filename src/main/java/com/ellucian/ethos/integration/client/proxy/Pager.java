/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy;


import com.ellucian.ethos.integration.client.EthosResponse;

/**
 * Data transfer object (DTO) used primarily within the SDK to easily specify various criteria supporting paging operations.
 * This class follows the builder pattern and contains an inner static Builder class used to build this object with the
 * various attributes/properties.
 * @since 0.0.1
 * @author David Kumar
 */
public class Pager {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /**
     * Various paging types to determine what kind of paging to be done.  This class is built with different attributes
     * depending on the pagingType (howToPage attribute).
     */
    public enum PagingType {
        /**
         * Page for all of the pages for a given resource, starting at the beginning (offset 0).
         */
        PAGE_ALL_PAGES("pageAllPages"),

        /**
         * Page up to the specified number of pages, starting at the beginning (offset 0).
         * e.g. Page some resource for 47 pages.
         */
        PAGE_TO_NUMPAGES("pageToNumPages"),

        /**
         * Page beginning from the given offset (row num) for all of the data.
         * e.g. Page for some resource from offset (row) 33 for all of the resource data.
         */
        PAGE_FROM_OFFSET("pageFromOffset"),

        /**
         * Page from the given offset for some number of pages.
         * e.g. Page for some resource from offset (row) 33 for 7 pages.
         */
        PAGE_FROM_OFFSET_FOR_NUMPAGES("pageFromOffsetForNumPages"),

        /**
         * Page up to some number of rows.
         * e.g. Page for some resource from the beginning (offset 0) up to row 77.
         */
        PAGE_TO_NUMROWS("pageToNumRows"),

        /**
         * Page from the given offset for some number of rows.
         * e.g. Page for some resource from offset (row) 33 up to row 77.
         */
        PAGE_FROM_OFFSET_FOR_NUMROWS("pageFromOffsetForNumRows");

        /**
         * The String value of this enum.
         */
        private String value;

        /**
         * Constructor for this enum taking the specified value.
         * @param value the value of this enum.
         */
        PagingType(final String value) {
            this.value = value;
        }

        /**
         * Gets the String value of this enum.
         * @return the value of this enum.
         */
        public String getValue() {
            return value;
        }

        /**
         * Returns the string value of this enum, same as <code>getValue()</code>
         * @return the string value of this enum.
         */
        @Override
        public String toString() {
            return this.getValue();
        }
    }

    /**
     * The resource name used when paging.
     */
    private String        resourceName;

    /**
     * The version specified when paging.
     */
    private String        version;

    /**
     * The optional request URL criteria filter used when paging.
     */
    private String        criteriaFilter;

    /**
     * The optional request URL named query filter used when paging.
     */
    private String        namedQueryFilter;

    /**
     * The optional request URL filter map used when paging.
     */
    private String        filterMap;

    /**
     * The pagingType for how to page.
     */
    private PagingType    howToPage;

    /**
     * The pageSize specified when paging.
     */
    private int           pageSize;

    /**
     * The number of pages to page for.
     */
    private int           numPages;

    /**
     * The number of rows to page for.  Data is still returned in pages up to the number of rows when this is specified.
     */
    private int           numRows;

    /**
     * The total count (of rows) for a given resource.
     */
    private int           totalCount;

    /**
     * The offset to begin paging from.
     */
    private int           offset;

    /**
     * Indicator of whether paging should be done or not for a given request.
     */
    private boolean       shouldDoPaging;

    /**
     * The EthosResponse from the initial GET request first made when determining if paging is needed, in order to get
     * the total count for the given resource.
     */
    private EthosResponse ethosResponse;

    /**
     * An inner static Builder class used for building the Pager object with various criteria.  This uses the builder
     * fluent API pattern.
     * All of the attributes in this Builder class correspond to the attributes in the containing Pager class.
     */
    public static class Builder {
        /**
         * The Ethos resource to page for.
         */
        private String        resourceName;

        /**
         * The version (media-type) of the ethos resource.
         */
        private String        version;

        /**
         * The optional request URL criteria filter used when paging.
         */
        private String        criteriaFilter;

        /**
         * The optional request URL named query filter used when paging.
         */
        private String        namedQueryFilter;

        /**
         * The optional request URL filter map used when paging.
         */
        private String        filterMap;

        /**
         * The page size to use when paging.
         */
        private int           pageSize;

        /**
         * The number of pages to page for.
         */
        private int           numPages;

        /**
         * The number of rows to page for.  Data is returned in pages up to the number of rows if specified.
         */
        private int           numRows;

        /**
         * The offset (row num) to begin paging from.
         */
        private int           offset;

        /**
         * The total count of rows for the given resource.
         */
        private int           totalCount;

        /**
         * How to page according to the PagingType enum.
         */
        private PagingType    pagingType;

        /**
         * Indicator of whether to do paging (true), or not (false).
         */
        private boolean       shouldDoPaging;

        /**
         * The EthosResponse from the initial Get request made used to get the total count.
         */
        private EthosResponse ethosResponse;

        /**
         * Assigns the given resourceName to the resourceName of this Builder.
         * @param resourceName the name of the Ethos resource.
         */
        public Builder( String resourceName ) {
            this.resourceName = resourceName;
        }

        /**
         * Assigns the specified version and returns this builder for fluent API functionality.
         * @param version The version of the Ethos resource (A.K.A media type).
         * @return This Builder with the version assigned.
         */
        public Builder forVersion( String version ) {
            this.version = version;
            return this;
        }

        /**
         * Assigns the specified request URL criteria filter and returns this builder for fluent API functionality.
         * Nulls out the namedQueryFilter and filterMap because there can only be one filter approach used at a time.
         * @param criteriaFilter The request URL criteria-based filter which can also be used when paging.
         * @return The Builder with the criteria filter assigned.
         */
        public Builder withCriteriaFilter(String criteriaFilter ) {
            this.criteriaFilter = criteriaFilter;
            this.namedQueryFilter = null;
            this.filterMap = null;
            return this;
        }

        /**
         * Assigns the specified request URL named query filter and returns this builder for fluent API functionality.
         * Nulls out the criteriaFilter and filterMap because there can only be one filter approach used at a time.
         * @param namedQueryFilter The request URL named query filter which can also be used when paging.
         * @return The Builder with the named query filter assigned.
         */
        public Builder withNamedQueryFilter(String namedQueryFilter ) {
            this.criteriaFilter = null;
            this.namedQueryFilter = namedQueryFilter;
            this.filterMap = null;
            return this;
        }

        /**
         * Assigns the specified request URL filter map and returns this builder for fluent API functionality.
         * Nulls out the criteriaFilter and namedQueryFilter because there can only be one filter approach used at a time.
         * @param filterMap The request URL filter map which can also be used when paging.
         * @return The Builder with the filter map assigned.
         */
        public Builder withFilterMap(String filterMap ) {
            this.criteriaFilter = null;
            this.namedQueryFilter = null;
            this.filterMap = filterMap;
            return this;
        }

        /**
         * Assigns the specified pageSize and returns this builder for fluent API functionality.
         * @param pageSize The pageSize (number of rows in each response) to page with.
         * @return This Builder with the pageSize assigned.
         */
        public Builder withPageSize( int pageSize ) {
            this.pageSize = pageSize;
            return this;
        }

        /**
         * Assigns the specified numPages and returns this builder for fluent API functionality.
         * @param numPages The number of pages to page for.
         * @return This Builder with the numPages assigned.
         */
        public Builder forNumPages( int numPages ) {
            this.numPages = numPages;
            return this;
        }

        /**
         * Assigns the specified numRows and returns this builder for fluent API functionality.
         * @param numRows The number of rows to page for.  Data is returned in pages up to the specified number of rows.
         * @return This Builder with the numRows assigned.
         */
        public Builder forNumRows( int numRows ) {
            this.numRows = numRows;
            return this;
        }

        /**
         * Assigns the specified offset and returns this builder for fluent API functionality.
         * @param offset The offset (row number) to start paging from.
         * @return This Builder with the offset assigned.
         */
        public Builder fromOffset( int offset ) {
            this.offset = offset;
            return this;
        }

        /**
         * Assigns the specified totalCount and returns this builder for fluent API functionality.
         * @param totalCount The total count of rows for the given resource.
         * @return This Builder with the totalCount assigned.
         */
        public Builder withTotalCount( int totalCount ) {
            this.totalCount = totalCount;
            return this;
        }

        /**
         * Assigns the specified PagingType for how to page and returns this builder for fluent API functionality.
         * @param pagingType Must be of the PagingType enumeration defined in the Pager class, and used to determine how to page.
         * @return This Builder with the pagingType assigned.
         */
        public Builder forPagingType(PagingType pagingType) {
            this.pagingType = pagingType;
            return this;
        }

        /**
         * Assigns the specified shouldDoPaging indicator and returns this builder for fluent API functionality.
         * @param shouldDoPaging Determines whether paging should be done, or not.
         * @return This Builder with the shouldDoPaging indicator assigned.
         */
        public Builder withShouldDoPaging(boolean shouldDoPaging) {
            this.shouldDoPaging = shouldDoPaging;
            return this;
        }

        /**
         * Assigns the specified EthosResponse and returns this builder for fluent API functionality.
         * @param ethosResponse The ethosResponse obtained from the initial GET request made to determine whether paging is needed or not.
         * @return This Builder with the ethosResponse assigned.
         */
        public Builder withEthosResponse(EthosResponse ethosResponse) {
            this.ethosResponse = ethosResponse;
            return this;
        }

        /**
         * Builds the Pager object with whatever attributes are specified from the various methods above.
         * @return A new Pager object containing whatever attributes are assigned.  Note that not all attributes may
         * have been assigned.
         */
        public Pager build() {
            Pager pager = new Pager();
            pager.resourceName = this.resourceName;
            pager.version = this.version;
            pager.criteriaFilter = this.criteriaFilter;
            pager.namedQueryFilter = this.namedQueryFilter;
            pager.filterMap = this.filterMap;
            pager.pageSize = this.pageSize;
            pager.numPages = this.numPages;
            pager.numRows = this.numRows;
            pager.offset = this.offset;
            pager.totalCount = this.totalCount;
            pager.howToPage = this.pagingType;
            pager.shouldDoPaging = this.shouldDoPaging;
            pager.ethosResponse = this.ethosResponse;
            return pager;
        }
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Gets the resource name as assigned by the Builder.
     * @return The name of the given resource.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Sets the resource name.
     * @param resourceName The resource name to set.
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Gets the version (media-type) as assigned by the Builder.
     * @return The version of the given resource.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version (media-type) of the given resource
     * @param version The resource version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the request URL criteria filter as assigned by the Builder.
     * @return The request URL criteria filter.
     */
    public String getCriteriaFilter() {
        return criteriaFilter;
    }

    /**
     * Sets the request URL criteria filter.
     * @param criteriaFilter The criteria filter contained in the request URL.
     */
    public void setCriteriaFilter(String criteriaFilter) {
        this.criteriaFilter = criteriaFilter;
    }

    /**
     * Gets the named query request URL filter as assigned by the Builder.
     * @return The request URL named query filter.
     */
    public String getNamedQueryFilter() {
        return namedQueryFilter;
    }

    /**
     * Sets the request URL named query filter.
     * @param namedQueryFilter The named query filter contained in the request URL.
     */
    public void setNamedQueryFilter(String namedQueryFilter) {
        this.namedQueryFilter = namedQueryFilter;
    }

    /**
     * Gets the filter map string as assigned by the Builder.
     * @return The request URL filter map.
     */
    public String getFilterMap() {
        return filterMap;
    }

    /**
     * Sets the request URL filter map string.
     * @param filterMap The request URL filter map value.
     */
    public void setFilterMap(String filterMap) {
        this.filterMap = filterMap;
    }

    /**
     * Gets the paging type to determine how to page.
     * @return A PagingType enum.
     */
    public PagingType getHowToPage() {
        return howToPage;
    }

    /**
     * Sets the payingType enum for how paging should be done.
     * @param howToPage The paging type enum to set.
     */
    public void setHowToPage(PagingType howToPage) {
        this.howToPage = howToPage;
    }

    /**
     * Gets the page size, or the number of rows in each page (response) as assigned by the Builder.
     * @return The page size.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Sets the page size.
     * @param pageSize The page size to set.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the number of pages to page for as assigned by the Builder.
     * @return The number of pages to page for.
     */
    public int getNumPages() {
        return numPages;
    }

    /**
     * Sets the number of pages to page for.
     * @param numPages The number of pages to page for.
     */
    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    /**
     * Gets the number of rows to page for as assigned by the Builder.
     * Data is returned in pages up to the number of rows (if specified).
     * @return The number of rows to page for.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Sets the number of rows to page for.
     * @param numRows The number of rows to page for.
     */
    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    /**
     * Gets the total count (number of rows) for the given resource, as assigned by the Builder.
     * @return The total count (number of rows) for the given resource.
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the total count (number of rows) for the given resource.
     * @param totalCount The total count (number of rows) for the given resource.
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * Gets the offset to begin paging from as assigned by the Builder.
     * @return The offset (row num) to start paging from.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the offset to begin paging from.
     * @param offset The offset to start paging from.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Gets the shouldDoPaging indicator as assigned by the Builder.
     * @return The indicator showing whether paging should be done (true), or not (false).
     */
    public boolean isShouldDoPaging() {
        return shouldDoPaging;
    }

    /**
     * Sets the shouldDoPaging indicator.
     * @param shouldDoPaging The indicator to determine whether paging should be done (true), or not (false).
     */
    public void setShouldDoPaging(boolean shouldDoPaging) {
        this.shouldDoPaging = shouldDoPaging;
    }

    /**
     * Gets the initial EthosResponse that was made to determine if paging was needed, as assigned by the Builder.
     * @return The ethosResponse (containing the total count) used to determine if paging was needed.
     */
    public EthosResponse getEthosResponse() {
        return ethosResponse;
    }

    /**
     * Sets the initial ethosResponse used to determine if paging is needed.
     * @param ethosResponse The initial ethosResponse used to determine if paging is needed.
     */
    public void setEthosResponse(EthosResponse ethosResponse) {
        this.ethosResponse = ethosResponse;
    }
}