package examples.page;

import examples.domain.CustomerDAO;
import net.sf.click.extras.panel.BasicPanel;
import net.sf.click.extras.panel.ListPanel;
import net.sf.click.extras.panel.Panel;

import java.util.List;

/**
 * Provides an ListPanel demonstration.
 *
 * @author Phil Barnes
 */
public class ListPanelDemo extends BorderedPage {

    public void onInit() {
        List customers = CustomerDAO.getCustomersSortedByName(8);
        addModel("customers", customers);

        ListPanel listPanel = new ListPanel("listPanel");
        Panel panel1 = new BasicPanel("panel1", "customersPanel1.htm");
        listPanel.addPanel(panel1);
        Panel panel2 = new BasicPanel("panel2", "customersPanel2.htm");
        listPanel.addPanel(panel2);
        Panel panel3 = new BasicPanel("panel3", "customersPanel3.htm");
        listPanel.addPanel(panel3);

        listPanel.setPage(this);
        addModel(listPanel.getName(), listPanel);
    }

}
