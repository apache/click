/*
 * Copyright 2005 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.control;

import junit.framework.TestCase;

/**
 * Provides DateField JUnit TestCase.
 *
 * @author Malcolm Edgar
 */
public class DateFieldTestCase extends TestCase {

    public void testFormatPattern() throws Exception {
        DateField dateField = new DateField("Delivery date");
        dateField.setFormatPattern(" dd MMM yyyy");
        
        System.err.println("calendarPatter='" + dateField.calendarPattern + "'");
        assertEquals(dateField.getFormatPattern(), " dd MMM yyyy");
        assertEquals(dateField.getCalendarPattern(), " %e %b %Y");
    }

}
