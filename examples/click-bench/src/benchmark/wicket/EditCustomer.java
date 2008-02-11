package benchmark.wicket;

import java.util.Arrays;

import benchmark.dao.Customer;
import benchmark.dao.CustomerDao;
import java.util.Date;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * Edit customer benchmark page for Wicket.
 * 
 * @author Phil Kulak
 */
public class EditCustomer extends WebPage {

    public EditCustomer(IModel customer) {
        add(new EditCustomerForm("form", customer));
    }

    private static class EditCustomerForm extends Form {

        public EditCustomerForm(String id, IModel model) {
            super(id, new CompoundPropertyModel(model));
            add(new TextField("firstName"));
            add(new TextField("lastName"));
            add(new DropDownChoice("state", Arrays.asList(CustomerDao.STATES)));
            add(new TextField("birthDate", Date.class));
        }

        @Override
        public void onSubmit() {
            Customer customer = (Customer) getModelObject();
            CustomerDao.getInstance().saveOrUpdate(customer);
            setResponsePage(newPage(CustomerList.class));
        }
    }

}
