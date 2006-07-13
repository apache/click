package net.sf.click.examples.page.panel;

import java.util.List;

import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.panel.Panel;
import net.sf.click.extras.panel.TabbedPanel;

/**
 * Provides an TabbedPanel demonstration.
 *
 * @author Phil Barnes
 */
public class TabbedPanelDemo extends BorderPage {

    public TabbedPanelDemo() {
        TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");

        Panel panel1 = new Panel("panel1", "panel/customersPanel1.htm");
        panel1.setLabel("The First Panel");
        tabbedPanel.addPanel(panel1, true);

        Panel panel2 = new Panel("panel2", "panel/customersPanel2.htm");
        panel2.setLabel("The Second Panel");
        tabbedPanel.addPanel(panel2);

        Panel panel3 = new Panel("panel3", "panel/customersPanel3.htm");
        panel3.setLabel("The Third Panel");
        tabbedPanel.addPanel(panel3);

        addControl(tabbedPanel);
    }

    public void onRender() {
        List customers = getCustomerService().getCustomersSortedByName(12);
        addModel("customers", customers);
    }

}
