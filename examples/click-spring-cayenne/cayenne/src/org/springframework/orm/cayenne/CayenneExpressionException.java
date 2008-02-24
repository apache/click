package org.springframework.orm.cayenne;

import org.objectstyle.cayenne.exp.ExpressionException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

/**
 * Exception wrapping Cayenne ExpressionException.
 */
public class CayenneExpressionException extends
        InvalidDataAccessApiUsageException {

    protected String expressionString;

    public CayenneExpressionException(ExpressionException ex) {
        super(ex.getUnlabeledMessage(), ex);
        this.expressionString = ex.getExpressionString();
    }

    public String getExpressionString() {
        return expressionString;
    }
}