package benchmark.click.pages;

import net.sf.click.Page;
import net.sf.click.control.Form;
import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import benchmark.dao.Customer;
import benchmark.dao.CustomerDao;

public class AddCustomer extends Page {

    private Form form;

    public void onInit() {
        form = new Form("form");
        form.add(new TextField("id"));
        form.add(new TextField("firstName"));
        form.add(new TextField("lastName"));
        Select stateSelect = new Select("state");
        populateStates(stateSelect);
        form.add(stateSelect);
        form.add(new TextField("birthDate"));
        form.add(new Submit("submit", this, "onSubmit"));
        addControl(form);
    }

    public boolean onSubmit() {
        if (form.isFormSubmission()) {
            Customer customer = new Customer();
            form.copyTo(customer);
            CustomerDao.getInstance().saveOrUpdate(customer);
        }
        return true;
    }
    
    /**
     * Populate the Select control from backend STATE info.
     * 
     * @param select
     */
    private void populateStates(Select select) {
        for (int i = 0; i < CustomerDao.STATES.length; i++) {
            String state = CustomerDao.STATES[i];
            Option option = new Option(Integer.toString(i), state);
            select.getOptionList().add(option);
        }
    }
}

