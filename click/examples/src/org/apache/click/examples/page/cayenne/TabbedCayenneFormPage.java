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
package org.apache.click.examples.page.cayenne;

import java.util.List;

import javax.annotation.Resource;

import org.apache.cayenne.DataObject;
import org.apache.click.control.Column;
import org.apache.click.control.FieldSet;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.Client;
import org.apache.click.examples.service.ClientService;
import org.apache.click.extras.cayenne.CayenneForm;
import org.apache.click.extras.cayenne.QuerySelect;
import org.apache.click.extras.cayenne.TabbedCayenneForm;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.IntegerField;
import org.springframework.stereotype.Component;

/**
 * Provides a TabbedCayenneForm and QuerySelect control demonstration.
 *
 * @see FormTablePage
 */
@Component
public class TabbedCayenneFormPage extends FormTablePage {

    private static final long serialVersionUID = 1L;

    @Resource(name="clientService")
    private ClientService clientService;

    // Constructor -----------------------------------------------------------

    /**
     * Create a TabbedCayenneFormPage object.
     */
    public TabbedCayenneFormPage() {
        ((TabbedCayenneForm)form).setBackgroundColor("#eee");
        ((TabbedCayenneForm)form).setTabHeight("155px");
        ((TabbedCayenneForm)form).setTabWidth("305px");

        FieldSet clientFieldSet = new FieldSet("Client");
        ((TabbedCayenneForm)form).addTabSheet(clientFieldSet);

        QuerySelect querySelect = new QuerySelect("title", true);
        querySelect.setQueryValueLabel("titles", "value", "label");
        clientFieldSet.add(querySelect);

        clientFieldSet.add(new TextField("firstName"));
        clientFieldSet.add(new TextField("lastName"));
        clientFieldSet.add(new DateField("dateJoined"));
        clientFieldSet.add(new EmailField("email"));

        FieldSet addressFieldSet = new FieldSet("Address");
        ((TabbedCayenneForm)form).addTabSheet(addressFieldSet);

        addressFieldSet.add(new TextField("address.line1", "Line One"));
        addressFieldSet.add(new TextField("address.line2", "Line Two"));
        addressFieldSet.add(new TextField("address.suburb", "Suburb"));

        querySelect = new QuerySelect("address.state", "State", true);
        querySelect.setQueryValueLabel("states", "value", "label");
        addressFieldSet.add(querySelect);

        IntegerField postCodeField = new IntegerField("address.postCode", "Post Code");
        postCodeField.setMaxLength(5);
        postCodeField.setSize(5);
        addressFieldSet.add(postCodeField);

        // Table
        table.addColumn(new Column("id"));
        table.addColumn(new Column("name"));

        Column column = new Column("email");
        column.setAutolink(true);
        table.addColumn(column);

        table.addColumn(new Column("address.state", "State"));

        column = new Column("dateJoined");
        column.setFormat("{0,date,dd MMM yyyy}");
        table.addColumn(column);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * @see FormTablePage#createForm()
     */
    @Override
    public CayenneForm createForm() {
        return new TabbedCayenneForm();
    }

    /**
     * @see FormTablePage#getDataObject(Object)
     */
    @Override
    public DataObject getDataObject(Object id) {
        return clientService.getClient(id);
    }

    /**
     * @see FormTablePage#getDataObjectClass()
     */
    @Override
    public Class getDataObjectClass() {
        return Client.class;
    }

    /**
     * @see FormTablePage#getRowList()
     */
       @Override
    public List getRowList() {
        return clientService.getClients();
    }

}
