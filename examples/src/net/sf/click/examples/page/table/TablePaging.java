package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.control.Column;
import net.sf.click.control.Table;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an demonstration of Table control paging.
 *
 * @author Malcolm Edgar
 */
public class TablePaging extends BorderPage {

    public Table table = new Table();

    public TablePaging() {
        // Setup customers table
        table.setAttribute("class", "its");
        table.setPageSize(10);
        table.setShowBanner(true);

        Column column = new Column("name");
        column.setAttribute("width", "140px;");
        table.addColumn(column);

        column = new Column("email");
        column.setAttribute("width", "230px;");
        column.setAutolink(true);
        table.addColumn(column);

        column = new Column("age");
        column.setAttribute("width", "40px;");
        column.setAttribute("style", "{text-align:center;}");
        table.addColumn(column);

        column = new Column("holdings");
        column.setAttribute("width", "100px;");
        column.setFormat("${0,number,#,##0.00}");
        column.setAttribute("style", "{text-align:right;}");
        table.addColumn(column);
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        List customers = getCustomerService().getCustomers();
        table.setRowList(customers);
    }
}
