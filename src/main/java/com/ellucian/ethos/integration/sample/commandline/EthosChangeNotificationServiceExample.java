/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.sample.commandline;


import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.service.EthosChangeNotificationService;

import java.io.IOException;
import java.util.List;

/**
 * Prerequisite to running this example class:
 * Configuration must be previously setup properly in Ethos Integration, and messages must be waiting for consumption,
 * in order to consume messages using the EthosChangeNotificationService.
 */
public class EthosChangeNotificationServiceExample {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private String apiKey;

    public EthosChangeNotificationServiceExample(String apiKey ) {
        this.apiKey = apiKey;
    }


    // ==========================================================================
    // Methods
    // ==========================================================================
    public static void main(String[] args) {
        if( args == null || args.length == 0 ) {
            System.out.println("Please enter an API key as a program argument when running this example class.  " +
                               "An API key is required to run these examples.");
            return;
        }
        String apiKey = args[ 0 ];
        EthosChangeNotificationServiceExample ethosChangeNotificationServiceExample = new EthosChangeNotificationServiceExample( apiKey );
        ethosChangeNotificationServiceExample.getNotificationsWithoutOverridesExample();
        ethosChangeNotificationServiceExample.getNotificationsWithOverridesExample();
    }

    public void getNotificationsWithoutOverridesExample() {
        System.out.println( "******* getNotificationsWithoutOverridesExample() *******" );
        EthosClientBuilder ethosClientBuilder = new EthosClientBuilder(apiKey)
                                                .withConnectionTimeout(30)
                                                .withConnectionRequestTimeout(30)
                                                .withSocketTimeout(30);
        EthosChangeNotificationService ethosChangeNotificationService = new EthosChangeNotificationService.Builder(ethosClientBuilder)
                                                                        .build();
        // The number of notifications to retrieve at a single time, defaults to 20 if not specified.
        int changeNotificationLimit = 3;
        try {
            List<ChangeNotification> changeNotificationList = ethosChangeNotificationService.getChangeNotifications( changeNotificationLimit );
            System.out.println( "CHANGE NOTIFICATION LIST LENGTH: " + changeNotificationList.size() );
            System.out.println( changeNotificationList.toString() );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public void getNotificationsWithOverridesExample() {
        System.out.println( "******* getNotificationsWithOverridesExample() *******" );
        EthosClientBuilder ethosClientBuilder = new EthosClientBuilder(apiKey)
                                                .withConnectionTimeout(30)
                                                .withConnectionRequestTimeout(30)
                                                .withSocketTimeout(30);
        // This configuration will override any change notifications for sections that do not have a version of 16.0.0, with
        // content from a sections v16.0.0 request for the given sections ID (GUID) for the given change notification.
        EthosChangeNotificationService ethosChangeNotificationService = new EthosChangeNotificationService.Builder(ethosClientBuilder)
                                                                        .withResourceAbbreviatedVersionOverride("sections", "v16.0.0")
                                                                        .build();
        try {
            List<ChangeNotification> changeNotificationList = ethosChangeNotificationService.getChangeNotifications();
            System.out.println( "CHANGE NOTIFICATION LIST LENGTH: " + changeNotificationList.size() );
            for( ChangeNotification cn : changeNotificationList ) {
                System.out.println( cn.toString() );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

}