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
import org.apache.click.ActionResult;
import org.apache.click.ajax.AjaxBehavior;
import org.apache.click.ajax.DefaultAjaxBehavior;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Table;
import org.apache.click.dataprovider.PagingDataProvider;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.LinkDecorator;
import org.springframework.stereotype.Component;

/**
 * Demonstrates how to sort, page, edit and delete customers using the
 * Table control and jQuery.
 */
@Component
public class TableAjaxPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Table table = new Table("table");

    private ActionLink editLink = new ActionLink("edit", "Edit", this, "onEditClick");
    private ActionLink deleteLink = new ActionLink("delete", "Delete", this, "onDeleteClick");

    @Resource(name="customerService")
    private CustomerService customerService;

    public TableAjaxPage() {
        addControl(editLink);
        editLink.addBehavior(new DefaultAjaxBehavior() {

            @Override
            public ActionResult onAction(Control source) {
                Customer customer = customerService.getCustomerForID(editLink.getValue());
                return new ActionResult("Edit Clicked for customer: " + customer.getName(), ActionResult.TEXT);
            }
        });

        addControl(deleteLink);
        deleteLink.addBehavior(new DefaultAjaxBehavior() {

            @Override
            public ActionResult onAction(Control source) {
                Customer customer = customerService.getCustomerForID(deleteLink.getValue());
                return new ActionResult("Delete Clicked for customer: " + customer.getName(), ActionResult.TEXT);
            }
        });

        table.getControlLink().addBehavior(new DefaultAjaxBehavior() {

            @Override
            public ActionResult onAction(Control source) {

                // NOTE: Ajax requests only process the target Control. Here we
                // process the table in order to update paging and sorting state
                table.onProcess();

                return new ActionResult(table.toString(), ActionResult.HTML);
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

        // Use a Paging DataProvider to only retrieve the selected customers
        table.setDataProvider(new PagingDataProvider<Customer>() {

            public List<Customer> getData() {
                int start = table.getFirstRow();
                int count = table.getPageSize();
                String sortColumn = table.getSortedColumn();
                boolean ascending = table.isSortedAscending();

                return customerService.getCustomersForPage(start, count, sortColumn, ascending);
            }

            public int size() {
                return customerService.getNumberOfCustomers();
            }
        });
    }

    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();
            headElements.add(new JsImport("/assets/js/jquery-1.4.2.js"));
            headElements.add(new JsScript("/ajax/table/table-ajax.js", new HashMap()));
        }
        return headElements;
    }
}
