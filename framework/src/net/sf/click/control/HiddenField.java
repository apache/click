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
package net.sf.click.control;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

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
 *
 * <h3>HiddenField Example</h3>
 *
 * An example is provided below which uses a hidden field to count the number of
 * times a form is consecutively submitted. The count is displayed in the
 * page template using the model "count" value.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> CountPage <span class="kw">extends</span> Page {
 *
 *     <span class="kw">public</span> Form form = <span class="kw">new</span> Form();
 *     <span class="kw">public</span> Integer count;
 *
 *     <span class="kw">private</span> HiddenField counterField = <span class="kw">new</span> HiddenField(<span class="st">"counterField"</span>, Integer.<span class="kw">class</span>);
 *
 *     <span class="kw">public</span> CountPage() {
 *         form.add(counterField);
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"  OK  "</span>));
 *     }
 *
 *     <span class="kw">public void</span> onGet() {
 *         count = <span class="kw">new</span> Integer(0);
 *         counterField.setValueObject(count);
 *     }
 *
 *     <span class="kw">public void</span> onPost() {
 *         count = (Integer) counterField.getValueObject();
 *         count = <span class="kw">new</span> Integer(count.intValue() + 1);
 *         counterField.setValueObject(count);
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

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The field value Object. */
    protected Object valueObject;

    /** The field value Class. */
    protected Class valueClass;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a HiddenField with the given name and Class.
     *
     * @param name the name of the hidden field
     * @param valueClass the Class of the value Object
     */
    public HiddenField(String name, Class valueClass) {
        if (name == null) {
            throw new IllegalArgumentException("Null name paratemer");
        }
        if (valueClass == null) {
            throw new IllegalArgumentException("Null valueClass paratemer");
        }
        this.name = name;
        this.valueClass = valueClass;
    }

    /**
     * Construct a HiddenField with the given name and value object.
     *
     * @param name the name of the hidden field
     * @param value the value object
     */
    public HiddenField(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name paratemer");
        }
        if (value == null) {
            throw new IllegalArgumentException("Null value paratemer");
        }
        this.name = name;
        this.valueClass = value.getClass();
        setValueObject(value);
    }

    /**
     * Create an HiddenField with no name or Class defined. <b>Please note</b>
     * the HiddenField's name and value Class must be defined before it is
     * valid.
     */
    public HiddenField() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the hiddenfield's html tag: <tt>input</tt>.
     *
     * @see AbstractControl#getTag()
     *
     * @return this controls html tag
     */
    public String getTag() {
        return "input";
    }

    /**
     * Returns true.
     *
     * @see Field#isHidden()
     *
     * @return true
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
     *
     * @return the Field value
     */
    public String getValue() {
        return (getValueObject() != null) ? getValueObject().toString() : "";
    }

    /**
     * @see Field#setValue(String)
     *
     * @param value the Field value
     */
    public void setValue(String value) {
        setValueObject(value);
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
     * Set the registed Class for the Hidden Field value Object.
     *
     * @param valueClass the registered Class for the Hidden Field value Object
     */
    public void setValueClass(Class valueClass) {
        this.valueClass = valueClass;
    }

    /**
     * Return the value Object of the hidden field.
     *
     * @see Field#getValueObject()
     *
     * @return the object representation of the Field value
     */
    public Object getValueObject() {
        return valueObject;
    }

    /**
     * @see Field#setValueObject(Object)
     *
     * @param value the object value to set
     */
    public void setValueObject(Object value) {
        if ((value != null) && (value.getClass() != valueClass)) {
            String msg =
                "The value.getClass(): '" + value.getClass().getName()
                + "' must be the same as the HiddenField valueClass: '"
                + ((valueClass != null) ? valueClass.getName() : "null") + "'";

            throw new IllegalArgumentException(msg);
        }

        this.valueObject = value;
    }

    /**
     * Returns null to ensure no client side JavaScript validation is performed.
     *
     * @return null to ensure no client side JavaScript validation is performed
     */
    public String getValidationJavaScript() {
        return null;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method binds the submitted request value to the Field's value.
     */
    public void bindRequestValue() {

        String aValue = getRequestValue();
        Class valueClass = getValueClass();

        if (valueClass == String.class) {
            setValue(aValue);

        } else if (aValue != null && aValue.length() > 0) {

             if (valueClass == Integer.class) {
                setValueObject(Integer.valueOf(aValue));

            } else if (valueClass == Boolean.class) {
                setValueObject(Boolean.valueOf(aValue));

            } else if (valueClass == Double.class) {
                setValueObject(Double.valueOf(aValue));

            } else if (valueClass == Float.class) {
                setValueObject(Float.valueOf(aValue));

            } else if (valueClass == Long.class) {
                setValueObject(Long.valueOf(aValue));

            } else if (valueClass == Short.class) {
                setValueObject(Short.valueOf(aValue));

            } else if (Date.class.isAssignableFrom(valueClass)) {
                long time = Long.parseLong(aValue);
                setValueObject(new Date(time));

            } else if (Serializable.class.isAssignableFrom(valueClass)) {
                try {
                    setValueObject(ClickUtils.decode(aValue));
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
    }

    /**
     * Render the HTML representation of the HiddenField.
     *
     * @see net.sf.click.Control#render(net.sf.click.util.HtmlStringBuffer)
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {

        buffer.elementStart(getTag());
        buffer.appendAttribute("type", getType());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());

        String valueStr = null;
        Class valueCls = getValueClass();

        if (valueCls == String.class
            || valueCls == Integer.class
            || valueCls == Boolean.class
            || valueCls == Double.class
            || valueCls == Float.class
            || valueCls == Long.class
            || valueCls == Short.class) {

            valueStr = String.valueOf(getValue());

        } else if (getValueObject() instanceof Date) {
            valueStr = String.valueOf(((Date) getValueObject()).getTime());

        } else if (getValueObject() instanceof Serializable) {
            try {
                valueStr = ClickUtils.encode(getValueObject());
            } catch (IOException ioe) {
                String msg =
                    "could not encode value for hidden field: "
                    + getValueObject();
                throw new RuntimeException(msg, ioe);
            }
        } else {
            valueStr = getValue();
        }

        buffer.appendAttribute("value", valueStr);

        buffer.elementEnd();
    }
}
