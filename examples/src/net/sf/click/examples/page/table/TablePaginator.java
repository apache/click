package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.control.Column;
import net.sf.click.control.DefaultPaginator;
import net.sf.click.control.InlinePaginator;
import net.sf.click.control.Table;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an demonstration of Table pagination options.
 *
 * @author Malcolm Edgar
 */
public class TablePaginator extends BorderPage {

    public Table table1 = new Table();
    public Table table2 = new Table();
    public Table table3 = new Table();

    public TablePaginator() {
        // Table 1
        addColumns(table1);
        table1.setPaginator(new DefaultPaginator());
        table1.setPaginatorAttachment(Table.PAGINATOR_ATTACHED);

        // Table 2
        addColumns(table2);
        table2.setPaginator(new DefaultPaginator());
        table2.setPaginatorAttachment(Table.PAGINATOR_DETACHED);

        // Table 3
        addColumns(table3);
        table3.setPaginator(new InlinePaginator());
        table3.setPaginatorAttachment(Table.PAGINATOR_INLINE);
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        List customers = getCustomerService().getCustomers();
        table1.setRowList(customers);
        table2.setRowList(customers);
        table3.setRowList(customers);
    }

    private void addColumns(Table table) {
        table.setClass(Table.CLASS_ITS);
        table.setPageSize(4);

        Column column = new Column("name");
        column.setWidth("140px;");
        table.addColumn(column);

        column = new Column("email");
        column.setAutolink(true);
        column.setWidth("230px;");
        table.addColumn(column);

        column = new Column("age");
        column.setTextAlign("center");
        column.setWidth("40px;");
        table.addColumn(column);

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setTextAlign("right");
        column.setWidth("100px;");
        table.addColumn(column);
    }
}
