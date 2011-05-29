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
package org.apache.click.examples.page.ajax.compare;

import org.apache.click.Control;
import org.apache.click.ActionResult;
import org.apache.click.ajax.DefaultAjaxBehavior;
import org.apache.click.control.ActionLink;
import org.apache.click.examples.page.BorderPage;

/**
 * An Ajax example using jQuery library.
 */
public class JqueryAjaxDemoPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private ActionLink link = new ActionLink("link", "Make Ajax Request");

    public JqueryAjaxDemoPage() {
        link.setId("link-id");

        addControl(link);

        // Add an Ajax behavior to the link. The behavior will be invoked when the
        // link is clicked. See the basic-ajax-demo.htm template for the client-side
        // Ajax code
        link.addBehavior(new DefaultAjaxBehavior() {

            @Override
            public ActionResult onAction(Control source) {
                // Formatted date instance that will be added to the
                String now = format.currentDate("MMM, yyyy dd HH:mm:ss");

                String msg = "Hello from jQuery at: " + now;
                // Return an action result containing the message
                return new ActionResult(msg, ActionResult.HTML);
            }
        });
    }
}
