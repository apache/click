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
package org.apache.click.examples.page.wizard;

import java.util.List;

import javax.annotation.Resource;

import org.apache.click.Context;
import org.apache.click.control.AbstractLink;
import org.apache.click.control.Column;
import org.apache.click.control.PageLink;
import org.apache.click.control.Table;
import org.apache.click.examples.domain.PostCode;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.PostCodeService;
import org.apache.click.extras.control.LinkDecorator;
import org.apache.click.util.HtmlStringBuffer;
import org.springframework.stereotype.Component;

/**
 * This Page provides a table to select the postal code from.
 *
 * It also acts as a fork in step 2, to show how one can navigate away from
 * a stateful page and then go back, with the components on the WizardPage still
 * populated with their old values.
 */
@Component
public class SelectPostCode extends BorderPage {

    private static final long serialVersionUID = 1L;

    /** Reference to the table. */
    private Table table = new Table("table");

    @Resource(name="postCodeService")
    private PostCodeService postCodeService;

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

            private static final long serialVersionUID = 1L;

            /**
             * Override default implementation to send parameters
             * address.postCode and address.state to the WizardPage.
             */
            @Override
            protected void renderActionLink(HtmlStringBuffer buffer,
                AbstractLink link, Context context, Object row, Object value) {
                // Remove the default parameter name set for the value
                link.setParameter(idProperty, null);

                PostCode postCode = (PostCode) row;
                // Add extra parameters for each row to the rendered action link
                link.setParameter("address.postCode", postCode.getPostCode());
                link.setParameter("address.state", postCode.getState());
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
    @Override
    public void onRender() {
        List<PostCode> states = postCodeService.getPostCodes();
        table.setRowList(states);
    }

}
