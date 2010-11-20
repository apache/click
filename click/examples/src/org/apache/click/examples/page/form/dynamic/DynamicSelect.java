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
package org.apache.click.examples.page.form.dynamic;

import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.control.DoubleField;
import org.apache.click.util.ClickUtils;

/**
 * Demonstrates dynamic Form behavior using a Select control.
 */
public class DynamicSelect extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("dynamicForm");

    private TextField nameField = new TextField("name", true);

    private Select select = new Select("investments", true);

    private Submit submit = new Submit("ok");

    // Investment options
    private static final String PROPERTY = "Property";
    private static final String STOCKS = "Stocks";
    private static final String[] INVESTMENTS = {PROPERTY, STOCKS};

    // Constructor ------------------------------------------------------------

    public DynamicSelect() {

        form.add(nameField);

        select.add(Option.EMPTY_OPTION);
        select.addAll(INVESTMENTS);
        form.add(select);

        // NB: when using form.submit() the submit button cannot be
        // called 'submit'. If it is, the browser is likely to throw a JS exception.
        // In this demo the submit button is called 'ok'.
        select.setAttribute("onchange", "form.submit();");

        form.add(submit);

        addControl(form);

        // Bind the form field request values
        ClickUtils.bind(form);

        if (STOCKS.equals(select.getValue())) {
            form.add(new DoubleField("amount", true));
        } else if (PROPERTY.equals(select.getValue())) {
            FieldSet address = new FieldSet("address");
            address.add(new TextField("street", true));
            address.add(new DoubleField("amount", true));
            form.add(address);
        }

        // When checkbox is checked and form is submitted, we don't want to validate
        // the partially filled in form
        if(form.isFormSubmission() && !submit.isClicked()) {
            form.setValidate(false);
            addModel("msg", "Validation is bypassed");
        }

        submit.setActionListener(new ActionListener() {
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                if (form.isValid()) {
                addModel("msg", "Form is valid after validation");
            }
                return true;
            }
        });
    }
}
