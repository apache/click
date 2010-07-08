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
import org.apache.click.ClickServlet;
import org.apache.click.Control;
import org.apache.click.MockContext;
import org.apache.click.control.TextField;
import org.apache.click.servlet.MockRequest;
import org.apache.click.servlet.MockResponse;
import org.apache.click.servlet.MockServletConfig;
import org.apache.click.servlet.MockServletContext;

public class AbstractContainerFieldText extends TestCase {

    /**
     * Check and ensure AbstractContainerField does not bind to the request
     * parameter by default. CLK-428.
     */
    public void testBindRequestValue() {
        initMockContext();

        AbstractContainerField field = new AbstractContainerField() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTag() {
                return "div";
            }
        };

        try {
            assertTrue(field.onProcess());
        } catch (Exception e) {
            fail("AbstractContainerField#onProcess should not bind to request parameter");
        }
    }
    
    /**
     * Check that overriding AbstractContainerField#insert(Control, int) will
     * still receive calls from AbstractContainerField#add(Control).
     */
    public void testInsertOverride() {
        FeedbackBorder border = new FeedbackBorder();
        border.add(new TextField("field1"));
        try {
            border.add(new TextField("field2"));
            fail("FeedbackBorder only allows one control to be added.");
        } catch (Exception expected) {
        }
    }

    private void initMockContext() {
        MockServletContext servletContext = new MockServletContext();
        String servletName = "click-servlet";
        MockServletConfig servletConfig = new MockServletConfig(servletName,
            servletContext);
        ClickServlet servlet = new ClickServlet();
        MockResponse response = new MockResponse();
        MockRequest request = new MockRequest() {

            // Override getParameter to throw exception if argument is null
            @Override
            public String getParameter(String name) {
                if (name == null) {
                    throw new IllegalArgumentException("Null parameter");
                }
                return super.getParameter(name);
            }
        };
        MockContext.initContext(servletConfig, request, response, servlet);
    }
    
    class FeedbackBorder extends AbstractContainerField {
        private static final long serialVersionUID = 1L;

        @Override
        public Control insert(Control control, int index) {

            // Enforce rule that only 1 control can be added
            if (getControls().size() > 0) {
                throw new IllegalStateException(
                    "Only one control is allowed on FeedbackBorder.");
            }

            super.insert(control, 0);
            return control;
        }
    }
}
