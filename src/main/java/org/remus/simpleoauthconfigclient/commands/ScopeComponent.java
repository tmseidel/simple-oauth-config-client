package org.remus.simpleoauthconfigclient.commands;

import org.remus.simpleoauthconfigclient.request.Scope;
import org.remus.simpleoauthconfigclient.service.ScopeService;
import org.remus.simpleoauthconfigclient.service.Session;
import org.remus.simpleoauthconfigclient.service.ShellHelper;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@ShellComponent
public class ScopeComponent {

    private ScopeService scopeService;

    private ShellHelper shellHelper;
    private Session session;

    public ScopeComponent(ScopeService scopeService, ShellHelper shellHelper, Session session) {
        this.scopeService = scopeService;
        this.shellHelper = shellHelper;
        this.session = session;
    }

    @ShellMethodAvailability
    public Availability loginAvailablity() {
        return session.isLoggedIn() ? Availability.available() : Availability.unavailable("No open session available, Please login first...");
    }

    @ShellMethod(value = "Lists all scopes", key = "list-scope")
    public String listScopes() {
        List<Scope> allScopes = scopeService.findAllScopes();
        return renderScopes(allScopes);
    }

    private String renderScopes(List<Scope> allScopes) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "Id");
        headers.put("name", "Name");
        headers.put("description", "Description");
        TableModel model = new BeanListTableModel<>(allScopes, headers);

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        return (tableBuilder.build().render(80));
    }

    @ShellMethod(value = "Creates a new scope", key = "create-scope")
    public String addScope(@NotBlank() String name, @ShellOption(defaultValue = "") String description) {
        Scope scope = scopeService.createScope(name, description);
        return renderScopes(Collections.singletonList(scope));
    }

    @ShellMethod(value = "Deletes a scope by the given id", key = "delete-scope")
    public void deleteScope(@Min(2) int id) {
        scopeService.deleteScope(id);
        shellHelper.printSuccess("SUCCESS\n");
    }

    @ShellMethod(value = "Creates a new scope", key = "edit-scope")
    public String editScope(@Min(2) int id,  @ShellOption(defaultValue = "") String name, @ShellOption(defaultValue = "") String description) {
        Scope scope = scopeService.editScope(id, name, description);
        return renderScopes(Collections.singletonList(scope));
    }




}
