package examples.page;

import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Option;
import net.sf.click.control.Select;
import examples.domain.Customer;
import examples.domain.CustomerDAO;

/**
 * Provides an ActionDemo example Page.
 *
 * @author Malcolm Edgar
 */
public class Ajax extends BorderedPage {

    public void onInit() {
        
        addModel("head-include", "ajax-head.htm");
        addModel("body-onload", "registerAjaxStuff();");
           
        Select customerSelect = new Select("customerSelect");
        customerSelect.setSize(7);
        customerSelect.setAttribute("onchange", "onClickCustomers(this);");
        addControl(customerSelect);

        List customerList = CustomerDAO.getCustomersSortedByName();
        for (Iterator i = customerList.iterator(); i.hasNext();) {
            Customer customer = (Customer) i.next();
            customerSelect.add(new Option(customer.getId(), customer.getName()));  
        }
    }

}
