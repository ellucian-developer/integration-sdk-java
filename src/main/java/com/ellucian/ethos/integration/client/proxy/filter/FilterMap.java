/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

import java.util.*;

/**
 * Plain filter map used to support filtering for resource versions 6 and prior.
 * Contains a HashMap of key/value pairs for each key/value in the URL filter.
 * @since 0.0.1
 * @author David Kumar
 */
public class FilterMap {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private Map<String,String> filterPairMap;

    private FilterMap() {
        this.filterPairMap = new HashMap<>();
    }

    /**
     * Static inner class for building a FilterMap instance, following the builder pattern.
     */
    public static class Builder {
        /** Map of String key/value pairs containing filter parameters. */
        private Map<String,String> filterMap;

        /** No-arg constructor for the Builder */
        public Builder() {
            this.filterMap = new HashMap<>();
        }

        /**
         * Adds a key/value pair as a filter parameter to the filterMap.
         * @param filterKey The key for the filter parameter.
         * @param filterValue The value of the filter parameter.
         * @return This Builder for fluency in supporting multiple filter parameters.
         */
        public Builder withParameterPair( String filterKey, String filterValue ) {
            filterMap.put( filterKey, filterValue );
            return this;
        }

        /**
         * Builds a FilterMap instance with the parameter pairs previously provided.
         * @return An instance of a FilterMap.
         */
        public FilterMap build() {
            FilterMap filterMap = new FilterMap();
            filterMap.filterPairMap = this.filterMap;
            return filterMap;
        }
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Gets a list of filter pair keys from the filterPairMap.
     * @return A list of filter keys.
     */
    public List<String> getFilterMapKeys() {
        Set<String> keySet = filterPairMap.keySet();
        List<String> keyList = new ArrayList<>();
        keyList.addAll( keySet );
        return keyList;
    }

    /**
     * Gets a filter map value using the given key.
     * @param key The filter map key used to retrieve a filter map value.
     * @return The value associated with the given key in the filterPairMap.
     */
    public String getFilterMapValue( String key ) {
        return filterPairMap.get( key );
    }

    /**
     * Provides a string representation of this filterMap with proper syntax for
     * the request URL filter, e.g:  <code>?credentialValue=A00000718&amp;credentialType=bannerId</code>.
     * @return A string containing the filterPairMap key/values in proper syntax for the request URL filter.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "?" );
        List<String> keyList = getFilterMapKeys();
        for( String key : keyList ) {
            String value = getFilterMapValue( key );
            sb.append(key).append("=").append(value).append("&");
        }
        // Remove the last '&' char.
        sb.deleteCharAt( sb.length() - 1 );
        return sb.toString();
    }
}