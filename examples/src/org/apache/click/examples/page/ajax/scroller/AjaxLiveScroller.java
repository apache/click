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
package org.apache.click.examples.page.ajax.scroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.click.ActionResult;

import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.util.ClickUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

/**
 * Demonstrates how to to dynamically load more customer while scrolling down
 * the Page. An Ajax request is made to a pageAction that sends back the new
 * customers to display.
 */
@Component
public class AjaxLiveScroller extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Resource(name="customerService")
    private CustomerService customerService;

    // Specifies the number of customers to retrieve at a time
    private int pageSize = 10;

    @Override
    public void onInit() {
        super.onInit();
        int offset = 0;
        List<Customer> customers = loadCustomers(offset, pageSize);
        addModel("customers", customers);
    }

    /**
     * A pageAction method that is called when the user scrolled to the bottom
     * of the page and more data is needed.
     */
    public ActionResult onScroll() {
        // Check if the offset parameter was received.
        int offset = NumberUtils.toInt(getContext().getRequest().getParameter("offset"));

        List<Customer> customers = loadCustomers(offset, pageSize);

        // Return customers between the given offset and pageSize
        Map actionResultModel = new HashMap();
        actionResultModel.put("customers", customers);
        actionResultModel.put("format", getFormat());

        // Return an action result for the customers.htm template and customers
        ActionResult actionResult = new ActionResult("/ajax/scroller/customers.htm", actionResultModel, ActionResult.HTML);
        return actionResult;
    }

    @Override
    public List<Element> getHeadElements() {
        // Lazily load head elements and ensure they are only loaded once
        if (headElements == null) {
            headElements = super.getHeadElements();

            // Add the jQuery library
            headElements.add(new JsImport("/assets/js/jquery-1.4.2.js"));

            // Create a default model and add the pageSize variable to pass to
            // the JavaScript template: ajax-live-scroller.js
            Map<String, Object> jsModel = ClickUtils.createTemplateModel(this, getContext());
            jsModel.put("pageSize", pageSize);

            // Note the actual JavaScript necessary to setup the dynamic scrolling
            // is specified in the Page JavaScript template: ajax-live-scroller.js.
            headElements.add(new JsScript("/ajax/scroller/ajax-live-scroller.js", jsModel));
        }
        return headElements;
    }

    private List<Customer> loadCustomers(int offset, int pageSize) {
        return customerService.getTopCustomersForPage(offset, pageSize);
    }
}
