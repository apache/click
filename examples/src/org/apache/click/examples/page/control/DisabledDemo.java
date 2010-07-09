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
package org.apache.click.examples.page.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.cayenne.access.DataContext;
import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.Checkbox;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.control.InvestmentSelect;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.springframework.stereotype.Component;

/**
 * Demonstrates disabled field behavior.
 *
 * Note the following:
 *
 * - disabled fields are not processed and validated
 * - disabled field values are not copied to domain objects
 * - disabled fields that are enabled through JavaScript, will be enabled
 */
@Component
public class DisabledDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    private FieldSet fieldSet = new FieldSet("customer");

    private TextField nameField = new TextField("name", true);

    private InvestmentSelect investmentSelect = new InvestmentSelect("investments", true);

    // This checkbox will enable/disable the form fields
    private Checkbox toggle = new Checkbox("toggle", "Enable Fields");

    private Submit submit = new Submit("save");

    @Resource(name="customerService")
    private CustomerService customerService;

    @Override
    public void onInit() {
        super.onInit();

        final Customer customer = loadCustomer();

        form.add(fieldSet);

        // Disable fields
        nameField.setDisabled(true);
        fieldSet.add(nameField);

        investmentSelect.setDisabled(true);
        fieldSet.add(investmentSelect);

        fieldSet.add(toggle);

        fieldSet.add(submit);
        submit.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {

                if (form.isValid()) {
                    // Copy field values to customer
                    form.copyTo(customer);
                    DataContext.getThreadObjectContext().commitChanges();
                }
                return true;
            }
        });

        addControl(form);

        // Populate field values from customer
        form.copyFrom(customer);

        addModel("customer", customer);
    }

    public Customer loadCustomer() {
        boolean ascending = true;
        return customerService.getCustomersSortedBy(Customer.DATE_JOINED_PROPERTY,
            ascending).get(0);
    }

    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();
            headElements.add(new JsImport("/assets/js/jquery-1.4.2.js"));

            Map<String, Object> jsModel = new HashMap<String, Object>();
            headElements.add(new JsScript("/control/disabled-demo.js", jsModel));
        }
        return headElements;
    }
}
