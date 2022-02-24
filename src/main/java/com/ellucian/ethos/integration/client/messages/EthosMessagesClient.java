package com.ellucian.ethos.integration.client.messages;

import com.ellucian.ethos.integration.EthosIntegrationUrls;
import com.ellucian.ethos.integration.client.EthosClient;
import com.ellucian.ethos.integration.client.EthosResponse;
import com.ellucian.ethos.integration.client.EthosResponseConverter;
import org.apache.http.Header;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An EthosClient used to publish and consume messages using the Ethos Integration messages service.
 *
 * <p>
 * This client accepts an API key that belongs to an ethos application.  All requests made from this client are done
 * on behalf of the application to which that API key belongs.  That means that calling the <code>consume</code> methods
 * will try to retrieve change-notifications from the subscription queue of that specific application.  It is assumed that
 * the application is configured in Ethos Integration to subscribe to some resource changes.
 *
 * <p>
 * Likewise, calling the <code>publish</code> methods will try to publish change-notifications on behalf of the application
 * to which the API key belongs.  It is assumed that the application is configured in Ethos Integration to own the resources
 * that are in the change-notifications being published.  If a change-notification is sent to the Ethos Integration
 * /publish endpoint for a resource that is not owned by the calling application, it will return a <code>403 Forbidden</code>
 * response.
 *
 * <p>
 * The preferred way to instantiate this class is via the {@link com.ellucian.ethos.integration.client.EthosClientBuilder EthosClientBuilder}.
 *
 * @since 0.0.1
 */
public class EthosMessagesClient extends EthosClient {

    /**
     * The version to use for the Ethos Messages API.
     */
    private static final String cnType = "application/vnd.hedtech.change-notifications.v2+json";

    /**
     * Creates an EthosMessagesClient using the given API key.
     * Note that the preferred way to get an instance of this class is through the {@link com.ellucian.ethos.integration.client.EthosClientBuilder EthosClientBuilder}.
     * @param apiKey A valid API key from Ethos Integration.  This is required to be a valid 36 character GUID string.
     *               If it is null, empty, or not in a valid GUID format, then an <code>IllegalArgumentException</code> will be thrown.
     * @param connectionTimeout The timeout <b>in seconds</b> for a connection to be established.
     * @param connectionRequestTimeout The timeout <b>in seconds</b> when requesting a connection from the Apache connection manager.
     * @param socketTimeout The timeout <b>in seconds</b> when waiting for data between consecutive data packets.
     */
    public EthosMessagesClient(String apiKey, Integer connectionTimeout, Integer connectionRequestTimeout, Integer socketTimeout) {
        super(apiKey, connectionTimeout, connectionRequestTimeout, socketTimeout);
    }

    /**
     * Gets a list of messages from the subscription queue of the application.  This will return the default number of messages, if
     * available, from Ethos Integration in the format of a change-notification.
     * <p>
     * By default, each subsequent call to consume messages will remove the previously retrieved messages from the queue.  If you need
     * to get the same messages (or subset of them) again, use the consume method that accepts a <code>lastProcessedID</code> parameter.
     * @return A list of messages in the change-notification format.
     * @throws IOException if there is an error making the HTTP request
     */
    public List<ChangeNotification> consume() throws IOException {
        return getMessages(-1, -1);
    }

    /**
     * Gets a list of messages from the subscription queue of the application.  This will return the number of messages specified
     * by the <code>limit</code> parameter, if there are that many available.  The messages will be returned in the format of a
     * change-notification.
     * <p>
     * The limit can be up to 1000, but the total size of the response payload from Ethos Integration
     * is limited to 1 MB.  If the size limit is reached, the number of messages returned will be less than the specified limit, even if there
     * are more messages remaining in the queue.
     * @param limit The maximum number of messages to retrieve with a single request.  This is required to be an integer between
     *                          1 and 1000, otherwise an <code>IllegalArgumentException</code> will be thrown.
     * @return A list of messages in the change-notification format.  This will return up to the given limit of messages if there are that many
     *      available in the queue and they all fit within the 1 MB payload limit.
     * @throws IOException if there is an error making the HTTP request
     */
    public List<ChangeNotification> consumeWithLimit(int limit) throws IOException {
        if( limit < 1 || limit > 1000 ) {
            throw new IllegalArgumentException("The 'limit' parameter has to be between 1 and 1000.");
        }

        return getMessages(limit, -1);
    }

    /**
     * Gets a list of messages from the subscription queue of the application, starting after the given ID.  This will return the default number of
     * messages, if available, from Ethos Integration in the format of a change-notification.
     * <p>
     * The <code>lastProcessedID</code> parameter can be used to indicate the ID of the last message that was successfully processed.
     * This parameter can be used to retrieve messages that have already been retrieved.  The messages in the queue have sequential ID's, and the
     * lastProcessedID parameter corresponds to the ID of a message in the queue.
     * <p>
     * Here is an example of how <code>lastProcessedID</code> can be used.  If the application consuming the messages retrieves messages 1-10, but
     * only successfully processes messages 1-5, it can set the lastProcessedID parameter to 5 in the next invocation.  That will give the application
     * messages 6-10 again.
     *
     * @param lastProcessedID The ID of the last message that was successfully processed.
     * @return A list of messages in the change-notification format.
     * @throws IOException if there is an error making the HTTP request
     */
    public List<ChangeNotification> consumeFromId(int lastProcessedID) throws IOException {
        return getMessages(-1, lastProcessedID);
    }

    /**
     * Gets a list of messages from the subscription queue of the application.  This will return the number of messages specified
     * by the <code>limit</code> parameter, if there are that many available.  The messages will be returned in the format of a
     * change-notification.
     * <p>
     * The limit can be up to 1000, but the total size of the response payload from Ethos Integration
     * is limited to 1 MB.  If the size limit is reached, the number of messages returned will be less than the specified limit, even if there
     * are more messages remaining in the queue.
     * <p>
     * The <code>lastProcessedID</code> parameter can be used to indicate the ID of the last message that was successfully processed.
     * This parameter can be used to retrieve messages that have already been retrieved.  The messages in the queue have sequential ID's, and the
     * lastProcessedID parameter corresponds to the ID of a message in the queue.
     * <p>
     * Here is an example of how <code>lastProcessedID</code> can be used.  If the application consuming the messages retrieves messages 1-10, but
     * only successfully processes messages 1-5, it can set the lastProcessedID parameter to 5 in the next invocation.  That will give the application
     * messages 6-10 again.
     * <p>
     * When both the <code>limit</code> and <code>lastProcessedID</code> parameters are used together, they do not affect each other.  The lastProcessedID gives
     * a starting point on what message should be the first one returned. The limit is evaluated separately to see how many messages should be returned
     * (starting with lastProcessedID + 1).
     *
     * @param limit The maximum number of messages to retrieve with a single request.  This is required to be an integer between
     *                          1 and 1000, otherwise an <code>IllegalArgumentException</code> will be thrown.
     * @param lastProcessedID The ID of the last message that was successfully processed.
     * @return A list of messages in the change-notification format.  This will return up to the given limit of messages if there are that many
     *      available in the queue and they all fit within the 1 MB payload limit.
     * @throws IOException if there is an error making the HTTP request
     */
    public List<ChangeNotification> consume(int limit, int lastProcessedID) throws IOException {
        if( limit < 1 || limit > 1000 ) {
            throw new IllegalArgumentException("The 'limit' parameter has to be between 1 and 1000.");
        }

        return getMessages(limit, lastProcessedID);
    }

    /**
     * Internal private method.  This is used to make the HTTP request to the consume endpoint to get messages.
     * @param limit the limit query parameter
     * @param lastProcessedID the lastProcessedID query parameter
     * @return a list of change-notifications
     * @throws IOException if there is an HTTP error or a JSON parsing error
     */
    private List<ChangeNotification> getMessages(int limit, int lastProcessedID) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", cnType);

        EthosResponse response = get(EthosIntegrationUrls.consume(getRegion(), lastProcessedID, limit), headers);
        return new EthosResponseConverter().toChangeNotificationList(response);
    }

    /**
     * Gets the number of available messages in the subscription queue of the application.  This will return 0 if
     * the app has an empty queue or if the app has no subscriber queue.
     * @return The number of available messages in the application's queue
     * @throws IOException if there is an error making the HTTP request
     */
    public int getNumAvailableMessages() throws IOException {
        EthosResponse response = head(EthosIntegrationUrls.consume(getRegion(), -1, -1));
        Header remaining =  response.getHeader("x-remaining");
        int numMessages = 0;
        try {
            numMessages = Integer.parseInt(remaining.getValue());
        } catch (Exception ex) {
            //just leave the numMessages as 0
        }
        return numMessages;
    }

}
