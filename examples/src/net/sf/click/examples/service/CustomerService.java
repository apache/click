package net.sf.click.examples.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.click.examples.domain.Customer;
import net.sf.click.extras.cayenne.CayenneTemplate;

import org.apache.commons.lang.StringUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

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

    public List getCustomersSortedBy(String property, boolean ascending) {
        SelectQuery query = new SelectQuery(Customer.class);
        if (property != null) {
            query.addOrdering(property, ascending);
        }
        return performQuery(query);
    }

    public List getCustomers(String name, Date startDate) {
        SelectQuery query = new SelectQuery(Customer.class);

        if (StringUtils.isNotBlank(name)) {
            query.andQualifier(ExpressionFactory.likeIgnoreCaseExp(Customer.NAME_PROPERTY, "%" + name + "%"));
        }
        if (startDate != null) {
            query.andQualifier(ExpressionFactory.greaterOrEqualExp(Customer.DATE_JOINED_PROPERTY, startDate));
        }

        query.addOrdering(Customer.NAME_PROPERTY, true);
        query.addOrdering(Customer.DATE_JOINED_PROPERTY, true);

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

    public void saveCustomer(Customer customer) {
        if (customer.getObjectContext() == null) {
            registerNewObject(customer);
        }
        commitChanges();
    }

    public Customer getCustomer(Object id) {
        return (Customer) getObjectForPK(Customer.class, id);
    }

    public void deleteCustomer(Integer id) {
        Customer customer = getCustomer(id);
        if (customer != null) {
            deleteObject(customer);
            commitChanges();
        }
    }

    public Customer findCustomerByID(Object value) {
        if (value != null && value.toString().length() > 0) {
            return (Customer) getCustomer(value);
        } else {
            return null;
        }
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

    public List getInvestmentCatetories() {
        List categories = new ArrayList();

        categories.add("Bonds");
        categories.add("Commerical Property");
        categories.add("Options");
        categories.add("Residential Property");
        categories.add("Stocks");

        return categories;
    }

}
