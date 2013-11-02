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

import java.math.BigDecimal;
import java.util.Locale;

import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.control.Form;
import org.apache.click.servlet.MockRequest;

public class DoubleFieldTest extends TestCase {

    Locale defaultLocale;

    @Override
    protected void setUp() throws Exception {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @Override
    protected void tearDown() throws Exception {
        Locale.setDefault(defaultLocale);
    }

    public void testOnProcess() {
        MockContext mockContext = MockContext.initContext();
        MockRequest request = mockContext.getMockRequest();

        DoubleField doubleField = new DoubleField("id");
        assertEquals("id", doubleField.getName());
        
        assertEquals(new Double(Double.POSITIVE_INFINITY), new Double(doubleField.getMaxValue()));
        assertEquals(new Double(Double.NEGATIVE_INFINITY), new Double(doubleField.getMinValue()));

        // Test not required, positive value
        request.getParameterMap().put("id", "1234");

        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("1234", doubleField.getValue());
        assertEquals(new Double(1234), doubleField.getValueObject());

        request.getParameterMap().put("id", "123.4");

        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("123.4", doubleField.getValue());
        assertEquals(new Double(123.4), doubleField.getValueObject());

        request.getParameterMap().clear();
        

        request.getParameterMap().put("id", "0");
        
        // Test not required + zero value
        doubleField.setRequired(false);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("0", doubleField.getValue());
        assertEquals(new Double(0), doubleField.getValueObject());
        
        // Test required + zero value
        doubleField.setRequired(true);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("0", doubleField.getValue());
        assertEquals(new Double(0), doubleField.getValueObject());
        
        request.getParameterMap().clear();

        // Test not required + blank value
        request.getParameterMap().put("id", "");
        doubleField.setRequired(false);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("", doubleField.getValue());
        assertNull(doubleField.getValueObject());

        // Test required + blank value
        request.getParameterMap().put("id", "");
        doubleField.setRequired(true);
        assertTrue(doubleField.onProcess());
        assertFalse(doubleField.isValid());
        assertEquals("", doubleField.getValue());
        assertNull(doubleField.getValueObject());

        request.getParameterMap().put("id", "10");

        // Test required value equal to min value
        doubleField.setRequired(true);
        doubleField.setMinValue(10);
        assertEquals(new Double(10), new Double(doubleField.getMinValue()));
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.getError(), doubleField.isValid());
        assertEquals("10", doubleField.getValue());
        assertEquals(new Double(10), doubleField.getValueObject());

        // Test required value larger than min value
        doubleField.setRequired(true);
        doubleField.setMinValue(11);
        assertTrue(doubleField.onProcess());
        assertFalse(doubleField.isValid());
        assertEquals("10", doubleField.getValue());
        assertEquals(new Double(10), doubleField.getValueObject());

        request.getParameterMap().put("id", "20");

        // Test required value equal to max value
        doubleField.setMaxValue(20);
        assertEquals(new Double(20), new Double(doubleField.getMaxValue()));
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("20", doubleField.getValue());
        assertEquals(new Double(20), doubleField.getValueObject());

        // Test required value larger than max value
        doubleField.setMaxValue(21);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("20", doubleField.getValue());
        assertEquals(new Double(20), doubleField.getValueObject());

        // Test require value smaller than max value
        doubleField.setMaxValue(19);
        assertTrue(doubleField.onProcess());
        assertFalse(doubleField.isValid());
        assertEquals("20", doubleField.getValue());
        assertEquals(new Double(20), doubleField.getValueObject());

        assertEquals(new Double(20), doubleField.getDouble());
        assertEquals(new Float(20), doubleField.getFloat());

        request.getParameterMap().put("id", "-20.1");

        // Test required min value smaller than min value
        doubleField.setMinValue(-21);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("-20.1", doubleField.getValue());
        assertEquals(new Double(-20.1), doubleField.getValueObject());
    }

    public void testLocaleServerENClientDE() {
        MockContext mockContext = MockContext.initContext(Locale.GERMANY);
        MockRequest request = mockContext.getMockRequest();

        DoubleField doubleField = new DoubleField("id");

        // German uses ',' as the decimal separator
        // German 123,4 => double 123.4
        request.setParameter("id", "123,4");
        assertTrue(doubleField.onProcess());
        assertEquals("123,4", doubleField.getValue());
        assertEquals(new Double(123.4), doubleField.getDouble());
        assertEquals(new Double(123.4), doubleField.getValueObject());
    }

    /**
     * Test that the fix for double->BigDecimal conversion work.
     */
    public void testFormCopyBigDecimal() {
        MockContext.initContext(Locale.US);
        
        Form form = new Form("form");

        DoubleField decimalField = new DoubleField("decimalField");
        String decimalValue = "0.1";
        decimalField.setValue(decimalValue);
        form.add(decimalField);

        MyObj obj = new MyObj(); 
        form.copyTo(obj);

        assertEquals(decimalValue, obj.decimalField.toString());
    }
    
    public static class MyObj {
        public BigDecimal decimalField;
        
        public void setDecimalField(BigDecimal value) {
            this.decimalField = value;
        }
    }
}
