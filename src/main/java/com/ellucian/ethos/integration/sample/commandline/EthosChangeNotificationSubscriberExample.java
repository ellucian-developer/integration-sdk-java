/*
 * ******************************************************************************
 *   Copyright  2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.sample.commandline;

import com.ellucian.ethos.integration.notification.EthosChangeNotificationListPollService;
import com.ellucian.ethos.integration.notification.EthosChangeNotificationPollService;
import com.ellucian.ethos.integration.service.EthosChangeNotificationService;

import java.io.IOException;

public class EthosChangeNotificationSubscriberExample {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private String apiKey;

    public EthosChangeNotificationSubscriberExample( String apiKey ) {
        this.apiKey = apiKey;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    public static void main( String[] args ) {
        if( args == null || args.length == 0 ) {
            System.out.println("Please enter an API key as a program argument when running this example class.  " +
                               "An API key is required to run these examples.");
            return;
        }
        String apiKey = args[ 0 ];
        EthosChangeNotificationSubscriberExample subscriberExample = new EthosChangeNotificationSubscriberExample( apiKey );
        subscriberExample.subscribeToChangeNotifications();
        subscriberExample.subscribeToChangeNotificationLists();
    }

    public void subscribeToChangeNotifications() {
        int numNotifications = 3;
        long pollingIntervalSeconds = 5;
        EthosChangeNotificationService cnService = new EthosChangeNotificationService.Builder(apiKey)
                                                   .build();
        EthosChangeNotificationPollService ethosChangeNotificationPollService = new EthosChangeNotificationPollService( cnService, pollingIntervalSeconds );
        MyChangeNotificationSubscriber myChangeNotificationSubscriber = new MyChangeNotificationSubscriber( numNotifications );
        // This starts the notification process for the subscriber receiving notifications.
        ethosChangeNotificationPollService.subscribe( myChangeNotificationSubscriber );
        try {
            // Sleeping to simulate time taken to do other stuff...
            // Notifications should come through the MyChangeNotificationSubscriber.onChangeNotification() method.
            Thread.sleep(3000);
        }
        catch( InterruptedException ie ) {
            ie.printStackTrace();
        }
        // When ready, cancel the subscription to stop receiving notifications.
        if( myChangeNotificationSubscriber.isSubscriptionRunning() ) {
            myChangeNotificationSubscriber.cancelSubscription();
        }
    }

    public void subscribeToChangeNotificationLists() {
        int numNotifications = 3;
        long pollingIntervalSeconds = 5;
        EthosChangeNotificationService cnService = new EthosChangeNotificationService.Builder(apiKey)
                                                   .build();
        EthosChangeNotificationListPollService ethosChangeNotificationListPollService = new EthosChangeNotificationListPollService( cnService, pollingIntervalSeconds );
        MyChangeNotificationListSubscriber myChangeNotificationListSubscriber = new MyChangeNotificationListSubscriber( numNotifications );
        // This starts the notification process for the subscriber receiving list notifications.
        ethosChangeNotificationListPollService.subscribe( myChangeNotificationListSubscriber );
        try {
            // Sleeping to simulate time taken to do other stuff...
            // Notifications should come through the MyChangeNotificationListSubscriber.onChangeNotificationList() method.
            Thread.sleep(3000);
        }
        catch( InterruptedException ie ) {
            ie.printStackTrace();
        }
        // When ready, cancel the list subscription to stop receiving list notifications.
        if( myChangeNotificationListSubscriber.isSubscriptionRunning() ) {
            myChangeNotificationListSubscriber.cancelSubscription();
        }
    }

}