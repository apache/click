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
import org.apache.click.servlet.MockPrincipal;
import org.apache.click.servlet.MockRequest;

/**
 * MockPrincipal tests.
 */
public class MockPrincipalTest extends TestCase {

    /**
     * Check that request's getRemoteUser lists the MockPrincipal name
     */
    public void testGetRemoteUser() {
        String user = "Bob";

        MockContext mockContext = MockContext.initContext();
        MockRequest mockRequest = mockContext.getMockRequest();

        // Create a new user principal with the roles "user" and "manager"
        MockPrincipal principal = new MockPrincipal(user);

        // Set the user principal on the request object
        mockRequest.setUserPrincipal(principal);

        // Check user is in "user" and "manager" roles
        assertEquals(user, mockRequest.getRemoteUser());
    }

    /**
     * Check that request's isUserInRole works properly.
     *
     * CLK-585
     */
    public void testUserInRole() {

        MockContext mockContext = MockContext.initContext();
        MockRequest mockRequest = mockContext.getMockRequest();

        // Create a new user principal with the roles "user" and "manager"
        MockPrincipal principal = new MockPrincipal("Bob", "user", "manager");

        // Set the user principal on the request object
        mockRequest.setUserPrincipal(principal);

        // Check user is in "user" and "manager" roles
        assertTrue(mockRequest.isUserInRole("user"));
        assertTrue(mockRequest.isUserInRole("manager"));

        // Check user is not in "Manager" role (check is case sensitivity)
        assertFalse(mockRequest.isUserInRole("Manager"));

        // Check user not in QA role
        assertFalse(mockRequest.isUserInRole("QA"));
    }
}
