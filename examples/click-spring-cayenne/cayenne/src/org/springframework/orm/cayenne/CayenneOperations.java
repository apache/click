package org.springframework.orm.cayenne;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.objectstyle.cayenne.access.OperationObserver;
import org.objectstyle.cayenne.access.ResultIterator;
import org.objectstyle.cayenne.query.GenericSelectQuery;
import org.objectstyle.cayenne.query.Query;
import org.springframework.dao.DataAccessException;

/**
 * A facade to Cayenne DataContext, adapting it for use with Spring DAO
 * exceptions, etc.
 */
public interface CayenneOperations {

    public void commitChanges() throws DataAccessException;

    public void rollbackChanges() throws DataAccessException;

    public Object objectForPK(Class dataObjectClass, Object pk)
            throws DataAccessException;

    /**
     * Runs an arbitrary DB operation defined as CayenneCallback.
     */
    public Object execute(CayenneCallback callback) throws DataAccessException;

    /**
     * The most generic (and also the least user-friendly) method to run a
     * number of queries, using the provided observer.
     */
    public void performQueries(Collection queries, OperationObserver observer)
            throws DataAccessException;

    public List performQuery(GenericSelectQuery query)
            throws DataAccessException;

    public List performQuery(String queryName, boolean refresh)
            throws DataAccessException;

    public List performQuery(String queryName, Map parameters, boolean refresh)
            throws DataAccessException;

    public List performQuery(String queryName,
                             String[] keys,
                             String[] values,
                             boolean refresh) throws DataAccessException;

    /**
     * Runs a single database select query returning result as a ResultIterator.
     * Returned ResultIterator will provide access to DataRows.
     */
    public ResultIterator performIteratedQuery(GenericSelectQuery query)
            throws DataAccessException;

    /**
     * Runs an updating query, returning an array of counts.
     */
    public int[] performNonSelectingQuery(Query query)
            throws DataAccessException;

    /**
     * Runs an updating query stored in Cayenne mapping and identified by name,
     * returning an array of counts.
     */
    public int[] performNonSelectingQuery(String queryName)
            throws DataAccessException;

    /**
     * Runs an updating query stored in Cayenne mapping and identified by name,
     * returning an array of counts.
     */
    public int[] performNonSelectingQuery(String queryName, Map parameters)
            throws DataAccessException;

    public int[] performNonSelectingQuery(String queryName,
                                          String[] keys,
                                          String[] values)
            throws DataAccessException;
}