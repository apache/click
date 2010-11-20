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
 * Demonstrates dynamic Form behaviour.
 */
public class DynamicForm extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("dynamicForm");

    private TextField nameField = new TextField("name", true);

    private Checkbox checkbox = new Checkbox("subscribe");

    private Submit submit = new Submit("ok");

    // Event Handlers ---------------------------------------------------------

    @Override
    public void onInit() {
        super.onInit();

        form.setJavaScriptValidation(true);
        form.add(nameField);
        form.add(checkbox);

        // NB: when using form.submit() the submit button cannot be
        // called 'submit'. If it is, the browser is likely to throw a JS exception.
        checkbox.setAttribute("onclick", "form.submit();");

        form.add(submit);

        addControl(form);

        // NB: Bind the submit button. If it wasn't clicked it means the Form was submitted
        // using JavaScript and we don't want to validate yet
        ClickUtils.bind(submit);

        // If submit was not clicked, don't validate
        if(form.isFormSubmission() && !submit.isClicked()) {
            form.setValidate(false);
            addModel("msg", "Validation is bypassed");
        }

        // NB: Bind the checkbox *before* the onProcess event, enabling us to
        // inspect the checkbox value inside the onInit event
        ClickUtils.bind(checkbox);

        // We can now check whether the user checked the checkbox
        if (checkbox.isChecked()) {
            // Dynamically add the email Field to the form
            form.add(new EmailField("email", true));
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
