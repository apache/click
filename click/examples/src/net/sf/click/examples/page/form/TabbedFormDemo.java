package net.sf.click.examples.page.form;

import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Checkbox;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Radio;
import net.sf.click.control.RadioGroup;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import net.sf.click.examples.control.PackagingRadioGroup;
import net.sf.click.examples.control.TitleSelect;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.CreditCardField;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;
import net.sf.click.extras.control.PageSubmit;
import net.sf.click.extras.control.TabbedForm;
import net.sf.click.extras.control.TelephoneField;
import net.sf.click.util.ClickUtils;

/**
 * Provides an TabbedForm control example.
 *
 * @author Malcolm Edgar
 */
public class TabbedFormDemo extends BorderPage {

    private TabbedForm form = new TabbedForm("form");
    private RadioGroup paymentGroup = new RadioGroup("paymentOption", true);
    private TelephoneField contactNumber = new TelephoneField("contactNumber");
    private Checkbox telephoneOnDelivery = new Checkbox("telephoneOnDelivery");
    private TextField cardName = new TextField("cardName");
    private CreditCardField cardNumber = new CreditCardField("cardNumber");
    private IntegerField expiry = new IntegerField("expiry");

    public TabbedFormDemo() {

        form.setBackgroundColor("#FFFABF");
        form.setTabHeight("210px");
        form.setTabWidth("420px");
        form.setErrorsPosition(Form.POSITION_TOP);

        // Contact tab sheet

        FieldSet contactTabSheet = new FieldSet("contactDetails");
        form.addTabSheet(contactTabSheet);

        contactTabSheet.add(new TitleSelect("title"));

        contactTabSheet.add(new TextField("firstName"));

        contactTabSheet.add(new TextField("middleNames"));

        contactTabSheet.add(new TextField("surname", true));

        contactTabSheet.add(contactNumber);

        contactTabSheet.add(new EmailField("email"));

        // Delivery tab sheet

        FieldSet deliveryTabSheet = new FieldSet("deliveryDetails");
        form.addTabSheet(deliveryTabSheet);

        TextArea textArea = new TextArea("deliveryAddress", true);
        textArea.setCols(30);
        textArea.setRows(3);
        deliveryTabSheet.add(textArea);

        deliveryTabSheet.add(new DateField("deliveryDate"));

        PackagingRadioGroup packaging = new PackagingRadioGroup("packaging");
        packaging.setValue("STD");
        deliveryTabSheet.add(packaging);

        deliveryTabSheet.add(telephoneOnDelivery);

        // Payment tab sheet

        FieldSet paymentTabSheet = new FieldSet("paymentDetails");
        form.addTabSheet(paymentTabSheet);

        paymentGroup.add(new Radio("cod", "Cash On Delivery "));
        paymentGroup.add(new Radio("credit", "Credit Card "));
        paymentGroup.setVerticalLayout(false);
        paymentTabSheet.add(paymentGroup);

        paymentTabSheet.add(cardName);
        paymentTabSheet.add(cardNumber);
        paymentTabSheet.add(expiry);
        expiry.setSize(4);
        expiry.setMaxLength(4);

        // Buttons

        form.add(new Submit("ok", "   OK   ",  this, "onOkClick"));
        form.add(new PageSubmit("cancel", HomePage.class));

        addControl(form);
    }

    public boolean onOkClick() {
        if (isFormValid()) {
            processDelivery();
        }
        return true;
    }

    /**
     * Perform additional form cross field validation returning true if valid.
     *
     * @return true if form is valid after cross field validation
     */
    protected boolean isFormValid() {
        if (telephoneOnDelivery.isChecked()) {
            contactNumber.setRequired(true);
            contactNumber.validate();
        }

        if (paymentGroup.getValue().equals("credit")) {
            cardName.setRequired(true);
            cardName.validate();
            cardNumber.setRequired(true);
            cardNumber.validate();
            expiry.setRequired(true);
            expiry.validate();
        }

        return form.isValid();
    }

    private void processDelivery() {
        List fieldList = ClickUtils.getFormFields(form);
        for (Iterator i = fieldList.iterator(); i.hasNext(); ) {
            Field field = (Field) i.next();
            System.out.println(field.getName() + "=" + field.getValue());
        }
    }
}
