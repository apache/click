package net.sf.click.util;

import junit.framework.TestCase;

public class RequestTypeConverterTest extends TestCase {
    
    public void test() {
        RequestTypeConverter rtc = new RequestTypeConverter();
        
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

        String timeValue = "1166878800000";
        String sqlValue = "2006-12-24"; 
        String localValue = "24/12/2006";
        
        java.util.Date date1 = (java.util.Date) rtc.convertValue(timeValue, java.util.Date.class);
        java.util.Date date2 = (java.util.Date) rtc.convertValue(sqlValue, java.util.Date.class);
        java.util.Date date3 = (java.util.Date) rtc.convertValue(localValue, java.util.Date.class);
        
        assertNotNull(date1);
        assertNotNull(date2);
        assertNotNull(date3);
        assertEquals(date1, date2);
        assertEquals(date1, date3);
        
        java.sql.Date date4 = (java.sql.Date) rtc.convertValue(timeValue, java.sql.Date.class);
        java.sql.Date date5 = (java.sql.Date) rtc.convertValue(sqlValue, java.sql.Date.class);
        java.sql.Date date6 = (java.sql.Date) rtc.convertValue(localValue, java.sql.Date.class);
        
        assertNotNull(date4);
        assertNotNull(date5);
        assertNotNull(date6);
        assertEquals(date4, date5);
        assertEquals(date4, date6);
        
        java.sql.Time date7 = (java.sql.Time) rtc.convertValue(timeValue, java.sql.Time.class);
        java.sql.Time date8 = (java.sql.Time) rtc.convertValue(sqlValue, java.sql.Time.class);
        java.sql.Time date9 = (java.sql.Time) rtc.convertValue(localValue, java.sql.Time.class);
        
        assertNotNull(date7);
        assertNotNull(date8);
        assertNotNull(date9);
        assertEquals(date7, date8);
        assertEquals(date7, date9);
        
        java.sql.Timestamp date10 = (java.sql.Timestamp) rtc.convertValue(timeValue, java.sql.Timestamp.class);
        java.sql.Timestamp date11 = (java.sql.Timestamp) rtc.convertValue(sqlValue, java.sql.Timestamp.class);
        java.sql.Timestamp date12 = (java.sql.Timestamp) rtc.convertValue(localValue, java.sql.Timestamp.class);
        
        assertNotNull(date10);
        assertNotNull(date11);
        assertNotNull(date12);
        assertEquals(date10, date11);
        assertEquals(date10, date12);
    }
}

/**
sql: 2006-12-24, 1166878800000
date2: Sun Dec 24 00:00:00 EST 2006, 1166878800000
d/MM/yyyy
*/