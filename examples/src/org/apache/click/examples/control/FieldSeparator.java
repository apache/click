package org.apache.click.examples.control;

import org.apache.click.Context;
import org.apache.click.control.FieldSet;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.util.ClickUtils;

import java.util.List;

/**
 * <tt>FieldSeparator</tt> is a component derived from a <tt>FieldSet</tt> that acts as a visual separator that
 * can have a "legend" too. <p/>
 * This component is practical in forms where such a visual representation is required, or
 * where a <tt>FieldSet</tt> would not auto-align correctly all the fields (despite tweaking the CSS), e.g. due
 * to variable label widths between the groups, common in:
 * <ul>
 *   <li>i18n forms - where tweaking the width for one language, would break the width for another</li>
 *   <li>dynamic forms - where there's no a priori knowledge about the label</li>
 * </ul>
 * or simply where the user doesn't want to manually handle this alignment problem, but let Click do
 * the job.
 */
public class FieldSeparator extends FieldSet {

    // Constructors -----------------------------------------------------------

    /**
     * Create a FieldSeparator with the given name. <p/>
     * The legend for this separator will be be inferred from the name.
     *
     * @param name the field separator name element value
     */
    public FieldSeparator(String name) {
        super(name);
        addStyleClass("fieldSeparator");
    }

    /**
     * Create a FieldSeparator with the given name and legend.
     *
     * @param name the field separator name
     * @param legend the field separator legend element value
     */
    public FieldSeparator(String name, String legend) {
        super(name, legend);
        addStyleClass("fieldSeparator");
    }

    /**
     * Create a FieldSeparator with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public FieldSeparator() {
        super();
        addStyleClass("fieldSeparator");
    }

    /**
     * Returns the FieldSeparator HTML HEAD elements for the
     * <tt>click/control.css</tt> resource.
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the list of HEAD elements to be included in the page
     */
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            Context context = getContext();
            String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

            headElements.add(new CssImport("/assets/css/separator.css", versionIndicator));
        }
        return headElements;
    }

}
