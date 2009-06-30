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
import org.apache.click.util.Bindable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Provides an demonstration of Table control styles.
 *
 * @author Malcolm Edgar
 */
public class TableStyles extends BorderPage implements ApplicationContextAware {

    private static final long serialVersionUID = 1L;

    @Bindable public Form form = new Form();
    @Bindable public Table table = new Table();

    private Select styleSelect = new Select("style", "Table Style:");
    private Checkbox hoverCheckbox = new Checkbox("hover", "Hover Rows:");

    private transient ApplicationContext applicationContext;

    // ----------------------------------------------------------- Constructor

    public TableStyles() {
        setStateful(true);

        // Setup table style select.
        form.setColumns(3);
        form.setLabelAlign(Form.ALIGN_LEFT);

        styleSelect.addAll(Table.CLASS_STYLES);
        styleSelect.setAttribute("onchange", "this.form.submit();");
        form.add(styleSelect);

        form.add(new Label("&nbsp; &nbsp;"));

        hoverCheckbox.setAttribute("onchange", "this.form.submit();");
        form.add(hoverCheckbox);

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
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * @see org.apache.click.Page#onRender()
     */
    @Override
    public void onRender() {
        table.setClass(styleSelect.getValue());
        table.setHoverRows(hoverCheckbox.isChecked());

        List<Customer> customers = getCustomerService().getCustomers();
        table.setRowList(customers);
    }

    /**
     * Return CustomerService instance from Spring application context.
     *
     * @return CustomerService instance
     */
    public CustomerService getCustomerService() {
        return (CustomerService) applicationContext.getBean("customerService");
    }

    /**
     * Set Spring application context as defined by
     * {@link org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)}.
     *
     * @param applicationContext set Spring application context
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        this.applicationContext = applicationContext;
    }
}
