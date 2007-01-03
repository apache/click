package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.MockRequest;

public class IntegerFieldTest extends TestCase {
    
    public void testOnProcess() {
        MockRequest request = new MockRequest();
        MockContext context = new MockContext(request);
        
        IntegerField intField = new IntegerField("id");
        assertEquals("id", intField.getName());
        
        intField.setContext(context);

        request.getParameterMap().put("id", "1234");
        
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("1234", intField.getValue());
        assertEquals(new Integer(1234), intField.getValueObject());
        
        request.getParameterMap().put("id", "123.4");
        
        assertTrue(intField.onProcess());
        assertFalse(intField.isValid());
        assertEquals("123.4", intField.getValue());
        assertNull(intField.getValueObject());
        
        request.getParameterMap().clear();
        
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("", intField.getValue());
        assertNull(intField.getValueObject());
        
        intField.setRequired(true);
        assertTrue(intField.onProcess());
        assertFalse(intField.isValid());
        assertEquals("", intField.getValue());
        assertNull(intField.getValueObject());
        
        request.getParameterMap().clear();
        
        request.getParameterMap().put("id", "0");
        
        intField.setRequired(true);
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("0", intField.getValue());
        assertNotNull(intField.getValueObject());
        assertEquals(new Integer(0), intField.getValueObject());
        
        intField.setRequired(false);
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("0", intField.getValue());
        assertNotNull(intField.getValueObject());
        assertEquals(new Integer(0), intField.getValueObject());
        
        request.getParameterMap().put("id", "10");
        
        intField.setMinValue(10);     
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("10", intField.getValue());
        assertEquals(new Integer(10), intField.getValueObject());
        
        intField.setMinValue(11);
        assertTrue(intField.onProcess());
        assertFalse(intField.isValid());
        assertEquals("10", intField.getValue());
        assertEquals(new Integer(10), intField.getValueObject());
        
        request.getParameterMap().put("id", "20");
        
        intField.setMaxValue(20);
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("20", intField.getValue());
        assertEquals(new Integer(20), intField.getValueObject());
        
        intField.setMaxValue(20);
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("20", intField.getValue());
        assertEquals(new Integer(20), intField.getValueObject());
        
        intField.setMaxValue(19);
        assertTrue(intField.onProcess());
        assertFalse(intField.isValid());
        assertEquals("20", intField.getValue());
        assertEquals(new Integer(20), intField.getValueObject());
        
        assertEquals(new Integer(20), intField.getInteger());
        assertEquals(new Long(20), intField.getLong());
        
        request.getParameterMap().put("id", "-20");
        
        intField.setMinValue(-21);
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("-20", intField.getValue());
        assertEquals(new Integer(-20), intField.getValueObject());
    }

}
