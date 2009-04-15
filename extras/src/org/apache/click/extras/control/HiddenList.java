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
 * Provides a list of hidden field.
 * <p>
 * Click provides a {@link org.apache.click.control.HiddenField} to render &lt;input type="hidden"&gt;.
 * However it can not render multiple values as a same name.
 * This control can have multiple values and it renders them as multiple hidden field.
 * </p>
 *
 * <h3>HiddenList Examples</h3>
 *
 * <pre class="codeJava">
 * HiddenList hiddenList = <span class="kw">new</span> HiddenList(<span class="st">"hiddenList"</span>);
 * hiddenList.addValue(<span class="st">"001"</span>);
 * hiddenList.addValue(<span class="st">"002"</span>); </pre>
 *
 * This <code>HiddenList</code> would generate following HTML:
 *
 * <pre class="codeHtml">
 * &lt;input type="hidden" name="hiddenList" id="form-hiddenList_1" value="001"/&gt;
 * &lt;input type="hidden" name="hiddenList" id="form-hiddenList_2" value="002"/&gt; </pre>
 *
 * @author Naoki Takezoe
 * @since 2.1.0
 */
public class HiddenList extends Field {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The hidden values. */
    protected List valueObject;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a HiddenList with the given name.
     *
     * @param name the name of the field
     */
    public HiddenList(String name) {
        super(name);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Set the list of hidden values.
     *
     * @param valueObject a list of Strings
     */
    public void setValueObject(Object valueObject) {
        if (!(valueObject instanceof List)) {
            throw new IllegalArgumentException();
        }
        this.valueObject = (List) valueObject;
    }

    /**
     * Returns the list of added values as a <tt>List</tt> of Strings.
     *
     * @return a list of Strings
     */
    public Object getValueObject() {
        if (this.valueObject == null) {
            this.valueObject = new ArrayList();
        }
        return this.valueObject;
    }

    /**
     * This method delegates to {@link #getValueObject()} to return the
     * hidden values as a <tt>java.util.List</tt> of Strings.
     *
     * @return a list of Strings
     */
    public List getValues() {
        return (List) getValueObject();
    }

    /**
     * Add the given value to this <code>HiddenList</code>.
     *
     * @param value the hidden value to add
     */
    public void addValue(String value) {
        getValues().add(value);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method binds the submitted request value to the Field's value.
     */
    public void bindRequestValue() {
        String[] values = getContext().getRequestParameterValues(getName());

        if (values != null) {
            List list = new ArrayList();
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
    public boolean isHidden() {
        return true;
    }

    /**
     * Return the HTML rendered Hidden Fields string.
     *
     * @see Object#toString()
     *
     * @return the HTML rendered Hidden Fields string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        List values = getValues();

        for (int i = 0; i < values.size(); i++) {
            buffer.elementStart("input");
            buffer.appendAttribute("type", "hidden");
            buffer.appendAttribute("name", getName());
            buffer.appendAttribute("id", getId() + "_" + (i + 1));
            buffer.appendAttribute("value", values.get(i));
            buffer.elementEnd();
        }

        return buffer.toString();
    }

}
