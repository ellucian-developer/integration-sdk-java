/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;


import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.service.EthosChangeNotificationService;

import java.util.List;
import java.util.concurrent.*;

/**
 * Subscription for ChangeNotification processing.  This subscription runs in a separate thread as configured by the
 * scheduledExecutorService.
 * @since 0.2.0
 * @author David Kumar
 */
public class EthosChangeNotificationSubscription extends AbstractEthosChangeNotificationSubscription {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /**
     * Constructs this subscription with the given params.
     * @param publisher The publisher for this subscription.
     * @param subscriber The subscriber subscribing to this subscription.  Should be a client application subscriber that
     *                   extends AbstractEthosChangeNotificationSubscriber.
     * @param ethosChangeNotificationService Service for retrieving ChangeNotifications.
     * @param pollingInterval The polling time interval between polling attempts.
     * @param pollingIntervalTimeUnit The time unit for the polling interval.
     * @throws IllegalArgumentException Thrown if the given ethosChangeNotificationService is null.
     */
    public EthosChangeNotificationSubscription( Flow.Publisher publisher,
                                                Flow.Subscriber subscriber,
                                                EthosChangeNotificationService ethosChangeNotificationService,
                                                long pollingInterval,
                                                TimeUnit pollingIntervalTimeUnit ) {
        super( publisher, subscriber, ethosChangeNotificationService, pollingInterval, pollingIntervalTimeUnit );
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Iterates over the given ChangeNotification list and calls subscriber.onNext() for each notification.
     * If an exception is thrown, will also call subscriber.onError() passing the given Throwable.
     * @param cnList The list of ChangeNotifications to propagate.
     */
    protected void processChangeNotifications( List<ChangeNotification> cnList ) {
        for( ChangeNotification cn : cnList ) {
            try {
                subscriber.onNext( cn );
            }
            catch( Throwable throwable ) {
                subscriber.onError( throwable );
            }
        }
    }

}