package examples.page;

import net.sf.click.Page;
import net.sf.click.control.DateField;
import net.sf.click.control.DoubleField;
import net.sf.click.control.EmailField;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.IntegerField;
import net.sf.click.control.Reset;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import examples.control.InvestmentSelect;

/**
 * Provides a Velocity Macro example.
 *
 * @author Malcolm Edgar
 */
public class VelocityMacro extends BorderedPage {

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

        nameField = new TextField("Name");
        nameField.setMinLength(5);
        nameField.setRequired(true);
        nameField.setTitle("Customer full name");
        nameField.setFocus(true);
        form.add(nameField);

        emailField = new EmailField("Email");
        emailField.setRequired(true);
        emailField.setTitle("Customers email address");
        form.add(emailField);

        ageField = new IntegerField("Age");
        ageField.setMinValue(1);
        ageField.setMaxValue(120);
        form.add(ageField);

        holdingsField = new DoubleField("Holdings");
        holdingsField.setRequired(true);
        holdingsField.setTitle("Total investment holdings");
        form.add(holdingsField);

        investmentsField = new InvestmentSelect("Investments");
        form.add(investmentsField);

        okButton = new Submit("    OK    ");
        form.add(okButton);

        cancelButton = new Submit(" Cancel ");
        cancelButton.setListener(this, "onCancelClick");
        form.add(cancelButton);

        form.add(new Reset("Reset"));
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}

