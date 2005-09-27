package examples.page;

import net.sf.click.control.Checkbox;
import net.sf.click.control.CreditCardField;
import net.sf.click.control.DateField;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.IntegerField;
import net.sf.click.control.Radio;
import net.sf.click.control.RadioGroup;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;

/**
 * Provides a form FieldSet example.
 *
 * @author Malcolm Edgar
 */
public class FieldSetDemo extends BorderedPage {

    public void onInit() {
        Form form = new Form("form");
        addControl(form);

        // Delivery fieldset

        FieldSet deliveryFieldSet = new FieldSet("Delivery Details");
        form.add(deliveryFieldSet);

        TextField addressToField = new TextField("Addressed To", true);
        addressToField.setSize(30);
        deliveryFieldSet.add(addressToField);

        TextArea textArea = new TextArea("Delivery Address", true);
        textArea.setCols(30);
        textArea.setRows(3);
        deliveryFieldSet.add(textArea);

        DateField dateField = new DateField("Delivery Date");
        deliveryFieldSet.add(dateField);

        RadioGroup radioGroup = new RadioGroup("Packaging");
        radioGroup.add(new Radio("STD", "Standard "));
        radioGroup.add(new Radio("PRO", "Protective "));
        radioGroup.add(new Radio("GFT", "Gift Wrap "));
        radioGroup.setValue("STD");
        radioGroup.setVerticalLayout(true);
        deliveryFieldSet.add(radioGroup);

        deliveryFieldSet.add(new Checkbox("Telephone on Delivery"));

        // Payment fieldset

        FieldSet paymentFieldSet = new FieldSet("Payment Details");
        form.add(paymentFieldSet);

        paymentFieldSet.add(new TextField("Card Name"));
        paymentFieldSet.add(new CreditCardField("Card Number"));
        IntegerField expiryField = new IntegerField("Expiry");
        expiryField.setSize(4);
        expiryField.setMaxLength(4);
        paymentFieldSet.add(expiryField);

        form.add(new Submit("    OK    "));
        form.add(new Submit("  Cancel  ", this, "onCancelClick"));
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}
