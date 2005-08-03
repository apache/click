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
        table.setAttribute("border", "1");
        table.setAttribute("cellpadding", "4");
        table.setAttribute("cellspacing", "4");

        Column column = new Column("name");
        column.setHeaderStyle("{background-color:navy;}");
        table.addColumn(column);

        column = new Column("email");
        column.setHeaderStyle("{background-color:navy;}");
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                String email = row.toString();
                return "<a href='mailto:" + email + "'>" + email + "</a>";
            }
        });
        table.addColumn(column);

        column = new Column("age");
        column.setHeaderStyle("{background-color:navy;}");
        table.addColumn(column);

        List customers = CustomerDAO.getCustomersSortedByName();
        table.setRowList(customers);

        addControl(table);
    }

}
