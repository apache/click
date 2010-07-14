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

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.cayenne.BaseContext;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Checkbox;
import org.apache.click.control.Column;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.control.TextField;
import org.apache.click.examples.control.InvestmentSelect;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.DoubleField;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.FieldColumn;
import org.apache.click.extras.control.FormTable;
import org.apache.click.extras.control.LinkDecorator;
import org.apache.click.extras.control.NumberField;
import org.springframework.stereotype.Component;

/**
 * Provides a CRUD demonstration using Form and FormTable.
 * <p/>
 * Note the following:
 *
 * #1. Form child controls are only processed on Form submission. In order to
 * process Form child controls when form is *not* submitted, Form.onProcess
 * must be overridden and the controls explicitly processed.
 *
 * #2. By default FormTable creates an internal Form for submissions. However
 * it is possible to use the FormTable constructor which accepts a Form so that
 * FormTable can be added to this "external" Form.
 */
@Component
public class EditFormTablePage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private static final int NUM_ROWS = 20;

    private FormTable table;

    private ActionLink deleteCustomer = new ActionLink("delete", "Delete", this, "onDeleteClick");

    private Form customerForm = new Form("customerForm");

    private Form form = new Form("form") {

        private static final long serialVersionUID = 1L;

        /**
         * #1. PLEASE NOTE: FormTable will only be processed by form if the
         * Form is submitted. Thus paging and sorting won't work by default.
         *
         * Here we override the default behavior and explicitly process
         * FormTable (table) so that paging and sorting will still work, even
         * if the Form was not submitted.
         */
        @Override
        public boolean onProcess() {
            if (form.isFormSubmission()) {
                return super.onProcess();
            } else {
                deleteCustomer.onProcess();
                return table.onProcess();
            }
        }
    };

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public EditFormTablePage() {
        // Setup customers form
        FieldSet fieldSet = new FieldSet("customer");
        fieldSet.add(new TextField("name")).setRequired(true);
        fieldSet.add(new EmailField("email")).setRequired(true);
        fieldSet.add(new InvestmentSelect("investments")).setRequired(true);
        fieldSet.add(new DoubleField("holdings"));
        DateField dateJoined = new DateField("dateJoined");
        dateJoined.setDate(new Date());
        fieldSet.add(dateJoined);
        customerForm.add(fieldSet);
        customerForm.add(new Submit("add", "Add Customer", this, "onAddClick"));

        // #2. Create the FormTable and pass in the existing Form into the
        // constructor. FormTable now knows it should not create an internal
        // Form instance.
        table = new FormTable("table", form);

        // Assemble the FormTable columns
        table.setClass(Table.CLASS_SIMPLE);
        table.setWidth("700px");
        table.setPageSize(5);
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
        numberField.setSize(5);
        column = new FieldColumn("holdings", numberField);
        column.setTextAlign("right");
        table.addColumn(column);

        column = new FieldColumn("dateJoined", new DateField());
        column.setDataStyle("white-space", "nowrap");
        table.addColumn(column);

        column = new FieldColumn("active", new Checkbox());
        column.setTextAlign("center");
        table.addColumn(column);

        Column actionColumn = new Column("Action");
        actionColumn.setSortable(false);
        ActionLink[] links = new ActionLink[]{deleteCustomer};
        actionColumn.setDecorator(new LinkDecorator(table, links, "id"));
        table.addColumn(actionColumn);

        deleteCustomer.setAttribute("onclick", "return window.confirm('Please confirm delete');");

        table.getForm().add(new Submit("update", "Update Customers", this, "onUpdateCustomersClick"));
        table.getForm().add(new Submit("cancel", this, "onCancelClick"));

        table.setSortable(true);

        fieldSet = new FieldSet("customers");
        form.add(fieldSet);

        // Add FormTable to FieldSet which is attached to Form
        fieldSet.add(table);

        addControl(customerForm);
        addControl(form);
    }

    // Event Handlers ---------------------------------------------------------

    @Override
    public boolean onSecurityCheck() {
        String pagePath = getContext().getPagePath(getClass());

        // In this demo we protect against duplicate post submissions
        if (form.onSubmitCheck(this, pagePath)) {
            return true;
        } else {
            getContext().setFlashAttribute("error", getMessage("invalid.form.submit"));
            return false;
        }
    }

    /**
     * @see org.apache.click.Page#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();

        refreshTableCustomers();
    }

    public boolean onUpdateCustomersClick() {
        if (form.isValid()) {
            // Please note with Cayenne ORM this will persist any changes
            // to data objects submitted by the form.
            BaseContext.getThreadObjectContext().commitChanges();
        }
        return true;
    }

    public boolean onCancelClick() {
        // Rollback any changes made to the customers, which are stored in
        // the data context
         BaseContext.getThreadObjectContext().rollbackChanges();

        refreshTableCustomers();

        table.setRenderSubmittedValues(false);

        form.clearErrors();

        return true;
    }

    public boolean onDeleteClick() {
        Integer id = deleteCustomer.getValueInteger();
        customerService.deleteCustomer(id);

        // The FormTable customer were already set in the onInit phase. Because
        // a customer was deleted we refresh the FormTable row list
        refreshTableCustomers();

        return true;
    }

     public boolean onInsertClick() {
        Customer customer = new Customer();
        customer.setName("Alpha");
        customer.setDateJoined(new Date());
        customerService.saveCustomer(customer);

        // The FormTable customer were already set in the onInit phase. Because
        // a customer was deleted we refresh the FormTable row list
        refreshTableCustomers();

        return true;
    }

    public boolean onAddClick() {
        if (customerForm.isValid()) {
            Customer customer = new Customer();
            customerForm.copyTo(customer);
            customerService.saveCustomer(customer);

            // The FormTable customer was set in the onInit phase. Since we just
            // added a new customer we refresh the FormTable row list
            refreshTableCustomers();
        }
        return true;
    }

    // Private Methods --------------------------------------------------------

    private void refreshTableCustomers() {
        List<Customer> allCustomers = customerService.getCustomersSortedBy(Customer.DATE_JOINED_PROPERTY, false);
        if (!allCustomers.isEmpty()) {
            List<Customer> customers = allCustomers.subList(0, NUM_ROWS);
            table.setRowList(customers);
        }
    }
}
