package org.springframework.orm.cayenne;

import org.objectstyle.cayenne.CayenneException;
import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.access.DataContext;
import org.springframework.dao.DataAccessException;

import junit.framework.TestCase;

public class CayenneTemplateTests extends TestCase {
    protected boolean executed;

    public void testExecute() {
        final DataContext testContext = new DataContext();
        executed = false;

        CayenneCallback callback = new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException, CayenneRuntimeException {
                assertSame(testContext, context);
                executed = true;
                return null;
            }
        };

        DataContext.bindThreadDataContext(testContext);
        try {
            new CayenneTemplate().execute(callback);
            assertTrue("Template failed to execute callback", executed);
        } finally {
            DataContext.bindThreadDataContext(null);
        }
    }

    public void testExecuteWrapExceptions() {
        final CayenneRuntimeException exception = new CayenneRuntimeException(
                "test CRE");
        executed = false;

        CayenneCallback callback = new CayenneCallback() {
            public Object doInCayenne(DataContext context)
                    throws CayenneException, CayenneRuntimeException {
                throw exception;
            }
        };

        DataContext.bindThreadDataContext(new DataContext());
        try {
            new CayenneTemplate().execute(callback);
            fail("CayenneRuntimeException wasn't rethrown.");
        } catch (DataAccessException dae) {
            assertSame(exception, dae.getCause());
        } finally {
            DataContext.bindThreadDataContext(null);
        }
    }
}