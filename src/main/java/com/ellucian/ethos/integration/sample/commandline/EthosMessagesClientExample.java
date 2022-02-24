package com.ellucian.ethos.integration.sample.commandline;

import com.ellucian.ethos.integration.client.EthosClientBuilder;
import com.ellucian.ethos.integration.client.messages.ChangeNotification;
import com.ellucian.ethos.integration.client.messages.EthosMessagesClient;

import java.io.IOException;
import java.util.List;

public class EthosMessagesClientExample {

    /**
     * Run this sample program.  This has a required program argument of an API key.
     * @param args The string arguments passed into this main method class.
     * @throws Exception Propagates any exception thrown.
     */
    public static void main (String[] args) throws Exception {
        if( args == null || args.length == 0 ) {
            System.out.println( "Please enter an API key as a program argument to run this sample program." );
            return;
        }
        String apiKey = args[ 0 ];

        checkAvailableMessages(apiKey);
        consumeMessages(apiKey);
    }

    public static void checkAvailableMessages(String apiKey) throws IOException {
        EthosMessagesClient client = new EthosClientBuilder(apiKey).buildEthosMessagesClient();
        int numMessages = client.getNumAvailableMessages();
        System.out.printf("Number of available messages: %s\n", numMessages);
    }

    public static void consumeMessages(String apiKey) throws IOException {
        EthosMessagesClient client = new EthosClientBuilder(apiKey).buildEthosMessagesClient();
        List<ChangeNotification> cnList = client.consume();
        System.out.printf("Retrieved '%d' messages.\n", cnList.size());
        System.out.println("Requesting the same set of messages again, using 'lastProcessedID=0'.");
        cnList = client.consumeFromId(0);
        System.out.printf("Retrieved '%d' messages.\n", cnList.size());
    }
}
