 /* Copyright 2004-2005 Malcolm A. Edgar
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
 
package net.sf.click.sandbox.chrisichris.control;

import org.apache.commons.lang.StringEscapeUtils;

import net.sf.click.control.ActionLink;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Submit;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a JavaScript form Submit button which renders itself as a link.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td><a href='' title='SubmitLink Control'>Action Link</a> </td>
 * </tr>
 * </table>
 *
 * <b>Note</b> this Field only works when JavaScript is enabled in the user
 * browser.
 * <p/>
 * SubmitLink can be rendered inside of its form like a Submit button or it can
 * be placed outside of its form anywhere on the Page like an ActionLink. If
 * used inside of the Form it is added to the form like a regular Form field ({@link net.sf.click.control.Form#add(Field)}).
 * If used outside of the Form the Form must be set through
 * {@link #setForm(Form)} and the SubmitLink must be added to the Page
 * ({@link net.sf.click.Page#addControl(Control)})
 * <b>after</b> the form has been added to the Page.
 * <p/>
 * Like Button SubmitLink supports invoking control listeners. When the
 * SubmitLink was clicked the {@link net.sf.click.control.Submit#isClicked()}
 * property is true. Like ActionLink the control can render the "href" URL
 * attribute using {@link #getHref()} and {@link #getHref(Object)} or render the
 * whole anchor tag in the toString() method.
 * <p/>
 * SubmitLink does also submit the {@link net.sf.click.control.Field#getValue()}
 * property when it is clicked. The value is transported through a hidden-field
 * which is shared by all SubmitLinks of one Form instance. Therefore if you set
 * the value of a SubmitLink it will only be present on the request when the
 * SubmitLink has been clicked. The value is not stored like for other Fields
 * over multiple submits.
 *
 * @see Submit
 *
 * @author Christian Essl
 */
public class SubmitLink extends Submit {

    private static final long serialVersionUID = 1L;

    /** Name Suffix for the hidden-field for the clicked value. */
    public static final String CLICK_FIELD_NAME = "click_submitlink_name";

    /** Name Suffix for the hidden-field fot the value. */
    public static final String VALUE_FIELD_NAME = "click_submitlink_value";

    // ----------------------------------------------------------- Constructors

    /**
     * Create a SubmitLink button with the given name.
     *
     * @param name the button name
     */
    public SubmitLink(String name) {
        super(name);
    }

    /**
     * Create a SubmitLink with the given name and label.
     *
     * @param name the button name
     * @param label the button display label
     */
    public SubmitLink(String name, String label) {
        super(name, label);
    }

    /**
     * Create a SubmitLink with the given name, listener object and listener
     * method.
     *
     * @param name the button name
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if listener is null or if the method is blank
     */
    public SubmitLink(String name, Object listener, String method) {
        super(name, listener, method);
    }

    /**
     * Create a SubmitLink with the given name, label, listener object and
     * listener method.
     *
     * @param name the button name
     * @param label the button display label
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if listener is null or if the method
     *      is blank
     */
    public SubmitLink(String name, String label, Object listener,
            String method) {

        super(name, label, listener, method);
    }

    /**
     * Create an SubmitLink with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid. <p/> <div
     * style="border: 1px solid red;padding:0.5em;"> No-args constructors are
     * provided for Java Bean tools support and are not intended for general
     * use. If you create a control instance using a no-args constructor you
     * must define its name before adding it to its parent. </div>
     */
    public SubmitLink() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Returns "hidden", because the field uses a hidden input type.
     *
     * @see net.sf.click.control.Submit#getType()
     *
     * @return "hidden"
     */
    public String getType() {
        return "hidden";
    }

    /**
     * Return the SubmitLink anchor &lt;a&gt; tag href attribute for the given
     * value. This method will create a JavaScript which stores the value to a
     * hidden-field an submits the form the Link belongs to.
     *
     * @param value the ActionLink value parameter
     * @return the ActionLink HTML href attribute
     */
    public String getHref(Object value) {

        String formSrc = "document.forms['"
            + StringEscapeUtils.escapeJavaScript(getForm().getName())
            + "'].";

        String script = "javascript:";
        script += formSrc + VALUE_FIELD_NAME + ".value = '";
        if (value != null) {
            script += StringEscapeUtils.escapeJavaScript(value.toString());
        }
        script += "';";

        script += formSrc + CLICK_FIELD_NAME + ".value = '"
                + StringEscapeUtils.escapeJavaScript(getName()) + "';";

        script += formSrc + "submit();";
        return script;
    }

    /**
     * Return the SubmitLink anchor &lt;a&gt; tag href attribute. This method
     * will create a JavaScript which stores the value to a hidden-field and
     * submits the form the Link belongs to.
     *
     * @return the ActionLink HTML href attribute
     */
    public String getHref() {
        return getHref(getValue());
    }

    /**
     * Return the HTML rendered anchor link string for the given value. This
     * method will render the entire anchor link including the tags, the label
     * and any attributes, see {@link Field#setAttribute(String, String)} for an
     * example.
     *
     * @param value which is submitted as a value when the link is clicked.
     * @return the HTML rendered anchor link string
     */
    public String getAnchorElement(Object value) {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        buffer.elementStart("a");

        buffer.appendAttribute("href", getHref(value));
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("title", getTitle());
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }
        buffer.closeTag();

        buffer.append(getLabel());

        buffer.elementEnd("a");

        return buffer.toString();
    }

    /**
     * Overriden to make sure that when added to a form the two necessary
     * hiddenfields are present.
     *
     * @see net.sf.click.control.Field#setForm(net.sf.click.control.Form)
     *
     * @param form the Form this belongs to or null
     */
    public void setForm(Form form) {
        super.setForm(form);
        if (form != null) {
            if (form.getField(CLICK_FIELD_NAME) == null) {
                form.add(new HiddenField(CLICK_FIELD_NAME, String.class));
            }
            if (form.getField(VALUE_FIELD_NAME) == null) {
                form.add(new HiddenField(VALUE_FIELD_NAME, String.class));
            }
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Bind the request submission, setting the {@link Field#value} and
     * {@link Submit#clicked} properties if defined.
     */
    public void bindRequestValue() {
        Field clickField = getForm().getField(CLICK_FIELD_NAME);
        if (clickField instanceof HiddenField) {
            String clickedName = clickField.getValue();
            if (clickedName != null && clickedName.equals(this.getName())) {
                this.clicked = true;
                Field valueField = getForm().getField(VALUE_FIELD_NAME);
                if (valueField instanceof HiddenField) {
                    String value = valueField.getValue();
                    this.setValue(value == null ? "" : value);
                } else {
                    throw new IllegalStateException(
                            "No hidden-field for SubmitLink in Form");
                }
            }
        } else {
            throw new IllegalStateException(
                    "No hidden-field for SubmitLink in Form");
        }
    }

    /**
     * Return the HTML rendered anchor link string. This method will render the
     * entire anchor link including the tags, the label and any attributes, see
     * {@link #setAttribute(String, String)} for an example.
     *
     * @return the HTML rendered anchor link string
     */
    public String toString() {
        return getAnchorElement(getValue());
    }

}
