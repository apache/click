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
package org.apache.click.control;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.click.MockContext;

/**
 * Test AbstractLink behavior.
 */
public class AbstractLinkTest extends TestCase {

    /**
     * Check that AbstractLink value is encoded.
     */
    public void testEscapeValue() {
        MockContext.initContext();

        ActionLink link = new ActionLink("name");
        String value = "<script>";
        String expected = "value=%3Cscript%3E";

        link.setValue(value);

        // Check that link encodes value properly
        assertTrue(link.toString().indexOf(expected) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(link.toString().indexOf(value) < 0);
    }

    /**
     * Check that Ampersands are url encoded -> '&amp;'.
     *
     * CLK-483.
     */
    public void testAmpersandEncoding() {
        MockContext.initContext();

        ActionLink link = new ActionLink("name");
        link.setParameter("param1", "value1");

        assertTrue(link.toString().indexOf("&amp;") > 0);
    }

    /**
     * Test that AbstractLink.getState contains the link parameters.
     * CLK-715
     */
    public void testGetState() {
        // Setup link

        ActionLink link  = new ActionLink("name");

        link.setParameter("name", "Steve");
        link.setParameter("age", "10");
        link.setValue("myval");

        Map state = (Map) link.getState();

        assertEquals(state, link.getParameters());
        assertEquals(state.get("value"), link.getValue());
    }

    /**
     * Test that AbstractLink.setState set the link parameters.
     *
     * CLK-715
     */
    public void testSetState() {
        // Setup link
        ActionLink link  = new ActionLink("name");

        // Setup state
        Map state = new HashMap();
        state.put("name", "Steve");
        state.put("age", "10");
        state.put("value", "myval");

        link.setState(state);

        // Check that link parameters is restored
        assertEquals("Steve", link.getParameter("name"));
        assertEquals("myval", link.getValue());
    }
}
