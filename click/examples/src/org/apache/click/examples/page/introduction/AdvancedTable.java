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
package org.apache.click.examples.page.introduction;

import java.util.List;

import org.apache.click.control.AbstractLink;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.PageLink;
import org.apache.click.control.Table;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.EditCustomer;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.LinkDecorator;
import org.apache.click.util.Bindable;
import org.apache.click.util.DataProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Provides an advanced Table usage example Page.
 * <p/>
 * This example also demonstrates how a stateful Page can be used to preserve
 * the Table sort and paging state while editing customers.
 */
public class AdvancedTable extends BorderPage implements ApplicationContextAware {

    private static final long serialVersionUID = 1L;

    @Bindable protected Table table = new Table();
    @Bindable protected PageLink editLink = new PageLink("Edit", EditCustomer.class);
    @Bindable protected ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    /**
     * Spring's application context from where a CustomerService instance can be
     * retrieved.
     */
    private transient ApplicationContext applicationContext;

    // Constructor ------------------------------------------------------------

    public AdvancedTable() {
        // Set Page to stateful to preserve Table sort and paging state while editing customers
        setStateful(true);

        table.setClass(Table.CLASS_ITS);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setSortable(true);

        table.addColumn(new Column("id"));

        table.addColumn(new Column("name"));

        Column column = new Column("email");
        column.setAutolink(true);
        column.setTitleProperty("name");
        table.addColumn(column);

        table.addColumn(new Column("investments"));

        editLink.setImageSrc("/assets/images/table-edit.png");
        editLink.setTitle("Edit customer details");
        editLink.setParameter("referrer", "/introduction/advanced-table.htm");

        deleteLink.setImageSrc("/assets/images/table-delete.png");
        deleteLink.setTitle("Delete customer record");
        deleteLink.setAttribute("onclick", "return window.confirm('Are you sure you want to delete this record?');");

        column = new Column("Action");
        column.setTextAlign("center");
        AbstractLink[] links = new AbstractLink[] { editLink, deleteLink };
        column.setDecorator(new LinkDecorator(table, links, "id"));
        column.setSortable(false);
        table.addColumn(column);

        table.setDataProvider(new DataProvider<Customer>() {

            private static final long serialVersionUID = 1L;

            public List<Customer> getData() {
                return getCustomerService().getCustomers();
            }
        });
    }

    // Event Handlers ---------------------------------------------------------

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        getCustomerService().deleteCustomer(id);
        return true;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Return CustomerService instance from Spring application context.
     *
     * @return CustomerService instance
     */
    public CustomerService getCustomerService() {
        return (CustomerService) applicationContext.getBean("customerService");
    }

    /**
     * Set Spring application context as defined by
     * {@link org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)}.
     *
     * @param applicationContext set Spring application context
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        this.applicationContext = applicationContext;
    }
}
