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

    public Form form = new Form();
    public Table table = new Table();

    private Select styleSelect = new Select("style", "Table Style:");
    private Checkbox hoverCheckbox = new Checkbox("hover", "Hover Rows:");

    // ----------------------------------------------------------- Constructor

    public TableStyles() {
        setStateful(true);

        // Setup table style select.
        form.setColumns(3);
        form.setLabelAlign(Form.ALIGN_LEFT);

        styleSelect.addAll(Table.CLASS_STYLES);
        styleSelect.setAttribute("onchange", "this.form.submit();");
        form.add(styleSelect);

        form.add(new Label("&nbsp; &nbsp;"));

        hoverCheckbox.setAttribute("onchange", "this.form.submit();");
        form.add(hoverCheckbox);

        // Setup customers table
        table.setClass(styleSelect.getValue());
        table.setHoverRows(true);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setSortable(true);

        Column column = new Column("id");
        column.setWidth("50px");
        column.setSortable(false);
        table.addColumn(column);

        column = new Column("name");
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

    // --------------------------------------------------------- Event Handlers

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        table.setClass(styleSelect.getValue());
        table.setHoverRows(hoverCheckbox.isChecked());

        List customers = getCustomerService().getCustomers();
        table.setRowList(customers);
    }

}
