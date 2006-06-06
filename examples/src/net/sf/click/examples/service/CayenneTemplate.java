package net.sf.click.examples.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectstyle.cayenne.DataObject;
import org.objectstyle.cayenne.DataObjectUtils;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.exp.Expression;
import org.objectstyle.cayenne.exp.ExpressionFactory;
import org.objectstyle.cayenne.query.Query;
import org.objectstyle.cayenne.query.SelectQuery;

/**
 * Provides a Spring style template for Cayenne data access operations. This
 * template uses thread bound <tt>DataContext</tt> for all data access
 * operations.
 * <p/>
 * This class is designed to be extended by custom DAO or
 * Service subclasses which provide their own public interface. All methods on
 * CayenneTemplate have protected visibility so they are not publicly visible on
 * the custom subclasses.
 *
 * @author Malcolm Edgar
 * @author Andrei Adamchik
 */
public class CayenneTemplate {

    // ------------------------------------------------------ Protected Methods

    protected DataContext getDataContext() {
        return DataContext.getThreadDataContext();
    }

    protected void commitChanges() {
        getDataContext().commitChanges();
    }

    protected void rollbackChanges() {
        getDataContext().rollbackChanges();
    }

    protected void registerNewObject(DataObject dataObject) {
        getDataContext().registerNewObject(dataObject);
    }

    protected DataObject createAndRegisterNewObject(Class dataObjectClass) {
        return getDataContext().createAndRegisterNewObject(dataObjectClass);
    }

    protected void deleteObject(DataObject dataObject) {
        getDataContext().deleteObject(dataObject);
    }

    protected Object objectForPK(Class dataObjectClass, Object pk) {
        return DataObjectUtils.objectForPK(getDataContext(),
                                           dataObjectClass,
                                           pk);
    }

    protected DataObject findObject(Class dataObjectClass, String property,
            Object value) {

        if (dataObjectClass == null) {
            String msg = "Null dataObjectClass parameter";
            throw new IllegalArgumentException(msg);
        }

        if (property == null) {
            throw new IllegalArgumentException("Null property parameter");
        }

        if (property == null) {
            throw new IllegalArgumentException("Null value parameter");
        }

        Expression qual = ExpressionFactory.matchExp(property, value);
        List list = performQuery(new SelectQuery(dataObjectClass, qual));

        if (list.size() == 1) {
            return (DataObject) list.get(0);

        } else if (list.size() > 1) {
            String msg = "SelectQuery for " + dataObjectClass.getName()
                    + " where " + property + " equals " + value + " returned "
                    + list.size() + " rows";
            throw new RuntimeException(msg);

        } else {
            return null;
        }
    }

    protected int[] performNonSelectingQuery(Query query) {
        return getDataContext().performNonSelectingQuery(query);
    }

    protected int[] performNonSelectingQuery(String queryName) {
        return getDataContext().performNonSelectingQuery(queryName);
    }

    protected int[] performNonSelectingQuery(String queryName, Map parameters) {
        return getDataContext().performNonSelectingQuery(queryName, parameters);
    }

    protected List performQuery(Query query) {
        return getDataContext().performQuery(query);
    }

    protected List performQuery(String queryName, boolean refresh) {
        return getDataContext().performQuery(queryName, refresh);
    }

    protected List performQuery(String queryName, Map parameters,
            boolean refresh) {

        return getDataContext().performQuery(queryName, parameters, refresh);
    }

    protected List performQuery(String queryName, String[] keys,
            String[] values, boolean refresh) {

        return performQuery(queryName, toMap(keys, values), refresh);
    }

    protected List performQuery(Class dataObjectClass, String property,
            Object value) {

        if (dataObjectClass == null) {
            String msg = "Null dataObjectClass parameter";
            throw new IllegalArgumentException(msg);
        }

        if (property == null) {
            throw new IllegalArgumentException("Null property parameter");
        }

        if (property == null) {
            throw new IllegalArgumentException("Null value parameter");
        }

        Expression qual = ExpressionFactory.matchExp(property, value);
        return performQuery(new SelectQuery(dataObjectClass, qual));
    }

    protected int[] performNonSelectingQuery(String queryName, String[] keys,
            String[] values) {

        return performNonSelectingQuery(queryName, toMap(keys, values));
    }

    protected Map toMap(String key, Object value) {
        return Collections.singletonMap(key, value);
    }

    protected Map toMap(String[] keys, Object[] values) {

        if (keys == null || keys.length == 0) {
            return Collections.EMPTY_MAP;
        }

        int len = keys.length;
        if (len == 1) {
            return Collections.singletonMap(keys[0], values[0]);
        }

        Map map = new HashMap();
        for (int i = 0; i < len; i++) {
            map.put(keys[i], values[i]);
        }

        return map;
    }
}
