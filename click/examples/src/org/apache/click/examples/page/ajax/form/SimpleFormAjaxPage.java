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
import org.apache.click.ActionResult;
import org.apache.click.ControlRegistry;
import org.apache.click.ajax.DefaultAjaxBehavior;
import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.page.BorderPage;

/**
 * Simple Form Ajax Demo example using the jQuery JavaScript library.
 */
public class SimpleFormAjaxPage extends BorderPage {

    private Form form = new Form("form");
    private Field nameFld = new TextField("name");
    private Submit saveBtn = new Submit("save");

    public SimpleFormAjaxPage() {
        addControl(form);
        form.add(nameFld);
        form.add(saveBtn);

        saveBtn.addBehavior(new DefaultAjaxBehavior() {

            @Override
            public ActionResult onAction(Control source) {
                // Return a success response
                // Form data can be saved here
                return new ActionResult("Hello " + nameFld.getValue(), ActionResult.HTML);
            }
        });

        // NOTE: we explicitly register the Form as an Ajax target so that the
        // Fom#onProcess method can be invoked. The save button's Behavior will
        // still handle the request though
        ControlRegistry.registerAjaxTarget(form);

        // Instead of explicitly registering the Form, the same can be achived by
        // adding an empty Behavior to the Form so that Click register the Form
        // as an Ajax target:
        // form.addBehavior(new DefaultAjaxBehavior());
    }

    /**
     * Add the jQuery and page JavaScript template to the Page HEAD elements.
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();
            headElements.add(new JsImport("/assets/js/jquery-1.4.2.js"));
            headElements.add(new JsScript("/ajax/form/simple-form-ajax.js", new HashMap()));
        }
        return headElements;
    }
}
