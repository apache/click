package net.sf.click.examples.page.ajax;

import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an Ajax select example Page.
 *
 * @author Malcolm Edgar
 */
public class AjaxPage extends BorderPage {

    public String headInclude = "ajax/ajax-head.htm";
    public String addLoadEvent = "registerAjax";
    public Select customerSelect = new Select("customerSelect");

    public void onInit() {
        super.onInit();

        customerSelect.setAttribute("onchange", "onCustomerChange(this);");

        List customerList = getCustomerService().getCustomersSortedByName(8);
        for (Iterator i = customerList.iterator(); i.hasNext();) {
            Customer customer = (Customer) i.next();
            customerSelect.add(new Option(customer.getId(), customer.getName()));
        }

        customerSelect.setSize(customerList.size());
    }

}
