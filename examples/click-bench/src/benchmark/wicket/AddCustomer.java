package benchmark.wicket;

import benchmark.dao.Customer;
import benchmark.dao.CustomerDao;
import java.util.Arrays;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Bob Schellink
 * 
 * Access this page at : http://localhost:8080/benchmark/wicket-session/?wicket:bookmarkablePage=wicket-2%3Abenchmark.wicket.AddCustomer
 */
public class AddCustomer extends WebPage {
    public AddCustomer() {
        add(new AddCustomerForm("form", new CompoundPropertyModel(new Customer())));
    }

    private static class AddCustomerForm extends Form {

        public AddCustomerForm(String id, IModel model) {
            super(id, model);
            add(new TextField("id"));
            add(new TextField("firstName"));
            add(new TextField("lastName"));
            add(new DropDownChoice("state", Arrays.asList(CustomerDao.STATES)));
            add(new TextField("birthDate"));
        }

        public void onSubmit() {
            Customer customer = (Customer) getModelObject();
            CustomerDao.getInstance().saveOrUpdate(customer);
        }
    }
}
