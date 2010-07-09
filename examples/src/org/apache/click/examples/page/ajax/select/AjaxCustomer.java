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
package org.apache.click.examples.page.ajax.select;

import javax.annotation.Resource;

import org.apache.click.Page;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.util.Bindable;
import org.springframework.stereotype.Component;

/**
 * Retrieves the Customer details using the given customerId parameter and
 * adds it to the Page model.
 * <p>
 * This Page class is configured to return its content type as "text/xml". Note
 * this can also be done by configuring a &lt;header&gt; element:
 *
 * <pre class="codeConfig">
 * &lt;page path="ajax-customer.htm" classname="examples.page.AjaxCustomer"&gt;
 *    &lt;header name="Content-Type" value="text/xml"/&gt;
 * &lt;/page&gt;
 * </pre>
 */
@Component
public class AjaxCustomer extends Page {

    private static final long serialVersionUID = 1L;

    @Bindable protected Customer customer;

    @Resource(name="customerService")
    private CustomerService customerService;

    /**
     * Process the AJAX request and return XML customer table.
     *
     * @see Page#onGet()
     */
    @Override
    public void onGet() {
        String customerId = getContext().getRequest().getParameter("customerId");

        customer = customerService.findCustomerByID(customerId);
    }

    /**
     * Ensure the Http response Content-type is "text/html; charset=UTF-8".
     *
     * @see Page#getContentType()
     */
    @Override
    public String getContentType() {
        return "text/html; charset=UTF-8";
    }

}
