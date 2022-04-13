package org.remus.simpleoauthconfigclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RestService {

    private RestTemplate restTemplate;

    private Session session;

    public RestService(RestTemplate restTemplate, Session session) {
        this.restTemplate = restTemplate;
        this.session = session;
    }

    public <T> T exchange(String url, Class<T> result, HttpMethod method, Optional<T> requestObject) {
        HttpHeaders headers = createHttpHeaders();
        if (requestObject.map(String.class::isInstance).orElse(false)) {
            headers.set("Content-Type","text/uri-list");
        } else {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        HttpEntity request = requestObject.map(e -> {
            try {
                String valueAsString;
                if (e instanceof String) {
                    valueAsString = (String) e;
                } else {
                    valueAsString = new ObjectMapper().writeValueAsString(e);
                }
                return new HttpEntity( valueAsString,headers);
            } catch (JsonProcessingException ex) {
                throw new IllegalArgumentException("Error sending request " + url, ex);
            }
        }).orElse(new HttpEntity<String>(headers));

        return restTemplate.exchange(session.getEndpoint() + url, method, request, result).getBody();
    }
    public <T> T halListing(String url, String jsonPath, TypeReference target) {
        return halListing(url,jsonPath,"self",target, null);
    }
    public <T> T halListing(String url, String jsonPath, String follow, Class target) {
        return halListing(url,jsonPath,follow,null, target);
    }
    public <T> T halListing(String url, String jsonPath, String follow,TypeReference target) {
        return halListing(url,jsonPath,follow,target, null);
    }

    private <T> T halListing(String url, String jsonPath, String follow, TypeReference typeReference, Class target) {
        HttpHeaders headers = createHttpHeaders();
        Traverson traverson = new Traverson(URI.create(session.getEndpoint() + url), MediaTypes.HAL_JSON);
        Object returnValue = traverson
                .follow(follow).withHeaders(headers)
                .toObject(jsonPath);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String jsonString = null;
            if (returnValue instanceof JSONArray) {
                jsonString = ((JSONArray) returnValue).toJSONString();
            } else {
                jsonString = objectMapper.writeValueAsString(returnValue);
            }
            if (typeReference != null) {
                return (T) objectMapper.readValue(jsonString, typeReference);
            }
            return (T) objectMapper.readValue(jsonString, target);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Reading the response was not successful",e);
        }

    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
        headers.set("Authorization", "Bearer " + session.getAccessToken());
        return headers;
    }


}
