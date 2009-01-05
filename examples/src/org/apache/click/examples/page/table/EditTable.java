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

import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.FieldSet;
import org.apache.click.control.HiddenField;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.cayenne.CayenneForm;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.DoubleField;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.LinkDecorator;

/**
 * Provides an demonstration of Table and Form editor pattern, and the use
 * CayenneForm and LinkDecorator classes.
 *
 * @author Malcolm Edgar
 */
public class EditTable extends BorderPage {

    public CayenneForm form = new CayenneForm("form", Customer.class);
    public Table table = new Table();
    public ActionLink editLink = new ActionLink("edit", "Edit", this, "onEditClick");
    public ActionLink deleteLink = new ActionLink("delete", "Delete", this, "onDeleteClick");

    // ------------------------------------------------------------ Constructor

    public EditTable() {
        // Setup customers form
        FieldSet fieldSet = new FieldSet("customer");
        fieldSet.add(new TextField("name"));
        fieldSet.add(new EmailField("email"));
        fieldSet.add(new DoubleField("holdings"));
        fieldSet.add(new DateField("dateJoined"));
        form.add(fieldSet);
        form.add(new Submit("save", this, "onSaveClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));
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
    }

    // --------------------------------------------------------- Event Handlers

    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        Customer customer = getCustomerService().getCustomerForID(id);
        if (customer != null) {
            form.setDataObject(customer);
        }
        return true;
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        getCustomerService().deleteCustomer(id);
        return true;
    }

    public boolean onSaveClick() {
        if (form.isValid()) {
            // Please note with Cayenne ORM this will persist any changes
            // to data objects submitted by the form.
            form.getDataObject();
            getDataContext().commitChanges();
            form.setDataObject(null);
        }
        return true;
    }

    public boolean onCancelClick() {
        getDataContext().rollbackChanges();
        form.setDataObject(null);
        form.clearErrors();
        return true;
    }

    /**
     * @see org.apache.click.Page#onGet()
     */
    public void onGet() {
        form.getField(Table.PAGE).setValue("" + table.getPageNumber());
        form.getField(Table.COLUMN).setValue(table.getSortedColumn());
    }

    /**
     * @see org.apache.click.Page#onPost()
     */
    public void onPost() {
        String pageNumber = form.getField(Table.PAGE).getValue();
        table.setPageNumber(Integer.parseInt(pageNumber));
        table.setSortedColumn(form.getField(Table.COLUMN).getValue());
    }

    /**
     * @see org.apache.click.Page#onRender()
     */
    public void onRender() {
        List customers = getCustomerService().getCustomers();
        table.setRowList(customers);
    }

}
