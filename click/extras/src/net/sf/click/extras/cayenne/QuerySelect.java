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
package net.sf.click.extras.cayenne;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.click.control.Decorator;
import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.util.PropertyUtils;

import org.objectstyle.cayenne.DataRow;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.query.SelectQuery;

/**
 * Provides a Cayenne Query Select control: &nbsp; &lt;select&gt;&lt;/select&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Query Select</td>
 * <td>
 * <select title='QuerySelect Control'>
 * <option value='MR'>Mr</option>
 * <option value='MRS'>Mrs</option>
 * <option value='MS'>Ms</option>
 * <option value='MISS'>Miss</option>
 * <option value='DR'>Dr</option>
 * </select>
 * </td>
 * </tr>
 * </table>
 *
 * The QuerySelect provides a Select control with the options automatically
 * populated from a Cayenne query. This control supports both named queries,
 * which are configured in the Cayenne Modeller, and <tt>SelectQuery</tt>
 * which can be defined in code.
 * <p/>
 * All Cayenne queries are executed using the thread local {@link DataContext}
 * obtained via <tt>DataContext.getThreadDataContext()</tt>, and are executed
 * as required by the <tt>onProcess()</tt> and <tt>toString()</tt> methods.
 * <p/>
 * QuerySelect has no dependency on {@link CayenneForm} and can be used
 * separately in other forms or controls.
 *
 * <h3>QuerySelect Examples</h3>
 *
 * The QuerySelect below executes a Cayenne Modeller defined named query
 * called "system.titles". This pre configured query returns a list of {@link DataRow}
 * objects containing "VALUE" and "LABEL" values, which are rendered as the
 * option values and labels.
 *
 * <pre class="codeJava">
 * QuerySelect title = <span class="kw">new</span> QuerySelect(<span class="st">"title"</span>);
 * title.setQueryValueLabel(<span class="st">"system.titles"</span>, <span class="st">"VALUE"</span>, <span class="st">"LABEL"</span>);
 * form.add(title); </pre>
 *
 * The example below uses a {@link SelectQuery} defined in code and renders
 * the <tt>Delivery</tt> object <tt>type</tt> and <tt>description</tt> properties
 * and the option values and labels.
 *
 * <pre class="codeJava">
 * QuerySelect delivery = <span class="kw">new</span> QuerySelect(<span class="st">"delivery"</span>, <span class="kw">true</span>);
 * delivery.setQuery(<span class="kw">new</span> SelectQuery(Delivery.<span class="kw">class</span>));
 * delivery.setOptionValue(<span class="st">"type"</span>);
 * delivery.setOptionLabel(<span class="st">"description"</span>);
 * form.add(delivery); </pre>
 *
 * The last example uses a {@link Decorator} to render the select options label.
 *
 * <pre class="codeJava">
 * QuerySelect userSelect = <span class="kw">new</span> QuerySelect(<span class="st">"user"</span>, <span class="kw">true</span>);
 * userSelect.setQuery(<span class="kw">new</span> SelectQuery(User.<span class="kw">class</span>));
 * userSelect.setOptionValue(<span class="st">"username"</span>);
 * userSelect.setDecorator(<span class="kw">new</span> Decorator() {
 *     <span class="kw">public</span> String render(Object object, Context context) {
 *         User user = (User) object;
 *         <span class="kw">return</span> user.getFirstName() + <span class="st">" "</span> + user.getLastName();
 *     }
 * });
 * form.add(userSelect); </pre>
 *
 * @see CayenneForm
 * @see PropertySelect
 *
 * @author Malcolm Edgar
 */
public class QuerySelect extends Select {

    private static final long serialVersionUID = 1L;

    /** The option label rendering decorator. */
    protected Decorator decorator;

    /** The flag specifing whether the cache should be ignored. */
    protected boolean expireCache;

    /** The name of the configured select query. */
    protected String queryName;

    /**
     * The flag indicating whether the option list includes an empty option
     * value. By default the list does not include an empty option value.
     */
    protected boolean optional;

    /** The query result property to render as the option label. */
    protected String optionLabel;

    /** The query result property to render as the option value. */
    protected String optionValue;

    /** The option list Cayenne <tt>SelectQuery</tt>. */
    protected SelectQuery selectQuery;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a QuerySelect field with the given name.
     *
     * @param name the name of the field
     */
    public QuerySelect(String name) {
        super(name);
    }

    /**
     * Create a QuerySelect field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public QuerySelect(String name, String label) {
        super(name, label);
    }

    /**
     * Create a QuerySelect field with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public QuerySelect(String name, boolean required) {
        super(name, required);
    }

    /**
     * Create a QuerySelect field with the given name, label and required
     * status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public QuerySelect(String name, String label, boolean required) {
        super(name, label, required);
    }

    /**
     * Create a QuerySelect field with no name defined, <b>please note</b>
     * the control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public QuerySelect() {
        super();
    }

    // ------------------------------------------------------------- Properties

    /**
     * Return the option label rendering decorator.
     *
     * @return the option label rendering decorator
     */
    public Decorator getDecorator() {
        return decorator;
    }

    /**
     * Set the decorator to render the option labels.
     *
     * @param decorator the decorator to render the select option labels
     */
    public void setDecorator(Decorator decorator) {
        this.decorator = decorator;
    }

    /**
     * Return true if the query should expire the cache.
     *
     * @return true if the query should expire the cache
     */
    public boolean getExpireCache() {
        return expireCache;
    }

    /**
     * Set the query should expire cache parameter.
     *
     * @param expireCache the query should expire cache parameter
     */
    public void setExpireCache(boolean expireCache) {
        this.expireCache = expireCache;
    }

    /**
     * Return the name of the configured query to populate the options list
     * with.
     *
     * @return the name of the configured query to populate the options list with
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Set the name of the configured query to populate the options list
     * with.
     *
     * @param queryName the name of the configured query to populate the options list with
     */
    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    /**
     * Set the configured queryName to execute, the property to render as
     * the option value, and the property to render as the option label.
     *
     * @param queryName the configured named query to execute
     * @param optionValue the property to render as the option value
     * @param optionLabel the property to render as the option label
     */
    public void setQueryValueLabel(String queryName, String optionValue,
            String optionLabel) {

        setQueryName(queryName);
        setOptionValue(optionValue);
        setOptionLabel(optionLabel);
    }

    /**
     * Return true if the option list includes an empty option value.
     *
     * @return true if the option list includes an empty option value
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Set whether the option list includes an empty option value.
     *
     * @param value set whether the option list includes an empty option value
     */
    public void setOptional(boolean value) {
        optional = value;
    }

    /**
     * Return the query result property to render as the option label.
     * <p/>
     * If the query returns <tt>DataRow</tt> this property will be accessed
     * via <tt>dataRow.get(getOptionLabel())</tt>. If the query returns
     * <tt>DataObject</tt> the property will be accessed using its getter
     * method.
     *
     * @return the query result property to render as the option label.
     */
    public String getOptionLabel() {
        return optionLabel;
    }

    /**
     * Set the query result property to render as the option label.
     *
     * @param optionLabel the query result property to render as the option label
     */
    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    /**
     * Return the query result property to render as the option value.
     * <p/>
     * If the query returns <tt>DataRow</tt> this property will be accessed
     * via <tt>dataRow.get(getOptionValue())</tt>. If the query returns
     * <tt>DataObject</tt> the property will be accessed using its getter
     * method.
     *
     * @return the query result property to render as the option label.
     */
    public String getOptionValue() {
        return optionValue;
    }

    /**
     * Set the query result property to render as the option value.
     *
     * @param optionValue the query result property to render as the option value
     */
    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    /**
     * Return the <tt>SelectQuery</tt> to populate the options list with.
     *
     * @return the <tt>SelectQuery</tt> to populate the options list with
     */
    public SelectQuery getSelectQuery() {
        return selectQuery;
    }

    /**
     * Set the <tt>SelectQuery</tt> to populate the options list with.
     *
     * @param selectQuery the <tt>SelectQuery</tt> to populate the options
     *  list with
     */
    public void setSelectQuery(SelectQuery selectQuery) {
        this.selectQuery = selectQuery;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Bind the request value to the control.
     *
     * @see Select#bindRequestValue()
     */
    public void bindRequestValue() {

        loadOptionList();

        super.bindRequestValue();
    }

    /**
     * Return a HTML rendered Select string. If the Select option list is
     * empty this method will load option list so that it can be rendered.
     *
     * @see Select#toString()
     *
     * @return a HTML rendered Select string
     */
    public String toString() {

        loadOptionList();

        return super.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Load the Select options list.
     */
    protected void loadOptionList() {
        if (optionValue == null) {
            throw new IllegalStateException("optionValue property not defined");
        }
        if (optionLabel == null && decorator == null) {
            String msg = "either the optionLabel or decorator property must be"
                         + "defined to render the option labels";
            throw new IllegalStateException(msg);
        }

        // Determine whether option list should be loaded
        if (getOptionList().size() == 1) {
            Option option = (Option) getOptionList().get(0);
            if (option.getValue().equals(Option.EMPTY_OPTION.getValue())) {
                // continue and load option list

            } else {
                // Don't load list
                return;
            }

        } else if (getOptionList().size() > 1) {
            // Don't load list
            return;
        }

        DataContext dataContext = DataContext.getThreadDataContext();

        List list = Collections.EMPTY_LIST;

        if (getSelectQuery() != null) {
            list = dataContext.performQuery(getSelectQuery());

        } else if (getQueryName() != null) {
            list = dataContext.performQuery(getQueryName(), getExpireCache());
        }

        if (isRequired() && getOptionList().isEmpty() || isOptional()) {
            getOptionList().add(Option.EMPTY_OPTION);
        }

        Map cache = new HashMap();

        for (int i = 0; i < list.size(); i++) {
            Object row = list.get(i);

            Object value = null;
            Object label = null;

            if (row instanceof DataRow) {
                DataRow dataRow = (DataRow) row;

                if (dataRow.containsKey(getOptionValue())) {
                    value = dataRow.get(getOptionValue());

                } else {
                    String msg = "no value in dataRow for optionValue: "
                                 + getOptionValue();
                    throw new RuntimeException(msg);
                }

                if (getOptionLabel() != null) {

                    if (dataRow.containsKey(getOptionLabel())) {
                        label = dataRow.get(getOptionLabel());

                    } else {
                        String msg = "no value in dataRow for optionLabel: "
                                     + getOptionLabel();
                        throw new RuntimeException(msg);
                    }

                } else {
                    label = getDecorator().render(dataRow, getContext());
                }

            } else {

                try {

                    value = PropertyUtils.getValue(row, getOptionValue(), cache);

                    if (getOptionLabel() != null) {
                        label = PropertyUtils.getValue(row, getOptionLabel(), cache);

                    } else {
                        label = getDecorator().render(row, getContext());
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            value = (value != null) ? value : "";
            label = (label != null) ? label : "";

            getOptionList().add(new Option(value.toString(), label.toString()));
        }
    }

}
