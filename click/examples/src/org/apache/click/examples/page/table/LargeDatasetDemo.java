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

import org.apache.click.control.Column;
import org.apache.click.control.Table;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.util.PaginatingList;
import org.apache.click.util.Bindable;
import org.apache.click.util.DataProvider;
import org.springframework.stereotype.Component;

/**
 * Provides a demonstration of a Table with a huge number of rows and how to
 * lazily page through the rows using a paginating list.
 */
@Component
public class LargeDatasetDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Bindable protected Table table;

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public LargeDatasetDemo() {
        table = new Table();

        // Setup customers table
        table.setClass(Table.CLASS_ITS);
        table.setSortable(true);

        // We will sort the data ourselves. We set table sorted attribute to true
        // so the table doesn't attempt to sort the data
        table.setSorted(true);

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

        table.setPageSize(5);

        table.setDataProvider(new DataProvider<Customer>() {
            public List<Customer> getData() {
                return getCustomerList();
            }
        });
    }

    // Private Methods --------------------------------------------------------

    private List<Customer> getCustomerList() {

        // Below we retrieve only those customers between:
        //     first row .. (first row + page size)
        List<Customer> customerList =
            customerService.getCustomersForPage(table.getFirstRow(),
                                                table.getPageSize(),
                                                table.getSortedColumn(),
                                                table.isSortedAscending());

        int customerCount = customerService.getNumberOfCustomers();

        // Return a paginating list of the Table control. The paginating list
        // provides a wrapper around the "page" of customer data so that the
        // Table control thinks it is working will the full result set rather
        // than just a window.
        return new PaginatingList<Customer>(customerList,
                                            table.getFirstRow(),
                                            table.getPageSize(),
                                            customerCount);
    }

}
