package examples.page;

import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Checkbox;
import net.sf.click.control.DateField;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.IntegerField;
import net.sf.click.control.Radio;
import net.sf.click.control.RadioGroup;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import net.sf.click.extras.control.CreditCardField;
import net.sf.click.util.ClickUtils;

/**
 * Provides a form FieldSet example.
 *
 * @author Malcolm Edgar
 */
public class FieldSetDemo extends BorderedPage {

    private Form form = new Form("form");

    public FieldSetDemo() {
        // Delivery fieldset

        FieldSet deliveryFieldSet = new FieldSet("deliveryDetails");
        form.add(deliveryFieldSet);

        TextField addressToField = new TextField("addressedTo", true);
        addressToField.setSize(30);
        deliveryFieldSet.add(addressToField);

        TextArea textArea = new TextArea("deliveryAddress", true);
        textArea.setCols(30);
        textArea.setRows(3);
        deliveryFieldSet.add(textArea);

        DateField dateField = new DateField("deliveryDate");
        deliveryFieldSet.add(dateField);

        RadioGroup radioGroup = new RadioGroup("packaging");
        radioGroup.add(new Radio("STD", "Standard "));
        radioGroup.add(new Radio("PRO", "Protective "));
        radioGroup.add(new Radio("GFT", "Gift Wrap "));
        radioGroup.setValue("STD");
        radioGroup.setVerticalLayout(true);
        deliveryFieldSet.add(radioGroup);

        deliveryFieldSet.add(new Checkbox("telephoneOnDelivery"));

        // Payment fieldset

        FieldSet paymentFieldSet = new FieldSet("paymentDetails");
        form.add(paymentFieldSet);

        paymentFieldSet.add(new TextField("cardName"));
        paymentFieldSet.add(new CreditCardField("cardNumber"));
        IntegerField expiryField = new IntegerField("expiry");
        expiryField.setSize(4);
        expiryField.setMaxLength(4);
        paymentFieldSet.add(expiryField);

        form.add(new Submit("ok", "   OK   ",  this, "onOkClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));

        addControl(form);
    }

    public boolean onOkClick() {
        if (form.isValid()) {
            List fieldList = ClickUtils.getFormFields(form);
            for (Iterator i = fieldList.iterator(); i.hasNext(); ) {
                Field field = (Field) i.next();
                System.out.println(field.getName() + "=" + field.getValue());
            }
        }
        return true;
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}
