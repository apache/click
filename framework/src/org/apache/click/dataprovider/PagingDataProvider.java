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
package org.apache.click.dataprovider;

/**
 * An interface to provide paginated data on demand to controls. It allows
 * specifying the total number of results represented by this DataProvider
 * through the {@link #size()} method.
 * <p/>
 * Example usage:
 *
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     private Table table = new Table("table");
 *
 *     public MyPage() {
 *
 *         ...
 *
 *         table.setDataProvider(new PagingDataProvider<Customer>() {
 *
 *             // Return a list of customers
 *             public List<Customer> getData() {
 *
 *                 int start = table.getFirstRow();
 *                 int count = table.getPageSize();
 *
 *                 return getCustomerService().getCustomers(start, count);
 *             }
 *
 *             // Return the total number of customers to page over
 *             public int size() {
 *                 return getCustomerService().getNumberOfCustomers();
 *             }
 *         });
 *     }
 * } </pre>
 *
 * <b>Please note</b>: when providing paginated data to controls that support
 * sorting e.g. Tables, you are responsible for sorting the data, as the Table
 * does not have access to all the data.
 * <p/>
 * Here is an example demonstrating both paging and sorting:
 *
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     private Table table = new Table("table");
 *
 *     public MyPage() {
 *
 *         ...
 *
 *         table.setDataProvider(new PagingDataProvider<Customer>() {
 *
 *             // Return a list of customers
 *             public List<Customer> getData() {
 *
 *                 int start = table.getFirstRow();
 *                 int count = table.getPageSize();
 *                 String sortColumn = table.getSortedColumn();
 *                 boolean ascending = table.isSortedAscending();
 *
 *                 return getCustomerService().getCustomers(start, count, sortColumn, ascending);
 *             }
 *
 *             // Return the total number of customers to page over
 *             public int size() {
 *                 return getCustomerService().getNumberOfCustomers();
 *             }
 *         });
 *     }
 * } </pre>
 */
public interface PagingDataProvider<T> extends DataProvider<T> {

    /**
     * Return the total number of results represented by this DataProvider.
     *
     * @return the total number of results represented by this DataProvider
     */
    public int size();
}
