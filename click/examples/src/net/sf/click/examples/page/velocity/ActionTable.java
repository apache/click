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
package net.sf.click.examples.page.velocity;

import java.util.List;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.control.PageLink;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.EditCustomer;

/**
 * Provides a dynamic ActionLink and PageLink example in a HTML table.
 * <p/>
 * In this example the controls are automatically added to the Page model
 * because they have public visiblity. The controls name is automatically set
 * to their field name.
 *
 * @author Malcolm Edgar
 */
public class ActionTable extends BorderPage {

    public List customers;
    public Customer customerDetail;
    public ActionLink viewLink = new ActionLink(this, "onViewClick");
    public PageLink editLink = new PageLink(EditCustomer.class);
    public ActionLink deleteLink = new ActionLink(this, "onDeleteClick");

    public void onInit() {
        super.onInit();

        String path = getContext().getPagePath(getClass());
        editLink.setParameter("referrer", path);
    }

    public boolean onViewClick() {
        Integer id = viewLink.getValueInteger();
        customerDetail = getCustomerService().getCustomerForID(id);

        return true;
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        getCustomerService().deleteCustomer(id);

        return true;
    }

    /**
     * Load the list of customers to display.  This method is not invoked
     * when a customer is being edited.
     *
     * @see Page#onRender()
     */
    public void onRender() {
        customers = getCustomerService().getCustomersSortedByName(7);
        getFormat().setEmptyString("&nbsp;");
    }

}
