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

import org.apache.click.control.Panel;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.panel.TabbedPanel;
import org.springframework.stereotype.Component;

/**
 * Provides an TabbedPanel demonstration.
 */
@Component
public class TabbedPanelDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");
    private List<Customer> customers;

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public TabbedPanelDemo() {
        addControl(tabbedPanel);

        Panel panel1 = new Panel("panel1", "panel/customersPanel1.htm");
        panel1.setLabel("The First Panel");
        tabbedPanel.add(panel1);

        Panel panel2 = new Panel("panel2", "panel/customersPanel2.htm");
        panel2.setLabel("The Second Panel");
        tabbedPanel.add(panel2);

        Panel panel3 = new Panel("panel3", "panel/customersPanel3.htm");
        panel3.setLabel("The Third Panel");
        tabbedPanel.add(panel3);

        // Register a listener that is notified when a different panel is selected.
        tabbedPanel.setTabListener(this, "onTabClick");
    }

    // Event Handlers ---------------------------------------------------------

    public boolean onTabClick() {
        System.out.println("Tab Clicked");
        return true;
    }

    /**
     * @see org.apache.click.Page#onRender()
     */
    @Override
    public void onRender() {
        customers = customerService.getCustomersSortedByName(12);
        addModel("customers", customers);
    }

}
