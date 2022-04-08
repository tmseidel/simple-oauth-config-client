package org.remus.simpleoauthconfigclient.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
public class Session {

    private String endpoint;
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private String refreshToken;

    public void start(String endpoint, String clientId, String clientSecret) {
        this.endpoint = endpoint;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public void logout() {
        this.clientId = null;
        this.clientSecret = null;
        this.endpoint = null;
    }

    public boolean isLoggedIn() {
        return clientId != null;
    }


    public String getEndpoint() {
        return this.endpoint;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }
}
