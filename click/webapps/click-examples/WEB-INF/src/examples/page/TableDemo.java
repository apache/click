package examples.page;

import java.util.List;

import net.sf.click.control.Column;
import net.sf.click.control.Table;
import examples.domain.CustomerDAO;

public class TableDemo extends BorderedPage {

    public void onInit() {
        List customers = CustomerDAO.getCustomersSortedByName();

        Table table = new Table("table");
        table.setAttribute("border", "1");

        Column column = new Column("name");
        column.setHeaderStyle("{background-color:navy;}");
        table.addColumn(column);

        column = new Column("email");
        table.addColumn(column);

        column = new Column("age");
        table.addColumn(column);

        table.setRows(customers);
        addControl(table);
    }

}
