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
package net.sf.click.examples.page.table;

import java.text.MessageFormat;
import java.util.List;

import net.sf.click.control.Column;
import net.sf.click.control.Table;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides an demonstration of Table control paging.
 *
 * @author Malcolm Edgar
 */
public class TableFooter extends BorderPage {

    public Table table;

    public TableFooter() {
        table = new Table() {
            public void renderFooterRow(HtmlStringBuffer buffer) {
                renderTotalHoldingsFooter(buffer);
            }
        };

        // Setup customers table
        table.setClass(Table.CLASS_ITS);

        Column column = new Column("name");
        column.setWidth("140px;");
        table.addColumn(column);

        column = new Column("email");
        column.setAutolink(true);
        column.setWidth("230px;");
        table.addColumn(column);

        column = new Column("age");
        column.setTextAlign("center");
        column.setWidth("40px;");
        table.addColumn(column);

        column = new Column("holdings");
        column.setFormat("${0,number,#,##0.00}");
        column.setTextAlign("right");
        column.setWidth("100px;");
        table.addColumn(column);
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        List customers = getCustomerService().getCustomersSortedByName(17);
        table.setRowList(customers);
    }

    /**
     * Render the total holdings footer.
     *
     * @param buffer the buffer to render the totals footer to
     */
    private void renderTotalHoldingsFooter(HtmlStringBuffer buffer) {
        double total = 0;
        for (int i = 0; i < table.getRowList().size(); i++) {
            Customer customer = (Customer) table.getRowList().get(i);
            if (customer.getHoldings() != null) {
                total += customer.getHoldings().doubleValue();
            }
        }

        String format = "<b>Total Holdings</b>: &nbsp; ${0,number,#,##0.00}";
        String totalDisplay = MessageFormat.format(format, new Object[] { new Double(total) });

        buffer.append("<tfoot><tr><td colspan='4' style='text-align:right'>");
        buffer.append(totalDisplay);
        buffer.append("</td></tr></tfoot>");
    }

}
