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

/**
 * Test Radio behavior.
 */
public class RadioTest extends TestCase {

    /**
     * Check that Radio value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();
        
        Form form = new Form("form");
        RadioGroup radioGroup = new RadioGroup("group");
        form.add(radioGroup);

        Radio radio = new Radio("name");
        radioGroup.add(radio);

        String value = "<script>";
        String expectedValue = "value=\"&lt;script&gt;\"";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedValue) > 1);
        
        String expectedId = "form_group_&lt;script&gt;";
        String expectedIdAttr = "id=\"" + expectedId + "\"";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedIdAttr) > 1);
        
        String expectedLabelValue = ">&lt;script&gt;</label>";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedLabelValue) > 1);
        
        String expectedLabelForAttr = "for=\"" + expectedId + "\"";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedLabelForAttr) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(radio.toString().indexOf(value) < 0);
    }
}
