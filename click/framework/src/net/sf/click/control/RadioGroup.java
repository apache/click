/*
 * Copyright 2005 Malcolm A. Edgar
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
 *
 * <table class='htmlHeader'>
 * <tr>
 * <td>Radio Group</td>
 * <td>
 * <input type='radio' name='header' value='R'>Red </input>
 * <input type='radio' name='header' checked value='G'>Green </input>
 * <input type='radio' name='header' value='B'>Blue</input>
 * </td>
 * </tr>
 * </table>
 *
 * The RadioGroup control provides a Field for containing grouped Radio buttons.
 * Radio controls added to a RadioGroup will have their name set to that of
 * the RadioGroup. This will ensure the buttons will toggle together so that
 * only one button is selected at a time.
 * <p/>
 * The example below illustrates a RadioGroup being added to a form.
 *
 * <div class="code">public class Purchase extends Page {
 *     Form form;
 *     RadioGroup radioGroup;
 *     ..
 *
 *     public void onInit() {
 *         Form form = new Form("form", getContext());
 *         ..
 *
 *         radioGroup = new RadioGroup("Packaging", getContext());
 *         radioGroup.add(new Radio("STD", "Standard "));
 *         radioGroup.add(new Radio("PRO", "Protective "));
 *         radioGroup.add(new Radio("GFT", "Gift Wrap "));
 *         radioGroup.setValue("STD");
 *         radioGroup.setVerticalLayout(true);
 *         form.add(radioGroup);
 *
 *         ..
 *     }
 * }
 * </div>
 *
 * This radio group field would be render as:
 *
 * <table class='htmlExample'>
 *  <tr>
 *   <td>Packaging</td>
 *   <td>
 *    <input type='radio' name='group' checked value='STD'>Standard</input><br>
 *    <input type='radio' name='group' value='PRO'>Protective</input><br>
 *    <input type='radio' name='group' value='GFT'>Gift Wrap</input>
 *   </td>
 *  </tr>
 * </table>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @see Radio
 *
 * @author Malcolm
 */
public class RadioGroup extends Field {

    // ----------------------------------------------------- Instance Variables

    /** The list of Radio controls. */
    protected List radioList;

    /**
     * The layout is vertical flag (default false). If the layout is vertical
     * each Radio controls is rendered on a new line using the &lt;br&gt;
     * tag.
     */
    protected boolean isVerticalLayout = true;

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
     * Return true if the radio control layout is vertical.
     *
     * @return true if the radio control layout is vertical
     */
    public boolean isVerticalLayout() {
        return isVerticalLayout;
    }

    /**
     * Set the vertical radio control layout flag.
     *
     * @param vertical the vertical layout flag
     */
    public void setVerticalLayout(boolean vertical) {
        isVerticalLayout = vertical;
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

        String value = getValue();

        for (int i = 0; i < size; i++) {
            Radio radio = (Radio) getRadioList().get(i);

            if (value != null && value.length() > 0) {
                if (radio.getValue().equals(value)) {
                    radio.setChecked(true);
                } else {
                    radio.setChecked(false);
                }
            }

            buffer.append(radio.toString());

            if (!isVerticalLayout() && (i < size - 1)) {
                buffer.append("<br>");
            }
        }

        return buffer.toString();
    }
}
