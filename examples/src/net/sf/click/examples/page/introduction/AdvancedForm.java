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
package net.sf.click.examples.page.introduction;

import net.sf.click.Page;
import net.sf.click.control.Checkbox;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.examples.service.CustomerService;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.EmailField;

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
