package net.sf.click.extras.panel;

import junit.framework.TestCase;
import net.sf.click.ActionListener;
import net.sf.click.Control;
import net.sf.click.MockContext;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Panel;

public class TabbedPanelTest extends TestCase {

    public void testDefaultActivePanel() {
        MockContext.initContext();

        TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");
        tabbedPanel.add(new Panel("panel1"));
        tabbedPanel.add(new Panel("panel2"));
        tabbedPanel.onInit();
        String activePanelName = tabbedPanel.getActivePanel().getName();
        
        // By default panel1 should be the active panel
        assertEquals("panel1", activePanelName);
    }
    
    /**
     * Test that the request parameter <tt>tabPanelIndex</tt> sets the new
     * active panel correctly.
     */
    public void testTabPanelIndexParameter() {
        MockContext context = MockContext.initContext();
        
        // Since tabbedPanel is zero index based, setting tabPanelIndex to 1
        // should set the active panel to panel2
        context.getMockRequest().setParameter("tabPanelIndex", "1");

        TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");
        tabbedPanel.add(new Panel("panel1"));
        tabbedPanel.add(new Panel("panel2"));
        tabbedPanel.onInit();
        String activePanelName = tabbedPanel.getActivePanel().getName();
        
        // By default panel2 should be the active panel
        assertEquals("panel2", activePanelName);
    }
    
    /**
     * Test that if user selects panel2, panel2 becomes the active panel.
     */
    public void testTabLinkClicked() {
        MockContext context = MockContext.initContext();
        
        // Simulate user selecting panel2
        context.getMockRequest().setParameter(ActionLink.ACTION_LINK, "tabLink");
        context.getMockRequest().setParameter(ActionLink.VALUE, "panel2");

        TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");
        tabbedPanel.add(new Panel("panel1"));
        tabbedPanel.add(new Panel("panel2"));
        tabbedPanel.onInit();
        String activePanelName = tabbedPanel.getActivePanel().getName();
        
        // By default panel2 should be the active panel
        assertEquals("panel2", activePanelName);
    }
    
    /**
     * Test that registered tab listener is fired.
     *
     * CLK-432.
     */
    public void testTabListenerFired() {
        MockContext context = MockContext.initContext();

        // Simulate user selecting panel2
        context.getMockRequest().setParameter(ActionLink.ACTION_LINK, "tabLink");
        context.getMockRequest().setParameter(ActionLink.VALUE, "panel2");

        TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");
        tabbedPanel.add(new Panel("panel1"));
        tabbedPanel.add(new Panel("panel2"));

        tabbedPanel.setTabListener(new ActionListener() {
            public boolean onAction(Control source) {
                return false;
            }
        });

        tabbedPanel.onInit();
        tabbedPanel.onProcess();
        // Simulate ClickServlet triggering all action events
        boolean actionResult = context.fireActionEventsAndClearRegistry();

        // If tab listener was triggered the actionResult should be false
        assertFalse(actionResult);
    }
}
