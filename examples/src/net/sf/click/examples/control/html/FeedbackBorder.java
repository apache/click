package net.sf.click.examples.control.html;

import net.sf.click.Control;
import net.sf.click.control.AbstractContainerField;
import net.sf.click.control.Field;

/**
 * This control provides feedback for any a field.
 * <p/>
 * If the field is required, FeedbackBorder will add a "required" css class.
 * <p/>
 * If the field is invalid, FeedbackBorder will display the field error message.
 *
 * @author Bob Schellink
 */
public class FeedbackBorder extends AbstractContainerField {

    private static final long serialVersionUID = 1L;

    public Control insert(Control control, int index) {

        // Enforce rule that only 1 control can be added
        if (getControls().size() > 0) {
            throw new IllegalStateException(
                "Only one control is allowed on FeedbackBorder.");
        }
        if (control == null) {
            throw new IllegalArgumentException("Control cannot be null");
        }

        // Lets assume only fields are allowed
        if (!(control instanceof Field)) {
            throw new IllegalArgumentException(
                "Only fields are allowed on FeedbackBorder.");
        }

        super.insert(control, 0);

        return control;
    }

    public void onRender() {
        Field field = (Field) getControls().get(0);

        // Add required css class
        if (field.isRequired()) {
            field.addStyleClass("required");
            add(new Html("<span class=\"required\">&nbsp;</span>"));
        }

        // If field is invalid, add error message
        if (!field.isValid()) {
            add(new Html("<span class=\"error\">" + field.getError() +
                "</span>"));
        }
    }
}
