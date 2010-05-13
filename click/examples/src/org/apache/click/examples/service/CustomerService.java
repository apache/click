/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.examples.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.query.IndirectQuery;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.click.examples.domain.Customer;
import org.apache.click.extras.cayenne.CayenneTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

/**
 * Provides a Customer Service.
 *
 * @see Customer
 */
@Component
public class CustomerService extends CayenneTemplate {

    @SuppressWarnings("unchecked")
    public List<Customer> getCustomers() {
        SelectQuery query = new SelectQuery(Customer.class);
        query.addOrdering(Customer.NAME_PROPERTY, true);
        return (List<Customer>) performQuery(query);
    }

    public int getNumberOfCustomers() {
        CountQuery query = new CountQuery(Customer.class);
        List result = performQuery(query);
        Map row = (Map) result.get(0);
        return (Integer) row.get("C");
    }

    @SuppressWarnings("unchecked")
    public List<Customer> getCustomersSortedBy(String property, boolean ascending,
        boolean useSharedCache) {

        SelectQuery query = new SelectQuery(Customer.class);
        if (property != null) {
            query.addOrdering(property, ascending);
        }

        if (useSharedCache) {
            // Example use of shared cache which is managed with oscache.properties
            query.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE);
        }

        return (List<Customer>) performQuery(query);
    }

    public List<Customer> getCustomersSortedBy(String property, boolean ascending) {
        return getCustomersSortedBy(property, ascending, false);
    }

    @SuppressWarnings("unchecked")
    public List<Customer> getCustomers(String name, Date startDate) {
        SelectQuery query = new SelectQuery(Customer.class);

        if (StringUtils.isNotBlank(name)) {
            query.andQualifier(ExpressionFactory.likeIgnoreCaseExp(Customer.NAME_PROPERTY, "%" + name + "%"));
        }
        if (startDate != null) {
            query.andQualifier(ExpressionFactory.greaterOrEqualExp(Customer.DATE_JOINED_PROPERTY, startDate));
        }

        query.addOrdering(Customer.NAME_PROPERTY, true);
        query.addOrdering(Customer.DATE_JOINED_PROPERTY, true);

        return (List<Customer>) performQuery(query);
    }

    @SuppressWarnings("unchecked")
    public List<Customer> getCustomerNamesLike(String name) {
        SelectQuery query = new SelectQuery(Customer.class);

        query.andQualifier(ExpressionFactory.likeIgnoreCaseExp(Customer.NAME_PROPERTY, "%" + name + "%"));

        query.addOrdering(Customer.NAME_PROPERTY, true);

        query.setFetchLimit(10);

        List list = performQuery(query);

        for (int i = 0; i < list.size(); i++) {
            list.set(i, ((Customer)list.get(i)).getName());
        }

        return (List<Customer>) list;
    }

    @SuppressWarnings("unchecked")
    public List<Customer> getCustomers(Date from, Date to) {
        Expression qual = ExpressionFactory.noMatchExp(Customer.DATE_JOINED_PROPERTY, null);

        if (from != null) {
            qual = qual.andExp(ExpressionFactory.greaterOrEqualExp(Customer.DATE_JOINED_PROPERTY, from));
        }
        if (to != null) {
            qual = qual.andExp(ExpressionFactory.lessOrEqualExp(Customer.DATE_JOINED_PROPERTY, to));
        }

        SelectQuery query = new SelectQuery(Customer.class, qual);
        query.addOrdering(Customer.DATE_JOINED_PROPERTY, true);

        return (List<Customer>) performQuery(query);
    }

    @SuppressWarnings("unchecked")
    public List<Customer> getCustomersSortedByName(int rows) {
        SelectQuery query = new SelectQuery(Customer.class);
        query.addOrdering(Customer.NAME_PROPERTY, true);
        query.setFetchLimit(rows);
        return (List<Customer>) performQuery(query);
    }

    @SuppressWarnings("unchecked")
    public List<Customer> getCustomersSortedByDateJoined(int rows) {
        SelectQuery query = new SelectQuery(Customer.class);
        query.addOrdering(Customer.DATE_JOINED_PROPERTY, true);
        query.setFetchLimit(rows);
        return (List<Customer>) performQuery(query);
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
            return getCustomerForID(value);
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

    @SuppressWarnings("unchecked")
    public List<Customer> getCustomersForName(String value) {
        Expression template = Expression.fromString("name likeIgnoreCase $name");
        Expression e = template.expWithParameters(toMap(Customer.NAME_PROPERTY, "%" + value + "%"));
        return (List<Customer>) performQuery(new SelectQuery(Customer.class, e));
    }

    @SuppressWarnings("unchecked")
    public List<Customer> getCustomersForAge(String value) {
        int age = NumberUtils.toInt(value);
        return (List<Customer>) performQuery(Customer.class, Customer.AGE_PROPERTY, age);
    }

    @SuppressWarnings("unchecked")
    public List<Customer> getCustomersForPage(int offset, int pageSize,
        String sortColumn, boolean ascending) {

        SelectQuery query = new SelectQuery(Customer.class);
        if (StringUtils.isNotBlank(sortColumn)) {
            query.addOrdering(sortColumn, ascending);
        }
        query.setFetchOffset(offset);
        query.setFetchLimit(pageSize);

        return (List<Customer>) performQuery(query);
    }

    public List<Customer> getTopCustomersForPage(int offset, int pageSize) {
        List<Customer> list = getCustomersSortedBy(Customer.HOLDINGS_PROPERTY, false);

        List<Customer> pageList = new ArrayList<Customer>(pageSize);
        for (int i = 0; i < pageSize; i++) {
            // Increment row index with the offset
            int rowIndex = offset + i;

            // Guard against rowIndex that moves past the end of the list
            if (rowIndex >= list.size()) {
                break;
            }
            pageList.add(list.get(rowIndex));
        }

        return pageList;
    }

    public List<String> getInvestmentCategories() {
        List<String> categories = new ArrayList<String>();

        categories.add("Bonds");
        categories.add("Commercial Property");
        categories.add("Options");
        categories.add("Residential Property");
        categories.add("Stocks");

        return categories;
    }

    /**
     * A custom Cayenne query which performs a count(*) query on the database.
     */
    class CountQuery extends IndirectQuery {

        private static final long serialVersionUID = 1L;

        protected Class objectClass;

        public CountQuery(Class objectClass) {
            this.objectClass = objectClass;
        }

        @SuppressWarnings("deprecation")
        protected Query createReplacementQuery(EntityResolver resolver) {
            DbEntity entity = resolver.lookupDbEntity(objectClass);

            if (entity == null) {
                throw new CayenneRuntimeException(
                    "No entity is mapped for java class: "
                    + objectClass.getName());
            }

            String sql = "SELECT #result('count(*)' 'int' 'C') FROM "
                + entity.getName();
            SQLTemplate replacement = new SQLTemplate(entity, sql);
            replacement.setFetchingDataRows(true);
            return replacement;
        }
    }
}
