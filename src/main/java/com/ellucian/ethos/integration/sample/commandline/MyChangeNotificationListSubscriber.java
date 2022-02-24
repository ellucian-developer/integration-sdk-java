/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.sample.commandline;

import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.notification.AbstractEthosChangeNotificationListSubscriber;

import java.util.List;

public class MyChangeNotificationListSubscriber extends AbstractEthosChangeNotificationListSubscriber  {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    public MyChangeNotificationListSubscriber() {
        super();
    }

    public MyChangeNotificationListSubscriber( int numNotifications ) {
        super( numNotifications );
    }
    // ==========================================================================
    // Methods
    // ==========================================================================

    @Override
    public void onChangeNotificationList(List<ChangeNotification> changeNotificationList) {
        System.out.println( "onChangeNotificationList ON " + Thread.currentThread().getName() + ", LIST SIZE: " + changeNotificationList.size() );
        System.out.println( "onChangeNotificationList ON " +Thread.currentThread().getName() + ", " + changeNotificationList.toString() );
    }

    @Override
    public void onChangeNotificationListError(Throwable throwable) {
        System.out.println( "HANDLING CHANGE NOTIFICATION LIST ERROR ON " + Thread.currentThread().getName()+ ", " + throwable.getMessage() );
        throwable.printStackTrace();
    }
}