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
package org.apache.click.examples.page.general;

import java.util.List;

import javax.annotation.Resource;

import org.apache.click.control.AbstractLink;
import org.apache.click.control.Column;
import org.apache.click.control.PageLink;
import org.apache.click.control.Table;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.examples.control.exporter.ExcelTableExporter;
import org.apache.click.examples.control.exporter.ExportTable;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.EditCustomer;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.LinkDecorator;

import org.apache.click.extras.control.TableInlinePaginator;
import org.springframework.stereotype.Component;

/**
 * Provides a Excel Export page example using the Apache POI library.
 */
@Component
public class ExcelTableExportPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private static final String ACTION_COLUMN = "action";

    private ExportTable table1 = new ExportTable("table1");
    private ExportTable table2 = new ExportTable("table2");
    private ExportTable table3 = new ExportTable("table3");

    private PageLink editLink = new PageLink("Edit", EditCustomer.class);

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public ExcelTableExportPage() {
        addColumns(table1);
        addColumns(table2);
        addColumns(table3);

        setupExporter(table1);
        table1.setExportAttachment(ExportTable.EXPORTER_ATTACHED);

        setupExporter(table2);
        table2.setExportAttachment(ExportTable.EXPORTER_DETACHED);

        setupExporter(table3);
        table3.setExportAttachment(ExportTable.EXPORTER_INLINE);

        // A simple caching dataProvider that only retrieves customers once
        DataProvider dataProvider = new DataProvider() {

            List<Customer> customers;

            public List<Customer> getData() {
                if (customers == null) {
                    customers = customerService.getCustomersSortedByName(10);
                }
                return customers;
            }
        };

        addControl(table1);
        table1.setDataProvider(dataProvider);
        addControl(table2);
        table2.setDataProvider(dataProvider);
        addControl(table3);
        table3.setDataProvider(dataProvider);

        addControl(editLink);
    }

    //  Private Methods --------------------------------------------------------

    private void setupExporter(ExportTable table) {
        // Setup table exporting
        ExcelTableExporter excel = new ExcelTableExporter("Excel", "/assets/images/page_excel.png");
        table.getExportContainer().add(excel);

        // Excluding the action column ensures the actions are not exported to
        // Excel
        table.getExcludedExportColumns().add(ACTION_COLUMN);
    }

    private void addColumns(ExportTable table) {
        table.setSortable(true);
        table.setClass(Table.CLASS_ITS);

        // Setup table paginator
        table.setPageSize(4);
        table.setPaginator(new TableInlinePaginator(table));
        table.setPaginatorAttachment(ExportTable.PAGINATOR_INLINE);

        Column column = new Column(Customer.NAME_PROPERTY);
        column.setWidth("140px;");
        table.addColumn(column);

        column = new Column(Customer.EMAIL_PROPERTY);
        column.setAutolink(true);
        column.setWidth("230px;");
        table.addColumn(column);

        column = new Column(Customer.AGE_PROPERTY);
        column.setTextAlign("center");
        column.setWidth("40px;");
        table.addColumn(column);

        column = new Column(Customer.HOLDINGS_PROPERTY);
        column.setFormat("{0,number,currency}");
        column.setTextAlign("right");
        column.setWidth("100px;");
        table.addColumn(column);

        column = new Column(Customer.DATE_JOINED_PROPERTY);
        column.setFormat("{0,date,medium}");
        column.setWidth("100px;");
        table.addColumn(column);

        // Excluding the dateJoined column ensures the date is not shown in the
        // HTML table, but will be exported to the Excel spreadsheet
        table.getExcludedColumns().add(Customer.DATE_JOINED_PROPERTY);

        column = new Column(ACTION_COLUMN);
        AbstractLink[] links = new AbstractLink[] { editLink };
        editLink.setParameter("referrer", "/general/excel-table-export.htm");
        column.setDecorator(new LinkDecorator(table, links, "id"));
        column.setSortable(false);
        table.addColumn(column);
    }
}
