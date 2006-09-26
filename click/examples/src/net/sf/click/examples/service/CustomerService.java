package net.sf.click.examples.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.click.examples.domain.Customer;

import org.objectstyle.cayenne.exp.Expression;
import org.objectstyle.cayenne.exp.ExpressionFactory;
import org.objectstyle.cayenne.query.SelectQuery;

/**
 * Provides a Customer Service.
 *
 * @see Customer
 *
 * @author Malcolm Edgar
 */
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

    public List getCustomers(Date from, Date to) {
        Expression qual = ExpressionFactory.noMatchExp("dateJoined", null);

        if (from != null) {
            qual = qual.andExp(ExpressionFactory.greaterOrEqualExp("dateJoined", from));
        }
        if (to != null) {
            qual = qual.andExp(ExpressionFactory.lessOrEqualExp("dateJoined", to));
        }

        SelectQuery query = new SelectQuery(Customer.class, qual);
        query.addOrdering("dateJoined", true);

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
        if (customer != null) {
            deleteObject(customer);
            commitChanges();
        }
    }

    public Customer findCustomerByID(Integer id) {
        return (Customer) getCustomer(id);
    }

    public Customer findCustomerByID(String value) {
        return (Customer) getCustomer(Integer.valueOf(value));
    }

    public List findCustomersByName(String value) {
        Expression template = Expression.fromString("name likeIgnoreCase $name");
        Expression e = template.expWithParameters(toMap("name", "%" + value + "%"));
        return  performQuery(new SelectQuery(Customer.class, e));
    }

    public List findCustomersByAge(String value) {
        return performQuery(Customer.class, "age", Integer.valueOf(value));
    }

    public List findCustomersByPage(int offset, int pageSize) {
        SelectQuery query = new SelectQuery(Customer.class);
        query.setPageSize(pageSize);
        List list = performQuery(query);

        List pageList = new ArrayList(pageSize);
        for (int i = 0; i < pageSize; i++) {
            pageList.add(list.get(offset + i));
        }

        return pageList;
    }

}
