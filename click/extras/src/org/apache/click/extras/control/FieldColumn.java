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

import java.util.HashMap;
import java.util.Map;

import ognl.OgnlException;

import org.apache.click.Context;
import org.apache.click.control.Column;
import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.click.util.PropertyUtils;

/**
 * Provides a FieldColumn for rendering table data cells.
 *
 * <table cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2'src='form-table.png' title='FormTable control'/>
 * </td>
 * </tr>
 * </table>
 * <p/>
 *
 * The FieldColumn class is used to define the table cell data
 * <tt>&lt;td&gt;</tt> editors. Each FieldColumn should include a {@link Field}
 * instance.
 * <p/>
 * If the FieldColumn is contained in a standard Table it will render the all
 * the table column data cells using its single field instance.
 * <p/>
 * If the FieldColumn is contained in a FormTable it will both render the column
 * data cells and process any posted column data values using the field instance.
 * Changes to the tables data objects will be available in the Table rowList
 * property.
 * <p/>
 * Please see the {@link FormTable} Javadoc for usage examples.
 *
 * @see Column
 * @see FormTable
 */
public class FieldColumn extends Column {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The columns field to process and render. */
    protected Field field;

    /** The ognl context map. */
    private transient Map<?, ?> ognlContext;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a table field column with the given property name. The table
     * header title will be set as the capitalized property name.
     *
     * @param name the name of the property to render
     */
    public FieldColumn(String name) {
        super(name);
    }

    /**
     * Create a table field column with the given property name and field.
     *
     * @param name the name of the property to render
     * @param field the field to process and render
     */
    public FieldColumn(String name, Field field) {
        super(name);
        if (field == null) {
            throw new IllegalArgumentException("Null field parameter");
        }
        this.field = field;
    }

    /**
     * Create a table field column with the given property name and header title.
     *
     * @param name the name of the property to render
     * @param title the column header title to render
     */
    public FieldColumn(String name, String title) {
        super(name, title);
    }

    /**
     * Create a table field column with the given property name, header title
     * and field.
     *
     * @param name the name of the property to render
     * @param title the column header title to render
     * @param field the field to process and render
     */
    public FieldColumn(String name, String title, Field field) {
        super(name, title);
        if (field == null) {
            throw new IllegalArgumentException("Null field parameter");
        }
        this.field = field;
    }

    /**
     * Create a FieldColumn with no name or field defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public FieldColumn() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the column editor field.
     *
     * @return the column editor field
     */
    public Field getField() {
        return field;
    }

    /**
     * Set the column editor field.
     *
     * @param field the column editor field
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * Set a row value based on the given property name and value. The given row
     * can either be a Java Object or a Map instance.
     * <p/>
     * If the row is a Java Object this method uses OGNL to set the value of the
     * row based on the property name. Property names can be specified using a
     * path expression e.g: "person.address.city".
     * <p/>
     * If the row object is a <tt>Map</tt> this method will attempt to set the
     * map's key/value pair based on the property name and value. The key
     * of the map is matched against the property name. If a key matches the
     * property name, the value will be copied to the map.
     * <p/>
     * <b>Note:</b> you can access the underlying Field using {@link #getField()}.
     *
     * @param row the row object to obtain the property from
     * @param propertyName the name of the property
     * @param value the row object property value
     * @throws RuntimeException if an error occurred obtaining the property
     */
    @SuppressWarnings("unchecked")
    public void setProperty(Object row, String propertyName, Object value) {
        if (row instanceof Map<?, ?>) {
            Map<Object, Object> map = (Map<Object, Object>) row;
            if (map.containsKey(propertyName)) {
                map.put(propertyName, value);
            }

        } else {
            if (ognlContext == null) {
                ognlContext = new HashMap<Object, Object>();
            }

            try {
                PropertyUtils.setValueOgnl(row,
                                           propertyName,
                                           value,
                                           ognlContext);

            } catch (OgnlException oe) {
                throw new RuntimeException(oe);
            }
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render the content within the column table data &lt;td&gt; element. If
     * the FieldColumn has a decorator defined this will be used instead to
     * render the table data cell.
     *
     * @param row the row object to render
     * @param buffer the string buffer to render to
     * @param context the request context
     * @param rowIndex the index of the current row within the parent table
     */
    @Override
    protected void renderTableDataContent(Object row, HtmlStringBuffer buffer,
            Context context, int rowIndex) {

        Object columnValue = getProperty(row);

        Field field = getField();

        //fallback to super implementation
        if (field == null) {
            super.renderTableDataContent(row, buffer, context, rowIndex);
            return;
        }

        field.setName(getName() + "_" + rowIndex);

        if (getTable() instanceof FormTable) {
            FormTable formTable = (FormTable) getTable();
            Form form = formTable.getForm();

            if (formTable.getRenderSubmittedValues()
                && !formTable.getControlLink().isClicked()
                && form.isFormSubmission()) {

                field.onProcess();

                if (field.getError() != null) {
                    field.setTitle(field.getError());
                }

            } else {
                field.setTitle(null);
                field.setError(null);
                field.setValueObject(columnValue);
            }

            if (getDecorator() != null) {
                Object value = getDecorator().render(row, context);
                if (value != null) {
                    buffer.append(value);
                }

            } else {
                getField().render(buffer);
            }

        } else {
            field.setValueObject(columnValue);

            if (getDecorator() != null) {
                Object value = getDecorator().render(row, context);
                if (value != null) {
                    buffer.append(value);
                }

            } else {
                getField().render(buffer);
            }
        }

        field.setValue(null);
    }

}
