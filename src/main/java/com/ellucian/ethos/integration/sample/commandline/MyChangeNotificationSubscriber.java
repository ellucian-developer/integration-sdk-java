/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.sample.commandline;

import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.notification.AbstractEthosChangeNotificationSubscriber;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyChangeNotificationSubscriber extends AbstractEthosChangeNotificationSubscriber {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private final static Log log = LogFactory.getLog(MyChangeNotificationSubscriber.class);

    public MyChangeNotificationSubscriber() {
        super();
    }
    public MyChangeNotificationSubscriber( int numNotifications ) {
        super(numNotifications);
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    @Override
    public void onChangeNotification(ChangeNotification changeNotification) {
        System.out.println( "onChangeNotification on " + Thread.currentThread().getName() + ", CN: " + changeNotification.toString() );
    }

    @Override
    public void onChangeNotificationError(Throwable throwable) {
        System.out.println( "onChangeNotificationError: " + Thread.currentThread().getName() + ", THROWABLE: " + throwable.getMessage() );
        throwable.printStackTrace();
    }
}