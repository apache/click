package net.sf.click.examples.page;

import java.util.List;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.examples.domain.Customer;

/**
 * Provides a dynamic ActionLink example in a HTML table.
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
    public ActionLink editLink = new ActionLink(this, "onEditClick");
    public ActionLink deleteLink = new ActionLink(this, "onDeleteClick");

    public boolean onViewClick() {
        Integer id = viewLink.getValueInteger();
        Customer customer = getCustomerService().getCustomer(id);
        addModel("customerDetail", customer);

        return true;
    }

    /**
     * Edit the selected customer.
     *
     * @return false to stop continued event processing
     */
    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        Customer customer = getCustomerService().getCustomer(id);

        if (customer != null) {
            getContext().setRequestAttribute("customer", customer);
            setForward(EditCustomer.class);
            return false;

        } else {
            return true;
        }
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
    }

}
