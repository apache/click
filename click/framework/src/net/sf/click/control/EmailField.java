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

import net.sf.click.Context;

/**
 * Provides a Email Field control: &nbsp; &lt;input type='text'&gt;.
 * <p/>
 * <table class='form'><tr>
 * <td>Email Field</td>
 * <td><input type='text' size='30' value='medgar@mycorp.com' title='EmailField Control'/></td>
 * </tr></table>
 * <p/>
 * EmailField will validate the email when the control is processed and invoke
 * the control listener if the email format is valid. 
 * <p/>
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification" 
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class EmailField extends TextField {

    /**
     * Construct an Email Field with the given label and a default size of 30.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field.
     */
    public EmailField(String label) {
        super(label);
        size = 30;
    }

    /**
     * Process the EmailField submission. If the Email value is valid the 
     * controls listener will be invoked.
     * <p/>
     * A field error message is displayed if a validation error occurs. 
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>net.sf.click.control.MessageProperties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>email-format-error</li>
     * <li>field-maxlength-error</li>
     * <li>field-minlength-error</li>
     * <li>field-required-error</li>
     * </ul></blockquote>
     * 
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        Context context = getContext();
        
        value = context.getRequest().getParameter(name);
        if (value != null) {
            value = value.trim();
        } else {
            value = "";
        }

        int length = value.length();
        if (length > 0) {
            if (getMinLength() > 0 && length < getMinLength()) {
                Object[] args = new Object[] { getLabel(), new Integer(getMinLength()) };
                error = getMessage(context, "field-minlength-error", args);
                return true;
            }
            
            if (getMaxLength() > 0 && length > getMaxLength()) {
                Object[] args = new Object[] { getLabel(), new Integer(getMaxLength()) };
                error = getMessage(context, "field-maxlength-error", args);
                return true;
            }

            int index = value.indexOf("@");
            if (index < 1 || index == length -1) {
                error = getMessage(context, "email-format-error", getLabel());
                return true;
            }
            if (!Character.isLetterOrDigit(value.charAt(0))) {
                error = getMessage(context, "email-format-error", getLabel());
                return true;
            }
            if (!Character.isLetterOrDigit(value.charAt(length - 1))) {
                error = getMessage(context, "email-format-error", getLabel());
                return true;
            }

            return invokeListener();

        } else {
            if (required) {
                error = getMessage(context, "field-required-error", getLabel());
            }
        }
        
        return true;
    }

}
