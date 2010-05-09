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

import org.apache.click.control.Radio;
import org.apache.click.control.RadioGroup;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.control.TitleSelect;
import org.apache.click.examples.control.html.cssform.VerticalFieldSet;
import org.apache.click.examples.control.html.cssform.CssForm;
import org.apache.click.examples.control.html.list.ListItem;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.control.CreditCardField;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.IntegerField;
import org.apache.click.extras.control.PageSubmit;

/**
 * This page demonstrates how to programmatically layout a form using
 * custom Controls and CSS.
 */
public class ContactDetailsPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private CssForm form;

    @Override
    public void onInit() {
        // Ensure the super implementation executes
        super.onInit();

        form = new CssForm("form");

        VerticalFieldSet fieldset = new VerticalFieldSet("contactDetails");
        fieldset.setLegend("Contact Details");

        fieldset.add(new TitleSelect("title"));
        fieldset.add(new TextField("firstName")).setRequired(true);
        fieldset.add(new TextField("middleNames"));
        fieldset.add(new TextField("lastName")).setRequired(true);
        fieldset.add(new TextField("contactNumber"));
        fieldset.add(new EmailField("email"));

        form.add(fieldset);

        fieldset = new VerticalFieldSet("paymentDetails");
        fieldset.setLegend("Payment Details");

        RadioGroup paymentGroup = new RadioGroup("paymentOption");
        paymentGroup.add(new Radio("cod", "Cash On Delivery "));
        paymentGroup.add(new Radio("credit", "Credit Card "));
        paymentGroup.setVerticalLayout(false);
        fieldset.add(paymentGroup);

        // Retrieve the paymentGroup's ListItem, and set its CSS class to "radio"
        ListItem item = fieldset.getHtmlList().getLast();
        item.setAttribute("class", "radio");

        fieldset.add(new TextField("cardholderName"));
        fieldset.add(new CreditCardField("cardNumber")).setRequired(true);
        fieldset.add(new IntegerField("expiry"));

        form.add(fieldset);

        Submit ok = new Submit("ok", "OK");
        Submit cancel = new PageSubmit("cancel", ContactDetailsPage.class);

        form.add(ok);
        form.add(cancel);

        addControl(form);
    }
}
