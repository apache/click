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
import org.apache.commons.lang.StringUtils;

/**
 * Test Checkbox behavior.
 */
public class CheckboxTest extends TestCase {

    /**
     * Check that Checkbox value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();

        Checkbox checkbox = new Checkbox("name");
        String value = "<script>";
        String valueAttr = "value=";

        checkbox.setValue(value);

        // Check that checkbox does not render a value attribute
        assertEquals(false, StringUtils.contains(checkbox.toString(), valueAttr));
        
        // Check that the value <script> is not rendered
        assertTrue(checkbox.toString().indexOf(value) < 0);
    }
}
