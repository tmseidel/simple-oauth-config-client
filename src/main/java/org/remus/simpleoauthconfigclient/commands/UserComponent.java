package org.remus.simpleoauthconfigclient.commands;

import org.remus.simpleoauthconfigclient.request.User;
import org.remus.simpleoauthconfigclient.service.OrganizationService;
import org.remus.simpleoauthconfigclient.service.ScopeService;
import org.remus.simpleoauthconfigclient.service.Session;
import org.remus.simpleoauthconfigclient.service.ShellHelper;
import org.remus.simpleoauthconfigclient.service.UserService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
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

@ShellComponent
public class UserComponent {

    private Session session;

    private UserService userService;

    private OrganizationService organizationService;

    private ScopeService scopeService;

    private ShellHelper shellHelper;

    public UserComponent(Session session, UserService userService, OrganizationService organizationService, ScopeService scopeService, ShellHelper shellHelper) {
        this.session = session;
        this.userService = userService;
        this.organizationService = organizationService;
        this.scopeService = scopeService;
        this.shellHelper = shellHelper;
    }

    @ShellMethodAvailability
    public Availability loginAvailablity() {
        return session.isLoggedIn() ? Availability.available() : Availability.unavailable("No open session available, Please login first...");
    }

    @ShellMethod(value = "Lists all users", key = "list-user")
    public String list() {
        List<User> allUsers = userService.findAll();
        return render(allUsers);
    }

    private String render(List<User> allUsers) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "Id");
        headers.put("name", "Name");
        headers.put("email", "Email");
        headers.put("scopeListString", "Scopes");
        headers.put("applications", "Applications");
        headers.put("organizationString", "Organization");
        TableModel model = new BeanListTableModel<>(allUsers, headers);

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        return (tableBuilder.build().render(120));
    }

    @ShellMethod(value = "Creates a new user", key = "create-user")
    public String add(@NotBlank() String email, @NotBlank() String name, @NotBlank() String password, boolean activated) {
        User user = userService.create(email, name, password,activated);
        return render(Collections.singletonList(user));
    }

    @ShellMethod(value = "Edits an existing user", key = "edit-user")
    public String edit(@NotBlank() int id, @NotBlank() String email, @NotBlank() String name, @NotBlank() String password, boolean activated) {
        User user = userService.edit(id, email, name, password,activated);
        return render(Collections.singletonList(user));
    }

    @ShellMethod(value = "Deletes an user by the given id", key = "delete-user")
    public void delete(@Min(1) int id) {
        userService.delete(id);
        shellHelper.printSuccess("SUCCESS\n");
    }

    @ShellMethod(value = "Assigns an organization to a user", key = "assign-organization-to-user")
    public void assignOrganizationToUser(@Min(1) int organizationId, @Min(1) int userId) {
        organizationService.checkId(organizationId);
        userService.assignOrganizationToUser(userId,organizationId);
        shellHelper.printSuccess("SUCCESS\n");
    }

    @ShellMethod(value = "Assigns multiple scopes to a user", key = "assign-scopes-to-user")
    public void assignOrganizationToUser(String scopeId, @Min(1) int userId) {
        Integer[] integers = Arrays.stream(scopeId.split(",")).map(e -> Integer.parseInt(e.trim())).toArray(Integer[]::new);
        scopeService.checkIds(integers);
        userService.assignScopesToUser(integers,userId);
        shellHelper.printSuccess("SUCCESS\n");
    }

}
