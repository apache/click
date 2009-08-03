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
package benchmark.click.container;

import benchmark.click.pages.EditCustomer;
import org.apache.click.Control;
import org.apache.click.Page;
import org.apache.click.control.ActionLink;

import benchmark.dao.CustomerDao;
import benchmark.dao.Customer;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.clickclick.control.html.table.Cell;
import net.sf.clickclick.control.html.table.HeaderCell;
import net.sf.clickclick.control.html.table.HtmlTable;
import net.sf.clickclick.control.html.table.Row;
import org.apache.click.ActionListener;
import org.apache.click.control.AbstractContainer;
import org.apache.click.control.Container;
import org.apache.click.util.HtmlStringBuffer;

public class CustomerList extends Page {

    private HtmlTable table;

    private ActionLink editLink = new ActionLink("Edit", this, "onEditClick");

    private ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    private final SimpleDateFormat FORMAT = new SimpleDateFormat(
        "MMMM d, yyyy");

    public CustomerList() {
        table = new HtmlTable("table");
        table.addStyleClass("decorated");

        addControl(table);
        addControl(editLink);
        addControl(deleteLink);

        editLink.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                Integer id = editLink.getValueInteger();
                Customer customer = CustomerDao.getInstance().findById(id);
                EditCustomer editPage = (EditCustomer) getContext().createPage(
                    EditCustomer.class);
                editPage.setCustomer(customer);
                setForward(editPage);
                return true;
            }
        });

        deleteLink.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                Integer id = deleteLink.getValueInteger();
                Customer customer = CustomerDao.getInstance().findById(id);
                CustomerDao.getInstance().delete(customer);
                return true;
            }
        });
    }

    public void onRender() {
        Container thead = new AbstractContainer() {
            public String getTag() {
                return "thead";
            }
        };
        thead.add(createHeaderRow());
        table.add(thead);
        
        List<Customer> customers = CustomerDao.getInstance().findAll();
        for (Customer customer : customers) {
            Row row = createRow(customer);
            table.add(row);
        }
    }

    private Row createHeaderRow() {
        Row row = new Row();
        Cell cell = new HeaderCell();
        cell.setText("First Name");
        row.add(cell);

        cell = new HeaderCell();
        cell.setText("Last Name");
        row.add(cell);

        cell = new HeaderCell();
        cell.setText("State");
        row.add(cell);

        cell = new HeaderCell();
        cell.setText("Birth Date");
        row.add(cell);

        cell = new HeaderCell();
        cell.setText("Options");
        row.add(cell);
        return row;
    }

    private Row createRow(final Customer customer) {
        Row row = new Row();
        Cell cell = new Cell();
        row.add(cell);
        cell.setText(customer.getFirstName());

        cell = new Cell();
        row.add(cell);
        cell.setText(customer.getLastName());

        cell = new Cell();
        row.add(cell);
        cell.setText(customer.getState());

        cell = new Cell();
        row.add(cell);
        cell.setText(FORMAT.format(customer.getBirthDate()));


        cell = new Cell();
        
        final AbstractContainer editLinkWrapper = new AbstractContainer("Edit") {
            public void render(HtmlStringBuffer buffer) {
                editLink.setValue(customer.getId().toString());
                editLink.render(buffer);
            }
        };

        
        final Container deleteLinkWrapper = new AbstractContainer("Delete") {
            public void render(HtmlStringBuffer buffer) {
                deleteLink.setValue(customer.getId().toString());
                deleteLink.render(buffer);
            }
        };

        cell.add(editLinkWrapper);
        cell.setText(" | ");
        cell.add(deleteLinkWrapper);
        row.add(cell);

        return row;
    }
}
