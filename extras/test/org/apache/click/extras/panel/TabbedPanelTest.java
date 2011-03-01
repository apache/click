/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.panel;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.MockContext;
import org.apache.click.Page;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Panel;

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
     * Test that the request parameter <tt>tabPanelIndex-<TabbedPanelName></tt>
     * sets the new active panel correctly.
     */
    public void testTabPanelIndexWithNameParameter() {
        MockContext context = MockContext.initContext();

        TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");

        // Since tabbedPanel is zero index based, setting tabPanelIndex to 1
        // should set the active panel to panel2
        context.getMockRequest().setParameter("tabPanelIndex-" + tabbedPanel.getName(), "1");

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

        TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");

        // Simulate user selecting panel2
        context.getMockRequest().setParameter(ActionLink.ACTION_LINK, "tabLink-" + tabbedPanel.getName());
        context.getMockRequest().setParameter(ActionLink.VALUE, "panel2");

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

        TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");

        // Simulate user selecting panel2
        context.getMockRequest().setParameter(ActionLink.ACTION_LINK, "tabLink-" + tabbedPanel.getName());
        context.getMockRequest().setParameter(ActionLink.VALUE, "panel2");

        tabbedPanel.add(new Panel("panel1"));
        tabbedPanel.add(new Panel("panel2"));

        tabbedPanel.setTabListener(new ActionListener() {
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                return false;
            }
        });

        tabbedPanel.onInit();
        tabbedPanel.onProcess();
        // Simulate ClickServlet triggering all action events
        boolean actionResult = context.executeActionListeners();

        // If tab listener was triggered the actionResult should be false
        assertFalse(actionResult);
    }

    /**
     * Check that adding controls replace existing controls with the same name.
     *
     * CLK-666
     */
    public void testReplace() {
        MockContext.initContext();

        Page page = new Page();
        TabbedPanel panel = new TabbedPanel("panel");
        page.addControl(panel);

        // Add two panels named child1 and child2
        Panel child1 = new Panel("child1");
        Panel child2 = new Panel("child2");
        panel.add(child1);
        panel.add(child2);

        // Execute onInit event
        panel.onInit();

        assertEquals(3, panel.getControlMap().size());
        assertEquals(3, panel.getControls().size());
        assertSame(child1, panel.getControls().get(1));
        assertSame(child2, panel.getControls().get(2));
        assertSame(child1, panel.getPanels().get(0));
        assertSame(child2, panel.getPanels().get(1));
        assertTrue(child1.isActive());
        assertFalse(child2.isActive());

        // Add another two panels named child1 and child2 and test that these
        // panels replaces the previous panels
        child1 = new Panel("child1");
        child2 = new Panel("child2");
        panel.add(child1);
        panel.add(child2);
        assertEquals(3, panel.getControlMap().size());
        assertEquals(3, panel.getControls().size());
        assertSame(child1, panel.getControls().get(1));
        assertSame(child2, panel.getControls().get(2));
        assertSame(child1, panel.getPanels().get(0));
        assertSame(child2, panel.getPanels().get(1));
        assertTrue(child1.isActive());
        assertFalse(child2.isActive());
    }


    /**
     * Test that TabbedPanel.getState contains the active panel.
     *
     * CLK-715
     */
    public void testGetState() {
        // Setup Panel
        TabbedPanel panel = new TabbedPanel("panel");

        // Add two panels named child1 and child2
        Panel child1 = new Panel("child1");
        Panel child2 = new Panel("child2");
        panel.add(child1);
        panel.add(child2);

        Map expectedTabLinkState = new HashMap();
        expectedTabLinkState.put("id", "1");
        panel.getTabLink().setParameters(expectedTabLinkState);

        panel.setActivePanel(child2);

        String expectedActivePanel = "child2";

        // Get state
        Object[] state = (Object[]) panel.getState();

        // Perform tests
        assertEquals(expectedActivePanel, state[0]);
        assertEquals(expectedTabLinkState, state[1]);
    }

    /**
     * Test that TabbedPanel.setState set the active panel.
     *
     * CLK-715
     */
    public void testSetState() {
        // Setup Panel
        TabbedPanel panel = new TabbedPanel("panel");

        // Add two panels named child1 and child2
        Panel child1 = new Panel("child1");
        Panel child2 = new Panel("child2");
        panel.add(child1);
        panel.add(child2);

        Map expectedTabLinkState = new HashMap();
        expectedTabLinkState.put("id", "1");

        String expectedActivePanelName = "child2";

        Object[] state = new Object[2];
        state[0] = expectedActivePanelName;
        state[1] = expectedTabLinkState;

        // Initially child1 should be active
        assertEquals(child1, panel.getActivePanel());
        // TabLink shouldn't have any parameters
        assertEquals(0, panel.getTabLink().getParameters().size());

        // Set state
        panel.setState(state);

        // Perform tests
        assertEquals(panel.getActivePanel().getName(), expectedActivePanelName);
        assertEquals(expectedTabLinkState, panel.getTabLink().getParameters());
    }
}
