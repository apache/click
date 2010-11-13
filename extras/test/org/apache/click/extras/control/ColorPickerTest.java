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

import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.servlet.MockRequest;

public class ColorPickerTest extends TestCase {


    public void testHexPattern() {
        Pattern pat = ColorPicker.HEX_PATTERN;
        assertTrue(pat.matcher("#ffffff").matches());
        assertTrue(pat.matcher("#1a9bf2").matches());
        assertTrue(pat.matcher("#1A9BC2").matches());
        assertTrue(pat.matcher("#fff").matches());
        assertTrue(pat.matcher("#E3F").matches());
        assertTrue(pat.matcher("#123").matches());
        assertTrue(pat.matcher("#a4b").matches());
    
        assertFalse(pat.matcher("#123456789").matches());
        assertFalse(pat.matcher("").matches());
        assertFalse(pat.matcher("FFFFFF").matches());
        assertFalse(pat.matcher("GF").matches());
        assertFalse(pat.matcher("#G12").matches());
        assertFalse(pat.matcher("#A2").matches());
        assertFalse(pat.matcher("#A2A2A").matches());
        assertFalse(pat.matcher("#1234").matches());
    }
    
    public void testValidate() {
        MockContext mockContext = MockContext.initContext();
        MockRequest mr = mockContext.getMockRequest();
        Map<String, Object> params = mr.getParameterMap();
        
        ColorPicker cp = new ColorPicker("color");
        
        params.put("color","#fff");
        assertTrue(cp.onProcess());
        assertTrue(cp.isValid());
        assertEquals("#fff",cp.getValue());

        cp = new ColorPicker("color");
        params.remove("color");
        assertTrue(cp.onProcess());
        assertTrue(cp.isValid());
        assertEquals("",cp.getValue());

        params.put("color", "");
        cp.setRequired(true);
        assertTrue(cp.onProcess());
        assertFalse(cp.isValid());
        
        cp = new ColorPicker("color");
        
        params.put("color", "invalid");
        assertTrue(cp.onProcess());
        assertFalse(cp.isValid());
        assertEquals("invalid",cp.getValue());
    }
    
    
}
