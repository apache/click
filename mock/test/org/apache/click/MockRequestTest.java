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
import org.apache.click.servlet.MockRequest;
import org.apache.click.control.TextArea;

/**
 * Sanity tests for MockRequest.
 */
public class MockRequestTest extends TestCase {

    /** Indicates that the textArea actionListener was invoked. */
    private boolean actionCalled = false;

    /**
     * Check that MockRequest can dynamically add parameters and trigger
     * a Controls action listener.
     */
    @SuppressWarnings("unchecked")
    public void testDynamicRequest() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();

        TextArea textArea = new TextArea("text");
        assertEquals("text", textArea.getName());

        request.setParameter("param", "value");
        request.getParameterMap().put("text", "textvalue");

        // Registry a listener which must be invoked
        textArea.setActionListener(new ActionListener() {
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                // When action is invoked, set flag to true
                return actionCalled = true;
            }
        });
        assertTrue(textArea.onProcess());

        // Fire all action events that was registered in the onProcess method
        context.executeActionListeners();

        assertTrue("TextArea action was not invoked", actionCalled);
        assertTrue(textArea.isValid());
        assertEquals("textvalue", textArea.getValue());
        assertEquals("textvalue", textArea.getValueObject());
        
        // Check that getParameterMap() is modifiable by adding a 
        // key/value pair.
        context = (MockContext) Context.getThreadLocalContext();
        context.getRequest().getParameterMap().put("textvalue", 
          textArea.getValue());
    }
}
