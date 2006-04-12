package net.sf.click.extras.control;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import net.sf.click.control.Field;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Field to input a number. Shows a button with a pop-up to show a
 * color-palette, by default also shows a text-input to enter the color as a
 * hex-rgb-number. <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Color Field</td>
 * <td><input type='text' size='30' value='medgar@mycorp.com' title='EmailField
 * Control' value='#dddddd'/> <span style="background-color:#dddddd"> <img
 * src="colorpicker/arrowdown.gif"/> </span> </td>
 * </tr>
 * </table>
 *
 * The text-input is shown by default. If {@link #setShowTextField(boolean)} is
 * set to false the only the color-button is shown. <p/> ColorPicker will
 * validate its input that it is a RGB-hex number. The number must be either a
 * tree-part (ie #fff) or 6-part number (ie #ffffff). ColorPicker will also
 * validate it's required status. <p/> Note: This control uses JS and should
 * work on IE6, FireFox and Safari. It is only tested on IE6 and FireFox on
 * windows.
 *
 *
 * @author Christian Essl
 *
 */
public class ColorPicker extends Field {

    /** The HEX number pattern. */
    public static final Pattern HEX_PATTERN = Pattern
            .compile("#[a-fA-F0-9]{3}([a-fA-F0-9]{3})?");

    /** The text field size attribute. The default size is 20. */
    protected int size = 7;

    /**
     * Wheter only the color chooser should be shown or also a text field to
     * enter a hex value.
     */
    protected boolean showTextField;

    /**
     * Contrutcs a ColorPicker with the given name, which shows the text-input
     * field. The field is not required.
     *
     * @param name
     *            name of the ColorPicker
     */
    public ColorPicker(String name) {
        this(name, true, false);
    }

    /**
     * Constructs a ColorPicker with the given name and text-field option. The
     * field is not required.
     *
     * @param name
     *            the name of the ColorPicker
     * @param showTextField
     *            wheter to show the text-field
     */
    public ColorPicker(String name, boolean showTextField) {
        this(name, showTextField, false);
    }

    /**
     * Constructs a ColorPicker with the given name, text-field option and
     * required-status.
     *
     * @param name
     *            name of ColorPicker
     * @param showTextField
     *            wheter to show the text-input
     * @param required
     *            wheter required or not.
     */
    public ColorPicker(String name, boolean showTextField, boolean required) {
        super(name);
        this.showTextField = true;
        this.setRequired(required);
    }

    /**
     * Create a ColorPicker with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid. <p/> <div
     * style="border: 1px solid red;padding:0.5em;"> No-args constructors are
     * provided for Java Bean tools support and are not intended for general
     * use. If you create a control instance using a no-args constructor you
     * must define its name before adding it to its parent. </div>
     */
    public ColorPicker() {
        super();
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
     * @param size
     *            the field size
     */
    public void setSize(int size) {
        this.size = size;
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
     * @param showTextField
     *            The showTextField to set.
     */
    public void setShowTextField(boolean showTextField) {
        this.showTextField = showTextField;
    }

    /**
     * Validates the input to check wheter is required or not and that the input
     * contains a valid color hex value.
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

        if (isColor(getValue())) {
            values.put("back_color", getValue());
            values.put("value", getValue());
        } else {
            values.put("back_color", "#ffffff");
            values.put("value", "");
        }

        values.put("ctxt", getContext().getRequest().getContextPath());

        HtmlStringBuffer buffer = new HtmlStringBuffer(96);
        if (getShowTextField()) {
            buffer.appendAttribute("size", getSize());
            buffer.appendAttribute("title", getTitle());
            if (isReadonly()) {
                buffer.appendAttributeReadonly();
            }
            buffer.appendAttribute("maxlength", 7);
            if (!isValid()) {
                buffer.appendAttribute("class", "error");
            } else if (isDisabled()) {
                buffer.appendAttribute("class", "disabled");
            }
        }
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }
        values.put("attributes", buffer.toString());

        // the img messages
        values.put("chooseColorMsg", getMessage("choose-color"));
        values.put("noColorMsg", getMessage("no-color"));
        values.put("closeMsg", getMessage("close"));

        String ret = getContext().renderTemplate(ColorPicker.class, values);

        return ret;
    }

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

    /**
     * Imports the files in the colorpicker package.
     *
     * @see net.sf.click.control.Field#onDeploy(javax.servlet.ServletContext)
     * @param servletContext the ServletContext
     * @throws IOException if can not write
     */
    public void onDeploy(ServletContext servletContext) throws IOException {
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/extras/control/colorpicker/colorpicker.js",
                "click/colorpicker");

        ClickUtils.deployFile(servletContext,
                "/net/sf/click/extras/control/colorpicker/colorscale.png",
                "click/colorpicker");

        ClickUtils.deployFile(servletContext,
                "/net/sf/click/extras/control/colorpicker/arrowdown.gif",
                "click/colorpicker");

        ClickUtils.deployFile(servletContext,
                "/net/sf/click/extras/control/colorpicker/close.gif",
                "click/colorpicker");
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/extras/control/colorpicker/nocolor.gif",
                "click/colorpicker");
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/extras/control/colorpicker/nocolorchoose.gif",
                "click/colorpicker");
    }

    /**
     * Imports prototype.js and colorpicker.js.
     *
     * @see net.sf.click.control.Field#getHtmlImports()
     * @return import for prototype.js and colorpicker.js
     */
    public String getHtmlImports() {
        String ctxt = getContext().getRequest().getContextPath();
        String ret = "<script type=\"text/javascript\" src=\"" + ctxt
                + "/click/prototype/prototype.js\"></script>\n";
        ret += "<script type=\"text/javascript\" src=\"" + ctxt
                + "/click/colorpicker/colorpicker.js\"></script>\n";
        return ret;
    }

}
