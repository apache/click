package net.sf.click.examples.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.examples.domain.Customer;

import org.objectstyle.cayenne.query.SelectQuery;


public class CustomerService extends CayenneTemplate {

    // Mock list of customers for AjaxLiveGrid example
    private static final List ALL_CUSTOMER_LIST = new ArrayList(20000);

    public List getAllCustomers() {
        if (ALL_CUSTOMER_LIST.isEmpty()) {
            List list = getCustomersSortedByName();
            for (int i = 0; i < 1; i++) {
                ALL_CUSTOMER_LIST.addAll(list);
            }
        }
        return ALL_CUSTOMER_LIST;
    }

    public List getCustomersSortedByName() {
        SelectQuery query = new SelectQuery(Customer.class);
        query.addOrdering("name", true);
        return performQuery(query);
    }

    public List getCustomersSortedByName(int rows) {
        SelectQuery query = new SelectQuery(Customer.class);
        query.addOrdering("name", true);
        query.setFetchLimit(rows);
        return performQuery(query);
    }

    public void setCustomer(Customer customer) {
        commitChanges();
    }

    public Customer getCustomer(Integer id) {
        return (Customer) objectForPK(Customer.class, id);
    }

    public void deleteCustomer(Integer id) {
        Customer customer = getCustomer(id);
        deleteObject(customer);
        commitChanges();
    }

    public Customer findCustomerByID(Integer id) {
        return (Customer) getCustomer(id);
    }

    public Customer findCustomerByID(String value) {
        return (Customer) getCustomer(Integer.valueOf(value));
    }

    public Customer findCustomerByName(String value) {
        return (Customer) findObject(Customer.class, "name", value);
    }

    public Customer findCustomerByAge(String value) {
        return (Customer) findObject(Customer.class, "age", Integer.valueOf(value));
    }

    public List findCustomersByPage(int offset, int pageSize) {
        // TODO: need to work out paging usage...
        SelectQuery query = new SelectQuery(Customer.class);
        query.addOrdering("name", true);
        query.setPageSize(pageSize);
        List list = performQuery(query);

        List pageList = new ArrayList(pageSize);
        for (int i = 0; i < pageSize; i++) {
            pageList.add(list.get(offset + 1));
        }

        return pageList;
    }

}
