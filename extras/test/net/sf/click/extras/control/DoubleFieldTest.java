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
        MockContext context = new MockContext(request);

        DoubleField doubleField = new DoubleField("id");
        assertEquals("id", doubleField.getName());

        doubleField.setContext(context);

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

        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("", doubleField.getValue());
        assertNull(doubleField.getValueObject());

        doubleField.setRequired(true);
        assertTrue(doubleField.onProcess());
        assertFalse(doubleField.isValid());
        assertEquals("", doubleField.getValue());
        assertNull(doubleField.getValueObject());

        request.getParameterMap().put("id", "10");

        doubleField.setMinValue(10);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("10", doubleField.getValue());
        assertEquals(new Double(10), doubleField.getValueObject());

        doubleField.setMinValue(11);
        assertTrue(doubleField.onProcess());
        assertFalse(doubleField.isValid());
        assertEquals("10", doubleField.getValue());
        assertEquals(new Double(10), doubleField.getValueObject());

        request.getParameterMap().put("id", "20");

        doubleField.setMaxValue(20);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("20", doubleField.getValue());
        assertEquals(new Double(20), doubleField.getValueObject());

        doubleField.setMaxValue(20);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("20", doubleField.getValue());
        assertEquals(new Double(20), doubleField.getValueObject());

        doubleField.setMaxValue(19);
        assertTrue(doubleField.onProcess());
        assertFalse(doubleField.isValid());
        assertEquals("20", doubleField.getValue());
        assertEquals(new Double(20), doubleField.getValueObject());

        assertEquals(new Double(20), doubleField.getDouble());
        assertEquals(new Float(20), doubleField.getFloat());
        assertEquals(Double.class, doubleField.getValueClass());

        request.getParameterMap().put("id", "-20.1");

        doubleField.setMinValue(-21);
        assertTrue(doubleField.onProcess());
        assertTrue(doubleField.isValid());
        assertEquals("-20.1", doubleField.getValue());
        assertEquals(new Double(-20.1), doubleField.getValueObject());
    }

    public void testLocaleServerENClientDE() {
        MockRequest request = new MockRequest(Locale.GERMANY);
        MockContext context = new MockContext(request);

        DoubleField doubleField = new DoubleField("id");
        doubleField.setContext(context);

        // German uses ',' as the decimal separator
        // German 123,4 => double 123.4
        request.getParameterMap().put("id", "123,4");
        assertTrue(doubleField.onProcess());
        assertEquals("123,4", doubleField.getValue());
        assertEquals(new Double(123.4), doubleField.getDouble());
        assertEquals(new Double(123.4), doubleField.getValueObject());
    }

}
