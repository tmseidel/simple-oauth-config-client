package org.remus.simpleoauthconfigclient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.remus.simpleoauthconfigclient.request.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScopeService {

    public static final String REST_URL = "/auth/admin/data/scopes";

    private RestService restTemplate;

    public ScopeService(RestService restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Scope> findAll() {
        TypeReference<List<Scope>> typeReference = new TypeReference<>() {
        };
        List<Scope> result = restTemplate.halListing(REST_URL,"$._embedded.scopes",typeReference);
        return result;
    }

    public Scope create(String name, String description) {
        Scope scope = new Scope();
        scope.setName(name);
        scope.setDescription(StringUtils.trimToNull(description));
        return restTemplate.exchange(REST_URL, Scope.class, HttpMethod.POST, Optional.of(scope));
    }

    public Scope edit(int id, String name, String description) {
        Scope scope = new Scope();
        scope.setId(id);
        scope.setName(StringUtils.trimToNull(name));
        scope.setDescription(StringUtils.trimToNull(description));
        return restTemplate.exchange(REST_URL + "/" + id, Scope.class, HttpMethod.PATCH, Optional.of(scope));
    }

    public void delete(int id) {
        restTemplate.exchange(REST_URL + "/" + id, Void.class, HttpMethod.DELETE, Optional.empty());
    }

    public void checkIds(Integer[] scopeId) {
        for (int i : scopeId) {
            restTemplate.exchange(REST_URL + "/" + i, Scope.class, HttpMethod.GET, Optional.empty());
        }
    }
}
