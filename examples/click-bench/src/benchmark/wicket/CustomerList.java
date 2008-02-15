package benchmark.wicket;

import java.text.SimpleDateFormat;
import java.util.List;

import benchmark.dao.Customer;
import benchmark.dao.CustomerDao;
import benchmark.dao.DetachableCustomer;
import benchmark.dao.DetachableCustomerList;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * List customers benchmark page for Wicket.
 * 
 * @author Phil Kulak
 */
public class CustomerList extends WebPage {

    private static class CustomerListView extends ListView {

        // In original test format was defined as a constant. But then
        // ArrayOutOfBounds exception is thrown every now and then. So it is
        // defined here as an instance variable.
        private SimpleDateFormat FORMAT = new SimpleDateFormat("MMMM d, yyyy");

        public CustomerListView(String id, IModel model) {
            super(id, model);
        }

        protected IModel getListItemModel(IModel model, int index) {
            Customer customer = (Customer) ((List) model.getObject()).get(index);
            return new CompoundPropertyModel(new DetachableCustomer(customer));
        }

        protected void populateItem(final ListItem item) {
            item.add(new Label("firstName"));
            item.add(new Label("lastName"));
            item.add(new Label("state"));
            item.add(new Label("birthDate", new Model() {

                public Object getObject() {
                    Customer customer = (Customer) item.getModelObject();

                    // Under heavy testing the customer is sometimes null
                    if (customer != null) {
                        try {
                            return FORMAT.format(customer.getBirthDate());
                        } catch (Exception exception) {
                            System.out.println("Error formatting date : " + customer.getBirthDate());
                            throw new RuntimeException(exception);
                        }

                    } else {
                        return customer.getBirthDate();
                    }
                }
            }));

            item.add(new Link("delete") {

                public void onClick() {
                    Customer customer = (Customer) getParent().getModelObject();
                    CustomerDao.getInstance().delete(customer);
                }
            });

            item.add(new Link("edit") {

                public void onClick() {
                    EditCustomer editPage = new EditCustomer(getParent().getModel());
                    setResponsePage(editPage);
                }
            });
        }
    }

    public CustomerList() {
        add(new CustomerListView("rows", new DetachableCustomerList()));
    }
}
