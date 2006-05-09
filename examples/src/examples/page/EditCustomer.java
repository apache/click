package examples.page;

import net.sf.click.Page;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;
import examples.control.InvestmentSelect;
import examples.domain.Customer;
import examples.domain.CustomerDAO;

/**
 * Provides an edit Customer Form example. The Customer business object
 * is initially passed to this Page as a request attribute.
 *
 * @author Malcolm Edgar
 */
public class EditCustomer extends BorderedPage {

    private Form form = new Form("form");
    private HiddenField referrerField = new HiddenField("referrer", String.class);

    public EditCustomer() {
        form.add(referrerField);

        form.add(new HiddenField("id", Long.class));

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

        form.add(new Submit("ok", this, "onOkClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));

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

            Customer customer = new Customer();
            form.copyTo(customer);
            CustomerDAO.setCustomer(customer);

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

    /**
     * On a POST Cancel button submit redirect to "action-table.htm"
     *
     * @return false to stop processing
     */
    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}
