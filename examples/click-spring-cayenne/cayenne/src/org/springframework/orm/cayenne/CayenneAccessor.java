package org.springframework.orm.cayenne;

import java.sql.SQLException;

import org.objectstyle.cayenne.CayenneException;
import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.access.DeleteDenyException;
import org.objectstyle.cayenne.access.OptimisticLockException;
import org.objectstyle.cayenne.exp.ExpressionException;
import org.objectstyle.cayenne.util.Util;
import org.objectstyle.cayenne.validation.ValidationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

/**
 * A superclass for templates or interceptors using Cayenne. Provides Spring
 * exception translation facility.
 * 
 * @author Andrei Adamchik
 */
public abstract class CayenneAccessor implements InitializingBean {
    protected SQLExceptionTranslator jdbcExceptionTranslator;

    /**
     * Returns thread-bound DataContext. If subclass obtains DataContext using
     * some other mechanism, this is a method to override.
     */
    protected DataContext threadDataContext() {
        return DataContext.getThreadDataContext();
    }

    /**
     * Eagerly initializes the exception translator, creating a default one if
     * not set.
     */
    public void afterPropertiesSet() {
        getJdbcExceptionTranslator();
    }

    /**
     * Returns the JDBC exception translator for this instance. If not
     * initialized, creates default portable SQLStateSQLExceptionTranslator, as
     * Cayenne configuration can span multiple DataSources,
     */
    // TODO: if we can propagate DataNode that caused the exception, we can get
    // a DataSource for exception and use SQLErrorCodeSQLExceptionTranslator.
    public SQLExceptionTranslator getJdbcExceptionTranslator() {
        if (this.jdbcExceptionTranslator == null) {
            this.jdbcExceptionTranslator = new SQLStateSQLExceptionTranslator();
        }
        return this.jdbcExceptionTranslator;
    }

    /**
     * Sets the JDBC exception translator for this instance. Applied to
     * SQLExceptions thrown by callback code. The default exception translator
     * is either a SQLStateSQLExceptionTranslator.
     */
    public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
        this.jdbcExceptionTranslator = jdbcExceptionTranslator;
    }

    /**
     * Converts SQLException to an appropriate exception from the
     * org.springframework.dao hierarchy.
     */
    public DataAccessException convertJdbcAccessException(SQLException ex) {
        return getJdbcExceptionTranslator().translate("Cayenne operation",
                null, ex);
    }

    /**
     * Converts a Cayenne exception (usualy this is CayenneRuntimeException) to
     * an appropriate exception from the org.springframework.dao hierarchy. Will
     * automatically detect wrapped SQLExceptions and convert them accordingly.
     */
    public DataAccessException convertAccessException(Exception ex) {
        // before unwind, handle ExpressionExceptions
        if (ex instanceof ExpressionException) {
            return new CayenneExpressionException((ExpressionException) ex);
        }

        Throwable th = Util.unwindException(ex);

        // handle SQL Exceptions
        if (th instanceof SQLException) {
            SQLException sqlException = (SQLException) th;
            return convertJdbcAccessException(sqlException);
        }

        if (th instanceof ValidationException) {
            return new CayenneValidationException((ValidationException) th);
        }

        if (th instanceof DeleteDenyException) {
            return new CayenneValidationException((DeleteDenyException) th);
        }

        if (th instanceof OptimisticLockException) {
            return new CayenneOptimisticLockingFailureException(
                    (OptimisticLockException) th);
        }

        if (th instanceof CayenneRuntimeException) {
            return new CayenneOperationException(((CayenneRuntimeException) th)
                    .getUnlabeledMessage(), th);
        }

        if (th instanceof CayenneException) {
            return new CayenneOperationException(((CayenneException) th)
                    .getUnlabeledMessage(), th);
        }

        // handle exceptions originated in Cayenne
        return new CayenneOperationException(th.getMessage(), th);
    }
}