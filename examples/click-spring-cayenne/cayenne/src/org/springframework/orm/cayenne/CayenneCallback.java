package org.springframework.orm.cayenne;

import org.objectstyle.cayenne.CayenneException;
import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.access.DataContext;

/**
 * An interface allowing to execute Cayenne operations via CayenneTemplate. This
 * allows CayenneTemplate to wrap normal Cayenne application code, providing
 * exception handling, DataContext injection, and other Spring services.
 * 
 * @author Andrei Adamchik
 */
public interface CayenneCallback {
    public Object doInCayenne(DataContext context) throws CayenneException,
            CayenneRuntimeException;
}