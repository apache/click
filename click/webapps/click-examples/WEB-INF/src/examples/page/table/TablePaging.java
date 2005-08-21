package examples.page.table;

import java.util.List;

import net.sf.click.extras.table.Column;
import net.sf.click.extras.table.Table;
import examples.domain.CustomerDAO;
import examples.page.BorderedPage;

/**
 * Provides an demonstration of Table control paging.
 *
 * @author Malcolm Edgar
 */
public class TablePaging extends BorderedPage {

    Table table;

    public void onInit() {

        // Setup customers table
        table = new Table("table");
        table.setAttribute("class", "its");
        table.setPageSize(3);
        table.setShowBanner(true);

        table.addColumn(new Column("name"));

        Column column = new Column("email");
        column.setAutolink(true);
        table.addColumn(column);

        column = new Column("age");
        column.setAttribute("style", "{text-align:center;}");
        table.addColumn(column);

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setAttribute("style", "{text-align:right;}");
        table.addColumn(column);

        List customers = CustomerDAO.getCustomersSortedByName();
        table.setRowList(customers);

        addControl(table);
    }

}
