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

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import net.sf.click.util.ClickUtils;

/**
 * Provides a Hidden Field control: &nbsp; &lt;input type='hidden'&gt;.
 * <p/>
 * The HiddenField control is useful for storing state information in a Form,
 * such as object ids, instead of using the Session object. This control is
 * capable of supporting the following classes:<blockquote><ul>
 * <li>Boolean</li>
 * <li>Date</li>
 * <li>Double</li>
 * <li>Float</li>
 * <li>Integer</li>
 * <li>Long</li>
 * <li>Short</li>
 * <li>String</li>
 * <li>Serializable</li>
 * </ul></blockquote>
 * <p/>
 * Serializable non-primitive objects will be serialized, compressed and
 * Base64 encoded, using {@link net.sf.click.util.ClickUtils#encode(Object)}
 * method, and decoded using the corresponding
 * {@link net.sf.click.util.ClickUtils#decode(String)} method.
 * <p/>
 * An example is provided below which uses a hidden field to count the number of
 * times a form is consecutively submitted. The count is displayed in the
 * page template using the model "count" value.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> CountPage <span class="kw">extends</span> Page {
 *
 *     HiddenField counterField;
 *
 *     <span class="kw">public void</span> onInit() {
 *         Form form = <span class="kw">new</span> Form(<span class="st">"form"</span>);
 *         addControl(form);
 *
 *         counterField = <span class="kw">new</span> HiddenField(<span class="st">"counterField"</span>, Integer.<span class="kw">class</span>);
 *         form.add(counterField);
 *
 *         form.add(<span class="kw">new</span> Submit(<span class="st">" OK "</span>));
 *     }
 *
 *     <span class="kw">public void</span> onGet() {
 *         Integer count = <span class="kw">new</span> Integer(0);
 *
 *         counterField.setValue(count);
 *         addModel(<span class="st">"count"</span>, count);
 *     }
 *
 *     <span class="kw">public void</span> onPost() {
 *         Integer count = (Integer) counterField.getValueObject();
 *
 *         count = <span class="kw">new</span> Integer(count.intValue() + 1);
 *
 *         counterField.setValue(count);
 *         addModel(<span class="st">"count"</span>, count);
 *     }
 * } </pre>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class HiddenField extends Field {

    // ----------------------------------------------------- Instance Variables

    /** The field value Object. */
    protected Object valueObject;

    /** The field value Class. */
    protected final Class valueClass;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a HiddenField with the given name and Class.
     *
     * @param name the name of the hidden field
     * @param valueClass the Class of the value Object
     */
    public HiddenField(String name, Class valueClass) {
        this.name = name;
        this.valueClass = valueClass;
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Returns true.
     *
     * @see Field#isHidden()
     */
    public boolean isHidden() {
        return true;
    }

    /**
     * Return the input type: 'hidden'.
     *
     * @return the input type: 'hidden'
     */
    public String getType() {
        return "hidden";
    }

    /**
     * @see Field#getValue()
     */
    public String getValue() {
        return (valueObject != null) ? valueObject.toString() : "";
    }

    /**
     * @see Field#setValue(Object)
     */
    public void setValue(Object value) {
        if ((value != null) && (value.getClass() != valueClass)) {
            String msg = "The value.getClass() must be the same as the " +
                         "HiddenField valueClass: " +
                         ((valueClass != null) ? valueClass.getName() : "null");

            throw new IllegalArgumentException(msg);
        }

        this.valueObject = value;
    }

    /**
     * Return the registed Class for the Hidden Field value Object.
     *
     * @return the registered Class for the Hidden Field value Object
     */
    public Class getValueClass() {
        return valueClass;
    }

    /**
     * Return the value Object.
     *
     * @return the value Object
     */
    public Object getValueObject() {
        return valueObject;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the HiddenField submission. If the value can be parsed any
     * control listener will be invoked.
     * <p/>
     * The value object will be set with the HiddenField's string value.
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        String aValue = getContext().getRequest().getParameter(name);
        Class valueClass = getValueClass();

        if (valueClass == String.class) {
            setValue(aValue);

        } else if (aValue != null && aValue.length() > 0) {

             if (valueClass == Integer.class) {
                setValue(Integer.valueOf(aValue));

            } else if (valueClass == Boolean.class) {
                setValue(Boolean.valueOf(aValue));

            } else if (valueClass == Double.class) {
                setValue(Double.valueOf(aValue));

            } else if (valueClass == Float.class) {
                setValue(Float.valueOf(aValue));

            } else if (valueClass == Long.class) {
                setValue(Long.valueOf(aValue));

            } else if (valueClass == Short.class) {
                setValue(Short.valueOf(aValue));

            } else if (Date.class.isAssignableFrom(valueClass)) {
                long time = Long.parseLong(aValue);
                setValue(new Date(time));

            } else if (Serializable.class.isAssignableFrom(valueClass)) {
                try {
                    setValue(ClickUtils.decode(aValue));
                } catch (ClassNotFoundException cnfe) {
                    String msg =
                        "could not decode value for hidden field: " + aValue;
                    throw new RuntimeException(msg, cnfe);
                } catch (IOException ioe) {
                    String msg =
                        "could not decode value for hidden field: " + aValue;
                    throw new RuntimeException(msg, ioe);
                }
            } else {
                setValue(aValue);
            }
        } else {
            setValue(null);
        }

        return invokeListener();
    }

    /**
     * Return the HTML rendered Hidden Field string.
     *
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(20);

        buffer.append("<input type='");
        buffer.append(getType());
        buffer.append("' name='");
        buffer.append(getName());
        buffer.append("' value='");
        if (valueClass == String.class
            || valueClass == Integer.class
            || valueClass == Boolean.class
            || valueClass == Double.class
            || valueClass == Float.class
            || valueClass == Long.class
            || valueClass == Short.class) {

            buffer.append(getValue());

        } else if (getValueObject() instanceof Date) {
            buffer.append(((Date)getValueObject()).getTime());

        } else if (getValueObject() instanceof Serializable) {
            try {
                buffer.append(ClickUtils.encode(getValueObject()));
            } catch (IOException ioe) {
                String msg =
                    "could not encode value for hidden field: " + getValueObject();
                throw new RuntimeException(msg, ioe);
            }
        } else {
            buffer.append(getValue());
        }
        buffer.append("'>");

        return buffer.toString();
    }
}

