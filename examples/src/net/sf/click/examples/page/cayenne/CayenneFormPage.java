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
package net.sf.click.examples.page.cayenne;

import java.util.List;

import net.sf.click.control.Column;
import net.sf.click.control.FieldSet;
import net.sf.click.control.TextField;
import net.sf.click.examples.domain.Client;
import net.sf.click.extras.cayenne.QuerySelect;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;

import org.apache.cayenne.DataObject;

/**
 * Provides a CayenneForm and QuerySelect control demonstration.
 *
 * @see FormTablePage
 *
 * @author Malcolm Edgar
 */
public class CayenneFormPage extends FormTablePage {

    // ------------------------------------------------------------ Constructor

    /**
     * Create a CayenneFormPage object.
     */
    public CayenneFormPage() {
        FieldSet clientFieldSet = new FieldSet("Client");
        form.add(clientFieldSet);

        QuerySelect querySelect = new QuerySelect("title", true);
        querySelect.setQueryValueLabel("titles", "value", "label");
        clientFieldSet.add(querySelect);

        clientFieldSet.add(new TextField("firstName"));
        clientFieldSet.add(new TextField("lastName"));
        clientFieldSet.add(new DateField("dateJoined"));
        clientFieldSet.add(new EmailField("email"));

        FieldSet addressFieldSet = new FieldSet("Address");
        form.add(addressFieldSet);

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

    // -------------------------------------------------------- Public Methods

    /**
     * @see FormTablePage#getDataObject(Object)
     */
    public DataObject getDataObject(Object id) {
        return getClientService().getClient(id);
    }

    /**
     * @see FormTablePage#getDataObjectClass()
     */
    public Class getDataObjectClass() {
        return Client.class;
    }

    /**
     * @see FormTablePage#getRowList()
     */
    public List getRowList() {
        return getClientService().getClients();
    }

}
