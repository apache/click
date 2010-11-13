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
import org.apache.click.MockContext;
import org.apache.click.servlet.MockRequest;

/**
 *
 */
public class FieldTest extends TestCase {

    /**
     * Test Field onProcess.
     */
    public void testOnProcess() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();

        Field field = new TextField("text");
        field.setRequired(true);

        assertEquals("", field.getValue());

        // Test with valid request parameter
        String expectedValue = "textvalue";

        request.setParameter("text", expectedValue);

        field.onProcess();

        // Perform tests
        assertEquals(expectedValue, field.getValue());
        assertTrue(field.isValid());

        // Test with empty  request parameter
        expectedValue = "";
        request.setParameter("text", expectedValue);

        field.onProcess();

        // Perform tests
        assertEquals(expectedValue, field.getValue());
        // Since the field value is empty, test that required validation occurred
        assertFalse(field.isValid());
    }

    /**
     * Test Field onProcess if the Field's request parameter is not available.
     */
    public void testOnProcessWithoutRequestParameter() {
        MockContext context = MockContext.initContext();

        Field field = new TextField("text");
        field.setRequired(true);

        assertEquals("", field.getValue());

        String expectedValue = "";

        field.onProcess();

        assertEquals(expectedValue, field.getValue());
        assertTrue(field.isValid());
    }
}
