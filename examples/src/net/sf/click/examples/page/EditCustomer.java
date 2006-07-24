package net.sf.click.examples.page;

import net.sf.click.Page;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.control.InvestmentSelect;
import net.sf.click.examples.domain.Customer;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;
import net.sf.click.extras.control.PageSubmit;

/**
 * Provides an edit Customer Form example. The Customer business object
 * is initially passed to this Page as a request attribute.
 *
 * @author Malcolm Edgar
 */
public class EditCustomer extends BorderPage {

    private Form form = new Form("form");
    private HiddenField referrerField = new HiddenField("referrer", String.class);
    private HiddenField idField = new HiddenField("id", Integer.class);

    public EditCustomer() {
        form.add(referrerField);

        form.add(idField);

        TextField nameField = new TextField("name", true);
        nameField.setMinLength(5);
        nameField.setFocus(true);
        form.add(nameField);

        form.add(new EmailField("email"));

        IntegerField ageField = new IntegerField("age");
        ageField.setMinValue(1);
        ageField.setMaxValue(120);
        form.add(ageField);

        form.add(new DoubleField("holdings"));
        form.add(new InvestmentSelect("investments"));
        form.add(new DateField("dateJoined"));
        form.add(new Checkbox("Active"));

        form.add(new Submit("ok", "  OK  ", this, "onOkClick"));
        form.add(new PageSubmit("cancel", HomePage.class));

        addControl(form);
    }

    /**
     * When page is first displayed on the GET request load the customer
     * details into the form fields.
     *
     * @see Page#onGet()
     */
    public void onGet() {
        Customer customer = (Customer)
            getContext().getRequestAttribute("customer");

        if (customer != null) {
            form.copyFrom(customer);
        }

        String referrer = getContext().getRequestParameter("referrer");
        referrerField.setValue(referrer);
    }

    /**
     * On a POST OK button submit, if the form is valid update the customer
     * details and return to "action-table.htm", otherwise display form errors.
     *
     * @return true
     */
    public boolean onOkClick() {
        if (form.isValid()) {
            Integer id = (Integer) idField.getValueObject();
            Customer customer = getCustomerService().getCustomer(id);

            form.copyTo(customer);

            getCustomerService().setCustomer(customer);

            String referrer = referrerField.getValue();
            if (referrer != null) {
                setRedirect(referrer);
            } else {
                setRedirect("/index.htm");
            }

            return true;

        } else {
            return true;
        }
    }

}
