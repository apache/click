 /* Copyright 2004-2006 Malcolm A. Edgar
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
package net.sf.click.extras.control;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import net.sf.click.control.Field;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a ColorPicker control: &nbsp; &lt;input type='text'&gt;&lt;img&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Color Field</td>
 * <td><input type='text' size='7' title='ColorPicker Control' value='#ee0000'/>
 * <div style="background-color:'#ee0000">
 * <img align='middle' style='cursor:pointer' src='colorpicker/arrowdown.gif' title='choose color'/>
 * </div>
 * </td>
 * </tr>
 * </table>
 *
 * The ColorPicker control provides a popup DHTML color picker
 * &lt;div&gt; and a text input where users can enter the color in hex format. The text input
 * field can be turned off by setting {@link #setShowTextField(boolean)} to false (default is true).
 * <p/>
 * The ColorPicker control will validate wheter the entered color is present (if required) and that it
 * is a valid hex color either in 3-digit presentation (ie #eee) or 6-digit presentation (ie #eeeee).
 * If the color is not required the color-picker popup will show a button for 'no-color' on the top-left.
 * <p/>
 * The color picker popup is based on JS script code from liferay.com which in turn is based on code
 * from http://typetester.maratz.com/. To enable the color
 * popup, reference the {@link net.sf.click.util.PageImports} object
 * in the page template. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$imports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="red">$form</span>
 *  &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * @author Christian Essl
 */
public class ColorPicker extends Field {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** The Calendar resource file names. */
    static final String[] COLOR_PICKER_RESOURCES = {
        "/net/sf/click/extras/control/colorpicker/colorpicker.js",
        "/net/sf/click/extras/control/colorpicker/colorscale.png",
        "/net/sf/click/extras/control/colorpicker/arrowdown.gif",
        "/net/sf/click/extras/control/colorpicker/close.gif",
        "/net/sf/click/extras/control/colorpicker/nocolor.gif",
        "/net/sf/click/extras/control/colorpicker/nocolorchoose.gif"
    };

    /** The HTML imports statements. */
    static final String HTML_IMPORTS =
        "<script type=\"text/javascript\" src=\"${path}/click/prototype/prototype.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"${path}/click/colorpicker/colorpicker.js\"></script>\n";

    /** The color validation hexidecimal pattern. */
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

    // ----------------------------------------------------- Instance Variables

    /**
     * The show text field option for entering a color hex value. The default
     * value is true.
     */
    protected boolean showTextField = true;

    /** The text field size attribute. The default size is 7. */
    protected int size = 7;

    // ----------------------------------------------------------- Constructors

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

    // ------------------------------------------------------ Public Attributes

//  TODO: surround picker image with achor tag which is focusable

//    /**
//     * Return the Field focus JavaScript.
//     *
//     * @return the Field focus JavaScript
//     */
//    public String getFocusJavaScript() {
//        return null;
//    }

    /**
     * Return the HTML head import statements for the JavaScript files
     * <tt>click/prototype.js</tt>) and <tt>click/colorpicker.js</tt>.
     *
     * @see net.sf.click.control.Field#getHtmlImports()
     *
     * @return the HTML head import statements for prototype.js and colorpicker.js
     */
    public String getHtmlImports() {
        String path = getContext().getRequest().getContextPath();

        return StringUtils.replace(HTML_IMPORTS, "${path}", path);
    }

    /**
     * Wheter the TextField to enter the color hex number should be shown or
     * not. Default is true
     *
     * @return Returns the showTextField.
     */
    public boolean getShowTextField() {
        return showTextField;
    }

    /**
     * Wheter the TextField to enter the color hex number should be shown or
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
    public String getValidationJavaScript() {
        Object[] args = new Object[9];
        args[0] = getId();
        args[1] = String.valueOf(isRequired());
        args[2] = getMessage("field-required-error", getErrorLabel());
        args[3] = getMessage("no-color-value", getErrorLabel());
        return MessageFormat.format(VALIDATE_COLORPICKER_FUNCTION, args);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Deploy the static resource files in the colorpicker package.
     *
     * @see net.sf.click.control.Field#onDeploy(javax.servlet.ServletContext)
     * @param servletContext the ServletContext
     */
    public void onDeploy(ServletContext servletContext) {
        for (int i = 0; i < COLOR_PICKER_RESOURCES.length; i++) {
            ClickUtils.deployFile(servletContext,
                                  COLOR_PICKER_RESOURCES[i],
                                  "click/colorpicker");
        }
    }

    /**
     * Returns the HTML for the color-picker. This is the content of the
     * ColorPicker.htm template.
     *
     * @return a HTML rendered TextField string
     */
    public String toString() {
        Map values = new HashMap();

        values.put("id", getId());
        values.put("field", this);
        values.put("path", getContext().getRequest().getContextPath());

        if (isColor(getValue())) {
            values.put("back_color", getValue());
            values.put("value", getValue());
        } else {
            values.put("back_color", "#ffffff");
            values.put("value", "");
        }

        HtmlStringBuffer textFieldAttributes = new HtmlStringBuffer(96);
        if (getShowTextField()) {
            textFieldAttributes.appendAttribute("size", getSize());
            textFieldAttributes.appendAttribute("title", getTitle());
            if (isReadonly()) {
                textFieldAttributes.appendAttributeReadonly();
            }
            textFieldAttributes.appendAttribute("maxlength", 7);
            if (!isValid()) {
                textFieldAttributes.appendAttribute("class", "error");
            } else if (isDisabled()) {
                textFieldAttributes.appendAttribute("class", "disabled");
            }
        }
        if (hasAttributes()) {
            textFieldAttributes.appendAttributes(getAttributes());
        }
        if (isDisabled()) {
            textFieldAttributes.appendAttributeDisabled();
        }
        values.put("attributes", textFieldAttributes.toString());

        // The image messages
        values.put("chooseColorMsg", getMessage("choose-color"));
        values.put("noColorMsg", getMessage("no-color"));
        values.put("closeMsg", getMessage("close"));

        return getContext().renderTemplate(ColorPicker.class, values);
    }

    /**
     * Validates the input to check wheter is required or not and that the
     * input contains a valid color hex value.
     *
     * @see net.sf.click.control.TextField#validate()
     */
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

    // -------------------------------------------------------- Private Methods

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
