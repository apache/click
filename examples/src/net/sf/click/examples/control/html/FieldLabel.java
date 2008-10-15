package net.sf.click.examples.control.html;

import net.sf.click.control.AbstractControl;
import net.sf.click.control.Field;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * This control associates an HTML <em>label</em> with a target field.
 *
 * @author Bob Schellink
 */
public class FieldLabel extends AbstractControl {

    private Field target;

    private String label;

    public FieldLabel(Field target) {
        this(target, ClickUtils.toLabel(target.getName()), null);
    }

    public FieldLabel(Field target, String label) {
        this(target, label, null);
    }

    public FieldLabel(Field target, String label, String accessKey) {
        this.target = target;
        this.label = label + ":";
        if (accessKey != null) {
            setAttribute("accesskey", accessKey);
        }
    }

    public String getTag() {
        return "label";
    }

    // Override render to produce a html label for example:
    // <label for="firstname">Firstname:</label>
    public void render(HtmlStringBuffer buffer) {
        // Open tag: <label
        buffer.elementStart(getTag());

        // Set attribute to target field's id
        setAttribute("for", target.getId());

        // Render all the labels attributes
        appendAttributes(buffer);

        // Close tag: <label for="firstname">
        buffer.closeTag();

        // Add label text: <label for="firstname">Firstname:
        buffer.append(label);

        // Close tag: <label for="firstname">Firstname:</label>
        buffer.elementEnd(getTag());
    }

}
