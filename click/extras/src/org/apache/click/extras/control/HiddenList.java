/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.click.control.Field;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a control for rendering a list of
 * {@link org.apache.click.control.HiddenField Hidden Fields}:
 * &nbsp; &lt;input type='hidden'&gt;.
 * <p/>
 * Click also provides the {@link org.apache.click.control.HiddenField} to render
 * &lt;input type="hidden"&gt;, however HiddenField can not render multiple values
 * under the same name.
 * <p/>
 * HiddenList supports multiple values under the same name by rendering
 * the values as multiple hidden fields.
 *
 * <h3>HiddenList Examples</h3>
 *
 * <pre class="prettyprint">
 * HiddenList hiddenList = new HiddenList("customerId");
 * hiddenList.addValue("123");
 * hiddenList.addValue("678"); </pre>
 *
 * This <code>HiddenList</code> would generate following HTML:
 *
 * <pre class="prettyprint">
 * &lt;input type="hidden" name="customerId" id="form-customerId_1" value="123"/&gt;
 * &lt;input type="hidden" name="customerId" id="form-customerId_2" value="678"/&gt; </pre>
 *
 * @since 2.1.0
 */
public class HiddenList extends Field {

    private static final long serialVersionUID = 1L;

    // Instance Variables -----------------------------------------------------

    /** The hidden values. */
    protected List<String> valueObject;

    // Constructors -----------------------------------------------------------

    /**
     * Create a HiddenList with the given name.
     *
     * @param name the name of the field
     */
    public HiddenList(String name) {
        super(name);
    }

    /**
     * Create a default HiddenList.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public HiddenList() {
    }

    // Public Attributes ------------------------------------------------------

    /**
     * Set the list of hidden values.
     *
     * @param valueObject a list of Strings
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setValueObject(Object valueObject) {
        if (!(valueObject instanceof List<?>)) {
            throw new IllegalArgumentException("the valueObject must be a"
                + " List.");
        }
        this.valueObject = (List<String>) valueObject;
    }

    /**
     * Returns the list of added values as a <tt>java.util.List</tt> of Strings.
     *
     * @return a list of Strings
     */
    @Override
    public Object getValueObject() {
        if (this.valueObject == null) {
            this.valueObject = new ArrayList<String>();
        }
        return this.valueObject;
    }

    /**
     * This method delegates to {@link #getValueObject()} to return the
     * hidden values as a <tt>java.util.List</tt> of Strings.
     *
     * @return a list of Strings
     */
    @SuppressWarnings("unchecked")
    public List<String> getValues() {
        return (List<String>) getValueObject();
    }

    /**
     * Add the given value to this <code>HiddenList</code>.
     *
     * @param value the hidden value to add
     */
    public void addValue(String value) {
        getValues().add(value);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * This method binds the submitted request values to the HiddenList values.
     * <p/>
     * <b>Please note:</b> while it is possible to explicitly bind the field
     * value by invoking this method directly, it is recommended to use the
     * "<tt>bind</tt>" utility methods in {@link org.apache.click.util.ClickUtils}
     * instead. See {@link org.apache.click.util.ClickUtils#bind(org.apache.click.control.Field)}
     * for more details.
     */
    @Override
    public void bindRequestValue() {
        String[] values = getContext().getRequestParameterValues(getName());

        if (values != null) {
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < values.length; i++) {
                list.add(values[i]);
            }
            setValueObject(list);
        }
    }

    /**
     * Returns true.
     *
     * @see Field#isHidden()
     *
     * @return true
     */
    @Override
    public boolean isHidden() {
        return true;
    }

    /**
     * Render the HTML representation of the HiddenField.
     *
     * @see org.apache.click.Control#render(org.apache.click.util.HtmlStringBuffer)
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {
        List<String> values = getValues();

        for (int i = 0; i < values.size(); i++) {
            buffer.elementStart("input");
            buffer.appendAttribute("type", "hidden");
            buffer.appendAttribute("name", getName());
            buffer.appendAttribute("id", getId() + "_" + (i + 1));
            buffer.appendAttribute("value", values.get(i));
            buffer.elementEnd();
        }
    }

}
