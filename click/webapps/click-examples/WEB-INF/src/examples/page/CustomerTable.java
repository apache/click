package examples.page;

import java.util.List;

import net.sf.click.Page;
import examples.domain.CustomerDAO;

/**
 * Provides JSP integration where a JSP page is used to render the results.
 *
 * @author Malcolm Edgar
 */
public class CustomerTable extends BorderedPage {

    /**
     * @see Page#onGet()
     */
    public void onGet() {
        List customers = CustomerDAO.getCustomersSortedByName(10);
        addModel("customers", customers);
    }
}
