package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.control.AbstractLink;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Column;
import net.sf.click.control.Form;
import net.sf.click.control.PageLink;
import net.sf.click.control.Submit;
import net.sf.click.control.Table;
import net.sf.click.control.TextField;
import net.sf.click.examples.control.SpacerButton;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.EditCustomer;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.LinkDecorator;

/**
 * Provides an demonstration of Table control paging.
 *
 * @author Malcolm Edgar
 */
public class SearchTablePage extends BorderPage {

    public Form form = new Form();
    public Table table = new Table();
    public PageLink editLink = new PageLink("Edit", EditCustomer.class);
    public ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    private TextField nameField = new TextField(Customer.NAME_PROPERTY);
    private DateField dateField = new DateField(Customer.DATE_JOINED_PROPERTY, "Start Date");

    // ----------------------------------------------------------- Constructors

    public SearchTablePage() {
        setStateful(true);

        // Setup the search form
        form.setColumns(2);
        form.add(nameField);
        form.add(dateField);
        form.add(new Submit("Search"));
        form.add(new Submit("Clear", this, "onClearClick"));
        form.add(new SpacerButton());
        form.add(new Submit("New...", this, "onNewClick"));

        // Setup customers table
        table.setClass(Table.CLASS_ITS);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setSortable(true);

        Column column = new Column(Customer.NAME_PROPERTY);
        column.setWidth("140px;");
        table.addColumn(column);

        column = new Column(Customer.EMAIL_PROPERTY);
        column.setAutolink(true);
        column.setWidth("230px;");
        table.addColumn(column);

        column = new Column(Customer.AGE_PROPERTY);
        column.setTextAlign("center");
        column.setWidth("40px;");
        table.addColumn(column);

        column = new Column(Customer.HOLDINGS_PROPERTY);
        column.setFormat("${0,number,#,##0.00}");
        column.setTextAlign("right");
        column.setWidth("100px;");
        table.addColumn(column);

        editLink.setImageSrc("/images/window-edit.png");
        editLink.setTitle("Edit customer details");
        editLink.setParameter("referrer", "/table/search-table.htm");

        deleteLink.setImageSrc("/images/window-delete.png");
        deleteLink.setTitle("Delete customer record");
        deleteLink.setAttribute("onclick", "return window.confirm('Are you sure you want to delete this record?');");

        column = new Column("Action");
        column.setTextAlign("center");
        AbstractLink[] links = new AbstractLink[] { editLink, deleteLink };
        column.setDecorator(new LinkDecorator(table, links, "id"));
        column.setSortable(false);
        table.addColumn(column);
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * Handle the clear button click event.
     *
     * @return true
     */
    public boolean onClearClick() {
        form.clearErrors();
        form.clearValues();
        return true;
    }

    /**
     * Handle the new button click event.
     *
     * @return false
     */
    public boolean onNewClick() {
        String path = getContext().getPagePath(EditCustomer.class);
        path += "?referrer=/table/search-table.htm";
        setRedirect(path);
        return false;
    }

    /**
     * Handle the delete link click event.
     *
     * @return true
     */
    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        getCustomerService().deleteCustomer(id);
        return true;
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        List customers =
            getCustomerService().getCustomers(nameField.getValue(), dateField.getDate());

        table.setRowList(customers);
    }
}
