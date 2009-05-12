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
package org.apache.click.extras.prototype;

import junit.framework.TestCase;

/**
 * Tests for CalendarField.
 *
 * @author Malcolm Edgar
 */
public class CalendarFieldTest extends TestCase {

    /**
     * Test Calendar format pattern.
     *
     * @throws java.lang.Exception
     */
    public void testFormatPattern() {
        CalendarField calendarField = new CalendarField("Delivery date");
        assertEquals("dd MMM yyyy", calendarField.getFormatPattern());
        assertEquals("dd NNN yyyy", calendarField.getCalendarPattern());

        calendarField = new CalendarField("Delivery date");
        calendarField.setFormatPattern(" dd MMM yyyy ");
        assertEquals(" dd MMM yyyy ", calendarField.getFormatPattern());
        assertEquals(" dd NNN yyyy ", calendarField.getCalendarPattern());

        calendarField = new CalendarField("Delivery date");
        calendarField.setFormatPattern("dd/MMM/yyyy");
        assertEquals("dd/MMM/yyyy", calendarField.getFormatPattern());
        assertEquals("dd/NNN/yyyy", calendarField.getCalendarPattern());

        calendarField = new CalendarField("Delivery date");
        calendarField.setFormatPattern("dd.MMM.yyyy");
        assertEquals("dd.MMM.yyyy", calendarField.getFormatPattern());
        assertEquals("dd.NNN.yyyy", calendarField.getCalendarPattern());

        calendarField = new CalendarField("Delivery date");
        calendarField.setFormatPattern("dd.MM.yy");
        assertEquals("dd.MM.yy", calendarField.getFormatPattern());
        assertEquals("dd.MM.yy", calendarField.getCalendarPattern());

        calendarField = new CalendarField("Delivery date");
        calendarField.setFormatPattern("d/M/yy");
        assertEquals("d/M/yy", calendarField.getFormatPattern());
        assertEquals("d/M/yy", calendarField.getCalendarPattern());
    }
}
