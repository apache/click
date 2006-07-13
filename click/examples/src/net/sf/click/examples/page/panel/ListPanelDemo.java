package net.sf.click.examples.page.panel;

import java.util.List;

import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.panel.Panel;
import net.sf.click.extras.panel.ListPanel;

/**
 * Provides an ListPanel demonstration.
 * <p/>
 * Please note the ListPanel control will be automatically add to the Page using
 * the fields name "listPanel".
 *
 * @author Phil Barnes
 */
public class ListPanelDemo extends BorderPage {

    public ListPanel listPanel = new ListPanel();

    public ListPanelDemo() {
        listPanel.addPanel(new Panel("panel1", "/panel/customersPanel1.htm"));
        listPanel.addPanel(new Panel("panel2", "/panel/customersPanel2.htm"));
        listPanel.addPanel(new Panel("panel3", "/panel/customersPanel3.htm"));
    }

    public void onRender() {
        List customers = getCustomerService().getCustomersSortedByName(12);
        addModel("customers", customers);
    }

}
