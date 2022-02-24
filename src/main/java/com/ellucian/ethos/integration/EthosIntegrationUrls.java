package com.ellucian.ethos.integration;

import com.ellucian.ethos.integration.authentication.SupportedRegions;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used for building Ethos Integration URLs with various criteria.
 */
public class EthosIntegrationUrls {

    /**
     * A <code>Map&lt;SupportedRegions,String&gt;</code> of supported regions where each region is assigned the
     * appropriate country code top-level domain and/or second-level domain.
     * Supported regions include:
     * <ul>
     *     <li>US: .com</li>
     *     <li>CANADA: .ca</li>
     *     <li>EUROPE: .ie</li>
     *     <li>AUSTRALIA: .com.au</li>
     * </ul>
     */
    private static Map<SupportedRegions, String> regionUrlPostFix;

    static {
        regionUrlPostFix = new HashMap<SupportedRegions, String>();
        regionUrlPostFix.put(SupportedRegions.US, ".com");
        regionUrlPostFix.put(SupportedRegions.CANADA, ".ca");
        regionUrlPostFix.put(SupportedRegions.EUROPE, ".ie");
        regionUrlPostFix.put(SupportedRegions.AUSTRALIA, ".com.au");
    }

    /**
     * The main domain for Ethos Integration.
     */
    private static final String mainBaseUrl = "https://integrate.elluciancloud";

    /**
     * Builds a URL for interacting with the Proxy APIs through Ethos Integration.
     * @param region The appropriate supported region to build the URL with.
     * @param resource The Ethos resource the URL should contain.
     * @param id The (optional) ID for the given resource to build URLs for "get by ID" requests.
     * @return A String value containing the URL to use for interacting with Ethos Integration Proxy APIs.
     */
    public static String apis(SupportedRegions region, String resource, String id)
    {
        String url = buildUrl(region, "/api");
        if( resource != null && resource.trim().isEmpty() == false ) {
            StringBuilder sb = new StringBuilder();
            sb.append( url );
            sb.append( "/" );
            sb.append( resource );
            if( id != null && id.trim().isEmpty() == false ) {
                sb.append( "/" );
                sb.append( id );
            }
            url = sb.toString();
        }
        return url;
    }

    /**
     * Builds a URL for interacting with the proxy APIs through Ethos Integration supporting filters.
     * @param region The appropriate supported region to build the URL with.
     * @param resource The Ethos resource the URL should contain.
     * @param filter The resource filter the URL should contain.
     * @return A String value containing the URL to use for interacting with Ethos Integration Proxy APIs, supporting filters.
     */
    public static String apiFilter( SupportedRegions region, String resource, String filter ) {
       String url = apis( region, resource, null );
       StringBuilder sb = new StringBuilder();
       sb.append( url );
       if( filter != null && filter.isBlank() == false ) {
           sb.append( filter );
       }
       return sb.toString();
    }

    /**
     * Builds a URL for interacting with the proxy APIs through Ethos Integration supporting paging with filters.
     * @param region The appropriate supported region to build the URL with.
     * @param resource The Ethos resource the URL should contain.
     * @param filter The resource filter the URL should contain.
     * @param offset The row index from which to begin paging for data for the given resource.
     * @param pageSize The number of rows each response can contain.
     * @return A String value containing the URL to use for interacting with Ethos Integration Proxy APIs, supporting filters.
     */
    public static String apiFilterPaging( SupportedRegions region, String resource, String filter, int offset, int pageSize ) {
        String url = apiFilter( region, resource, filter );
        return addPaging( url, offset, pageSize );
    }

    /**
     * Builds a URL for interacting with the Proxy APIs through Ethos Integration, in support of paging.
     * @param region The appropriate supported region to build the URL with.
     * @param resource The Ethos resource the URL should contain.
     * @param offset The row index from which to begin paging for data for the given resource.
     * @param pageSize The number of rows each response can contain.
     * @return A String value containing the URL to use for interacting with Ethos Integration Proxy APIs, in support of paging.
     */
    public static String apiPaging( SupportedRegions region, String resource, int offset, int pageSize ) {
        String urlStr = apis( region, resource, null );
        return addPaging( urlStr, offset, pageSize );
    }

    /**
     * Builds a URL for interacting with the Errors API through Ethos Integration, in support of paging.
     * @param region The appropriate supported region to build the URL with.
     * @param offset The row index from which to begin paging for errors.
     * @param pageSize The number of errors (limit) each response can contain.
     * @return A String value containing the URL to use for interacting with Ethos Integration Errors API, in support of paging.
     */
    public static String errorsPaging( SupportedRegions region, int offset, int pageSize ) {
        String url = errors( region );
        return addPaging( url, offset, pageSize );
    }


    /**
     * Builds an Ethos Integration URL supporting the Errors API.
     * @param region The appropriate supported region to build the URL with.
     * @return A String value containing the URL to use for interacting with Ethos Integration Errors APIs.
     */
    public static String errors(SupportedRegions region)
    {
        return buildUrl(region, "/errors");
    }


    /**
     * Builds an Ethos Integration URL supporting the Token API.
     * @param region The appropriate supported region to build the URL with.
     * @return A String value containing the URL to use for interacting with Ethos Integration Token API.
     */
    public static String auth(SupportedRegions region)
    {
        return buildUrl(region, "/auth");
    }

    /**
     * Builds an Ethos Integration URL supporting the Application Configuration API.
     * @param region The appropriate supported region to build the URL with.
     * @return A String value containing the URL to use for interacting with Ethos Integration Application Configuration API.
     */
    public static String appConfig( SupportedRegions region ) {
        return buildUrl( region, "/appconfig" );
    }

    /**
     * Builds an Ethos Integration URL supporting the Available Resources API.
     * @param region The appropriate supported region to build the URL with.
     * @return A String value containing the URL to use for interacting with Ethos Integration Available Resources API.
     */
    public static String availableResources( SupportedRegions region ) {
        return buildUrl( region, "/admin/available-resources" );
    }

    /**
     * Builds an Ethos Integration URL supporting the consume API.
     * @param region The appropriate supported region to build the URL with.
     * @param lastProcessedID A value to use for the 'lastProcessedID' query parameter.  Any value of zero or greater will
     *                        be added to the URL as a query parameter.  If the value is less than zero, it will not be added
     *                        to the URL.
     * @param limit A value to use for the 'limit' query parameter.  Any value greater than zero will be added to the URL as
     *              a query parameter.  If the value is zero or less, it will not be added to the URL.
     * @return The URL to use for calling the Ethos Integration consume endpoint.
     */
    public static String consume(SupportedRegions region, long lastProcessedID, int limit) {
        StringBuilder params = new StringBuilder();
        // add lastProcessedID query param if it is at least zero
        if(lastProcessedID >= 0) {
            params.append("lastProcessedID=");
            params.append(lastProcessedID);
        }
        // add limit query param if it is greater than zero
        if(limit > 0) {
            if(params.length() > 0) {
                params.append("&");
            }
            params.append("limit=");
            params.append(limit);
        }

        String url = params.length() > 0 ? "/consume?" + params.toString() : "/consume";
        return buildUrl(region, url);
    }

    /**
     * Builds the URL with the mainBaseUrl, the supported region, and the correct path.
     * @param region The appropriate supported region to build the URL with.
     * @param urlEnd The correct path for the type of API the URL will be used with (<code>/api</code> for Proxy API URL,
     *               <code>/auth</code> for Token API URL, etc.).
     * @return A String value containing the URL to use for interacting with the desired Ethos Integration API.
     */
    private static String buildUrl(SupportedRegions region, String urlEnd)
    {
        StringBuilder sb = new StringBuilder();
        sb.append( mainBaseUrl );
        sb.append( regionUrlPostFix.get(region) );
        sb.append( urlEnd );
        return sb.toString();
    }

    /**
     * Adds paging filter criteria to the given URL string.
     * @param urlStr The URL string to add paging criteria to.
     * @param offset The offset param to page from.
     * @param pageSize The limit param to page with.
     * @return A URL string containing the offset and limit params for paging.
     */
    private static String addPaging( String urlStr, int offset, int pageSize ) {
        StringBuilder sb = new StringBuilder( urlStr );
        if( offset >= 0 && pageSize > 0 ) {
            // If offset is >= 0 and pageSize is > 0 then use them to build the URL.
            if( urlStr.contains("?") ) {
                sb.append("&offset=" );
            }
            else {
                sb.append("?offset=");
            }
            sb.append( offset );
            sb.append( "&limit=" );
            sb.append( pageSize );
        }
        else if( offset >= 0 ) { // Offset >= 0, so pageSize must be negative.  Do not include pageSize.
            if( urlStr.contains("?") ) {
                sb.append("&offset=" );
            }
            else {
                sb.append("?offset=");
            }
            sb.append( offset );
        }
        else if( pageSize > 0 ) { // pageSize > 0, so offset must be negative.  Do not include offset.
            if( urlStr.contains("?") ) {
                sb.append("&limit=" );
            }
            else {
                sb.append("?limit=");
            }
            sb.append( pageSize );
        }
        // By default, if both offset and pageSize are negative, they will not be included in the URL.
        return sb.toString();
    }
}
