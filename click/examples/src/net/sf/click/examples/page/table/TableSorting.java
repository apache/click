package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.control.Column;
import net.sf.click.control.Table;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an demonstration of Table column sorting using a database.
 *
 * @author Malcolm Edgar
 */
public class TableSorting extends BorderPage {

    public Table table = new Table();

    public TableSorting() {
        // Setup customers table
        table.setAttribute("class", "mars");
        table.setHoverRows(true);

        Column column = new Column("id");
        column.setSortable(false);
        table.addColumn(column);

        table.addColumn(new Column("name"));

        column = new Column("email");
        column.setAutolink(true);
        table.addColumn(column);

        column = new Column("age");
        column.setDataStyle("text-align", "center");
        column.setHeaderStyle("text-align", "center");
        table.addColumn(column);

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setDataStyle("text-align", "right");
        column.setHeaderStyle("text-align", "right");
        table.addColumn(column);

        column = new Column("active");
        column.setDataStyle("text-align", "center");
        table.addColumn(column);
    }

    /**
     * Load the Table rowList to render using the selected sorting column, and
     * then set the Table status to sorted.
     *
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        List customers = getCustomerService().getCustomersSortedBy(table.getSortedColumn());
        table.setRowList(customers);
        table.setSorted(true);
    }

}
