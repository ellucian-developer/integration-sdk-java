/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;

import com.ellucian.ethos.integration.client.messages.ChangeNotification;

import java.util.concurrent.Flow;

/**
 * Abstract class which provides "hooks" into the Ethos SDK for ChangeNotification subscribers.  Client application code
 * should extend this class and implement the abstract methods to handle individual ChangeNotifications as desired.
 * <p>
 * A client application code ChangeNotification subscriber extending this class will become active in receiving ChangeNotifications
 * after being subscribed to an EthosChangeNotificationPublisher via the {@link EthosChangeNotificationPollService#subscribe(Flow.Subscriber)}
 * method.  A client subscriber will stop receiving ChangeNotifications after calling the {@link AbstractEthosChangeNotificationSubscriber#cancelSubscription()}
 * method.
 * @since 0.2.0
 * @author David Kumar
 */
public abstract class AbstractEthosChangeNotificationSubscriber extends AbstractEthosNotificationSubscriber implements Flow.Subscriber<ChangeNotification> {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /**
     * Enables subclasses to construct this class without specifying the number of notifications to retrieve
     * from Ethos Integration at a single time.
     */
    public AbstractEthosChangeNotificationSubscriber() {
        super();
    }

    /**
     * Enables subclasses to construct this class with the specified number of notifications to retrieve from Ethos
     * Integration at a single time (per polling request).
     * @param numNotifications The number of notifications to retrieve from Ethos Integration per polling request.
     */
    public AbstractEthosChangeNotificationSubscriber( Integer numNotifications ) {
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
     * to the {@link AbstractEthosChangeNotificationSubscriber#onChangeNotification(ChangeNotification)} method.
     * @param item The ChangeNotification to process.
     */
    @Override
    public void onNext(ChangeNotification item) {
        onChangeNotification( item );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Called by the subscription to handle any exceptions thrown during the processing call to onNext().
     * Relays the throwable to the {@link AbstractEthosChangeNotificationSubscriber#onChangeNotificationError(Throwable)} method.
     * @param throwable The exception caught by the subscription.
     */
    @Override
    public void onError(Throwable throwable) {
        onChangeNotificationError( throwable );
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
     * for processing ChangeNotifications.
     * @param changeNotification The ChangeNotification to process.
     */
    public abstract void onChangeNotification( ChangeNotification changeNotification );

    /**
     * This is the hook which client application code should implement when subclassing this class for
     * handling errors that occurred during onChangeNotification().
     * @param throwable The exception thrown during the call to onChangeNotification().
     */
    public abstract void onChangeNotificationError( Throwable throwable );


}