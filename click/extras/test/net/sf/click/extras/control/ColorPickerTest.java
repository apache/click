package net.sf.click.extras.control;

import java.util.Map;
import java.util.regex.Pattern;

import net.sf.click.MockContext;
import net.sf.click.MockRequest;

import junit.framework.TestCase;

public class ColorPickerTest extends TestCase {


    public void testHexPattern() {
        Pattern pat = ColorPicker.HEX_PATTERN;
        assertTrue(pat.matcher("#ffffff").matches());
        assertTrue(pat.matcher("#1a9bf2").matches());
        assertTrue(pat.matcher("#1A9BC2").matches());
        assertTrue(pat.matcher("#fff").matches());
        assertTrue(pat.matcher("#E3F").matches());
        assertTrue(pat.matcher("#123").matches());
        assertTrue(pat.matcher("#a4b").matches());
    
        assertFalse(pat.matcher("#123456789").matches());
        assertFalse(pat.matcher("").matches());
        assertFalse(pat.matcher("FFFFFF").matches());
        assertFalse(pat.matcher("GF").matches());
        assertFalse(pat.matcher("#G12").matches());
        assertFalse(pat.matcher("#A2").matches());
        assertFalse(pat.matcher("#A2A2A").matches());
        assertFalse(pat.matcher("#1234").matches());
    }
    
    public void testValidate() {
        MockRequest mr = new MockRequest();
        Map paras = mr.getParameterMap();
        MockContext ctxt = new MockContext(mr);
        
        ColorPicker cp = new ColorPicker("color");
        cp.setContext(ctxt);
        
        paras.put("color","#fff");
        assertTrue(cp.onProcess());
        assertTrue(cp.isValid());
        assertEquals("#fff",cp.getValue());
        
        paras.remove("color");
        assertTrue(cp.onProcess());
        assertTrue(cp.isValid());
        assertEquals("",cp.getValue());
        
        cp.setRequired(true);
        assertTrue(cp.onProcess());
        assertFalse(cp.isValid());
        
        cp = new ColorPicker("color");
        cp.setContext(ctxt);
        
        paras.put("color", "invalid");
        assertTrue(cp.onProcess());
        assertFalse(cp.isValid());
        assertEquals("invalid",cp.getValue());
    }
    
    
}
