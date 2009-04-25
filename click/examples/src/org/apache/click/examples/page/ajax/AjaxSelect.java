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

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.springframework.stereotype.Component;

/**
 * Provides an Ajax select example Page.
 *
 * @author Malcolm Edgar
 */
@Component
public class AjaxSelect extends BorderPage {

    public String jsInclude = "ajax/ajax-select-include.htm";

    public Select customerSelect = new Select("customerSelect");

    @Resource(name="customerService")
    private CustomerService customerService;

    @Override
    public void onInit() {
        super.onInit();

        customerSelect.setAttribute("onchange", "onCustomerChange(this);");

        List<Customer> customerList = customerService.getCustomersSortedByName(8);
        for (Customer customer : customerList) {
            customerSelect.add(new Option(customer.getId(), customer.getName()));
        }

        customerSelect.setSize(customerList.size());
    }

}
