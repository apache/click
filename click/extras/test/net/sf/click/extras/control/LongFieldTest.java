package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.MockRequest;

public class LongFieldTest extends TestCase {
    
    public void testOnProcess() {
        MockRequest request = new MockRequest();
        MockContext context = new MockContext(request);
        
        LongField longField = new LongField("id");
        assertEquals("id", longField.getName());
        
        longField.setContext(context);

        request.getParameterMap().put("id", "1234");
        
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("1234", longField.getValue());
        assertEquals(new Long(1234), longField.getValueObject());
        
        request.getParameterMap().put("id", "123.4");
        
        assertTrue(longField.onProcess());
        assertFalse(longField.isValid());
        assertEquals("123.4", longField.getValue());
        assertNull(longField.getValueObject());
        
        request.getParameterMap().clear();
        
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("", longField.getValue());
        assertNull(longField.getValueObject());
        
        longField.setRequired(true);
        assertTrue(longField.onProcess());
        assertFalse(longField.isValid());
        assertEquals("", longField.getValue());
        assertNull(longField.getValueObject());
        
        request.getParameterMap().put("id", "10");
        
        longField.setMinValue(10);     
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("10", longField.getValue());
        assertEquals(new Long(10), longField.getValueObject());
        
        longField.setMinValue(11);
        assertTrue(longField.onProcess());
        assertFalse(longField.isValid());
        assertEquals("10", longField.getValue());
        assertEquals(new Long(10), longField.getValueObject());
        
        request.getParameterMap().put("id", "20");
        
        longField.setMaxValue(20);
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("20", longField.getValue());
        assertEquals(new Long(20), longField.getValueObject());
        
        longField.setMaxValue(20);
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("20", longField.getValue());
        assertEquals(new Long(20), longField.getValueObject());
        
        longField.setMaxValue(19);
        assertTrue(longField.onProcess());
        assertFalse(longField.isValid());
        assertEquals("20", longField.getValue());
        assertEquals(new Long(20), longField.getValueObject());
        
        assertEquals(new Long(20), longField.getLong());
        assertEquals(new Integer(20), longField.getInteger());
        assertEquals(Long.class, longField.getValueClass());
        
        request.getParameterMap().put("id", "-20");
        
        longField.setMinValue(-21);
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("-20", longField.getValue());
        assertEquals(new Long(-20), longField.getValueObject());
    }

}
