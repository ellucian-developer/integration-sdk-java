/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.service;


import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.EthosResponseConverter;
import com.ellucian.ethos.integration.client.config.EthosConfigurationClient;
import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.client.messages.EthosMessagesClient;
import com.ellucian.ethos.integration.client.proxy.EthosProxyClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.Header;

import java.io.IOException;
import java.util.*;

/**
 * Service class used for retrieving ChangeNotifications.  Uses the {@link EthosMessagesClient EthosMessagesClient} and
 * {@link EthosProxyClient EthosProxyClient} to do so.  If a specific version of a resource is requested for change notifications,
 * this service will retrieve the desired version of the resource and return the content of that resource (and version) in the
 * corresponding change notification.  For example, if persons v12 is requested but the change notification retrieved is for
 * persons v8, this service will retrieve persons v12 and replace the content in the persons v8 notification with the content
 * of persons v12.
 * <p>
 * This class contains an inner {@link EthosChangeNotificationService.Builder Builder} class following the builder pattern.  This Builder class should be used to
 * build an instance of this class.
 * @since 0.2.0
 * @author David Kumar
 */
public class EthosChangeNotificationService extends EthosService {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /**
     * <b>Used internally by the SDK.</b>
     * Constant value used to determine if a ChangeNotification is for a deleted operation.
     */
    private final String OPERATION_DELETED = "deleted";

    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * Default constant value used to replace the contentType in the ChangeNotification if there is no x-content-restricted header when overriding a resource version.
     */
    private final String RESOURCE_CONTENT_TYPE = "resource-representation";

    /**
     * A map containing the requested resource(s) and version for each resource.
     */
    private Map<String,String> resourceVersionOverrideMap;

    /**
     * Used to convert EthosResponses from resource version overrides to JsonNode containing the response content body.
     */
    private EthosResponseConverter ethosResponseConverter;

    /**
     * The EthosMessagesClient used for retrieving change notification messages from Ethos.
     */
    protected EthosMessagesClient ethosMessagesClient;

    /**
     * The EthosProxyClient used for making Ethos API calls through Ethos.
     */
    protected EthosProxyClient ethosProxyClient;


    /**
     * Instantiates this service class with the given {@link EthosClientBuilder EthosClientBuilder}.
     * This constructor is only called from the inner Builder class.
     * @param ethosClientBuilder The EthosClientBuilder used to build the required clients that this service uses.
     */
    private EthosChangeNotificationService( EthosClientBuilder ethosClientBuilder ) {
        super( ethosClientBuilder );
        this.ethosMessagesClient = ethosClientBuilder.buildEthosMessagesClient();
        this.ethosProxyClient = ethosClientBuilder.buildEthosProxyClient();
        this.resourceVersionOverrideMap = new HashMap<>();
        this.ethosResponseConverter = new EthosResponseConverter();
    }

    /**
     * Builder class following the builder pattern used to build an EthosChangeNotificationService.
     */
    public static class Builder {

        /**
         * The EthosClientBuilder used by the subclasses to build the Ethos clients used by this service.  All clients must use
         * the same API key and timeout values configured within the ethosClientBuilder.
         */
        private EthosClientBuilder ethosClientBuilder;

        /**
         * Maps the resource name to a resource version to override versions of the same resource found in change notifications.
         */
        private Map<String,String> resourceVersionOverrideMap;

        /**
         * <b>Used internally by the SDK.</b>
         * <p>
         * No-arg constructor for this builder.
         */
        private Builder() {
            this.resourceVersionOverrideMap = new HashMap<>();
        }

        /**
         * Instantiates this builder with the given apiKey.
         * @param apiKey The API key used by the ethosClientBuilder of this service.
         */
        public Builder( String apiKey ) {
            this();
            this.ethosClientBuilder = new EthosClientBuilder( apiKey );
        }

        /**
         * Instantiates this builder with the given {@link EthosClientBuilder EthosClientBuilder}.
         * @param ethosClientBuilder The EthosClientBuilder used to build the various Ethos clients used by this service.
         * @throws IllegalArgumentException Thrown if the given ethosClientBuilder is null.
         */
        public Builder( EthosClientBuilder ethosClientBuilder ) {
            this();
            if( ethosClientBuilder == null ) {
                throw new IllegalArgumentException("ERROR: Cannot build an EthosChangeNotificationService because the provided ethosClientBuilder was null." );
            }
            this.ethosClientBuilder = ethosClientBuilder;
        }

        /**
         * Sets the connection timeout value for the ethosClientBuilder.
         * @param connectionTimeout The timeout <b>in seconds</b> for a connection to be established, as used by the EthosClientBuilder.
         * @return This Builder, for fluent API usage.
         */
        public Builder withConnectionTimeout( Integer connectionTimeout ) {
            this.ethosClientBuilder.withConnectionTimeout( connectionTimeout );
            return this;
        }

        /**
         * Sets the connection request timeout value for the ethosClientBuilder.
         * @param connectionRequestTimeout The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager,
         *                                 as used by the EthosClientBuilder.
         * @return This Builder, for fluent API usage.
         */
        public Builder withConnectionRequestTimeout( Integer connectionRequestTimeout ) {
            this.ethosClientBuilder.withConnectionRequestTimeout( connectionRequestTimeout );
            return this;
        }

        /**
         * Sets the socket timeout value for the ethosClientBuilder.
         * @param socketTimeout The timeout <b>in seconds</b> when waiting for data during a period of inactivity between consecutive data packets,
         *                      as used by the EthosClientBuilder.
         * @return This Builder, for fluent API usage.
         */
        public Builder withSocketTimeout( Integer socketTimeout ) {
            this.ethosClientBuilder.withSocketTimeout( socketTimeout );
            return this;
        }

        /**
         * Adds the resource name and version to the resourceVersionOverrideMap.  This enables this service to override
         * the content of change notifications that match the resource names and versions listed in this map with the content
         * of the given resource for the version specified in the map.  For example, if a change notification is for persons v8
         * and persons v12 is added to the resourceVersionOverrideMap, then for the persons change notifications that are NOT
         * v12, this service will retrieve persons v12 (using the GUID from the change notification) and replace the content
         * of the persons change notification with the persons v12 content.  This overrides the version in the change notification
         * with the desired version listed in the resourceVersionOverrideMap.
         * @param resourceName The name of the resource.
         * @param version The desired version of the resource to override change notification of the same resource with.
         * @return This Builder, for fluent API usage.
         */
        public Builder withResourceVersionOverride( String resourceName, String version ) {
            if( resourceName == null || resourceName.isBlank() ) {
                throw new IllegalArgumentException("ERROR: Cannot build an EthosChangeNotificationService due to a null or blank resource name for the resource version override." );
            }
            if( version == null || version.isBlank() ) {
                throw new IllegalArgumentException("ERROR: Cannot build an EthosChangeNotificationService due to a null or blank version for the resource version override." );
            }
            this.resourceVersionOverrideMap.put( resourceName, version );
            return this;
        }

        /**
         * Same as {@link com.ellucian.ethos.integration.service.EthosChangeNotificationService.Builder#withResourceVersionOverride(String, String)}
         * except the version can be abbreviated as just the version value, e.g: 16, or 16.0.0.  Also accepts the version value
         * prefixed with the char 'v', e.g: v16, or v16.0.0.
         * @param resourceName The name of the resource.
         * @param version The desired abbreviated version of the resource to override change notification of the same resource with.  Can be (but not required) prefixed
         *                with the 'v' char to denote version, e.g: v12, or v12.1.0.
         * @return This Builder, for fluent API usage.
         */
        public Builder withResourceAbbreviatedVersionOverride( String resourceName, String version ) {
            if( version == null || version.isBlank() ) {
                throw new IllegalArgumentException("ERROR: Cannot build an EthosChangeNotificationService due to a null or blank abbreviated version for the resource version override." );
            }
            if( version.startsWith("v") || version.startsWith("V") ) {
                version = version.substring( 1 );
            }
            String fullVersion = EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, version );
            return withResourceVersionOverride( resourceName, fullVersion );
        }

        /**
         * Builds an instance of the EthosChangeNotificationService with the given ethosClientBuilder and any resource version overrides.
         * @return An instance of the EthosChangeNotificationService.
         */
        public EthosChangeNotificationService build() {
            EthosChangeNotificationService ethosChangeNotificationService = new EthosChangeNotificationService( this.ethosClientBuilder );
            Iterator<String> keyIter = resourceVersionOverrideMap.keySet().iterator();
            while( keyIter.hasNext() ) {
                String key = keyIter.next();
                ethosChangeNotificationService.addResourceVersionOverride( key, resourceVersionOverrideMap.get(key) );
            }
            return ethosChangeNotificationService;
        }

    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Enables client application code to add resource version overrides after this service has been built.
     * @param resourceName The name of the resource.
     * @param version The desired version of the resource to override change notification of the same resource with.
     */
    public void addResourceVersionOverride( String resourceName, String version ) {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException("ERROR: Cannot add a resource version override for the EthosChangeNotificationService with a null or blank resource name value." );
        }
        if( version == null || version.isBlank() ) {
            throw new IllegalArgumentException("ERROR: Cannot add a resource version override for the EthosChangeNotificationService with a null or blank version header value." );
        }
        resourceVersionOverrideMap.put( resourceName, version );
    }

    /**
     * Same as {@link EthosChangeNotificationService#addResourceVersionOverride(String, String)}
     * except the version can be abbreviated as just the version value, e.g: 16, or 16.0.0.  Also accepts the version value
     * prefixed with the char 'v', e.g: v16, or v16.0.0.
     * @param resourceName The name of the resource.
     * @param version The desired version of the resource to override change notification of the same resource with.
     */
    public void addResourceAbbreviatedVersionOverride( String resourceName, String version ) {
        if( version == null || version.isBlank() ) {
            throw new IllegalArgumentException("ERROR: Cannot add a resource version override for the EthosChangeNotificationService with a null or blank version header value." );
        }
        if( version.startsWith("v") || version.startsWith("V") ) {
            version = version.substring( 1 );
        }
        String fullVersion = EthosConfigurationClient.FULL_VERSION.replace( EthosConfigurationClient.FULL_VERSION_TAG, version );
        addResourceVersionOverride( resourceName, fullVersion );
    }

    /**
     * Gets a list of resources to be overridden in change notifications.  These are the resources added to the resource version override
     * capability of this class.
     * @return A list of resource names configured to override change notification content for the specified version.  Will be
     *         an empty list if no overrides have been specified.
     */
    public List<String> getOverriddenResources() {
        Set<String> resourceKeySet = resourceVersionOverrideMap.keySet();
        return new ArrayList<String>( resourceKeySet );
    }

    /**
     * Gets the overridden version for the given resource name specified when adding to the resource version override.
     * @param resourceName The name of the resource to get the version override for.
     * @return The version overriding the version of any change notification with the given resource name, or null if the resourceName
     *         is null or blank, or if there is no version mapping for the given resourceName.
     */
    public String getOverriddenResourceVersion( String resourceName ) {
        if( resourceName == null || resourceName.isBlank() ) {
            return null;
        }
        return resourceVersionOverrideMap.get( resourceName );
    }

    /**
     * Enables client application code to remove resource version overrides after this service has been built.
     * @param resourceName The name of the resource to remove from the resource version override map.
     */
    public void removeResourceVersionOverride( String resourceName ) {
        if( resourceName == null || resourceName.isBlank() ) {
            throw new IllegalArgumentException("ERROR: Cannot remove a resource version override for the EthosChangeNotificationService with a null or blank resource name value." );
        }
        resourceVersionOverrideMap.remove( resourceName );
    }

    /**
     * Retrieves change notifications using the default limit of 20 messages for message retrieval.
     * @return A list of ChangeNotifications, overriding any notifications with the desired version of the resource if
     *         notifications in the returned list match those added to this class (by resource name) to be overridden.
     * @throws IOException Propagated if thrown when retrieving messages.
     */
    public List<ChangeNotification> getChangeNotifications() throws IOException {
        List<ChangeNotification> changeNotificationList = ethosMessagesClient.consume();
        for( ChangeNotification cn : changeNotificationList ) {
            cn = processChangeNotificationOverrides( cn );
        }
        return changeNotificationList;
    }

    /**
     * Retrieves change notifications using the given limit for message retrieval.
     * @param limit The number of messages to retrieve at once.
     * @return A list of ChangeNotifications, overriding any notifications with the desired version of the resource if
     *         notifications in the returned list match those added to this class (by resource name) to be overridden.
     * @throws IOException Propagated if thrown when retrieving messages.
     */
    public List<ChangeNotification> getChangeNotifications(int limit) throws IOException {
        List<ChangeNotification> changeNotificationList = ethosMessagesClient.consumeWithLimit( limit );
        for( ChangeNotification cn : changeNotificationList ) {
            cn = processChangeNotificationOverrides( cn );
        }
        return changeNotificationList;
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Overrides and ChangeNotifications by resource and version that are listed in the resourceVersionOverrideMap.
     * Makes a call to retrieve the version of the resource listed in the resourceVersionOverrideMap, and replaces the
     * content of the given ChangeNotification if the given ChangeNotification matches the resource and version listed in the
     * override map.
     * @param changeNotification The ChangeNotification to retrieve the specified resource version for, if it matches what
     *                           is listed in the resourceVersionOverrideMap.
     * @return The given ChangeNotification with content replaced by the version of the resource listed in the override map
     *         if the resource and version of the changeNotification match the resource and version listed in the override map.
     * @throws IOException Propagated if thrown by ethosProxyClient when getting resource by version to override.
     */
    protected ChangeNotification processChangeNotificationOverrides( ChangeNotification changeNotification ) throws IOException {
        Set<String> resourceKeySet = resourceVersionOverrideMap.keySet();
        if( resourceKeySet.isEmpty() ) {
            return changeNotification;
        }
        Iterator<String> resourceKeyIter = resourceKeySet.iterator();
        while( resourceKeyIter.hasNext() ) {
            String resourceKey = resourceKeyIter.next();
            if( resourceKey.equalsIgnoreCase(changeNotification.getResource().getName()) ) {
                if( changeNotification.getOperation().equals(OPERATION_DELETED) ) {
                    break; // Just break here for any deleted operations even if the resource names match since we do not get a resource that's been deleted.
                }
                String versionValue = resourceVersionOverrideMap.get( resourceKey );
                if( versionValue.equals(changeNotification.getResource().getVersion()) == false ) {
                    // The version from the override map does not match the version from the changeNotification, so
                    // go get the resource for the version from the override map and replace it's content in the changeNotification.
                    EthosResponse resourceResponse = ethosProxyClient.getById( changeNotification.getResource().getName(), changeNotification.getResource().getId(), versionValue );
                    changeNotification = overrideChangeNotification( changeNotification, resourceResponse, versionValue );
                }
                break;  // Break here because this changeNotification matched the resource name override.  Cannot have more than 1 resource with the same name in the override map.
            }
        }
        return changeNotification;
    }

    /**
     * <b>Used internally by the SDK.</b>
     * <p>
     * Updates the given changeNotification with the data from the EthosResponse.  Specifically, updates the contentType,
     * content, and version of the changeNotification.
     * @param changeNotification The ChangeNotification to update/override.
     * @param ethosResponse The EthosResponse containing the data from the proxy call for overriding the changeNotification.
     * @param version The full version header of the resource overridden.
     * @return The updated ChangeNotification.
     * @throws com.fasterxml.jackson.core.JsonProcessingException Propagated if thrown by the ethosResponseConverter.
     */
    private ChangeNotification overrideChangeNotification( ChangeNotification changeNotification, EthosResponse ethosResponse, String version ) throws JsonProcessingException {
        Header contentRestrictedHeader = ethosResponse.getHeader( EthosProxyClient.HDR_X_CONTENT_RESTRICTED );
        if( contentRestrictedHeader != null ) {
            changeNotification.setContentType( contentRestrictedHeader.getValue() );
        }
        else {
            changeNotification.setContentType( RESOURCE_CONTENT_TYPE );
        }
        Header versionHeader = ethosResponse.getHeader( EthosProxyClient.HDR_X_MEDIA_TYPE );
        if( versionHeader != null ) {
            changeNotification.getResource().setVersion( versionHeader.getValue() );
        }
        else {
            changeNotification.getResource().setVersion( version );
        }
        changeNotification.setContent( ethosResponseConverter.toJsonNode(ethosResponse) );
        return changeNotification;
    }

}