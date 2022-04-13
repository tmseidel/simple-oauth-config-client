package org.remus.simpleoauthconfigclient.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    public static class UserHalContainer {
        @JsonProperty("_embedded")
        private UserHalResource resource;

        public UserHalResource getResource() {
            return resource;
        }

        public void setResource(UserHalResource resource) {
            this.resource = resource;
        }
    }

    public static class UserHalResource {
        private List<User> users;

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }
    }

    private int id;

    private String name;

    private Set<Scope> scopeList;

    private List<String> applications;

    private Organization organization;

    private boolean activated;

    private String password;

    private Date lastLogin;

    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Scope> getScopeList() {
        return scopeList;
    }

    public void setScopeList(Set<Scope> scopeList) {
        this.scopeList = scopeList;
    }

    public List<String> getApplications() {
        return applications;
    }

    public void setApplications(List<String> applications) {
        this.applications = applications;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganizationString() {
        if (this.organization != null) {
            return this.organization.getName() + "(id:" + this.organization.getId()+")";
        }
        return null;
    }

    public String getScopeListString() {
        if (scopeList != null) {
            return scopeList.stream().map(e -> e.getName() + "(id:" + e.getId()+")").collect(Collectors.joining(",","[","]"));
        }
        return null;
    }
}
