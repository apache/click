package net.sf.click.examples.page.panel;

import java.util.List;

import net.sf.click.control.Panel;
import net.sf.click.examples.page.BorderPage;
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
    public List customers;

    public ListPanelDemo() {
        listPanel.addControl(new Panel("panel1", "/panel/customersPanel1.htm"));
        listPanel.addControl(new Panel("panel2", "/panel/customersPanel2.htm"));
        listPanel.addControl(new Panel("panel3", "/panel/customersPanel3.htm"));
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        customers = getCustomerService().getCustomersSortedByName(12);
    }

}
