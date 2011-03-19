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
import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.Column;
import org.apache.click.control.Form;

import org.apache.click.control.Panel;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.panel.TabbedPanel;
import org.springframework.stereotype.Component;

/**
 * Provides a stateful TabbedPanel demo. The selected Tab will be
 * preserved between requests.
 */
@Component
public class StatefulTabbedPanelDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");
    private List<Customer> customers;

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    @Override
    public void onInit() {
        super.onInit();

        addControl(tabbedPanel);

        Panel panel1 = new Panel("panel1", "panel/tabbed/panel1.htm");
        tabbedPanel.add(panel1);

        Panel panel2 = new Panel("panel2", "panel/tabbed/form-panel2.htm");
        Form form = new Form("form");
        panel2.add(form);

        final TextField field = new TextField("name", "Enter your name");
        form.add(field);
        Submit submit = new Submit("go");
        submit.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                addModel("msg", "Hi " + field.getValue() + ". Your form has been saved!");
                return true;
            }
        });

        form.add(submit);

        panel2.setLabel("The Second Panel");
        tabbedPanel.add(panel2);

        Panel panel3 = new Panel("panel3", "panel/tabbed/table-panel3.htm");
        Table table = new Table("table");
        table = new Table("table");

        table.setClass(Table.CLASS_ITS);

        table.addColumn(new Column("id"));
        table.addColumn(new Column("name"));
        table.addColumn(new Column("email"));
        table.addColumn(new Column("investments"));
        table.setPageSize(5);
        table.setSortable(true);

        List list = customerService.getCustomersSortedByName(100);
        table.setRowList(list);

        panel3.add(table);

        tabbedPanel.add(panel3);

        // NOTE: Save the TabbedPanel state when a new panel is activated.
        // Register a listener that is notified when a different panel is selected.
        tabbedPanel.setTabListener(new ActionListener() {
            public boolean onAction(Control source) {
                // Save the TabbedPanel state to the session
                tabbedPanel.saveState(getContext());
                return true;
            }
        });

        // NOTE: Restore the TabbedPanel state. This will set the active panel from the
        // state that was saved from the TaListener above
        tabbedPanel.restoreState(getContext());
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * @see org.apache.click.Page#onRender()
     */
    @Override
    public void onRender() {
        customers = customerService.getCustomersSortedByName(12);
        addModel("customers", customers);
    }
}
