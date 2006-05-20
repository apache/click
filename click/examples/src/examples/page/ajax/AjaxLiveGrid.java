package examples.page.ajax;

import java.util.List;

import examples.page.BorderPage;

/**
 * Provides an Ajax demo of Rico's LiveGrid.
 *
 * @author Phil Barnes
 */
public class AjaxLiveGrid extends BorderPage {

    public void onInit() {
        addModel("body-onload", "javascript:bodyOnLoad();");

        List customerList = getCustomerService().getAllCustomers();
        addModel("customers", customerList);
        addModel("totalRows",new Integer(customerList.size()));
        // Always start at a 0 offset
        addModel("offset", new Integer(0));
    }

}
