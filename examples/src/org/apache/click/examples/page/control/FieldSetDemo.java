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
package org.apache.click.examples.page.control;

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
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.CreditCardField;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.IntegerField;
import org.apache.click.extras.control.PageSubmit;
import org.apache.click.util.ContainerUtils;

/**
 * Provides a form FieldSet example.
 */
public class FieldSetDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    // Payment options
    private RadioGroup paymentGroup;
    private TextField cardHolder;
    private CreditCardField cardNumber;
    private IntegerField cardExpiry;

    // Constructor -----------------------------------------------------------

    public FieldSetDemo() {
        addControl(form);

        form.setLabelAlign(Form.ALIGN_RIGHT);
        form.setLabelStyle("width:11em;");
        form.setFieldStyle("width:22em;");

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

        PackagingRadioGroup radioGroup = new PackagingRadioGroup("packaging");
        radioGroup.setValue("STD");
        radioGroup.setVerticalLayout(true);
        deliveryFieldSet.add(radioGroup);

        deliveryFieldSet.add(new Checkbox("telephoneOnDelivery"));

        // Payment fieldset

        FieldSet paymentFieldSet = new FieldSet("paymentDetails");
        form.add(paymentFieldSet);

        paymentGroup = new RadioGroup("paymentOption", true);
        paymentGroup.add(new Radio("cod", "Cash On Delivery "));
        paymentGroup.add(new Radio("credit", "Credit Card "));
        paymentGroup.setVerticalLayout(false);
        paymentFieldSet.add(paymentGroup);

        cardHolder = new TextField("cardHolderName");
        paymentFieldSet.add(cardHolder);
        cardNumber = new CreditCardField("cardNumber");
        paymentFieldSet.add(cardNumber);
        cardExpiry = new IntegerField("expiry");
        cardExpiry.setSize(4);
        cardExpiry.setMaxLength(4);
        paymentFieldSet.add(cardExpiry);

        form.add(new Submit("ok", "  OK  ",  this, "onOkClick"));
        form.add(new PageSubmit("cancel", HomePage.class));
    }

    // Event Handlers ---------------------------------------------------------

    public boolean onOkClick() {
        if (isFormValid()) {
            for (Field field : ContainerUtils.getInputFields(form)) {
                System.out.println(field.getName() + "=" + field.getValue());
            }
        }
        return true;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Perform additional form cross field validation returning true if valid.
     *
     * @return true if form is valid after cross field validation
     */
    protected boolean isFormValid() {
        // If credit payment option is specified, ensure credit criteria is provided
        if (paymentGroup.getValue().equals("credit")) {
            cardHolder.setRequired(true);
            cardHolder.validate();
            cardNumber.setRequired(true);
            cardNumber.validate();
            cardExpiry.setRequired(true);
            cardExpiry.validate();
        }

        return form.isValid();
    }
}
