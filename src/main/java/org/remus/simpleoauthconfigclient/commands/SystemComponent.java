package org.remus.simpleoauthconfigclient.commands;

import org.remus.simpleoauthconfigclient.response.FirstRunResponse;
import org.remus.simpleoauthconfigclient.response.LoginResponse;
import org.remus.simpleoauthconfigclient.service.LoginService;
import org.remus.simpleoauthconfigclient.service.Session;
import org.remus.simpleoauthconfigclient.service.ShellHelper;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class SystemComponent {

    private LoginService loginService;
    private Session session;
    private ShellHelper shellHelper;

    public SystemComponent(LoginService loginService, Session session, ShellHelper shellHelper) {
        this.loginService = loginService;
        this.session = session;
        this.shellHelper = shellHelper;
    }

    @ShellMethod("Initializes a simple-oauth-server")
    public void initialize(String endpoint) {
        FirstRunResponse initialize = loginService.initialize(endpoint);
        shellHelper.printSuccess("Initialization complete...\n");
        shellHelper.printInfo(String.format(" ClientId: %s\n",initialize.getClientId()));
        shellHelper.printInfo(String.format(" ClientSecret: %s\n",initialize.getClientSecret()));
    }

    @ShellMethod("Downloads the public key for encrypting the JWT token")
    public void downloadPubKey(String file) {
        loginService.savePublicKeyTo(file);
        shellHelper.printSuccess(String.format("Public key saved to %s\n",file));
    }

    public Availability downloadPubKeyAvailability() {
        return session.isLoggedIn() ? Availability.available() : Availability.unavailable("No open session available");
    }



    @ShellMethod("Open connection to a simple-oauth-server")
    public void login(String endpoint, String clientId, String clientSecret) {
        LoginResponse login = loginService.login(endpoint, clientId, clientSecret);
        shellHelper.printSuccess("SUCCESS");
        shellHelper.print(" - Access token is ");
        shellHelper.printInfo(login.getAccessToken());
        shellHelper.print("\n");
    }
}
