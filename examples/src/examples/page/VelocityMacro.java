package examples.page;

import net.sf.click.control.Form;
import net.sf.click.control.Reset;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;
import examples.control.InvestmentSelect;

/**
 * Provides a Velocity Macro example.
 *
 * @author Malcolm Edgar
 */
public class VelocityMacro extends BorderedPage {

    public VelocityMacro() {
        Form form = new Form("form");
        addControl(form);

        TextField nameField = new TextField("name", true);
        nameField.setMinLength(5);
        nameField.setTitle("Customer full name");
        nameField.setFocus(true);
        form.add(nameField);

        EmailField emailField = new EmailField("email", true);
        emailField.setTitle("Customers email address");
        form.add(emailField);

        IntegerField ageField = new IntegerField("age");
        ageField.setMinValue(1);
        ageField.setMaxValue(120);
        form.add(ageField);

        DoubleField holdingsField = new DoubleField("holdings", true);
        holdingsField.setTitle("Total investment holdings");
        form.add(holdingsField);

        form.add(new InvestmentSelect("investments"));

        form.add(new Submit("ok", "    OK    "));
        form.add(new Submit("cancel", this, "onCancelClick"));
        form.add(new Reset("reset"));
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}

