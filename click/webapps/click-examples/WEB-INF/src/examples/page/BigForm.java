package examples.page;

import net.sf.click.control.Checkbox;
import net.sf.click.control.CreditCardField;
import net.sf.click.control.DateField;
import net.sf.click.control.DoubleField;
import net.sf.click.control.EmailField;
import net.sf.click.control.Form;
import net.sf.click.control.IntegerField;
import net.sf.click.control.Label;
import net.sf.click.control.PasswordField;
import net.sf.click.control.Radio;
import net.sf.click.control.RadioGroup;
import net.sf.click.control.Reset;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;

/**
 * Provides a big form example which includes all the available Form Fields.
 *
 * @author Malcolm Edgar
 */
public class BigForm extends BorderedPage {

    String DELIVERY_OPTIONS[] = {
            "[ Select ]", "Store Pickup", "Home Delivery", "Office Delivery"
    };

    RadioGroup radioGroup;

    public void onInit() {
        Form form = new Form("form", getContext());
        addControl(form);

        form.add(new Label("<b>My Details</b>"));

        TextField textField = new TextField("Username");
        textField.setRequired(true);
        textField.setMinLength(6);
        textField.setMaxLength(12);
        textField.setTitle("Your username must be between 6 and 12 characters");
        textField.setFocus(true);
        form.add(textField);

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setRequired(true);
        passwordField.setMinLength(6);
        passwordField.setMaxLength(20);
        form.add(passwordField);

        EmailField emailField = new EmailField("Email");
        emailField.setRequired(true);
        form.add(emailField);

        IntegerField integerField = new IntegerField("Customer Number");
        integerField.setRequired(true);
        form.add(integerField);

        form.add(new Label("<hr/>"));
        form.add(new Label("<b>Order Details</b>"));

        DoubleField doubleField = new DoubleField("Retail Price");
        doubleField.setValue("99.95");
        form.add(doubleField);

        CreditCardField creditCardField = new CreditCardField("Credit Card");
        form.add(creditCardField);

        IntegerField monthField = new IntegerField("Expiry Month");
        monthField.setMaxLength(2);
        monthField.setMinValue(1);
        monthField.setMaxValue(12);
        form.add(monthField);

        IntegerField yearField = new IntegerField("Expiry Year");
        yearField.setMaxLength(4);
        yearField.setMinValue(2000);
        yearField.setMaxValue(2010);
        form.add(yearField);

        Checkbox checkbox = new Checkbox("Contact me");
        checkbox.setTitle("Please contact me before delivery");
        form.add(checkbox);

        radioGroup = new RadioGroup("Packaging", getContext());
        radioGroup.add(new Radio("STD", "Standard "));
        radioGroup.add(new Radio("PRO", "Protective "));
        radioGroup.add(new Radio("GFT", "Gift Wrap "));
        radioGroup.setValue("STD");
        form.add(radioGroup);

        Select select = new Select("Delivery type");
        select.addAll(DELIVERY_OPTIONS);
        select.setRequired(true);
        select.setTitle("Type of delivery required");
        form.add(select);

        DateField dateField = new DateField("Delivery date");
        dateField.setFormatPattern("dd/MM/yy");
        form.add(dateField);
        
        DateField calendarField = new DateField("Shipping date");
        form.add(calendarField);

        TextArea textArea = new TextArea("Delivery Notes");
        textArea.setTitle("Please tell us about any special delivery instructions");
        form.add(textArea);

        Submit okButton = new Submit("    OK    ");
        okButton.setTitle("Submit the form");
        form.add(okButton);

        Submit cancelButton = new Submit("  Cancel  ");
        cancelButton.setListener(this, "onCancelClick");
        cancelButton.setTitle("Return to Click Examples");
        form.add(cancelButton);

        Reset reset = new Reset("  Reset  ");
        reset.setTitle("Undo any form changes");
        form.add(reset);
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}
