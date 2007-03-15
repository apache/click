package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Column;
import net.sf.click.control.Table;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.LinkDecorator;

public class CustomersPage extends BorderPage {

    public Table table = new Table();
    public ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    public CustomersPage() {
        table.setClass(Table.CLASS_ITS);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setSortable(true);

        table.addColumn(new Column("id"));

        Column column = new Column("email");
        column.setAutolink(true);
        column.setTitleProperty("name");
        table.addColumn(column);

        table.addColumn(new Column("investments"));

        deleteLink.setImageSrc("/images/delete-16px.gif");
        deleteLink.setTitle("Delete customer record");
        deleteLink.setAttribute("onclick", "return window.confirm('Are you sure you want to delete this record?');");

        column = new Column("Action");
        column.setTextAlign("center");
        column.setDecorator(new LinkDecorator(table, deleteLink, "id"));
        column.setSortable(false);
        table.addColumn(column);
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        getCustomerService().deleteCustomer(id);
        return true;
    }

    /**
     * @see Page#onRender()
     */
    public void onRender() {
        List list = getCustomerService().getCustomersSortedByName();
        table.setRowList(list);
    }
}
