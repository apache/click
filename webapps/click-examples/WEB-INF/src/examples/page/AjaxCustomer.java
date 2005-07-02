package examples.page;

import net.sf.click.Page;
import examples.domain.Customer;
import examples.domain.CustomerDAO;

/**
 * Retrieves the Customer details using the given customerId parameter and
 * adds it to the Page model.
 * <p>
 * This Page class is configured to return its content type as "text/xml". Note
 * this can also be done by configuring a &lt;header&gt; element:
 *
 * <pre class="codeConfig">
 * &lt;page path="ajax-customer.htm" classname="examples.page.AjaxCustomer"&gt;
 *    &lt;header name="Content-Type" value="text/xml"/&gt;
 * &lt;/page&gt;
 * </pre>
 *
 * @author Malcolm Edgar
 */
public class AjaxCustomer extends Page {

    public void onGet() {
        String customerId = getContext().getRequest().getParameter("customerId");

        Customer customer = CustomerDAO.findCustomerByID(customerId);

        addModel("customer", customer);

        setHeader("Content-Type", "text/xml");
    }

}
