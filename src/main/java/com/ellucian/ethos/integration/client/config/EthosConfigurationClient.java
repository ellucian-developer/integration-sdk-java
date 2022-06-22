/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.config;

import com.ellucian.ethos.integration.EthosIntegrationUrls;
import com.ellucian.ethos.integration.client.EthosClient;
import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.EthosResponseConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.*;

/**
 * An {@link EthosClient EthosClient} used to retrieve configuration data from Ethos Integration.  This could include
 * things like available versions of a given Ethos resource, and other data available from the <code>/appConfig</code>
 * and/or <code>admin/available-resources</code> Ethos Integration APIs.
 * <p>
 * Note that the preferred way to instantiate this class is through the {@link EthosClientBuilder EthosClientBuilder}.
 * </p>
 */
public class EthosConfigurationClient extends EthosClient {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /** Constant used internally when processing version lists for semantic versions. */
    private static final String SEMANTIC_LIST = "semanticList";

    /** Constant used internally when processing version lists for non-semantic versions. */
    private static final String NONSEMANTIC_LIST = "nonSemanticList";

    /** Constant used when accessing a resources property from a JsonNode. */
    public static final String JSON_ACCESSOR_RESOURCES                = "/resources";

    /** Constant used when accessing a name property from a JsonNode. */
    public static final String JSON_ACCESSOR_NAME                     = "/name";

    /** Constant used when accessing an id property from a JsonNode. */
    public static final String JSON_ACCESSOR_ID                       = "/id";

    /** Constant used when accessing an appId property from a JsonNode. */
    public static final String JSON_ACCESSOR_APPID                    = "/appId";

    /** Constant used when accessing an applicationId property from a JsonNode. */
    public static final String JSON_ACCESSOR_APPICATIONID             = "/applicationId";

    /** Constant used when accessing an appName property from a JsonNode. */
    public static final String JSON_ACCESSOR_APPNAME                  = "/appName";

    /** Constant used when accessing a resource property path from a JsonNode. */
    public static final String JSON_ACCESSOR_RESOURCE                 = "/resource";

    /** Constant used when accessing a resource/name property path from a JsonNode. */
    public static final String JSON_ACCESSOR_RESOURCE_NAME            = "/resource/name";

    /** Constant used when accessing a resourceName property from a JsonNode. */
    public static final String JSON_ACCESSOR_RESOURCENAME             = "/resourceName";

    /** Constant used when accessing a resource/representations property path from a JsonNode. */
    public static final String JSON_ACCESSOR_RESOURCE_REPRESENTATIONS = "/resource/representations";

    /** Constant used when accessing a representations property path from a JsonNode. */
    public static final String JSON_ACCESSOR_REPRESENTATIONS          = "/representations";

    /** Constant used when accessing an X-Media-Type property from a JsonNode. */
    public static final String JSON_ACCESSOR_XMEDIATYPE               = "/X-Media-Type";

    /** Constant used when accessing a versions property from a JsonNode. */
    public static final String JSON_ACCESSOR_VERSIONS                 = "/versions";

    /** Constant used when accessing a version property from a JsonNode. */
    public static final String JSON_ACCESSOR_VERSION                  = "/version";

    /** Constant used when accessing an ownerOverrides property from a JsonNode. */
    public static final String JSON_ACCESSOR_OWNEROVERRIDES           = "/ownerOverrides";

    /** Constant used when accessing a namedQueries property from a JsonNode. */
    public static final String JSON_ACCESSOR_NAMEDQUERIES             = "/namedQueries";

    /** Constant used when accessing a filters property from a JsonNode. */
    public static final String JSON_ACCESSOR_FILTERS                  = "/filters";

    /** Constant used when setting a resource property on a JsonNode. */
    public static final String JSON_SETTER_RESOURCE                   = "resource";

    /** Constant used when setting an appId property on a JsonNode. */
    public static final String JSON_SETTER_APPID                      = "appId";

    /** Constant used when setting an appName property on a JsonNode. */
    public static final String JSON_SETTER_APPNAME                    = "appName";

    /** Constant used when setting a version property on a JsonNode. */
    public static final String JSON_SETTER_VERSION                    = "version";

    /** Constant used when setting a versions property on a JsonNode. */
    public static final String JSON_SETTER_VERSIONS                   = "versions";

    /** Constant used when setting a resourceName property on a JsonNode. */
    public static final String JSON_SETTER_RESOURCENAME               = "resourceName";

    /** Constant used when setting a namedQuery property on a JsonNode. */
    public static final String JSON_SETTER_NAMEDQUERIES               = "namedQueries";

    /** Constant used when setting a filters property on a JsonNode. */
    public static final String JSON_SETTER_FILTERS                    = "filters";


    /** The tag used as a placeholder for the version value in the FULL_VERSION string. */
    public static String FULL_VERSION_TAG = "<VERSION>";

    /** Pattern used to provide the full version of a given resource. */
    public static String FULL_VERSION = "application/vnd.hedtech.integration.v" + FULL_VERSION_TAG +"+json";

    /** EthosResponseConverter used to convert from EthosResponses to String or JsonNode formats. */
    private EthosResponseConverter ethosResponseConverter;

    /**
     * Public constructor taking a sessionToken parameter.
     * <p>
     * Note that the preferred way to instantiate this class is through the {@link EthosClientBuilder EthosClientBuilder}.
     * </p>
     * @param apiKey A valid API key from Ethos Integration.  This is required to be a valid 36 character GUID string.
     *               If it is null, empty, or not in a valid GUID format, then an <code>IllegalArgumentException</code> will be thrown.
     * @param connectionTimeout The timeout <b>in seconds</b> for a connection to be established.
     * @param connectionRequestTimeout The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager.
     * @param socketTimeout The timeout <b>in seconds</b> when waiting for data between consecutive data packets.
     */
    public EthosConfigurationClient( String apiKey, Integer connectionTimeout, Integer connectionRequestTimeout, Integer socketTimeout  ) {
        super( apiKey, connectionTimeout, connectionRequestTimeout, socketTimeout );
        this.ethosResponseConverter = new EthosResponseConverter();
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Get application configuration info from Ethos Integration.  This returns the configuration
     * data for the application that this client's session token belongs to.
     * @return The config info for your app in string format.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getAppConfig() throws IOException  {
        EthosResponse ethosResponse = get( EthosIntegrationUrls.appConfig(getRegion()), new HashMap<>() );
        return ethosResponseConverter.toContentString( ethosResponse );
    }


    /**
     * Get application configuration as a JsonNode object from Ethos Integration.  This returns the configuration
     * data for the application that this client's session token belongs to.
     * <p>
     * The following is an example of the JSON data structure contained within the returned ArrayNode:
     * <pre>
     * {
     *     "id": "11111111-1111-1111-1111-111111111111",
     *     "name": "client app",
     *     "subscriptions": [
     *         {
     *             "resourceName": "academic-levels",
     *             "applicationId": "22222222-2222-2222-2222-222222222222"
     *         }
     *     ],
     *     "ownerOverrides": [
     *         {
     *             "resourceName": "academic-credentials",
     *             "applicationId": "22222222-2222-2222-2222-222222222222"
     *         },
     *         {
     *             "resourceName": "academic-disciplines",
     *             "applicationId": "22222222-2222-2222-2222-222222222222"
     *         },
     * 	],
     * 	"metadata": {
     *         "createdBy": "someone@ellucian.me",
     *         "createdOn": "2020-07-21T17:58:59.880Z",
     *         "modifiedBy": "someone@ellucian.me",
     *         "modifiedOn": "2020-10-27T16:28:34.979Z",
     *         "version": "4.9.2"
     *     }
     * }
     * </pre>
     * @return The config info for your app as a JsonNode object.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getAppConfigJson() throws IOException {
        EthosResponse ethosResponse = get( EthosIntegrationUrls.appConfig(getRegion()), new HashMap<>() );
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }


    /**
     * Get the details about all the available resources in the tenant associated with this client's session token.
     * @return The available resources details in string format.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getAllAvailableResources() throws IOException {
        EthosResponse ethosResponse = get( EthosIntegrationUrls.availableResources(getRegion()), new HashMap<>() );
        return ethosResponseConverter.toContentString( ethosResponse );
    }


    /**
     * Get the details about all the available resources in the tenant associate with this client's session token as a
     * List of JsonNodes.
     * <p>
     * The following is an example of the JSON data structure contained within the returned ArrayNode:
     * <pre>
     * [
     *   {
     *      "id": "11111111-1111-1111-11111111111111111",
     *      "name": "Banner Student API",
     *      "about": [
     *          {
     *              "applicationName": "StudentApi",
     *              "applicationVersion": "9.21",
     *              "name": "StudentApi",
     *              "version": "9.21"
     *          }
     *      ],
     *      "resources": [
     *          {
     *              "name": "academic-credentials",
     *              "representations": [
     *                  {
     *                      "filters": [ "type" ],
     *                      "X-Media-Type": "application/vnd.hedtech.integration.v6+json",
     *                      "methods": [ "get", "post", "put" ],
     *                      "version": "v6"
     *                  },
     *                  {
     *                      "filters": [ "type" ],
     *                      "X-Media-Type": "application/json",
     *                      "methods": [ "get", "post", "put" ]
     *                  }
     *              ]
     *          },
     *          {
     *              "name": "academic-disciplines",
     *              "representations": [
     *                  {
     *                      "filters": [ "type" ],
     *                      "X-Media-Type": "application/vnd.hedtech.integration.v7+json",
     *                      "methods": [ "get", "post", "put" ],
     *                      "version": "v7"
     *                  },
     *                  {
     *                      "filters": [ "type" ],
     *                      "X-Media-Type": "application/vnd.hedtech.integration.v10+json",
     *                      "methods": [ "get", "post", "put" ],
     *                      "version": "v10"
     *                  },
     *                  {
     *                      "filters": [ "type" ],
     *                      "X-Media-Type": "application/vnd.hedtech.integration.v15+json",
     *                      "methods": [ "get", "post", "put" ],
     *                      "namedQueries": [
     *                            {
     *                                "filters": [ "majorStatus" ],
     *                                "name": "majorStatus"
     *                            }
     *                      ],
     *                      "version": "v15"
     *                  },
     *                  {
     *                      "filters": [ "type" ],
     *                      "X-Media-Type": "application/json",
     *                      "methods": [ "get", "post", "put" ],
     *                      "namedQueries": [
     *                            {
     *                                "filters": [ "majorStatus" ],
     *                                "name": "majorStatus"
     *                            }
     *                      ]
     *                  }
     *              ]
     *          }
     *      ]
     *   }
     * ]
     * </pre>
     * @return The available resources details as a JsonNode, or null if the response from available resources is null or empty.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getAllAvailableResourcesAsJson() throws IOException {
        EthosResponse ethosResponse = get( EthosIntegrationUrls.availableResources(getRegion()), new HashMap<>());
        return ethosResponseConverter.toJsonNode( ethosResponse );
    }


    /**
     * Gets the details about the available resources an application points to, from the tenant associated with this
     * client's session token.  The results will be filtered based on the 'ownerOverrides' array from the session token application's
     * configuration data.
     * <p>
     * The following is an example of the JSON data structure contained within the returned ArrayNode:
     * <pre>
     * [
     *    {
     *        "name": "academic-credentials",
     * 	      "representations": [
     *            {
     *               "filters": [ "type" ],
     *               "X-Media-Type": "application/vnd.hedtech.integration.v6+json",
     *               "methods": [ "get", "post", "put" ],
     *               "version": "v6"
     *            },
     *            {
     *                "filters": [ "type" ],
     *                "X-Media-Type": "application/json",
     *                "methods": [ "get", "post", "put" ]
     *            }
     *        ]
     *    },
     *    {
     *        "name": "academic-disciplines",
     *        "representations": [
     *            {
     *                "filters": [ "type" ],
     *                "X-Media-Type": "application/vnd.hedtech.integration.v7+json",
     *                "methods": [ "get", "post", "put" ],
     *                "version": "v7"
     *            },
     *            {
     *                "filters": [ "type" ],
     *                "X-Media-Type": "application/vnd.hedtech.integration.v10+json",
     *                "methods": [ "get", "post", "put" ],
     *                "version": "v10"
     *            },
     *            {
     *                "filters": [ "type" ],
     *                "X-Media-Type": "application/vnd.hedtech.integration.v15+json",
     *                "methods": [ "get", "post", "put" ],
     *                "namedQueries": [
     *                    {
     *                        "filters": [ "majorStatus" ],
     *                        "name": "majorStatus"
     *                    }
     *                ],
     *                "version": "v15"
     *            },
     *            {
     *                "filters": [ "type" ],
     *                "X-Media-Type": "application/json",
     *                "methods": [ "get", "post", "put" ],
     *                "namedQueries": [
     *                    {
     *                        "filters": [ "majorStatus" ],
     *                        "name": "majorStatus"
     *                    }
     *                ]
     *            }
     *        ]
     *    }
     * ]
     * </pre>
     * @return An ArrayNode containing resource details, or an empty ArrayNode if the response from appConfig does not contain ownerOverrides or if
     *         the response from available resources is null or empty.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public ArrayNode getAvailableResourcesForAppAsJson() throws IOException {
        JsonNode appConfigNode = getAppConfigJson();
        JsonNode ownerOverridesNode = appConfigNode.at( JSON_ACCESSOR_OWNEROVERRIDES );
        return filterAvailableResources( getAllAvailableResourcesAsJson(), ownerOverridesNode );
    }


    /**
     * Get the details about the available resources an application points to, from the tenant associated with this
     * client's session token.  The results will be filtered based on the 'ownerOverrides' array from the session token application's
     * configuration data.
     * @return The available resources details in string format.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getAvailableResourcesForApp()throws IOException {
        return getAvailableResourcesForAppAsJson().toString();
    }


    /**
     * Returns a list of version headers for the given resourceName filtered by the ownerOverrides property from appConfig.
     * Returns a simple list of version headers because each resource is only listed once under ownerOverrides.
     * The results will be filtered based on the resources available in the 'ownerOverrides' array from the session token application's
     * configuration data.
     * If resourceName not found in ownerOverrides, returns an empty List.
     * @param resourceName The resource name for which to get a list of supported version headers.
     * @return A list of version headers according to whether the given resource is found in the ownerOverrides property of appConfig,
     *         or an empty list if not found.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getVersionHeadersForApp( String resourceName ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get version headers for ownerOverrides applications due to a null or empty resource name." );
        ArrayNode resourcesArrayNode = getAvailableResourcesForAppAsJson();
        return getVersionListForOwnerOverrides( resourcesArrayNode, JSON_ACCESSOR_XMEDIATYPE );
    }


    /**
     * Returns a list of versions for the given resourceName filtered by the ownerOverrides property from appConfig.
     * Returns a simple list of versions because each resource is only listed once under ownerOverrides.
     * The results will be filtered based on the resources available in the 'ownerOverrides' array from the session token application's
     * configuration data.
     * If resourceName not found in ownerOverrides, returns an empty List.
     * @param resourceName The resource name for which to get a list of supported versions.
     * @return A list of versions according to whether the given resource is found in the ownerOverrides property of appConfig,
     *         or an empty list if not found.  Each version in the list will be prefixed with the 'v' char, e.g. "v4.5".
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getVersionsForApp( String resourceName ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get versions for ownerOverrides applications due to a null or empty resource name." );
        ArrayNode resourcesArrayNode = getAvailableResourcesForAppAsJson();
        return getVersionListForOwnerOverrides( resourcesArrayNode, JSON_ACCESSOR_VERSION );
    }


    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Extracts a list of versions as either a list of version headers or a list of version values depending on the
     * representationType.
     * @param resourcesArrayNode The ArrayNode containing a list of resources per the ownerOverrides configuration from appConfig.
     * @param representationType The type of version representation to extract, either "/version" for the version value,
     *                           or "/X-Media-Type" for the version header value.
     * @return A list of either version values or version headers per the representationType.
     */
    protected List<String> getVersionListForOwnerOverrides( ArrayNode resourcesArrayNode, String representationType ) {
        List<String> versionList = new ArrayList<>();
        Iterator<JsonNode> resourcesIter = resourcesArrayNode.iterator();
        while( resourcesIter.hasNext() ) {
            JsonNode representationsNode = resourcesIter.next().at(JSON_ACCESSOR_REPRESENTATIONS);
            Iterator<JsonNode> representationsIter = representationsNode.iterator();
            while( representationsIter.hasNext() ) {
                JsonNode repNode = representationsIter.next();
                String verStr = repNode.at(representationType).asText();
                if( verStr == null || verStr.isBlank() ) {
                    continue;
                }
                if( versionList.contains(verStr) == false ) {
                    versionList.add( verStr );
                }
            }
        }
        return versionList;
    }


    /**
     *  Get the details of a single resource from the tenant associated with this client's session token.  The results will include
     *  details from each application in the tenant that owns the resource.
     *  <p>
     *  The returned value will be in String format of a JSON array with each object containing the following properties:
     *  <ul>
     *      <li>appId - the ID of an owning application</li>
     *      <li>appName - the name of an owning application</li>
     *      <li>resource - the details of the resource</li>
     *  </ul>
     *  <p>
     *  Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty.
     * @param resourceName The name of the resource for which to return details.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return The resource details in string format.
     */
    public String getResourceDetails( String resourceName ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get resource details due to a null or empty resourceName param." );
        ArrayNode resourceArrayNode = getResourceDetailsAsJson( resourceName );
        return resourceArrayNode.toString();
    }


    /**
     * Get the details of a single resource from the tenant associated with this client's session token.  The results will include
     * details from each application in the tenant that owns the resource.
     * <p>
     * The format of the response will be a list of JsonNodes with each object containing the following properties:
     * <ul>
     *     <li>appId - the ID of an owning application</li>
     *     <li>appName - the name of an owning application</li>
     *     <li>resource - the details of the resource</li>
     * </ul>
     * <p>
     * The following is an example of the JSON data structure contained within the returned ArrayNode:
     * <pre>
     * [
     *    {
     *        "appId": "11111111-1111-1111-1111-111111111111",
     *        "appName": "Banner Integration API",
     *        "resource": {
     *            "name": "persons",
     *            "representations": [
     *                {
     *                    "filters": [ "title", "firstName", "lastName", "role" ],
     *                    "X-Media-Type": "application/vnd.hedtech.integration.v6json",
     *                    "methods": [ "get", "post", "put" ],
     *                    "version": "v6"
     *                },
     *                {
     *                    "filters": [ "names.title", "names.firstName", "names.lastName", "roles.role" ],
     *                    "X-Media-Type": "application/vnd.hedtech.integration.v8json",
     *                    "methods": [ "get", "post", "put" ],
     *                    "namedQueries": [
     *                        {
     *                            "filters": [ "personFilter" ],
     *                            "name": "personFilter"
     *                        }
     *                    ],
     *                    "version": "v8"
     *                }
     *            ]
     *        }
     *    }
     * ]
     * </pre>
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty.
     * <br>
     * Throws an EthosResourceNotFoundException (Runtime) if the given resourceName is not found in the available-resources response.
     * @param resourceName The name of the resource for which to return details.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return An ArrayNode containing resource details, or an empty ArrayNode if resourceName is null or empty/blank.
     */
    public ArrayNode getResourceDetailsAsJson(String resourceName ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get resource details as JSON due to a null or empty resourceName param." );
        JsonNode resourcesNode = getAllAvailableResourcesAsJson();
        ArrayNode filteredArrayNode = filterAvailableResources( resourcesNode, resourceName );
        if( filteredArrayNode.isEmpty() ) {
            String errMsg = String.format("Ethos resource name \"%s\" not found in the available resources response.", resourceName );
            throw new EthosResourceNotFoundException( errMsg, resourceName );
        }
        return filteredArrayNode;
    }


    /**
     * Gets only the major version headers as a List of Strings for the given resource name.
     * Gets the entire version header string of the resource also found in the Accept Header
     * of the request containing only the major versioning notation: e.g. application/vnd.hedtech.integration.v12+json
     * <p>
     * The following table shows examples given various supported versions.
     * <table style="width:33%; border:2px solid #507daf; font-family:verdana">
     *     <caption style="text-align:left">Supported resource versions and versions returned by the SDK:</caption>
     *     <thead style="background-color:#cbd8e7; text-align:center">
     *         <tr>
     *             <th>Resource Supports Versions</th><th>SDK Returns Version</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <tr style="background-color:#eef2f7">
     *             <td><div>v12.2.1, <br> v12, <br> v12.0.0, <br> v13.1.0</div></td><td><div>application/vnd.hedtech.integration.v12+json,
     *                                                                            <br>application/vnd.hedtech.integration.v13+json</div></td>
     *         </tr>
     *         <tr>
     *             <td><div>v3, <br> v4.5.0, <br> v5.0.1</div></td><td><div>application/vnd.hedtech.integration.v3+json,
     *                                                              <br>application/vnd.hedtech.integration.v4+json,
     *                                                              <br>application/vnd.hedtech.integration.v5+json</div></td>
     *         </tr>
     *         <tr style="background-color:#eef2f7">
     *             <td><div>v2.0.1, <br> v3.2.0, <br> v3.0.0</div></td><td><div>application/vnd.hedtech.integration.v2+json,
     *                                                                 <br>application/vnd.hedtech.integration.v3+json</div></td>
     *         </tr>
     *         <tr>
     *             <td><div>v10.0.0, <br> v11.12.0, <br> v11.12.3</div></td><td><div>application/vnd.hedtech.integration.v10+json,
     *                                                                    <br>application/vnd.hedtech.integration.v11+json</div></td>
     *         </tr>
     *     </tbody>
     * </table>
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty.
     * @param resourceName The name of the Ethos resource to get version information for.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return A List of Strings where each item in the list is a major version supported by the given resource.
     */
    public List<String> getMajorVersionsOfResource(String resourceName ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get major versions due to a null or empty resourceName param." );
        List<String> versionList = getVersionsOfResourceAsStrings( resourceName );
        List<String> filteredVersionList = filterMajorVersions( versionList );
        List<String> versionHeaderList = new ArrayList<>();
        for( String filteredVersion : filteredVersionList ) {
            versionHeaderList.add( FULL_VERSION.replace(FULL_VERSION_TAG, filteredVersion) );
        }
        return versionHeaderList;
    }

    /**
     * <b>Intended to be used internally within the SDK.</b>
     * <p>
     * Filters the given versionList to remove duplicate versions and ensure each version in the returned list
     * a major whole number version only.
     * @param versionList A list of versions from a given resource.
     * @return A list of filtered versions containing no duplicates where each version in the list is a major whole version only.
     */
    protected List<String> filterMajorVersions( List<String> versionList ) {
        List<String> filteredList = new ArrayList<>();
        for( String verStr : versionList ) {
            if( verStr.isBlank() ) {
                continue;
            }
            if( verStr.startsWith("v") ) {
                verStr = verStr.substring( 1 ); // Remove the 'v' if it starts with it.
            }
            if( verStr.contains(".") ) {
                verStr = verStr.substring( 0, verStr.indexOf('.') );
            }
            if( filteredList.contains(verStr) == false ) {
                filteredList.add( verStr );
            }
        }
        return filteredList;
    }


    /**
     * Indicates if the given resource supports the given major version.  For example, if the resource supports this version:
     * <br><i>application/vnd.hedtech.integration.v12+json</i>
     * <br>this method will return true if the major version is 12.  However, if the resource only supports these versions:
     * <br><i>application/vnd.hedtech.integration.v12.0+json</i> OR <i>application/vnd.hedtech.integration.v12.0.0+json</i>
     * <br>this method will return false.
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty.
     * @param resourceName The name of the Ethos resource.
     * @param majorVersion The major version of the resource to check.
     * @return true if the given resource supports the given major version, false otherwise.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public boolean isResourceVersionSupported( String resourceName, int majorVersion ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot check if resource version is supported for major version due to a null or empty resourceName param." );
        boolean versionSupported = false;
        List<String> versionList = getVersionsOfResourceAsStrings( resourceName );
        for( String ver : versionList ) {
            if( String.valueOf(majorVersion).equals(ver.substring(1)) ) { // Substring to remove the beginning 'v' char.
                versionSupported = true;
                break;
            }
        }
        return versionSupported;
    }


    /**
     * Indicates if the given resource supports the given major.minor version.  For example, if the resource supports this version:
     * <br><i>application/vnd.hedtech.integration.v12.1+json</i>
     * <br>this method will return true if the major version is 12 and the minor version is 1.  However, if the resource only supports these versions:
     * <br><i>application/vnd.hedtech.integration.v12.0+json</i> OR <i>application/vnd.hedtech.integration.v12.0.1+json</i>
     * <br>this method will return false.
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty.
     * @param resourceName The name of the Ethos resource.
     * @param majorVersion The major version of the resource to check.
     * @param minorVersion The minor version of the resource to check.
     * @return true if the given resource supports the given major version, false otherwise.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public boolean isResourceVersionSupported( String resourceName, int majorVersion, int minorVersion ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot check if resource version is supported for major.minor version due to a null or empty resourceName param." );
        boolean versionSupported = false;
        List<String> versionList = getVersionsOfResourceAsStrings( resourceName );
        for( String ver : versionList ) {
            String requestedVersion = new StringBuilder().append(majorVersion).append(".").append(minorVersion).toString();
            if( requestedVersion.equals(ver.substring(1)) ) { // Substring to remove the beginning 'v' char.
                versionSupported = true;
                break;
            }
        }
        return versionSupported;
    }


    /**
     * Indicates if the given resource supports the given major.minor.patch version.  For example, if the resource supports this version:
     * <br><i>application/vnd.hedtech.integration.v12.1.0+json</i>
     * <br>this method will return true if the major version is 12, the minor version is 1, and the patch version is 0.
     * However, if the resource only supports these versions:
     * <br><i>application/vnd.hedtech.integration.v12+json</i> OR <i>application/vnd.hedtech.integration.v12.1+json</i>
     * <br>this method will return false.
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty.
     * @param resourceName The name of the Ethos resource.
     * @param majorVersion The major version of the resource to check.
     * @param minorVersion The minor version of the resource to check.
     * @param patchVersion The patch version of the resource to check.
     * @return true if the given resource supports the given major version, false otherwise.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public boolean isResourceVersionSupported( String resourceName, int majorVersion, int minorVersion, int patchVersion ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot check if resource version is supported for major.minor.patch version due to a null or empty resourceName param." );
        boolean versionSupported = false;
        List<String> versionList = getVersionsOfResourceAsStrings( resourceName );
        for( String ver : versionList ) {
            String requestedVersion = new StringBuilder().append(majorVersion).append(".").append(minorVersion).append(".").append(patchVersion).toString();
            if( requestedVersion.equals(ver.substring(1)) ) { // Substring to remove the beginning 'v' char.
                versionSupported = true;
                break;
            }
        }
        return versionSupported;
    }


    /**
     * Indicates if the given resource supports the given full version header.  The full version header should be in the following
     * format:  application/vnd.hedtech.integration.vSEMVER+json, where SEMVER is the semantic version of the
     * requested resource.
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName or fullVersionHeader is null or empty.
     * @param resourceName The name of the Ethos resource.
     * @param fullVersionHeader The full version header of the Ethos resource as described above.
     * @return true if the given resource supports the specified full version header, false otherwise.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public boolean isResourceVersionSupported( String resourceName, String fullVersionHeader ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot check if resource version is supported for the given full version due to a null or empty resourceName param." );
        if( fullVersionHeader == null || fullVersionHeader.isBlank() ) {
            throw new IllegalArgumentException( String.format("Error: cannot check if resource version is supported for resource \"%s\" for the given full version header due to a null or empty full version header param.", resourceName) );
        }
        boolean versionSupported = false;
        List<String> versionHeaderList = getVersionHeadersOfResourceAsStrings( resourceName );
        for( String ver : versionHeaderList ) {
            if( fullVersionHeader.equals(ver) ) {
                versionSupported = true;
                break;
            }
        }
        return versionSupported;
    }


    /**
     * Indicates if the requested Ethos resource supports a version represented by the given SemVer object.
     * SemVer objects are to be used where a version is truly a semantic version, and not when any of the major, minor,
     * or patch versions of the semantic version notation are missing or not known.  For example, this method should
     * not be used when trying to determine if version 12.2 is supported because the given SemVer will translate 12.2
     * into 12.2.0 which is different.
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty or the given semVer is null.
     * @param resourceName The name of the Ethos resource.
     * @param semVer The SemVer object containing the full semantic version to check for the resource.
     * @return true if the resource supports the semantic version represented by the given semVer, false otherwise.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public boolean isResourceVersionSupported( String resourceName, SemVer semVer ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot check if resource version is supported for the given SemVer due to a null or empty resourceName param." );
        if( semVer == null ) {
            throw new IllegalArgumentException( String.format("Error: cannot check if resource version is supported for resource \"%s\" for the given SemVer due to a null semVer param.", resourceName) );
        }
        boolean versionSupported = false;
        List<String> versionList = getVersionsOfResourceAsStrings( resourceName );
        for( String ver : versionList ) {
            SemVer sv = new SemVer.Builder(ver).build();
            if( semVer.equals(sv) ) {
                versionSupported = true;
                break;
            }
        }
        return versionSupported;
    }


    /**
     * Gets the full version header string for the given resource and major version if the resource supports the given major
     * version.  If the resource does not support the major version, an UnsupportedVersionException is thrown, which is a
     * RuntimeException.
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty.
     * @param resourceName The name of the resource to request the version string for.
     * @param majorVersion The major version of the given resource to request the full version string for.
     * @return The full version string for the given resource and major version: e.g. application/vnd.hedtech.integration.v12+json
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getVersionHeader(String resourceName, int majorVersion ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get full resource version for the given major version due to a null or empty resourceName param." );
        if( isResourceVersionSupported( resourceName, majorVersion) == false ) {
            throw new UnsupportedVersionException("The given major version is unsupported for the requested resource.", resourceName, String.valueOf(majorVersion) );
        }
        return FULL_VERSION.replace(FULL_VERSION_TAG, String.valueOf(majorVersion) );
    }


    /**
     * Gets the full version header string for the given resource, major, and minor version if the resource supports the given
     * major.minor version.  If the resource does not support the major.minor version, an UnsupportedVersionException is thrown,
     * which is a RuntimeException.
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty.
     * @param resourceName The name of the resource to request the version string for.
     * @param majorVersion The major version of the given resource to request the full version string for.
     * @param minorVersion The minor version of the given resource to request the full version string for.
     * @return The full version string for the given resource and major.minor version: e.g. application/vnd.hedtech.integration.v12.1+json
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getVersionHeader(String resourceName, int majorVersion, int minorVersion ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get full resource version for the given major.minor version due to a null or empty resourceName param." );
        String requestedVersion = new StringBuilder().append(majorVersion).append(".").append(minorVersion).toString();
        if( isResourceVersionSupported( resourceName, majorVersion, minorVersion) == false ) {
            throw new UnsupportedVersionException("The given major.minor version is unsupported for the requested resource.", resourceName, requestedVersion );
        }
        return FULL_VERSION.replace(FULL_VERSION_TAG, requestedVersion );
    }


    /**
     * Gets the full version header string for the given resource, major, minor, and patch version if the resource supports the given
     * major.minor.patch version.  If the resource does not support the major.minor.patch version, an UnsupportedVersionException is thrown,
     * which is a RuntimeException.
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty.
     * @param resourceName The name of the resource to request the version string for.
     * @param majorVersion The major version of the given resource to request the full version string for.
     * @param minorVersion The minor version of the given resource to request the full version string for.
     * @param patchVersion The patch version of the given resource to request the full version string for.
     * @return The full version string for the given resource and major.minor.patch version: e.g. application/vnd.hedtech.integration.v12.1.3+json
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getVersionHeader(String resourceName, int majorVersion, int minorVersion, int patchVersion ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get full resource version for the given major.minor.patch version due to a null or empty resourceName param." );
        String requestedVersion = new StringBuilder().append(majorVersion).append(".").append(minorVersion).append(".").append(patchVersion).toString();
        if( isResourceVersionSupported( resourceName, majorVersion, minorVersion, patchVersion) == false ) {
            throw new UnsupportedVersionException("The given major.minor.patch version is unsupported for the requested resource.", resourceName, requestedVersion );
        }
        return FULL_VERSION.replace(FULL_VERSION_TAG, requestedVersion );
    }


    /**
     * Gets the full version header string for the given resource and SemVer if the resource supports the given
     * version contained within the SemVer.  If the resource does not support the SemVer version, an UnsupportedVersionException is thrown,
     * which is a RuntimeException.
     * <p>
     * Also throws an IllegalArgumentException (Runtime) if the given resourceName is null or empty or the given semVer is null.
     * <p>
     * NOTE: This method should only be used when the complete semVer notation of a version is known.  For example, using the SemVer version
     * when only the major.minor version is provided will result in a patch version of 0, which may or may not be desired.
     * @param resourceName The name of the resource to request the version string for.
     * @param semVer The SemVer containing the requested version of the resource.
     * @return The full version string for the given resource and major.minor.patch version: e.g. application/vnd.hedtech.integration.v12.0.0+json
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getVersionHeader(String resourceName, SemVer semVer ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get full resource version for the given SemVer due to a null or empty resourceName param." );
        if( semVer == null ) {
            throw new IllegalArgumentException( "Error: Cannot get full resource version for the given SemVer due to a null semVer param." );
        }
        if( isResourceVersionSupported( resourceName, semVer) == false ) {
            String unsupportedVersion = semVer.toString();
            throw new UnsupportedVersionException("The given semVer major.minor.patch version is unsupported for the requested resource.", resourceName, unsupportedVersion );
        }
        return FULL_VERSION.replace(FULL_VERSION_TAG, semVer.toString() );
    }


    /**
     * Gets a list of versions of the given resource name from the available-resources API.
     * Each version in the list is from the /resource/representations/version property of the available-resources response
     * for the given resource across all Ethos applications for the tenant in the access token.  Each version value in the
     * returned list is prefixed with the 'v' char, e.g. "v4.5.1".  The returned list may contain duplicate version values.
     * <p>
     * Each element in the returned ArrayNode contains the following properties:
     * <ul>
     *     <li>appId - The GUID applicationId.</li>
     *     <li>appName - The name of the application in Ethos Integration.</li>
     *     <li>resourceName - The name of the Ethos resource.</li>
     *     <li>versions - An array of version values, each prefixed with the 'v' char: e.g. [ "v6", "v8", "v12.1.0" ]</li>
     * </ul>
     * <p>
     * The following is an example of the JSON data structure contained within the returned ArrayNode:
     * <pre>
     * [
     *     {
     *       "appId" : "11111111-1111-1111-1111-111111111111",
     *       "appName" : "Banner Integration API",
     *       "resourceName" : "general-ledger-transactions",
     *       "versions" : [ "v6", "v8", "v12.1.0", "v12.0.0", "v12" ]
     *     }
     * ]
     * </pre>
     * @param resourceName The resource for which to get a list of versions.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return A list of supported version values for the given resource.
     */
    public ArrayNode getVersionsOfResource( String resourceName ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get versions of resource due to a null or empty resourceName param." );
        // Should return an arrayNode of objects containing appId, appName, resourceName, and list of versions across all apps in the tenant.
        return getResourceVersionsByRepresentationType( resourceName, JSON_ACCESSOR_VERSION );
    }


    /**
     * Gets a list of full version headers of the given resource name from the available-resources API.
     * Each version in the list is from the /resource/representations/X-Media-Type property of the available-resources response
     * for the given resource across all Ethos applications for the tenant in the access token.  An example of a full version
     * header returned in the list is "application/vnd.hedtech.integration.v8+json".
     * The returned list may contain duplicate version header values.
     * <p>
     * Each element in the returned ArrayNode contains the following properties:
     * <ul>
     *     <li>appId - The GUID applicationId.</li>
     *     <li>appName - The name of the application in Ethos Integration.</li>
     *     <li>resourceName - The name of the Ethos resource.</li>
     *     <li>versions - An array of version header values: e.g. [ "application/vnd.hedtech.integration.v8+json", "application/vnd.hedtech.integration.v12.1.0+json" ]</li>
     * </ul>
     * <p>
     * The following is an example of the JSON data structure contained within the returned ArrayNode:
     * <pre>
     * [
     *     {
     *         "appId" : "11111111-1111-1111-1111-111111111111",
     *         "appName" : "Banner Integration API",
     *         "resourceName" : "general-ledger-transactions",
     *         "versions" : [
     *             "application/vnd.hedtech.integration.v6+json",
     *             "application/vnd.hedtech.integration.v8+json",
     *             "application/vnd.hedtech.integration.v12.1.0+json",
     *             "application/vnd.hedtech.integration.v12.0.0+json",
     *             "application/vnd.hedtech.integration.v12+json",
     *             "application/json"
     *         ]
     *     }
     * ]
     * </pre>
     * @param resourceName The resource for which to get a list of versions.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return A list of supported version headers for the given resource.
     */
    public ArrayNode getVersionHeadersOfResource( String resourceName ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get version headers of resource due to a null or empty resourceName param." );
        // Should return an arrayNode of objects containing appId, appName, resourceName, and list of version headers across all apps in the tenant.
        return getResourceVersionsByRepresentationType( resourceName, JSON_ACCESSOR_XMEDIATYPE );
    }


    /**
     * Gets a list of version string values from the /versions json property of the ArrayNode returned from {@link #getVersionsOfResource(String) getVersionsOfResource()}.
     * Version values are gathered from across Ethos Integration applications for the given tenant (access token) and resource name,
     * and should not contain duplicate values.
     * @param resourceName The name of the Ethos resource to get a list of versions for.
     * @return A list of version strings.  Each element in the list is prefixed with the 'v' char, e.g. "v4.5".
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getVersionsOfResourceAsStrings( String resourceName ) throws IOException {
        ArrayNode arrayNode = getVersionsOfResource( resourceName );
        List<String> versionList = getVersionList( arrayNode, JSON_ACCESSOR_VERSIONS );
        return removeDuplicatesFromStringList( versionList );
    }


    /**
     * Gets a list of version header string values from the /versions json property of the ArrayNode returned from {@link #getVersionHeadersOfResource(String) getVersionHeadersOfResource()}.
     * @param resourceName The name of the Ethos resource to get a list of version headers for.
     * @return A list of version header strings.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getVersionHeadersOfResourceAsStrings( String resourceName ) throws IOException {
        ArrayNode arrayNode = getVersionHeadersOfResource( resourceName );
        List<String> versionList = getVersionList( arrayNode, JSON_ACCESSOR_VERSIONS );
        return removeDuplicatesFromStringList( versionList );
    }


    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Reads the given application/resource arrayNode and returns a simple list of version strings from that node.
     * The given arrayNode is expected to contain an array of the following properties:
     * <ul>
     *     <li>appId - the application ID of the app in Ethos Integration</li>
     *     <li>appName - the name of the application in Ethos Integration</li>
     *     <li>resourceName - the name of the Ethos resource</li>
     *     <li>versions - a list of versions based on the representationType</li>
     * </ul>
     * @param arrayNode The arrayNode containing application/resource info with a list of versions per resource.
     * @param representationType The JSON property to use, expected to be "/versions" when the arrayNode contains
     *                           both version values (v2, v3.0.4, etc.) and version header strings.
     * @return A simple list of versions from the given arrayNode, could contain duplicate version values.
     */
    protected List<String> getVersionList( ArrayNode arrayNode, String representationType ) {
        List<String> versionList = new ArrayList<>();
        Iterator<JsonNode> arrayNodeIter = arrayNode.iterator();
        while( arrayNodeIter.hasNext() ) {
            JsonNode appResourceNode = arrayNodeIter.next();
            JsonNode versionsNode = appResourceNode.at(representationType);
            Iterator<JsonNode> versionsNodeIter = versionsNode.iterator();
            while (versionsNodeIter.hasNext()) {
                JsonNode versionNode = versionsNodeIter.next();
                String versionStr = versionNode.asText();
                versionList.add(versionStr);
            }
        }
        return versionList;
    }


    /**
     * Gets the latest version of the given resource.  Could return either a Semantic version (e.g. 12.0.0), or a
     * non-semantic version (e.g. 12), as a string value.
     * @param resourceName The name of the resource to get the latest version of.
     * @return The latest version of the given resource, either a semantic version, or a non-semantic whole number value, as a string.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getLatestVersion( String resourceName ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get latest version of resource due to a null or empty resourceName param." );
        List<String> versionList = getVersionsOfResourceAsStrings( resourceName );
        // The version list could contain both non-semantic and semantic versions.
        // Split the versionList into 2 lists:  1 for non-semantic versions and 1 for semantic versions.
        Map<String,List> versionSplitMap = splitVersionList( versionList );
        // Get the first value from the semantic and non-semantic lists in the map, and compare them.
        List<SemVer> semanticList = versionSplitMap.get( SEMANTIC_LIST );
        List<Integer> nonSemanticList = versionSplitMap.get( NONSEMANTIC_LIST );
        if( semanticList.isEmpty() && nonSemanticList.isEmpty() ) {
            // Both lists are empty, so no version was found for this resource, and the null/empty version value is
            // removed from the versionList for application/json versions.  So return application/json as the version.
            return "application/json";
        }
        if( semanticList.isEmpty() ) {
            // The semantic list is empty, so get the first element from the non-semantic list, convert it to a String and return it.
            Integer latestVersion = nonSemanticList.get( 0 );
            return String.valueOf( latestVersion );
        }
        if( nonSemanticList.isEmpty() ) {
            // The non-semantic list is empty, so get the first element from the semantic list, convert it to a String and return it.
            SemVer latestSemVer = semanticList.get( 0 );
            return latestSemVer.toString();
        }
        Integer nonSemanticVer = nonSemanticList.get( 0 );
        SemVer semanticVer = semanticList.get( 0 );
        if( semanticVer.getMajor() >= nonSemanticVer ) {
            // If the semantic major version value is >= the non-semantic version, then return the semantic version.
            // This includes being equal because there is a semantic version.
            return semanticVer.toString();
        }
        // The non-semantic version is > semantic, so return it.
        return String.valueOf( nonSemanticVer );
    }


    /**
     * Gets the latest version header of the given resource.
     * @param resourceName The name of the resource to get the latest version header for.
     * @return The latest full version header value of the given resource.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public String getLatestVersionHeader( String resourceName ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get latest version header of resource due to a null or empty resourceName param." );
        String latestVersion = getLatestVersion( resourceName );
        return FULL_VERSION.replace(FULL_VERSION_TAG, latestVersion );
    }


    /**
     * Gets a JsonNode containing the supported filters and named queries for the given resource.
     * Uses the latest version of the resource to retrieve filters and/or named queries.
     * <br>
     * The following is an example of the JSON data structure within the returned JsonNode:
     * <pre>
     * {
     *   "resourceName" : "persons",
     *   "version" : "application/vnd.hedtech.integration.v12.3.0+json",
     *   "namedQueries" : [
     *       {
     *           "filters" : [ "personFilter" ],
     *           "name" : "personFilter"
     *       }
     *   ],
     *   "filters" : [
     *       "names.title",
     *       "names.firstName",
     *       "names.middleName",
     *       "names.lastNamePrefix",
     *       "names.lastName",
     *       "names.pedigree",
     *       "roles.role",
     *       "credentials.type",
     *       "credentials.value",
     *       "alternativeCredentials.type.id",
     *       "alternativeCredentials.value",
     *       "emails.address"
     *   ]
     * }
     * </pre>
     * @param resourceName The name of the resource to get available filters and named queries for.
     * @return A JsonNode containing the name of the resource and version header, with filters if they exist for the
     *         given resource and version, and/or named queries if they exist for the given resource and version.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getFiltersAndNamedQueries( String resourceName ) throws IOException {
        return getFiltersAndNamedQueries( resourceName, null );
    }


    /**
     * Gets a JsonNode containing the supported filters and named queries for the given resource and version header.
     * If the given versionHeader is null or empty, the latest version will be used.
     * <br>
     * The following is an example of the JSON data structure within the returned JsonNode:
     * <pre>
     * {
     *   "resourceName" : "persons",
     *   "version" : "application/vnd.hedtech.integration.v12.3.0+json",
     *   "namedQueries" : [
     *       {
     *           "filters" : [ "personFilter" ],
     *           "name" : "personFilter"
     *       }
     *   ],
     *   "filters" : [
     *       "names.title",
     *       "names.firstName",
     *       "names.middleName",
     *       "names.lastNamePrefix",
     *       "names.lastName",
     *       "names.pedigree",
     *       "roles.role",
     *       "credentials.type",
     *       "credentials.value",
     *       "alternativeCredentials.type.id",
     *       "alternativeCredentials.value",
     *       "emails.address"
     *   ]
     * }
     * </pre>
     * Throws an EthosResourceNotFoundException (Runtime) if the given resourceName is not found in the available-resource response.
     * @param resourceName The name of the resource to get available filters and named queries for.
     * @param versionHeader The version header value used to retrieve available filters and/or named queries for the resource.
     *                      If null or empty, the latest version header value will be used.
     * @return A JsonNode containing the name of the resource and version header, with filters if they exist for the
     *         given resource and version, and/or named queries if they exist for the given resource and version.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public JsonNode getFiltersAndNamedQueries( String resourceName, String versionHeader ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get filters and named queries for resource due to a null or empty resourceName param." );
        ArrayNode appResourceArrayNode = getResourceDetailsAsJson( resourceName );
        if( versionHeader == null || versionHeader.isBlank() ) {
            versionHeader = getLatestVersionHeader( resourceName );
        }
        ObjectNode filteredNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
        Iterator<JsonNode> appResourceIter = appResourceArrayNode.iterator();
        while( appResourceIter.hasNext() ) {
            JsonNode appResourceNode = appResourceIter.next();
            JsonNode resourceNode = appResourceNode.at( JSON_ACCESSOR_RESOURCE );
            JsonNode representationsNode = resourceNode.at( JSON_ACCESSOR_REPRESENTATIONS );
            Iterator<JsonNode> repIter = representationsNode.iterator();
            boolean isFound = false;
            while( repIter.hasNext() ) {
                JsonNode repNode = repIter.next();
                String repHeader = repNode.at(JSON_ACCESSOR_XMEDIATYPE).asText();
                if( versionHeader.equals(repHeader) ) {
                    // Found the version, now get the filters and named queries in this repNode.
                    filteredNode.put( JSON_SETTER_RESOURCENAME, resourceName );
                    filteredNode.put( JSON_SETTER_VERSION, versionHeader );
                    JsonNode namedQueryNode = repNode.at( JSON_ACCESSOR_NAMEDQUERIES );
                    if( namedQueryNode.isMissingNode() == false ) {
                        // The named query node is not missing, so add it.
                        filteredNode.set( JSON_SETTER_NAMEDQUERIES, namedQueryNode );
                    }
                    JsonNode filtersNode = repNode.at( JSON_ACCESSOR_FILTERS );
                    if( filtersNode.isMissingNode() == false ) {
                        // The filters node is not missing, so add it.
                        filteredNode.set( JSON_SETTER_FILTERS, filtersNode );
                    }
                    isFound = true;
                    break;
                }
                if( isFound ) {
                    break;
                }
            }
        }
        return filteredNode;
    }


    /**
     * Gets a list of filters for the given resource name.  The latest version of the given resource is used to obtain
     * the list of filters.
     * @param resourceName The name of the Ethos resource for which to get a list of filters.
     * @return A list of filter values (strings) which the given resource supports, or an empty list if none found.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getFilters( String resourceName ) throws IOException {
        return getFilters( resourceName, null );
    }


    /**
     * Gets a list of filters for the given resource name and version header value.
     * @param resourceName The name of the Ethos resource for which to get a list of filters.
     * @param versionHeader The full version header value for which to get a list of filters for the given resource.
     * @return A list of filter values (strings) which the given resource supports, or an empty list if none found.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public List<String> getFilters( String resourceName, String versionHeader ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get filters due to a null or empty resource name." );
        if( versionHeader == null || versionHeader.isBlank() ) {
            versionHeader = getLatestVersionHeader( resourceName );
        }
        JsonNode resourceFiltersNode = getFiltersAndNamedQueries( resourceName, versionHeader );
        List<String> filterList = new ArrayList<>();
        JsonNode filtersNode = resourceFiltersNode.at( JSON_ACCESSOR_FILTERS );
        Iterator<JsonNode> filtersIter = filtersNode.iterator();
        while( filtersIter.hasNext() ) {
            JsonNode fNode = filtersIter.next();
            filterList.add( fNode.asText() );
        }
        return filterList;
    }


    /**
     * Gets a map of named queries for the given resource name.  The latest version of the given resource will be used
     * when retrieving the named queries.
     * <p>
     * The map keys are the names of the named queries, and the map value for each key is a list of string filter values such that:
     * <pre>
     *     Map&lt;String,List&lt;String&gt;&gt; namedQueriesMap = getNamedQueries("someResource");
     *     List&lt;String&gt; filterValuesList = namedQueriesMap.get("someNamedQuery");
     *     for( String filter : filterValuesList ) {
     *         System.out.println( String.format("FILTER: %s", filter) );
     *     }
     * </pre>
     * @param resourceName The name of the Ethos resource for which to get a map of named queries.
     * @return A map of named query values (strings) which the given resource supports, or an empty map if none found.
     *         Each key in the map is the name of a named query, with a corresponding value which is a string list of filter values.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public Map<String,List<String>> getNamedQueries( String resourceName ) throws IOException {
        return getNamedQueries( resourceName, null );
    }


    /**
     * Gets a map of named queries for the given resource name and version header value.  If the given version header is
     * null or empty, the latest version of the given resource will be used.
     * <p>
     * The map keys are the names of the named queries, and the map value for each key is a list of string filter values such that:
     * <pre>
     *     Map&lt;String,List&lt;String&gt;&gt; namedQueriesMap = getNamedQueries("someResource", "application/vnd.hedtech.integration.v8+json");
     *     List&lt;String&gt; filterValuesList = namedQueriesMap.get("someNamedQuery");
     *     for( String filter : filterValuesList ) {
     *         System.out.println( String.format("FILTER: %s", filter) );
     *     }
     * </pre>
     * @param resourceName The name of the Ethos resource for which to get a map of named queries.
     * @param versionHeader The full version header value for which to get a map of named queries for the given resource.
     * @return A map of named query values (strings) which the given resource supports, or an empty map if none found.
     *         Each key in the map is the name of a named query, with a corresponding value which is a string list of filter values.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     */
    public Map<String,List<String>> getNamedQueries( String resourceName, String versionHeader ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get named queries due to a null or empty resource name." );
        if( versionHeader == null || versionHeader.isBlank() ) {
            versionHeader = getLatestVersionHeader( resourceName );
        }
        JsonNode resourceFiltersNode = getFiltersAndNamedQueries( resourceName, versionHeader );
        Map<String,List<String>> namedQueriesMap = new HashMap<>();
        JsonNode namedQueriesNode = resourceFiltersNode.at( JSON_ACCESSOR_NAMEDQUERIES );
        Iterator<JsonNode> namedQueriesIter = namedQueriesNode.iterator();
        while( namedQueriesIter.hasNext() ) {
            List<String> filtersList = new ArrayList<>();
            JsonNode nqNode = namedQueriesIter.next();
            Iterator<JsonNode> filtersIter = nqNode.at(JSON_ACCESSOR_FILTERS).iterator();
            while( filtersIter.hasNext() ) {
                JsonNode fNode = filtersIter.next();
                filtersList.add( fNode.asText() );
            }
            JsonNode nameNode = nqNode.at( JSON_ACCESSOR_NAME );
            namedQueriesMap.put( nameNode.asText(), filtersList );
        }
        return namedQueriesMap;
    }


    /**
     * <b>Intended to be used internally within the SDK.</b>
     * <p>
     * Filter the given list of available resources by the list of desired resources.  The expected formats are:
     * <ul>
     *     <li>availableResources - JsonNode response from calling the /admin/available-resources endpoint in Ethos Integration</li>
     *     <li>desiredResources - JsonNode array containing objects with 'applicationId' and 'resourceName' properties.  This is the format of
     *         the 'ownerOverrides' array from calling the /appConfig endpoint in Ethos Integration.</li>
     * </ul>
     * @param availableResourcesNode A JsonNode containing the entire list of available resources from Ethos Integration.
     * @param desiredResourceNode A JsonNode containing the target list of specific app resources to be returned.
     * @return An ArrayNode containing a filtered list of available resources, or an empty ArrayNode if availableResourcesNode or resourceName is null.
     */
    protected ArrayNode filterAvailableResources( JsonNode availableResourcesNode, JsonNode desiredResourceNode ) {
        ArrayNode arrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        if( availableResourcesNode == null || desiredResourceNode == null ) {
            return arrayNode;
        }
        Iterator<JsonNode> desiredResourcesIter = desiredResourceNode.iterator();
        while( desiredResourcesIter.hasNext() ) {
            JsonNode desiredNode = desiredResourcesIter.next();
            String desiredAppId = desiredNode.at(JSON_ACCESSOR_APPICATIONID).asText();
            String desiredResourceName = desiredNode.at(JSON_ACCESSOR_RESOURCENAME).asText();
            Iterator<JsonNode> availableResourcesIter = availableResourcesNode.iterator();
            while( availableResourcesIter.hasNext() ) {
                JsonNode appNode = availableResourcesIter.next();
                String appId = appNode.at(JSON_ACCESSOR_ID).asText();
                if( appId.equals(desiredAppId) ) {
                    Iterator<JsonNode> resourcesIter = appNode.at(JSON_ACCESSOR_RESOURCES).iterator();
                    while( resourcesIter.hasNext() ) {
                        JsonNode resourceNode = resourcesIter.next();
                        String resourceName = resourceNode.at(JSON_ACCESSOR_NAME).asText();
                        if( desiredResourceName.equals(resourceName) ) {
                            arrayNode.add( resourceNode );
                        }
                    }
                    break; // Break this middle loop here because we found the appId we wanted.
                }
            }
        }
        return arrayNode;
    }


    /**
     * <b>Intended to be used internally within the SDK.</b>
     * <p>
     * Filter the given list of available resources by the name of the resource.  The expected format of the available resources list is
     * the JSON response from calling the /admin/available-resources endpoint in Ethos Integration.  This returns an array containing
     * the details of the given resource from each owning application in the available resources list.
     * @param availableResourcesNode A JsonNode containing the entire list of available resources from Ethos Integration.
     * @param resourceName The name of the resource for which to return details.
     * @return An ArrayNode containing a filtered list of available resources, or an empty ArrayNode if availableResourcesNode or resourceName is null.
     */
    protected ArrayNode filterAvailableResources(JsonNode availableResourcesNode, String resourceName ) {
        ArrayNode arrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        if( availableResourcesNode == null || resourceName == null ) {
            return arrayNode;
        }
        Iterator<JsonNode> availableResourcesIter = availableResourcesNode.iterator();
        while( availableResourcesIter.hasNext() ) {
            JsonNode appNode = availableResourcesIter.next();
            JsonNode resourcesNode = appNode.at(JSON_ACCESSOR_RESOURCES);
            Iterator<JsonNode> resourcesIter = resourcesNode.iterator();
            while (resourcesIter.hasNext()) {
                JsonNode rNode = resourcesIter.next();
                String rName = rNode.at(JSON_ACCESSOR_NAME).asText();
                if (resourceName.equals(rName)) {
                    String appId = appNode.at(JSON_ACCESSOR_ID).asText();
                    String appName = appNode.at(JSON_ACCESSOR_NAME).asText();
                    ObjectNode filteredNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
                    filteredNode.put(JSON_SETTER_APPID, appId);
                    filteredNode.put(JSON_SETTER_APPNAME, appName);
                    filteredNode.set(JSON_SETTER_RESOURCE, rNode);
                    arrayNode.add( filteredNode );
                    break;
                }
            }
        }
        return arrayNode;
    }


    /**
     * <b>Intended to be used internally within the SDK.</b>
     * <p>
     * Gets an ArrayNode containing a list of objects with each element in the list having this structure:
     * <ul>
     *     <li>appId - the application ID of the app in Ethos Integration</li>
     *     <li>appName - the name of the application in Ethos Integration</li>
     *     <li>resourceName - the name of the Ethos resource</li>
     *     <li>versions - a list of versions based on the representationType</li>
     * </ul>
     * <p>
     * Gets a list of versions of the given resource name from the available-resources API.
     * Uses the representationType param to return either a list of version headers, or a list of version values.
     * Version values are prefixed with the char 'v', e.g. v4.5.0.
     * To return a list of version headers, the representationType must be '/X-Media-Type'.  To return a list of version
     * values, the representationType must be '/version'.
     * A runtime IllegalArgumentException will be thrown if the resourceName param is null or empty, or if the representationType
     * param is not one of those two supported values.
     * Each version in the list is from the /resource/representations/X-Media-Type or the /resource/representations/version
     * property of the available-resources response for the given resource across all Ethos applications for the tenant in the access token.
     * The returned list may contain duplicate version values.
     * @param resourceName The resource for which to get a list of versions.
     * @param representationType The jsonLabel in the available-resources response used to access the version or version header value.
     *                           Expected to only be '/version' or '/X-Media-Type', otherwise an IllegalArgumentException will be thrown.
     * @throws IOException Propagates this exception if it occurs when making the call in the {@link EthosClient EthosClient}.
     * @return A list of supported version values for the given resource according to the given representationType.  If the
     *         representationType is '/X-Media-Type' a list of full version headers will be returned.  If the representationType
     *         is '/version' a list of version values where each version value is prefixed with the 'v' char will be returned.
     */
    protected ArrayNode getResourceVersionsByRepresentationType( String resourceName, String representationType ) throws IOException {
        validateResourceNameNotNull( resourceName, "Error: Cannot get resource versions by representation type due to a null or empty resourceName param." );
        if( representationType == null ) {
            throw new IllegalArgumentException("Error: Cannot get resource versions by representation type due to a null representation type." );
        }
        if( representationType.equals(JSON_ACCESSOR_VERSION) == false &&
            representationType.equals(JSON_ACCESSOR_XMEDIATYPE) == false ) {
            throw new IllegalArgumentException( "Error: Cannot get resource versions by representation type due to an invalid representation type value.  " +
                                                "This value must be either \"/version\" or \"/X-Media-Type\"." );
        }
        ArrayNode resultArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        ArrayNode arrayNode = getResourceDetailsAsJson( resourceName );
        Iterator<JsonNode> jsonNodeIterator = arrayNode.iterator();
        while( jsonNodeIterator.hasNext() ) {
            JsonNode appResourceNode = jsonNodeIterator.next();
            String appId = appResourceNode.at(JSON_ACCESSOR_APPID).asText();
            String appName = appResourceNode.at(JSON_ACCESSOR_APPNAME).asText();
            String rscName = appResourceNode.at(JSON_ACCESSOR_RESOURCE_NAME).asText();
            ObjectNode filteredNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
            ArrayNode versionArrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
            filteredNode.put(JSON_SETTER_APPID, appId);
            filteredNode.put(JSON_SETTER_APPNAME, appName);
            filteredNode.put(JSON_SETTER_RESOURCENAME, rscName);
            JsonNode representationsNode = appResourceNode.at(JSON_ACCESSOR_RESOURCE_REPRESENTATIONS);
            Iterator<JsonNode> representationsIter = representationsNode.iterator();
            while( representationsIter.hasNext() ) {
                JsonNode repElementNode = representationsIter.next();
                JsonNode repNode = repElementNode.at( representationType );
                // Only add the representation/version if it has a value.  Default representations (application/json) will not.
                if( repNode.asText() != null && repNode.asText().isBlank() == false ) {
                    versionArrayNode.add( repNode );
                }
            }
            filteredNode.set( JSON_SETTER_VERSIONS, versionArrayNode );
            resultArrayNode.add( filteredNode );
        }
        return resultArrayNode;
    }


    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * Validates the given resourceName to ensure it is not null or empty (blank).
     * Throws an IllegalArgumentException with the given errorMessage if the resourceName is null or empty.
     * @param resourceName The resourceName to validate against being null or empty.
     * @param errorMessage The error message to use when throwing an IllegalArgumentException.
     */
    private void validateResourceNameNotNull(String resourceName, String errorMessage ) {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException( errorMessage );
        }
    }


    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * Removes duplicate values in the given list of strings.
     * @param sourceList The list of strings to filter duplicate values from.
     * @return A list of strings with the same content as the given sourceList, minus any duplicate values.
     */
    private List<String> removeDuplicatesFromStringList( List<String> sourceList ) {
        // A HashSet prevents duplicate values in the list.
        return new ArrayList<>( new HashSet<String>(sourceList) );
    }


    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * The given versionList could contain both semantic and non-semantic version values.  Splits the versionList
     * into 2 lists:  1 with SemVer values, and 1 with non-semantic Integer version values.
     * <p>
     * Any null or blank version values in the versionList will be filtered out and not processed.
     * @param versionList A list of version string values, e.g. [ "v3", "v4.1", "v7.0.1" ].
     * @return A Map with 2 entries:  1 string key of "semanticList" with a list of SemVer objects,
     *         and 1 string key of "nonSemanticList" with a list of Integer non-semantic versions.  Both lists within the
     *         returned map are sorted in descending order such that the highest version value is in the 0th index of the list.
     */
    private Map<String,List> splitVersionList( List<String> versionList ) {
        Map<String,List> resultMap = new HashMap<String,List>();
        List<SemVer> semanticList = new ArrayList<>();
        List<Integer> nonSemanticList = new ArrayList<>();
        for( String ver : versionList ) {
            if( ver == null || ver.isBlank() ) {
                continue;
            }
            if( ver.startsWith("v") ) { // Strip off the beginning 'v' char if it is there.
                ver = ver.substring( 1 );
            }
            if( ver.contains(".") ) { // If the ver value contains a dot, it's semantic.
                semanticList.add( new SemVer.Builder(ver).build() );
            }
            else {
                nonSemanticList.add( Integer.valueOf(ver) );
            }
        }
        // Sort both lists in descending order.
        Collections.sort( semanticList );
        Collections.reverse( semanticList );
        Collections.sort( nonSemanticList );
        Collections.reverse( nonSemanticList );
        resultMap.put( SEMANTIC_LIST, semanticList );
        resultMap.put( NONSEMANTIC_LIST, nonSemanticList );
        return resultMap;
    }

}