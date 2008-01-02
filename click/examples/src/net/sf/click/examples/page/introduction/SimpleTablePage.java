package net.sf.click.examples.page.introduction;

import java.util.List;

import net.sf.click.Page;
import net.sf.click.control.Column;
import net.sf.click.control.Table;
import net.sf.click.examples.page.BorderPage;

public class SimpleTablePage extends BorderPage {

    public Table table = new Table();

    // ------------------------------------------------------------ Constructor

    public SimpleTablePage() {
        table.setClass(Table.CLASS_ITS);

        table.addColumn(new Column("id"));
        table.addColumn(new Column("name"));
        table.addColumn(new Column("email"));
        table.addColumn(new Column("investments"));
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * @see Page#onRender()
     */
    public void onRender() {
        List list = getCustomerService().getCustomersSortedByName(10);
        table.setRowList(list);
    }
}
