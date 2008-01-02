package net.sf.click.control;

import net.sf.click.MockContext;
import net.sf.click.MockRequest;
import junit.framework.TestCase;

public class TextAreaTest extends TestCase {
    
    public void testOnProcess() {
        MockRequest request = new MockRequest();
        MockContext.initContext(request);
        
        TextArea textArea = new TextArea("text");
        assertEquals("text", textArea.getName());
        
        request.getParameterMap().put("text", "textvalue");
        
        assertTrue(textArea.onProcess());
        assertTrue(textArea.isValid());
        assertEquals("textvalue", textArea.getValue());
        assertEquals("textvalue", textArea.getValueObject());
        
        request.getParameterMap().put("text", "");
        
        assertTrue(textArea.onProcess());
        assertTrue(textArea.isValid());
        assertEquals("", textArea.getValue());
        assertEquals(null, textArea.getValueObject());
        
        textArea.setRequired(true);
        
        assertTrue(textArea.onProcess());
        assertFalse(textArea.isValid());
        assertEquals("", textArea.getValue());
        assertEquals(null, textArea.getValueObject());
        
        request.getParameterMap().put("text", "ratherlongtextvalue");
        
        textArea.setMinLength(10);
        assertTrue(textArea.onProcess());
        assertTrue(textArea.isValid());
        assertEquals("ratherlongtextvalue", textArea.getValue());
        assertEquals("ratherlongtextvalue", textArea.getValueObject());
                
        textArea.setMinLength(20);
        assertTrue(textArea.onProcess());
        assertFalse(textArea.isValid());
        assertEquals("ratherlongtextvalue", textArea.getValue());
        assertEquals("ratherlongtextvalue", textArea.getValueObject());   
        
        textArea.setMinLength(0);
        
        textArea.setMaxLength(20);
        assertTrue(textArea.onProcess());
        assertTrue(textArea.isValid());
        assertEquals("ratherlongtextvalue", textArea.getValue());
        assertEquals("ratherlongtextvalue", textArea.getValueObject());
                
        textArea.setMaxLength(10);
        assertTrue(textArea.onProcess());
        assertFalse(textArea.isValid());
        assertEquals("ratherlongtextvalue", textArea.getValue());
        assertEquals("ratherlongtextvalue", textArea.getValueObject());   
    }

}
