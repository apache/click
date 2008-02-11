package benchmark.click.pages;

import benchmark.click.controls.DateTextField;
import net.sf.click.Page;
import net.sf.click.control.Form;
import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;

import benchmark.dao.Customer;
import benchmark.dao.CustomerDao;
import net.sf.click.control.HiddenField;

public class EditCustomer extends Page {

    private Customer customer;

    private Form form;
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void onInit() {
        form = new Form("form");
        form.add(new HiddenField("id", Integer.class));
        form.add(new TextField("firstName"));
        form.add(new TextField("lastName"));
        Select stateSelect = new Select("state");
        populateStates(stateSelect);
        form.add(stateSelect);
        form.add(new DateTextField("birthDate"));
        form.add(new Submit("submit", this, "onSubmit"));
        addControl(form);
    }

    public boolean onSubmit() {
        if (form.isFormSubmission()) {
            Integer id = (Integer) form.getField("id").getValueObject();
            Customer customerHolder = CustomerDao.getInstance().findById(id);
            form.copyTo(customerHolder);
            CustomerDao.getInstance().saveOrUpdate(customerHolder);
            String target = getContext().getPagePath(CustomerList.class);
            setForward(target);
        }
        return true;
    }

    public void onGet() {
        if (customer != null) {
            form.copyFrom(customer);
        }
    }

    /**
     * Populate the Select control from backend STATE info.
     * 
     * @param select
     */
    private void populateStates(Select select) {
        for (int i = 0; i < CustomerDao.STATES.length; i++) {
            String state = CustomerDao.STATES[i];
            Option option = new Option(state, state);
            select.getOptionList().add(option);
        }
    }
}
