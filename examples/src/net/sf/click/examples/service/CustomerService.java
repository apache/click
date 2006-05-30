package net.sf.click.examples.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.examples.domain.Customer;

import org.objectstyle.cayenne.query.SelectQuery;


public class CustomerService extends CayenneTemplate {


    public List getCustomers() {
        SelectQuery query = new SelectQuery(Customer.class);
        query.addOrdering("name", true);
        return performQuery(query);
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
//        query.addOrdering("name", true);
        query.setPageSize(pageSize);
        List list = performQuery(query);

        List pageList = new ArrayList(pageSize);
        for (int i = 0; i < pageSize; i++) {
            pageList.add(list.get(offset + i));
        }

        return pageList;
    }

}
