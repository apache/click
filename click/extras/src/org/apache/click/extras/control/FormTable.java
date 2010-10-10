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

import java.util.List;

import org.apache.click.control.Button;
import org.apache.click.control.Column;
import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.Table;
import org.apache.click.element.Element;
import org.apache.click.util.HtmlStringBuffer;

import org.apache.click.control.ActionLink;
import org.apache.click.dataprovider.PagingDataProvider;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a FormTable data grid control.
 *
 * <table cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2'src='form-table.png' title='FormTable control'/>
 * </td>
 * </tr>
 * </table>
 *
 * <p/>
 * The FormTable is a composite control which includes a {@link #form} object
 * and an array of {@link FieldColumn} objects.
 * <p/>
 * <b>Please note</b> it is possible to associate FormTable with an external
 * Form through this {@link FormTable#FormTable(java.lang.String, org.apache.click.control.Form) constructor}.
 * <p/>
 * FieldColumn extends the {@link Column} class and includes a {@link Field}
 * object which is uses to render its column value. Each table data cell
 * <tt>&lg;td&gt;</tt> contains a uniquely named form field, which is rendered
 * by the columns field.
 * <p/>
 * When the tables form field data is posted the submitted values are processed
 * by the column field objects using a flyweight style visitor pattern, i.e.
 * the column field instance is reused and processes all the posted values for
 * its column.
 * <p/>
 * After FormTable changes have been submitted their values will be applied to
 * the objects contained in the Tables rows list. If the posted values are
 * invalid for the given field constraints, the field error will be highlighted
 * in the table. Field error messages will be rendered as 'title' attribute
 * tooltip values.
 *
 * <h3>IMPORTANT NOTE</h3>
 * Do not populate the FormTable rowList in the Page's <tt>onRender()</tt> method.
 * <p/>
 * When using the FormTable control its rowList property
 * must be populated before the control is processed so that any submitted data
 * values can be applied to the rowList objects. This generally means that the
 * FormTable rowList should be populated in the page <tt>onInit()</tt> method.
 * Note this is different from the Table control where the rowlist is generally
 * populated in the page <tt>onRender()</tt> method.
 *
 * <h3>FormTable Example</h3>
 *
 * An code example usage of the FormTable is provided below. This example will
 * render the FormTable illustrated in the image above.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> FormTablePage <span class="kw">extends</span> BorderPage {
 *
 *     <span class="kw">private static final int</span> NUM_ROWS = 5;
 *
 *     <span class="kw">public</span> FormTable table = <span class="kw">new</span> FormTable();
 *
 *     <span class="kw">public</span> FormTablePage() {
 *         // Setup customers table
 *         table.addStyleClass(<span class="st">"simple"</span>);
 *         table.setAttribute(<span class="st">"width"</span>, <span class="st">"550px"</span>);
 *         table.getForm().setButtonAlign(Form.ALIGN_RIGHT);
 *
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"id"</span>));
 *
 *         FieldColumn column = <span class="kw">new</span> FieldColumn(<span class="st">"name"</span>, new TextField());
 *         column.getField().setRequired(<span class="kw">true</span>);
 *         table.addColumn(column);
 *
 *         column = <span class="kw">new</span> FieldColumn(<span class="st">"investments"</span>, <span class="kw">new</span> InvestmentSelect());
 *         column.getField().setRequired(<span class="kw">true</span>);
 *         table.addColumn(column);
 *
 *         column = <span class="kw">new</span> FieldColumn(<span class="st">"holdings"</span>, <span class="kw">new</span> NumberField());
 *         column.setAttribute(<span class="st">"style"</span>, <span class="st">"{text-align:right;}"</span>);
 *         table.addColumn(column);
 *
 *         column = <span class="kw">new</span> FieldColumn(<span class="st">"active"</span>, <span class="kw">new</span> Checkbox());
 *         column.setAttribute(<span class="st">"style"</span>, <span class="st">"{text-align:center;}"</span>);
 *         table.addColumn(column);
 *
 *         table.getForm().add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"   OK   "</span>, <span class="kw">this</span>, <span class="st">"onOkClick"</span>));
 *         table.getForm().add(<span class="kw">new</span> Submit(<span class="st">"cancel"</span>, <span class="kw">this</span>, <span class="st">"onCancelClick"</span>));
 *     }
 *
 *     <span class="kw">public void</span> onInit() {
 *         <span class="kw">// Populate table before it is processed</span>
 *         List customers = getCustomerService().getCustomersSortedByName(NUM_ROWS);
 *         table.setRowList(customers);
 *     }
 *
 *     <span class="kw">public boolean</span> onOkClick() {
 *         <span class="kw">if</span> (table.getForm().isValid()) {
 *             getDataContext().commitChanges();
 *         }
 *         <span class="kw">return true</span>;
 *     }
 *
 *     <span class="kw">public boolean</span> onCancelClick() {
 *         getDataContext().rollbackChanges();
 *
 *         List customers = getCustomerService().getCustomersSortedByName(NUM_ROWS);
 *
 *         table.setRowList(customers);
 *         table.setRenderSubmittedValues(<span class="kw">false</span>);
 *
 *         <span class="kw">return true</span>;
 *     }
 * } </pre>
 *
 * Note in this example the <tt>onCancelClick()</tt> button rolls back the
 * changes made to the rowList objects, by reloading their values from the
 * database and having the FormTable not render the submitted values.
 *
 * <a name="form-example" href="#"></a>
 * <h3>Combining Form and FormTable</h3>
 * By default FormTable will create an internal Form to submit its values.
 * <p/>
 * If you would like to integrate FormTable with an externally defined Form,
 * use the {@link FormTable#FormTable(java.lang.String, org.apache.click.control.Form) constructor}
 * which accepts a Form.
 * <p/>
 * Example usage:
 * <pre class="prettyprint">
 * private Form form;
 * private FormTable formTable;
 *
 * public void onInit() {
 *
 *     // LIMITATION: Form only processes its children when the Form is submitted.
 *     // Since FormTable sorting and paging is done via GET requests,
 *     // the Form onProcess method won't process the FormTable.
 *     // To fix this we override the default Form#onProcess behavior and check
 *     // if Form was submitted. If it was not we explicitly process the FormTable.
 *     form = new Form("form") {
 *         public boolean onProcess() {
 *             if (isFormSubmission()) {
 *                 // Delegate to super implementation
 *                 return super.onProcess();
 *             } else {
 *                 // If form is not submitted, explicitly process the table
 *                 return formTable.onProcess();
 *             }
 *         }
 *     };
 *
 *     formTable = new FormTable("formTable", form);
 *     formTable.setPageSize(10);
 *     form.add(formTable);
 *     ...
 * } </pre>
 *
 * @see FieldColumn
 * @see Form
 * @see Table
 */
public class FormTable extends Table {

    private static final long serialVersionUID = 1L;

    /** The table form. */
    protected Form form;

    /** Indicates whether an internal Form should be created, true by default. */
    protected boolean useInternalForm = true;

    /** The render the posted form values flag, default value is true. */
    protected boolean renderSubmittedValues = true;

    // Constructors -----------------------------------------------------------

    /**
     * Create an FormTable for the given name and Form.
     * <p/>
     * If you want to add the FormTable to an externally defined Form, this is
     * the constructor to use.
     * <p/>
     * <b>Please note:</b> if you want to use FormTable with an external Form,
     * see <a href="#form-example">this example</a> which demonstrates a
     * workaround of the <tt>form submit limitation</tt>.
     *
     * @param name the table name
     * @param form the table form
     * @throws IllegalArgumentException if the name is null
     */
    public FormTable(String name, Form form) {
        useInternalForm = false;
        this.form = form;
        init();
        setName(name);
    }

    /**
     * Create a FormTable for the given name.
     * <p/>
     * <b>Note</b> that an internal Form control will automatically be created
     * by FormTable.
     *
     * @param name the table name
     * @throws IllegalArgumentException if the name is null
     */
    public FormTable(String name) {
        init();
        setName(name);
    }

    /**
     * Create a FormTable with no name defined.
     * <p/>
     * <b>Note</b> that an internal Form control will automatically be created
     * by FormTable.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public FormTable() {
        super();
        init();
    }

    // Public Attributes ------------------------------------------------------

    /**
     * Return the form buttons HTML string representation.
     *
     * @return the form buttons HTML string representation
     */
    public String getButtonsHtml() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(256);

        renderButtons(buffer);

        return buffer.toString();
    }

    /**
     * Add the column to the table. The column will be added to the
     * {@link #columns} Map using its name.
     *
     * @see Table#addColumn(Column)
     *
     * @param column the column to add to the table
     * @return the added column
     * @throws IllegalArgumentException if the table already contains a column
     * with the same name
     */
    @Override
    public Column addColumn(Column column) {
        super.addColumn(column);

        if (column instanceof FieldColumn) {
            FieldColumn fieldColumn = (FieldColumn) column;
            if (fieldColumn.getField() != null) {
                fieldColumn.getField().setForm(getForm());
            }
        }

        return column;
    }

    /**
     * Return the form object associated with this FormTable.
     * <p/>
     * The returned Form control will either be an internally created Form
     * instance, or an external instance specified through
     * this {@link FormTable#FormTable(java.lang.String, org.apache.click.control.Form) contructor}.
     *
     * @return the form object
     */
    public Form getForm() {
        if (form == null) {
            form = new Form();
        }
        return form;
    }

    /**
     * Return the HEAD elements for the Control. This method will include the
     * HEAD elements of the contained fields.
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the list of HEAD elements
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            int firstRow = getFirstRow();
            int lastRow = getLastRow();

            for (int i = 0; i < getColumnList().size(); i++) {
                Column column = getColumnList().get(i);
                if (column instanceof FieldColumn) {
                    Field field = ((FieldColumn) column).getField();

                    if (field != null) {
                        for (int j = firstRow; j < lastRow; j++) {
                            field.setName(column.getName() + "_" + j);

                            headElements.addAll(field.getHeadElements());
                        }
                    }
                }
            }
        }
        return headElements;
    }

    /**
     * @see org.apache.click.Control#setName(String)
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    @Override
    public void setName(String name) {
        super.setName(name);

        if (useInternalForm) {
            getForm().setName(getName() + "_form");
        }
    }

    /**
     * Set the parent of the FormTable. Also set the parent of the
     * {@link #getControlLink()} to the {@link #getForm()}.
     *
     * @see org.apache.click.Control#setParent(Object)
     *
     * @param parent the parent of the FormTable
     * @throws IllegalStateException if {@link #name} is not defined
     * @throws IllegalArgumentException if the given parent instance is
     * referencing <tt>this</tt> object: <tt>if (parent == this)</tt>
     */
    @Override
    public void setParent(Object parent) {
        super.setParent(parent);

        if (!useInternalForm) {
            // If FormTable is added to external Form, set the control link
            // parent to the external Form
            getControlLink().setParent(getForm());
        }
    }

    /**
     * Return true if the table will render the submitted form values. By
     * default FormTable renders submitted values.
     *
     * @return true if the table will render the submitted form values
     */
    public boolean getRenderSubmittedValues() {
        return renderSubmittedValues;
    }

    /**
     * Set whether the table should render the submitted form values.
     *
     * @param render set whether the table should render the submitted form values
     */
    public void setRenderSubmittedValues(boolean render) {
        renderSubmittedValues = render;
    }

    /**
     * Set the list of form table rows. Each row can either be a value object
     * (JavaBean) or an instance of a <tt>Map</tt>.
     * <p/>
     * <b>Important</b> ensure you set the rowList before control is processed
     * so posted object changes can be applied. Do not invoke this method via
     * the Page onRender() method, otherwise object updates will not be applied.
     * <p/>
     * Please note the rowList is cleared in table {@link #onDestroy()} method
     * at the end of each request.
     *
     * @param rowList the list of table rows to set
     */
    @Override
    public void setRowList(List rowList) {
        super.setRowList(rowList);
    }

    /**
     * @see org.apache.click.control.Table#setSortedColumn(java.lang.String)
     *
     * @param columnName the name of the sorted column
     */
    @Override
    public void setSortedColumn(String columnName) {
        Field field = (Field) getForm().getFields().get(COLUMN);
        if (field != null) {
            field.setValue(columnName);
        }
        setSorted(false);
        super.setSortedColumn(columnName);
    }

    /**
     * @see org.apache.click.control.Table#setSortedAscending(boolean)
     *
     * @param ascending the ascending sort order status
     */
    @Override
    public void setSortedAscending(boolean ascending) {
        Field field = (Field) getForm().getFields().get(ASCENDING);
        if (field != null) {
            field.setValue(Boolean.toString(ascending));
        }
        setSorted(false);
        super.setSortedAscending(ascending);
    }

    /**
     * @see org.apache.click.control.Table#setPageNumber(int)
     *
     * @param pageNumber set the currently displayed page number
     */
    @Override
    public void setPageNumber(int pageNumber) {
        Field field = (Field) getForm().getFields().get(PAGE);
        if (field != null) {
            field.setValue(Integer.toString(pageNumber));
        }
        super.setPageNumber(pageNumber);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Process the FormTable control. This method will process the submitted
     * form data applying its values to the objects contained in the Tables
     * rowList.
     *
     * @see Table#onProcess()
     *
     * @return true if further processing should continue or false otherwise
     */
    @Override
    public boolean onProcess() {
        ActionLink localControlLink = getControlLink();

        boolean continueProcessing = super.onProcess();

        if (localControlLink.isClicked()) {
            getForm().getField(PAGE).setValue(Integer.toString(getPageNumber()));

            getForm().getField(COLUMN).setValue(getSortedColumn());

            getForm().getField(ASCENDING).setValue(Boolean.toString(isSortedAscending()));

        } else {

            if (getForm().isFormSubmission()) {
                Field pageField = getForm().getField(PAGE);
                pageField.onProcess();
                if (StringUtils.isNotBlank(pageField.getValue())) {
                    setPageNumber(Integer.parseInt(pageField.getValue()));
                }

                Field columnField = getForm().getField(COLUMN);
                columnField.onProcess();
                setSortedColumn(columnField.getValue());

                Field ascendingField = getForm().getField(ASCENDING);
                ascendingField.onProcess();
                setSortedAscending("true".equals(ascendingField.getValue()));

                // Ensure data is retrieved before getRowCount can be called
                getRowList();

                // Range sanity check
                int pageNumber = Math.min(getPageNumber(), getRowCount() - 1);
                pageNumber = Math.max(pageNumber, 0);
                setPageNumber(pageNumber);

                //Have to sort list here before we process each field. Otherwise if
                //sortRowList() is only called in Table.toString(), the fields values set here
                //will not correspond to their rows in the rowList.
                sortRowList();

                int firstRow = 0;
                int lastRow = 0;

                if (getDataProvider() instanceof PagingDataProvider<?>) {
                    lastRow = getRowList().size();
                } else {
                    firstRow = getFirstRow();
                    lastRow = getLastRow();
                }

                List<?> rowList = getRowList();
                List<Column> columnList = getColumnList();

                for (int i = firstRow; i < lastRow; i++) {
                    Object row = rowList.get(i);

                    for (Column column : columnList) {

                        if (column instanceof FieldColumn) {
                            FieldColumn fieldColumn = (FieldColumn) column;
                            Field field = fieldColumn.getField();

                            if (field != null) {
                                HtmlStringBuffer buffer = new HtmlStringBuffer();
                                buffer.append(column.getName());
                                buffer.append("_");
                                buffer.append(i);
                                field.setName(buffer.toString());

                                field.onProcess();

                                if (field.isValid()) {
                                    fieldColumn.setProperty(row, column.getName(),
                                        field.getValueObject());
                                } else {
                                    getForm().setError(getMessage("formtable-error"));
                                }
                            }
                        }
                    }
                }
            }
        }

        return continueProcessing;
    }

    /**
     * @see org.apache.click.control.AbstractControl#getControlSizeEst()
     *
     * @return the estimated rendered control size in characters
     */
    @Override
    public int getControlSizeEst() {
        int bufferSize = 0;
        if (getPageSize() > 0) {
            bufferSize = (getColumnList().size() * 60) * (getPageSize() + 1) + 256;
        } else {
            bufferSize = (getColumnList().size() * 60) * (getRowList().size() + 1) + 256;
        }
        return bufferSize;
    }

    /**
     * Render the HTML representation of the FormTable.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {
        if (useInternalForm) {
            buffer.append(getForm().startTag());

            // Render the Table
            super.render(buffer);

            renderButtons(buffer);

            buffer.append(getForm().endTag());

        } else {
            super.render(buffer);
        }
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Render the Form Buttons to the string buffer.
     * <p/>
     * This method is only invoked if the Form is created by the FormTable,
     * and not when the Form is defined externally.
     *
     * @param buffer the StringBuffer to render to
     */
    protected void renderButtons(HtmlStringBuffer buffer) {
        Form form = getForm();

        List<Button> buttonList = form.getButtonList();
        if (!buttonList.isEmpty()) {
            buffer.append("<table cellpadding=\"0\" cellspacing=\"0\"");
            if (getAttribute("width") != null) {
                buffer.appendAttribute("width", getAttribute("width"));
            }
            buffer.append("><tbody><tr><td");
            buffer.appendAttribute("align", form.getButtonAlign());
            buffer.append(">\n");
            buffer.append("<table class=\"buttons\" id=\"");
            buffer.append(getId());
            buffer.append("-buttons\"><tbody>\n");
            buffer.append("<tr class=\"buttons\">");
            for (Button button : buttonList) {
                buffer.append("<td class=\"buttons\"");
                buffer.appendAttribute("style", form.getButtonStyle());
                buffer.closeTag();

                button.render(buffer);

                buffer.append("</td>");
            }
            buffer.append("</tr>\n");
            buffer.append("</tbody></table>\n");
            buffer.append("</td></tr></tbody></table>\n");
        }
    }

    // Private Methods --------------------------------------------------------

    /**
     * Initialize the FormTable.
     */
    private void init() {
        Form form = getForm();

        // TODO: this wont work, as table control links have unique name
        form.add(new HiddenField(PAGE, String.class));
        form.add(new HiddenField(COLUMN, String.class));
        form.add(new HiddenField(ASCENDING, String.class));

        // If Form is internal add it to table
        if (useInternalForm) {
            add(form);
        }
    }
}
