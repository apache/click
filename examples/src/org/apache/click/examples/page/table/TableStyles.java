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

import org.apache.click.control.Checkbox;
import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.click.control.Label;
import org.apache.click.control.Select;
import org.apache.click.control.Table;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.TableInlinePaginator;
import org.apache.click.dataprovider.DataProvider;

/**
 * Provides an demonstration of Table control styles.
 */
public class TableStyles extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");
    private Table table = new Table("table");

    private Select styleSelect = new Select("style", "Table Style:");
    private Checkbox hoverCheckbox = new Checkbox("hover", "Hover Rows:");

        private CustomerService customerService;

    // Constructor -----------------------------------------------------------

    public TableStyles() {
        addControl(form);
        addControl(table);

        // Setup table style select.
        form.setColumns(3);
        form.setLabelAlign(Form.ALIGN_LEFT);

        styleSelect.addAll(Table.CLASS_STYLES);
        styleSelect.setAttribute("onchange", "form.submit();");
        form.add(styleSelect);

        form.add(new Label("&nbsp; &nbsp;"));

        hoverCheckbox.setAttribute("onclick", "form.submit();");
        form.add(hoverCheckbox);

        // Rexstore form's selection data  from the session
        form.restoreState(getContext());

        // Setup customers table
        table.setClass(styleSelect.getValue());
        table.setHoverRows(true);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setSortable(true);

        table.setPaginator(new TableInlinePaginator(table));
        table.setPaginatorAttachment(Table.PAGINATOR_INLINE);

        Column column = new Column("id");
        column.setWidth("50px");
        column.setSortable(false);
        table.addColumn(column);

        column = new Column("name");
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

        table.setDataProvider(new DataProvider<Customer>() {
            public List<Customer> getData() {
                return getCustomerService().getCustomers();
            }
        });
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * @see org.apache.click.Page#onPost()
     */
    @Override
    public void onPost() {
         // Save the posted form data in the session, so this data will be
        // available for future requests
        form.saveState(getContext());
    }

    /**
     * @see org.apache.click.Page#onRender()
     */
    @Override
    public void onRender() {
        table.setClass(styleSelect.getValue());
        table.setHoverRows(hoverCheckbox.isChecked());
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Return CustomerService instance.
     *
     * @return CustomerService instance
     */
    public CustomerService getCustomerService() {
        return customerService;
    }

    /**
     * Set the CustomerService instance that is injected by the Spring
     * application context.
     *
     * @param customerService the customerService instance to inject
     */
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }
}
