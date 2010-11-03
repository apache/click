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
import org.apache.click.control.HiddenField;

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
 * Provides a TabbedPanel demonstration, integrating a Form and Table.
 */
@Component
public class TabbedPanelWithControls extends BorderPage {

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

        // PLEASE NOTE: Form is addedd to the second Panel(at index 1), so when it
        // posts to the server it needs to send the index of it's own Panel so that the
        // TabbedPanel activates that Panel actives Panel at index 1. If we don't
        // cater for this, the form post will be handled by the Panel at index 0

        // When TabbedPanel detects the request Parameter 'tabPanelIndex', it
        // switches to the Panel at the given index.
        // we add a HiddenField called 'tabPanelIndex' set to index 1.
        //
        int tabIndex = 1;
        form.add(new HiddenField("tabPanelIndex", tabIndex));

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

        // PLEASE NOTE: Table is addedd to the third Panel (at index 2), so when
        // the table is sorted or paged, it needs to send the index of it's Panel
        // so that the TabbedPanel activates Panel at index 2. If we don't cater
        // for this, the table sorting/paging request will be handled by the Panel
        // with index 0
        // When TabbedPanel detects the request Parameter 'tabPanelIndex', it
        // switches to the Panel at the given index.
        // we add a parameter called 'tabPanelIndex' to the table link
        tabIndex = 2;
        table.getControlLink().setParameter("tabPanelIndex", tabIndex);

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
