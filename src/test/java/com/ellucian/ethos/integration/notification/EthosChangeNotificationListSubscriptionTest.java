/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.notification;

import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.client.messages.ChangeNotificationFactory;
import com.ellucian.ethos.integration.service.EthosChangeNotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class EthosChangeNotificationListSubscriptionTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    @Mock
    private EthosChangeNotificationListPollService mockEthosChangeNotificationListPublisher;

    @Mock
    private Flow.Subscriber mockSubscriber;

    @Mock
    private EthosChangeNotificationService mockEthosChangeNotificationService;

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Test
    public void subscriptionThrowsExceptionWithNullConstructorParams() {
        assertThrows(IllegalArgumentException.class, () -> {
            String apiKey = "11111111-1111-1111-1111-111111111111";
            new EthosChangeNotificationListSubscription( null, mockSubscriber, mockEthosChangeNotificationService, 10, TimeUnit.SECONDS );
            new EthosChangeNotificationListSubscription(mockEthosChangeNotificationListPublisher, null, mockEthosChangeNotificationService, 10, TimeUnit.SECONDS );
            new EthosChangeNotificationListSubscription(mockEthosChangeNotificationListPublisher, mockSubscriber, null, 10, TimeUnit.SECONDS );
        });
    }

    @Test
    public void requestProcessesNotificationsWithNumNotificationsTest() throws Exception {
        List<ChangeNotification> cnList = buildChangeNotificationsList();
        int numNotifications = 3;
        // Mock what the change notification service returns.
        doReturn(cnList).when(mockEthosChangeNotificationService).getChangeNotifications( numNotifications );
        EthosChangeNotificationListSubscription listSubscription = buildListSubscription();
        // Execute the test, this starts the polling thread.
        listSubscription.request( numNotifications );
        // Sleep on main thread for 3 seconds to let the polling thread run.
        Thread.sleep( 3000 );
        // Cancel the polling thread.
        listSubscription.cancel();
        Thread.sleep( 2000 );
        // Examine the results.
        Mockito.verify(mockEthosChangeNotificationService, Mockito.never()).getChangeNotifications();
        Mockito.verify(mockSubscriber, Mockito.atLeastOnce()).onNext(any(List.class));
        Mockito.verify(mockSubscriber, Mockito.atLeastOnce()).onComplete();
        Mockito.verify(mockEthosChangeNotificationListPublisher, Mockito.times(1)).unsubscribe(mockSubscriber);
    }

    @Test
    public void requestDoesNotProcessNotificationsWithNumNotificationsTest() throws Exception {
        List<ChangeNotification> cnList = new ArrayList<>();
        int numNotifications = 3;
        // Mock what the change notification service returns.
        doReturn(cnList).when(mockEthosChangeNotificationService).getChangeNotifications( numNotifications );
        EthosChangeNotificationListSubscription listSubscription = buildListSubscription();
        // Execute the test, this starts the polling thread.
        listSubscription.request( numNotifications );
        // Sleep on main thread for 3 seconds to let the polling thread run.
        Thread.sleep( 3000 );
        // Cancel the polling thread.
        listSubscription.cancel();
        // Sleep for 2 seconds to give the cancellation process a chance to run.
        Thread.sleep( 2000 );
        // Examine the results.
        Mockito.verify(mockEthosChangeNotificationService, Mockito.never()).getChangeNotifications();
        Mockito.verify(mockSubscriber, Mockito.never()).onNext(any(List.class));
        Mockito.verify(mockSubscriber, Mockito.atLeastOnce()).onComplete();
        Mockito.verify(mockEthosChangeNotificationListPublisher, Mockito.times(1)).unsubscribe(mockSubscriber);
    }

    @Test
    public void requestProcessesNotificationsWithDefaultNumNotificationsTest() throws Exception {
        List<ChangeNotification> cnList = buildChangeNotificationsList();
        // A negative value should make it use the default number of messages retrieved in the EthosChangeNotificationService.
        int numNotifications = -1;
        // Mock what the change notification service returns.
        doReturn(cnList).when(mockEthosChangeNotificationService).getChangeNotifications();
        EthosChangeNotificationListSubscription listSubscription = buildListSubscription();
        // Execute the test, this starts the polling thread.
        listSubscription.request( numNotifications );
        // Sleep on main thread for 3 seconds to let the polling thread run.
        Thread.sleep( 3000 );
        // Cancel the polling thread.
        listSubscription.cancel();
        Thread.sleep( 2000 );
        // Examine the results.
        Mockito.verify(mockEthosChangeNotificationService, Mockito.never()).getChangeNotifications(any(Integer.class));
        Mockito.verify(mockSubscriber, Mockito.atLeastOnce()).onNext(any(List.class));
        Mockito.verify(mockSubscriber, Mockito.atLeastOnce()).onComplete();
        Mockito.verify(mockEthosChangeNotificationListPublisher, Mockito.times(1)).unsubscribe(mockSubscriber);
    }

    @Test
    public void requestDoesNotProcessNotificationsWithDefaultNumNotificationsTest() throws Exception {
        List<ChangeNotification> cnList = new ArrayList<>();
        // A negative value should make it use the default number of messages retrieved in the EthosChangeNotificationService.
        int numNotifications = -1;
        // Mock what the change notification service returns.
        doReturn(cnList).when(mockEthosChangeNotificationService).getChangeNotifications();
        EthosChangeNotificationListSubscription listSubscription = buildListSubscription();
        // Execute the test, this starts the polling thread.
        listSubscription.request( numNotifications );
        // Sleep on main thread for 3 seconds to let the polling thread run.
        Thread.sleep( 3000 );
        // Cancel the polling thread.
        listSubscription.cancel();
        Thread.sleep( 2000 );
        // Examine the results.
        Mockito.verify(mockEthosChangeNotificationService, Mockito.never()).getChangeNotifications(any(Integer.class));
        Mockito.verify(mockSubscriber, Mockito.never()).onNext(any(List.class));
        Mockito.verify(mockSubscriber, Mockito.atLeastOnce()).onComplete();
        Mockito.verify(mockEthosChangeNotificationListPublisher, Mockito.times(1)).unsubscribe(mockSubscriber);
    }

    /**
     * Builds the class under test.
     * @return
     */
    private EthosChangeNotificationListSubscription buildListSubscription() {
        int pollingInterval = 2;
        return Mockito.spy( new EthosChangeNotificationListSubscription(mockEthosChangeNotificationListPublisher,
                mockSubscriber,
                mockEthosChangeNotificationService,
                pollingInterval,
                TimeUnit.SECONDS) );
    }

    private List<ChangeNotification> buildChangeNotificationsList() {
        List<ChangeNotification> cnList = new ArrayList<>();
        try {
            ChangeNotification cn = ChangeNotificationFactory.createCNFromJson(getChangeNotification());
            cnList.add(cn);
            cn = ChangeNotificationFactory.createCNFromJson(getChangeNotification());
            cnList.add(cn);
            cn = ChangeNotificationFactory.createCNFromJson(getChangeNotification());
            cnList.add(cn);
        }
        catch( JsonProcessingException jpe ) {
            jpe.printStackTrace();
        }
        return cnList;
    }

    private String getChangeNotification() {
        return "{" +
                "    \"id\": 1," +
                "    \"published\": \"2020-10-30 12:00:00.000Z\"," +
                "    \"publisher\": {" +
                "        \"applicationName\": \"app1\"," +
                "        \"id\": \"" + UUID.randomUUID().toString() + "\"," +
                "        \"tenant\": {" +
                "            \"id\": \"" + UUID.randomUUID().toString() + "\"," +
                "            \"alias\": \"ellucian\"," +
                "            \"name\": \"university\"," +
                "            \"environment\": \"test\"" +
                "        }" +
                "    }," +
                "    \"resource\": {" +
                "        \"name\": \"persons\"," +
                "        \"id\": \"" + UUID.randomUUID().toString() + "\"," +
                "        \"version\": \"application/vnd.hedtech.integration.v12+json\"," +
                "        \"domain\": \"core\"" +
                "    }," +
                "    \"operation\": \"created\"," +
                "    \"contentType\": \"resource-representation\"," +
                "    \"content\": {" +
                "        \"id\": \"" + UUID.randomUUID().toString() + "\"" +
                "    }" +
                "}";
    }

}