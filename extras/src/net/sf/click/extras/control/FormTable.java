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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.click.Context;
import net.sf.click.control.Button;
import net.sf.click.control.Column;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Table;
import net.sf.click.util.HtmlStringBuffer;
import ognl.Ognl;

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
 * invalid for the given field constraints, the field error will be highlited
 * in the table. Field error messages will be rendered as 'title' attribute
 * tooltip values.
 * <p/>
 * When using the FormTable control its rowList property must be populated
 * before the control is processed so that any submitted data values can be
 * applied to the rowList objects. This generally means that the FormTable
 * rowList should be populated in the page <tt>onInit()</tt> method.
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
 *         table.setAttribute(<span class="st">"class"</span>, <span class="st">"simple"</span>);
 *         table.setAttribute(<span class="kw">"width"</span>, <span class="kw">"550px"</span>);
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
 * Note is this example the <tt>onCancelClick()</tt> button rolls back the
 * changes made to the rowList objects, by reloading their values from the
 * database and having the FormTable not render the submitted values.
 *
 * @see FieldColumn
 * @see Form
 * @see Table
 */
public class FormTable extends Table {

    private static final long serialVersionUID = 1L;

    /** The table form. */
    protected Form form = new Form();

    /** The render the posted form values flag, default value is true. */
    protected boolean renderSubmittedValues = true;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an FormTable for the given name.
     *
     * @param name the table name
     * @throws IllegalArgumentException if the name is null
     */
    public FormTable(String name) {
        setName(name);
    }

    /**
     * Create a FormTable with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public FormTable() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

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
     * @throws IllegalArgumentException if the table already contains a column
     * with the same name
     */
    public void addColumn(Column column) {
        super.addColumn(column);
        if (column instanceof FieldColumn) {
            FieldColumn fieldColumn = (FieldColumn) column;
            fieldColumn.getField().setForm(getForm());
        }
    }

    /**
     * Set the context object.
     *
     * @see net.sf.click.Control#setContext(net.sf.click.Context)
     *
     * @param context the contex to set
     */
    public void setContext(Context context) {
        super.setContext(context);

        addControl(getForm());
        getForm().setName(getName() + "_form");
        getForm().setContext(context);

        form.add(new HiddenField(PAGE, String.class));
        form.add(new HiddenField(COLUMN, String.class));
    }

    /**
     * Return the form object.
     *
     * @return the form object
     */
    public Form getForm() {
        return form;
    }

    /**
     * Return the HTML head element import string.
     *
     * @see net.sf.click.Control#getHtmlImports()
     *
     * @return the HTML head element import string
     */
    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(255);

        buffer.append(super.getHtmlImports());
        buffer.append(getForm().getHtmlImports());

        return buffer.toString();
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

    // --------------------------------------------------------- Public Methods

    /**
     * Process the FormTable control. This method will process the submitted
     * form data applying its values to the objects contained in the Tables
     * rowList.
     *
     * @see Table#onProcess()
     *
     * @return true if further processing should continue or false otherwise
     */
    public boolean onProcess() {
        if (getForm().isFormSubmission()) {
            Field pageField = getForm().getField(PAGE);
            pageField.onProcess();
            if (pageField.getValue().length() > 0) {
                setPageNumber(Integer.parseInt(pageField.getValue()));
            }

            Field columnField = getForm().getField(COLUMN);
            columnField.onProcess();
            setSortedColumn(columnField.getValue());


            // Range sanity check
            int pageNumber = Math.min(getPageNumber(), getRowList().size() - 1);
            pageNumber = Math.max(pageNumber, 0);
            setPageNumber(pageNumber);

            int firstRow = getFirstRow();
            int lastRow = getLastRow();

            List rowList = getRowList();
            List columnList = getColumnList();

            Map ognlContext = new HashMap();

            for (int i = firstRow; i < lastRow; i++) {
                Object row = rowList.get(i);

                for (int j = 0; j < columnList.size(); j++) {

                    Column column = (Column) columnList.get(j);

                    if (column instanceof FieldColumn) {
                        Field field = ((FieldColumn) column).getField();

                        field.setName(column.getName() + "_" + i);
                        field.setContext(context);

                        field.onProcess();

                        if (field.isValid()) {
                            try {
                                Ognl.setValue(column.getName(),
                                        ognlContext,
                                        row,
                                        field.getValueObject());

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            getForm().setError(field.getError());
                        }
                    }
                }
            }
        }

        return super.onProcess();
    }

    /**
     * Return a HTML rendered FormTable string.
     *
     * @see Object#toString()
     *
     * @return a HTML rendered FormTable string
     */
    public String toString() {
        if (getContext() == null) {
            throw new IllegalStateException("context is not defined");
        }

        int bufferSize =
            (getColumnList().size() * 60) * (getRowList().size() + 1)
            + 256;

        HtmlStringBuffer buffer = new HtmlStringBuffer(bufferSize);

        buffer.append(getForm().startTag());

        buffer.append(super.toString());

        renderButtons(buffer);

        buffer.append(getForm().endTag());

        return buffer.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render the Form Buttons to the string buffer.
     *
     * @param buffer the StringBuffer to render to
     */
    protected void renderButtons(HtmlStringBuffer buffer) {
        Form form = getForm();

        if (!form.getButtonList().isEmpty()) {
            buffer.append("<table cellpadding=\"0\" cellspacing=\"0\"");
            if (getAttribute("width") != null) {
                buffer.appendAttribute("width", getAttribute("width"));
            }
            buffer.append("><tr><td");
            buffer.appendAttribute("align", form.getButtonAlign());
            buffer.append(">\n");
            buffer.append("<table class=\"buttons\" id=\"");
            buffer.append(getId());
            buffer.append("-buttons\">\n");
            buffer.append("<tr class=\"buttons\">");
            for (int i = 0, size = form.getButtonList().size(); i < size; i++) {
                buffer.append("<td class=\"buttons\"");
                buffer.appendAttribute("style", form.getButtonStyle());
                buffer.closeTag();

                Button button = (Button) form.getButtonList().get(i);
                buffer.append(button);

                buffer.append("</td>");
            }
            buffer.append("</tr>\n");
            buffer.append("</table>\n");
            buffer.append("</td></tr></table>\n");
        }
    }

}
