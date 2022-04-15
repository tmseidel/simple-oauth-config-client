package org.remus.simpleoauthconfigclient.commands;

import org.remus.simpleoauthconfigclient.request.Organization;
import org.remus.simpleoauthconfigclient.request.Scope;
import org.remus.simpleoauthconfigclient.service.OrganizationService;
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
public class OrganizationComponent {

    private OrganizationService organizationService;
    private ShellHelper shellHelper;
    private Session session;

    public OrganizationComponent(OrganizationService organizationService, ShellHelper shellHelper, Session session) {
        this.organizationService = organizationService;
        this.shellHelper = shellHelper;
        this.session = session;
    }
    @ShellMethodAvailability
    public Availability loginAvailablity() {
        return session.isLoggedIn() ? Availability.available() : Availability.unavailable("No open session available, Please login first...");
    }

    @ShellMethod(value = "Lists all organizations", key = "list-organization")
    public String list() {
        List<Organization> allOrganizations = organizationService.findAll();
        return render(allOrganizations);
    }

    private String render(List<Organization> allOrganizations) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "Id");
        headers.put("name", "Name");
        headers.put("ipRestriction", "IP-Restriction");
        TableModel model = new BeanListTableModel<>(allOrganizations, headers);

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        return (tableBuilder.build().render(120));
    }

    @ShellMethod(value = "Creates a new organization", key = "create-organization")
    public String add(@NotBlank() String name, @ShellOption(defaultValue = "") String ipRestriction) {
        Organization organization = organizationService.create(name, ipRestriction);
        return render(Collections.singletonList(organization));
    }

    @ShellMethod(value = "Deletes an organization by the given id", key = "delete-organization")
    public void delete(@Min(1) int id) {
        organizationService.delete(id);
        shellHelper.printSuccess("SUCCESS\n");
    }

    @ShellMethod(value = "Edits an existing organization", key = "edit-organization")
    public String edit(@Min(1) int id,  @ShellOption(defaultValue = "") String name, @ShellOption(defaultValue = "") String ipRestriction) {
        Organization organization = organizationService.edit(id, name, ipRestriction);
        return render(Collections.singletonList(organization));
    }


}
