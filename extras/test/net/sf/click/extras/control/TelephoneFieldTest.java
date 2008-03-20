package net.sf.click.extras.control;

import net.sf.click.MockContext;
import junit.framework.TestCase;
import net.sf.click.servlet.MockRequest;

public class TelephoneFieldTest extends TestCase {
    
    public void testOnProcess() {
        MockContext mockContext = MockContext.initContext();
        MockRequest request = mockContext.getMockRequest();
        
        TelephoneField telephoneField = new TelephoneField("telephone");
        assertEquals("telephone", telephoneField.getName());
        
        request.getParameterMap().put("telephone", "02 8734 7653");
        
        assertTrue(telephoneField.onProcess());
        assertTrue(telephoneField.isValid());
        assertEquals("02 8734 7653", telephoneField.getValue());
        assertEquals("02 8734 7653", telephoneField.getValueObject());
        
        request.getParameterMap().put("telephone", "1800-DOCTOR");
        
        assertTrue(telephoneField.onProcess());
        assertFalse(telephoneField.isValid());
        assertEquals("1800-DOCTOR", telephoneField.getValue());
  
        request.getParameterMap().put("telephone", "01-923 02 2345 3654");

        assertTrue(telephoneField.onProcess());
        assertTrue(telephoneField.isValid());
        assertEquals("01-923 02 2345 3654", telephoneField.getValue());
        
        request.getParameterMap().put("telephone", "");
        
        assertTrue(telephoneField.onProcess());
        assertTrue(telephoneField.isValid());
        assertEquals("", telephoneField.getValue());
        assertNull(telephoneField.getValueObject());
        
        telephoneField.setRequired(true);
        
        assertTrue(telephoneField.onProcess());
        assertFalse(telephoneField.isValid());
        assertEquals("", telephoneField.getValue());
        assertEquals(null, telephoneField.getValueObject());
        
        request.getParameterMap().put("telephone", "(01) 2345 3654");
        
        telephoneField.setMinLength(10);
        assertTrue(telephoneField.onProcess());
        assertTrue(telephoneField.isValid());
        assertEquals("(01) 2345 3654", telephoneField.getValue());
        assertEquals("(01) 2345 3654", telephoneField.getValueObject());
                
        telephoneField.setMinLength(20);
        assertTrue(telephoneField.onProcess());
        assertFalse(telephoneField.isValid());
        assertEquals("(01) 2345 3654", telephoneField.getValue());
        assertEquals("(01) 2345 3654", telephoneField.getValueObject());   
        
        telephoneField.setMinLength(0);
        
        telephoneField.setMaxLength(20);
        assertTrue(telephoneField.onProcess());
        assertTrue(telephoneField.isValid());
        assertEquals("(01) 2345 3654", telephoneField.getValue());
        assertEquals("(01) 2345 3654", telephoneField.getValueObject());
                
        telephoneField.setMaxLength(10);
        assertTrue(telephoneField.onProcess());
        assertFalse(telephoneField.isValid());
        assertEquals("(01) 2345 3654", telephoneField.getValue());
        assertEquals("(01) 2345 3654", telephoneField.getValueObject());   
    }

}
