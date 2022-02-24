package com.ellucian.ethos.integration.sample.commandline;

import com.ellucian.ethos.integration.authentication.AccessToken;
import com.ellucian.ethos.integration.client.EthosClient;

import java.io.IOException;

/**
 * Accepts an API key argument and illustrates how to get an access token to make calls to
 * Ethos Integration.
 * @since 0.0.1
 */
public class GetAccessTokenExample {

    /**
     * An example of how to get an access token and view the properties.
     * @param apiKey The API key used to get an auth access token.
     * @throws IOException Propagated out if thrown from the EthosClient.
     */
    public static void getToken( String apiKey ) throws IOException {
        // create an EthosClient using your API key
        EthosClient client = new EthosClient(apiKey, null, null, null);
        System.out.printf("Using API Key '%s'\n", apiKey);
        System.out.printf("The token provider's AWS region is '%s'\n", client.getRegion());
        System.out.printf("The token provider's auto-refresh value is '%s'\n", client.getAutoRefresh());
        System.out.printf("The token provider's configured duration for a token to be valid is %s minutes\n", client.getExpirationMinutes());

        // get a new access token and check the properties
        AccessToken token = client.getAccessToken();
        System.out.printf("The token's valid value is '%b' and it expires at %s\n", token.isValid(), token.getExpirationTime().toLocalTime().toString());
    }

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
        getToken(apiKey);
    }

}
