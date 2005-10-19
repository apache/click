package click.cayenne.entity;

import java.util.List;

import org.objectstyle.cayenne.exp.ExpressionFactory;
import org.objectstyle.cayenne.query.SelectQuery;
import org.objectstyle.cayenne.validation.SimpleValidationFailure;
import org.objectstyle.cayenne.validation.ValidationResult;

import click.cayenne.entity.auto._Department;


public class Department extends _Department {

    protected void validateForSave(ValidationResult validationResult) {
        super.validateForSave(validationResult);

        // check for name uniqueness

        if (getName() != null) {
            SelectQuery query = new SelectQuery(Department.class, ExpressionFactory
                    .matchExp(NAME_PROPERTY, getName()));

            List matches = getDataContext().performQuery(query);
            if (matches.size() > 1 || (matches.size() == 1 && !matches.contains(this))) {
                validationResult.addFailure(new SimpleValidationFailure(
                        this,
                        "Can't use name '"
                                + getName()
                                + "', there is another department with the same name."));
            }
        }
    }
}
