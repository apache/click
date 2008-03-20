package net.sf.click;

import junit.framework.TestCase;
import net.sf.click.servlet.MockRequest;
import net.sf.click.control.TextArea;

/**
 *
 * @author Bob Schellink
 */
public class MockRequestTest extends TestCase {

    public void testDynamicRequest() {
        MockContext context = MockContext.initContext();
        MockRequest request = (MockRequest) context.getMockRequest();

        TextArea textArea = new TextArea("text");
        assertEquals("text", textArea.getName());

        request.setParameter("param", "value");
        request.getParameterMap().put("text", "textvalue");

        assertTrue(textArea.onProcess());
        assertTrue(textArea.isValid());
        assertEquals("textvalue", textArea.getValue());
        assertEquals("textvalue", textArea.getValueObject());
        
        // Check that getParameterMap() is modifiable by adding a 
        // key/value pair.
        context = (MockContext) Context.getThreadLocalContext();
        context.getRequest().getParameterMap().put("textvalue", 
          textArea.getValue());
    }
}
