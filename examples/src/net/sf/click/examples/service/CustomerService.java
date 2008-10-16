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
import org.apache.commons.lang.math.NumberUtils;

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
        query.addOrdering(Customer.NAME_PROPERTY, true);
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

    public List getCustomerNamesLike(String name) {
        SelectQuery query = new SelectQuery(Customer.class);

        query.andQualifier(ExpressionFactory.likeIgnoreCaseExp(Customer.NAME_PROPERTY, "%" + name + "%"));

        query.addOrdering(Customer.NAME_PROPERTY, true);

        query.setFetchLimit(10);

        List list = performQuery(query);

        for (int i = 0; i < list.size(); i++) {
            list.set(i, ((Customer)list.get(i)).getName());
        }

        return list;
    }

    public List getCustomers(Date from, Date to) {
        Expression qual = ExpressionFactory.noMatchExp(Customer.DATE_JOINED_PROPERTY, null);

        if (from != null) {
            qual = qual.andExp(ExpressionFactory.greaterOrEqualExp(Customer.DATE_JOINED_PROPERTY, from));
        }
        if (to != null) {
            qual = qual.andExp(ExpressionFactory.lessOrEqualExp(Customer.DATE_JOINED_PROPERTY, to));
        }

        SelectQuery query = new SelectQuery(Customer.class, qual);
        query.addOrdering(Customer.DATE_JOINED_PROPERTY, true);

        return performQuery(query);
    }

    public List getCustomersSortedByName(int rows) {
        SelectQuery query = new SelectQuery(Customer.class);
        query.addOrdering(Customer.NAME_PROPERTY, true);
        query.setFetchLimit(rows);
        return performQuery(query);
    }

    public List getCustomersSortedByDateJoined(int rows) {
        SelectQuery query = new SelectQuery(Customer.class);
        query.addOrdering(Customer.DATE_JOINED_PROPERTY, true);
        query.setFetchLimit(rows);
        return performQuery(query);
    }

    public void saveCustomer(Customer customer) {
        if (customer.getObjectContext() == null) {
            registerNewObject(customer);
        }
        commitChanges();
    }

    public Customer getCustomerForID(Object id) {
        return (Customer) getObjectForPK(Customer.class, id);
    }

    public void deleteCustomer(Integer id) {
        Customer customer = getCustomerForID(id);
        if (customer != null) {
            deleteObject(customer);
            commitChanges();
        }
    }

    public Customer findCustomerByID(Object value) {
        if (value != null && value.toString().length() > 0) {
            return (Customer) getCustomerForID(value);
        } else {
            return null;
        }
    }

    public Customer findCustomerByName(String name) {
        SelectQuery query = new SelectQuery(Customer.class);
        query.andQualifier(ExpressionFactory.matchExp(Customer.NAME_PROPERTY,name));

        List list = performQuery(query);

        if (!list.isEmpty()) {
            return (Customer) list.get(0);
        } else {
            return null;
        }
    }

    public List getCustomersForName(String value) {
        Expression template = Expression.fromString("name likeIgnoreCase $name");
        Expression e = template.expWithParameters(toMap(Customer.NAME_PROPERTY, "%" + value + "%"));
        return  performQuery(new SelectQuery(Customer.class, e));
    }

    public List getCustomersForAge(String value) {
        int age = NumberUtils.toInt(value);
        return performQuery(Customer.class, Customer.AGE_PROPERTY, new Integer(age));
    }

    public List getCustomersForPage(int offset, int pageSize) {
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
