package org.remus.simpleoauthconfigclient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.remus.simpleoauthconfigclient.request.Organization;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    private static final String REST_URL = "/auth/admin/data/organizations";

    private RestService restTemplate;

    public OrganizationService(RestService restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Organization> findAll() {
        TypeReference<List<Organization>> typeReference = new TypeReference<>() {
        };
        List<Organization> result = restTemplate.halListing(REST_URL, "$._embedded.organizations", typeReference);
        return result;
    }

    public Organization create(String name, String ipRestriction) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setIpRestriction(StringUtils.trimToNull(ipRestriction));
        return restTemplate.exchange(REST_URL, Organization.class, HttpMethod.POST, Optional.of(organization));
    }

    public Organization edit(int id, String name, String ipRestriction) {
        Organization organization = new Organization();
        organization.setId(id);
        organization.setName(StringUtils.trimToNull(name));
        organization.setIpRestriction(StringUtils.trimToNull(ipRestriction));
        return restTemplate.exchange(REST_URL + "/" + id, Organization.class, HttpMethod.PATCH, Optional.of(organization));
    }

    public void delete(int id) {
        restTemplate.exchange(REST_URL + "/" + id, Void.class, HttpMethod.DELETE, Optional.empty());
    }

}
