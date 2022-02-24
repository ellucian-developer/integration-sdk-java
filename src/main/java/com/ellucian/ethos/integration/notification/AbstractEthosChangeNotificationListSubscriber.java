/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;

import com.ellucian.ethos.integration.client.messages.ChangeNotification;

import java.util.List;
import java.util.concurrent.Flow;

/**
 * Abstract class which provides "hooks" into the Ethos SDK for ChangeNotification list subscribers.  Client application code
 * should extend this class and implement the abstract methods to handle entire lists of ChangeNotifications as desired.
 * <p>
 * A client application code ChangeNotification list subscriber extending this class will become active in receiving lists
 * of ChangeNotifications after being subscribed to an EthosChangeNotificationListPublisher via the {@link EthosChangeNotificationListPollService#subscribe(Flow.Subscriber)}
 * method.  A client subscriber will stop receiving ChangeNotification lists after calling the {@link AbstractEthosChangeNotificationListSubscriber#cancelSubscription()}
 * method.
 * @since 0.2.0
 * @author David Kumar
 */
public abstract class AbstractEthosChangeNotificationListSubscriber extends AbstractEthosNotificationSubscriber implements Flow.Subscriber<List<ChangeNotification>> {

    /**
     * Enables subclasses to construct this class without specifying the number of notifications to retrieve
     * from Ethos Integration at a single time.
     */
    public AbstractEthosChangeNotificationListSubscriber() {
        super();
    }

    /**
     * Enables subclasses to construct this class with the specified number of notifications to retrieve from Ethos
     * Integration at a single time (per polling request).
     * @param numNotifications The number of notifications to retrieve from Ethos Integration per polling request.
     */
    public AbstractEthosChangeNotificationListSubscriber( Integer numNotifications ) {
        super( numNotifications );
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
    @Override
    public void onSubscribe( Flow.Subscription subscription ) {
        doSubscribe( subscription );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Called by the subscription used by this subscriber for each notification.  Relays the given notification
     * to the {@link AbstractEthosChangeNotificationListSubscriber#onChangeNotificationList(List)} method.
     * @param item The list of ChangeNotifications to process.
     */
    @Override
    public void onNext(List<ChangeNotification> item) {
        onChangeNotificationList( item );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Called by the subscription to handle any exceptions thrown during the processing call to onNext().
     * Relays the throwable to the {@link AbstractEthosChangeNotificationListSubscriber#onChangeNotificationListError(Throwable)} method.
     * @param throwable The exception caught by the subscription.
     */
    @Override
    public void onError(Throwable throwable) {
        onChangeNotificationListError( throwable );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Called by the subscriber when a given polling operation is complete.
     */
    @Override
    public void onComplete() {
    }

    /**
     * This is the hook which client application code should implement when subclassing this class
     * for processing entire lists of ChangeNotifications.
     * @param changeNotificationList The list of ChangeNotifications to process.
     */
    public abstract void onChangeNotificationList( List<ChangeNotification> changeNotificationList );

    /**
     * This is the hook which client application code should implement when subclassing this class for
     * handling errors that occurred during onChangeNotificationList().
     * @param throwable The exception thrown during the call to onChangeNotification().
     */
    public abstract void onChangeNotificationListError( Throwable throwable );

}