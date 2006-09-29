package net.sf.click.examples.page.cayenne;

import java.util.List;

import net.sf.click.control.ActionLink;
import net.sf.click.control.Column;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Submit;
import net.sf.click.control.Table;
import net.sf.click.control.TextField;
import net.sf.click.examples.domain.Client;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.cayenne.CayenneForm;
import net.sf.click.extras.cayenne.QuerySelect;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;
import net.sf.click.extras.control.LinkDecorator;

/**
 * Provides a CayenneForm and QuerySelect control demonstration.
 *
 * @author Malcolm Edgar
 */
public class CayenneFormPage extends BorderPage {

    protected static final int MAX_TABLE_SIZE = 5;

    public CayenneForm form = new CayenneForm();
    public Table table = new Table();
    public ActionLink editLink = new ActionLink("edit", this, "onEditClick");
    public ActionLink removeLink = new ActionLink("remove", this, "onRemoveClick");
    public String msg = null;

    public CayenneFormPage() {
        form.setDataObjectClass(Client.class);
        form.setErrorsPosition(Form.POSITION_TOP);

        FieldSet clientFieldSet = new FieldSet("Client");
        form.add(clientFieldSet);

        QuerySelect querySelect = new QuerySelect("title", true);
        querySelect.setQueryValueLabel("titles", "value", "label");
        clientFieldSet.add(querySelect);

        clientFieldSet.add(new TextField("firstName"));
        clientFieldSet.add(new TextField("lastName"));
        clientFieldSet.add(new DateField("dateJoined"));
        clientFieldSet.add(new EmailField("email"));

        FieldSet addressFieldSet = new FieldSet("Address");
        form.add(addressFieldSet);

        addressFieldSet.add(new TextField("address.line1", "Line One"));
        addressFieldSet.add(new TextField("address.line2", "Line Two"));
        addressFieldSet.add(new TextField("address.suburb", "Suburb"));

        querySelect = new QuerySelect("address.state", "State", true);
        querySelect.setQueryValueLabel("states", "value", "label");
        addressFieldSet.add(querySelect);

        IntegerField postCodeField = new IntegerField("address.postCode", "Post Code");
        postCodeField.setMaxLength(5);
        postCodeField.setSize(5);
        addressFieldSet.add(postCodeField);

        form.add(new Submit("New", this, "onCancelClick"));
        form.add(new Submit("Save", this, "onSaveClick"));
        form.add(new Submit("Cancel", this, "onCancelClick"));
        form.add(new HiddenField("pageNumber", String.class));

        // Table
        table.setAttribute("class", "simple");
        table.setAttribute("width", "500px;");
        table.setShowBanner(true);
        table.setPageSize(MAX_TABLE_SIZE);

        table.addColumn(new Column("id"));
        table.addColumn(new Column("name"));

        Column column = new Column("email");
        column.setAutolink(true);
        table.addColumn(column);

        table.addColumn(new Column("address.state", "State"));

        column = new Column("dateJoined");
        column.setFormat("{0,date,dd MMM yyyy}");
        table.addColumn(column);

        column = new Column("Action");
        column.setAttribute("width", "100px;");
        ActionLink[] links = new ActionLink[]{editLink, removeLink};
        column.setDecorator(new LinkDecorator(table, links, "id"));
        table.addColumn(column);

        removeLink.setAttribute("onclick", "return window.confirm('Are you sure you want to delete this Client?');");
    }

    public boolean onSaveClick() {
        if (form.isValid()) {
            Client client = (Client) form.getDataObject();

            msg = (client.isNew())
                ? "Client " + client.getName() + " created."
                : "Client " + client.getName() + " updated.";

            getDataContext().commitChanges();

            onCancelClick();
        }
        return true;
    }

    public boolean onCancelClick() {
        form.setDataObject(null);
        form.clearErrors();
        return true;
    }

    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        if (id != null) {
            Client client = getClientService().getClient(id);
            form.setDataObject(client);
        }
        return true;
    }

    public boolean onRemoveClick() {
        Integer id = removeLink.getValueInteger();
        if (id != null) {
            Client client = getClientService().getClient(id);

            if (client != null) {
                msg = "Client " + client.getName() + " deleted.";

                getClientService().deleteClient(client);

                onCancelClick();
            }
        }
        return true;
    }

    /**
     * @see net.sf.click.Page#onGet()
     */
    public void onGet() {
        form.getField("pageNumber").setValue("" + table.getPageNumber());
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        List list = getClientService().getClients();
        table.setRowList(list);

        if (list.size() <= MAX_TABLE_SIZE) {
            table.setShowBanner(false);
            table.setPageSize(0);
        }
    }

}
