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
package org.apache.click.examples.page.ajax.table;

import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import org.apache.click.Control;
import org.apache.click.Partial;
import org.apache.click.ajax.AjaxBehavior;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Table;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.LinkDecorator;
import org.springframework.stereotype.Component;

/**
 * Basic Table Ajax Demo example using the jQuery JavaScript library.
 */
@Component
public class TableAjaxDemoPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Table table = new Table("table");

    private ActionLink editLink = new ActionLink("edit", "Edit", this, "onEditClick");
    private ActionLink deleteLink = new ActionLink("delete", "Delete", this, "onDeleteClick");

    @Resource(name="customerService")
    private CustomerService customerService;

    public TableAjaxDemoPage() {
        addControl(editLink);
        editLink.addBehavior(new AjaxBehavior() {

            @Override
            public Partial onAction(Control source) {
                Customer customer = customerService.getCustomerForID(editLink.getValue());
                return new Partial("Edit Clicked for customer: " + customer.getName(), Partial.TEXT);
            }
        });

        addControl(deleteLink);
        deleteLink.addBehavior(new AjaxBehavior() {

            @Override
            public Partial onAction(Control source) {
                Customer customer = customerService.getCustomerForID(deleteLink.getValue());
                return new Partial("Delete Clicked for customer: " + customer.getName(), Partial.TEXT);
            }
        });

        addControl(table);

        // Setup customers table
        table.setClass(Table.CLASS_ISI);
        table.setPageSize(4);
        table.setShowBanner(true);
        table.setSortable(true);

        Column column = new Column("name");
        column.setWidth("140px;");
        table.addColumn(column);

        column = new Column("email");
        column.setAutolink(true);
        column.setWidth("230px;");
        table.addColumn(column);

        column = new Column("age");
        column.setTextAlign("center");
        column.setWidth("40px;");
        table.addColumn(column);

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setTextAlign("right");
        column.setWidth("100px;");
        table.addColumn(column);

        column = new Column("Action");
        column.setSortable(false);
        ActionLink[] links = new ActionLink[]{editLink, deleteLink};
        column.setDecorator(new LinkDecorator(table, links, "id"));
        table.addColumn(column);

        table.setDataProvider(new DataProvider<Customer>() {
            public List<Customer> getData() {
                return customerService.getCustomers();
            }
        });
    }

    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();
            headElements.add(new JsImport("/assets/js/jquery-1.3.2.js"));
            headElements.add(new JsScript("/ajax/table/table-ajax-demo.js", new HashMap()));
        }
        return headElements;
    }
}
