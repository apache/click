/*
 * Copyright 2004-2005 Malcolm A. Edgar
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;

/**
 * Provides a FieldSet control: &nbsp; &lt;fieldset/&gt;.
 *
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.10">FIELDSET</a>
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class FieldSet extends Field {

    private static final long serialVersionUID = -1811752204415596846L;

    // ----------------------------------------------------- Instance Variables

    /** The ordered list of field values, excluding buttons. */
    protected final List fieldList = new ArrayList();

    /** The map of fields keyed by field name. */
    protected final Map fields = new HashMap();

    /** The FieldSet legend. */
    protected String legend;

    /** The FieldSet legend attributes map. */
    protected Map legendAttributes;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a FieldSet with the given legend.
     * <p/>
     * The fieldset name will be Java property representation of the given
     * legend.
     *
     * @param legend the fieldset legend element value
     */
    public FieldSet(String legend) {
        setLegend(legend);
        setName(ClickUtils.toName(legend));
    }

    /**
     * Create a FieldSet with the given name and legend.
     *
     * @param name the fieldset name
     * @param legend the fieldset legend element value
     */
    public FieldSet(String name, String legend) {
        setName(name);
        setLegend(legend);
    }

    /**
     * Create a FieldSet with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public FieldSet() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Add the field to the form, and set the fields form property. The field
     * will be added to the {@link #fields} Map using its name.
     * <p/>
     * Note <tt>FieldSet</tt> does not support adding <tt>Button</tt> or
     * nested <tt>FieldSet</tt> elements.
     *
     * @param field the field to add to the form
     * @throws IllegalArgumentException if the fieldset already contains a field
     * with the same name, or if the field name is not defined
     */
    public void add(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("field parameter cannot be null");
        }
        if (StringUtils.isBlank(field.getName())) {
            throw new IllegalArgumentException("Field name not defined");
        }
        if (getFields().containsKey(field.getName()) && !(field instanceof Label)) {
            throw new IllegalArgumentException
                ("Form already contains field named: " + field.getName());
        }
        if (field instanceof FieldSet) {
            throw new IllegalArgumentException
                ("FieldSet does not support nested fieldsets");
        }
        if (field instanceof Button) {
            throw new IllegalArgumentException
                ("FieldSet does not support Buttons");
        }

        getFieldList().add(field);
        getFields().put(field.getName(), field);
        field.setForm(getForm());

        if (getContext() != null) {
            field.setContext(getContext());
        }
    }

    /**
     * Remove the given field from the fieldset.
     *
     * @param field the field to remove from the fieldset
     */
    public void remove(Field field) {
        if (field != null && getFields().containsKey(field.getName())) {
            field.setForm(null);
            getFields().remove(field.getName());
            getFieldList().remove(field);
        }
    }

    /**
     * Remove the named field from the fieldset.
     *
     * @param name the name of the field to remove from the fieldset
     */
    public void removeField(String name) {
        remove(getField(name));
    }

    /**
     * Remove the list of named fields from the fieldset.
     *
     * @param fieldNames the list of field names to remove from the fieldset
     */
    public void removeFields(List fieldNames) {
        if (fieldNames != null) {
            for (int i = 0; i < fieldNames.size(); i++) {
                removeField(fieldNames.get(i).toString());
            }
        }
    }

    /**
     * @see Control#setContext(Context)
     */
    public void setContext(Context context) {
        super.setContext(context);
        this.context = context;
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            field.setForm(form);
        }
    }

    /**
     * Return the named field if contained in the fieldset, or null if not
     * found.
     *
     * @param name the name of the field
     * @return the named field if contained in the fieldset
     */
    public Field getField(String name) {
        return (Field) fields.get(name);
    }

    /**
     * Return the List of form fields, ordered in addition order to the
     * fieldset.
     *
     * @return the ordered List of fieldset fields
     */
    public List getFieldList() {
        return fieldList;
    }

    /**
     * Return the Map of fieldset fields, keyed on field name.
     *
     * @return the Map of fieldset fields, keyed on field name
     */
    public Map getFields() {
        return fields;
    }

    /**
     * Set the fieldset's the parent <tt>Form</tt>.
     *
     * @param form fieldset's parent <tt>Form</tt>.
     */
    public void setForm(Form form) {
        super.setForm(form);
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            field.setForm(form);
        }
    }

    /**
     * Return the fieldset Legend element value: &lt;legend&gt;
     *
     * @return the fieldset Legend element value
     */
    public String getLegend() {
        return legend;
    }

    /**
     * Set the fieldset Legend element value: &lt;legend&gt;
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
     * This method is not supported and will throw a
     * <tt>UnsupportedOperationException</tt>.
     *
     * @see Control#setListener(Object, String)
     */
    public void setListener(Object listener, String method) {
        throw new UnsupportedOperationException("setListener not supported");
    }

    /**
     * Return true if all contained fields are valid.
     *
     * @see Field#isValid()
     */
    public boolean isValid() {
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            if (!field.isValid()) {
                return false;
            }
        }
        return true;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the request invoking <tt>onProcess()</tt> on the contained
     * <tt>Field</tt> elements.
     *
     * @return true if all Fields were processed, or false if one Field returned
     *  false
     */
    public boolean onProcess() {
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            boolean continueProcessing = field.onProcess();
            if (!continueProcessing) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return the HTML string representation of the fieldset.
     *
     * @see Object#toString()
     */
    public String toString() {
        // Estimate the size of the string buffer
        int bufferSize = 160 + (fieldList.size() * 350);
        StringBuffer buffer = new StringBuffer(bufferSize);

        buffer.append("<fieldset id='");
        buffer.append(getId());
        buffer.append("'");
        if (hasAttributes()) {
            ClickUtils.renderAttributes(getAttributes(), buffer);
        }
        if (isDisabled()) {
            buffer.append(getDisabled());
        }
        buffer.append(">\n");
        if (getLegend() != null) {
            buffer.append("<legend id='");
            buffer.append(getId());
            buffer.append("-legend'");
            if (hasLegendAttributes()) {
                ClickUtils.renderAttributes(getLegendAttributes(), buffer);
            }
            buffer.append(">");
            buffer.append(getLegend());
            buffer.append("</legend>\n");
        }

        renderFields(buffer);

        buffer.append("</fieldset>\n");

        return buffer.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render the non hidden Form Fields to the string buffer.
     *
     * @param buffer the StringBuffer to render to
     * @return the number of hidden Fields
     */
    protected void renderFields(StringBuffer buffer) {
        if (getFieldList().isEmpty()) {
            return;
        }

        buffer.append("<table class='fields' id='");
        buffer.append(getId());
        buffer.append("-fields'>\n");

        int column = 1;

        for (int i = 0, size = fieldList.size(); i < size; i++) {

            Field field = (Field) fieldList.get(i);

            if (field.isHidden()) {
                buffer.append(field.toString());
                buffer.append("\n");

            } else {

                if (column == 1) {
                    buffer.append("<tr class='fields'>\n");
                }

                if (field instanceof Label) {
                    buffer.append("<td class='fields' colspan='2' align='");
                    buffer.append(getForm().getLabelAlign());
                    buffer.append("'");
                    if (field.hasAttributes()) {
                        ClickUtils.renderAttributes
                            (field.getAttributes(), buffer);
                    }
                    buffer.append(">");
                    buffer.append(field);
                    buffer.append("</td>\n");

                } else {
                    // Write out label
                    if (Form.LEFT.equals(getForm().getLabelsPosition())) {
                        buffer.append("<td class='fields' align='");
                        buffer.append(getForm().getLabelAlign());
                        buffer.append("'>");
                    } else {
                        buffer.append("<td class='fields' valign='top'>");
                    }

                    if (field.isRequired()) {
                        buffer.append(Form.labelRequiredPrefix);
                    }
                    buffer.append("<label");
                    buffer.append(field.getDisabled());
                    if (field.getError() != null) {
                        buffer.append(" class='error'");
                    }
                    buffer.append(">");
                    buffer.append(field.getLabel());
                    buffer.append("</label>");
                    if (field.isRequired()){
                        buffer.append(Form.labelRequiredSuffix);
                    }

                    if (Form.LEFT.equals(getForm().getLabelsPosition())) {
                        buffer.append("</td>\n");
                        buffer.append("<td align='left'>");
                    } else {
                        buffer.append("<br>");
                    }

                    // Write out field
                    buffer.append(field);
                    buffer.append("</td>\n");
                }

                if (column == getForm().getColumns()) {
                    buffer.append("</tr>\n");
                    column = 1;
                } else {
                    column++;
                }

            }
        }
        buffer.append("</table>\n");
    }

}
