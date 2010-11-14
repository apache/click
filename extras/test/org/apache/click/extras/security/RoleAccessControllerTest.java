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
package org.apache.click.extras.security;

import junit.framework.TestCase;
import org.apache.click.servlet.MockPrincipal;
import org.apache.click.servlet.MockRequest;

/**
 * Provides tests for RoleAccessController.
 */
public class RoleAccessControllerTest extends TestCase {

    /**
     * Sanity test for hasAccess.
     */
    public void testHasAccess() {
        // Setup
        RoleAccessController controller = new RoleAccessController();
        MockRequest request = new MockRequest();
        String role = "userRole";
        MockPrincipal principal = new MockPrincipal("bob", role);
        request.setUserPrincipal(principal);

        // Perform tests
        assertTrue(controller.hasAccess(request, role));
    }

    /**
     * Check that hasAccess handles and null roles and allows access by default.
     *
     * CLK-724
     */
    public void testNullRoles() {
        // Setup
        RoleAccessController controller = new RoleAccessController();
        MockRequest request = new MockRequest();
        String role = "userRole";
        MockPrincipal principal = new MockPrincipal("bob", role);
        request.setUserPrincipal(principal);

        role = null;

        // Perform tests
        assertTrue(controller.hasAccess(request, role));
    }
}
