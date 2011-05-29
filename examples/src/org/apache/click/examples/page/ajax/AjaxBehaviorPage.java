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
package org.apache.click.examples.page.ajax;

import org.apache.click.Control;
import org.apache.click.ActionResult;
import org.apache.click.ajax.DefaultAjaxBehavior;
import org.apache.click.control.ActionLink;
import org.apache.click.examples.page.BorderPage;

/**
 * Demonstrates how to handle AJAX requests with an Ajax Behavior. The Behavior
 * is added to the ActionLink and its onAction method is invoked to handle the
 * AJAX request. The onAction method returns an ActionResult that is rendered
 * to the browser.
 *
 * The client-side is implemented using the jQuery library.
 */
public class AjaxBehaviorPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private ActionLink link = new ActionLink("link", "here");

    public AjaxBehaviorPage() {
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

                String msg = "AjaxBehavior <tt>onAction()</tt> method invoked at: " + now;
                // Return an action result containing the message
                return new ActionResult(msg, ActionResult.HTML);
            }
        });
    }
}
