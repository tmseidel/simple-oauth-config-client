package org.remus.simpleoauthconfigclient.commands;

import org.remus.simpleoauthconfigclient.request.Application;
import org.remus.simpleoauthconfigclient.service.ApplicationService;
import org.remus.simpleoauthconfigclient.service.ScopeService;
import org.remus.simpleoauthconfigclient.service.Session;
import org.remus.simpleoauthconfigclient.service.ShellHelper;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
public class ApplicationComponent {

    private ApplicationService applicationService;
    private ScopeService scopeService;
    private ShellHelper shellHelper;
    private Session session;

    public ApplicationComponent(ApplicationService applicationService, ScopeService scopeService, ShellHelper shellHelper, Session session) {
        this.applicationService = applicationService;
        this.scopeService = scopeService;
        this.shellHelper = shellHelper;
        this.session = session;
    }

    @ShellMethodAvailability
    public Availability loginAvailablity() {
        return session.isLoggedIn() ? Availability.available() : Availability.unavailable("No open session available, Please login first...");
    }

    @ShellMethod(value = "Lists all application", key = "list-application")
    public String list() {
        List<Application> applicationList = applicationService.findAll();
        return render(applicationList);
    }

    @ShellMethod(value = "Shows a single application", key = "detail-application")
    public String showSingle(@Min(1)int id) {
        Application application = applicationService.getById(id);
        return renderSingle(application);
    }

    private String render(List<Application> allApplication) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "Id");
        headers.put("name", "Name");
        headers.put("loginUrls", "Login-Urls");
        headers.put("logoutUrl", "Logout-Url");
        headers.put("css", "CSS");
        headers.put("activated", "Activated");
        headers.put("trustworthy", "Trustworthy");
        headers.put("scopeListString", "Scopes");
        TableModel model = new BeanListTableModel<>(allApplication, headers);

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        return (tableBuilder.build().render(300));
    }

    private String renderSingle(Application application) {
        Object[][] data = new Object[][] {
                {"Id", application.getId()},
                {"Name", application.getName()},
                {"Client-Id", application.getClientId()},
                {"Client-Secret", application.getClientSecret()},
                {"Login-Urls",application.getLoginUrls()},
                {"Logout-Url",application.getLogoutUrl()},
                {"CSS",application.getCss()},
                {"Activated",application.isActivated()},
                {"Trustworthy",application.isTrustworthy()},
                {"Scopes",application.getScopeListString()},

        };
        TableModel model = new ArrayTableModel(data);

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        return (tableBuilder.build().render(300));
    }

    @ShellMethod(value = "Creates a new application", key = "create-application")
    public String add(
            @NotBlank() String name,
            @ShellOption(defaultValue = "") String clientId,
            @ShellOption(defaultValue = "") String clientSecret,
            @NotBlank() String loginUrls,
            @ShellOption(defaultValue = "REGULAR") String applicationType,
            @ShellOption(defaultValue = "") String logoutUrl,
            @ShellOption(defaultValue = "") String css,
            boolean activated,
            boolean trustworthy) {

        Application application = applicationService.create(name, clientId, clientSecret, Arrays.stream(loginUrls.split(",")).collect(Collectors.toSet()), logoutUrl, css, activated, trustworthy, applicationType);
        return render(Collections.singletonList(application));
    }

    @ShellMethod(value = "Deletes an application by the given id", key = "delete-application")
    public void delete(@Min(1) int id) {
        applicationService.delete(id);
        shellHelper.printSuccess("SUCCESS\n");
    }

    @ShellMethod(value = "Edits an existing application", key = "edit-application")
    public String edit(
            @Min(1) int id,
            @ShellOption(defaultValue = "") String name,
            @ShellOption(defaultValue = "") String clientId,
            @ShellOption(defaultValue = "") String clientSecret,
            @ShellOption(defaultValue = "") String loginUrls,
            @ShellOption(defaultValue = "") String applicationType,
            @ShellOption(defaultValue = "") String logoutUrl,
            @ShellOption(defaultValue = "") String css,
            boolean activated,
            boolean trustworthy) {

        Application application = applicationService.edit(id, name, clientId, clientSecret, Arrays.stream(loginUrls.split(",")).collect(Collectors.toSet()), logoutUrl, css, activated, trustworthy, applicationType);
        return render(Collections.singletonList(application));
    }

    @ShellMethod(value = "Assigns multiple scopes to an application", key = "assign-scopes-to-application")
    public void assignScopesToApplication(String scopeId, @Min(1) int applicationId) {
        Integer[] integers = Arrays.stream(scopeId.split(",")).map(e -> Integer.parseInt(e.trim())).toArray(Integer[]::new);
        scopeService.checkIds(integers);
        applicationService.assignScopesToApplication(integers,applicationId);
        shellHelper.printSuccess("SUCCESS\n");
    }


}
