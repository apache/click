package org.springframework.orm.cayenne;

import org.apache.cayenne.CayenneException;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.DataContext;

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