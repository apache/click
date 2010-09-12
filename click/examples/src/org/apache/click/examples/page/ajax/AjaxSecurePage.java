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
import org.apache.click.Context;
import org.apache.click.Page;
import org.apache.click.control.ActionLink;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.util.ClickUtils;

/**
 * Provides an <tt>onSecurityCheck</tt> example secure Page for handling Ajax
 * requests. Two links are presented to the user. Clicking on the first link will
 * redirect the user to this page and show an error message. Clicking on the
 * second link will show an error message without redirecting to another page.
 */
public class AjaxSecurePage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private ActionLink secureLinkWithRedirect = new ActionLink("secureLinkWithRedirect");
    private ActionLink secureLinkWithMessage = new ActionLink("secureLinkWithMessage");

    /**
     * @see Page#onSecurityCheck()
     */
    @Override
    public boolean onSecurityCheck() {
        // As onSecurityCheck event occurs bfore controls are processed, we use
        // explicit binding to determine which link was clicked

        ClickUtils.bind(secureLinkWithRedirect);
        ClickUtils.bind(secureLinkWithMessage);

        boolean performRedirect = secureLinkWithRedirect.isClicked();
        boolean performShowMessage = secureLinkWithMessage.isClicked();

        Context context = getContext();
        if (performRedirect) {
            context.setFlashAttribute("flash", "You have been <b>Redirected</b> "
                + "since you don't have permission to access the link.");

            String redirectUrl = context.getPagePath(AjaxSecurePage.class);
            String contextPath = context.getRequest().getContextPath();
            redirectUrl = contextPath + redirectUrl;

            context.getResponse().setHeader("REDIRECT_URL",redirectUrl);
            return false;

        } else if (performShowMessage) {
            ActionResult result = new ActionResult("You do not have permission "
                + "to access the link.");
            result.render(context);
            return false;
        }

            return true;
    }

    @Override
    public void onInit() {
        super.onInit();
        secureLinkWithRedirect.setId("secureLinkWithRedirectId");
        secureLinkWithMessage.setId("secureLinkWithMessageId");
        addControl(secureLinkWithRedirect);
        addControl(secureLinkWithMessage);
    }
}
