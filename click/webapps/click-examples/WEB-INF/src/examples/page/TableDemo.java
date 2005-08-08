package examples.page;

import java.util.List;

import net.sf.click.Context;
import net.sf.click.control.Column;
import net.sf.click.control.Decorator;
import net.sf.click.control.Table;
import examples.domain.CustomerDAO;

public class TableDemo extends BorderedPage {

    public void onInit() {

        Table table = new Table("table");
        table.setAttribute("class", "simple");

        Column column = new Column("name");
        table.addColumn(column);

        column = new Column("email");
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                String email = row.toString();
                return "<a href='mailto:" + email + "'>" + email + "</a>";
            }
        });
        table.addColumn(column);

        column = new Column("age");
        table.addColumn(column);

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        table.addColumn(column);

        List customers = CustomerDAO.getCustomersSortedByName();
        table.setRowList(customers);

        addControl(table);
    }

}
