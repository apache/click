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

import javax.servlet.http.HttpSession;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Provides tests for Context behavior.
 */
public class ContextTest extends TestCase {

    /**
     * Assert that when a HttpSession is invalidated, a new HttpSession
     * is created, and that the session was not cached by the Context.
     *
     * CLK-371.
     */
    public void testInvalidateSession() {
        try {
            MockContext context = MockContext.initContext();
            HttpSession session = context.getSession();
            Assert.assertNull(context.getSessionAttribute("key"));
            context.setSessionAttribute("key", "value");
            Assert.assertEquals("value", context.getSessionAttribute("key"));

            // Invalidate should clear all attributes from session instance
            session.invalidate();

            // Since we run outside a real Servlet Container, we simulate
            // a Servlet Container nullifying the session that was invalidated
            // above.
            context.getMockRequest().setSession(null);

            // Assert that context returns the newly created session, and not a
            // cached value
            Assert.assertNotSame(session, context.getSession());

            // Assert that session attribute was cleared
            Assert.assertNull(context.getSessionAttribute("key"));
        } catch (Throwable t) {
            Assert.fail("Test should not throw exception");
        }
    }
}
