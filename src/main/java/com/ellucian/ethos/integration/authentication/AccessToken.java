package com.ellucian.ethos.integration.authentication;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * An access token that can be used for authentication to make calls to Ethos Integration.
 * To get an Authorization header that can be used to make HTTP requests, use the getAuthHeader() method.  This will
 * return a Map containing a single entry of the Authorization header key/value pair.  That can be used as-is or added
 * to an existing headers map to pass to the EthosClient making the requests.
 * @since 0.0.1
 */
public class AccessToken {

    private String token;

    private LocalDateTime expirationTime;

    /**
     * Creates an instance of an access token that expires at the given time.
     * @param token an encoded JWT string
     * @param expirationTime the time when this token will expire
     */
    public AccessToken(String token, LocalDateTime expirationTime) {
        this.token = token;
        this.expirationTime = expirationTime;
    }

    /**
     * Gets the JWT value for this access token.
     * @return encoded JWT string
     */
    public String getToken() {
        return token;
    }

    /**
     * Gets the time when this access token will expire
     * @return the expiration time
     */
    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    /**
     * Determines if the token is still valid, meaning that is has a JWT string and it is not expired.
     * @return if the token is valid.
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return token != null && !token.isEmpty() && now.isBefore(expirationTime);
    }

    /**
     * Gets an HTTP Authorization header containing the access token.
     * @return Authorization header containing the access token
     */
    public Map<String, String> getAuthHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);
        return header;
    }

}
