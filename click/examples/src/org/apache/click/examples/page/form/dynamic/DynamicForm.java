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
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.control.EmailField;
import org.apache.click.util.ClickUtils;

/**
 * Demonstrates dynamic Form behavior.
 */
public class DynamicForm extends BorderPage {

    private Form form = new Form("dynamicForm");

    private TextField nameField = new TextField("name", true);

    private Checkbox checkbox = new Checkbox("subscribe");

    private Submit submit = new Submit("ok");

    @Override
    public void onInit() {
        super.onInit();

        form.add(nameField);
        form.add(checkbox);

        // The Click script, '/click/control.js', provides the JavaScript
        // function Click.submit(formName, validate). To bypass validation
        // specify 'false' as the second argument.
        checkbox.setAttribute("onclick", "Click.submit(dynamicForm, false)");

        form.add(submit);

        addControl(form);

        // NB: Bind the checkbox *before* the onProcess event, enabling us to
        // inspect the checkbox value inside the onInit event, or even the
        // page constructor
        ClickUtils.bind(checkbox);

        // We could also bind *all* form fields in one go
        // ClickUtils.bind(form);

        // We can safely check whether the user checked the checkbox
        if (checkbox.isChecked()) {
            // Dynamically add the email Field to the form
            form.add(new EmailField("email", true));
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
                addModel("msg", "Validation was bypassed");
            } else {
                addModel("msg", "Form is valid after validation");
            }
        }
        return true;
    }
}
