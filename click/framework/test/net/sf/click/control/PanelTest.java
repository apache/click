package net.sf.click.control;

import junit.framework.TestCase;

/**
 * Test Panel behavior.
 */
public class PanelTest extends TestCase {

    /**
     * Check that Panel.getId() returns null when Panel name is null.
     *
     * CLK-464
     */
    public void testProcessControlWhenNameIsNull() {
        // Check that panel without name returns null id
        Panel panel = new Panel();
        assertNull(panel.getId());
        
        // Check that panel with name returns name id
        String name = "mypanel";
        panel = new Panel(name);
        assertEquals(name, panel.getId());
    }
}
