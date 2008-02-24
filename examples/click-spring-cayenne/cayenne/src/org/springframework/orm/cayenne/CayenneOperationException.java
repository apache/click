package org.springframework.orm.cayenne;

import org.springframework.dao.UncategorizedDataAccessException;

/**
 * Generic uncategorized Cayenne exception.
 */
public class CayenneOperationException extends UncategorizedDataAccessException {

    public CayenneOperationException(String message, Throwable ex) {
        super(message, ex);
    }
}