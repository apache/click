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
package org.apache.click.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import junit.framework.TestCase;

/**
 * Tests for RequestTypeConverter.
 */
public class RequestTypeConverterTest extends TestCase {
   
    /**
     * Sanity checks for RequestTypeConverter.
     */
    public void test() {
        RequestTypeConverter rtc = new RequestTypeConverter();
        assertEquals("true", rtc.convertValue("true", Boolean.class).toString());
        assertEquals("false", rtc.convertValue("false", Boolean.class).toString());

        assertNull(rtc.convertValue(null, java.util.Date.class));
        assertNull(rtc.convertValue(null, java.sql.Date.class));
        assertNull(rtc.convertValue(null, java.sql.Time.class));
        assertNull(rtc.convertValue(null, java.sql.Timestamp.class));

        assertNull(rtc.convertValue(" ", java.util.Date.class));
        assertNull(rtc.convertValue(" ", java.sql.Date.class));
        assertNull(rtc.convertValue(" ", java.sql.Time.class));
        assertNull(rtc.convertValue(" ", java.sql.Timestamp.class));

        assertNull(rtc.convertValue("a", java.util.Date.class));
        assertNull(rtc.convertValue("a", java.sql.Date.class));
        assertNull(rtc.convertValue("a", java.sql.Time.class));
        assertNull(rtc.convertValue("a", java.sql.Timestamp.class));

        // TODO: MS test needs to account for daylight savings and other factors
        String timeValue = "1166878800000";

        String sqlValue = "2006-12-24";

        // Set default Locale to Australia so that formatting date in the
        // format dd/MM/yyyy works
        Locale.setDefault(new Locale("en", "AU"));
        String localValue = "24/12/2006";

        java.util.Date date1 = (java.util.Date) rtc.convertValue(timeValue, java.util.Date.class);
        java.util.Date date2 = (java.util.Date) rtc.convertValue(sqlValue, java.util.Date.class);
        java.util.Date date3 = (java.util.Date) rtc.convertValue(localValue, java.util.Date.class);

        assertNotNull(date1);
        assertNotNull(date2);
        assertNotNull(date3);
//        assertEquals(date1, date2);
        assertEquals(date2, date3);
        
        java.sql.Date date4 = (java.sql.Date) rtc.convertValue(timeValue, java.sql.Date.class);
        java.sql.Date date5 = (java.sql.Date) rtc.convertValue(sqlValue, java.sql.Date.class);
        java.sql.Date date6 = (java.sql.Date) rtc.convertValue(localValue, java.sql.Date.class);
        
        assertNotNull(date4);
        assertNotNull(date5);
        assertNotNull(date6);
//        assertEquals(date4, date5);
        assertEquals(date5, date6);
        
        java.sql.Time date7 = (java.sql.Time) rtc.convertValue(timeValue, java.sql.Time.class);
        java.sql.Time date8 = (java.sql.Time) rtc.convertValue(sqlValue, java.sql.Time.class);
        java.sql.Time date9 = (java.sql.Time) rtc.convertValue(localValue, java.sql.Time.class);
        
        assertNotNull(date7);
        assertNotNull(date8);
        assertNotNull(date9);
//        assertEquals(date7, date8);
        assertEquals(date8, date9);
        
        java.sql.Timestamp date10 = (java.sql.Timestamp) rtc.convertValue(timeValue, java.sql.Timestamp.class);
        java.sql.Timestamp date11 = (java.sql.Timestamp) rtc.convertValue(sqlValue, java.sql.Timestamp.class);
        java.sql.Timestamp date12 = (java.sql.Timestamp) rtc.convertValue(localValue, java.sql.Timestamp.class);
        
        assertNotNull(date10);
        assertNotNull(date11);
        assertNotNull(date12);
//        assertEquals(date10, date11);
        assertEquals(date11, date12);
    }

    /**
     * Check that very large BigDecimal numbers are converted correctly.
     * CLK-694.
     */
    public void testLargeBigDecimalConvertion() {
        RequestTypeConverter rtc = new RequestTypeConverter();

        String requestParam = "9999999999999999999999999999999999999999999999999.99";
        BigDecimal bd = (BigDecimal) rtc.convertValue(requestParam, BigDecimal.class);

        assertEquals(requestParam, bd.toString());
    }

    /**
     * Check that very large BigInteger numbers are converted correctly.
     */
    public void testLargeBigIntegerConvertion() {
        RequestTypeConverter rtc = new RequestTypeConverter();

        String requestParam = "99999999999999999999999999999999999999999999999";
        BigInteger bi = (BigInteger) rtc.convertValue(requestParam, BigInteger.class);
        assertEquals(requestParam, bi.toString());
    }
}
