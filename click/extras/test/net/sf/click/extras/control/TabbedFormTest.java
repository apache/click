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
package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.MockContainer;
import net.sf.click.MockContext;

public class TabbedFormTest extends TestCase {

    public void testGetHtmlImport() {
        MockContainer container = new MockContainer("web");
        container.start();

        // MockContext is created when a container tests a page. There
        // are no pages to test here so we manually create a MockContext,
        // but reuse the Mock Servlet objects created in the container.
        MockContext.initContext(container.getServletConfig(), container.getRequest(), container.getResponse(), container.getClickServlet());

        TabbedForm form = new TabbedForm("form");

        assertTrue(form.toString().indexOf("<form") > 0);
        assertTrue(form.getHtmlImports().indexOf("/control.js") > 0);
        assertTrue(form.getHtmlImports().indexOf("/control.css") > 0);
        assertTrue(form.getHtmlImports().indexOf("/extras-control.css") > 0);
        
        container.stop();
    }
}
