package net.sf.click.sandbox.chrisichris.control;

import net.sf.click.MockContext;
import net.sf.click.MockRequest;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import junit.framework.TestCase;

public class SubmitLinkTest extends TestCase{

    public SubmitLinkTest() {
        super();
    }
    
    public void testSetForm() {
        Form form = new Form("name");
        SubmitLink link = new SubmitLink("link");
        SubmitLink link2 = new SubmitLink("link2");
        
        form.add(link);
        //see that a second add does not read
        form.add(link2);
        
        assertNotNull(getValueField(form));
        assertNotNull(getClickField(form));
        assertTrue(getValueField(form) instanceof HiddenField);
        assertTrue(getClickField(form) instanceof HiddenField);
        
        form = new Form("form");
        link = new SubmitLink("link");
        link2 = new SubmitLink("link2");
        link.setForm(form);
        link2.setForm(form);
        
        assertNotNull(getValueField(form));
        assertNotNull(getClickField(form));
        assertTrue(getValueField(form) instanceof HiddenField);
        assertTrue(getClickField(form) instanceof HiddenField);
    }
    
    public void testOnProcessForm() {
        MockRequest req = new MockRequest();
        MockContext ctxt = new MockContext(req);

        Form form = new Form("name");
        form.setContext(ctxt);
        
        SubmitLink link = new SubmitLink("link");
        form.add(link);
        SubmitLink link2 = new SubmitLink("link2");
        form.add(link2);
        
        req.getParameterMap().put(Form.FORM_NAME, form.getName());
        req.getParameterMap().put(SubmitLink.CLICK_FIELD_NAME, "link");
        req.getParameterMap().put(SubmitLink.VALUE_FIELD_NAME, "some value");
        
        assertFalse(link.isClicked());
        assertEquals("",link.getValue());
        assertFalse(link2.isClicked());
        assertEquals("",link2.getValue());

        assertTrue(form.onProcess());
        assertTrue(link.isClicked());
        assertEquals("some value",link.getValue());
        assertFalse(link2.isClicked());
        
    }
    
    public void testOnProcessLink() {
        MockRequest req = new MockRequest();
        MockContext ctxt = new MockContext(req);

        Form form = new Form("name");
        form.setContext(ctxt);
        SubmitLink link = new SubmitLink("link");
        link.setForm(form);
        SubmitLink link2 = new SubmitLink("link2");
        link2.setForm(form);
        
        req.getParameterMap().put(Form.FORM_NAME, form.getName());
        req.getParameterMap().put(SubmitLink.CLICK_FIELD_NAME, "link");
        req.getParameterMap().put(SubmitLink.VALUE_FIELD_NAME, "some value");
        
        assertFalse(link.isClicked());
        assertEquals("",link.getValue());
        
        assertTrue(form.onProcess());
        assertTrue(link.onProcess());
        assertTrue(link2.onProcess());
        assertTrue(link.isClicked());
        assertEquals("some value",link.getValue());
        assertFalse(link2.isClicked());
    }

    private Field getValueField(Form form) {
        return form.getField(SubmitLink.VALUE_FIELD_NAME);
    }
    
    private Field getClickField(Form form) {
        return form.getField(SubmitLink.CLICK_FIELD_NAME);
    }

}
