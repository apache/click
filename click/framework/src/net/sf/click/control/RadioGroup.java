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
import java.util.Iterator;
import java.util.List;

import net.sf.click.Context;
import net.sf.click.Control;

/**
 * Provides a RadioGroup control.
 *
 * <table class='htmlHeader' cellspacing='6'>
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
 * <pre class="codeJava">
 * <span class="kw">public class</span> Purchase <span class="kw">extends</span> Page {
 *
 *     Form form;
 *     RadioGroup radioGroup;
 *     ..
 *
 *     <span class="kw">public void</span> onInit() {
 *         Form form = <span class="kw">new</span> Form(<span class="st">"form"</span>, getContext());
 *         ..
 *
 *         radioGroup = <span class="kw">new</span> RadioGroup(<span class="st">"Packaging"</span>, getContext());
 *         radioGroup.add(<span class="kw">new</span> Radio(<span class="st">"STD"</span>, <span class="st">"Standard "</span>));
 *         radioGroup.add(<span class="kw">new</span> Radio(<span class="st">"PRO"</span>, <span class="st">"Protective "</span>));
 *         radioGroup.add(<span class="kw">new</span> Radio(<span class="st">"GFT"</span>, <span class="st">"Gift Wrap "</span>));
 *         radioGroup.setValue(<span class="st">"STD"</span>);
 *         radioGroup.setVerticalLayout(<span class="kw">true</span>);
 *         form.add(radioGroup);
 *
 *         ..
 *     }
 * } </pre>
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
 * @author Malcolm Edgar
 * @version $Id$
 */
public class RadioGroup extends Field {

    private static final long serialVersionUID = 2978451472698468194L;

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
     * Create a RadioGroup with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     */
    public RadioGroup(String label) {
        super(label);
    }

    /**
     * Create a RadioGroup field with no label/name or context defined,
     * <b>please note</b> the control's name and context must be defined before
     * it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public RadioGroup() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Add the given radio to the radio group. When the radio is added to the
     * group its name is set to that of the radio group and its context is
     * set.
     *
     * @param radio the radio control to add to the radio group
     * @throws IllegalArgumentException if the radio parameter is null
     */
    public void add(Radio radio) {
        if (radio == null) {
            throw new IllegalArgumentException("Null radio parameter");
        }

        radio.setName(getName());
        getRadioList().add(radio);
        if (getContext() != null) {
            radio.setContext(getContext());
        }
    }

    /**
     * @see Control#setContext(Context)
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;

        if (hasRadios()) {
            for (Iterator i = getRadioList().iterator(); i.hasNext();) {
                Object object = i.next();
                if (object instanceof Control) {
                    ((Control)object).setContext(context);
                }
            }
        }
    }

    /**
     * @see Field#setForm(Form)
     */
    public void setForm(Form form) {
        super.setForm(form);
        if (hasRadios()) {
            for (int i = 0, size = getRadioList().size(); i < size; i++) {
                Radio radio = (Radio) getRadioList().get(i);
                radio.setForm(getForm());
            }
        }
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

    /**
     * Return true if RadioGroup has Radio controls, or false otherwise.
     *
     * @return true if RadioGroup has Radio controls, or false otherwise
     */
    public boolean hasRadios() {
        return radioList != null && !radioList.isEmpty();
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
            if (!continueProcessing && validate()) {
                return false;
            }
        }

        if (!validate()) {
            return true;
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
