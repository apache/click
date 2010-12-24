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
package org.apache.click.examples.page.table;

import java.util.List;

import javax.annotation.Resource;

import org.apache.click.Context;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;
import org.apache.click.control.PageLink;
import org.apache.click.control.Table;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.EditCustomer;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.dataprovider.DataProvider;
import org.springframework.stereotype.Component;

/**
 * Provides an demonstration of Table control paging.
 */
@Component
public class TableDecorator extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Table table = new Table("table");

    private ActionLink viewLink = new ActionLink("view", this, "onViewClick");
    private PageLink editLink = new PageLink("edit", EditCustomer.class);
    private ActionLink deleteLink = new ActionLink("delete", this, "onDeleteClick");

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public TableDecorator() {
        // Add controls to page
        addControl(table);
        addControl(viewLink);
        addControl(editLink);
        addControl(deleteLink);

        // Setup customers table
        table.setClass(Table.CLASS_SIMPLE);

        Column column = new Column("name");
        column.setSortable(false);
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                Customer customer = (Customer) row;
                String email = customer.getEmail();
                String name = customer.getName();
                return "<a href='mailto:" + email + "'>" + name + "</a>";
            }
        });
        table.addColumn(column);

        column = new Column("investments");
        column.setAutolink(true);
        table.addColumn(column);

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setTextAlign("right");
        table.addColumn(column);

        viewLink.setTitle("View customer details");

        editLink.setListener(this, "onEditClick");
        editLink.setTitle("Edit customer details");
        editLink.setParameter("referrer", "/table/table-decorator.htm");

        deleteLink.setTitle("Delete customer record");
        deleteLink.setAttribute("onclick", "return window.confirm('Are you sure you want to delete this record?');");

        column = new Column("Action");
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                Customer customer = (Customer) row;
                String id = String.valueOf(customer.getId());

                viewLink.setValue(id);
                editLink.setParameter("id", id);
                deleteLink.setValue(id);

                return viewLink.toString() + " | " +
                       editLink.toString() + " | " +
                       deleteLink.toString();
            }
        });
        table.addColumn(column);

        table.setDataProvider(new DataProvider<Customer>() {
            public List<Customer> getData() {
                return customerService.getCustomersSortedByName(12);
            }
        });
    }

    // Event Handlers ---------------------------------------------------------

    public boolean onViewClick() {
        Integer id = viewLink.getValueInteger();
        Customer customerDetail = customerService.getCustomerForID(id);
        addModel("customerDetail", customerDetail);
        return true;
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        customerService.deleteCustomer(id);
        return true;
    }

}
