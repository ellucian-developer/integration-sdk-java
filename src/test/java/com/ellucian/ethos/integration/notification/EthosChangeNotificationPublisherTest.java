/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;


import com.ellucian.ethos.integration.service.EthosChangeNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.Flow;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class EthosChangeNotificationPublisherTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    @Mock
    private Flow.Subscriber mockSubscriber;

    @Mock
    private Flow.Subscriber mockSubscriberA;

    @Mock
    private Flow.Subscriber mockSubscriberB;

    @Mock
    private Flow.Subscriber mockSubscriberC;

    @Mock
    private EthosChangeNotificationService mockEthosChangeNotificationService;

    // ==========================================================================
    // Methods
    // ==========================================================================

    @Test
    public void publisherThrowsExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EthosChangeNotificationPollService( null, 10 );
        });
    }

    @Test
    public void subscribeThrowsExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            EthosChangeNotificationPollService publisher = new EthosChangeNotificationPollService( mockEthosChangeNotificationService, 10 );
            publisher.subscribe( null );
        });
    }

    @Test
    public void subscribeTest() {
        Mockito.doNothing().when(mockSubscriber).onSubscribe( any(Flow.Subscription.class) );
        EthosChangeNotificationPollService publisher = new EthosChangeNotificationPollService( mockEthosChangeNotificationService, 10 );
        // Run the test.
        publisher.subscribe( mockSubscriber );
        // Examine the result.
        Mockito.verify(mockSubscriber, Mockito.times(1)).onSubscribe( any(Flow.Subscription.class) );
        assert( publisher.getNumberOfSubscribers() == 1 );
    }

    @Test
    public void unsubscribeThrowsExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            EthosChangeNotificationPollService publisher = new EthosChangeNotificationPollService( mockEthosChangeNotificationService, 10 );
            publisher.unsubscribe( null );
        });
    }

    @Test
    public void unsubscribeTest() {
        Mockito.doNothing().when(mockSubscriber).onSubscribe( any(Flow.Subscription.class) );
        EthosChangeNotificationPollService publisher = new EthosChangeNotificationPollService( mockEthosChangeNotificationService, 10 );
        // First subscribe so that there is something to unsubscribe.
        publisher.subscribe( mockSubscriber );
        // Run the test.
        publisher.unsubscribe( mockSubscriber );
        // Examine the result.
        assert( publisher.getNumberOfSubscribers() == 0 );
    }

    @Test
    public void getNumberOfSubscribersTest() {
        Mockito.doNothing().when(mockSubscriberA).onSubscribe( any(Flow.Subscription.class) );
        Mockito.doNothing().when(mockSubscriberB).onSubscribe( any(Flow.Subscription.class) );
        Mockito.doNothing().when(mockSubscriberC).onSubscribe( any(Flow.Subscription.class) );
        EthosChangeNotificationPollService publisher = new EthosChangeNotificationPollService( mockEthosChangeNotificationService, 10 );
        // Subscribe 3 subscribers.
        publisher.subscribe( mockSubscriberA );
        publisher.subscribe( mockSubscriberB );
        publisher.subscribe( mockSubscriberC );
        // Run the test.
        int numSubscribers = publisher.getNumberOfSubscribers();
        // Examine the results.
        assert( numSubscribers == 3 );
    }

    @Test
    public void getSubscribersTest() {
        Mockito.doNothing().when(mockSubscriberA).onSubscribe( any(Flow.Subscription.class) );
        Mockito.doNothing().when(mockSubscriberB).onSubscribe( any(Flow.Subscription.class) );
        Mockito.doNothing().when(mockSubscriberC).onSubscribe( any(Flow.Subscription.class) );
        EthosChangeNotificationPollService publisher = new EthosChangeNotificationPollService( mockEthosChangeNotificationService, 10 );
        // Subscribe 3 subscribers.
        publisher.subscribe( mockSubscriberA );
        publisher.subscribe( mockSubscriberB );
        publisher.subscribe( mockSubscriberC );
        // Run the test.
        List<Flow.Subscriber> subscriberList = publisher.getSubscribers();
        // Examine the results.
        assert( subscriberList.size() == 3 );
        assert( subscriberList.contains(mockSubscriberA) );
        assert( subscriberList.contains(mockSubscriberB) );
        assert( subscriberList.contains(mockSubscriberC) );
    }

}