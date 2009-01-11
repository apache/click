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
package benchmark.click.pages;

import org.apache.click.Page;
import org.apache.click.control.AbstractLink;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Table;
import org.apache.click.extras.control.LinkDecorator;

import benchmark.dao.CustomerDao;
import benchmark.dao.Customer;

public class CustomerList extends Page {

    private Table table;

    private ActionLink editLink = new ActionLink("Edit", this, "onEditClick");

    private ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    public CustomerList() {
        table = new Table("table");
        table.addStyleClass("decorated");
        Column first = new Column("firstName");
        table.addColumn(first);
        Column last = new Column("lastName");
        table.addColumn(last);
        Column state = new Column("state");
        table.addColumn(state);
        Column dob = new Column("birthDate", "Date of Birth");
        dob.setFormat("{0,date,MMMM d, yyyy}");
        table.addColumn(dob);

        Column actions = new Column("Options");
        AbstractLink[] links = new AbstractLink[]{editLink, deleteLink};
        actions.setDecorator(new LinkDecorator(table, links, "id"));
        table.addColumn(actions);

        addControl(table);
        addControl(editLink);
        addControl(deleteLink);
    }

    public void onRender() {
        table.setRowList(CustomerDao.getInstance().findAll());
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        Customer customer = CustomerDao.getInstance().findById(id);
        CustomerDao.getInstance().delete(customer);
        return true;
    }

    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        Customer customer = CustomerDao.getInstance().findById(id);
        EditCustomer editPage = (EditCustomer) getContext().createPage(EditCustomer.class);
        editPage.setCustomer(customer);
        setForward(editPage);
        return false;
    }
}
