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

import java.util.Date;
import java.util.Locale;
import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.servlet.MockRequest;
import org.apache.commons.lang.StringUtils;

/**
 * Provides DateField JUnit TestCase.
 */
public class DateFieldTest extends TestCase {

    public void testNullParameter() {
        MockContext mockContext = MockContext.initContext();
        MockRequest request = mockContext.getMockRequest();

        DateField dateField = new DateField("dateField");
        assertEquals("dateField", dateField.getName());
        
        request.getParameterMap().put("dateField", "");        
        dateField.onProcess();        
        Date date = dateField.getDate();
        assertNull(date);
        
        request.getParameterMap().put("dateField", " ");        
        dateField.onProcess();        
        date = dateField.getDate();
        assertNull(date);

        request.getParameterMap().put("dateField", null);        
        dateField.onProcess();        
        date = dateField.getDate();
        assertNull(date);
        
        dateField.setValue(null);
        date = dateField.getDate();
        assertNull(date);
    }

    /**
     * DateField should cache Date value instead of reparsing the string
     * value each time.
     * 
     * CLK-316
     */
    public void testIntegerCacheValue() {
        MockContext mockContext = MockContext.initContext();
        MockRequest request = mockContext.getMockRequest();
        mockContext.setLocale(Locale.US);
        
        DateField dateField = new DateField("dateField");
        dateField.setFormatPattern("dd MMM yyyy H m s S");
        String requestParam = "06 Oct 2008 2 30 59 999";
        request.getParameterMap().put("dateField", requestParam);

        assertTrue(dateField.onProcess());

        // Check that the value equals the request parameter
        assertEquals(requestParam, dateField.getValue());
        
        // Retrieve the date from field: this should cache the Date
        Date date  = dateField.getDate();
        // Check that upon second retrieval the cached value is returned
        assertSame(date, dateField.getDate());

        // Check that getValueObject also returns the cached value
        assertSame(date, dateField.getValueObject());
        
        // Set date on the dateField and check that time value is not lost
        dateField.setDate(date);
        
        assertEquals(requestParam, dateField.getValue());
    }

    /**
     * Test Calendar format pattern.
     *
     * @throws java.lang.Exception
     */
    public void testFormatPattern() {
        MockContext.initContext();

        DateField calendarField = new DateField("Delivery date");
        assertEquals("dd MMM yyyy", calendarField.getFormatPattern());
        assertEquals("dd NNN yyyy", calendarField.getCalendarPattern());

        calendarField = new DateField("Delivery date");
        calendarField.setFormatPattern(" dd MMM yyyy ");
        assertEquals(" dd MMM yyyy ", calendarField.getFormatPattern());
        assertEquals(" dd NNN yyyy ", calendarField.getCalendarPattern());

        calendarField = new DateField("Delivery date");
        calendarField.setFormatPattern("dd/MMM/yyyy");
        assertEquals("dd/MMM/yyyy", calendarField.getFormatPattern());
        assertEquals("dd/NNN/yyyy", calendarField.getCalendarPattern());

        calendarField = new DateField("Delivery date");
        calendarField.setFormatPattern("dd.MMM.yyyy");
        assertEquals("dd.MMM.yyyy", calendarField.getFormatPattern());
        assertEquals("dd.NNN.yyyy", calendarField.getCalendarPattern());

        calendarField = new DateField("Delivery date");
        calendarField.setFormatPattern("dd.MM.yy");
        assertEquals("dd.MM.yy", calendarField.getFormatPattern());
        assertEquals("dd.MM.yy", calendarField.getCalendarPattern());

        calendarField = new DateField("Delivery date");
        calendarField.setFormatPattern("d/M/yy");
        assertEquals("d/M/yy", calendarField.getFormatPattern());
        assertEquals("d/M/yy", calendarField.getCalendarPattern());
    }

    /**
     * Check that help text is not rendered twice.
     *
     * CLK-574
     */
    public void testRenderHelp() {
        MockContext.initContext();

        DateField dateField = new MyDateField("date");

        int matches = StringUtils.countMatches(dateField.toString(), "Help Me!");

        assertEquals(1, matches);
    }

    public class MyDateField extends DateField {
        private static final long serialVersionUID = 1L;

        public MyDateField(String name) {
            super(name);
        }
    }
}
