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

import junit.framework.TestCase;

import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.MockContext;
import org.apache.click.servlet.MockRequest;

/**
 * Test Button behavior.
 */
public class SubmitTest extends TestCase {
    /**
     * Test Submit onProcess behavior.
     */
    public void testOnProcess() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        
        Submit button = new Submit("button");
        assertEquals("button", button.getName());
        
        assertTrue(button.onProcess());
        
        request.setParameter("button", "true");
        assertTrue(button.onProcess());
        
        final boolean check[] = new boolean[1];
        button.setActionListener(new ActionListener() {
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                check[0] = true;
                return false;
            }
        });

        // No request param -> no action listener executed
        request.removeParameter("button");
        assertTrue(button.onProcess());
        context.executeActionListeners();
        assertFalse(check[0]);

        // Disabled button with request param
        request.setParameter("button", "true");
        button.setDisabled(true);
        assertTrue(button.onProcess());
        assertTrue(button.isValid());
        assertFalse(button.isDisabled());

        // Disabled button without request param
        request.removeParameter("button");
        button.setDisabled(true);
        assertTrue(button.onProcess());
        assertTrue(button.isValid());
        assertTrue(button.isDisabled());
    }
    
    /**
     * Coverage test of constructors.
     */
    public void testConstructors() {
        Submit button = new Submit();
        assertNull(button.getName());
        
        button = new Submit("button", "label");
        assertEquals("button", button.getName());
        assertEquals("label", button.getLabel());
        
        Listener l = new Listener();
        assertEquals("button", button.getName());
        button = new Submit("button", l, "onAction");

        try {
            button = new Submit("button", null, "onAction");
            assertTrue("Should throw exception", false);
        } catch (IllegalArgumentException e) { }

        try {
            button = new Submit("button", l, null);
            assertTrue("Should throw exception", false);
        } catch (IllegalArgumentException e) { }

        button = new Submit("button", "label", l, "onAction");
        assertEquals("button", button.getName());
        assertEquals("label", button.getLabel());

        try {
            button = new Submit("button", "label", null, "onAction");
            assertTrue("Should throw exception", false);
        } catch (IllegalArgumentException e) { }

        try {
            button = new Submit("button", "label", l, null);
            assertTrue("Should throw exception", false);
        } catch (IllegalArgumentException e) { }

    }

    static class Listener {
        public boolean fired;
        public boolean onAction() {
            fired = true;
            return true;
        }
    }

    /**
     * Coverage test of onClick.
     */
    public void testCancelJavaScriptValidation() {
        MockContext.initContext();
        
        Submit button = new Submit("button");
        assertFalse(button.getCancelJavaScriptValidation());

        button = new Submit("button");
        button.setCancelJavaScriptValidation(false);
        assertFalse(button.getCancelJavaScriptValidation());
        
        button.setCancelJavaScriptValidation(true);
        assertTrue(button.getCancelJavaScriptValidation());
        assertNotNull(button.getAttribute("onclick"));
    }
    
    /**
     * Coverage test of tab-index.
     */
    public void testTabIndex() {
        MockContext.initContext();
        
        Submit button = new Submit("button");
        button.setTabIndex(5);

        assertTrue(button.toString().contains("tabindex=\"5\""));
    }

    /**
     * Coverage test of disabled property.
     */
    public void testDisabled() {
        MockContext.initContext();
        
        Submit button = new Submit("button");
        button.setDisabled(true);

        assertTrue(button.toString().contains("disabled=\"disabled\""));
    }
}
