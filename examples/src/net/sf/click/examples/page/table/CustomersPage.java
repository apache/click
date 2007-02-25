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
        table.setPageSize(5);
        table.setShowBanner(true);
        table.setSortable(true);

        table.addColumn(new Column("id"));
        table.addColumn(new Column("name"));

        Column column = new Column("email");
        column.setAutolink(true);
        table.addColumn(column);

        table.addColumn(new Column("investments"));

        column = new Column("Action");
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
