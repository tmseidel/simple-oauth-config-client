package org.remus.simpleoauthconfigclient.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Application {
    public static class ApplicationHalContainer {
        @JsonProperty("_embedded")
        private ApplicationHalResource resource;

        public ApplicationHalResource getResource() {
            return resource;
        }

        public void setResource(ApplicationHalResource resource) {
            this.resource = resource;
        }
    }

    public static class ApplicationHalResource {
        private List<Application> applications;

        public List<Application> getApplications() {
            return applications;
        }

        public void setApplications(List<Application> applications) {
            this.applications = applications;
        }
    }

    private int id;

    private String name;

    private String clientId;

    private String clientSecret;

    private Set<String> loginUrls;

    private String logoutUrl;

    private String css;

    private boolean activated;

    private boolean trustworthy;

    private String applicationType;

    private Set<Scope> scopeList;

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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Set<String> getLoginUrls() {
        return loginUrls;
    }

    public void setLoginUrls(Set<String> loginUrls) {
        this.loginUrls = loginUrls;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isTrustworthy() {
        return trustworthy;
    }

    public void setTrustworthy(boolean trustworthy) {
        this.trustworthy = trustworthy;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public Set<Scope> getScopeList() {
        return scopeList;
    }

    public void setScopeList(Set<Scope> scopeList) {
        this.scopeList = scopeList;
    }

    public String getScopeListString() {
        if (scopeList != null) {
            return scopeList.stream().map(e -> e.getName() + "(id:" + e.getId() + ")").collect(Collectors.joining(",\n", "[", "]"));
        }
        return null;
    }
}
