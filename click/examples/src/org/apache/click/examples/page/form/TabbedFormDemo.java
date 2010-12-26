/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.examples.page.form;

import java.io.Serializable;

import org.apache.click.control.Checkbox;
import org.apache.click.control.Field;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Radio;
import org.apache.click.control.RadioGroup;
import org.apache.click.control.Submit;
import org.apache.click.control.TextArea;
import org.apache.click.control.TextField;
import org.apache.click.examples.control.PackagingRadioGroup;
import org.apache.click.examples.control.TitleSelect;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.examples.util.ExampleUtils;
import org.apache.click.extras.control.CreditCardField;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.IntegerField;
import org.apache.click.extras.control.PageSubmit;
import org.apache.click.extras.control.TabbedForm;
import org.apache.click.extras.control.TelephoneField;
import org.apache.click.util.ContainerUtils;

/**
 * Provides an TabbedForm control example.
 */
public class TabbedFormDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private TabbedForm form = new TabbedForm("form");
    private RadioGroup paymentGroup = new RadioGroup("paymentOption", true);
    private TelephoneField contactNumber = new TelephoneField("contactNumber");
    private Checkbox telephoneOnDelivery = new Checkbox("telephoneOnDelivery");
    private TextField cardName = new TextField("cardName");
    private CreditCardField cardNumber = new CreditCardField("cardNumber");
    private IntegerField expiry = new IntegerField("expiry");


    protected Form optionsForm = new Form("optionsForm");

    /** Form options holder. */
    public static class Options implements Serializable {
        static final long serialVersionUID = 1L;
        boolean javaScriptValidate = false;
    }

    private Checkbox jsValidate = new Checkbox("jsValidate", "JavaScript Validate");
    // Constructor ------------------------------------------------------------

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

        // Settings Form
        FieldSet fieldSet = new FieldSet("options", "Form Options");
        jsValidate.setAttribute("onclick", "form.submit();");
        fieldSet.add(jsValidate);
        optionsForm.add(fieldSet);
        optionsForm.setListener(this, "onOptionsSubmit");
        addControl(optionsForm);
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * @see org.apache.click.Page#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();

        applyOptions();
    }

    public boolean onOptionsSubmit() {
        Options options = new Options();
        options.javaScriptValidate = jsValidate.isChecked();
        ExampleUtils.setSessionObject(options);
        applyOptions();
        return true;
    }


    public boolean onOkClick() {
        if (isFormValid()) {
            processDelivery();
        }
        return true;
    }

    // Protected Methods ------------------------------------------------------

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

    protected void processDelivery() {
        for (Field field : ContainerUtils.getInputFields(form)) {
            System.out.println(field.getName() + "=" + field.getValue());
        }
    }

    private void applyOptions() {
        Options options = (Options) ExampleUtils.getSessionObject(Options.class);

        form.setJavaScriptValidation(options.javaScriptValidate);
        jsValidate.setChecked(options.javaScriptValidate);
    }
}
