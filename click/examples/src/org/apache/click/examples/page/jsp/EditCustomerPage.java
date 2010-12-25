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
package org.apache.click.examples.page.jsp;

import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.DoubleField;
import org.apache.click.extras.control.EmailField;

/**
 * Demo a form submit using JSP as template.
 */
public class EditCustomerPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    // Constructor ------------------------------------------------------------

    public EditCustomerPage() {
        addControl(form);

        // Setup customers form
        FieldSet fieldSet = new FieldSet("customer");
        fieldSet.add(new TextField("name", true));
        fieldSet.add(new EmailField("email"));
        fieldSet.add(new DoubleField("holdings", true));
        fieldSet.add(new DateField("dateJoined"));
        form.add(fieldSet);
        form.add(new Submit("save", this, "onSaveClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));
    }

    // Event Handlers ---------------------------------------------------------

    @Override
    public boolean onSecurityCheck() {
        return form.onSubmitCheck(this, EditCustomerPage.class);
    }

    public boolean onSaveClick() {
        if (form.isValid()) {
            // Perform logic
            // Optionally forward to another Page for display:
            // setForward(ViewCustomersPage.class);
        }
        return true;
    }

    public boolean onCancelClick() {
        form.clearErrors();
        form.clearValues();
        return true;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.jsp"</tt>
     *
     * @see org.apache.click.Page#getTemplate()
     */
    @Override
    public String getTemplate() {
        return "/border-template.jsp";
    }
}
