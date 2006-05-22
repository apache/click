package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.control.Column;
import net.sf.click.control.Form;
import net.sf.click.control.Select;
import net.sf.click.control.Table;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an demonstration of Table control styles.
 *
 * @author Malcolm Edgar
 */
public class TableStyles extends BorderPage {

    private static final String[] STYLES = {
        "isi", "its", "mars", "simple", "report",
    };

    private Table table = new Table("table");;
    private Select styleSelect = new Select("style", "Table Style:");

    public TableStyles() {
        // Setup table style select.
        Form form = new Form("form");
        form.setMethod("GET");
        addControl(form);

        styleSelect.addAll(STYLES);
        styleSelect.setAttribute("onchange", "this.form.submit();");
        form.add(styleSelect);

        // Setup customers table
        table.setAttribute("class", styleSelect.getValue());

        table.addColumn(new Column("id"));

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

        addControl(table);
    }

    public void onInit() {
        List customers = getCustomerService().getCustomersSortedByName();
        table.setRowList(customers);
    }

    public void onGet() {
        // Note the style form uses GET method.
        table.setAttribute("class", styleSelect.getValue());
    }

}
