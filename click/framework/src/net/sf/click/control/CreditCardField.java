/*
 * Copyright 2004 Malcolm A. Edgar
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

/**
 * Provides a Credit Card control: &nbsp; &lt;input type='text'&gt;&lt;select&gt;.
 * <p/>
 * <table class='form'><tr>
 * <td>Credit Card</td>
 * <td><input type='text' title='CreditCard Control'/><select title='Card type'><option>VISA</option><option>MASTER</option>
 * <option>AMEX</option><option>DINNER</option><option>DISCOVER</option></select>
 * </td>
 * </tr></table>
 * <p/>
 * CreditCardField will validate the card number against the selected card type 
 * when the control is processed. 
 * <p/>
 * Supported card include VISA, MASTER, AMEX, DINERS and DISCOVER.
 * <p/>
 * An example page using CreditCardField is provided below:<blockquote><pre>
 * public class PaymentPage extends Page {
 * 
 *     Form form;
 *     CreditCardField creditCardField;
 *     IntegerField expiryField;
 * 
 *     public void onInit() {
 *         form = new Form("form", getContext());
 *         addControl(form);
 * 
 *         creditCardField = new CreditCardField("Credit Card");
 *         creditCardField.setRequired(true);
 *         form.add(creditCardField);
 * 
 *         expiryField = new IntegerField("Expiry Date");
 *         expiryField.setRequired(true);
 *         expiryField.setMinLength(4);
 *         expiryField.setMaxLength(4);
 *         expiryField.setSize(4);
 *         form.add(expiryField);
 * 
 *         form.add(new Submit("  OK  "));
 * 
 *         Submit cancelButton = new Submit(" Cancel ");
 *         cancelButton.setListener(this, "onCancelClick");
 *         form.add(cancelButton);
 *     }
 * 
 *     public boolean onCancelClick() {
 *         setRedirect("index.htm");
 *         return false;
 *     }
 *  
 *     public void onPost() {
 *         if (form.isValid()) {
 *             String cardType = creditCardField.getCardType();
 *             Long cardNumber = creditCardField.getCardNumber();
 *             String expiryDate = expiryField.getInteger();
 * 
 *             // Make payment
 *             ..
 *         }
 *     }
 * }
 * </pre></blockquote>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification" 
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 * 
 * @author Malcolm Edgar
 */
public class CreditCardField extends TextField {

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
        CARD_OPTIONS.add(new Select.Option(VISA, "Visa"));
        CARD_OPTIONS.add(new Select.Option(MASTER, "Master"));
        CARD_OPTIONS.add(new Select.Option(AMEX, "AmEx"));
        CARD_OPTIONS.add(new Select.Option(DINERS, "Diners"));
        CARD_OPTIONS.add(new Select.Option(DISCOVER, "Discover"));
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
        super(label);
        setMaxLength(19);
        setSize(19);
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
        cardTypeSelect.addAll(CARD_OPTIONS);
    }

    // --------------------------------------------------------- Public Methods

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
        
        cardType = getContext().getRequest().getParameter(SELECT_NAME);
        
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
        buffer.append(cardTypeSelect);

        return buffer.toString();
    }
}
