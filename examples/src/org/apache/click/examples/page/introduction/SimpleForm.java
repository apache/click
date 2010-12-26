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
package org.apache.click.examples.page.introduction;

import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides a simple Form example Page.
 * <p/>
 * Note the public scope Form control field is automatically added to the Page's
 * list of controls and the String msg field is automatically added to the
 * Page's model.
 * <p/>
 * The form <tt>onSubmit</tt> control listener is invoked when the submit button
 * is clicked.
 */
public class SimpleForm extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    // Constructor ------------------------------------------------------------

    public SimpleForm() {
        addControl(form);

        form.add(new TextField("name", true));
        form.add(new Submit("OK"));

        form.setListener(this, "onSubmit");
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * Handle the form submit event.
     */
    public boolean onSubmit() {
        if (form.isValid()) {
            String msg = "Your name is " + form.getFieldValue("name");
            addModel("msg", msg);
        }
        return true;
    }

}
