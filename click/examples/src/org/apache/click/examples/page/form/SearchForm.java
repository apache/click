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
package org.apache.click.examples.page.form;

import java.util.List;

import org.apache.click.control.Form;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides a search form example demonstrating how to layout a form manually
 * in the page template.
 *
 * @author Malcolm Edgar
 */
public class SearchForm extends BorderPage {

    public Form form = new Form();

    private TextField textField;
    private Select typeSelect;

    public SearchForm() {
        textField = new TextField("search");
        form.add(textField);

        typeSelect = new Select("type");
        typeSelect.addAll(new String[] {"ID", "Name", "Age"});
        typeSelect.setValue("Name");
        typeSelect.setStyle("font-size", "9pt");
        form.add(typeSelect);

        form.add(new Submit("go", " Go "));
    }

    /**
     * @see org.apache.click.Page#onPost()
     */
    public void onPost() {
        Customer customer = null;
        String value = textField.getValue().trim();
        String type = typeSelect.getValue().toLowerCase();

        if (type.equals("id")) {
            customer = getCustomerService().findCustomerByID(value);
        }
        else if (type.equals("name")) {
            List list = getCustomerService().getCustomersForName(value);
            if (!list.isEmpty()) {
                customer = (Customer) list.get(0);
            }
        }
        else if (type.equals("age")) {
            List list = getCustomerService().getCustomersForAge(value);
            if (!list.isEmpty()) {
                customer = (Customer) list.get(0);
            }
        }

        if (customer != null) {
            addModel("customerDetail", customer);
        }
        else {
            addModel("message", "Customer not found");
        }
    }
}
