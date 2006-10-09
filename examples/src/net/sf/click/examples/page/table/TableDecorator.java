package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.Context;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Column;
import net.sf.click.control.Decorator;
import net.sf.click.control.Table;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an demonstration of Table control paging.
 *
 * @author Malcolm Edgar
 */
public class TableDecorator extends BorderPage {

    public Table table = new Table();

    private ActionLink viewLink = new ActionLink("view");
    private ActionLink editLink = new ActionLink("edit");
    private ActionLink deleteLink = new ActionLink("delete");

    public TableDecorator() {
        // Setup customers table
        table.setAttribute("class", "simple");

        Column column = new Column("name");
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                Customer customer = (Customer) row;
                String email = customer.getEmail();
                String name = customer.getName();
                return "<a href='mailto:" + email + "'>" + name + "</a>";
            }
        });
        table.addColumn(column);

        column = new Column("investments");
        column.setAutolink(true);
        table.addColumn(column);

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setDataStyle("text-align", "right");
        table.addColumn(column);

        viewLink.setListener(this, "onViewClick");
        viewLink.setLabel("View");
        viewLink.setAttribute("title", "View customer details");
        table.addControl(viewLink);

        editLink.setListener(this, "onEditClick");
        editLink.setLabel("Edit");
        editLink.setAttribute("title", "Edit customer details");
        table.addControl(editLink);

        deleteLink.setListener(this, "onDeleteClick");
        deleteLink.setLabel("Delete");
        deleteLink.setAttribute("title", "Delete customer record");
        deleteLink.setAttribute
            ("onclick", "return window.confirm('Please confirm delete');");
        table.addControl(deleteLink);

        column = new Column("Action");
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                Customer customer = (Customer) row;
                String customerId = String.valueOf(customer.getId());

                viewLink.setContext(context);
                viewLink.setValue(customerId);
                editLink.setContext(context);
                editLink.setValue(customerId);
                deleteLink.setContext(context);
                deleteLink.setValue(customerId);

                return viewLink.toString() + " | " +
                       editLink.toString() + " | " +
                       deleteLink.toString();
            }
        });
        table.addColumn(column);
    }

    public boolean onViewClick() {
        Integer id = viewLink.getValueInteger();
        Customer customer = getCustomerService().getCustomer(id);
        addModel("customerDetail", customer);
        return true;
    }

    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        Customer customer = getCustomerService().getCustomer(id);

        if (customer != null) {
            getContext().setRequestAttribute("customer", customer);
            setForward("/edit-customer.htm?referrer=/table/table-decorator.htm");
            return false;

        } else {
            return true;
        }
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        getCustomerService().deleteCustomer(id);
        return true;
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        List customers = getCustomerService().getCustomersSortedByName(12);
        table.setRowList(customers);
    }

}
