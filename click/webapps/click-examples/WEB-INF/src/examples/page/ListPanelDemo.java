package examples.page;

import examples.domain.CustomerDAO;
import net.sf.click.Panel;
import net.sf.click.panel.BasicPanel;
import net.sf.click.panel.ListPanel;

import java.util.List;

public class ListPanelDemo extends BorderedPage {

    public void onInit() {
        List customers = CustomerDAO.getCustomersSortedByName();
        addModel("customers", customers);

        ListPanel listPanel = new ListPanel("listPanel");
        Panel panel1 = new BasicPanel("panel1", "customersPanel1.htm");
        listPanel.addPanel(panel1);
        Panel panel2 = new BasicPanel("panel2", "customersPanel2.htm");
        listPanel.addPanel(panel2);
        Panel panel3 = new BasicPanel("panel3", "customersPanel3.htm");
        listPanel.addPanel(panel3);
        addPanel(listPanel);
    }


}
