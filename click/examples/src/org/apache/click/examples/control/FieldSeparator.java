package org.apache.click.examples.control;

import org.apache.click.Context;
import org.apache.click.control.FieldSet;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.util.ClickUtils;

import java.util.List;

/**
 * FieldSeparator is a component derived from a FieldSet that acts as a visual separator
 * having a label/legend too. This component is practical in forms where a FieldSet would
 * not align correctly the fields due to variable label width between the groups.<p/>
 *
 * @see FieldSeparatorDemo
 *
 */
public class FieldSeparator extends FieldSet {
    public FieldSeparator(String name) {
        super(name);
        addStyleClass("fieldSeparator");        
    }

    public FieldSeparator(String name, String legend) {
        super(name, legend);
        addStyleClass("fieldSeparator");
    }

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
