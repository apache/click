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

import org.apache.click.ActionResult;
import org.apache.click.control.ActionLink;
import org.apache.click.examples.page.BorderPage;

/**
 * Demonstrates how a Page Action can be used to handle an AJAX request.
 * The Page Action is invoked by Click to handle the AJAX request.
 * The Page Action method returns an ActionResult that is rendered to the
 * browser.
 *
 * A Page Action is a regular method defined on a Page with the following signature:
 * - the method must take no arguments
 * - the method must return an ActionResult
 *
 * PageActions provide the simplest way to handle Ajax requests.
 *
 * The client-side is implemented using the jQuery library.
 */
public class PageActionPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    public PageActionPage() {
        ActionLink link = new ActionLink("link", "here");
        link.setId("link-id");
        addControl(link);
    }

    // Note the pageAction method signature: a no-arg method returning an ActionResult
    public ActionResult onLinkClicked() {
        // Formatted date instance that will be returned to the browser
        String now = format.currentDate("MMM, yyyy dd HH:mm:ss");

        String msg = "PageAction method <tt>onLinkClicked()</tt> invoked at: " + now;

        // Return an action result containing the message
        return new ActionResult(msg, ActionResult.HTML);
    }
}
