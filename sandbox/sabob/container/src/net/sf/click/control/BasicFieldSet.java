/*
 * Copyright 2004-2008 Malcolm A. Edgar
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.click.Control;
import net.sf.click.MockContext;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.ContainerUtils;
import net.sf.click.util.HtmlStringBuffer;

public class BasicFieldSet extends ContainerField {

    // -------------------------------------------------------------- Constants
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------- Instance Variables
    /** The FieldSet legend. */
    protected String legend;

    /** The FieldSet legend attributes map. */
    protected Map legendAttributes;

    // ------------------------------------------------------ Constructorrs
    public BasicFieldSet() {
    }

    public BasicFieldSet(String name) {
        super(name);
    }

    /**
     * Create a FieldSet with the given name and legend.
     *
     * @param name the fieldset name
     * @param legend the fieldset legend element value
     */
    public BasicFieldSet(String name, String legend) {
        super(name);
        setLegend(legend);
    }

    // ------------------------------------------------------ Public methods
    public String getTag() {
        return "fieldset";
    }

    /**
     * Return the fieldset Legend element value: &lt;legend&gt;
     * <p/>
     * If the legend value is null, this method will attempt to find a
     * localized label message in the parent messages using the key:
     * <blockquote>
     * <tt>getName() + ".title"</tt>
     * </blockquote>
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key.
     * If a value cannot be found in the parent or control messages then the
     * FieldSet name will be converted into a legend using the
     * {@link ClickUtils#toLabel(String)} method.
     *
     * @return the fieldset Legend element value
     */
    public String getLegend() {
        if (legend == null) {
            legend = getMessage(getName() + ".legend");
        }
        if (legend == null) {
            legend = ClickUtils.toLabel(getName());
        }
        return legend;
    }

    /**
     * Set the fieldset Legend element value: &lt;legend&gt;. If the legend
     * value is a zero length string no legend element will be rendered. You
     * can set a blank zero length string if you want to render the fieldset
     * border but don't want a legend caption.
     *
     * @param legend the fieldset Legend element value
     */
    public void setLegend(String legend) {
        this.legend = legend;
    }

    /**
     * Return the legend HTML attribute with the given name, or null if the
     * attribute does not exist.
     *
     * @param name the name of legend HTML attribute
     * @return the legend HTML attribute
     */
    public String getLegendAttribute(String name) {
        if (legendAttributes != null) {
            return (String) legendAttributes.get(name);
        } else {
            return null;
        }
    }

    /**
     * Set the fieldset HTML attribute with the given attribute name and value.
     *
     * @param name the name of the form HTML attribute
     * @param value the value of the form HTML attribute
     * @throws IllegalArgumentException if name parameter is null
     */
    public void setLegendAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (legendAttributes == null) {
            legendAttributes = new HashMap(5);
        }

        if (value != null) {
            legendAttributes.put(name, value);
        } else {
            legendAttributes.remove(name);
        }
    }

    /**
     * Return the fieldset attributes Map.
     *
     * @return the fieldset attributes Map
     */
    public Map getLegendAttributes() {
        if (legendAttributes == null) {
            legendAttributes = new HashMap(5);
        }
        return legendAttributes;
    }

    /**
     * Return true if the fieldset has attributes or false otherwise.
     *
     * @return true if the fieldset has attributes on false otherwise
     */
    public boolean hasLegendAttributes() {
        if (legendAttributes != null && !legendAttributes.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Process the request invoking <tt>onProcess()</tt> on the contained
     * <tt>Control</tt> elements.
     *
     * @return true if all Controls were processed, or false if one Control returned
     * false
     */
    public boolean onProcess() {
        if (hasControls()) {
            for (Iterator it = getControls().iterator(); it.hasNext();) {
                Control control = (Control) it.next();
                String controlName = control.getName();
                if (!controlName.startsWith(Form.SUBMIT_CHECK)) {
                    boolean continueProcessing = control.onProcess();
                    if (!continueProcessing) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //-------------------------------------------- protected methods

    protected void renderContent(HtmlStringBuffer buffer) {
        if (getLegend().length() > 0) {
            buffer.elementStart("legend");
            if (hasLegendAttributes()) {
                if (getLegendAttributes().containsKey("id")) {
                   buffer.appendAttribute("id", getId());
                }
                buffer.appendAttributes(getLegendAttributes());
            }
            buffer.closeTag();
            buffer.append(getLegend());
            buffer.elementEnd("legend");
            buffer.append("\n");
        }
        container.renderContent(buffer);
    }

    // -------------------------------------------------------- Private methods

    public static void main(String[] args) {
        MockContext.initContext();
        Form form = new Form("form");
        FieldSet fs = new FieldSet("name", "legend");
        form.add(fs);
        fs.setShowBorder(true);
        fs.addControl(new TextField("text1"));
        fs.addControl(new TextField("text2"));
        fs.getField("text1").setError("error");
        // FieldSet should be valid
        System.out.println("Is Valid: " + fs.isValid());
        // Form should be invalid.
        System.out.println("Is Valid: " + form.isValid());
    }
}
