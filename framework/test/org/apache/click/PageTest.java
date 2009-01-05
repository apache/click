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
package org.apache.click;

import junit.framework.TestCase;
import org.apache.click.pages.JspRedirectPage;
import org.apache.click.pages.RedirectToHtm;
import org.apache.click.pages.RedirectToJsp;
import org.apache.click.pages.RedirectToSelfPage;
import org.apache.click.pages.SetPathToJspPage;

/**
 * Tests for the Page class.
 */
public class PageTest extends TestCase {

    /**
     * Test that redirecting to a htm works.
     */
    public void testRedirect() {
        MockContainer container = new MockContainer("web");
        container.start();
        String contextPath = container.getRequest().getContextPath();
        container.getRequest().setMethod("GET");

        RedirectToHtm page = (RedirectToHtm) container.testPage(RedirectToHtm.class);

        // assert that the Page successfully redirected to test.htm
        String expected = contextPath + "/test.htm";
        assertEquals(expected, container.getRedirect());
        container.stop();
    }

    /**
     * Test that redirecting to a Page using a JSP template is converted to htm
     * before redirecting. CLK-338
     */
    public void testRedirectToJSP() {
        MockContainer container = new MockContainer("web");
        container.start();
        String contextPath = container.getRequest().getContextPath();
        container.getRequest().setMethod("GET");

        RedirectToJsp page = (RedirectToJsp) container.testPage(RedirectToJsp.class);

        // assert that the Page successfully redirected to jsp-page.htm, meaning
        // Click converted the Page JSP template from jsp-page.jsp to jsp-page.htm
        String expected = contextPath + "/jsp-page.htm";
        assertEquals(expected, container.getRedirect());
        container.stop();
    }

    /**
     * Test custom redirecting which does *not* convert jsp extension to htm.
     * CLK-429
     */
    public void testCustomRedirectToJSP() {
        MockContainer container = new MockContainer("web");
        container.start();
        String contextPath = container.getRequest().getContextPath();
        container.getRequest().setMethod("GET");

        JspRedirectPage page = (JspRedirectPage) container.testPage(JspRedirectPage.class);

        // assert that the Page successfully redirected to test.jsp, meaning
        // Click DID NOT convert the location from test.jsp to test.htm
        String expected = contextPath + "/test.jsp";
        assertEquals(expected, container.getRedirect());
        container.stop();
    }

    /**
     * Test that invoking setRedirect with an existing contextPath won't add
     * the contextPath twice.
     * CLK-456
     */
    public void testRedirectDuplicateContextPath() {
        MockContext context = MockContext.initContext();
        String contextPath = context.getRequest().getContextPath();
        RedirectToHtm page = new RedirectToHtm();
        String redirect = "/test.htm";

        // assert that the Page redirect to /contextPath/test.htm
        String expected = contextPath + redirect;
        page.setRedirect(redirect);
        assertEquals(expected, page.getRedirect());

        // assert that setting redirect to a path already prefixed with contextPath
        // won't add a second contextPath
        page.setRedirect(contextPath + "/test.htm");
        assertEquals(expected, page.getRedirect());
    }

    /**
     * Test that Page.setRedirect properly adds context path even if the page
     * path and context path have the same prefix.
     * CLK-468
     */
    public void testRedirectWherePageAndContextPathAreTheSame() {
        MockContainer container = new MockContainer("web");
        container.start();
        container.getRequest().setMethod("GET");

        RedirectToSelfPage page = (RedirectToSelfPage) container.testPage(RedirectToSelfPage.class);

        assertEquals(RedirectToSelfPage.class.getName(), container.getRedirectPageClass().getName());

        container.stop();
    }

    /**
     * Test that Page.setPath(path), where path is a JSP, works properly. Click
     * should set the request to forward to the JSP page.
     * CLK-141
     */
    public void testSetPathToJSP() {
        MockContainer container = new MockContainer("web");
        container.start();
        container.getRequest().setMethod("GET");

        SetPathToJspPage page = (SetPathToJspPage) container.testPage(SetPathToJspPage.class);
        assertEquals(SetPathToJspPage.PATH, container.getForward());

        container.stop();
    }
}
