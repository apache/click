package examples.page;

import net.sf.click.Page;
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
import examples.domain.CustomerDatabase;

/**
 * Provides an edit Customer Form example. The Customer business object
 * is initially passed to this Page as a request attribute.
 *
 * @author Malcolm Edgar
 */
public class EditCustomer extends Page {

    Form form;
    HiddenField idField;
    TextField nameField;
    EmailField emailField;
    IntegerField ageField;
    DoubleField holdingsField;
    InvestmentSelect investmentsField;
    DateField dateJoinedField;
    Submit okButton;
    Submit cancelButton;

    /**
     * @see Page#onInit()
     */
    public void onInit() {
        form = new Form("form", getContext());
        addControl(form);

        idField = new HiddenField("id", Long.class);
        form.add(idField);

        nameField = new TextField("Name");
        nameField.setMinLength(5);
        nameField.setRequired(true);
        nameField.setTitle("Customer full name");
        nameField.setFocus(true);
        form.add(nameField);

        emailField = new EmailField("Email");
        emailField.setTitle("Customers email address");
        form.add(emailField);

        ageField = new IntegerField("Age");
        ageField.setMinValue(1);
        ageField.setMaxValue(120);
        form.add(ageField);

        holdingsField = new DoubleField("Holdings");
        holdingsField.setTitle("Total investment holdings");
        form.add(holdingsField);

        investmentsField = new InvestmentSelect("Investments");
        form.add(investmentsField);

        dateJoinedField = new DateField("Date Joined");
        dateJoinedField.setTitle("Date customer joined fund");
        form.add(dateJoinedField);

        okButton = new Submit("    OK    ");
        okButton.setListener(this, "onOkClick");
        form.add(okButton);

        cancelButton = new Submit(" Cancel ");
        cancelButton.setListener(this, "onCancelClick");
        form.add(cancelButton);
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
            idField.setValue(customer.getId());
            nameField.setValue(customer.getName());
            emailField.setValue(customer.getEmail());
            ageField.setValue(customer.getAge());
            holdingsField.setValue(customer.getHoldings());
            investmentsField.setValue(customer.getInvestments());
            dateJoinedField.setDate(customer.getDateJoined());
        }
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

            customer.setId((Long) idField.getValueObject());
            customer.setName(nameField.getValue());
            customer.setEmail(emailField.getValue());
            customer.setAge(ageField.getInteger());
            customer.setHoldings(holdingsField.getDouble());
            customer.setInvestments(investmentsField.getValue());
            customer.setDateJoined(dateJoinedField.getDate());

            CustomerDatabase.setCustomer(customer);

            setRedirect("action-table.htm");
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
        setRedirect("action-table.htm");
        return false;
    }
}
