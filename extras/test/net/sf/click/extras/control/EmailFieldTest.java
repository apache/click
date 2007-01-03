package net.sf.click.extras.control;

import net.sf.click.MockContext;
import net.sf.click.MockRequest;
import junit.framework.TestCase;

public class EmailFieldTest extends TestCase {
    
    public void testOnProcess() {
        MockRequest request = new MockRequest();
        MockContext context = new MockContext(request);
        
        EmailField emailField = new EmailField("email");
        assertEquals("email", emailField.getName());
        
        emailField.setContext(context);

        request.getParameterMap().put("email", "username@server");
        
        assertTrue(emailField.onProcess());
        assertTrue(emailField.isValid());
        assertEquals("username@server", emailField.getValue());
        assertEquals("username@server", emailField.getValueObject());
        
        request.getParameterMap().put("email", "username@");
        
        assertTrue(emailField.onProcess());
        assertFalse(emailField.isValid());
        assertEquals("username@", emailField.getValue());
  
        request.getParameterMap().put("email", "@servr");
        
        assertTrue(emailField.onProcess());
        assertFalse(emailField.isValid());
        assertEquals("@servr", emailField.getValue());
        
        request.getParameterMap().put("email", "");
        
        assertTrue(emailField.onProcess());
        assertTrue(emailField.isValid());
        assertEquals("", emailField.getValue());
        assertNull(emailField.getValueObject());
        
        emailField.setRequired(true);
        
        assertTrue(emailField.onProcess());
        assertFalse(emailField.isValid());
        assertEquals("", emailField.getValue());
        assertEquals(null, emailField.getValueObject());
        
        request.getParameterMap().put("email", "username@server");
        
        emailField.setMinLength(10);
        assertTrue(emailField.onProcess());
        assertTrue(emailField.isValid());
        assertEquals("username@server", emailField.getValue());
        assertEquals("username@server", emailField.getValueObject());
                
        emailField.setMinLength(20);
        assertTrue(emailField.onProcess());
        assertFalse(emailField.isValid());
        assertEquals("username@server", emailField.getValue());
        assertEquals("username@server", emailField.getValueObject());   
        
        emailField.setMinLength(0);
        
        emailField.setMaxLength(20);
        assertTrue(emailField.onProcess());
        assertTrue(emailField.isValid());
        assertEquals("username@server", emailField.getValue());
        assertEquals("username@server", emailField.getValueObject());
                
        emailField.setMaxLength(10);
        assertTrue(emailField.onProcess());
        assertFalse(emailField.isValid());
        assertEquals("username@server", emailField.getValue());
        assertEquals("username@server", emailField.getValueObject());   
    }

}
