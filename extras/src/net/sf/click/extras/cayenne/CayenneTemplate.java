/*
 * Copyright 2006-2007 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.extras.cayenne;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.DeleteDenyException;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.SelectQuery;

/**
 * Provides base Cayenne data access object or service class to extend, following
 * the Spring DAO template pattern. This class uses thread bound
 * <tt>DataContext</tt> for all data access operations.
 * <p/>
 * This class is designed to be extended by custom DAO or Service subclasses
 * which provide their own public interface. All methods on CayenneTemplate have
 * protected visibility so they are not publicly visible on the custom subclasses.
 * <p/>
 * CayenneTemplate provides many convience DataContext methods using the
 * DataContext object bound to the current thread.
 *
 * @author Malcolm Edgar
 * @author Andrei Adamchik
 */
public class CayenneTemplate {

    // ------------------------------------------------------ Protected Methods

    /**
     * Instantiates new object and registers it with itself. Object class must
     * have a default constructor.
     *
     * @param dataObjectClass the data object class to create and register
     * @return the new registered data object
     */
    protected DataObject createAndRegisterNewObject(Class dataObjectClass) {
        return getDataContext().createAndRegisterNewObject(dataObjectClass);
    }

    /**
     * Commit any changes in the thread local DataContext.
     */
    protected void commitChanges() {
        getDataContext().commitChanges();
    }

    /**
     * Schedules an object for deletion on the next commit of this DataContext. Object's
     * persistence state is changed to PersistenceState.DELETED; objects related to this
     * object are processed according to delete rules, i.e. relationships can be unset
     * ("nullify" rule), deletion operation is cascaded (cascade rule).
     *
     * @param dataObject a persistent data object that we want to delete
     * @throws org.apache.cayenne.DeleteDenyException if a DENY delete rule
     * is applicable for object deletion
     */
    protected void deleteObject(DataObject dataObject) throws DeleteDenyException {
        Validate.notNull(dataObject, "Null dataObject parameter");

        getDataContext().deleteObject(dataObject);
    }

    /**
     * Find the data object for the specified class, property name and property
     * value, or null if no data object was found.
     *
     * @param dataObjectClass the data object class to find
     * @param property the name of the property
     * @param value the value of the property
     * @return the data object for the specified class, property name and property value
     * @throws RuntimeException if more than one data object was identified for the
     * given property name and value
     */
    protected DataObject findObject(Class dataObjectClass, String property,
            Object value) {

        List list = performQuery(dataObjectClass, property, value);

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

    /**
     * Return the thread local DataContext.
     *
     * @return the thread local DataContext
     * @throws IllegalStateException if there is no DataContext bound to the current thread
     */
    protected DataContext getDataContext() {
        return DataContext.getThreadDataContext();
    }

    /**
     * Perform a database query returning the data object specified by the
     * class and the primary key. This method will perform a database query
     * and refresh the object cache.
     *
     * @param doClass the data object class to retrieve
     * @param id the data object primary key
     * @return the data object for the given class and id
     */
    protected DataObject getObjectForPK(Class doClass, Object id) {
        return getObjectForPK(doClass, id, true);
    }

    /**
     * Perform a query returning the data object specified by the
     * class and the primary key value. If the refresh parameter is true a
     * database query will be performed, otherwise the a query against the
     * object cache will be performed first.
     *
     * @param dataObjectClass the data object class to retrieve
     * @param id the data object primary key
     * @param refresh the refresh the object cache mode
     * @return the data object for the given class and id
     */
    protected DataObject getObjectForPK(Class dataObjectClass, Object id, boolean refresh) {
        Validate.notNull(dataObjectClass, "Null dataObjectClass parameter.");

        ObjEntity objEntity =
            getDataContext().getEntityResolver().lookupObjEntity(dataObjectClass);

        if (objEntity == null) {
            throw new CayenneRuntimeException("Unmapped DataObject Class: "
                    + dataObjectClass.getName());
        }

        String pkName = getPkName(dataObjectClass);

        ObjectId objectId = new ObjectId(objEntity.getName(), pkName, id);

        int refreshMode = (refresh) ? ObjectIdQuery.CACHE_REFRESH : ObjectIdQuery.CACHE;

        ObjectIdQuery objectIdQuery = new ObjectIdQuery(objectId, false, refreshMode);

        return (DataObject) DataObjectUtils.objectForQuery(getDataContext(), objectIdQuery);
    }

    /**
     * Return the database primary key column name for the given data object.
     *
     * @param dataObjectClass the class of the data object
     * @return the primary key column name
     */
    protected String getPkName(Class dataObjectClass) {
        Validate.notNull(dataObjectClass, "Null dataObjectClass parameter.");

        ObjEntity objEntity =
            getDataContext().getEntityResolver().lookupObjEntity(dataObjectClass);

        if (objEntity == null) {
            throw new CayenneRuntimeException("Unmapped DataObject Class: "
                    + dataObjectClass.getName());
        }

        DbEntity dbEntity = objEntity.getDbEntity();
        if (dbEntity == null) {
            throw new CayenneRuntimeException("No DbEntity for ObjEntity: "
                    + objEntity.getName());
        }

        List pkAttributes = dbEntity.getPrimaryKey();
        if (pkAttributes.size() != 1) {
            throw new CayenneRuntimeException("PK contains "
                    + pkAttributes.size()
                    + " columns, expected 1.");
        }

        DbAttribute attr = (DbAttribute) pkAttributes.get(0);

        return attr.getName();
    }

    /**
     * Performs a single selecting query. Various query setting control the behavior of
     * this method and the results returned:
     * <ul>
     * <li>Query caching policy defines whether the results are retrieved from cache or
     * fetched from the database. Note that queries that use caching must have a name that
     * is used as a caching key.
     * </li>
     * <li>Query refreshing policy controls whether to refresh existing data objects and
     * ignore any cached values.
     * </li>
     * <li>Query data rows policy defines whether the result should be returned as
     * DataObjects or DataRows.
     * </li>
     * </ul>
     *
     * @param query the query to perform
     * @return a list of DataObjects or a DataRows for the query
     */
    protected List performQuery(Query query) {
        return getDataContext().performQuery(query);
    }

    /**
     * Returns a list of objects or DataRows for a named query stored in one of the
     * DataMaps. Internally Cayenne uses a caching policy defined in the named query. If
     * refresh flag is true, a refresh is forced no matter what the caching policy is.
     *
     * @param queryName a name of a GenericSelectQuery defined in one of the DataMaps. If
     *  no such query is defined, this method will throw a CayenneRuntimeException
     * @param refresh A flag that determines whether refresh of <b>cached lists</b>
     *  is required in case a query uses caching.
     * @return the list of data object or DataRows for the named query
     */
    protected List performQuery(String queryName, boolean refresh) {
        return getDataContext().performQuery(queryName, refresh);
    }

    /**
     * Returns a list of objects or DataRows for a named query stored in one of the
     * DataMaps. Internally Cayenne uses a caching policy defined in the named query. If
     * refresh flag is true, a refresh is forced no matter what the caching policy is.
     *
     * @param queryName a name of a GenericSelectQuery defined in one of the DataMaps. If
     *  no such query is defined, this method will throw a CayenneRuntimeException
     * @param parameters A map of parameters to use with stored query
     * @param refresh A flag that determines whether refresh of <b>cached lists</b>
     *  is required in case a query uses caching.
     * @return the list of data object or DataRows for the named query
     */
    protected List performQuery(String queryName, Map parameters, boolean refresh) {

        return getDataContext().performQuery(queryName, parameters, refresh);
    }

    /**
     * Return a list of data object of the specified class for the given property
     * and value.
     *
     * @param dataObjectClass the data object class to return
     * @param property the name of the property to select
     * @param value the property value to select
     * @return a list of data objects for the given class and property name and value
     */
    protected List performQuery(Class dataObjectClass, String property,
            Object value) {

        Validate.notNull(dataObjectClass, "Null dataObjectClass parameter");
        Validate.notNull(property, "Null property parameter");
        Validate.notNull(value, "Null value parameter");

        Expression qual = ExpressionFactory.matchExp(property, value);
        return performQuery(new SelectQuery(dataObjectClass, qual));
    }

    /**
     * Performs a single database query that does not select rows. Returns an
     * array of update counts.
     *
     * @param query the query to perform
     * @return the array of update counts
     */
    protected int[] performNonSelectingQuery(Query query) {
        return getDataContext().performNonSelectingQuery(query);
    }

    /**
     * Performs a named mapped query that does not select rows. Returns an array
     * of update counts.
     *
     * @param queryName the name of the query to perform
     * @return the array of update counts
     */
    protected int[] performNonSelectingQuery(String queryName) {
        return getDataContext().performNonSelectingQuery(queryName);
    }

    /**
     * Performs a named mapped non-selecting query using a map of parameters.
     * Returns an array of update counts.
     *
     * @param queryName the name of the query to perform
     * @param parameters the Map of query paramater names and values
     * @return the array of update counts
     */
    protected int[] performNonSelectingQuery(String queryName, Map parameters) {
        return getDataContext().performNonSelectingQuery(queryName, parameters);
    }

    /**
     * Registers a transient object with the context, recursively registering all
     * transient DataObjects attached to this object via relationships.
     *
     * @param dataObject new object that needs to be made persistent
     */
    protected void registerNewObject(DataObject dataObject) {
        getDataContext().registerNewObject(dataObject);
    }

    /**
     * Reverts any changes that have occurred to objects registered in the
     * thread local DataContext.
     */
    protected void rollbackChanges() {
        getDataContext().rollbackChanges();
    }

    /**
     * Return a Map containing the given key name and value.
     *
     * @param key the map key name
     * @param value the map key value
     * @return a Map containing the given key name and value
     */
    protected Map toMap(String key, Object value) {
        return Collections.singletonMap(key, value);
    }

}
