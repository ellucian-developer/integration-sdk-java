/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;

import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.service.EthosChangeNotificationService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class containing the processing logic common for both ChangeNotification and ChangeNotification list
 * processing.  Contains the {@link EthosChangeNotificationService} used for retrieving ChangeNotifications.
 * Extends the {@link AbstractEthosNotificationSubscription} which is the primary base class for notification subscriptions.
 *
 * @since 0.2.0
 * @author David Kumar
 */
public abstract class AbstractEthosChangeNotificationSubscription extends AbstractEthosNotificationSubscription {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** The service used for retrieving ChangeNotifications. */
    protected EthosChangeNotificationService ethosChangeNotificationService;

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
    public AbstractEthosChangeNotificationSubscription( Flow.Publisher publisher,
                                                        Flow.Subscriber subscriber,
                                                        EthosChangeNotificationService ethosChangeNotificationService,
                                                        long pollingInterval,
                                                        TimeUnit pollingIntervalTimeUnit ) {
        super( publisher, subscriber, pollingInterval, pollingIntervalTimeUnit );
        if( ethosChangeNotificationService == null ) {
            throw new IllegalArgumentException( "ERROR: Cannot build an instance of a subclass of AbstractEthosChangeNotificationSubscription " +
                    "due to a null EthosChangeNotificationService.  Please provide a valid EthosChangeNotificationService reference." );
        }
        this.ethosChangeNotificationService = ethosChangeNotificationService;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Runs the ChangeNotification subscription process for receiving both individual ChangeNotifications and ChangeNotification lists.
     * Uses the scheduledExecutorService to schedule a thread to run the process for this subscription according to the
     * polling interval and pollingIntervalTimeUnit.  Has an initial delay of 1 second before running.
     * @param numNotifications Indicates the number of ChangeNotifications to retrieve at a single time from Ethos Integration
     *                         per poll request.  Must be between 1 and 1000.
     */
    @Override
    public void request(long numNotifications) {
        int numNotificationsInt = (int) numNotifications;
        cancelSubscription.set( false );
        long initialDelay = 1;
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay( () -> {
                processRequest(numNotificationsInt);
            },
            initialDelay,
            pollingInterval,
            pollingIntervalTimeUnit
        );
    }

    /**
     * Submits a cancellation request to cancel the current subscription process.  The process should stop running so that
     * no further notifications are received after the current poll attempt completes.
     */
    @Override
    public void cancel() {
        super.cancel();
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Processes the current polling attempt run by the scheduledExecutorService.  This method will retrieve ALL notifications
     * from Ethos Integration using the number of notifications (numNotifications) per request made via the
     * ethosChangeNotificationService.  If there are many notifications to retrieve, this method could take a while to run,
     * especially if the numNotifications is a small number.
     * @param numNotifications The number of notifications to retrieve at a time from Ethos Integration for each notification
     *                         request made.  Must be between 1 and 1000.
     */
    protected void processRequest( int numNotifications ) {
        try {
            // First check for cancellation here in case there are no notifications retrieved.
            if( cancelSubscription.get() ) {
                subscriber.onComplete();
                processCancellation();
                return;
            }
            List<ChangeNotification> cnList = getChangeNotifications( numNotifications );
            while( cnList != null && cnList.isEmpty() == false ) {
                processChangeNotifications( cnList );
                // Check if need to cancel after processing the notification list, before getting the next one.
                if( cancelSubscription.get() ) {
                    subscriber.onComplete();
                    processCancellation();
                    break;
                }
                cnList = getChangeNotifications( numNotifications );
            }
            subscriber.onComplete();
        }
        catch( IOException ioe ) {
            String msg = "ERROR: Exception thrown retrieving change notifications from a change notification subscription.  " +
                         "The change notification polling service will be shutdown due to the following error:  " + ioe.getMessage();
            subscriber.onError( new EthosChangeNotificationSubscriptionException(msg, ioe) );
            subscriber.onComplete();
            cancel();
            processCancellation();
        }
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Subclass implementations should override this method.
     * @param cnList The list of ChangeNotifications to propagate.
     */
    protected abstract void processChangeNotifications( List<ChangeNotification> cnList );

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Gets ChangeNotifications from Ethos Integration using the ethosChangeNotificationService.  The default number of
     * notifications will be retrieved if numNotifications is &lt; 0.
     * @param numNotifications The number of notifications to retrieve from Ethos Integration.
     * @return a List of ChangeNotifications, or an empty list of none are found.
     * @throws IOException Propagated if thrown from the ethosChangeNotificationService.
     */
    protected List<ChangeNotification> getChangeNotifications( int numNotifications ) throws IOException {
        List<ChangeNotification> cnList = null;
        if( numNotifications < 0 ) {
            cnList = ethosChangeNotificationService.getChangeNotifications();
        }
        else {
            cnList = ethosChangeNotificationService.getChangeNotifications( numNotifications );
        }
        return cnList;
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Processes the cancellation of this subscription.  Unsubscribes the subscriber from the publisher, cancels future
     * processing of the thread running this subscription, and shuts down the scheduledExecutorService running the schedule
     * for the thread running this subscription process.
     */
    protected void processCancellation() {
        if( scheduledFuture != null ) {
            ((AbstractEthosNotificationPollService) publisher).unsubscribe(subscriber);
            scheduledFuture.cancel(true);
            scheduledExecutorService.shutdownNow();
        }
    }

}