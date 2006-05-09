package examples.page.ajax;

import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Option;
import net.sf.click.control.Select;
import examples.domain.Customer;
import examples.domain.CustomerDAO;
import examples.page.BorderedPage;

/**
 * Provides an ActionDemo example Page.
 *
 * @author Malcolm Edgar
 */
public class Ajax extends BorderedPage {

    public Ajax() {
        addModel("head-include", "ajax/ajax-head.htm");
        addModel("body-onload", "registerAjax();");

        Select customerSelect = new Select("customerSelect");
        customerSelect.setAttribute("onchange", "onCustomerChange(this);");
        addControl(customerSelect);

        List customerList = CustomerDAO.getCustomersSortedByName(8);
        for (Iterator i = customerList.iterator(); i.hasNext();) {
            Customer customer = (Customer) i.next();
            customerSelect.add(new Option(customer.getId(), customer.getName()));
        }
        customerSelect.setSize(customerList.size());
    }

}
