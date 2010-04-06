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

import org.apache.click.control.AbstractLink;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.click.control.PageLink;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.control.TextField;
import org.apache.click.examples.control.SpacerButton;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.EditCustomer;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.LinkDecorator;
import org.apache.click.extras.control.TableInlinePaginator;
import org.apache.click.util.Bindable;
import org.apache.click.dataprovider.DataProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Provides an demonstration of Table control paging.
 */
public class SearchTablePage extends BorderPage implements ApplicationContextAware {

    private static final long serialVersionUID = 1L;

    @Bindable protected Form form = new Form();
    @Bindable protected Table table = new Table();
    @Bindable protected PageLink editLink = new PageLink("Edit", EditCustomer.class);
    @Bindable protected ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    private TextField nameField = new TextField(Customer.NAME_PROPERTY);
    private DateField dateField = new DateField(Customer.DATE_JOINED_PROPERTY, "Start Date");

    private transient ApplicationContext applicationContext;

    // Constructor ------------------------------------------------------------

    public SearchTablePage() {
        setStateful(true);

        // Setup the search form
        form.setColumns(2);
        form.add(nameField);
        form.add(dateField);
        form.add(new Submit("Search"));
        form.add(new Submit("Clear", this, "onClearClick"));
        form.add(new SpacerButton());
        form.add(new Submit("New...", this, "onNewClick"));

        // Setup customers table
        table.setClass(Table.CLASS_ITS);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setSortable(true);
        table.setPaginator(new TableInlinePaginator(table));
        table.setPaginatorAttachment(Table.PAGINATOR_INLINE);

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
        column.setFormat("${0,number,#,##0.00}");
        column.setTextAlign("right");
        column.setWidth("100px;");
        table.addColumn(column);

        editLink.setImageSrc("/assets/images/table-edit.png");
        editLink.setTitle("Edit customer details");
        editLink.setParameter("referrer", "/table/search-table.htm");

        deleteLink.setImageSrc("/assets/images/table-delete.png");
        deleteLink.setTitle("Delete customer record");
        deleteLink.setAttribute("onclick", "return window.confirm('Are you sure you want to delete this record?');");

        column = new Column("Action");
        column.setTextAlign("center");
        AbstractLink[] links = new AbstractLink[] { editLink, deleteLink };
        column.setDecorator(new LinkDecorator(table, links, "id"));
        column.setSortable(false);
        table.addColumn(column);

        table.setDataProvider(new DataProvider<Customer>() {
            public List<Customer> getData() {
                return getCustomerService().getCustomers(nameField.getValue(),
                                                         dateField.getDate());
            }
        });
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * Handle the clear button click event.
     *
     * @return true
     */
    public boolean onClearClick() {
        form.clearErrors();
        form.clearValues();
        return true;
    }

    /**
     * Handle the new button click event.
     *
     * @return false
     */
    public boolean onNewClick() {
        String path = getContext().getPagePath(EditCustomer.class);
        path += "?referrer=/table/search-table.htm";
        setRedirect(path);
        return false;
    }

    /**
     * Handle the delete link click event.
     *
     * @return true
     */
    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        getCustomerService().deleteCustomer(id);
        return true;
    }

    // Public Methods ---------------------------------------------------------

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
