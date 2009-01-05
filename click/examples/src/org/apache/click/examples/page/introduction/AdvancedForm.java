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

import org.apache.click.Page;
import org.apache.click.control.Checkbox;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.EmailField;

/**
 * Provides an advanced form example.
 *
 * @author Malcolm Edgar
 */
public class AdvancedForm extends BorderPage {

    public Form form = new Form();
    public String msg;

    private Select investmentSelect = new Select("investment");

    // ------------------------------------------------------------ Constructor

    public AdvancedForm() {
        FieldSet fieldSet = new FieldSet("Customer");
        form.add(fieldSet);

        TextField nameField = new TextField("name", true);
        nameField.setMinLength(5);
        nameField.setFocus(true);
        fieldSet.add(nameField);

        fieldSet.add(new EmailField("email", true));

        fieldSet.add(investmentSelect);

        fieldSet.add(new DateField("dateJoined", true));
        fieldSet.add(new Checkbox("active"));

        form.add(new Submit("ok", " OK ", this, "onOkClicked"));
        form.add(new Submit("cancel", this, "onCancelClicked"));
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * @see Page#onInit()
     */
    public void onInit() {
        super.onInit();

        CustomerService customerService = getCustomerService();
        investmentSelect.add(Option.EMPTY_OPTION);
        investmentSelect.addAll(customerService.getInvestmentCatetories());
    }

    /**
     * Handle the OK button click event.
     *
     * @return true
     */
    public boolean onOkClicked() {
        if (form.isValid()) {
            Customer customer = new Customer();
            form.copyTo(customer);

            getCustomerService().saveCustomer(customer);

            form.clearValues();

            msg = "A new customer record has been created.";
        }
        return true;
    }

    /**
     * Handle the Cancel button click event.
     *
     * @return false
     */
    public boolean onCancelClicked() {
        setRedirect(HomePage.class);
        return false;
    }

}
