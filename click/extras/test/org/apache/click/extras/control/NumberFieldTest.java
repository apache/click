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
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import org.apache.click.MockContext;

import junit.framework.TestCase;
import org.apache.click.control.Form;
import org.apache.click.servlet.MockRequest;

public class NumberFieldTest extends TestCase{

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

    public void testFormat() {
        MockContext.initContext(Locale.US);
        
        Number decNum = new Float(2.56f);
        
        NumberField engF = new NumberField("en");

        assertNull(engF.getPattern());
        engF.setPattern("#.00");
        assertEquals("#.00", engF.getPattern());
        engF.setPattern(null);
        assertNull(engF.getPattern());

        engF.setValue("some Text");
        assertEquals("some Text", engF.getValue());
        assertNull(engF.getNumber());
        
        engF.setValue("12.456,5656");
        assertEquals("12.456,5656", engF.getValue());
        assertEquals(new Double(12.456), engF.getNumber());
        
        engF.setNumber(decNum);
        assertEquals("2.56", engF.getValue());
        assertEquals(2.56d, engF.getNumber().doubleValue(),0);
        
        engF.setValue("123.6");
        assertEquals(123.6d, engF.getNumber().doubleValue(),0);
        assertEquals(engF.getNumber(), engF.getValueObject());
        
        engF.setPattern("0");
        engF.setNumber(new Float(123.6f));
        assertEquals("124", engF.getValue());
        assertEquals(124, engF.getNumber().intValue());
        
        engF.setValue("123.6");
        assertEquals("123.6", engF.getValue());
        assertEquals(123.6f, engF.getNumber().floatValue(),0);
        
        engF.setPattern("0.00");
        engF.setNumber(new Float(123.6f));
        assertEquals("123.60", engF.getValue());
        assertEquals(123.6f, engF.getNumber().floatValue(),0);
        
        engF.setValue("12.223");
        assertEquals(12.223f, engF.getNumber().floatValue(),0);
        
        //keeps the pattern
        engF.setNumberFormat(NumberFormat.getInstance(Locale.GERMAN));
        engF.setNumber(decNum);
        assertEquals("2,56", engF.getValue());
        engF.setValue("3456,134");
        assertEquals(3456.134f, engF.getNumber().floatValue(),0);
        
        MockContext.initContext(Locale.GERMANY);
        
        NumberField germanF = new NumberField("de");
        
        germanF.setNumber(decNum);
        assertEquals("2,56", germanF.getValue());
        germanF.setValue("3.456,134");
        assertEquals(3456.134f, germanF.getNumber().floatValue(),0);
    }
    
    public void testOnProcess() {
        MockContext mockContext = MockContext.initContext(Locale.US);
        MockRequest req = mockContext.getMockRequest();
        Map<String, Object> params = req.getParameterMap();
        
        NumberField engF = new NumberField("en");
        engF.setPattern("#,##0.00");
        
        engF.setValidate(false);
        params.put("en", "no number");
        assertTrue(engF.onProcess());
        assertEquals("no number", engF.getValue());
        assertTrue(engF.isValid());
        assertNull(engF.getNumber());
        engF.validate();
        assertFalse(engF.isValid());
        
        engF = new NumberField("en");
        engF.setPattern("#,##0.00");
        params.put("en", "12.3");

        engF.setValidate(false);
        assertTrue(engF.onProcess());
        assertEquals("12.3",engF.getValue());
        assertEquals(12.3f,engF.getNumber().floatValue(),0);
        engF.validate();
        assertEquals("12.30",engF.getValue());
        
        engF = new NumberField("en");
        engF.setPattern("#,##0.00");
        params.put("en", "12.3");
        
        assertTrue(engF.onProcess());
        assertEquals("12.30",engF.getValue());
        assertEquals("12.3", req.getParameter(engF.getName()));
        
        params.put("en", "some value");
        assertTrue(engF.onProcess());
        assertEquals("some value", engF.getValue());
        assertNull(engF.getNumber());
        assertEquals("some value", req.getParameter(engF.getName()));
    }
    
    public void testValidate() {
        MockContext mockContext = MockContext.initContext(Locale.US);
        MockRequest req = mockContext.getMockRequest();
        Map<String, Object> params = req.getParameterMap();
        
        NumberField engF = new NumberField("en");
        engF.setPattern("0");
        
        engF.setMaxValue(100);
        engF.setMinValue(1);
        engF.setRequired(true);
        
        params.put("en", "2.23");
        assertTrue(engF.onProcess());
        assertTrue(engF.isValid());
        assertEquals("2", engF.getValue());
        
        engF.setValue("123,45");
        engF.validate();
        assertFalse(engF.isValid());
        assertEquals("123,45", engF.getValue());
        
        engF.setValue("-12");
        engF.validate();
        assertFalse(engF.isValid());
        assertEquals("-12", engF.getValue());
        
        engF = new NumberField("en");
        engF.setPattern("0");
        
        // Test required + blank value
        engF.setRequired(true);
        params.put("en", "");
        
        assertTrue(engF.onProcess());
        assertFalse(engF.isValid());
        assertEquals(0, engF.getValue().length());
        
        engF.setValue("");
        assertFalse(engF.isValid());
        assertEquals("",engF.getValue());
        
        engF.setValue("some text");
        assertFalse(engF.isValid());
        assertEquals("some text", engF.getValue());    
    }


    /**
     * Test that the fix for number->BigDecimal conversion work.
     *
     * CLK-694.
     */
    public void testFormCopyBigDecimal() {
        MockContext.initContext(Locale.US);

        Form form = new Form("form");

        NumberField bigDecimalField = new NumberField("bigDecimalField");
        NumberField bigIntegerField = new NumberField("bigIntegerField");

        // Specify a very large value
        String bigValue = "999999999999999999";
        bigDecimalField.setValue(bigValue);
        form.add(bigDecimalField);
        bigIntegerField.setValue(bigValue);
        form.add(bigIntegerField);

        MyObj obj = new MyObj();
        form.copyTo(obj);

        assertEquals(bigValue, obj.bigDecimalField.toString());
        assertEquals(bigValue, obj.bigIntegerField.toString());
    }

    /**
     * Test that Field->BigInteger conversion works.
     */
    public void testFormCopyBigInteger() {
        MockContext.initContext(Locale.US);

        Form form = new Form("form");

        NumberField bigDecimalField = new NumberField("bigDecimalField");
        NumberField bigIntegerField = new NumberField("bigIntegerField");

        // Specify a very large value
        String bigValue = "999999999999999999";
        bigDecimalField.setValue(bigValue);
        form.add(bigDecimalField);
        bigIntegerField.setValue(bigValue);
        form.add(bigIntegerField);

        MyObj obj = new MyObj();
        form.copyTo(obj);

        assertEquals(bigValue, obj.bigDecimalField.toString());
        assertEquals(bigValue, obj.bigIntegerField.toString());
    }

    /**
     * POJO for testing of copying values between Fields and domain objects.
     */
    public static class MyObj {

        public BigDecimal bigDecimalField;

        public BigInteger bigIntegerField;

        public void setBigDecimalField(BigDecimal value) {
            this.bigDecimalField = value;
        }

        public void setBigIntegerField(BigInteger value) {
            this.bigIntegerField = value;
        }
    }

}
