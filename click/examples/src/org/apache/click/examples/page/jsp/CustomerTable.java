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
package org.apache.click.examples.page.jsp;

import java.util.List;

import javax.annotation.Resource;

import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.springframework.stereotype.Component;

/**
 * Provides JSP Page example where a JSP page and JSP border template is used to
 * render a table.
 */
@Component
public class CustomerTable extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Resource(name="customerService")
    private CustomerService customerService;

    // Event Handlers ---------------------------------------------------------

    /**
     * @see org.apache.click.Page#onRender()
     */
    @Override
    public void onRender() {
        List<Customer> customers = customerService.getCustomersSortedByName(10);
        addModel("customers", customers);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.jsp"</tt>
     *
     * @see org.apache.click.Page#getTemplate()
     */
    @Override
    public String getTemplate() {
        return "/border-template.jsp";
    }
}
