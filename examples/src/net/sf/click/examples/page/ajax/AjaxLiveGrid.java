package net.sf.click.examples.page.ajax;

import java.util.List;

import net.sf.click.examples.page.BorderPage;

/**
 * Provides an Ajax demo of Rico's LiveGrid.
 *
 * @author Phil Barnes
 */
public class AjaxLiveGrid extends BorderPage {

    public String headInclude = "ajax/ajax-live-grid-head.htm";
    public String addLoadEvent = "onLoad";

    public void onInit() {
        super.onInit();

        List customerList = getCustomerService().getCustomers();
        addModel("customers", customerList);

        addModel("totalRows", new Integer(customerList.size()));

        // Always start at a 0 offset
        addModel("offset", new Integer(0));
    }

}
