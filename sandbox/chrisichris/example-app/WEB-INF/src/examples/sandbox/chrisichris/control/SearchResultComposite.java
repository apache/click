package examples.sandbox.chrisichris.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import examples.domain.Customer;
import examples.domain.CustomerDAO;

import net.sf.click.sandbox.chrisichris.control.ChildActionLink;
import net.sf.click.sandbox.chrisichris.control.Composite;
import net.sf.click.sandbox.chrisichris.control.StatePage;

public class SearchResultComposite extends Composite {

    private ChildActionLink link;
    public SearchResultComposite() {
        super();
    }

    public SearchResultComposite(String name) {
        super(name);
    }
    
    public void setSearchString(String searchString) {
        StatePage.setStateAttribute(this,"searchString",searchString);
        setSelectedCustomerId(null);
    }
    
    public String getSearchString() {
        String ret = (String) StatePage.getStateAttribute(this,"searchString");
        return ret;
    }
    
    public void setSelectedCustomerId(Long id) {
        StatePage.setStateAttribute(this,"selectedCustomer",id);
    }
    
    public Long getSelectedCustomerId() {
        Long ret = (Long) StatePage.getStateAttribute(this,"selectedCustomer");
        return ret;
    }
    
    public Customer getSelectedCustomer() {
        Long id = getSelectedCustomerId();
        if(id == null) {
            return null;
        }
        return CustomerDAO.getCustomer(id);
    }
    
    public List getCustomers() {
        String txt = getSearchString();
        if(txt == null) {
            return Collections.EMPTY_LIST;
        }
        List customers = CustomerDAO.getCustomersSortedByName();
        List selCustomers = new ArrayList();
        for (Iterator it = customers.iterator(); it.hasNext();) {
            Customer cust = (Customer) it.next();
            if(cust.getName().startsWith(txt)) {
                selCustomers.add(cust);
            }
        }
        return selCustomers;
        
    }
    
    protected void onInit() {
        link = new ChildActionLink("link",this,"onCustomerSelected");
        addControl(link);
    }
    
    public boolean onCustomerSelected() {
        Long id = link.getValueLong();
        setSelectedCustomerId(id);
        return false;
    }
    
    protected void onRender() {
        List customers = getCustomers();
        if(customers.size() != 0) {
            addModel("customers",customers);
        }
        
        Customer cust = getSelectedCustomer();
        if(cust != null) {
            addModel("showCustomer",cust);
        }
    }

}
