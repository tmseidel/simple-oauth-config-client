package org.remus.simpleoauthconfigclient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.remus.simpleoauthconfigclient.request.Organization;
import org.remus.simpleoauthconfigclient.request.Scope;
import org.remus.simpleoauthconfigclient.request.User;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final String REST_URL = "/auth/admin/data/users";

    private RestService restTemplate;

    public UserService(RestService restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<User> findAll() {
        TypeReference<List<User>> typeReference = new TypeReference<>() {
        };
        List<User> result = restTemplate.halListing(REST_URL, "$._embedded.users", typeReference);
        TypeReference<List<Organization>> typeReferenceOrg = new TypeReference<>() {
        };
        for (User user : result) {
            try {
                Organization organization = restTemplate.halListing(REST_URL + "/" + user.getId(), "$", "organization", Organization.class);
                user.setOrganization(String.valueOf(organization.getId()));
            } catch (HttpClientErrorException.NotFound e){
                //skip
            }
            try {
                TypeReference<List<Scope>> scopeTypeReference = new TypeReference<>() {};
                List<Scope> scopeList = restTemplate.halListing(REST_URL + "/" + user.getId(), "$._embedded.scopes", "scopeList",scopeTypeReference);
                user.setScopes(scopeList.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toSet()));
            } catch (HttpClientErrorException.NotFound e) {
                // skip
            }


        }
        return result;
    }

    public User create(String email, String name, String password) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setActivated(true);
        user.setLastLogin(new Date());
        user.setPassword(password);
        return restTemplate.exchange(REST_URL, User.class, HttpMethod.POST, Optional.of(user));
    }

    public User edit(int id, String name, String ipRestriction) {
        User user = new User();
        user.setId(id);
        user.setName(StringUtils.trimToNull(name));
        return restTemplate.exchange(REST_URL + "/" + id, User.class, HttpMethod.PATCH, Optional.of(user));
    }

    public void delete(int id) {
        restTemplate.exchange(REST_URL + "/" + id, Void.class, HttpMethod.DELETE, Optional.empty());
    }

}
