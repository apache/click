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
package net.sf.click.examples.page.panel;

import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Panel;
import net.sf.click.control.Submit;
import net.sf.click.control.Table;
import net.sf.click.control.TextField;
import net.sf.click.examples.page.BorderPage;

import org.apache.commons.lang.StringUtils;

/**
 * Demonstrates usage of the Panel Column Control.
 *
 * @author rlecheta
 */
public class PanelColumnDemo extends BorderPage {

    public Panel panel = new Panel("panel", "/panel/customerDetailsPanel.htm");
    public Form form = new Form();
    public String nameSearch;
    public Table table = new Table("table");

    private TextField textName = new TextField("name", true);

    // ------------------------------------------------------------ Constructor

    public PanelColumnDemo() {
        form.setMethod("get");
        form.add(textName);
        textName.setFocus(true);
        form.add(new Submit("search", " Search ", this, "onSearch"));

        // The name of the PanelColumn is "customer" thus ${customer}
        // variable will be available in the template
        table.addColumn(new PanelColumn("customer", panel));
        table.setPageSize(3);
    }

    // --------------------------------------------------------- Event Handlers

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

    /**
     * @see net.sf.click.Page#onGet()
     */
    public void onGet() {
        if (StringUtils.isNotEmpty(nameSearch)) {

            // Just fill the value so the user can see it
            textName.setValue(nameSearch);

            // And fill the table again.
            processSearch(nameSearch);
        }
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Search the Customer by name and create the Table control
     *
     * @param value
     */
    private void processSearch(String value) {
        // Search for user entered value
        List list = getCustomerService().getCustomersForName(value);

        table.setRowList(list);

        // Set the parameter in the pagination link,
        // so in the next page, we can fill the table again.
        table.getControlLink().setParameter("nameSearch", value);
    }
}
