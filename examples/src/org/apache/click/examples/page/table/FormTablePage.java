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

import org.apache.cayenne.BaseContext;
import org.apache.click.control.Checkbox;
import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.control.TextField;
import org.apache.click.examples.control.InvestmentSelect;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.FieldColumn;
import org.apache.click.extras.control.FormTable;
import org.apache.click.extras.control.NumberField;
import org.apache.click.dataprovider.DataProvider;
import org.springframework.stereotype.Component;

/**
 * Provides an demonstration of Table control paging.
 */
@Component
public class FormTablePage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private static final int NUM_ROWS = 20;

    private FormTable table = new FormTable("table");

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public FormTablePage() {
        addControl(table);

        // Setup customers table
        table.setClass(Table.CLASS_SIMPLE);
        table.setWidth("700px");
        table.getForm().setButtonAlign(Form.ALIGN_RIGHT);
        table.setPageSize(10);
        table.setShowBanner(true);

        table.addColumn(new Column("id"));

        FieldColumn column = new FieldColumn("name", new TextField());
        column.getField().setRequired(true);
        column.setVerticalAlign("baseline");
        table.addColumn(column);

        column = new FieldColumn("email", new EmailField());
        column.getField().setRequired(true);
        table.addColumn(column);

        column = new FieldColumn("investments", new InvestmentSelect());
        column.getField().setRequired(true);
        table.addColumn(column);

        NumberField numberField = new NumberField();
        numberField.setSize(10);
        column = new FieldColumn("holdings", numberField);
        column.setTextAlign("right");
        table.addColumn(column);

        DateField field = new DateField();
        column = new FieldColumn("dateJoined", field);
        column.setDataStyle("white-space", "nowrap");
        table.addColumn(column);

        column = new FieldColumn("active", new Checkbox());
        column.setTextAlign("center");
        table.addColumn(column);

        table.getForm().add(new Submit("ok", "  OK  ", this, "onOkClick"));
        table.getForm().add(new Submit("cancel", this, "onCancelClick"));
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * @see org.apache.click.Page#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();

        table.setDataProvider(createDataProvider());
    }

    public boolean onOkClick() {
        if (table.getForm().isValid()) {
            // Please note with Cayenne ORM this will persist any changes
            // to data objects submitted by the form.
            BaseContext.getThreadObjectContext().commitChanges();

            // With other ORM frameworks like Hibernate you would retrieve
            // rows for the table as persist those objects. For example:
            /*
            List rowList = table.getRowList();
            for (Iterator i = rowList.iterator(); i.hasNext();) {
                Object row = (Object) i.next();
                getSession().save(row);
            }
            */
        }
        return true;
    }

    public boolean onCancelClick() {
        // Rollback any changes made to the customers, which are stored in
        // the data context
        BaseContext.getThreadObjectContext().rollbackChanges();

        // Refresh the FormTable rowList by setting a new DataProvider
        table.setDataProvider(createDataProvider());

        // As form was cancelled, don't render the user submitted values
        table.setRenderSubmittedValues(false);

        return true;
    }

    public DataProvider createDataProvider() {
        DataProvider dp = new DataProvider() {
            public List<Customer> getData() {
                return customerService.getCustomersSortedByName(NUM_ROWS);
            }
        };

        return dp;
    }
}
