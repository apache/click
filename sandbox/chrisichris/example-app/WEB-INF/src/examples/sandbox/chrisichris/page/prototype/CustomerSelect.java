package examples.sandbox.chrisichris.page.prototype;

import java.util.Iterator;
import java.util.List;

import net.sf.click.Page;
import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.sandbox.chrisichris.prototype.AjaxAction;
import net.sf.click.sandbox.chrisichris.prototype.AjaxPage;
import net.sf.click.sandbox.chrisichris.prototype.PrototypeAjax;
import examples.domain.Customer;
import examples.domain.CustomerDAO;

public class CustomerSelect extends AjaxPage {

    public static final AjaxAction customerAction = new AjaxAction() {
        public void doExecute(Page page) {
            CustomerSelect cs = (CustomerSelect) page;
            String id = page.getContext().getRequestParameter("id");
            Long lid = Long.valueOf(id);
            Customer customer = CustomerDAO.findCustomerByID(lid);
            cs.addModel("customer", customer);
            cs.setContentType("text/xml");
            cs.setTemplate("/prototype/CustomerSelect.htm?customerDetails");
        }
    };

    static {
        AjaxAction.createActionMap(CustomerSelect.class);
    }

    private Select select = new Select("customerSelect");

    public CustomerSelect() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.click.sandbox.christian.prototype.AjaxPage#onInitAlways()
     */
    public void onInitPage() {
        addControl(select);
        List customerList = CustomerDAO.getCustomersSortedByName(8);
        for (Iterator i = customerList.iterator(); i.hasNext();) {
            Customer customer = (Customer) i.next();
            select.add(new Option(customer.getId(), customer.getName()));
        }
        select.setSize(customerList.size());

        // the ajax code
        PrototypeAjax pA = new PrototypeAjax(customerAction
                .getUrl(getContext()));
        pA.addJSParameter("id", "this.value");

        addModel("selectAjax", pA);
        select.setAttribute("onchange", pA.onClickJS());

    }

}
