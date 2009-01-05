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
package org.apache.click.examples.page.panel;

import java.util.Date;
import java.util.List;

import org.apache.click.control.Column;
import org.apache.click.control.Table;
import org.apache.click.examples.control.FilterPanel;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides example usage of a custom date range FilterPanel control.
 *
 * @author Malcolm Edgar
 */
public class FilterPanelDemo extends BorderPage {

    public FilterPanel filterPanel = new FilterPanel();
    public Table table = new Table();

    public FilterPanelDemo() {
        // Setup customers table
        table.setClass("isi");
        table.setWidth("550px");
        table.setSortable(false);

        table.addColumn(new Column("name"));

        Column column = new Column("age");
        column.setTextAlign("center");
        table.addColumn(column);

        table.addColumn(new Column("investments"));

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setTextAlign("right");
        table.addColumn(column);

        column = new Column("dateJoined");
        column.setTextAlign("right");
        column.setFormat("{0, date,dd MMM yyyy}");
        table.addColumn(column);
    }

    /**
     * @see org.apache.click.Page#onRender()
     */
    public void onRender() {
        Date from = filterPanel.getStartDate();
        Date to = filterPanel.getEndDate();

        List customers = getCustomerService().getCustomers(from, to);

        table.setRowList(customers);
    }
}
