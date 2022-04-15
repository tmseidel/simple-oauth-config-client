package org.remus.simpleoauthconfigclient.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.remus.simpleoauthconfigclient.request.Application;
import org.remus.simpleoauthconfigclient.request.Organization;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    public static final String REST_URL = "/auth/admin/data/applications";
    private static final String REST_URL_LISTING = "/auth/admin/data/applications?projection=configclient";

    private RestService restTemplate;

    private Session session;

    public ApplicationService(RestService restTemplate, Session session) {
        this.restTemplate = restTemplate;
        this.session = session;
    }

    public List<Application> findAll() {
        Application.ApplicationHalContainer exchange = restTemplate.exchange(REST_URL_LISTING, Application.ApplicationHalContainer.class, HttpMethod.GET, Optional.empty());
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ;
        return exchange.getResource().getApplications();
    }

    public Application create(String name, String clientId, String clientSecret, Set<String> loginUrls, String logoutUrl, String css, boolean activated, boolean trustworthy, String applicationType) {
        Application application = new Application();
        if (StringUtils.isEmpty(clientId)) {
            application.setClientId(RandomStringUtils.random(32, 0, 0, true, true, null, new SecureRandom()));
        } else {
            application.setClientId(StringUtils.trimToNull(clientId));
        }
        if (StringUtils.isEmpty(clientSecret)) {
            application.setClientSecret(RandomStringUtils.random(64, 0, 0, true, true, null, new SecureRandom()));
        } else {
            application.setClientSecret(StringUtils.trimToNull(clientSecret));
        }
        application.setName(StringUtils.trimToNull(name));
        application.setActivated(activated);
        application.setTrustworthy(trustworthy);
        application.setCss(StringUtils.trimToNull(css));
        application.setApplicationType(StringUtils.trimToNull(applicationType));
        application.setLoginUrls(loginUrls);
        application.setLogoutUrl(StringUtils.trimToNull(logoutUrl));
        return restTemplate.exchange(REST_URL, Application.class, HttpMethod.POST, Optional.of(application));
    }

    public Application edit(int id, String name, String clientId, String clientSecret, Set<String> loginUrls, String logoutUrl, String css, boolean activated, boolean trustworthy, String applicationType) {

        Application application = new Application();
        application.setId(id);
        application.setName(StringUtils.trimToNull(name));
        application.setClientId(StringUtils.trimToNull(clientId));
        application.setClientSecret(StringUtils.trimToNull(clientSecret));
        application.setActivated(activated);
        application.setTrustworthy(trustworthy);
        application.setCss(StringUtils.trimToNull(css));
        application.setApplicationType(StringUtils.trimToNull(applicationType));
        application.setLoginUrls(loginUrls);
        application.setLogoutUrl(StringUtils.trimToNull(logoutUrl));
        return restTemplate.exchange(REST_URL + "/" + id, Application.class, HttpMethod.PATCH, Optional.of(application));
    }

    public void delete(int id) {
        restTemplate.exchange(REST_URL + "/" + id, Void.class, HttpMethod.DELETE, Optional.empty());
    }

    public void assignScopesToApplication(Integer[] scopeId, int applicationId) {
        String uriList = Arrays.stream(scopeId).map(e -> session.getEndpoint() + ScopeService.REST_URL + "/" + e).collect(Collectors.joining("\n"));
        restTemplate.exchange(REST_URL + "/" + applicationId + "/scopeList", String.class, HttpMethod.PUT, Optional.of(uriList));
    }

    public void checkIds(Integer[] integers) {
        for (int integer : integers) {
            Application exchange = restTemplate.exchange(REST_URL + "/" + integer, Application.class, HttpMethod.GET, Optional.empty());
        }
    }

    public Application getById(int id) {
        return restTemplate.exchange(REST_URL + "/" + id, Application.class, HttpMethod.GET, Optional.empty());
    }
}
