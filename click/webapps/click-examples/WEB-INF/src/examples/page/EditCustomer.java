package examples.page;

import net.sf.click.Page;
import net.sf.click.control.Checkbox;
import net.sf.click.control.DateField;
import net.sf.click.control.DoubleField;
import net.sf.click.control.EmailField;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.IntegerField;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
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

    Form form;
    HiddenField referrerField;

    /**
     * @see Page#onInit()
     */
    public void onInit() {
        form = new Form("form", getContext());
        addControl(form);

        referrerField = new HiddenField("referrer", String.class);
        form.add(referrerField);

        form.add(new HiddenField("id", Long.class));

        TextField nameField = new TextField("Name");
        nameField.setMinLength(5);
        nameField.setRequired(true);
        nameField.setTitle("Customer full name");
        nameField.setFocus(true);
        form.add(nameField);

        EmailField emailField = new EmailField("Email");
        emailField.setTitle("Customers email address");
        form.add(emailField);

        IntegerField ageField = new IntegerField("Age");
        ageField.setMinValue(1);
        ageField.setMaxValue(120);
        form.add(ageField);

        DoubleField holdingsField = new DoubleField("Holdings");
        holdingsField.setTitle("Total investment holdings");
        form.add(holdingsField);

        form.add(new InvestmentSelect("Investments"));

        DateField dateJoinedField = new DateField("Date Joined");
        dateJoinedField.setTitle("Date customer joined fund");
        form.add(dateJoinedField);

        form.add(new Checkbox("Active"));

        form.add(new Submit("    OK    ", this, "onOkClick"));
        form.add(new Submit(" Cancel ", this, "onCancelClick"));
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
            form.copyFrom(customer, true);
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
            form.copyTo(customer, true);
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
