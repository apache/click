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

import junit.framework.TestCase;
import org.apache.click.MockContainer;
import org.apache.click.MockContext;
import org.apache.commons.lang.StringUtils;

/**
 * Provides tests for the PickList control.
 */
public class PickListTest extends TestCase {

    /**
     * Check that PickList style attribute is only rendered once.
     *
     * CLK-712: In Click 2.2.0 the class attribute was removed before rendering the
     * pickList attributes, after which the class attribute was added again.
     * This could cause concurrent modification exceptions if the PickList is rendered
     * by multiple threads.
     */
    public void testClassAttributeRenderin() {
        // PickList uses Velocity to render its template. In this test we start a
        // MockContainer which also configures Velocity
        MockContainer container = new MockContainer("web");
        container.start();

        // MockContext is created when a container tests a page. There
        // is no page to test so we manually create a MockContext
        // and reuse the Mock Servlet objects created in the container.
        MockContext.initContext(container.getServletConfig(),
            container.getRequest(), container.getResponse(), container.getClickServlet());

        PickList pickList = new PickList("pickList");

        pickList.addStyleClass("white");
        pickList.setAttribute("title", "test");

        String pickListStr = pickList.toString();

        // Perform checks within the first 100 characters
        pickListStr = pickListStr.substring(0, 100);

        // Check that class attribute was rendered
        assertEquals(1, StringUtils.countMatches(pickListStr, "class=\"white picklist\""));

        // Check that the title attribute was rendered
        assertEquals(1, StringUtils.countMatches(pickListStr, "title=\"test\""));

        // Check that class attribute was rendered once
        assertEquals(1, StringUtils.countMatches(pickListStr, "class="));
        container.stop();
    }
}
