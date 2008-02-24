package org.springframework.orm.cayenne;

import java.sql.SQLException;

import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.access.OptimisticLockException;
import org.objectstyle.cayenne.exp.ExpressionException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import junit.framework.TestCase;

public class CayenneAccessorTests extends TestCase {
    public void testJdbcExceptionTranslator() {
        CayenneAccessor accessor = new CayenneAccessor() {
        };

        assertNotNull("default translator is not loaded", accessor
                .getJdbcExceptionTranslator());

        SQLExceptionTranslator translator = new SQLExceptionTranslator() {
            public DataAccessException translate(String task,
                                                 String sql,
                                                 SQLException sqlex) {
                return null;
            }
        };

        accessor.setJdbcExceptionTranslator(translator);
        assertSame(translator, accessor.getJdbcExceptionTranslator());
    }

    public void testConvertExpressionException() {
        Exception ex = new ExpressionException("test expression exception");
        CayenneAccessor accessor = new CayenneAccessor() {
        };

        DataAccessException converted = accessor.convertAccessException(ex);
        assertTrue(converted instanceof CayenneExpressionException);
    }

    public void testConvertOptimistickLockException() {
        Exception ex = new OptimisticLockException(null,
                "test optimistic lock exception", null);
        CayenneAccessor accessor = new CayenneAccessor() {
        };

        DataAccessException converted = accessor.convertAccessException(ex);
        assertTrue(converted instanceof CayenneOptimisticLockingFailureException);
    }

    public void testConvertNestedException() {
        Exception ex = new Exception("test nested exception");
        CayenneAccessor accessor = new CayenneAccessor() {
        };

        DataAccessException converted = accessor
                .convertAccessException(new CayenneRuntimeException(ex));
        assertTrue(converted instanceof CayenneOperationException);
        assertSame(ex, converted.getCause());
    }

    public void testConvertJdbcException() {
        SQLException ex = new SQLException("test SQL exception");
        CayenneAccessor accessor = new CayenneAccessor() {
        };

        DataAccessException converted = accessor.convertJdbcAccessException(ex);
        assertNotNull(converted);
        assertSame(ex, converted.getCause());
    }

    public void testConvertNestedJdbcException() {
        SQLException ex = new SQLException("test nested SQL exception");
        CayenneAccessor accessor = new CayenneAccessor() {
        };

        DataAccessException converted = accessor
                .convertAccessException(new CayenneRuntimeException(ex));
        assertNotNull(converted);
        assertSame(ex, converted.getCause());
    }
}