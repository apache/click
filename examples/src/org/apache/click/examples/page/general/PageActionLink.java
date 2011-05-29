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
package org.apache.click.examples.page.general;

import org.apache.click.ActionResult;
import org.apache.click.control.PageLink;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides a PageAction demo. The PageLink sets a PAGE_ACTION to the page method
 * 'getDate'. Clicking on the PageLink will invoke the 'getDate' method and render
 * the ActionResult.
 */
public class PageActionLink extends BorderPage {

    private static final long serialVersionUID = 1L;

    private PageLink link = new PageLink("link", "Get Date", PageActionLink.class);

    public PageActionLink() {
        addControl(link);

        // We set a PAGE_ACTION to invoke the getDate method below
        link.setParameter(PAGE_ACTION, "getDate");
    }

    /**
     * This method will be invoked when the link is clicked.
     */
    public ActionResult getDate() {
        return new ActionResult(format.currentDate("MMM dd, yyyy HH:mm:ss"));
    }
}
