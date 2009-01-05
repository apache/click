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

import java.util.Iterator;
import java.util.List;

import org.apache.click.control.Checkbox;
import org.apache.click.control.Field;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
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
import org.apache.click.util.ClickUtils;

/**
 * Provides a form FieldSet example.
 *
 * @author Malcolm Edgar
 */
public class FieldSetDemo extends BorderPage {

    public Form form = new Form();

    public FieldSetDemo() {
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

        paymentFieldSet.add(new TextField("cardName"));
        paymentFieldSet.add(new CreditCardField("cardNumber"));
        IntegerField expiryField = new IntegerField("expiry");
        expiryField.setSize(4);
        expiryField.setMaxLength(4);
        paymentFieldSet.add(expiryField);

        form.add(new Submit("ok", "  OK  ",  this, "onOkClick"));
        form.add(new PageSubmit("cancel", HomePage.class));
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
}
