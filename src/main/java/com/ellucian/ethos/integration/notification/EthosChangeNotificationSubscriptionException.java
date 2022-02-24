/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;


/**
 * A RuntimeException that can be throws during subscription processing for ChangeNotifications.
 * @since 0.2.0
 * @author David Kumar
 */
public class EthosChangeNotificationSubscriptionException extends RuntimeException {

    // ==========================================================================
    // Attributes
    // ==========================================================================

    /**
     * Constructs this exception with the given error message.
     * @param message The error message pertinent to ChangeNotification processing.
     */
    public EthosChangeNotificationSubscriptionException( String message ) {
        super( message );
    }

    /**
     * Constructs this exception with the given error message and Throwable cause.
     * @param message The error message pertinent to ChangeNotification processing.
     * @param throwable The cause of this exception.
     */
    public EthosChangeNotificationSubscriptionException( String message, Throwable throwable ) {
        super( message, throwable );
    }
    // ==========================================================================
    // Methods
    // ==========================================================================

}