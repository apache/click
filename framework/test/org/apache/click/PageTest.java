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
import org.apache.click.control.Form;
import org.apache.click.pages.HeaderTestPage;
import org.apache.click.pages.JspRedirectPage;
import org.apache.click.pages.RedirectToHtm;
import org.apache.click.pages.RedirectToJsp;
import org.apache.click.pages.RedirectToSelfPage;
import org.apache.click.pages.RequestBindingPage;
import org.apache.click.pages.SetPathToJspPage;
import org.apache.click.servlet.MockRequest;

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

        container.testPage(RedirectToHtm.class);

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

        container.testPage(RedirectToJsp.class);

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

        container.testPage(JspRedirectPage.class);

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

        container.testPage(RedirectToSelfPage.class);

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

        container.testPage(SetPathToJspPage.class);
        assertEquals(SetPathToJspPage.PATH, container.getForward());

        container.stop();
    }

    /**
     * Test getMessage variations.
     */
    public void testGetMessage() {
        MockContext.initContext();

        String expected = "Version 0.21";

        Page page = new Page();
        String version = page.getMessage("version");
        assertEquals(expected, version);

        version = page.getMessage("version", "arg");
        assertEquals(expected, version);

        version = page.getMessage("version", "arg1", "arg2");
        assertEquals(expected, version);

        version = page.getMessage("version", (String) null);
        assertEquals(expected, version);

        Object args[] = new Object[1];
        args[0] = null;
        version = page.getMessage("version", args);
        assertEquals(expected, version);
    }

    /**
     * Check that adding controls replace existing controls with the same name.
     *
     * CLK-666
     */
    public void testReplace() {
        MockContext.initContext();

        Page page = new Page();

        // Add two controls named child1 and child2
        Form child1 = new Form("child1");
        Form child2 = new Form("child2");
        page.addControl(child1);
        page.addControl(child2);
        assertEquals(2, page.getModel().size());
        assertEquals(2, page.getControls().size());
        assertSame(child1, page.getControls().get(0));
        assertSame(child2, page.getControls().get(1));

        // Add another two controls named child1 and child2 and test that these
        // controls replaces the previous controls
        child1 = new Form("child1");
        child2 = new Form("child2");
        page.addControl(child1);
        page.addControl(child2);
        assertEquals(2, page.getModel().size());
        assertEquals(2, page.getControls().size());
        assertSame(child1, page.getControls().get(0));
        assertSame(child2, page.getControls().get(1));
    }

    /**
     * Check that headers can be set during page constructor
     *
     * CLK-711
     */
    public void testSetPageHeaders() {
        MockContext.initContext();

        HeaderTestPage headerTestPage = new HeaderTestPage();
        assertEquals(headerTestPage.getHeaders().get(headerTestPage.expiresHeader), headerTestPage.expiresValue);
    }

    /**
     * Check that headers can be set during page constructor and that default
     * headers won't override the explicitly set headers.
     *
     * CLK-711
     */
    public void testOverrideDefaultHeaders() {
        MockContainer container = new MockContainer("web");
        container.start();

        HeaderTestPage headerTestPage = container.testPage(HeaderTestPage.class);

        System.out.println(headerTestPage.getHeaders());

        // Check that 'Expires' header (a default header) has been set by Page
        // and not been overridden by ClickServlet
        assertEquals(headerTestPage.getHeaders().get(headerTestPage.expiresHeader), headerTestPage.expiresValue);

        // A Default header. In this test we check that this header is added to the page headers
        String pragmaHeader = "Pragma";
        String pragmaValue = "no-cache";
        assertEquals(headerTestPage.getHeaders().get(pragmaHeader), pragmaValue);

        container.stop();
    }

    /**
     * Check that Page variables are bound to request parameters.
     */
    public void testRequestParameterBinding() {
        MockContainer container = new MockContainer("web");
        container.start();

        MockRequest request = container.getRequest();
        String bigDecimalValue = "100.99";
        String stringValue = "hello";
        String boolValue = "true";

        request.setParameter("bigDecimal", bigDecimalValue);
        request.setParameter("string", stringValue);
        request.setParameter("bool", boolValue);

        RequestBindingPage page = container.testPage(RequestBindingPage.class);

        assertEquals(bigDecimalValue.toString(), page.getBigDecimal().toString());
        assertEquals(stringValue, page.getString());
        assertEquals(boolValue, Boolean.toString(page.getBoolean()));

        container.stop();
    }
}
