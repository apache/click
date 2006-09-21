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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.click.control.Decorator;
import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.util.ClickUtils;
import ognl.Ognl;

import org.apache.commons.lang.StringUtils;
import org.objectstyle.cayenne.DataObject;
import org.objectstyle.cayenne.DataObjectUtils;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.query.SelectQuery;

/**
 * Provides a DataObject property Select control: &nbsp; &lt;select&gt;&lt;/select&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Property Select</td>
 * <td>
 * <select title='PropertySelect Control'>
 * <option value='110'>P. Barnes</option>
 * <option value='220'>M. Edgar</option>
 * <option value='330'>C. Essl</option>
 * <option value='440'>A. Mohombe</option>
 * <option value='550'>N. Takezoe</option>
 * </select>
 * </td>
 * </tr>
 * </table>
 *
 * The PropertySelect provides a Select control for {@link DataObject}
 * relationship properties (properties which are also DataObjects). For
 * properties which are not DataObjects use the {@link QuerySelect} control.
 * <p/>
 * The PropertySelect control will only work inside a CayenneForm
 * as it obtains meta data about DataObject property from the parent form.
 * <p/>
 * Currently this control only supports selecting a single elements.
 *
 * <h3>PropertySelect Example</h3>
 *
 * For example given a Pet DataObject which has a PetType DataObject property.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> Pet <span class="kw">implements</span> DataObject {
 *     <span class="kw">public</span> PetType getType() {
 *         ..
 *     }
 *
 *     <span class="kw">public void</span> setType(PetType value) {
 *         ..
 *     }
 * }
 *
 * <span class="kw">public class</span> PetType <span class="kw">implements</span> DataObject {
 *     <span class="kw">public</span> String getName() {
 *         ..
 *     }
 * } </pre>
 *
 * You would use the PropertySelect in a CayenneForm to edit the Pet type
 * property. In this example the PetType name property is rendered as the
 * select options label.
 *
 * <pre class="codeJava">
 * CayenneForm form = <span class="kw">new</span> CayenneForm(<span class="st">"form"</span>, Pet.<span class="kw">class</span>);
 *
 * PropertySelect typeSelect = <span class="kw">new</span> PropertySelect(<span class="st">"type"</span>, <span class="kw">true</span>);
 * typeSelect.setOptionLabel(<span class="st">"name"</span>);
 * form.add(typeSelect); </pre>
 *
 * In this example no query is specified and the control will create a
 * simple {@link SelectQuery} based on the property class. However,
 * generally you should a named query (configured in the Cayenne Modeller)
 * or a explicity set a <tt>SelectQuery</tt>.
 * <p/>
 * Note when using a named query ensure that it will return DataObjects and not
 * DataRows.
 *
 * @see CayenneForm
 * @see QuerySelect
 *
 * @author Malcolm Edgar
 */
public class PropertySelect extends Select {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The option label rendering decorator. */
    protected Decorator decorator;

    /** The name of the configured select query. */
    protected String queryName;

    /** The data object property to render as the option label. */
    protected String optionLabel;

    /** The option list Cayenne <tt>SelectQuery</tt>. */
    protected SelectQuery selectQuery;

    /** The property value object. */
    protected DataObject valueObject;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a PropertySelect field with the given name.
     *
     * @param name the name of the field
     */
    public PropertySelect(String name) {
        super(name);
    }

    /**
     * Create a PropertySelect field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public PropertySelect(String name, String label) {
        super(name, label);
    }

    /**
     * Create a PropertySelect field with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public PropertySelect(String name, boolean required) {
        super(name, required);
    }

    /**
     * Create a PropertySelect field with the given name, label and required
     * status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public PropertySelect(String name, String label, boolean required) {
        super(name, label, required);
    }

    /**
     * Create a PropertySelect field with no name defined, <b>please note</b>
     * the control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public PropertySelect() {
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
     * Return true if multiple options can be selected.
     *
     * @see Select#isMultiple()
     *
     * @return false
     */
    public boolean isMultiple() {
        return false;
    }

    /**
     * Set the multiple options can be selected flag.
     *
     * @see Select#setMultiple(boolean)
     *
     * @param value the multiple options can be selected flag
     */
    public void setMultiple(boolean value) {
        String msg = "PropertySelect does not support multiple property values";
        throw new UnsupportedOperationException(msg);
    }

    /**
     * Set the <tt>DataObject</tt> property to render as the option label.
     *
     * @param optionLabel the <tt>DataObject</tt> property to render as the
     *  option label
     */
    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
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

    /**
     * Return the <tt>DataObject</tt> property class.
     *
     * @see net.sf.click.control.Field#getValueClass()
     *
     * @return the <tt>DataObject</tt> property class
     */
    public Class getValueClass() {
        return valueObject.getClass();
    }

    /**
     * Return the property <tt>DataObject</tt> value, or null if value was not
     * defined.
     *
     * @see net.sf.click.control.Field#getValueObject()
     *
     * @return the property <tt>DataObject</tt> value
     */
    public Object getValueObject() {
        return valueObject;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Bind the request value to the control, looking up the <tt>DataObject</tt>
     * based on the submitted primary key value and setting this object as
     * the Select <tt>valueObject</tt>.
     *
     * @see Select#bindRequestValue()
     */
    public void bindRequestValue() {

        loadOptionList();

        super.bindRequestValue();

        if (StringUtils.isNotBlank(getValue())) {

            CayenneForm form = (CayenneForm) getForm();
            Class doClass = form.getDataObjectClass();
            String getterName = ClickUtils.toGetterName(getName());

            try {
                Method method = doClass.getMethod(getterName, null);

                DataContext dataContext = form.getDataContext();

                Class propertyClass = method.getReturnType();

                Integer propertyPk = new Integer(getValue());

                valueObject = DataObjectUtils.objectForPK(dataContext,
                                                          propertyClass,
                                                          propertyPk);

                setValue(propertyPk.toString());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
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

        // Load property options if not already set
        if (getOptionList().size() <= 1) {
            loadOptionList();
        }

        // Select option value if value defined and not form submission
        if (getValueObject() == null && !getForm().isFormSubmission()) {

            CayenneForm form = (CayenneForm) getForm();
            DataContext dataContext = form.getDataContext();
            Class doClass = form.getDataObjectClass();
            Integer doPk = form.getDataObjectPk();

            if (doPk != null) {
                DataObject dataObject =
                    DataObjectUtils.objectForPK(dataContext, doClass, doPk);

                String getterName = ClickUtils.toGetterName(getName());

                try {
                    Method method = doClass.getMethod(getterName, null);

                    DataObject property =
                        (DataObject) method.invoke(dataObject, null);

                    if (property != null) {
                        Object propPk = DataObjectUtils.pkForObject(property);
                        setValue(propPk.toString());
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return super.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Load the Select options list. This method will attempt to select the
     * options using the following techniques.
     * <ol>
     * <li>if a <tt>SelectQuery</tt> is defined load options from SelectQuery</li>
     * <li>if a query name is defined load options from configured named query</li>
     * <li>else create a <tt>SelectQuery</tt> based on the property class</li>
     * </ol>
     */
    protected void loadOptionList() {

        CayenneForm form = (CayenneForm) getForm();
        DataContext dataContext = form.getDataContext();

        try {
            List list = null;

            if (getSelectQuery() != null) {
                list = dataContext.performQuery(getSelectQuery());

            } else if (getQueryName() != null) {
                 list = dataContext.performQuery(getQueryName(), false);

            } else {
                Class doClass = form.getDataObjectClass();
                String getterName = ClickUtils.toGetterName(getName());
                Method method = doClass.getMethod(getterName, null);
                Class propertyClass = method.getReturnType();

                list = dataContext.performQuery(new SelectQuery(propertyClass));
            }

            if (isRequired() && getOptionList().isEmpty()) {
                getOptionList().add(Option.EMPTY_OPTION);
            }

            Map ognlContext = new HashMap();

            for (int i = 0; i < list.size(); i++) {
                DataObject dataObject = (DataObject) list.get(i);
                int pk = DataObjectUtils.intPKForObject(dataObject);

                String value = String.valueOf(pk);
                Object label = null;

                if (getDecorator() != null) {
                    label = getDecorator().render(dataObject, getContext());
                } else {
                    label = Ognl.getValue(optionLabel, ognlContext, dataObject);
                }

                Option option = new Option(value, label.toString());

                add(option);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
