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
package org.apache.click.examples.page.panel;

import java.util.List;

import javax.annotation.Resource;

import org.apache.click.control.Form;
import org.apache.click.control.Panel;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.util.Bindable;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Demonstrates usage of the Panel Column Control.
 */
@Component
public class PanelColumnDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Bindable protected String nameSearch;

    private Panel panel = new Panel("panel", "/panel/customerDetailsPanel.htm");
    private Form form = new Form("form");
    private Table table;

    private TextField textName = new TextField("name", true);

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public PanelColumnDemo() {
        addControl(panel);
        addControl(form);

        table = new Table("table") {
            @Override
            protected void renderHeaderRow(HtmlStringBuffer buffer) {
                // We don't want to render table columns so we override #renderHeaderRow
                // to do nothing
            }
        };
        addControl(table);

        form.add(textName);
        textName.setFocus(true);
        form.add(new Submit("search", " Search ", this, "onSearch"));

        // The name of the PanelColumn is "customer" thus ${customer}
        // variable will be available in the template
        table.addColumn(new PanelColumn("customer", panel));
        table.setPageSize(3);
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * Search button handler
     */
    public boolean onSearch() {
        if (form.isValid()) {
            String value = textName.getValue().trim();

            processSearch(value);

            return true;
        }
        return false;
    }

    @Override
    public void onPost() {
        handleRequest();
    }

    @Override
    public void onGet() {
        handleRequest();
    }

    // Private Methods --------------------------------------------------------

    private void handleRequest() {
        if (StringUtils.isNotEmpty(nameSearch)) {

            // Just fill the value so the user can see it
            textName.setValue(nameSearch);

            // And fill the table again.
            processSearch(nameSearch);
        }
    }

    /**
     * Search the Customer by name and create the Table control
     *
     * @param value
     */
    private void processSearch(String value) {
        // Search for user entered value
        List<Customer> list = customerService.getCustomersForName(value);

        table.setRowList(list);

        // Set the parameter in the pagination link,
        // so in the next page, we can fill the table again.
        table.getControlLink().setParameter("nameSearch", value);
    }
}
