/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.control;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.click.Context;
import org.apache.click.control.Field;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a ColorPicker control: &nbsp; &lt;input type='text'&gt;&lt;img&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Color Field</td>
 * <td><input type='text' size='7' title='ColorPicker Control' value='#EE0000'/>
 * <img align='top' style='cursor:pointer' src='colorpicker/color-picker.png' title='choose color'/>
 * </td>
 * </tr>
 * </table>
 *
 * The ColorPicker control provides a popup DHTML color picker and a text input
 * where users can enter the color in hex format. The text input field can be
 * turned off by setting {@link #setShowTextField(boolean)} to false
 * (default is true).
 * <p/>
 * The ColorPicker control will validate whether the entered color is present (if required) and that it
 * is a valid hex color either in 3-digit presentation (ie #EEE) or 6-digit presentation (ie #EEEEEE).
 * If the color is not required the color-picker popup will show a button for 'no-color' on the top-left.
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * ColorPicker depends on the <a class="external" target="_blank" href="http://www.prototypejs.org">Prototype</a>
 * JavaScript library.
 * <p/>
 * The ColorPicker control makes use of the following resources
 * (which Click automatically deploys to the application directories,
 * <tt>/click/colorpicker</tt> and <tt>/click/prototype</tt>):
 *
 * <ul>
 * <li><tt>click/colorpicker/colorpicker.css</tt></li>
 * <li><tt>click/prototype/prototype.js</tt></li>
 * <li><tt>click/colorpicker/colorpicker.js</tt></li>
 * </ul>
 *
 * To import these ColorPicker files simply reference the variables
 * <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> in the page template. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 * &lt;head&gt;
 * <span class="blue">$headElements</span>
 * &lt;/head&gt;
 * &lt;body&gt;
 *
 * <span class="red">$form</span>
 *
 * <span class="blue">$jsElements</span>
 * &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * <h4>Credits</h4>
 *
 * The color picker popup is based on JS script code from
 * <a target="_blank" href="http://www.dhtmlgoodies.com/index.html?whichScript=submitted-color-picker">www.dhtmlgoodies.com</a>.
 */
public class ColorPicker extends Field {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    /** The color validation hexadecimal pattern. */
    static final Pattern HEX_PATTERN =
        Pattern.compile("#[a-fA-F0-9]{3}([a-fA-F0-9]{3})?");

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the Field required status</li>
     * <li>2 - is the localized error message for required validation</li>
     * <li>3 - is the localized error message for pattern validation</li>
     * </ul>
     */
    final static String VALIDATE_COLORPICKER_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateColorPicker(\n"
        + "         ''{0}'',{1}, [''{2}'',''{3}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    // Instance Variables -----------------------------------------------------

    /**
     * The show text field option for entering a color hex value. The default
     * value is true.
     */
    protected boolean showTextField = true;

    /** The text field size attribute. The default size is 7. */
    protected int size = 7;

    // Constructors -----------------------------------------------------------

    /**
     * Construct a ColorPicker with the given name. The color picker will show
     * the text input field.
     *
     * @param name the name of the field
     */
    public ColorPicker(String name) {
        super(name);
    }

    /**
     * Construct the ColorPicker with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public ColorPicker(String name, String label) {
        super(name, label);
    }

    /**
     * Construct a ColorPicker with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public ColorPicker(String name, boolean required) {
        super(name);
        setRequired(required);
    }

    /**
     * Constructs a ColorPicker with the given name, required status and
     * display text field option.
     *
     * @param name the name of field
     * @param required the field required status
     * @param showTextField flag to show the text input field
     */
    public ColorPicker(String name, boolean required, boolean showTextField) {
        this(name, required);
        this.showTextField = showTextField;
    }

    /**
     * Create a ColorPicker with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public ColorPicker() {
        super();
    }

    // Public Attributes ------------------------------------------------------

    /**
     * Return the ColorPicker HTML HEAD elements for the following
     * resources:
     * <p/>
     * <ul>
     * <li><tt>click/colorpicker/colorpicker.css</tt></li>
     * <li><tt>click/prototype/prototype.js</tt></li>
     * <li><tt>click/colorpicker/colorpicker.js</tt></li>
     * </ul>
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML HEAD elements for the control
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            Context context = getContext();
            String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

            headElements.add(new CssImport("/click/colorpicker/colorpicker.css", versionIndicator));
            headElements.add(new JsImport("/click/prototype/prototype.js", versionIndicator));
            headElements.add(new JsImport("/click/colorpicker/colorpicker.js", versionIndicator));
        }
        return headElements;
    }

    /**
     * Whether the TextField to enter the color hex number should be shown or
     * not. Default is true
     *
     * @return Returns the showTextField.
     */
    public boolean getShowTextField() {
        return showTextField;
    }

    /**
     * Whether the TextField to enter the color hex number should be shown or
     * not. Default is true.
     *
     * @param showTextField the showTextField to set
     */
    public void setShowTextField(boolean showTextField) {
        this.showTextField = showTextField;
    }

    /**
     * Return the field size. By default is 7. Only used when
     * {@link #getShowTextField()} true.
     *
     * @return the field size
     */
    public int getSize() {
        return size;
    }

    /**
     * Set the field size.
     *
     * @param size the field size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Return the field JavaScript client side validation function.
     * <p/>
     * The function name must follow the format <tt>validate_[id]</tt>, where
     * the id is the DOM element id of the fields focusable HTML element, to
     * ensure the function has a unique name.
     *
     * @return the field JavaScript client side validation function
     */
    @Override
    public String getValidationJavaScript() {
        Object[] args = new Object[9];
        args[0] = getId();
        args[1] = String.valueOf(isRequired());
        args[2] = getMessage("field-required-error", getErrorLabel());
        args[3] = getMessage("no-color-value", getErrorLabel());
        return MessageFormat.format(VALIDATE_COLORPICKER_FUNCTION, args);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * @see org.apache.click.control.AbstractControl#getControlSizeEst()
     *
     * @return the estimated rendered control size in characters
     */
    @Override
    public int getControlSizeEst() {
        return 96;
    }

    /**
     * Render the HTML representation of the ColorPicker.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {
        Context context = getContext();
        Map<String, Object> values = new HashMap<String, Object>();

        values.put("id", getId());
        values.put("field", this);
        values.put("path", context.getRequest().getContextPath());

        if (isColor(getValue())) {
            values.put("back_color", getValue());
        } else {
            values.put("back_color", "#FFFFFF");
        }
        values.put("value", getValue());

        HtmlStringBuffer textFieldAttributes = new HtmlStringBuffer(96);
        if (getShowTextField()) {
            textFieldAttributes.appendAttribute("size", getSize());
            textFieldAttributes.appendAttribute("title", getTitle());
            if (isReadonly()) {
                textFieldAttributes.appendAttributeReadonly();
            }
            textFieldAttributes.appendAttribute("maxlength", 7);

            if (isValid()) {
                removeStyleClass("error");
                if (isDisabled()) {
                    addStyleClass("disabled");
                } else {
                    removeStyleClass("disabled");
                }
            } else {
                addStyleClass("error");
            }
        }

        appendAttributes(textFieldAttributes);

        if (isDisabled()) {
            textFieldAttributes.appendAttributeDisabled();
        }
        values.put("attributes", textFieldAttributes.toString());

        // The image messages
        values.put("chooseColorMsg", getMessage("choose-color"));
        values.put("noColorMsg", getMessage("no-color"));
        values.put("closeMsg", getMessage("close"));

        renderTemplate(buffer, values);
    }

    /**
     * Returns the HTML for the color-picker. This is the content of the
     * ColorPicker.htm template.
     *
     * @return a HTML rendered ColorPicker string
     */
    @Override
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(getControlSizeEst());
        render(buffer);
        return buffer.toString();
    }

    /**
     * Validates the input to check whether is required or not and that the
     * input contains a valid color hex value.
     *
     * @see org.apache.click.control.TextField#validate()
     */
    @Override
    public void validate() {
        setError(null);

        String value = getValue();

        int length = value.length();
        if (length > 0) {
            Matcher matcher = HEX_PATTERN.matcher(value);
            if (!matcher.matches()) {
                setErrorMessage("no-color-value");
            }
        } else {
            if (isRequired()) {
                setErrorMessage("field-required-error");
            }
        }
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Render a Velocity template for the given data model.
     *
     * @param buffer the specified buffer to render the template output to
     * @param model the model data to merge with the template
     */
    protected void renderTemplate(HtmlStringBuffer buffer, Map<String, ?> model) {
        buffer.append(getContext().renderTemplate(ColorPicker.class, model));
    }

    // Private Methods --------------------------------------------------------

    private boolean isColor(String value) {
        if (value == null) {
            return false;
        }
        int length = value.length();
        if (length > 0) {
            Matcher matcher = HEX_PATTERN.matcher(value);
            return matcher.matches();
        } else {
            return false;
        }

    }

}
