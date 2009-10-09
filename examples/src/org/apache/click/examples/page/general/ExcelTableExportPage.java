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

import org.apache.click.Page;
import org.apache.click.control.AbstractLink;
import org.apache.click.control.Column;
import org.apache.click.control.PageLink;
import org.apache.click.control.Table;
import org.apache.click.examples.control.ExportTable;
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

    private ExportTable table = new ExportTable("table");
    private PageLink editLink = new PageLink("Edit", EditCustomer.class);

    @Resource(name="customerService")
    private CustomerService customerService;

    // ------------------------------------------------------------ Constructor

    public ExcelTableExportPage() {
        table.setClass(Table.CLASS_ITS);

        table.addColumn(new Column("id"));
        table.addColumn(new Column("name"));
        table.addColumn(new Column("age"));
        table.addColumn(new Column("email"));

        Column column = new Column("holdings");
        column.setFormat("{0,number,currency}");
        column.setTextAlign("right");
        table.addColumn(column);

        column = new Column("dateJoined");
        column.setFormat("{0,date,medium}");
        table.addColumn(column);

        column = new Column("action");
        column.setTextAlign("center");
        AbstractLink[] links = new AbstractLink[] { editLink };
        editLink.setParameter("referrer", "/general/excel-table-export.htm");
        column.setDecorator(new LinkDecorator(table, links, "id"));
        column.setSortable(false);
        table.addColumn(column);

        table.setPageSize(5);
        table.setSortable(true);
        table.setExportBannerPosition(Table.POSITION_BOTTOM);

        // Exclude the action column from being exported
        table.getExcludedColumns().add("action");
        table.setExportAttachment(ExportTable.EXPORTER_INLINE);
        table.setPaginator(new TableInlinePaginator(table));
        table.setPaginatorAttachment(ExportTable.PAGINATOR_INLINE);

        addControl(table);
        addControl(editLink);
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * @see Page#onRender()
     */
    @Override
    public void onRender() {
        List<Customer> list = customerService.getCustomersSortedByName(10);
        table.setRowList(list);
    }
}
