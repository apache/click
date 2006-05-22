package net.sf.click.examples.page;

import java.util.List;

import net.sf.click.Page;

/**
 * Provides JSP integration where a JSP page is used to render the results.
 *
 * @author Malcolm Edgar
 */
public class CustomerTable extends BorderPage {

    /**
     * @see Page#onGet()
     */
    public void onGet() {
        List customers = getCustomerService().getCustomersSortedByName(10);
        addModel("customers", customers);
    }
}
