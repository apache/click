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

import net.sf.click.Context;

/**
 * Provides a RadioGroup control.
 * <p/>
 * <table class='form'><tr>
 * <td>Radio Group</td>
 * <td><input type='radio' title='Radio Control'/></td>
 * </tr></table>
 * <p/>
 * TODO: radiogroup javadoc + examples
 * 
 * <p/>
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification" 
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 * 
 * @author Malcolm
 */
public class RadioGroup extends Field {
    
    // ----------------------------------------------------- Instance Variables
    
    /** The list of Radio controls. */
    protected List radioList;

    /** 
     * The layout is horizontal flag (default true). If the layout is vertical
     * each Radio controls is rendered on a new line using the &lt;br&gt; 
     * tag.
     */
    protected boolean isLayoutHorizontal = true;
    
    // ----------------------------------------------------------- Constructors

    /**
     * Create a radio group with the given label and context.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     * @param context the request context
     */
    public RadioGroup(String label, Context context) {
        super(label);
        setContext(context);
    }

    // ------------------------------------------------------ Public Attributes
    
    /**
     * Add the given radio to the radio group. When the radio is added to the 
     * group its name is set to that of the radio group and its context is 
     * set.
     * 
     * @param radio the radio control to add to the radio group
     */
    public void add(Radio radio) {
        if (radio == null) {
            throw new IllegalArgumentException("Null radio parameter");
        }
        
        radio.setName(getName());
        radio.setContext(getContext());
        getRadioList().add(radio);
    }
    
    /**
     * Return true if the radio control layout is horizontal.
     * 
     * @return true if the radio control layout is horizontal
     */
    public boolean isLayoutHorizontal() {
        return isLayoutHorizontal;
    }
    
    /**
     * Set the horizontal radio control layout flag.
     * 
     * @param horizontal the horizontal layout flag
     */
    public void setLayoutHorizontal(boolean horizontal) {
        isLayoutHorizontal = horizontal;
    }
    
    /**
     * Return the list of radio controls.
     * 
     * @return the list of radio controls
     */
    public List getRadioList() {
        if (radioList == null) {
            radioList = new ArrayList();
        }
        return radioList;
    }

    /**
     * Return the input field type of: &nbsp; null
     *
     * @return the input field type &nbsp; null
     */
    public String getType() {
        return null;
    }
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Process the request Context setting the checked value and invoking 
     * the controls listener if defined.
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        
        value = getRequestValue();
        
        boolean continueProcessing = true;
        for (int i = 0, size = getRadioList().size(); i < size; i++) {
            Radio radio = (Radio) getRadioList().get(i);
            continueProcessing = radio.onProcess();
            if (!continueProcessing) {
                return false;
            }
        }
        
        if (value.length() > 0) {
            return invokeListener();
        } else {
            return true;
        }
    }
    
    /**
     * Return the HTML rendered RadioGroup string.
     *
     * @see Object#toString()
     */
    public String toString() {
        final int size = getRadioList().size();
        
        StringBuffer buffer = new StringBuffer(size * 30);

        for (int i = 0; i < size; i++) {
            Radio radio = (Radio) getRadioList().get(i);
            buffer.append(radio.toString());
            if (!isLayoutHorizontal() && (i < size - 1)) {
                buffer.append("<br>");
            }
        }

        return buffer.toString();
    }
}
