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
package org.apache.click.extras.control;

import java.util.Collections;
import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.Page;
import org.apache.click.extras.security.RoleAccessController;
import org.apache.click.servlet.MockPrincipal;
import org.apache.click.servlet.MockRequest;

public class MenuTest extends TestCase {

    /**
     * Check that Menu value is escaped.
     */
    public void testEscapeValue() {
        MockContext.initContext();

        Menu rootMenu = new Menu("root");
        Menu menu = new Menu("menu");
        rootMenu.add(menu);

        String value = "<script>";
        String expected = "title=\"&lt;script&gt;\"";

        menu.setTitle(value);
        menu.setLabel(value);

        assertTrue(menu.toString().indexOf(expected) > 1);
    }

    public void testI18N() {
        MockContext.initContext();

        Page page = new Page();
        Menu rootMenu = new MyMenu("root");
        page.addControl(rootMenu);

        Menu menu = new MyMenu("mymenu");
        rootMenu.add(menu);

        assertEquals("Root Label", menu.getLabel());
        assertEquals("Root Title", menu.getTitle());
    }

    /**
     * Check that menu without any roles defined (it's public) can be viewed by
     * the user.
     *
     * CLK-724
     */
    public void testAccessForMenuWithoutRoles() {
        MockContext.initContext();

        // Setup
        Menu menu = new Menu("menu");
        RoleAccessController controller = new RoleAccessController();
        menu.setAccessController(controller);

        MockRequest request = new MockRequest();
        String role = "userRole";
        MockPrincipal principal = new MockPrincipal("bob", role);
        request.setUserPrincipal(principal);

        // Perform tests
        assertTrue(menu.isUserInRoles());
    }

    public void testAccessForMenu() {
        // Setup
        Menu menu = new Menu("menu");
        String role = "userRole";
        menu.setRoles(Collections.singletonList(role));

        RoleAccessController controller = new RoleAccessController();
        menu.setAccessController(controller);

        MockContext context = MockContext.initContext();
        MockPrincipal principal = new MockPrincipal("bob", role);
        context.getMockRequest().setUserPrincipal(principal);

        // Perform tests
        assertTrue(menu.isUserInRoles());
    }

    public class MyMenu extends Menu {
        private static final long serialVersionUID = 1L;

        public MyMenu(String name) {
            super(name);
        }
    }
}
