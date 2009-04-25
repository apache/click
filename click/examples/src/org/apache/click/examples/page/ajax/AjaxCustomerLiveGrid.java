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
package org.apache.click.examples.page.ajax;

import java.util.List;

import javax.annotation.Resource;

import org.apache.click.Page;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.service.CustomerService;
import org.springframework.stereotype.Component;

/**
 * Retrieves the current page and "buffered" Customer list using the given offset
 * and page_size parameters and adds them to the Page model.
 * <p/>
 * This Page class is configured to return its content type as "text/xml". Note
 * this can also be done by configuring a &lt;header&gt; element:
 * <p/>
 * <pre class="codeConfig">
 * &lt;page path="ajax-customer-live-grid.htm" classname="examples.page.AjaxCustomerLiveGrid"&gt;
 * &lt;header name="Content-Type" value="text/xml"/&gt;
 * &lt;/page&gt; </pre>
 *
 * @author Phil Barnes
 */
@Component
public class AjaxCustomerLiveGrid extends Page {

    @Resource(name="customerService")
    private CustomerService customerService;

    /**
     * Process the AJAX request and return XML customer table.  This method
     * retreives the "buffered" results for the live grid component, starting
     * with the current displayed page + the "page_size" number of additional
     * results for smooth scrolling and instant result display purposes
     *
     * @see org.apache.click.Page#onGet()
     */
    public void onGet() {
        String offset = getContext().getRequest().getParameter("offset");
        String pageSize = getContext().getRequest().getParameter("page_size");

        if (offset != null && pageSize != null) {
            List<Customer> customers =
                customerService.getCustomersForPage(Integer.parseInt(offset),
                                                    Integer.parseInt(pageSize));

            // Add the BUFFERED paginated results to the model for XML response
            addModel("customers", customers);

            // Add the offset back so we know where to start numbering the results
            addModel("offset", new Integer(offset));
        }
    }

    /**
     * Ensure the Http response Content-type is "text/xml".
     *
     * @see org.apache.click.Page#getContentType()
     */
    public String getContentType() {
        return "text/xml";
    }
}
