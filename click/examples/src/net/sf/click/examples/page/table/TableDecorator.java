package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.Context;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Column;
import net.sf.click.control.Decorator;
import net.sf.click.control.PageLink;
import net.sf.click.control.Table;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.EditCustomer;

/**
 * Provides an demonstration of Table control paging.
 *
 * @author Malcolm Edgar
 */
public class TableDecorator extends BorderPage {

    public Table table = new Table();
    public Customer customerDetail;

    private ActionLink viewLink = new ActionLink("view", this, "onViewClick");
    private PageLink editLink = new PageLink("edit", EditCustomer.class);
    private ActionLink deleteLink = new ActionLink("delete", this, "onDeleteClick");

    public TableDecorator() {
        // Setup customers table
        table.setClass(Table.CLASS_SIMPLE);

        Column column = new Column("name");
        column.setSortable(false);
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
        column.setTextAlign("right");
        table.addColumn(column);

        viewLink.setTitle("View customer details");
        table.addControl(viewLink);

        editLink.setListener(this, "onEditClick");
        editLink.setTitle("Edit customer details");
        editLink.setParameter("referrer", "/table/table-decorator.htm");
        table.addControl(editLink);

        deleteLink.setTitle("Delete customer record");
        deleteLink.setAttribute
            ("onclick", "return window.confirm('Are you sure you want to delete this record?');");
        table.addControl(deleteLink);

        column = new Column("Action");
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                Customer customer = (Customer) row;
                String id = String.valueOf(customer.getId());

                viewLink.setContext(context);
                viewLink.setValue(id);

                editLink.setContext(context);
                editLink.setParameter("id", id);

                deleteLink.setContext(context);
                deleteLink.setValue(id);

                return viewLink.toString() + " | " +
                       editLink.toString() + " | " +
                       deleteLink.toString();
            }
        });
        table.addColumn(column);
    }

    public boolean onViewClick() {
        Integer id = viewLink.getValueInteger();
        customerDetail = getCustomerService().getCustomer(id);
        return true;
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
