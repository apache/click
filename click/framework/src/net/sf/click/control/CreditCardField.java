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
import java.util.List;

import net.sf.click.util.ClickUtils;

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
 * The CreditCardField uses a JavaScript onKeyPress() integerFilter() method to
 * prevent users from entering invalid characters. To enable number key filtering
 * reference the method {@link Form#getHtmlImports()} in the page template
 * (imports click/form.js file). For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$form.htmlImports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="blue">$form</span>
 *  &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * An example page using CreditCardField is provided below:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> PaymentPage <span class="kw">extends</span> Page {
 *
 *     Form form;
 *     CreditCardField creditCardField;
 *     IntegerField expiryField;
 *
 *     <span class="kw">public void</span> onInit() {
 *         form = <span class="kw">new</span> Form(<span class="st">"form"</span>, getContext());
 *         addControl(form);
 *
 *         creditCardField = <span class="kw">new</span> CreditCardField(<span class="st">"Credit Card"</span>);
 *         creditCardField.setRequired(<span class="kw">true</span>);
 *         form.add(creditCardField);
 *
 *         expiryField = <span class="kw">new</span> IntegerField(<span class="st">"Expiry Date"</span>);
 *         expiryField.setRequired(<span class="kw">true</span>);
 *         expiryField.setMinLength(4);
 *         expiryField.setMaxLength(4);
 *         expiryField.setSize(4);
 *         form.add(expiryField);
 *
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"  OK  "</span>));
 *
 *         Submit cancelButton = <span class="kw">new</span> Submit(<span class="st">" Cancel "</span>);
 *         cancelButton.setListener(<span class="kw">this</span>, "<span class="st">onCancelClick"</span>);
 *         form.add(cancelButton);
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
 * @version $Id$
 */
public class CreditCardField extends TextField {

    private static final long serialVersionUID = -4591403023871778803L;

    /** The Visa type credit card: "VISA" */
    public static final String VISA = "VISA";

    /** The MasterCard type credit card: "MASTER" */
    public static final String MASTER = "MASTER";

    /** The American Express type credit card: "AMEX" */
    public static final String AMEX = "AMEX";

    /** The Diners Club type credit card: "DINERS" */
    public static final String DINERS = "DINERS";

    /** The Discovery type credit card: "DISCOVER" */
    public static final String DISCOVER = "DISCOVER";

    /** The statically initialized card type options list. */
    protected static final List CARD_OPTIONS = new ArrayList();

    /** The card type Select name. */
    protected static final String SELECT_NAME = "cardtype";

    static {
        // TODO: localize labels
        CARD_OPTIONS.add(new Option(VISA, "Visa"));
        CARD_OPTIONS.add(new Option(MASTER, "Master"));
        CARD_OPTIONS.add(new Option(AMEX, "AmEx"));
        CARD_OPTIONS.add(new Option(DINERS, "Diners"));
        CARD_OPTIONS.add(new Option(DISCOVER, "Discover"));
    }

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
     * Construct the credit card field with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     */
    public CreditCardField(String label) {
        this(ClickUtils.toName(label), label);
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
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
        cardTypeSelect.addAll(CARD_OPTIONS);
    }

    /**
     * Construct the credit card field with the given label and required status.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     * @param required the field required status
     */
    public CreditCardField(String label, boolean required) {
        this(label);
        setRequired(required);
    }

    /**
     * Create a credit card field with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public CreditCardField() {
        super();
        setMaxLength(19);
        setSize(19);
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
        cardTypeSelect.addAll(CARD_OPTIONS);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the selected Credit Card type: &nbsp;
     * <tt>[ "VISA" | "MASTER" | "AMEX" | "DINERS" | "DISCOVER" ]</tt>
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

    // -------------------------------------------------------- Public Methods

    /**
     * Process the Credit Card submission, using the card type to validate
     * the card number.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>/click-control.properties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>creditcard-number-error</li>
     * <li>field-maxlength-error</li>
     * <li>field-minlength-error</li>
     * <li>field-required-error</li>
     * </ul></blockquote>
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        value = getRequestValue();

        cardType = getContext().getRequestParameter(SELECT_NAME);

        if (!validate()) {
            return true;
        }

        // Strip spaces and '-' chars
        StringBuffer buffer = new StringBuffer(value.length());
        for (int i = 0, size = value.length(); i < size; i++) {
            char aChar = value.charAt(i);
            if (aChar != '-' && aChar != ' ') {
                buffer.append(aChar);
            }
        }
        value = buffer.toString();

        final int length = value.length();
        if (length > 0) {
            if (length < getMinLength()) {
                Object[] args = new Object[] { getLabel(), new Integer(getMinLength()) };
                setError(getMessage("field-minlength-error", args));
                return true;
            }
            if (length > getMaxLength()) {
                Object[] args = new Object[] { getLabel(), new Integer(getMaxLength()) };
                setError(getMessage("field-maxlength-error", args));
                return true;
            }

            // Shortest valid number is VISA with 13 digits
            if (length < 13) {
                setError(getMessage("creditcard-number-error", getLabel()));
                return true;
            }

            if (cardType != null) {
                final char firstdig = value.charAt(0);
                final char seconddig = value.charAt(1);

                boolean isValid = false;

                if (cardType.equals(VISA)) {

                    isValid = ((length == 16) || (length == 13))
                              && (firstdig == '4');

                } else if (cardType.equals(MASTER)) {

                    isValid = (length == 16) && (firstdig == '5')
                              && ("12345".indexOf(seconddig)>=0);

                } else if (cardType.equals(AMEX)) {

                    isValid = (length == 15) && (firstdig == '3')
                              && ("47".indexOf(seconddig)>=0);

                } else if (cardType.equals(DINERS)) {

                    isValid = (length == 14) && (firstdig == '3')
                              && ("068".indexOf(seconddig) >=0 );

                } else if (cardType.equals(DISCOVER)) {

                    isValid = (length == 16) && value.startsWith("6011");
                }

                if (!isValid) {
                    setError(getMessage("creditcard-number-error", getLabel()));
                }
            }

            return invokeListener();

        } else {
            if (isRequired()) {
                setError(getMessage("field-required-error",  getLabel()));
            }

            return true;
        }
    }

    /**
     * Return the HTML rendered CreditCardField string.
     *
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(400);

        // Render card number field
        String textField = super.toString();
        buffer.append(textField);

        // Render card type select
        cardTypeSelect.setContext(getContext());
        cardTypeSelect.setValue(cardType);
        cardTypeSelect.setForm(getForm());
        buffer.append(cardTypeSelect);

        return buffer.toString();
    }
}
