package examples.page;

import java.util.Iterator;
import java.util.List;

import net.sf.click.Page;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
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
import net.sf.click.extras.control.TabbedForm;
import net.sf.click.util.ClickUtils;

public class TabbedFormDemo extends BorderedPage {

    private TabbedForm form = new TabbedForm("form");

    public TabbedFormDemo() {

        // Delivery tab sheet
        FieldSet deliveryTabSheet = new FieldSet("deliveryDetails");
        form.addTabSheet(deliveryTabSheet);

        TextField addressToField = new TextField("addressedTo", true);
        addressToField.setSize(30);
        deliveryTabSheet.add(addressToField);

        TextArea textArea = new TextArea("deliveryAddress", true);
        textArea.setCols(30);
        textArea.setRows(3);
        deliveryTabSheet.add(textArea);

        DateField dateField = new DateField("deliveryDate");
        deliveryTabSheet.add(dateField);

        RadioGroup radioGroup = new RadioGroup("packaging");
        radioGroup.add(new Radio("STD", "Standard "));
        radioGroup.add(new Radio("PRO", "Protective "));
        radioGroup.add(new Radio("GFT", "Gift Wrap "));
        radioGroup.setValue("STD");
        radioGroup.setVerticalLayout(true);
        deliveryTabSheet.add(radioGroup);

        deliveryTabSheet.add(new Checkbox("telephoneOnDelivery"));

        // Payment tab sheet

        FieldSet paymentTabSheet = new FieldSet("paymentDetails");
        form.addTabSheet(paymentTabSheet);

        paymentTabSheet.add(new TextField("cardName"));
        paymentTabSheet.add(new CreditCardField("cardNumber"));
        IntegerField expiryField = new IntegerField("expiry");
        expiryField.setSize(4);
        expiryField.setMaxLength(4);
        paymentTabSheet.add(expiryField);

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
