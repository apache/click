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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.click.control.Column;
import org.apache.click.control.Table;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.util.Bindable;
import org.springframework.stereotype.Component;

/**
 * Provides a demonstration of a Table with a huge number of rows and how to
 * lazily page through the rows using a custom List implementation.
 *
 * @author Bob Schellink
 */
@Component
public class LargeDatasetDemo extends BorderPage {

    @Bindable public Table table;

    @Resource(name="customerService")
    private CustomerService customerService;

    public LargeDatasetDemo() {
        table = new Table() {

            /**
             * Sorting must be done on Database, so we override Table build in
             * sorting to do nothing.
             */
            protected void sortRowList() {
            }
        };

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
    }

    /**
     * @see org.apache.click.Page#onRender()
     */
    @Override
    public void onRender() {
        // Create DataProvider for the specified table and total number of customers
        DataProvider dataProvider = new DataProvider(table, getCustomerCount());

        // Set the DataProvider as the Table row list. Table is now able to
        // calculate the last row value.
        // NOTE: If table rowList is not set, table cannot calculate the last row
        // and invoking #getLastRow will return 0.
        table.setRowList(dataProvider);

        // Retrieve customers given the firstRow, lastRow and pageSize
        List customers = getCustomers(table.getFirstRow(), table.getPageSize(),
            table.getSortedColumn(), table.isSortedAscending());

        // Add the customers to the table dataProvider
        dataProvider.addAll(customers);
    }

    // ---------------------------------------------------------- Inner Classes

    /**
     * Provides a custom List implementation which returns a pre-defined size,
     * even if the underlying amount of entries is less.
     *
     * The List also returns correct row for a specified index by offsetting
     * the index against the Table's firstRow value.
     */
    class DataProvider extends ArrayList {

        /** The DataProvider Table instance. */
        private Table table;

        /** The total number of rows of the dataProvider. */
        private int numOfRows;

        /**
         * Create a new DataProvider instance for the given Table and total number
         * of rows.
         *
         * @param table this dataProvider Table instance
         * @param numOfRows the total number of rows of the dataProvider
         */
        public DataProvider(Table table, int numOfRows) {
            this.table = table;
            this.numOfRows = numOfRows;
        }

        /**
         * Returns the row at the specified index, offsetted by the current
         * table first row value.
         *
         * @param index the index of the row as viewed in the Table
         * @return the the row at the specified index, offsetted by the
         * current table first row value.
         */
        public Object get(final int index) {
            int realIndex = index - table.getFirstRow();
            return super.get(realIndex);
        }

        /**
         * Always return the total number of rows even if the number of entries
         * are less.
         */
        public int size() {
            return numOfRows;
        }
    }

    // -------------------------------------------------------- Private Methods

    private int getCustomerCount() {
        return customerService.getNumberOfCustomers();
    }

    private List<Customer> getCustomers(int from, int pageSize, String sortedColumn,
        boolean ascending) {
        // Below we retrieve only those customers between the from and to
        // args. In a real application one would use an ORM or JDBC to only
        // retrieve the needed rows
        return customerService.getCustomersForPage(from, pageSize, sortedColumn, ascending);
    }

}
