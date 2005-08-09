package examples.page;

import examples.domain.CustomerDAO;
import net.sf.click.Panel;
import net.sf.click.panel.BasicPanel;
import net.sf.click.panel.ListPanel;
import net.sf.click.panel.TabbedPanel;

import java.util.List;

public class TabbedPanelDemo extends BorderedPage {

    public void onInit() {
        List customers = CustomerDAO.getCustomersSortedByName();
        addModel("customers", customers);

        TabbedPanel tabbedPanel = new TabbedPanel("tabbedPanel");
        Panel panel1 = new BasicPanel("panel1", "customersPanel1.htm");
        panel1.setLabel("The First Panel");
        tabbedPanel.addPanel(panel1,true);
        Panel panel2 = new BasicPanel("panel2", "customersPanel2.htm");
        panel2.setLabel("The Second Panel");
        tabbedPanel.addPanel(panel2,false);
        Panel panel3 = new BasicPanel("panel3", "customersPanel3.htm");
        panel3.setLabel("The Third Panel");
        tabbedPanel.addPanel(panel3,false);
        addPanel(tabbedPanel);
    }


}
