package org.remus.simpleoauthconfigclient.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.remus.simpleoauthconfigclient.request.FirstRunRequest;
import org.remus.simpleoauthconfigclient.response.FirstRunResponse;
import org.remus.simpleoauthconfigclient.response.LoginResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;


@Service
public class LoginService {

    public static final String URL_PUBLIC_KEY = "/auth/firstStart/pub";
    private RestTemplate restTemplate;
    private Session session;

    public LoginService(RestTemplate restTemplate, Session session) {
        this.restTemplate = restTemplate;
        this.session = session;
    }

    private static final String FIRST_START_URL = "auth/firstStart/run";


    public LoginResponse login(String url, String clientId, String clientSecret) {
        if (!StringUtils.isEmpty(clientId) && !StringUtils.isEmpty(clientSecret)) {
            String tokenRequestUrl = "/auth/oauth/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientId, clientSecret);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("grant_type", "client_credentials");
            map.add("scope", "data.superadmin");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(url + tokenRequestUrl, request, LoginResponse.class);
            session.start(url, clientId,clientSecret);
            session.setAccessToken(response.getBody().getAccessToken());
            session.setRefreshToken(response.getBody().getRefreshToken());

            return response.getBody();
        }
        throw new IllegalArgumentException("ClientId and Client-Secret must not be empty.");
    }

    public FirstRunResponse initialize(String endpoint) {
        String url = endpoint + "/" + FIRST_START_URL;
        FirstRunRequest request = new FirstRunRequest();
        request.setInitialAuthToken("4xjKQ8537XRBeF26IH9WB1OC0CAoJsfQ");

        ResponseEntity<FirstRunResponse> response = restTemplate.postForEntity(url, request, FirstRunResponse.class);
        session.start(endpoint, response.getBody().getClientId(), response.getBody().getClientSecret());
        return response.getBody();
    }

    public void savePublicKeyTo(String file) {
        String url = session.getEndpoint() + URL_PUBLIC_KEY;
        try {
            FileUtils.copyURLToFile(
                    new URL(url),
                    new File(file));
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid download-url", e);
        }
    }

}
