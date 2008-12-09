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
package net.sf.click.examples.page.wizard;

import java.util.List;
import net.sf.click.Context;
import net.sf.click.control.AbstractLink;
import net.sf.click.control.Column;
import net.sf.click.control.PageLink;
import net.sf.click.control.Table;
import net.sf.click.examples.domain.PostCode;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.LinkDecorator;
import net.sf.click.util.HtmlStringBuffer;

/**
 * This Page provides a table to select the postal code from.
 *
 * It also acts as a fork in step 2, to show how one can navigate away from
 * a stateful page and then go back, with the components on the WizardPage still
 * populated with their old values.
 *
 * @author Bob Schellink
 */
public class SelectPostCode extends BorderPage {

    /** Reference to the table. */
    private Table table = new Table("table");

    /**
     * Default constructor.
     */
    public SelectPostCode() {
        table.addColumn(new Column("postCode"));
        table.addColumn(new Column("state"));
        table.addColumn(new Column("locality"));
        PageLink selectState = new PageLink("select", WizardPage.class);
        Column action = new Column("action");
        LinkDecorator decorator = new LinkDecorator(table, selectState, "postCode") {

            /**
             * Override default implementation to send parameters
             * address.postCode and address.state to the WizardPage.
             */
            protected void renderActionLink(HtmlStringBuffer buffer,
                AbstractLink link, Context context, Object row, Object value) {
                // Remove the default parameter name set for the value
                String idPropertyValue = (String) link.getParameters().remove(idProperty);
                
                // Add extra parameters for each row to the rendered action link
                link.setParameter("address.postCode", idPropertyValue);
                link.setParameter("address.state", ((PostCode) row).getState());
                super.renderActionLink(buffer, link, context, row, value);
            }
        };

        action.setDecorator(decorator);
        table.addColumn(action);

        table.setClass(Table.CLASS_BLUE2);
        addControl(table);
    }

    /**
     * Override onRender to populate the table row data.
     */
    public void onRender() {
        List states = getPostCodeService().getPostCodes();
        table.setRowList(states);
    }
}
