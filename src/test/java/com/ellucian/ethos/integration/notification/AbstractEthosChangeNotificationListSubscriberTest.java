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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AbstractEthosChangeNotificationListSubscriberTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    @Mock
    private EthosChangeNotificationListSubscription mockSubscription;

    // Need an implementation of the abstract class to test with.
    public class MyEthosChangeNotificationListSubscriber extends AbstractEthosChangeNotificationListSubscriber {
        public MyEthosChangeNotificationListSubscriber() {
            super();
        }
        public MyEthosChangeNotificationListSubscriber( Integer numNotifications ) {
            this();
            this.numNotifications = numNotifications;
        }
        @Override
        public void onChangeNotificationList(List<ChangeNotification> changeNotificationList) {

        }
        @Override
        public void onChangeNotificationListError(Throwable throwable) {

        }
    }
    // ==========================================================================
    // Methods
    // ==========================================================================

    @Test
    public void onSubscribeThrowsExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            MyEthosChangeNotificationListSubscriber subscriber = new MyEthosChangeNotificationListSubscriber();
            subscriber.onSubscribe( null );
        });
    }

    @Test
    public void onSubscribeWithNumNotificationsTest() {
        Integer numNotifications = 3;
        MyEthosChangeNotificationListSubscriber subscriber = new MyEthosChangeNotificationListSubscriber( numNotifications );
        Mockito.doNothing().when(mockSubscription).request( numNotifications );
        // Run the test.
        subscriber.onSubscribe( mockSubscription );
        // Examine the results.
        Mockito.verify(mockSubscription, Mockito.times(1)).request( numNotifications );
    }

    @Test
    public void onSubscribeWithDefaultNumNotificationsTest() {
        MyEthosChangeNotificationListSubscriber subscriber = new MyEthosChangeNotificationListSubscriber();
        Mockito.doNothing().when(mockSubscription).request( AbstractEthosChangeNotificationSubscriber.DEFAULT_NUM_NOTIFICATIONS );
        // Run the test.
        subscriber.onSubscribe( mockSubscription );
        // Examine the results.
        Mockito.verify(mockSubscription, Mockito.times(1)).request( AbstractEthosChangeNotificationSubscriber.DEFAULT_NUM_NOTIFICATIONS );
    }

    @Test
    public void onNextTest() {
        MyEthosChangeNotificationListSubscriber spySubscriber = Mockito.spy( new MyEthosChangeNotificationListSubscriber() );
        List<ChangeNotification> cnList = new ArrayList<>();
        ChangeNotification cn = new ChangeNotification();
        cnList.add( cn );
        // Run the test.
        spySubscriber.onNext( cnList );
        // Examine the result.
        Mockito.verify(spySubscriber, Mockito.times(1)).onChangeNotificationList( cnList );
    }

    @Test
    public void onErrorTest() {
        MyEthosChangeNotificationListSubscriber spySubscriber = Mockito.spy( new MyEthosChangeNotificationListSubscriber() );
        RuntimeException re = new RuntimeException();
        // Run the test.
        spySubscriber.onError( re );
        // Examine the result.
        Mockito.verify(spySubscriber, Mockito.times(1)).onChangeNotificationListError( re );
    }

    @Test
    public void cancelSubscriptionTest() {
        MyEthosChangeNotificationListSubscriber subscriber = new MyEthosChangeNotificationListSubscriber();
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