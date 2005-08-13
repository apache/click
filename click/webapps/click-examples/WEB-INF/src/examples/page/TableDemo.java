package examples.page;

import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Select;
import net.sf.click.extras.table.Column;
import net.sf.click.extras.table.Table;
import examples.domain.CustomerDAO;

/**
 * Provides an Table control demonstration.
 *
 * @author Malcolm Edgar
 */
public class TableDemo extends BorderedPage {

    private static final String[] STYLES = {
        "isi", "its", "mars", "simple", "report",
    };

    Table table;
    Select styleSelect;

    public void onInit() {

        // Setup customers table
        table = new Table("table");
        table.setAttribute("class", "simple");

        table.addColumn(new Column("name"));

        Column column = new Column("email");
        column.setAutolink(true);
        table.addColumn(column);

        table.addColumn(new Column("age"));

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setAttribute("style", "{text-align:right;}");
        table.addColumn(column);

        List customers = CustomerDAO.getCustomersSortedByName();
        table.setRowList(customers);

        addControl(table);

        // Setup table style select.
        Form form = new Form("form", getContext());
        addControl(form);

        styleSelect = new Select("Style");
        styleSelect.addAll(STYLES);
        styleSelect.setAttribute("onchange", "this.form.submit();");
        form.add(styleSelect);
    }

    public void onPost() {
        table.setAttribute("class", styleSelect.getValue());
    }

}
