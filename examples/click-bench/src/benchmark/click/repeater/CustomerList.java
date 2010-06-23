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
package benchmark.click.repeater;

import benchmark.click.pages.EditCustomer;
import org.apache.click.Page;

import benchmark.dao.CustomerDao;
import benchmark.dao.Customer;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.clickclick.control.html.table.Cell;
import net.sf.clickclick.control.html.table.HtmlTable;
import net.sf.clickclick.control.html.table.Row;
import net.sf.clickclick.control.html.table.TableBody;
import net.sf.clickclick.control.html.table.TableHeader;
import net.sf.clickclick.control.repeater.Repeater;
import net.sf.clickclick.control.repeater.RepeaterRow;
import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.AbstractContainer;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Container;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.util.HtmlStringBuffer;

// 427 l
// 399 dp with index remove index
// 470 l
// 440 dp with index remove index
// 
public class CustomerList extends Page {

    private HtmlTable table;

    private ActionLink editLink = new ActionLink("Edit", this, "onEditClick");

    private ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    private final SimpleDateFormat FORMAT = new SimpleDateFormat("MMMM d, yyyy");

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
                EditCustomer editPage = getContext().createPage(EditCustomer.class);
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
                setRedirect(CustomerList.class);
                return false;
            }
        });

        TableHeader header = new TableHeader();
        header.setColumns("First Name", "Last Name", "State", "Birth Date", "Options");
        table.add(header);

        TableBody body = new TableBody();
        table.add(body);

        Repeater repeater = new Repeater() {
             public void buildRow(final Object item, final RepeaterRow row, final int index) {
                final Customer customer = (Customer) item;

                Row tableRow = new Row();
                tableRow.add(customer.getFirstName(),
                        customer.getLastName(),
                        customer.getState(),
                        FORMAT.format(customer.getBirthDate()));

                Cell actions = new Cell();

                final AbstractContainer editLinkWrapper = new AbstractContainer("Edit") {

                    @Override
                    public void render(HtmlStringBuffer buffer) {
                        editLink.setValueObject(customer.getId());
                        editLink.render(buffer);
                    }
                };

                final Container deleteLinkWrapper = new AbstractContainer("Delete") {

                    @Override
                    public void render(HtmlStringBuffer buffer) {
                        deleteLink.setValueObject(customer.getId());
                        deleteLink.render(buffer);
                    }
                };

                actions.add(editLinkWrapper);
                actions.setText(" | ");
                actions.add(deleteLinkWrapper);
                tableRow.add(actions);

                row.add(tableRow);
            }
        };

        repeater.setDataProvider(new DataProvider() {
            public List<Customer> getData() {
                return CustomerDao.getInstance().findAll();
            }
        });

        /*
        List<Customer> customers = CustomerDao.getInstance().findAll();
        repeater.setItems(customers);*/

        body.add(repeater);
    }
}
