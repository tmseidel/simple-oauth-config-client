package org.remus.simpleoauthconfigclient.commands;

import org.remus.simpleoauthconfigclient.request.Organization;
import org.remus.simpleoauthconfigclient.request.User;
import org.remus.simpleoauthconfigclient.service.Session;
import org.remus.simpleoauthconfigclient.service.ShellHelper;
import org.remus.simpleoauthconfigclient.service.UserService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@ShellComponent
public class UserComponent {

    private Session session;

    private UserService userService;

    private ShellHelper shellHelper;

    public UserComponent(Session session, UserService userService, ShellHelper shellHelper) {
        this.session = session;
        this.userService = userService;
        this.shellHelper = shellHelper;
    }

    @ShellMethodAvailability
    public Availability loginAvailablity() {
        return session.isLoggedIn() ? Availability.available() : Availability.unavailable("No open session available, Please login first...");
    }

    @ShellMethod(value = "Lists all users", key = "list-user")
    public String list() {
        List<User> allOrganizations = userService.findAll();
        return render(allOrganizations);
    }

    private String render(List<User> allUsers) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "Id");
        headers.put("name", "Name");
        headers.put("email", "Email");
        headers.put("scopes", "Scopes");
        headers.put("applications", "Applications");
        headers.put("Organization", "organization");
        TableModel model = new BeanListTableModel<>(allUsers, headers);

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        return (tableBuilder.build().render(120));
    }

    @ShellMethod(value = "Creates a new organization", key = "create-user")
    public String add(@NotBlank() String email, @NotBlank() String name, @NotBlank() String password) {
        User user = userService.create(email, name, password);
        return render(Collections.singletonList(user));
    }

}
