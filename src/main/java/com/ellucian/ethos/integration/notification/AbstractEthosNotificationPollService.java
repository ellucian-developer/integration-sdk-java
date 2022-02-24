/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;

/**
 * Abstract class containing a mapping of subscribers to subscriptions, and the polling interval in seconds to wait between
 * poll attempts.  Extended by subclasses implementing the {@link Flow.Publisher} interface.
 *
 * @since 0.2.0
 * @author David Kumar
 */
public abstract class AbstractEthosNotificationPollService {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /** The default poll interval in seconds.  This is how long to wait between poll attempts by default if poll interval is not specified.*/
    public static final long DEFAULT_POLL_INTERVAL_SECONDS = 60;

    /** Maps the subscribers to their subscriptions, as there could be multiple instances of subscribers. */
    protected Map<Flow.Subscriber,Flow.Subscription> subscriberMap;

    /** The polling interval in seconds between poll attempts to retrieve notification messages from Ethos Integration. */
    protected long pollingIntervalSeconds;

    /**
     * No-arg constructor setting the pollingIntervalSeconds to DEFAULT_POLL_INTERVAL_SECONDS.
     */
    public AbstractEthosNotificationPollService() {
        this.pollingIntervalSeconds = DEFAULT_POLL_INTERVAL_SECONDS;
        this.subscriberMap = new HashMap<>();
    }

    /**
     * Constructs an instance of this class with the given params.
     * @param pollingIntervalSeconds The polling interval between poll attempts to retrieve notification messages, in seconds.
     * @throws IllegalArgumentException Thrown if the ethosChangeNotificationService is null.
     */
    public AbstractEthosNotificationPollService( long pollingIntervalSeconds ) {
        this();
        this.pollingIntervalSeconds = pollingIntervalSeconds;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    /**
     * Unsubscribes the given subscriber from this publisher, removing them from the subscriber map.
     * @param subscriber The subscriber to unsubscribe.
     * @throws IllegalArgumentException Thrown if the given subscriber is null.
     */
    public void unsubscribe( Flow.Subscriber subscriber ) {
        if( subscriber == null ) {
            throw new IllegalArgumentException("ERROR: Cannot unsubscribe a null subscriber." );
        }
        subscriberMap.remove( subscriber );
    }

    /**
     * Gets the number of subscribers this publisher is responsible for.
     * @return The number of subscribers this publisher has.
     */
    public int getNumberOfSubscribers() {
        return subscriberMap.size();
    }

    /**
     * Gets a list of the subscribers this publisher has.
     * @return A list of subscribers for this publisher.
     */
    public List<Flow.Subscriber> getSubscribers() {
        return new ArrayList( subscriberMap.keySet() );
    }

}