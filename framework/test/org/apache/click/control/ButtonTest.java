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
public class ButtonTest extends TestCase {
    /**
     * Test Button onProcess behavior.
     */
    public void testOnProcess() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        
        Button button = new Button("button");
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
        
        request.setParameter("button", "true");

        // Not an ajax request -> no action listener executed
        assertTrue(button.onProcess());
        context.executeActionListeners();
        assertFalse(check[0]);

        // ajax request, but no request param -> no action listener executed
        request.removeParameter("button");
        request.setParameter("X-Requested-With", "true");
        assertTrue(button.onProcess());
        context.executeActionListeners();
        assertFalse(check[0]);

        // Ajax request & request param -> call the onAction.
        request.setParameter("button", "true");
        request.setParameter("X-Requested-With", "true");
        assertTrue(button.onProcess());
        context.executeActionListeners();
        assertTrue(check[0]);
        
       
        button.setDisabled(true);
        assertTrue(button.onProcess());
        assertTrue(button.isValid());
        assertFalse(button.isDisabled());

        request.removeParameter("button");
        
        button.setDisabled(true);
        assertTrue(button.onProcess());
        assertTrue(button.isValid());
        assertTrue(button.isDisabled());
        
        request.removeParameter("button");
        assertTrue(button.onProcess());
        assertTrue(button.isValid());
    }
    
    /**
     * Coverage test of constructors.
     */
    public void testConstructors() {
        Button button = new Button();
        assertNull(button.getName());
        
        button = new Button("button", "label");
        assertEquals("label", button.getLabel());
    }
        
    /**
     * Coverage test of onClick.
     */
    public void testOnClick() {
        MockContext.initContext();
        
        Button button = new Button("button");
        assertNull(button.getOnClick());

        button.setOnClick("javascript:return false;");
        assertEquals("javascript:return false;", button.getOnClick());
    }
    
    /**
     * Coverage test of tab-index.
     */
    public void testTabIndex() {
        MockContext.initContext();
        
        Button button = new Button("button");
        button.setTabIndex(5);

        assertTrue(button.toString().contains("tabindex=\"5\""));
    }

    /**
     * Coverage test of disabled property.
     */
    public void testDisabled() {
        MockContext.initContext();
        
        Button button = new Button("button");
        button.setDisabled(true);
        System.out.println(button.toString());
        assertTrue(button.toString().contains("disabled=\"disabled\""));
    }
}
