package net.sf.click.examples.page.introduction;

import java.util.List;

import net.sf.click.Page;
import net.sf.click.control.AbstractLink;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Column;
import net.sf.click.control.PageLink;
import net.sf.click.control.Table;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.EditCustomer;
import net.sf.click.extras.control.LinkDecorator;

public class AdvancedTable extends BorderPage {

    public Table table = new Table();
    public PageLink editLink = new PageLink("Edit", EditCustomer.class);
    public ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    // ------------------------------------------------------------ Constructor

    public AdvancedTable() {
        table.setClass(Table.CLASS_ITS);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setSortable(true);

        table.addColumn(new Column("id"));

        table.addColumn(new Column("name"));

        Column column = new Column("email");
        column.setAutolink(true);
        column.setTitleProperty("name");
        table.addColumn(column);

        table.addColumn(new Column("investments"));

        editLink.setImageSrc("/assets/images/window-edit.png");
        editLink.setTitle("Edit customer details");
        editLink.setParameter("referrer", "/introduction/advanced-table.htm");

        deleteLink.setImageSrc("/assets/images/window-delete.png");
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

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        getCustomerService().deleteCustomer(id);
        return true;
    }

    /**
     * @see Page#onRender()
     */
    public void onRender() {
        List list = getCustomerService().getCustomers();
        table.setRowList(list);
    }
}
