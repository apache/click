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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import org.apache.click.Page;
import org.apache.click.control.Checkbox;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.EmailField;
import org.springframework.stereotype.Component;

/**
 * Provides an advanced form example.
 */
@Component
public class AdvancedForm extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    private Select investmentSelect = new Select("investments");

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public AdvancedForm() {
        addControl(form);

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

    // Event Handlers ---------------------------------------------------------

    /**
     * @see Page#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();

        investmentSelect.setDefaultOption(Option.EMPTY_OPTION);
        investmentSelect.setDataProvider(new DataProvider() {

            public List<Option> getData() {
                List<Option> options = new ArrayList<Option>();
                for (String category : customerService.getInvestmentCategories()) {
                    options.add(new Option(category));
                }
                return options;
            }
        });
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

            customerService.saveCustomer(customer);

            form.clearValues();

            String msg = "A new customer record has been created.";
            addModel("msg", msg);
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
