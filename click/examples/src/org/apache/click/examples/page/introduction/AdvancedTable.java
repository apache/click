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
package org.apache.click.examples.page.introduction;

import java.util.List;

import javax.annotation.Resource;

import org.apache.click.Page;
import org.apache.click.control.AbstractLink;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.PageLink;
import org.apache.click.control.Table;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.EditCustomer;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.LinkDecorator;
import org.apache.click.util.Bindable;
import org.springframework.stereotype.Component;

/**
 * Provides an advanced Table usage example Page.
 *
 * @author Malcolm Edgar
 */
@Component
public class AdvancedTable extends BorderPage {

    @Bindable public Table table = new Table();
    @Bindable public PageLink editLink = new PageLink("Edit", EditCustomer.class);
    @Bindable public ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    @Resource(name="customerService")
    private CustomerService customerService;

    // ------------------------------------------------------------ Constructor

    public AdvancedTable() {
        table.setClass(Table.CLASS_ITS);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setSortable(true);

        table.addColumn(new Column("id"));

        table.addColumn(new Column("name"));

        Column column = new Column("email");
        column.setAutolink(true);
        column.setTitleProperty("name");
        table.addColumn(column);

        table.addColumn(new Column("investments"));

        editLink.setImageSrc("/assets/images/table-edit.png");
        editLink.setTitle("Edit customer details");
        editLink.setParameter("referrer", "/introduction/advanced-table.htm");

        deleteLink.setImageSrc("/assets/images/table-delete.png");
        deleteLink.setTitle("Delete customer record");
        deleteLink.setAttribute("onclick", "return window.confirm('Are you sure you want to delete this record?');");

        column = new Column("Action");
        column.setTextAlign("center");
        AbstractLink[] links = new AbstractLink[] { editLink, deleteLink };
        column.setDecorator(new LinkDecorator(table, links, "id"));
        column.setSortable(false);
        table.addColumn(column);
    }

    // --------------------------------------------------------- Event Handlers

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        customerService.deleteCustomer(id);
        return true;
    }

    /**
     * @see Page#onRender()
     */
    @Override
    public void onRender() {
        List list = customerService.getCustomers();
        table.setRowList(list);

        // Pass the Table paging and sorting parameters to the link's target
        // page: 'EditCustomer'
        editLink.setParameter(Table.PAGE, String.valueOf(table.getPageNumber()));
        editLink.setParameter(Table.COLUMN, table.getSortedColumn());
        editLink.setParameter(Table.SORT, Boolean.toString(table.isSorted()));
        editLink.setParameter(Table.ASCENDING, Boolean.toString(table.isSortedAscending()));
        editLink.setParameter(ActionLink.ACTION_LINK, table.getControlLink().getName());
    }
}
