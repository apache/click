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

import java.util.Map;
import javax.annotation.Resource;

import org.apache.click.Context;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.util.Bindable;
import org.apache.click.util.ClickUtils;
import org.springframework.stereotype.Component;

/**
 * Provides an Ajax select example Page.
 *
 * @author Malcolm Edgar
 */
@Component
public class AjaxSelect extends BorderPage {

    @Bindable protected Select customerSelect = new Select("customerSelect");

    @Resource(name="customerService")
    private CustomerService customerService;

    @Override
    public void onInit() {
        super.onInit();

        List<Customer> customerList = customerService.getCustomersSortedByName(8);
        for (Customer customer : customerList) {
            customerSelect.add(new Option(customer.getId(), customer.getName()));
        }

        customerSelect.setSize(customerList.size());
    }

    @Override
    public List getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            // Include the prototype.js library which is made available under
            // the web folder "/click/prototype/"
            headElements.add(new JsImport("/click/prototype/prototype.js"));

            Context context = getContext();

            // Create a model to pass to the Page JavaScript template. The
            // template recognizes the following Velocity variables:
            // $context, $path, $selector and $target
            Map model = ClickUtils.createTemplateModel(this, context);

            // Set path to the AjaxCustomer Page path
            model.put("path", context.getPagePath(AjaxCustomer.class));

            // Add a CSS selector, in this case the customerSelect ID attribute
            model.put("selector", customerSelect.getId());

            // Add the ID of a target element in the Page template to replace
            // with new data, in this example the target is 'customerDetails'
            model.put("target", "customerDetails");

            // Include the Page associated JavaScript template
            headElements.add(new JsScript("/ajax/ajax-select.js", model));
        }

        return headElements;
    }
}
