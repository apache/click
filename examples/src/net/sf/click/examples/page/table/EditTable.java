package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.control.ActionLink;
import net.sf.click.control.Column;
import net.sf.click.control.FieldSet;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Submit;
import net.sf.click.control.Table;
import net.sf.click.control.TextField;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.cayenne.CayenneForm;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.LinkDecorator;

/**
 * Provides an demonstration of Table and Form editor pattern, and the use
 * CayenneForm and LinkDecorator classes.
 *
 * @author Malcolm Edgar
 */
public class EditTable extends BorderPage {

    public CayenneForm form = new CayenneForm("form", Customer.class);
    public Table table = new Table();
    public ActionLink editLink = new ActionLink("edit", "Edit", this, "onEditClick");
    public ActionLink deleteLink = new ActionLink("delete", "Delete", this, "onDeleteClick");

    public EditTable() {
        // Setup customers form
        FieldSet fieldSet = new FieldSet("customer");
        fieldSet.add(new TextField("name"));
        fieldSet.add(new EmailField("email"));
        fieldSet.add(new DoubleField("holdings"));
        fieldSet.add(new DateField("dateJoined"));
        form.add(fieldSet);
        form.add(new Submit("save", this, "onSaveClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));
        form.add(new HiddenField("pageNumber", String.class));

        // Setup customers table
        table.setAttribute("class", "simple");
        table.setPageSize(10);
        table.setShowBanner(true);

        Column column = new Column("name");
        table.addColumn(column);

        column = new Column("email");
        column.setAutolink(true);
        table.addColumn(column);

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setAttribute("style", "{text-align:right;}");
        table.addColumn(column);

        column = new Column("dateJoined");
        column.setFormat("{0,date,medium}");
        table.addColumn(column);

        column = new Column("Action");
        ActionLink[] links = new ActionLink[]{editLink, deleteLink};
        column.setDecorator(new LinkDecorator(table, links, "id"));
        table.addColumn(column);

        deleteLink.setAttribute("onclick", "return window.confirm('Please confirm delete');");
    }

    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        Customer customer = getCustomerService().getCustomer(id);
        if (customer != null) {
            form.setDataObject(customer);
        }
        return true;
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        getCustomerService().deleteCustomer(id);
        return true;
    }

    public boolean onSaveClick() {
        if (form.isValid()) {
            // Please note with Cayenne ORM this will persist any changes
            // to data objects submitted by the form.
            form.getDataObject();
            getDataContext().commitChanges();
            form.setDataObject(null);
        }
        return true;
    }

    public boolean onCancelClick() {
        getDataContext().rollbackChanges();
        form.setDataObject(null);
        form.clearErrors();
        return true;
    }

    /**
     * @see net.sf.click.Page#onGet()
     */
    public void onGet() {
        form.getField("pageNumber").setValue(""+table.getPageNumber());
    }

    /**
     * @see net.sf.click.Page#onPost()
     */
    public void onPost() {
        String pageNumber = form.getField("pageNumber").getValue();
        table.setPageNumber(Integer.parseInt(pageNumber));
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        List customers = getCustomerService().getCustomers();
        table.setRowList(customers);
    }

}
