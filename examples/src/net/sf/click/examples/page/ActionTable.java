package net.sf.click.examples.page;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.examples.domain.Customer;

/**
 * Provides a dynamic ActionLink example in a HTML table.
 *
 * @author Malcolm Edgar
 */
public class ActionTable extends BorderPage {

    private ActionLink viewLink;
    private ActionLink editLink;
    private ActionLink deleteLink;

    public ActionTable() {
        viewLink = new ActionLink("viewLink");
        viewLink.setListener(this, "onViewClick");
        addControl(viewLink);

        editLink = new ActionLink("editLink");
        editLink.setListener(this, "onEditClick");
        addControl(editLink);

        deleteLink = new ActionLink("deleteLink");
        deleteLink.setListener(this, "onDeleteClick");
        addControl(deleteLink);
    }

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
            setForward("edit-customer.htm");
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
     * @see Page#onGet()
     */
    public void onGet() {
        addModel("customers", getCustomerService().getCustomersSortedByName(7));
    }

}
