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

import java.io.Serializable;

/**
 * An interface to provide data on demand to controls.
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
 *         table.setDataProvider(new DataProvider<Customer>() {
 *
 *             // Return a list of customers
 *             public List<Customer> getData() {
 *
 *                 return getCustomerService().getCustomers();
 *             }
 *         });
 *     }
 * } </pre>
 */
public interface DataProvider<T> extends Serializable {

    /**
     * Return the iterable collection of data items supplied by the data provider.
     *
     * @return the iterable collection of data items supplied by the data provider.
     */
    public Iterable<T> getData();

}
