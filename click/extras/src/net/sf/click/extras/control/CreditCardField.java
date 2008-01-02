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
package net.sf.click.extras.control;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.control.TextField;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a Credit Card control: &nbsp; &lt;input type='text'&gt;&lt;select&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Credit Card</td>
 * <td><input type='text' title='CreditCard Control'/><select title='Card type'><option>VISA</option><option>MASTER</option>
 * <option>AMEX</option><option>DINNER</option><option>DISCOVER</option></select>
 * </td>
 * </tr>
 * </table>
 *
 * CreditCardField will validate the card number against the selected card type
 * when the control is processed.
 * <p/>
 * Supported card include VISA, MASTER, AMEX, DINERS and DISCOVER.
 * <p/>
 * The CreditCardField uses a JavaScript onkeypress() integerFilter() method to
 * prevent users from entering invalid characters. To enable number key filtering
 * reference the {@link net.sf.click.util.PageImports} object in the page template.
 *
 * <h3>CreditCardField Example</h3>
 *
 * A CreditCardField example is provided below:
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
 * An example page using CreditCardField is provided below:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> PaymentPage <span class="kw">extends</span> Page {
 *
 *     <span class="kw">private</span> Form form = <span class="kw">new</span> Form(<span class="st">"form"</span>);
 *     <span class="kw">private</span> CreditCardField creditCardField  = <span class="kw">new</span> CreditCardField(<span class="st">"creditCard"</span>, <span class="kw">true</span>);
 *     <span class="kw">private</span> IntegerField expiryField  = <span class="kw">new</span> IntegerField(<span class="st">"expiryDate"</span>, <span class="kw">true</span>);
 *
 *     <span class="kw">public</span> PaymentPage() {
 *         form.add(creditCardField);
 *
 *         expiryField.setMinLength(4);
 *         expiryField.setMaxLength(4);
 *         expiryField.setSize(4);
 *         form.add(expiryField);
 *
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"  OK  "</span>));
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"cancel"</span>, <span class="kw">this</span>, "<span class="st">onCancelClick"</span>));
 *
 *         addControl(form);
 *     }
 *
 *     <span class="kw">public boolean</span> onCancelClick() {
 *         setRedirect(<span class="st">"index.htm"</span>);
 *         <span class="kw">return false</span>;
 *     }
 *
 *     <span class="kw">public void</span> onPost() {
 *         <span class="kw">if</span> (form.isValid()) {
 *             String cardType = creditCardField.getCardType();
 *             Long cardNumber = creditCardField.getCardNumber();
 *             String expiryDate = expiryField.getInteger();
 *
 *             <span class="cm">// Make payment</span>
 *             ..
 *         }
 *     }
 * } </pre>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class CreditCardField extends TextField {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** The Visa type credit card: "VISA". */
    public static final String VISA = "VISA";

    /** The MasterCard type credit card: "MASTER". */
    public static final String MASTER = "MASTER";

    /** The American Express type credit card: "AMEX". */
    public static final String AMEX = "AMEX";

    /** The Diners Club type credit card: "DINERS". */
    public static final String DINERS = "DINERS";

    /** The Discovery type credit card: "DISCOVER". */
    public static final String DISCOVER = "DISCOVER";

    /** The statically initialized card type options list. */
    protected static final List CARD_OPTIONS = new ArrayList();

    /** The card type Select name. */
    protected static final String SELECT_NAME = "cardType";

    static {
        CARD_OPTIONS.add(new Option(VISA, "Visa"));
        CARD_OPTIONS.add(new Option(MASTER, "Master"));
        CARD_OPTIONS.add(new Option(AMEX, "AmEx"));
        CARD_OPTIONS.add(new Option(DINERS, "Diners"));
        CARD_OPTIONS.add(new Option(DISCOVER, "Discover"));
    }

    // -------------------------------------------------------------- Constants

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the card type id</li>
     * <li>2 - is the Field required status</li>
     * <li>3 - is the minimum length</li>
     * <li>4 - is the maximum length</li>
     * <li>5 - is the localized error message for required validation</li>
     * <li>6 - is the localized error message for minimum length validation</li>
     * <li>7 - is the localized error message for maximum length validation</li>
     * <li>8 - is the localized error message for format validation</li>
     * </ul>
     */
    protected final static String VALIDATE_CREDITCARD_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateCreditCardField(\n"
        + "         ''{0}'', ''{1}'', {2}, {3}, {4}, [''{5}'',''{6}'',''{7}'', ''{8}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    /**
     * The CreditCardField.js imports statement.
     */
    public static final String CREDITCARD_IMPORTS =
        "<script type=\"text/javascript\" src=\"$/click/CreditCardField.js\"></script>\n";

    // ----------------------------------------------------- Instance Variables

    /**
     * The type of credit card: <tt>["VISA" | "MASTER" | "AMEX" | "DINERS" |
     * "DISCOVER"]</tt>. The default value is "VISA"
     */
    protected String cardType = VISA;

    /** The card type Select. */
    protected Select cardTypeSelect = new Select(SELECT_NAME);

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the credit card field with the given name.
     *
     * @param name the name of the field
     */
    public CreditCardField(String name) {
        super(name);
        setMaxLength(19);
        setSize(19);
        setAttribute("onkeypress", "javascript:return integerFilter(event);");
        cardTypeSelect.addAll(CARD_OPTIONS);
    }

    /**
     * Construct the credit card field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public CreditCardField(String name, String label) {
        super(name, label);
        setMaxLength(19);
        setSize(19);
        setAttribute("onkeypress", "javascript:return integerFilter(event);");
        cardTypeSelect.addAll(CARD_OPTIONS);
    }

    /**
     * Construct the credit card field with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public CreditCardField(String name, boolean required) {
        this(name);
        setRequired(required);
    }


    /**
     * Construct the credit card field with the given name, label and required
     * status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public CreditCardField(String name, String label, boolean required) {
        this(name, label);
        setRequired(required);
    }

    /**
     * Create a credit card field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public CreditCardField() {
        super();
        setMaxLength(19);
        setSize(19);
        setAttribute("onkeypress", "javascript:return integerFilter(event);");
        cardTypeSelect.addAll(CARD_OPTIONS);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the selected Credit Card type: &nbsp;
     * <tt>[ "VISA" | "MASTER" | "AMEX" | "DINERS" | "DISCOVER" ]</tt>.
     *
     * @return the selected Credit Card type
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * Return the Credit Card number.
     *
     * @return the Credit Card number
     */
    public Long getCardNumber() {
        String value = getValue();
        if (value != null && value.length() > 0) {
            return Long.valueOf(value);
        } else {
            return null;
        }
    }

    /**
     * Return the HTML head import statements for the CreditCardField.js.
     *
     * @return the HTML head import statements for the CreditCardField.js
     */
    public String getHtmlImports() {
        String path = getContext().getRequest().getContextPath();

        return StringUtils.replace(CREDITCARD_IMPORTS, "$", path);
    }

    // -------------------------------------------------------- Public Methods

    /**
     * Bind the request submission, setting the value property and
     * {@link #cardType} property if defined in the request.
     */
    public void bindRequestValue() {
        super.bindRequestValue();

        cardType = getContext().getRequestParameter(SELECT_NAME);
    }

    /**
     * Return the HTML rendered CreditCardField string.
     *
     * @return the HTML rendered CreditCardField string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(400);

        // Render card number field
        String textField = super.toString();
        buffer.append(textField);

        // Render card type select
        cardTypeSelect.setValue(cardType);
        cardTypeSelect.setForm(getForm());
        buffer.append(cardTypeSelect);

        return buffer.toString();
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
        args[1] = getForm().getId() + "_" + SELECT_NAME;
        args[2] = String.valueOf(isRequired());
        args[3] = String.valueOf(getMinLength());
        args[4] = String.valueOf(getMaxLength());
        args[5] = getMessage("field-required-error", getErrorLabel());
        args[6] = getMessage("field-minlength-error",
                new Object[]{getErrorLabel(), String.valueOf(getMinLength())});
        args[7] = getMessage("field-maxlength-error",
                new Object[]{getErrorLabel(), String.valueOf(getMaxLength())});
        args[8] = getMessage("creditcard-number-error", getErrorLabel());

        return MessageFormat.format(VALIDATE_CREDITCARD_FUNCTION, args);
    }

    /**
     * Validate the CreditCardField request submission, using the card type to
     * validate the card number.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle:
     * <blockquote>
     * <ul>
     *   <li>/click-control.properties
     *     <ul>
     *       <li>field-maxlenght-error</li>
     *       <li>field-minlength-error</li>
     *       <li>field-required-error</li>
     *     </ul>
     *   </li>
     *   <li>/net/sf/click/extras/control/CreditCardField.properties
     *     <ul>
     *       <li>creditcard-number-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    public void validate() {
        super.validate();

        if (isValid() && getValue().length() > 0) {
            String value = getValue();
            String cardType = getCardType();

            // Strip spaces and '-' chars
            HtmlStringBuffer buffer = new HtmlStringBuffer(value.length());
            for (int i = 0, size = value.length(); i < size; i++) {
                char aChar = value.charAt(i);
                if (aChar != '-' && aChar != ' ') {
                    buffer.append(aChar);
                }
            }
            value = buffer.toString();

            final int length = value.length();

            // Shortest valid number is VISA with 13 digits
            if (length < 13) {
                setErrorMessage("creditcard-number-error");
                return;
            }

            if (cardType == null) {
                throw new IllegalArgumentException("cardType is null");
            }
            final char firstdig = value.charAt(0);
            final char seconddig = value.charAt(1);

            boolean isValid = false;

            if (cardType.equals(VISA)) {

                isValid = ((length == 16) || (length == 13))
                        && (firstdig == '4');

            } else if (cardType.equals(MASTER)) {

                isValid = (length == 16) && (firstdig == '5')
                        && ("12345".indexOf(seconddig) >= 0);

            } else if (cardType.equals(AMEX)) {

                isValid = (length == 15) && (firstdig == '3')
                        && ("47".indexOf(seconddig) >= 0);

            } else if (cardType.equals(DINERS)) {

                isValid = (length == 14) && (firstdig == '3')
                        && ("068".indexOf(seconddig) >= 0);

            } else if (cardType.equals(DISCOVER)) {

                isValid = (length == 16) && value.startsWith("6011");
            }

            if (!isValid) {
                setErrorMessage("creditcard-number-error");
            }
        }
    }

    /**
     * Deploy the <tt>CreditCardField.js</tt> file to the <tt>click</tt> web
     * directory when the application is initialized.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFile(servletContext,
                              "/net/sf/click/extras/control/CreditCardField.js",
                              "click");
    }

}
