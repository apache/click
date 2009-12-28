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
package org.apache.click.examples.page.velocity;

import java.util.List;

import javax.annotation.Resource;

import org.apache.click.Page;
import org.apache.click.control.ActionLink;
import org.apache.click.control.PageLink;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.EditCustomer;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.util.Bindable;
import org.springframework.stereotype.Component;

/**
 * Provides a dynamic ActionLink and PageLink example in a HTML table.
 * <p/>
 * In this example the controls are automatically added to the Page model
 * because they have public visiblity. The controls name is automatically set
 * to their field name.
 *
 * @author Malcolm Edgar
 */
@Component
public class ActionTable extends BorderPage {

    @Bindable protected List customers;
    @Bindable protected Customer customerDetail;
    @Bindable protected ActionLink viewLink = new ActionLink(this, "onViewClick");
    @Bindable protected PageLink editLink = new PageLink(EditCustomer.class);
    @Bindable protected ActionLink deleteLink = new ActionLink(this, "onDeleteClick");

    @Resource(name="customerService")
    private CustomerService customerService;

    @Override
    public void onInit() {
        super.onInit();

        String path = getContext().getPagePath(getClass());
        editLink.setParameter("referrer", path);
    }

    public boolean onViewClick() {
        Integer id = viewLink.getValueInteger();
        customerDetail = customerService.getCustomerForID(id);

        return true;
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        customerService.deleteCustomer(id);

        return true;
    }

    /**
     * Load the list of customers to display.  This method is not invoked
     * when a customer is being edited.
     *
     * @see Page#onRender()
     */
    @Override
    public void onRender() {
        customers = customerService.getCustomersSortedByName(7);
        getFormat().setEmptyString("&nbsp;");
    }

}
