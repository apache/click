/*
 * Copyright 2006 Malcolm A. Edgar
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
package net.sf.click.extras.control;

import net.sf.click.Context;
import net.sf.click.control.Column;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.util.HtmlStringBuffer;

/**
 *
 *
 *
 * @see Column
 * @see FormTable
 *
 * @author Malcolm Edgar
 */
public class FieldColumn extends Column {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The columns field to process and render. */
    protected Field field;

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
        };
        this.field = field;
    }

    /**
     * Create a FieldColumn with no name defined.
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
     * Set the column editor field
     *
     * @param field the column editor field
     */
    public void setField(Field field) {
        this.field = field;
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render the content within the column table data &lt;td&gt; element.
     *
     * @param row the row object to render
     * @param buffer the string buffer to render to
     * @param context the request context
     * @param rowIndex the index of the current row within the parent table
     */
    protected void renderTableDataContent(Object row, HtmlStringBuffer buffer,
            Context context, int rowIndex) {

        if (getDecorator() != null) {
            Object value = getDecorator().render(row, context);
            if (value != null) {
                buffer.append(value);
            }

        } else {
            Object columnValue = getProperty(row);
            FormTable formTable = (FormTable) getTable();
            Form form = formTable.getForm();

            Field field = getField();

            field.setName(getName() + "_" + rowIndex);
            field.setContext(context);

            if (formTable.getRenderSubmittedValues()
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

            buffer.append(getField());
        }
    }

}
