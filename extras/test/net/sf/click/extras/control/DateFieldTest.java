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
package net.sf.click.extras.control;

import java.util.Date;

import net.sf.click.MockContext;
import junit.framework.TestCase;
import net.sf.click.servlet.MockRequest;

/**
 * Provides DateField JUnit TestCase.
 *
 * @author Malcolm Edgar
 */
public class DateFieldTest extends TestCase {

    public void testFormatPattern() throws Exception {
        DateField dateField = new DateField("Delivery date");
        assertEquals("dd MMM yyyy", dateField.getFormatPattern());
        assertEquals("%d %b %Y", dateField.getCalendarPattern());
        
        dateField = new DateField("Delivery date");
        dateField.setFormatPattern(" dd MMM yyyy ");
        assertEquals(" dd MMM yyyy ", dateField.getFormatPattern());
        assertEquals(" %d %b %Y ", dateField.getCalendarPattern());        

        dateField = new DateField("Delivery date");
        dateField.setFormatPattern("dd/MMM/yyyy");
        assertEquals("dd/MMM/yyyy", dateField.getFormatPattern());
        assertEquals("%d/%b/%Y", dateField.getCalendarPattern()); 
        
        dateField = new DateField("Delivery date");
        dateField.setFormatPattern("dd.MMM.yyyy");
        assertEquals("dd.MMM.yyyy", dateField.getFormatPattern());
        assertEquals("%d.%b.%Y", dateField.getCalendarPattern());
        
        dateField = new DateField("Delivery date");
        dateField.setFormatPattern("dd.MM.yy");
        assertEquals("dd.MM.yy", dateField.getFormatPattern());
        assertEquals("%d.%m.%y", dateField.getCalendarPattern());
        
        dateField = new DateField("Delivery date");
        dateField.setFormatPattern("d/M/yy");
        assertEquals("d/M/yy", dateField.getFormatPattern());
        assertEquals("%e/%m/%y", dateField.getCalendarPattern());
    }
    
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
}
