package net.sf.click.examples.page.panel;

import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Panel;
import net.sf.click.control.Submit;
import net.sf.click.control.Table;
import net.sf.click.control.TextField;

import org.apache.commons.lang.StringUtils;

/**
 * Demonstrates usage of the Panel Column Control.
 *
 * @author rlecheta
 */
public class PanelColumnDemo extends net.sf.click.examples.page.BorderPage {

    public Panel panel = new Panel("panel", "/panel/customerDetailsPanel.htm");

    public Form form = new Form();

    private TextField textName;

    private Table table;

    public String nameSearch;

    /**
     * @see net.sf.click.examples.page.BorderPage#onInit()
     */
    public void onInit() {
        super.onInit();

        form.setMethod("get");

        form.add(textName = new TextField("name", "Name: ", true));
        textName.setFocus(true);
        form.add(new Submit("search", " Search ", this, "onSearch"));

        table = new Table("table");
        // The name of the PanelColumn is "customer" thus ${customer}
        // variable will be available in the template
        table.addColumn(new PanelColumn("customer", panel));
        table.setPageSize(3);
        addControl(table);
    }

    /**
     * Search listener
     * 
     * @return
     */
    public boolean onSearch() {
        if (form.isValid()) {
            String value = textName.getValue().trim();

            processSearch(value);

            return true;
        }
        return false;
    }

    public void onGet() {
        if (StringUtils.isNotEmpty(nameSearch)) {

            //just fill the value so the user can see it...
            textName.setValue(nameSearch);

            //and fill the table again...
            processSearch(nameSearch);
        }

    }

    /**
     * Search the Customer by name and create the Table control
     * 
     * @param value
     */
    private void processSearch(String value) {
        // Search for user entered value
        List list = getCustomerService().getCustomersForName(value);

        table.setRowList(list);

        // Set the parameter in the pagination link, 
        // so in the next page, we can fill the table again...
        table.getControlLink().setParameter("nameSearch", value);
    }
}
