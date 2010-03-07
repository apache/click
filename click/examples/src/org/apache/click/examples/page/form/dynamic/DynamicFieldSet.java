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
import org.apache.click.control.Checkbox;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.IntegerField;
import org.apache.click.util.ClickUtils;

/**
 * Demonstrates dynamic Form behavior.
 */
public class DynamicFieldSet extends BorderPage {

    private Form form = new Form("dynamicForm");

    private FieldSet customerFS = new FieldSet("customer");

    private FieldSet addressFS = new FieldSet("address");

    private TextField nameField = new TextField("name", true);

    private IntegerField ageField = new IntegerField("age");

    private Checkbox addressChk = new Checkbox("address");

    private EmailField emailField = new EmailField("email", true);

    private Submit submit = new Submit("ok");

    @Override
    public void onInit() {
        super.onInit();

        form.add(customerFS);
        nameField.setLabelStyleClass("label-style");
        nameField.setParentStyleClass("parent-style");
        nameField.setLabelStyle("color: blue; font-weight: bold");
        nameField.setParentStyle("padding-bottom: 20px");
        customerFS.add(nameField);
        customerFS.add(ageField);
        customerFS.add(addressChk);

        // The Click script, '/click/control.js', provides the JavaScript
        // function Click.submit(formName, validate). To bypass validation
        // specify 'false' as the second argument.
        addressChk.setAttribute("onclick", "Click.submit(dynamicForm, false)");

        form.add(submit);

        addControl(form);

        // NB: Bind and validate form fields *before* the onProcess event,
        // enabling us to inspect Field values in the onInit event or even
        // the Page constructor
        boolean valid = ClickUtils.bindAndValidate(form);

        // We can safely check whether the user checked the addressChk
        if (addressChk.isChecked()) {
            // Dynamically add a new Field and FieldSet to the form
            addressFS.add(emailField);
            form.add(addressFS);
        }

        form.setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                return onFormSubmit();
            }
        });
    }

    public boolean onFormSubmit() {
        // onFormSubmit listens on Form itself and will be invoked whenever the
        // form is submitted.
        if (form.isValid()) {

            // Check isBypassValidation() flag whether the form validation occurred
            if (form.isBypassValidation()) {
                addModel("msg", "Validation bypassed");
            } else {
                addModel("msg", "Form is valid after validation");
            }
        }
        return true;
    }
}
