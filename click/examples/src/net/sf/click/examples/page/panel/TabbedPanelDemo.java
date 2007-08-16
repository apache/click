package net.sf.click.examples.page.panel;

import java.util.List;

import net.sf.click.control.Panel;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.panel.TabbedPanel;

/**
 * Provides an TabbedPanel demonstration.
 *
 * @author Phil Barnes
 */
public class TabbedPanelDemo extends BorderPage {

    public TabbedPanel tabbedPanel = new TabbedPanel();
    public List customers;

    public TabbedPanelDemo() {
        Panel panel1 = new Panel("panel1", "panel/customersPanel1.htm");
        panel1.setLabel("The First Panel");
        tabbedPanel.addControl(panel1);

        Panel panel2 = new Panel("panel2", "panel/customersPanel2.htm");
        panel2.setLabel("The Second Panel");
        tabbedPanel.addControl(panel2);

        Panel panel3 = new Panel("panel3", "panel/customersPanel3.htm");
        panel3.setLabel("The Third Panel");
        tabbedPanel.addControl(panel3);
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        customers = getCustomerService().getCustomersSortedByName(12);
    }

}
