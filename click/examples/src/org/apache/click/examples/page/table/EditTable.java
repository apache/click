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
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Field;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.DoubleField;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.LinkDecorator;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.util.ContainerUtils;
import org.springframework.stereotype.Component;

/**
 * Provides an demonstration of Table and Form editor pattern, and the use
 * CayenneForm and LinkDecorator classes.
 */
@Component
public class EditTable extends BorderPage {

    public static final String OBJECT_ID = "id";

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");
    private Table table = new Table("table");
    private ActionLink editLink = new ActionLink("edit", "Edit", this, "onEditClick");
    private ActionLink deleteLink = new ActionLink("delete", "Delete", this, "onDeleteClick");

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public EditTable() {
        addControl(form);
        addControl(table);
        addControl(editLink);
        addControl(deleteLink);

        // Setup customers form
        FieldSet fieldSet = new FieldSet("customer");
        fieldSet.add(new TextField("name"));
        fieldSet.add(new EmailField("email"));
        fieldSet.add(new DoubleField("holdings"));
        fieldSet.add(new DateField("dateJoined"));
        form.add(fieldSet);
        form.add(new Submit("save", this, "onSaveClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));
        form.add(new HiddenField(OBJECT_ID, Integer.class));
        form.add(new HiddenField(Table.PAGE, String.class));
        form.add(new HiddenField(Table.COLUMN, String.class));

        // Setup customers table
        table.setClass(Table.CLASS_SIMPLE);
        table.setPageSize(8);
        table.setShowBanner(true);

        Column column = new Column("name");
        column.setWidth("140px");
        table.addColumn(column);

        column = new Column("email");
        column.setAutolink(true);
        column.setWidth("220px");
        table.addColumn(column);

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setTextAlign("right");
        column.setWidth("100px");
        table.addColumn(column);

        column = new Column("dateJoined");
        column.setFormat("{0,date,medium}");
        column.setWidth("90px");
        table.addColumn(column);

        column = new Column("Action");
        column.setSortable(false);
        ActionLink[] links = new ActionLink[]{editLink, deleteLink};
        column.setDecorator(new LinkDecorator(table, links, "id"));
        table.addColumn(column);

        deleteLink.setAttribute("onclick", "return window.confirm('Please confirm delete');");

        table.setDataProvider(new DataProvider<Customer>() {
            public List<Customer> getData() {
                return customerService.getCustomers();
            }
        });
    }

    // Event Handlers ---------------------------------------------------------

    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        Customer customer = customerService.getCustomerForID(id);
        if (customer != null) {
            form.copyFrom(customer);
        }
        return true;
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        customerService.deleteCustomer(id);
        return true;
    }

    public boolean onSaveClick() {
        if (form.isValid()) {
            String id = form.getFieldValue(OBJECT_ID);
            Customer customer = customerService.getCustomerForID(id);
            form.copyTo(customer);
            BaseContext.getThreadObjectContext().commitChanges();
            clearNonHiddenFieldValues(form);
        }
        return true;
    }

    public boolean onCancelClick() {
        BaseContext.getThreadObjectContext().rollbackChanges();
        clearNonHiddenFieldValues(form);
        form.clearErrors();
        return true;
    }

    /**
     * @see org.apache.click.Page#onGet()
     */
    @Override
    public void onGet() {
        form.getField(Table.PAGE).setValue("" + table.getPageNumber());
        form.getField(Table.COLUMN).setValue(table.getSortedColumn());
    }

    /**
     * @see org.apache.click.Page#onPost()
     */
    @Override
    public void onPost() {
        String pageNumber = form.getField(Table.PAGE).getValue();
        table.setPageNumber(Integer.parseInt(pageNumber));
        table.setSortedColumn(form.getField(Table.COLUMN).getValue());
    }

    private void clearNonHiddenFieldValues(Form form) {
        List<Field> fields = ContainerUtils.getInputFields(form);
            for (Field field : fields) {
                if (!(field instanceof HiddenField)) {
                    field.setValue("");
                }
            }
    }
}
