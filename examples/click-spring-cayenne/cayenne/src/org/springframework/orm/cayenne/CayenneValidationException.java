package org.springframework.orm.cayenne;

import org.apache.cayenne.access.DeleteDenyException;
import org.apache.cayenne.validation.ValidationException;
import org.apache.cayenne.validation.ValidationResult;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

/**
 * A exception thrown on Cayenne DataObject validation failures during commit.
 * Wraps Cayenne VaidationException or DeleteDenyException.
 */
// TODO: is DeleteDeny really the same beast as ValidationException? 
public class CayenneValidationException extends
        InvalidDataAccessResourceUsageException {

    protected ValidationResult validationResult;

    public CayenneValidationException(DeleteDenyException ex) {
        super(ex.getMessage(), ex);
    }

    public CayenneValidationException(ValidationResult validationResult) {
        super(validationResult != null ? validationResult.toString() : null);
        this.validationResult = validationResult;
    }

    public CayenneValidationException(ValidationException ex) {
        super(ex.getMessage(), ex);
        this.validationResult = (ex != null) ? ex.getValidationResult() : null;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}