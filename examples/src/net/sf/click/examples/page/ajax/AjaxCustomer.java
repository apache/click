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

import net.sf.click.Page;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.SpringPage;

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
 *
 * @author Malcolm Edgar
 */
public class AjaxCustomer extends SpringPage {

    public Customer customer;

    /**
     * Process the AJAX request and return XML customer table.
     *
     * @see Page#onGet()
     */
    public void onGet() {
        String customerId = getContext().getRequest().getParameter("customerId");

        customer = getCustomerService().findCustomerByID(customerId);
    }

    /**
     * Ensure the Http response Content-type is "text/xml".
     *
     * @see Page#getContentType()
     */
    public String getContentType() {
        return "text/xml; charset=UTF-8";
    }

}
