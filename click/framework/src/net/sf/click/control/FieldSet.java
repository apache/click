/*
 * Copyright 2004-2006 Malcolm A. Edgar
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

import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a FieldSet container control: &nbsp; &lt;fieldset&gt;.
 *
 * <table style='margin-bottom: 1.25em'>
 * <tr>
 * <td>
 * <fieldset id='form-paymentDetails'>
 * <legend id='form-paymentDetails-legend'>FieldSet</legend>
 * <table class='fields' id='form-paymentDetails-fields'>
 * <tr class='fields'>
 * <td class='fields' align='left'><label>Card Name</label><font color="red">*</font></td>
 * <td align='left'><input type='text' name='cardName' id='form-cardName' value='' size='20'/></td>
 * </tr>
 * <tr class='fields'>
 * <td class='fields' align='left'><label>Card Number</label><font color="red">*</font></td>
 * <td align='left'><input type='text' name='cardNumber' id='form-cardNumber' value='' size='19' onkeypress='javascript:return integerFilter(event);' maxlength='19'/><select name='cardtype' id='form-cardtype' size='1'><option selected='selected' value='VISA'>Visa</option><option value='MASTER'>Master</option><option value='AMEX'>AmEx</option><option value='DINERS'>Diners</option><option value='DISCOVER'>Discover</option></select></td>
 * </tr>
 * <tr class='fields'>
 * <td class='fields' align='left'><label>Expiry</label><font color="red">*</font></td>
 * <td align='left'><input type='text' name='expiry' id='form-expiry' value='' size='4' onkeypress='javascript:return integerFilter(event);' maxlength='4'/></td>
 * </tr>
 * </table>
 * </fieldset>
 * </td>
 * </tr>
 * </table>
 *
 * FieldSet provides a container for laying out form <tt>Field</tt> controls.
 * The FieldSet will use the properties of its parent Form for laying out and
 * rendering of its Fields. To further customize the rendering of a FieldSet
 * override the {@link #renderFields(HtmlStringBuffer)} method.
 * <p/>
 * A FieldSet can contain any Field controls except for <tt>Button</tt> and
 * <tt>FieldSet</tt> controls.
 *
 * <h3>FieldSet Example</h3>
 *
 * An FieldSet example containing credit card payment details is provided below:
 *
 * <pre class='codeJava'>
 * <span class='kw'>public class</span> PaymentDetails() {
 *
 *     <span class="kw">public</span> Form form = <span class='kw'>new</span> Form();
 *
 *     <span class='kw'>public</span> PaymentDetails() {
 *         FieldSet paymentFieldSet = <span class='kw'>new</span> FieldSet(<span class='st'>"paymentDetails"</span>);
 *         form.add(paymentFieldSet);
 *
 *         paymentFieldSet.add(<span class='kw'>new</span> TextField(<span class='st'>"cardName"</span>, <span class='kw'>true</span>));
 *         paymentFieldSet.add(<span class='kw'>new</span> CreditCardField(<span class='st'>"cardNumber"</span>, <span class='kw'>true</span>));
 *         IntegerField expiryField = <span class='kw'>new</span> IntegerField(<span class='st'>"expiry"</span>, <span class='kw'>true</span>);
 *         expiryField.setSize(4);
 *         expiryField.setMaxLength(4);
 *         paymentFieldSet.add(expiryField);
 *
 *         form.add(<span class='kw'>new</span> Submit(<span class='st'>"ok"</span>, <span class='st'>"    OK    "</span>, <span class='kw'>this</span>, <span class='st'>"onOkClick"</span>);
 *         form.add(<span class='kw'>new</span> Submit(<span class='st'>"  Cancel  "</span>, <span class='kw'>this</span>, <span class='st'>"onCancelClick"</span>));
 *     }
 * } </pre>
 *
 * When the FieldSet is processed it invokes the <tt>onProcess()</tt> method
 * of its contained Fields. Beyond this the FieldSet performs no server side
 * processing, and should be considered simply as a container for laying out
 * form fields.
 *
 * <p/>
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.10">FIELDSET</a>
 *
 * @author Malcolm Edgar
 */
public class FieldSet extends Field {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /**
     * The number of fieldset layout table columns. By default this property
     * inherits its value from the parent form, but can be overridden to specify
     * the fieldsets number of layout columns.
     * <p/>
     * This property is used to layout the number of table columns the fieldset
     * is rendered with using a flow layout style.
     */
    protected Integer columns;

    /** The ordered list of field values, excluding buttons. */
    protected final List fieldList = new ArrayList();

    /** The map of fields keyed by field name. */
    protected final Map fields = new HashMap();

    /** The map of field width values. */
    protected Map fieldWidths = new HashMap();

    /** The FieldSet legend. */
    protected String legend;

    /** The FieldSet legend attributes map. */
    protected Map legendAttributes;

    /** The render the fieldset border flag, default value is true. */
    protected boolean showBorder = true;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a FieldSet with the given name.
     *
     * @param name the fieldset name element value
     */
    public FieldSet(String name) {
        setName(name);
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
     * Create a FieldSet with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
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
            String msg = "field parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        if (StringUtils.isBlank(field.getName())) {
            throw new IllegalArgumentException("Field name not defined");
        }
        if (getFields().containsKey(field.getName())
            && !(field instanceof Label)) {

            String msg =
                "Form already contains field named: " + field.getName();
            throw new IllegalArgumentException(msg);
        }
        if (field instanceof FieldSet) {
            String msg = "FieldSet does not support nested fieldsets";
            throw new IllegalArgumentException(msg);
        }
        if (field instanceof Button) {
            String msg = "FieldSet does not support Buttons";
            throw new IllegalArgumentException(msg);
        }

        getFieldList().add(field);
        getFields().put(field.getName(), field);
        field.setForm(getForm());

        field.setParent(this);

        if (getForm() != null && getForm().getDefaultFieldSize() > 0) {
            if (field instanceof TextField) {
                ((TextField) field).setSize(getForm().getDefaultFieldSize());

            } else if (field instanceof FileField) {
                ((FileField) field).setSize(getForm().getDefaultFieldSize());

            } else if (field instanceof TextArea) {
                ((TextArea) field).setCols(getForm().getDefaultFieldSize());
            }
        }
    }

    /**
     * Add the field to the fieldset and specify the field's width in columns.
     * <p/>
     * Note Button or HiddenFields types are not valid for this method.
     *
     * @param field the field to add to the form
     * @param width the width of the field in table columns
     * @throws IllegalArgumentException if the form already contains a field or
     *  a button is added, or if the field name is not defined
     */
    public void add(Field field, int width) {
        if (field instanceof HiddenField) {
            throw new IllegalArgumentException("not valid a valid field type");
        }
        if (width < 1) {
            throw new IllegalArgumentException("invalid field width: " + width);
        }
        add(field);
        getFieldWidths().put(field.getName(), new Integer(width));
    }

    /**
     * Remove the given field from the fieldset.
     *
     * @param field the field to remove from the fieldset
     */
    public void remove(Field field) {
        if (field != null && getFields().containsKey(field.getName())) {
            field.setForm(null);
            if (field.getParent() == this) {
                field.setParent(null);
            }
            getFields().remove(field.getName());
            getFieldWidths().remove(field.getName());
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
     * Return the number of fieldset layout table columns. This property is used
     * to layout the number of table columns the fieldset is rendered with.
     * <p/>
     * By default this property inherits its value from the parent form, but
     * can be specified to override the form value.
     *
     * @return the number of fieldset layout table columns
     */
    public int getColumns() {
        if (columns != null) {
            return columns.intValue();
        } else {
            return getForm().getColumns();
        }
    }

    /**
     * Set the number of fieldset layout table columns. This property is used to
     * layout the number of table columns the fieldset is rendered with.
     *
     * @param columns the number of fieldset layout table columns
     */
    public void setColumns(int columns) {
        this.columns = new Integer(columns);
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
     * Return the map of field width values, keyed on field name.
     *
     * @return the map of field width values, keyed on field name
     */
    public Map getFieldWidths() {
        return fieldWidths;
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
     * This method is not supported and will throw a
     * <tt>UnsupportedOperationException</tt>.
     *
     * @see net.sf.click.Control#setListener(Object, String)
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
        throw new UnsupportedOperationException("setListener not supported");
    }

    /**
     * Return the render the fieldset border flag. The border is the HTML
     * &lt;fieldset&gt; element.
     *
     * @return the render the fieldset border flag
     */
    public boolean getShowBorder() {
        return showBorder;
    }

    /**
     * Set the render the fieldset border flag. The border is the HTML
     * &lt;fieldset&gt; element.
     *
     * @param value the render the fieldset border flag
     */
    public void setShowBorder(boolean value) {
        this.showBorder = value;
    }

    /**
     * Return true if all contained fields are valid.
     *
     * @see Field#isValid()
     *
     * @return true if all contained fields are valid
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
     * Initialize the fields contained in the fieldset.
     *
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            field.onInit();
        }
    }

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
            if (!field.getName().startsWith(Form.SUBMIT_CHECK)) {
                boolean continueProcessing = field.onProcess();
                if (!continueProcessing) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Perform any pre rendering logic.
     *
     * @see net.sf.click.Control#onRender()
     */
    public void onRender() {
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            field.onRender();
        }
    }

    /**
     * Destroy the fields contained in the fieldset.
     *
     * @see net.sf.click.Control#onDestroy()
     */
    public void onDestroy() {
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            try {
                field.onDestroy();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * Return the HTML string representation of the fieldset.
     *
     * @see Object#toString()
     *
     * @return the HTML string representation of the fieldset
     */
    public String toString() {
        // Estimate the size of the string buffer
        int bufferSize = 160 + (fieldList.size() * 350);
        HtmlStringBuffer buffer = new HtmlStringBuffer(bufferSize);

        if (getShowBorder()) {
            buffer.elementStart("fieldset");

            buffer.appendAttribute("id", getId());

            appendAttributes(buffer);

            if (isDisabled()) {
                buffer.appendAttributeDisabled();
            }

            buffer.closeTag();
            buffer.append("\n");

            if (getLegend().length() > 0) {
                buffer.elementStart("legend");
                buffer.appendAttribute("id", getId() + "-legend");
                if (hasLegendAttributes()) {
                    buffer.appendAttributes(getLegendAttributes());
                }
                buffer.closeTag();
                buffer.append(getLegend());
                buffer.elementEnd("legend");
                buffer.append("\n");
            }
        }

        renderFields(buffer);

        if (getShowBorder()) {
            buffer.elementEnd("fieldset");
            buffer.append("\n");
        }

        return buffer.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render the fieldsets form fields to the string buffer. This method will
     * apply the parent Forms properties to the layout and rendering of fields.
     *
     * @param buffer the StringBuffer to render to
     */
    protected void renderFields(HtmlStringBuffer buffer) {
        if (getFieldList().isEmpty()) {
            return;
        }

        buffer.elementStart("table");
        buffer.appendAttribute("class", "fields");
        buffer.appendAttribute("id", getId() + "-fields");
        buffer.closeTag();
        buffer.append("\n");

        int column = 1;
        boolean openTableRow = false;

        for (int i = 0, size = fieldList.size(); i < size; i++) {

            Field field = (Field) fieldList.get(i);

            if (!field.isHidden()) {

                // Field width
                Integer width = (Integer) getFieldWidths().get(field.getName());

                if (column == 1) {
                    buffer.append("<tr class=\"fields\">\n");
                    openTableRow = true;
                }

                if (field instanceof Label) {
                    buffer.append("<td class=\"fields\" align=\"");
                    buffer.append(getForm().getLabelAlign());
                    buffer.append("\"");

                    if (width != null) {
                        int colspan = (width.intValue() * 2);
                        buffer.appendAttribute("colspan", colspan);
                    } else {
                        buffer.appendAttribute("colspan", 2);
                    }

                    if (field.hasAttributes()) {
                        //Temporarily remove the style attribute
                        String tempStyle = null;
                        if (field.hasAttribute("style")) {
                            tempStyle = field.getAttribute("style");
                            field.setAttribute("style", null);
                        }
                        buffer.appendAttributes(field.getAttributes());

                        //Put style back in attribute map
                        if (tempStyle != null) {
                            field.setAttribute("style", tempStyle);
                        }
                    }
                    buffer.append(">");
                    buffer.append(field);
                    buffer.append("</td>\n");

                } else {
                    // Write out label
                    if (Form.POSITION_LEFT.equals(getForm().getLabelsPosition())) {
                        buffer.append("<td class=\"fields\"");
                        buffer.appendAttribute("align", form.getLabelAlign());
                        buffer.appendAttribute("style", form.getLabelStyle());
                        buffer.append(">");
                    } else {
                        buffer.append("<td class=\"fields\" valign=\"top\"");
                        buffer.appendAttribute("style", form.getLabelStyle());
                        buffer.append(">");
                    }

                    if (field.isRequired()) {
                        buffer.append(form.getLabelRequiredPrefix());
                    } else {
                        buffer.append(form.getLabelNotRequiredPrefix());
                    }
                    buffer.elementStart("label");
                    buffer.appendAttribute("for", field.getId());
                    if (field.isDisabled()) {
                        buffer.appendAttributeDisabled();
                    }
                    if (field.getError() != null) {
                        buffer.appendAttribute("class", "error");
                    }
                    buffer.closeTag();
                    buffer.append(field.getLabel());
                    buffer.elementEnd("label");
                    if (field.isRequired()) {
                        buffer.append(form.getLabelRequiredSuffix());
                    } else {
                        buffer.append(form.getLabelNotRequiredSuffix());
                    }

                    if (Form.POSITION_LEFT.equals(getForm().getLabelsPosition())) {
                        buffer.append("</td>\n");
                        buffer.append("<td align=\"left\"");
                        buffer.appendAttribute("style", form.getFieldStyle());

                        if (width != null) {
                            int colspan = (width.intValue() * 2) + 1;
                            buffer.appendAttribute("colspan", colspan);
                        }

                        buffer.append(">");
                    } else {
                        buffer.append("<br/>");
                    }

                    // Write out field
                    buffer.append(field);
                    buffer.append("</td>\n");
                }

                if (width != null) {
                    if (field instanceof Label) {
                        column += width.intValue();

                    } else {
                        column += (width.intValue() - 1);
                    }
                }

                if (column >= getColumns()) {
                    buffer.append("</tr>\n");
                    openTableRow = false;
                    column = 1;
                } else {
                    column++;
                }

            }
        }

        if (openTableRow) {
            buffer.append("</tr>\n");
        }

        buffer.elementEnd("table");
        buffer.append("\n");
    }

}
