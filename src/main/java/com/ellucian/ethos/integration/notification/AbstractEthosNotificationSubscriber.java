/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;

import java.util.concurrent.Flow;

/**
 * Abstract class containing common processing logic for subscribing to and cancelling subscriptions.  Subclasses should
 * implement the {@link Flow.Subscriber} interface.
 * @since 0.2.0
 * @author David Kumar
 */
public abstract class AbstractEthosNotificationSubscriber {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /**
     * Constant for indicating the default number of notifications to be retrieved at a time from Ethos Integration.
     * The value of this constant is not the default number of notifications that Ethos Integration uses. */
    public static final int DEFAULT_NUM_NOTIFICATIONS = -1;

    /** The number of notifications to request at a time from Ethos Integration.  Must be between 1 and 1000. */
    protected Integer numNotifications;

    /**  The subscription used by this subscriber. */
    protected Flow.Subscription subscription;

    /**
     * Enables subclasses to construct this class without specifying the number of notifications to retrieve
     * from Ethos Integration at a single time.
     */
    public AbstractEthosNotificationSubscriber() {
        this.numNotifications = null;
    }

    /**
     * Enables subclasses to construct this class with the specified number of notifications to retrieve from Ethos
     * Integration at a single time (per polling request).
     * @param numNotifications The number of notifications to retrieve from Ethos Integration per polling request.
     */
    public AbstractEthosNotificationSubscriber( Integer numNotifications ) {
        this();
        this.numNotifications = numNotifications;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Initiates the subscription process for this subscriber.
     * @param subscription The subscription to process, or request notifications for.
     */
    public void doSubscribe( Flow.Subscription subscription ) {
        if( subscription == null ) {
            throw new IllegalArgumentException( "ERROR: Cannot subscribe with a null subscription." );
        }
        this.subscription = subscription;
        if( numNotifications == null ) {
            subscription.request( DEFAULT_NUM_NOTIFICATIONS );
        }
        else {
            subscription.request( numNotifications );
        }
    }

    /**
     * Cancels the subscription used by this subscriber.  This will request for the thread running the subscription to
     * stop after the current polling operation is completed, after which no further notifications should be received
     * for this subscriber.
     */
    public void cancelSubscription() {
        if( subscription != null ) {
            subscription.cancel();
        }
    }

    /**
     * Indicates whether the subscription for this subscriber is running, or not.
     * @return true if the subscription is running, false if not (if it were canceled).
     */
    public boolean isSubscriptionRunning() {
        if( subscription == null ) {
            return false;
        }
        return ((AbstractEthosNotificationSubscription)subscription).isRunning();
    }

}