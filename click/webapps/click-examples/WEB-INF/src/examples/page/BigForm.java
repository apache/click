package examples.page;

import net.sf.click.control.Checkbox;
import net.sf.click.control.FileField;
import net.sf.click.control.Form;
import net.sf.click.control.Label;
import net.sf.click.control.PasswordField;
import net.sf.click.control.Radio;
import net.sf.click.control.RadioGroup;
import net.sf.click.control.Reset;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import net.sf.click.extras.control.CreditCardField;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;

/**
 * Provides a big form example which includes all the available Form Fields.
 *
 * @author Malcolm Edgar
 */
public class BigForm extends BorderedPage {

    private static final String DELIVERY_OPTIONS[] = {
        "[ Select ]", "Store Pickup", "Home Delivery", "Office Delivery"
    };

    public BigForm() {
        Form form = new Form("form");
        form.setErrorsPosition(Form.POSITION_TOP);

        form.add(new Label("user", "<b>My Details</b>"));

        TextField textField = new TextField("username", true);
        textField.setMinLength(6);
        textField.setMaxLength(12);
        textField.setTitle("Your username must be between 6 and 12 characters");
        textField.setFocus(true);
        form.add(textField);

        PasswordField passwordField = new PasswordField("password");
        passwordField.setMinLength(6);
        passwordField.setMaxLength(20);
        form.add(passwordField);

        form.add(new EmailField("email", "Email"));

        form.add(new IntegerField("customerNumber", true));

        form.add(new Label("hr", "<hr/>"));
        form.add(new Label("order", "<b>Order Details</b>"));

        DoubleField doubleField = new DoubleField("retailPrice");
        doubleField.setValue("99.95");
        form.add(doubleField);

        form.add(new CreditCardField("creditCard"));

        IntegerField monthField = new IntegerField("expiryMonth");
        monthField.setSize(2);
        monthField.setMaxLength(2);
        monthField.setMinValue(1);
        monthField.setMaxValue(12);
        form.add(monthField);

        IntegerField yearField = new IntegerField("expiryYear");
        yearField.setSize(4);
        yearField.setMaxLength(4);
        yearField.setMinValue(2000);
        yearField.setMaxValue(2010);
        form.add(yearField);

        Checkbox checkbox = new Checkbox("contact", "Contact me");
        checkbox.setTitle("Please contact me before delivery");
        form.add(checkbox);

        RadioGroup radioGroup = new RadioGroup("Packaging");
        radioGroup.add(new Radio("STD", "Standard "));
        radioGroup.add(new Radio("PRO", "Protective "));
        radioGroup.add(new Radio("GFT", "Gift Wrap "));
        radioGroup.setValue("STD");
        radioGroup.setVerticalLayout(false);
        form.add(radioGroup);

        Select select = new Select("deliveryType");
        select.addAll(DELIVERY_OPTIONS);
        select.setTitle("Type of delivery required");
        form.add(select);

        DateField dateField = new DateField("deliveryDate");
        dateField.setRequired(true);
        form.add(dateField);

        TextArea textArea = new TextArea("deliveryNotes");
        textArea.setCols(30);
        textArea.setTitle("Please tell us about any special delivery instructions");
        form.add(textArea);

        FileField fileField = new FileField("deliveryDocuments");
        fileField.setSize(26);
        form.add(fileField);

        form.add(new Submit("ok", "    OK    "));

        Submit cancelButton = new Submit("canel", this, "onCancelClick");
        cancelButton.setTitle("Return to Click Examples");
        form.add(cancelButton);

        form.add(new Reset("reset", "  Reset  "));

        addControl(form);
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}