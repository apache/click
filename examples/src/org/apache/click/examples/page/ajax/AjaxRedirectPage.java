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

import java.util.Date;
import org.apache.click.ActionResult;
import org.apache.click.Context;
import org.apache.click.Control;
import org.apache.click.ajax.DefaultAjaxBehavior;
import org.apache.click.control.ActionLink;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides an example Page showing how to perform redirects with Ajax requests.
 *
 * Since Ajax does not support redirects we return the url to redirect to in a
 * custom response url called 'REDIRECT_URL'. The client-side then uses JavaScript
 * to simulate a redirect by setting the value of window.location to the redirect
 * url.
 *
 */
public class AjaxRedirectPage extends BorderPage {

    private static final long serialVersionUID = 1L;
    private ActionLink redirectLink = new ActionLink("redirectLink");

    @Override
    public void onInit() {
        super.onInit();
        redirectLink.setId("redirectLinkId");
        addControl(redirectLink);

        redirectLink.addBehavior(new DefaultAjaxBehavior() {

            @Override
            public ActionResult onAction(Control source) {
                Context context = getContext();
                context.setFlashAttribute("flash", "Redirected at " + new Date());

            String redirectUrl = context.getPagePath(AjaxRedirectPage.class);
            String contextPath = context.getRequest().getContextPath();
            redirectUrl = contextPath + redirectUrl;

            context.getResponse().setHeader("REDIRECT_URL",redirectUrl);

            // We are redirecting so no response is returned
            return null;
            }
        });
    }
}
