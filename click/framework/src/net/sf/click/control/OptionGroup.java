/*
 * Copyright 2005-2006 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a select Option Group element: &nbsp; &lt;optgroup&gt;&lt;/optgroup&gt;.
 * <p/>
 * The OptionGroup class uses an immutable design so Option instances can be
 * shared by multiple Pages in the multi-threaded Servlet environment.
 * This enables OptionGroup instances to be cached as static variables.
 * <p/>
 * For an OptionGroup code example see the {@link Option} Javadoc example.
 * <p/>
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.6">OPTGROUP</a>
 *
 * @see Select
 * @see Option
 *
 * @author Malcolm Edgar
 */
public class OptionGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------- Instance Variables

    /** The groups child Option/OptGroup objects. */
    protected List children = new ArrayList();

    /** The label for the OptionGroup. */
    protected final String label;

    // ------------------------------------------------------- Constructors

    /**
     * Create an OptionGroup with the given display label.
     *
     * @param label the display label for the OptionGroup
     */
    public OptionGroup(String label) {
        this.label = label;
    }

    // -------------------------------------------------- Public Attributes

    /**
     * Add the given Option or OptionGroup object to this group.
     *
     * @param object the Option or OptionGroup to add
     */
    public void add(Object object) {
        getChildren().add(object);
    }

    /**
     * Return the OptionGroup children.
     *
     * @return the OptionGroup children
     */
    public List getChildren() {
        return children;
    }

    /**
     * Return the display label.
     *
     * @return the display label
     */
    public String getLabel() {
        return label;
    }

    // ----------------------------------------------------- Public Methods

    /**
     * Return a HTML rendered OptionGroup string.
     *
     * @param select the parent Select
     * @return a rendered HTML OptionGroup string
     */
    public String renderHTML(Select select) {

        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.elementStart("optgroup");
        buffer.appendAttribute("label", getLabel());
        buffer.closeTag();

        List list = getChildren();
        for (int i = 0, size = list.size(); i < size; i++) {
            Object object = list.get(i);

            if (object instanceof Option) {
                Option option = (Option) object;
                buffer.append(option.renderHTML(select));

            } else if (object instanceof OptionGroup) {
                OptionGroup optionGroup = (OptionGroup) object;
                buffer.append(optionGroup.renderHTML(select));

            } else {
                String msg = "Select option class not instance of Option"
                    + " or OptionGroup: " + object.getClass().getName();
                throw new IllegalArgumentException(msg);
            }
        }

        buffer.elementEnd("optgroup");

        return buffer.toString();
    }
}

