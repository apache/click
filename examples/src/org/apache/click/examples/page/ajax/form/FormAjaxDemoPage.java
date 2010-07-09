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
package org.apache.click.examples.page.ajax.form;

import java.util.HashMap;
import java.util.List;
import org.apache.click.Control;
import org.apache.click.Partial;
import org.apache.click.ajax.AjaxBehavior;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.IntegerField;

/**
 * Basic Form Ajax Demo example using the jQuery JavaScript library.
 */
public class FormAjaxDemoPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    private TextField nameField = new TextField("name", true);
    private IntegerField ageField = new IntegerField("age");
    private DateField dateField = new DateField("date");

    private Submit save = new Submit("save");
    private Submit cancel = new Submit("cancel");

    public FormAjaxDemoPage() {
        addControl(form);
        form.add(nameField);
        form.add(ageField);
        form.add(dateField);
        form.add(save);
        form.add(cancel);

        save.addBehavior(new AjaxBehavior() {

            @Override
            public Partial onAction(Control source) {
                // Update the form which might contain errors
                return new Partial(form.toString(), Partial.HTML);
            }
        });

        cancel.addBehavior(new AjaxBehavior() {

            @Override
            public Partial onAction(Control source) {
                // Update the form and ensure errors and values have been cleared
                form.clearValues();
                form.clearErrors();
                return new Partial(form.toString(), Partial.HTML);
            }
        });

        // NOTE: we add a Behavior to Form so that Click registers the Form as an Ajax target
        // ALSO NOTE: we don't implement the onAction method as the save and cancel Submits
        // handles the Behavior action event
        form.addBehavior(new AjaxBehavior());
    }

    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();
            headElements.add(new JsImport("/assets/js/jquery-1.4.2.js"));
            headElements.add(new JsScript("/ajax/form/form-ajax-demo.js", new HashMap()));
        }
        return headElements;
    }
}
