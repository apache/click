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
import net.sf.click.util.ClickUtils;
import net.sf.click.util.ContainerUtils;
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
 * <p/>
 * One can use FieldSet with both {@link Form} and {@link BasicForm}, however
 * the behavior of FieldSet differs for each.
 * <p/>
 * <ul>
 *  <li>BasicForm - when adding FieldSet to a BasicForm, it will behave exactly
 *   like a normal container. The fields will be rendered next to each other in
 *   the order they were added to the fieldset.
 *  </li>
 *  <li>Field - When adding FieldSet to a Form, it will delegate rendering to
 *   {@link Form#renderFieldSet(net.sf.click.util.HtmlStringBuffer, net.sf.click.control.FieldSet)}
 *   thus using the properties of its parent Form for laying out and rendering of
 *   its fields.
 *   <p/>
 *   A FieldSet can contain any Control, but when used in conjuction
 *   with a {@link Form}, it is recommended to only add non-button fields.
 *   Form's auto-layout works best with fields, however nothing stops you from
 *   adding other controls to the fieldset.
 *  </li>
 * </ul>
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
public class FieldSet extends AbstractContainer {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------- Instance Variables

    /** The map of field width values. */
    protected Map fieldWidths;

    /** The parent BasicForm. */
    protected BasicForm form;

    /** The FieldSet label. */
    protected String label;

    /** The FieldSet legend. */
    protected String legend;

    /** The FieldSet legend attributes map. */
    protected Map legendAttributes;

    /** The render the fieldset border flag, default value is true. */
    protected boolean showBorder = true;

    // ------------------------------------------------------ Constructorrs

    /**
     * Create a FieldSet with the given name.
     *
     * @param name the fieldset name element value
     */
    public FieldSet(String name) {
        super(name);
    }

    /**
     * Create a FieldSet with the given name and legend.
     *
     * @param name the fieldset name
     * @param legend the fieldset legend element value
     */
    public FieldSet(String name, String legend) {
        super(name);
        setLegend(legend);
    }

    /**
     * Create a FieldSet with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public FieldSet() {
    }

    // ------------------------------------------------------ Public Methods

    /**
     * Add a Field to the FieldSet and return the added instance.
     * <p/>
     * <b>Please note</b> if the FieldSet's parent is a {@link Form}, the
     * Fields inside the FieldSet will be laid out by the Form.
     *
     * @see Container#add(net.sf.click.Control)
     *
     * @param index the index at which the control is to be inserted
     * @param control the control to add to the FieldSet and return
     * @return the control that was added to the FieldSet
     * @throws IllegalArgumentException if the control is null, the Field's name
     * is not defined, the container already contains a control with the same
     * name, if the control's parent is a Page or if the control is neither a
     * Field nor FieldSet
     */
    public Control add(int index, Control control) {
         if (control == null) {
            throw new IllegalArgumentException("Field parameter cannot be null");
        }
        if (control instanceof Field) {
            Field field = (Field) control;
            if (StringUtils.isBlank(field.getName())) {
               String msg = "Field name not defined: " + field.getClass().getName();
                throw new IllegalArgumentException(msg);
            }
            if (getControlMap().containsKey(field.getName())
                && !(field instanceof Label)) {

                throw new IllegalArgumentException(
                    "FieldSet already contains field named: " + field.getName());
            }
            getControls().add(field);

            getControlMap().put(field.getName(), field);

            field.setForm(getForm());

            field.setParent(this);

            if (getForm() instanceof Form) {
                Form form = (Form) getForm();
                if (form != null && form.getDefaultFieldSize() > 0) {
                    if (field instanceof TextField) {
                        ((TextField) field).setSize(form.getDefaultFieldSize());

                    } else if (field instanceof FileField) {
                        ((FileField) field).setSize(form.getDefaultFieldSize());

                    } else if (field instanceof TextArea) {
                        ((TextArea) field).setCols(form.getDefaultFieldSize());
                    }
                }
            }

        } else {
            super.add(index, control);
        }

        return control;
    }

    /**
     * Add the field to the fieldSet, and set the fields form property. The
     * field will be added to {@link #getControlMap()} using its name.
     * <p/>
     * Field instances will be add to {@link #getControls()}.
     *
     * @see #add(net.sf.click.Control)
     *
     * @param field the field to add to the form
     * @return the field added to this form
     * @throws IllegalArgumentException if the field is null, the field name
     * is not defined, the fieldSet already contains a control with the same name
     * or if the field's parent is a Page
     */
    public Field add(Field field) {
        add(getControls().size(), field);
        return field;
    }

    /**
     * Add the field to the fieldset and specify the field's width in columns.
     * <p/>
     * Note Button or HiddenFields types are not valid arguments for this method.
     *
     * @param field the field to add to the fieldset
     * @param width the width of the field in table columns
     * @return the field added to this fieldset
     * @throws IllegalArgumentException if the field is null, field's name is
     * not defined, field is a Button or HiddenField, the fieldset already
     * contains a control with the same name, if the field's parent is a Page or
     * the width &lt; 1
     */
    public Field add(Field field, int width) {
        if (field == null) {
            throw new IllegalArgumentException("Field parameter cannot be null");
        }
        if (field instanceof Button || field instanceof HiddenField) {
            String msg = "Not valid a valid field type: " + field.getClass().getName();
            throw new IllegalArgumentException(msg);
        }
        if (width < 1) {
            throw new IllegalArgumentException("Invalid field width: " + width);
        }

        add(field);
        getFieldWidths().put(field.getName(), new Integer(width));
        return field;
    }

    /**
     * @see Container#remove(net.sf.click.Control)
     *
     * @param control the control to remove from the container
     * @return true if the control was removed from the container
     * @throws IllegalArgumentException if the control is null
     */
    public boolean remove(Control control) {
        boolean removed = super.remove(control);

        if (control instanceof Field) {
            Field field = (Field) control;
            getFieldWidths().remove(field.getName());
            field.setForm(null);
        }
        return removed;
    }

    /**
     * Remove the given field from the fieldset.
     *
     * @param field the field to remove from the fieldset
     *
     * @throws IllegalArgumentException if the field is null
     */
    public void remove(Field field) {
        remove((Control) field);
    }

    /**
     * Return the fieldsets's html tag: <tt>fieldset</tt>.
     *
     * @see AbstractControl#getTag()
     *
     * @return this controls html tag
     */
    public String getTag() {
        return "fieldset";
    }

    /**
     * Return the render fieldset border flag. The border is the HTML
     * &lt;fieldset&gt; element.
     *
     * @return the render the fieldset border flag
     */
    public boolean getShowBorder() {
        return showBorder;
    }

    /**
     * Set the render fieldset border flag. The border is the HTML
     * &lt;fieldset&gt; element.
     *
     * @param value the render the fieldset border flag
     */
    public void setShowBorder(boolean value) {
        this.showBorder = value;
    }

    /**
     * Return the List of fields, ordered in addition order to the fieldset.
     *
     * @return the ordered List of fieldset fields
     */
    public List getFieldList() {
        return ContainerUtils.getFieldsAndLabels(this);
    }

    /**
     * Return the Map of fieldset fields, keyed on field name.
     *
     * @return the Map of fieldset fields, keyed on field name
     */
    public Map getFields() {
        return ContainerUtils.getFieldMap(this);
    }

    /**
     * Return the map of field width values, keyed on field name.
     *
     * @return the map of field width values, keyed on field name
     */
    public Map getFieldWidths() {
        if (fieldWidths == null) {
            fieldWidths = new HashMap();
        }
        return fieldWidths;
    }

    /**
     * Return the parent Form containing the FieldSet or null if no form is
     * present in the parent hierarchy.
     *
     * @return the parent Form containing the FieldSet
     */
    public BasicForm getForm() {
        if (form != null) {
            return form;

        } else {
            // Find form in parent hierarchy
            return ContainerUtils.findForm(this);
        }
    }

    /**
     * Set the FieldSet's the parent <tt>Form</tt>.
     *
     * @param form FieldSet's parent <tt>Form</tt>
     */
    public void setForm(BasicForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Cannot set the FieldSet's form to null");
        }

        this.form = form;

        // Set the specified form on the fieldsSets children. This call is not
        // recursive to childrens children
        for (Iterator it = getControls().iterator(); it.hasNext();) {
            Control control = (Control) it.next();
            if (control instanceof Field) {
                ((Field) control).setForm(form);
            }
        }
    }

    /**
     * Return the fieldSet display label.
     *
     * @see Field#getLabel()
     *
     * @return the display label of the Field
     */
    public String getLabel() {
        if (label == null) {
            label = getMessage(getName() + ".label");
        }
        if (label == null) {
            label = ClickUtils.toLabel(getName());
        }
        return label;
    }

    /**
     * Set the Field display caption.
     *
     * @param label the display label of the Field
     */
    public void setLabel(String label) {
        this.label = label;
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
            String fsName = getName();
            if (fsName != null) {
                legend = ClickUtils.toLabel(fsName);
            }
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
                if (controlName == null || !controlName.startsWith(Form.SUBMIT_CHECK)) {
                    boolean continueProcessing = control.onProcess();
                    if (!continueProcessing) {
                        return false;
                    }
                }
            }
        }
        return true;
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
     * Render the HTML representation of the FieldSet.
     * <p/>
     * If FieldSet is contained within a {@link Form} instance, this method will
     * delegate rendering to {@link Form#renderFieldSet(net.sf.click.util.HtmlStringBuffer, net.sf.click.control.FieldSet)}.
     * <p/>
     * The size of buffer is determined by {@link #getControlSizeEst()}.
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        BasicForm form = getForm();

        if (form instanceof Form) {
            ((Form) form).renderFieldSet(buffer, this);
        } else {
            if (getShowBorder()) {
                renderTagBegin(getTag(), buffer);
                buffer.closeTag();
                if (hasControls()) {
                    buffer.append("\n");
                }

                renderContent(buffer);

                renderTagEnd(getTag(), buffer);
            } else {
                renderChildren(buffer);
            }
        }
    }

    /**
     * Returns the HTML representation of the FieldSet.
     * <p/>
     * The rendering of the FieldSet is delegated to
     * {@link #render(net.sf.click.util.HtmlStringBuffer)}. The size of buffer
     * is determined by {@link #getControlSizeEst()}.
     *
     * @see Object#toString()
     *
     * @return the HTML representation of this control
     */
    public String toString() {
        return super.toString();
    }

    //-------------------------------------------- protected methods

    /**
     * @see AbstractControl#renderTagBegin(java.lang.String, net.sf.click.util.HtmlStringBuffer)
     *
     * @param tagName the name of the tag to render
     * @param buffer the buffer to append the output to
     */
    protected void renderTagBegin(String tagName, HtmlStringBuffer buffer) {
        if (tagName == null) {
            throw new IllegalStateException("Tag cannot be null");
        }

        buffer.elementStart(tagName);

        String id = getId();
        if (id != null) {
            buffer.appendAttribute("id", id);
        }

        appendAttributes(buffer);
    }

    /**
     * @see AbstractContainer#renderContent(net.sf.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderContent(HtmlStringBuffer buffer) {
        String fsLegend = getLegend();
        if (fsLegend != null && fsLegend.length() > 0) {
            buffer.elementStart("legend");
            if (hasLegendAttributes()) {
                Object legendId = getLegendAttributes().get("id");
                if (legendId != null) {
                    buffer.appendAttribute("id", legendId);
                }
                buffer.appendAttributes(getLegendAttributes());
            }
            buffer.closeTag();
            buffer.append(fsLegend);
            buffer.elementEnd("legend");
            buffer.append("\n");
        }
        super.renderContent(buffer);
    }
}
