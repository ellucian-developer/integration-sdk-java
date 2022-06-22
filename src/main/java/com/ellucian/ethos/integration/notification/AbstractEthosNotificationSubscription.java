/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An abstract super class for Ethos subscriptions containing the publisher, subscriber, and the threading capabilities
 * used by subscription subclasses to run those subscription processes.
 * <p>
 * Subscriptions will run indefinitely until canceled.  The {@link #cancel()} method must be called to stop the
 * subscription from running.
 * <p>
 * Implements the {@link Flow.Subscription} interface.
 * @since 0.2.0
 * @author David Kumar
 */
public abstract class AbstractEthosNotificationSubscription implements Flow.Subscription {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** The publisher of this subscription. */
    protected Flow.Publisher publisher;

    /** The subscriber subscribing to this subscription. */
    protected Flow.Subscriber subscriber;

    /** Used to manage cancellation requests for cancelling the thread running this subscription. */
    protected AtomicBoolean cancelSubscription;

    /** Runs the thread for this subscription on a polling interval schedule. */
    protected ScheduledExecutorService scheduledExecutorService;

    /** Assigned by the scheduledExecutorService when it schedules the subscription process to run. */
    protected ScheduledFuture scheduledFuture;

    /**
     * The amount of time to wait between polls.  A single poll will process all notifications from Ethos Integration.
     * Subsequent polls will then process further notifications as they are available.
     */
    protected long pollingInterval;

    /** The time unit for the polling interval.  Commonly specified by the publisher as TimeUnit.SECONDS. */
    protected TimeUnit pollingIntervalTimeUnit;

    /**
     * Super constructor which must be called by subclasses supplying the given params.  If the given pollingIntervalTimeUnit is null,
     * it defaults to TimeUnit.SECONDS.
     * @param publisher The publisher for this subscription.
     * @param subscriber The subscriber subscribing to this subscription.
     * @param pollingInterval The polling interval between poll attempts.
     * @param pollingIntervalTimeUnit The time unit for polling intervals.
     * @throws IllegalArgumentException Thrown if the given publisher or subscriber is null.
     */
    public AbstractEthosNotificationSubscription( Flow.Publisher publisher, Flow.Subscriber subscriber, long pollingInterval, TimeUnit pollingIntervalTimeUnit ) {
        if( publisher == null ) {
            throw new IllegalArgumentException( "ERROR: Cannot build subscription due to a null publisher.  Please provide a valid publisher reference." );
        }
        if( subscriber == null ) {
            throw new IllegalArgumentException( "ERROR: Cannot build subscription due to a null subscriber.  Please provide a valid subscriber reference." );
        }
        this.publisher = publisher;
        this.subscriber = subscriber;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledFuture = null;
        this.cancelSubscription = new AtomicBoolean( false );
        this.pollingInterval = pollingInterval;
        if( pollingIntervalTimeUnit == null ) {
            this.pollingIntervalTimeUnit = TimeUnit.SECONDS;
        }
        else {
            this.pollingIntervalTimeUnit = pollingIntervalTimeUnit;
        }
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Should be overridden by subscription subclasses in the SDK.
     * @param numNotifications Indicates some number of notifications to process.
     */
    @Override
    public void request(long numNotifications) {
    }

    /**
     * Sets the cancellation flag to true, thereby requesting that the subscription stop running after the current
     * polling operation completes.  Subscriptions will run indefinitely, so this method must be called to stop
     * them from running.
     */
    @Override
    public void cancel() {
        cancelSubscription.set( true );
    }

    /**
     * Indicates if this subscription is still running, or not.
     * @return true if this subscription is running, in which case a subscriber would be subscribed to it and be receiving
     *         notifications (if any are retrieved), false if this subscription has been canceled.
     */
    public boolean isRunning() {
        if( scheduledFuture == null ) {
            return false;
        }
        if( scheduledExecutorService == null ) {
            return false;
        }
        if( scheduledExecutorService.isShutdown() ) {
            return false;
        }
        if( scheduledExecutorService.isTerminated() ) {
            return false;
        }
        return true;
    }

}