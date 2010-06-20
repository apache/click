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
package org.apache.click.examples.control;

import org.apache.click.control.Form;
import org.apache.click.control.Panel;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.DoubleField;

/**
 * Provides a reusable Panel to capture Client details.
 */
public class ClientPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    public ClientPanel(String name) {
        super(name);
    }

    @Override
    public void onInit() {
        form.add(new TextField("name")).setRequired(true);
        form.add(new DateField("dateJoined"));
        form.add(new DoubleField("holdings"));

        form.add(new Submit("save", this, "onSave"));
        form.add(new Submit("cancel", this, "onCancel"));

        add(form);

        // Invoke super onInit AFTER controls have been added to Panel, otherwise
        // the controls will not be reachable from the Panel and their onInit
        // event won't be invoked.
        super.onInit();
    }

    public boolean onSave() {
        if (form.isValid()) {
            // In real app one would store client in database
            addModel("msg", "Successfully created new Client: '"
                + form.getFieldValue("name") + "'.");
        }
        return true;
    }

    public boolean onCancel() {
        addModel("msg", "Cancelled.");

        return true;
    }
}
