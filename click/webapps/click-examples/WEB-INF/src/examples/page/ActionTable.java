package examples.page;
import examples.domain.Customer;
import examples.domain.CustomerDatabase;
import net.sf.click.Page;
import net.sf.click.control.ActionLink;

/**
 * Provides a dynamic ActionLink example in a HTML table.
 *
 * @author Malcolm Edgar
 */
public class ActionTable extends Page {

    ActionLink viewLink;
    ActionLink editLink;
    ActionLink deleteLink;

    /**
     * @see Page#onInit()
     */
    public void onInit() {
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
        Long id = viewLink.getValueLong();
        Customer customer = CustomerDatabase.getCustomer(id);
        addModel("customerDetail", customer);

        return true;
    }

    /**
     * Edit the selected customer.
     *
     * @return false to stop continued event processing
     */
    public boolean onEditClick() {
        Long id = editLink.getValueLong();
        Customer customer = CustomerDatabase.getCustomer(id);

        if (customer != null) {
            getContext().setRequestAttribute("customer", customer);
            setForward("edit-customer.htm");
            return false;

        } else {
            return true;
        }
    }

    public boolean onDeleteClick() {
        Long id = deleteLink.getValueLong();
        CustomerDatabase.deleteCustomer(id);

        return true;
    }

    /**
     * Load the list of customers to display.  This method is not invoked
     * when a customer is being edited.
     *
     * @see Page#onGet()
     */
    public void onGet() {
        addModel("customers", CustomerDatabase.getCustomersSortedByName());
    }

}
