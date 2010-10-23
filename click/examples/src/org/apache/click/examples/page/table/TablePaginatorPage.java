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
import org.apache.click.control.TablePaginator;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.TableInlinePaginator;
import org.springframework.stereotype.Component;

/**
 * Provides an demonstration of Table pagination options.
 */
@Component
public class TablePaginatorPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    protected Table table1 = new Table("table1");
    protected Table table2 = new Table("table2");
    protected Table table3 = new Table("table3");

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public TablePaginatorPage() {
        addControl(table1);
        addControl(table2);
        addControl(table3);

        // Table 1
        addColumns(table1);
        table1.setPaginator(new TablePaginator(table1));
        table1.setPaginatorAttachment(Table.PAGINATOR_ATTACHED);

        // Table 2
        addColumns(table2);
        table2.setPaginator(new TablePaginator(table2));
        table2.setPaginatorAttachment(Table.PAGINATOR_DETACHED);

        // Table 3
        addColumns(table3);
        table3.setPaginator(new TableInlinePaginator(table3));
        table3.setPaginatorAttachment(Table.PAGINATOR_INLINE);
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * @see org.apache.click.Page#onRender()
     */
    @Override
    public void onRender() {
        List<Customer> customers = customerService.getCustomers();
        table1.setRowList(customers);
        table2.setRowList(customers);
        table3.setRowList(customers);
    }

    // Private Methods --------------------------------------------------------

    private void addColumns(Table table) {
        table.setClass(Table.CLASS_ITS);
        table.setPageSize(4);

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
    }
}
