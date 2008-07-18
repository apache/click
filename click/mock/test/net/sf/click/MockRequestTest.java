package net.sf.click;

import junit.framework.TestCase;
import net.sf.click.servlet.MockRequest;
import net.sf.click.control.TextArea;

/**
 *
 * @author Bob Schellink
 */
public class MockRequestTest extends TestCase {

    // Indicates that the textArea actionListener was invoked
    private boolean actionCalled = false;
    
    public void testDynamicRequest() {
        MockContext context = MockContext.initContext();
        MockRequest request = (MockRequest) context.getMockRequest();

        TextArea textArea = new TextArea("text");
        assertEquals("text", textArea.getName());

        request.setParameter("param", "value");
        request.getParameterMap().put("text", "textvalue");

        // Registry a listener which must be invoked
        textArea.setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                // When action is invoked, set flag to true
                return actionCalled = true;
            }
        });
        assertTrue(textArea.onProcess());

        // Fire all action events that was registered in the onProcess method
        context.fireActionEventsAndClearRegistry();

        assertTrue("TextArea action was not invoked", actionCalled);
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
