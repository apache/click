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
import org.apache.click.extras.panel.ListPanel;
import org.springframework.stereotype.Component;

/**
 * Provides an ListPanel demonstration.
 * <p/>
 * Please note the ListPanel control will be automatically add to the Page using
 * the fields name "listPanel".
 */
@Component
public class ListPanelDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private ListPanel listPanel = new ListPanel("listPanel");

    @Resource(name="customerService")
    private CustomerService customerService;

    public ListPanelDemo() {
        addControl(listPanel);
        listPanel.add(new Panel("panel1", "/panel/customersPanel1.htm"));
        listPanel.add(new Panel("panel2", "/panel/customersPanel2.htm"));
        listPanel.add(new Panel("panel3", "/panel/customersPanel3.htm"));
    }

    /**
     * @see org.apache.click.Page#onRender()
     */
    @Override
    public void onRender() {
        List<Customer> customers = customerService.getCustomersSortedByName(12);
        addModel("customers", customers);
    }

}
