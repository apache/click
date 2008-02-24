package org.springframework.orm.cayenne;

import java.util.Map;

import org.objectstyle.cayenne.access.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * A exception wrapping Cayenne optimistic locking failure.
 */
public class CayenneOptimisticLockingFailureException extends
        OptimisticLockingFailureException {

    protected String querySQL;
    protected Map qualifierSnapshot;

    public CayenneOptimisticLockingFailureException(OptimisticLockException ex) {
        super(ex.getUnlabeledMessage(), ex);

        this.querySQL = ex.getQuerySQL();
        this.qualifierSnapshot = ex.getQualifierSnapshot();
    }

    public Map getQualifierSnapshot() {
        return qualifierSnapshot;
    }

    public String getQuerySQL() {
        return querySQL;
    }
}