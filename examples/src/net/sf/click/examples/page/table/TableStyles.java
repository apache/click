package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.control.Checkbox;
import net.sf.click.control.Column;
import net.sf.click.control.Form;
import net.sf.click.control.Label;
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

    public Form form = new Form();
    public Table table = new Table();

    private Select styleSelect = new Select("style", "Table Style:");
    private Checkbox hoverCheckbox = new Checkbox("hover", "Hover Rows:");

    public TableStyles() {
        // Setup table style select.
        form.setColumns(3);
        form.setLabelAlign(Form.ALIGN_LEFT);
        form.setMethod("GET");

        styleSelect.addAll(STYLES);
        styleSelect.setAttribute("onchange", "this.form.submit();");
        form.add(styleSelect);

        form.add(new Label("&nbsp; &nbsp;"));

        hoverCheckbox.setAttribute("onchange", "this.form.submit();");
        form.add(hoverCheckbox);

        // Setup customers table
        table.setAttribute("class", styleSelect.getValue());
        table.setHoverRows(true);

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
    }

    /**
     * @see net.sf.click.Page#onGet()
     */
    public void onGet() {
        // Note the style form uses GET method.
        table.setAttribute("class", styleSelect.getValue());
        table.setHoverRows(hoverCheckbox.isChecked());
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        List customers = getCustomerService().getCustomersSortedByName(12);
        table.setRowList(customers);
    }

}
