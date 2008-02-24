package org.springframework.orm.cayenne;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectstyle.cayenne.CayenneException;
import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.DataObjectUtils;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.access.DeleteDenyException;
import org.objectstyle.cayenne.access.OperationObserver;
import org.objectstyle.cayenne.access.ResultIterator;
import org.objectstyle.cayenne.query.GenericSelectQuery;
import org.objectstyle.cayenne.query.Query;
import org.springframework.dao.DataAccessException;

/**
 * A template for Cayenne access from DAO. Wraps Cayenne and SQLExceptions in
 * Spring DAO exception. Uses thread-bound DataContext for all operations.
 * 
 * @author Andrei Adamchik
 */
public class CayenneTemplate extends CayenneAccessor implements
        CayenneOperations {

    /**
     * Property that defines whether DataContext should be rolled back on
     * operation errors.
     */
    // this is kind of an extenstion of transaction handling... 
    // does it make sense to configure it here?
    protected boolean autoRollback;

    public boolean isAutoRollback() {
        return autoRollback;
    }

    public void setAutoRollback(boolean autoRollback) {
        this.autoRollback = autoRollback;
    }

    public void commitChanges() throws DataAccessException {
        execute(new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException {
                context.commitChanges();
                return null;
            }
        });
    }

    public Object objectForPK(final Class dataObjectClass, final Object pk)
            throws DataAccessException {
        return execute(new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException {
                return DataObjectUtils
                        .objectForPK(context, dataObjectClass, pk);
            }
        });
    }

    public void rollbackChanges() throws DataAccessException {
        // no Cayenne exceptions are thrown here...
        threadDataContext().rollbackChanges();
    }

    public ResultIterator performIteratedQuery(final GenericSelectQuery query)
            throws DataAccessException {
        return (ResultIterator) execute(new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException {
                return context.performIteratedQuery(query);
            }
        });
    }

    public int[] performNonSelectingQuery(final Query query)
            throws DataAccessException {
        return (int[]) execute(new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException {
                return context.performNonSelectingQuery(query);
            }
        });
    }

    public int[] performNonSelectingQuery(final String queryName)
            throws DataAccessException {
        return (int[]) execute(new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException {
                return context.performNonSelectingQuery(queryName);
            }
        });
    }

    public int[] performNonSelectingQuery(final String queryName,
                                          final Map parameters)
            throws DataAccessException {
        return (int[]) execute(new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException {
                return context.performNonSelectingQuery(queryName, parameters);
            }
        });
    }

    public void performQueries(final Collection queries,
                               final OperationObserver observer)
            throws DataAccessException {
        execute(new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException {
                context.performQueries(queries, observer);
                return null;
            }
        });
    }

    public List performQuery(final GenericSelectQuery query)
            throws DataAccessException {
        return (List) execute(new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException {
                return context.performQuery(query);
            }
        });
    }

    public List performQuery(final String queryName, final boolean refresh)
            throws DataAccessException {
        return (List) execute(new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException {
                return context.performQuery(queryName, refresh);
            }
        });
    }

    public List performQuery(final String queryName,
                             final Map parameters,
                             final boolean refresh) throws DataAccessException {
        return (List) execute(new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException {
                return context.performQuery(queryName, parameters, refresh);
            }
        });
    }

    public int[] performNonSelectingQuery(String queryName,
                                          String[] keys,
                                          String[] values)
            throws DataAccessException {
        return performNonSelectingQuery(queryName, toMap(keys, values));
    }

    public List performQuery(String queryName,
                             String[] keys,
                             String[] values,
                             boolean refresh) throws DataAccessException {
        return performQuery(queryName, toMap(keys, values), refresh);
    }

    /**
     * Main worker method that wraps CayenneCalbac execution in Spring exception
     * handler.
     */
    public Object execute(CayenneCallback callback) throws DataAccessException {
        DataContext context = threadDataContext();
        try {
            return callback.doInCayenne(context);
        } catch (CayenneRuntimeException ex) {
            rollbackDataContext(context, ex);
            throw convertAccessException(ex);
        } catch (CayenneException ex) {
            rollbackDataContext(context, ex);
            throw convertAccessException(ex);
        }
    }

    protected void rollbackDataContext(DataContext context, Throwable th) {
        if (isAutoRollback()) {
            context.rollbackChanges();
        }
    }

    private Map toMap(String[] keys, Object[] values) {
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