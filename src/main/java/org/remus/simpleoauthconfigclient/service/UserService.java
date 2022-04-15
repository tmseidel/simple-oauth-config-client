package org.remus.simpleoauthconfigclient.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.remus.simpleoauthconfigclient.request.User;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final String REST_URL = "/auth/admin/data/users";
    private static final String REST_URL_LISTING = "/auth/admin/data/users?projection=configclient";

    private RestService restTemplate;

    private Session session;

    public UserService(RestService restTemplate, Session session) {
        this.restTemplate = restTemplate;
        this.session = session;
    }

    public List<User> findAll() {
        User.UserHalContainer exchange = restTemplate.exchange(REST_URL_LISTING, User.UserHalContainer.class, HttpMethod.GET, Optional.empty());
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ;
        return exchange.getResource().getUsers();
    }

    public User create(String email, String name, String password, boolean activated) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setActivated(activated);
        user.setPassword(password);
        return restTemplate.exchange(REST_URL, User.class, HttpMethod.POST, Optional.of(user));
    }

    public User edit(int id, String email, String name, String password, boolean activated) {
        User user = new User();
        user.setId(id);
        user.setActivated(activated);
        user.setPassword(StringUtils.trimToNull(password));
        user.setName(StringUtils.trimToNull(name));
        user.setEmail(StringUtils.trimToNull(email));
        return restTemplate.exchange(REST_URL + "/" + id, User.class, HttpMethod.PATCH, Optional.of(user));
    }

    public void delete(int id) {
        restTemplate.exchange(REST_URL + "/" + id, Void.class, HttpMethod.DELETE, Optional.empty());
    }

    public void assignOrganizationToUser(int userId, int organizationId) {
        String uriList = session.getEndpoint() + OrganizationService.REST_URL + "/" + organizationId;
        restTemplate.exchange(REST_URL + "/" + userId + "/organization", String.class, HttpMethod.PUT, Optional.of(uriList));

    }

    public void assignScopesToUser(Integer[] scopeId, int userId) {
        String uriList = Arrays.stream(scopeId).map(e -> session.getEndpoint() + ScopeService.REST_URL + "/" + e).collect(Collectors.joining("\n"));
        restTemplate.exchange(REST_URL + "/" + userId + "/scopeList", String.class, HttpMethod.PUT, Optional.of(uriList));
    }

    public void assignApplicationsToUser(Integer[] applicationId, int userId) {
        String uriList = Arrays.stream(applicationId).map(e -> session.getEndpoint() + ApplicationService.REST_URL + "/" + e).collect(Collectors.joining("\n"));
        restTemplate.exchange(REST_URL + "/" + userId + "/applications", String.class, HttpMethod.PUT, Optional.of(uriList));
    }
}
