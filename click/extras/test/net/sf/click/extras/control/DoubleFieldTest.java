package net.sf.click.extras.control;

import java.util.Locale;

import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.MockRequest;

public class DoubleFieldTest extends TestCase {

    Locale defaultLocale;

    protected void setUp() throws Exception {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    protected void tearDown() throws Exception {
        Locale.setDefault(defaultLocale);
    }

    public void testOnProcess() {
        MockRequest request = new MockRequest();
        MockContext.initContext(request);

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
        
        // Test not required zero value
        doubleField.setRequired(false);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("0", doubleField.getValue());
        assertEquals(new Double(0), doubleField.getValueObject());
        
        // Test required zero value
        doubleField.setRequired(true);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("0", doubleField.getValue());
        assertEquals(new Double(0), doubleField.getValueObject());
        
        request.getParameterMap().clear();

        // Test not required blank value
        doubleField.setRequired(false);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("", doubleField.getValue());
        assertNull(doubleField.getValueObject());

        // Test required blank value
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

        // Test requried value larger than max value
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
        MockRequest request = new MockRequest(Locale.GERMANY);
        MockContext.initContext(request);

        DoubleField doubleField = new DoubleField("id");

        // German uses ',' as the decimal separator
        // German 123,4 => double 123.4
        request.getParameterMap().put("id", "123,4");
        assertTrue(doubleField.onProcess());
        assertEquals("123,4", doubleField.getValue());
        assertEquals(new Double(123.4), doubleField.getDouble());
        assertEquals(new Double(123.4), doubleField.getValueObject());
    }

}
