/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;


import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AbstractEthosChangeNotificationSubscriberTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    @Mock
    private EthosChangeNotificationSubscription mockSubscription;

    // Need an implementation of the abstract class to test with.
    public class MyEthosChangeNotificationSubscriber extends AbstractEthosChangeNotificationSubscriber {
        public MyEthosChangeNotificationSubscriber() {
            super();
        }
        public MyEthosChangeNotificationSubscriber( Integer numNotifications ) {
            this();
            this.numNotifications = numNotifications;
        }
        @Override
        public void onChangeNotification(ChangeNotification changeNotification) {

        }
        @Override
        public void onChangeNotificationError(Throwable throwable) {

        }
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    @Test
    public void onSubscribeThrowsExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            MyEthosChangeNotificationSubscriber subscriber = new MyEthosChangeNotificationSubscriber();
            subscriber.onSubscribe( null );
        });
    }

    @Test
    public void onSubscribeWithNumNotificationsTest() {
        Integer numNotifications = 3;
        MyEthosChangeNotificationSubscriber subscriber = new MyEthosChangeNotificationSubscriber( numNotifications );
        Mockito.doNothing().when(mockSubscription).request( numNotifications );
        // Run the test.
        subscriber.onSubscribe( mockSubscription );
        // Examine the results.
        Mockito.verify(mockSubscription, Mockito.times(1)).request( numNotifications );
    }

    @Test
    public void onSubscribeWithDefaultNumNotificationsTest() {
        MyEthosChangeNotificationSubscriber subscriber = new MyEthosChangeNotificationSubscriber();
        Mockito.doNothing().when(mockSubscription).request( AbstractEthosChangeNotificationSubscriber.DEFAULT_NUM_NOTIFICATIONS );
        // Run the test.
        subscriber.onSubscribe( mockSubscription );
        // Examine the results.
        Mockito.verify(mockSubscription, Mockito.times(1)).request( AbstractEthosChangeNotificationSubscriber.DEFAULT_NUM_NOTIFICATIONS );
    }

    @Test
    public void onNextTest() {
        MyEthosChangeNotificationSubscriber spySubscriber = Mockito.spy( new MyEthosChangeNotificationSubscriber() );
        ChangeNotification cn = new ChangeNotification();
        // Run the test.
        spySubscriber.onNext( cn );
        // Examine the result.
        Mockito.verify(spySubscriber, Mockito.times(1)).onChangeNotification( cn );
    }

    @Test
    public void onErrorTest() {
        MyEthosChangeNotificationSubscriber spySubscriber = Mockito.spy( new MyEthosChangeNotificationSubscriber() );
        RuntimeException re = new RuntimeException();
        // Run the test.
        spySubscriber.onError( re );
        // Examine the result.
        Mockito.verify(spySubscriber, Mockito.times(1)).onChangeNotificationError( re );
    }

    @Test
    public void cancelSubscriptionTest() {
        MyEthosChangeNotificationSubscriber subscriber = new MyEthosChangeNotificationSubscriber();
        Mockito.doNothing().when(mockSubscription).request( AbstractEthosChangeNotificationSubscriber.DEFAULT_NUM_NOTIFICATIONS );
        Mockito.doNothing().when(mockSubscription).cancel();
        // First call onSubscribe so that it can be cancelled.
        subscriber.onSubscribe( mockSubscription );
        // Run the test.
        subscriber.cancelSubscription();
        // Examine the results.
        Mockito.verify(mockSubscription, Mockito.times(1)).cancel();
    }

}