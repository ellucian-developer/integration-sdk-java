/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;

import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.service.EthosChangeNotificationService;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

/**
 * Service for distributing lists of Ethos ChangeNotifications retrieved from Ethos Integration.  Implements the
 * {@link Flow.Publisher} interface, but this does NOT publish messages to Ethos Integration.  Serves as a "publisher"
 * to push notification messages out to client application code from Ethos Integration through the SDK.  An instance of
 * this class should be used to subscribe client application ChangeNotification list subscribers (extending {@link AbstractEthosChangeNotificationListSubscriber}).
 *
 * @since 0.2.0
 * @author David Kumar
 */
public class EthosChangeNotificationListPollService extends AbstractEthosNotificationPollService implements Flow.Publisher<List<ChangeNotification>> {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /** Used by the EthosChangeNotificationSubscription built by this class. */
    protected EthosChangeNotificationService ethosChangeNotificationService;

    /**
     * Constructs an instance of this class with the given {@link EthosChangeNotificationService}.  Uses the default
     * {@link AbstractEthosNotificationPollService#DEFAULT_POLL_INTERVAL_SECONDS} polling interval.
     * @param ethosChangeNotificationService The ChangeNotification service to use for retrieving notification messages.
     * @throws IllegalArgumentException Thrown if the ethosChangeNotificationService is null.
     */
    public EthosChangeNotificationListPollService(EthosChangeNotificationService ethosChangeNotificationService ) {
        super();
        if( ethosChangeNotificationService == null ) {
            throw new IllegalArgumentException( "ERROR: Cannot build EthosChangeNotificationListPublisher due to a null EthosChangeNotificationService.  " +
                    "Please provide a valid EthosChangeNotificationService reference." );
        }
        this.ethosChangeNotificationService = ethosChangeNotificationService;
    }

    /**
     * Constructs an instance of this class with the given params.
     * @param ethosChangeNotificationService The ChangeNotification service to use for retrieving notification messages.
     * @param pollingIntervalSeconds The polling interval between poll attempts to retrieve notification messages, in seconds.
     * @throws IllegalArgumentException Thrown if the ethosChangeNotificationService is null.
     */
    public EthosChangeNotificationListPollService(EthosChangeNotificationService ethosChangeNotificationService, long pollingIntervalSeconds ) {
        super( pollingIntervalSeconds );
        if( ethosChangeNotificationService == null ) {
            throw new IllegalArgumentException( "ERROR: Cannot build EthosChangeNotificationListPublisher due to a null EthosChangeNotificationService.  " +
                    "Please provide a valid EthosChangeNotificationService reference." );
        }
        this.ethosChangeNotificationService = ethosChangeNotificationService;
    }
    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Subscribes the given subscriber to an {@link EthosChangeNotificationListSubscription}, initiating the process for
     * the subscriber to receive entire lists of ChangeNotifications at a time.
     * @param subscriber An instance of a client application implementation of {@link AbstractEthosChangeNotificationListSubscriber}.
     */
    @Override
    public void subscribe( Flow.Subscriber<? super List<ChangeNotification>> subscriber ) {
        if( subscriber == null ) {
            throw new IllegalArgumentException("ERROR: Cannot subscribe due to a null list subscriber.  Please provide a valid subscriber." );
        }
        EthosChangeNotificationListSubscription subscription = new EthosChangeNotificationListSubscription( this, subscriber, ethosChangeNotificationService, pollingIntervalSeconds, TimeUnit.SECONDS );
        subscriberMap.put( subscriber, subscription );
        subscriber.onSubscribe( subscription );
    }

}