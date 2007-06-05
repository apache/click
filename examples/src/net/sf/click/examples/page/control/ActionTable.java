package net.sf.click.examples.page.control;

import java.util.List;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.control.PageLink;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.EditCustomer;

/**
 * Provides a dynamic ActionLink and PageLink example in a HTML table.
 * <p/>
 * In this example the controls are automatically added to the Page model
 * because they have public visiblity. The controls name is automatically set
 * to their field name.
 *
 * @author Malcolm Edgar
 */
public class ActionTable extends BorderPage {

    public List customers;
    public ActionLink viewLink = new ActionLink(this, "onViewClick");
    public PageLink editLink = new PageLink(EditCustomer.class);
    public ActionLink deleteLink = new ActionLink(this, "onDeleteClick");

    public void onInit() {
        String path = getContext().getPagePath(getClass());
        editLink.setParameter("referrer", path);
    }

    public boolean onViewClick() {
        Integer id = viewLink.getValueInteger();
        Customer customer = getCustomerService().getCustomer(id);
        if (customer != null) {
        	addModel("customerDetail", customer);
        }

        return true;
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        getCustomerService().deleteCustomer(id);

        return true;
    }

    /**
     * Load the list of customers to display.  This method is not invoked
     * when a customer is being edited.
     *
     * @see Page#onRender()
     */
    public void onRender() {
        customers = getCustomerService().getCustomersSortedByName(7);
        getFormat().setEmptyString("&nbsp;");
    }

}
