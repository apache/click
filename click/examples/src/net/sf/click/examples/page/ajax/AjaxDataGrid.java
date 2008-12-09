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
package net.sf.click.examples.page.ajax;

import java.util.List;

import net.sf.click.examples.page.BorderPage;

/**
 * Provides an Ajax demo of Rico's LiveGrid.
 *
 * @author Phil Barnes
 */
public class AjaxDataGrid extends BorderPage {

    public String headInclude = "ajax/ajax-data-grid-include.htm";

    public void onInit() {
        super.onInit();

        List customerList = getCustomerService().getCustomers();
        addModel("customers", customerList);

        addModel("totalRows", new Integer(customerList.size()));

        // Always start at a 0 offset
        addModel("offset", new Integer(0));
    }

}
