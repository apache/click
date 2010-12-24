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
import org.apache.click.dataprovider.PagingDataProvider;
import org.springframework.stereotype.Component;

/**
 * Provides a demonstration of a Table with a huge number of rows and how to
 * lazily page through the rows using a PagingDataProvider.
 */
@Component
public class LargeDatasetDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Table table = new Table("table");

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public LargeDatasetDemo() {
        // Add table to page
        addControl(table);

        // Setup customers table
        table.setClass(Table.CLASS_ITS);
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

        table.setPageSize(5);

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

}
