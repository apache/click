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
package org.apache.click.examples.page.velocity;

import org.apache.click.control.Form;
import org.apache.click.control.Reset;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.control.InvestmentSelect;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.DoubleField;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.IntegerField;
import org.apache.click.extras.control.PageSubmit;

/**
 * Provides a Velocity Macro example.
 */
public class VelocityMacro extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    public VelocityMacro() {
        addControl(form);
        TextField nameField = new TextField("name", true);
        nameField.setMinLength(5);
        nameField.setTitle("Customer full name");
        nameField.setFocus(true);
        form.add(nameField);

        EmailField emailField = new EmailField("email", true);
        emailField.setTitle("Customers email address");
        form.add(emailField);

        IntegerField ageField = new IntegerField("age");
        ageField.setMinValue(1);
        ageField.setMaxValue(120);
        form.add(ageField);

        DoubleField holdingsField = new DoubleField("holdings", true);
        holdingsField.setTitle("Total investment holdings");
        form.add(holdingsField);

        form.add(new InvestmentSelect("investments"));

        form.add(new Submit("ok", " OK "));
        form.add(new PageSubmit("cancel", HomePage.class));
        form.add(new Reset("reset"));
    }

}
