package net.sf.click.control;

import net.sf.click.MockContext;
import junit.framework.TestCase;
import net.sf.click.servlet.MockRequest;

/**
 * Test TextField behavior.
 */
public class TextFieldTest extends TestCase {

    /**
     * Test TextField onProcess behavior.
     */
    public void testOnProcess() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        
        TextField textField = new TextField("text");
        assertEquals("text", textField.getName());
        
        request.getParameterMap().put("text", "textvalue");
        
        assertTrue(textField.onProcess());
        assertTrue(textField.isValid());
        assertEquals("textvalue", textField.getValue());
        assertEquals("textvalue", textField.getValueObject());
        
        request.getParameterMap().put("text", "");
        
        assertTrue(textField.onProcess());
        assertTrue(textField.isValid());
        assertEquals("", textField.getValue());
        assertEquals(null, textField.getValueObject());
        
        textField.setRequired(true);
        
        assertTrue(textField.onProcess());
        assertFalse(textField.isValid());
        assertEquals("", textField.getValue());
        assertEquals(null, textField.getValueObject());
        
        request.getParameterMap().put("text", "ratherlongtextvalue");
        
        textField.setMinLength(10);
        assertTrue(textField.onProcess());
        assertTrue(textField.isValid());
        assertEquals("ratherlongtextvalue", textField.getValue());
        assertEquals("ratherlongtextvalue", textField.getValueObject());
                
        textField.setMinLength(20);
        assertTrue(textField.onProcess());
        assertFalse(textField.isValid());
        assertEquals("ratherlongtextvalue", textField.getValue());
        assertEquals("ratherlongtextvalue", textField.getValueObject());   
        
        textField.setMinLength(0);
        
        textField.setMaxLength(20);
        assertTrue(textField.onProcess());
        assertTrue(textField.isValid());
        assertEquals("ratherlongtextvalue", textField.getValue());
        assertEquals("ratherlongtextvalue", textField.getValueObject());
                
        textField.setMaxLength(10);
        assertTrue(textField.onProcess());
        assertFalse(textField.isValid());
        assertEquals("ratherlongtextvalue", textField.getValue());
        assertEquals("ratherlongtextvalue", textField.getValueObject());   
    }

    /**
     * Check that textfield value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();

        TextField field = new TextField("name");
        String value = "<script>";
        String expected = "&lt;script&gt;";
        field.setValue(value);
        assertTrue(field.toString().indexOf(expected) > 1);

        // Check that the value <script> is not rendered
        assertTrue(field.toString().indexOf(value) < 0);
    }
}
