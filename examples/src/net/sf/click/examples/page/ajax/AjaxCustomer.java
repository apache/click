package net.sf.click.examples.page.ajax;

import net.sf.click.Page;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.SpringPage;

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
public class AjaxCustomer extends SpringPage {

    /**
     * Process the AJAX request and return XML customer table.
     *
     * @see Page#onGet()
     */
    public void onGet() {
        String customerId = getContext().getRequest().getParameter("customerId");

        Customer customer = getCustomerService().findCustomerByID(customerId);

        if (customer != null) {
            addModel("customer", customer);
        }
    }

    /**
     * Ensure the Http response Content-type is "text/xml".
     *
     * @see Page#getContentType()
     */
    public String getContentType() {
        return "text/xml";
    }

    /**
     * Override border template to return page path.
     *
     * @see Page#getTemplate()
     */
    public String getTemplate() {
        return getPath();
    }
}
