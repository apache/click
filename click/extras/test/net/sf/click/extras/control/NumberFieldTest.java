package net.sf.click.extras.control;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import net.sf.click.MockContext;
import net.sf.click.MockRequest;

import junit.framework.TestCase;

public class NumberFieldTest extends TestCase{

    Locale defaultLocale;

    protected void setUp() throws Exception {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    protected void tearDown() throws Exception {
        Locale.setDefault(defaultLocale);
    }

    public void testFormat() {
        MockRequest req = new MockRequest(Locale.US);
        MockContext.initContext(req);
        Number decNum = new Float(2.56f);
        
        NumberField engF = new NumberField("en");

        assertNull(engF.getPattern());
        engF.setPattern("#.00");
        assertEquals("#.00", engF.getPattern());
        engF.setPattern(null);
        assertNull(engF.getPattern());

        engF.setValue("some Text");
        assertEquals("some Text", engF.getValue());
        assertNull(engF.getNumber());
        
        engF.setValue("12.456,5656");
        assertEquals("12.456,5656", engF.getValue());
        assertEquals(new Double(12.456), engF.getNumber());
        
        engF.setNumber(decNum);
        assertEquals("2.56", engF.getValue());
        assertEquals(2.56d, engF.getNumber().doubleValue(),0);
        
        engF.setValue("123.6");
        assertEquals(123.6d, engF.getNumber().doubleValue(),0);
        assertEquals(engF.getNumber(), engF.getValueObject());
        
        engF.setPattern("0");
        engF.setNumber(new Float(123.6f));
        assertEquals("124", engF.getValue());
        assertEquals(124, engF.getNumber().intValue());
        
        engF.setValue("123.6");
        assertEquals("123.6", engF.getValue());
        assertEquals(123.6f, engF.getNumber().floatValue(),0);
        
        engF.setPattern("0.00");
        engF.setNumber(new Float(123.6f));
        assertEquals("123.60", engF.getValue());
        assertEquals(123.6f, engF.getNumber().floatValue(),0);
        
        engF.setValue("12.223");
        assertEquals(12.223f, engF.getNumber().floatValue(),0);
        
        //keeps the pattern
        engF.setNumberFormat(NumberFormat.getInstance(Locale.GERMAN));
        engF.setNumber(decNum);
        assertEquals("2,56", engF.getValue());
        engF.setValue("3456,134");
        assertEquals(3456.134f, engF.getNumber().floatValue(),0);
        
        req = new MockRequest(Locale.GERMANY);
        MockContext.initContext(req);
        NumberField germanF = new NumberField("de");
        
        germanF.setNumber(decNum);
        assertEquals("2,56", germanF.getValue());
        germanF.setValue("3.456,134");
        assertEquals(3456.134f, germanF.getNumber().floatValue(),0);
    }
    
    public void testOnProcess() {
        MockRequest req = new MockRequest(Locale.US);
        Map params = req.getParameterMap();
        MockContext.initContext(req);
        
        NumberField engF = new NumberField("en");
        engF.setPattern("#,##0.00");
        
        engF.setValidate(false);
        params.put("en", "no number");
        assertTrue(engF.onProcess());
        assertEquals("no number", engF.getValue());
        assertTrue(engF.isValid());
        assertNull(engF.getNumber());
        engF.validate();
        assertFalse(engF.isValid());
        
        engF = new NumberField("en");
        engF.setPattern("#,##0.00");
        params.put("en", "12.3");

        engF.setValidate(false);
        assertTrue(engF.onProcess());
        assertEquals("12.3",engF.getValue());
        assertEquals(12.3f,engF.getNumber().floatValue(),0);
        engF.validate();
        assertEquals("12.30",engF.getValue());
        
        engF = new NumberField("en");
        engF.setPattern("#,##0.00");
        params.put("en", "12.3");
        
        assertTrue(engF.onProcess());
        assertEquals("12.30",engF.getValue());
        assertEquals("12.3", engF.getRequestValue());
        
        params.put("en", "some value");
        assertTrue(engF.onProcess());
        assertEquals("some value", engF.getValue());
        assertNull(engF.getNumber());
        assertEquals("some value", engF.getRequestValue());
    }
    
    public void testValidate() {
        MockRequest req = new MockRequest(Locale.US);
        Map params = req.getParameterMap();
        MockContext.initContext(req);
        
        NumberField engF = new NumberField("en");
        engF.setPattern("0");
        
        engF.setMaxValue(100);
        engF.setMinValue(1);
        engF.setRequired(true);
        
        params.put("en", "2.23");
        assertTrue(engF.onProcess());
        assertTrue(engF.isValid());
        assertEquals("2", engF.getValue());
        
        engF.setValue("123,45");
        engF.validate();
        assertFalse(engF.isValid());
        assertEquals("123,45", engF.getValue());
        
        engF.setValue("-12");
        engF.validate();
        assertFalse(engF.isValid());
        assertEquals("-12", engF.getValue());
        
        engF = new NumberField("en");
        engF.setPattern("0");
        
        engF.setRequired(true);
        params.remove("en");
        
        assertTrue(engF.onProcess());
        assertFalse(engF.isValid());
        assertEquals(0, engF.getValue().length());
        
        engF.setValue("");
        assertFalse(engF.isValid());
        assertEquals("",engF.getValue());
        
        engF.setValue("some text");
        assertFalse(engF.isValid());
        assertEquals("some text", engF.getValue());    
    }
    
}
