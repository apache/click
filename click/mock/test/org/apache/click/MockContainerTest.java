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

import java.io.File;
import java.net.URI;
import java.net.URL;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.click.control.Form;
import org.apache.click.pages.BorderTestPage;
import org.apache.click.pages.FormPage;
import org.apache.click.pages.ForwardPage;
import org.apache.click.pages.RedirectPage;
import org.apache.click.pages.TestPage;

/**
 * Sanity tests for MockContainer.
 */
public class MockContainerTest extends TestCase {

    /**
     * Test TestPage.
     */
    public void testPage() {
        try {
            MockContainer container = new MockContainer("web");

            container.start();

            // Set a parameter
            container.setParameter("myparam", "Some parameter");

            // Upload a file
            URL resource = container.getClass().getResource("/web/test.htm");
            URI uri = new URI(resource.toString());
            container.setParameter("myfile", new File(uri), "text/html");

            // First run
            Page testPage = container.testPage(TestPage.class);

            System.out.println("\nFirst run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml() 
                + "\n===============================================================\n");

            Assert.assertEquals("200", testPage.getContext().getRequestAttribute("id"));

            // Second run
            testPage = container.testPage("test.htm");
            Assert.assertEquals("200", testPage.getContext().getRequestAttribute("id"));

            System.out.println("\nSecond run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            container.stop();//TODO remove need for stop???

            container.start();

            // Third run
            testPage = container.testPage("/test.htm");
            System.out.println("\nThird run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            container.stop();

        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            Assert.fail();
        }
    }

    /**
     * Test BorderPage.
     */
    public void testBroderPage() {
        try {
            MockContainer container = new MockContainer("web");

            container.start();

            // Set a parameter
            container.setParameter("myparam", "Some parameter");

            // Upload a file
            URL resource = container.getClass().getResource("/web/test.htm");
            URI uri = new URI(resource.toString());
            container.setParameter("myfile", new File(uri), "text/html");

            // Process page
            Page testPage = container.testPage(BorderTestPage.class);

            System.out.println(
                "\n======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            // Check that border page markup is available
            Assert.assertTrue(container.getHtml().indexOf("<h1>Header</h1>") >= 0);

        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            Assert.fail();
        }
    }

    /**
     * Test ForwardPage.
     */
    public void testForward() {
        try {
            MockContainer container = new MockContainer("web");

            container.start();

            // Set a parameter
            container.setParameter("myparam", "Some parameter");

            // Upload a file
            URL resource = container.getClass().getResource("/web/test.htm");
            URI uri = new URI(resource.toString());
            container.setParameter("myfile", new File(uri), "text/html");

            // Process page
            Page testPage = container.testPage(TestPage.class);

            System.out.println("\nFirst run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            // ForwardPage forwards to TestPage.class
            testPage = container.testPage(ForwardPage.class);

            // Assert that forwardUrl was set
            Assert.assertEquals("/test.htm", container.getForward());
            Assert.assertEquals(TestPage.class, container.getForwardPageClass());

            // ForwardPage result will be empty because the template is NOT
            // rendered when forwarding a request.
            System.out.println("Forward result: " + container.getHtml());
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            Assert.fail();
        }
    }

    /**
     * Test RedirectPage.
     */
    public void testRedirect() {
        try {
            MockContainer container = new MockContainer("web");

            container.start();

            // Set a parameter
            container.setParameter("myparam", "Some parameter");

            // Upload a file
            URL resource = container.getClass().getResource("/web/test.htm");
            URI uri = new URI(resource.toString());
            container.setParameter("myfile", new File(uri), "text/html");

            // Process page
            Page testPage = container.testPage(TestPage.class);

            System.out.println("\nFirst run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            // RedirectPage redirects to TestPage.class
            testPage = container.testPage(RedirectPage.class);

            // Assert that redirectUrl was set
            System.out.println("Redirect " + container.getRedirect());
            Assert.assertEquals("/mock/test.htm", container.getRedirect());
            Assert.assertEquals(TestPage.class, container.getRedirectPageClass());

            // Alternatively use the container.getDestination() call which returns
            // either the forward or redirect value. For redirect values the 
            // context is removed making for easier testing.
            Assert.assertEquals("/test.htm", container.getForwardOrRedirectUrl());

            // RedirectPage result will be empty because the template is NOT
            // rendered when redirecting a request.
            System.out.println("Redirect result: " + container.getHtml());
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            Assert.fail();
        }
    }

    /**
     * Test FormPage.
     */
    public void testFormBinding() {
        try {
            MockContainer container = new MockContainer("web");

            container.start();

            // Set the form name to ensure a Form submission occurs
            container.setParameter(Form.FORM_NAME, "form");

            // Set the field parameter
            container.setParameter("myfield", "one");

            // Process page
            FormPage formPage = (FormPage) container.testPage(FormPage.class);

            System.out.println("\nFirst run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            // Assert that form with id="form" was rendered
            Assert.assertTrue(container.getHtml().indexOf("id=\"form\"") > 0);

            // Assert that form field "myfield" was bound to request parameter "myfield"
            Assert.assertEquals("one", formPage.getForm().getFieldValue("myfield"));

        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            Assert.fail();
        }
    }
}
