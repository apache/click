package examples.page.ajax;

import java.util.List;

import examples.domain.CustomerDAO;
import examples.page.BorderedPage;

/**
 * Provides an Ajax demo of Rico's LiveGrid.
 *
 * @author Phil Barnes
 */
public class AjaxLiveGrid extends BorderedPage {

    public AjaxLiveGrid() {
        addModel("body-onload", "javascript:bodyOnLoad();");

        List customerList = CustomerDAO.getAllCustomers();
        addModel("customers", customerList);
        addModel("totalRows",new Integer(customerList.size()));
        // Always start at a 0 offset
        addModel("offset", new Integer(0));
    }

}
