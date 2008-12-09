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

import net.sf.click.control.Panel;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.panel.ListPanel;

/**
 * Provides an ListPanel demonstration.
 * <p/>
 * Please note the ListPanel control will be automatically add to the Page using
 * the fields name "listPanel".
 *
 * @author Phil Barnes
 */
public class ListPanelDemo extends BorderPage {

    public ListPanel listPanel = new ListPanel();
    public List customers;

    public ListPanelDemo() {
        listPanel.add(new Panel("panel1", "/panel/customersPanel1.htm"));
        listPanel.add(new Panel("panel2", "/panel/customersPanel2.htm"));
        listPanel.add(new Panel("panel3", "/panel/customersPanel3.htm"));
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        customers = getCustomerService().getCustomersSortedByName(12);
    }

}
