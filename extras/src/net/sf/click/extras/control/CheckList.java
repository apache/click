/*
 * Copyright 2004-2008 Malcolm A. Edgar
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.control.Field;
import net.sf.click.control.Option;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;
import net.sf.click.util.PropertyUtils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a scrollable check list. This is an implementation of the Checklist
 * from <a href="http://c82.net/article.php?ID=25">Check it don't select it</a>
 * <p/>
 * A scrollable check list is a more user friendly substitution for
 * multiple-select-boxes. It is a scrollabe div which has many select-boxes.
 * <p/>
 * To make the CheckList scrollable, set the height of the CheckList through
 * {@link #setHeight(String)}.
 * <p/>
 * <b>Note</b> when setting the height it is recommended that the CheckList
 * should not be sortable, because of browser incompatibilities.
 * <p/>
 * The CheckList is also sortable by drag-drop if the
 * {@link #setSortable(boolean)} property is set to true. In this case the
 * method {@link #getSortorder()} returns the keys of all the options whether
 * they where selected or not in the order provided by the user.
 * <p/>
 * Sortable is provided by scriptaculous which is only supported on IE6, FF and
 * Safari1.2 and higher. This control is only tested on IE6 and FF on Windows.
 * With IE the text of the dragged element has a black-outline which does not
 * look good. To turn this off define an explicit back-ground color for the
 * &lt;li&gt; elements. Typically you will do this in a style: .listClass li
 * {background-color: #xxx}, where the listClass is set through
 * {@link #addStyleClass(String)}.
 * <p/>
 * If a select is required at least one item must be
 * selected so that the input is valid. Other validations are not done.
 * <p/>
 * The Control listener will be invoked in any case whether the CheckList is valid or not.
 * <p/>
 * The values of the CheckList are provided by
 * {@link net.sf.click.control.Option} instances. To populate the CheckList with
 * Options, use the add methods.
 * <p/>
 * The label of the Option is shown to the user and the value is what is
 * provided in the {@link #getSelectedValues()} and {@link #getSortorder()}
 * Lists.
 * <p/>
 * The selected values can be retrieved from {@link #getSelectedValues()}. The
 * get/setValue() property is not supported.
 * <p/>
 * The select uses the /click/checklist.css style. By providing a style which
 * extends this style the appearance of the list can be changed.
 * <p/>
 * To set the additional style class use {@link #addStyleClass(String)}.
 * This will append the given class to the default class of this control.
 * Alternatively {@link #setStyle(String, String)} can be used to set the style
 * of the outer div.
 * <p/>
 * For an example please look at the click-examples and the at the above blog.
 *
 * @see net.sf.click.control.Option
 *
 * @author Christian Essl
 */
public class CheckList extends Field {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** The Checklist resource file names. */
    static final String[] CHECKLIST_RESOURCES = {
        "/net/sf/click/extras/control/checklist/checklist.css",
        "/net/sf/click/extras/control/checklist/checklist.js"
    };

    /** The Prototype resource file names. */
    static final String[] PROTOTYPE_RESOURCES = {
        "/net/sf/click/extras/control/prototype/builder.js",
        "/net/sf/click/extras/control/prototype/controls.js",
        "/net/sf/click/extras/control/prototype/dragdrop.js",
        "/net/sf/click/extras/control/prototype/effects.js",
        "/net/sf/click/extras/control/prototype/prototype.js",
        "/net/sf/click/extras/control/prototype/slider.js"
    };

    /** The HTML import statements. */
    public static final String HTML_IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/checklist/checklist{1}.css\"/>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/checklist/checklist{1}.js\"></script>\n";

    /** The JavaScript sorting HTML import statements. */
    public static final String JS_SORT_HTML_IMPORTS =
          "<script type=\"text/javascript\" src=\"{0}/click/prototype/prototype{1}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/prototype/builder{1}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/prototype/effects{1}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/prototype/dragdrop{1}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/prototype/controls{1}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/prototype/slider{1}.js\"></script>\n";

    /** The style class which is always set on this element (checkList). */
    protected static final String STYLE_CLASS = "checkList";

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the full path name to the checkbox</li>
     * <li>2 - is the Field required status</li>
     * <li>3 - is the localized error message for required validation</li>
     * </ul>
     */
    protected final static String VALIDATE_CHECKLIST_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateCheckList(\n"
        + "         {1} ,{2}, [''{3}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    // ----------------------------------------------------- Instance Variables

    /** The height if null not scrollable. */
    protected String height;

    /** The Select Option list. */
    protected List optionList;

    /** If sortable by drag and drop. */
    protected boolean sortable;

    /**
     * The key of the values in the order they are present (only set when
     * sortable).
     */
    protected List sortorder;

    /** The selected values. */
    private List selectedValues;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a CheckList field with the given name.
     *
     * @param name the name of the field
     */
    public CheckList(String name) {
        super(name);
    }

    /**
     * Create a CheckList field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public CheckList(String name, String label) {
        super(name, label);
    }

    /**
     * Create a CheckList field with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public CheckList(String name, boolean required) {
        super(name);
        setRequired(required);
    }

    /**
     * Create a CheckList field with the given name, label and required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public CheckList(String name, String label, boolean required) {
        super(name, label);
        setRequired(required);
    }

    /**
     * Create a CheckList field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public CheckList() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Add the given Option.
     *
     * @param option the Option value to add
     * @throws IllegalArgumentException if option is null
     */
    public void add(Option option) {
        if (option == null) {
            String msg = "option parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getOptionList().add(option);
    }

    /**
     * Add the given option value. This convenience method will create a new
     * {@link Option} with the given value and add it to the CheckList. The new
     * Option display label will be the same as its value.
     *
     * @param value the option value to add
     * @throws IllegalArgumentException if the value is null
     */
    public void add(String value) {
        if (value == null) {
            String msg = "value parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getOptionList().add(new Option(value));
    }

    /**
     * Add the given Option collection to the CheckList.
     *
     * @param options the collection of Option objects to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(Collection options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getOptionList().addAll(options);
    }

    /**
     * Add the given Map of option values and labels to the CheckList. The Map
     * entry key will be used as the option value and the Map entry value will
     * be used as the option label.
     * <p/>
     * It is recommended that <tt>LinkedHashMap</tt> is used as the Map
     * parameter to maintain the order of the option vales.
     *
     * @param options the Map of option values and labels to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(Map options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        for (Iterator i = options.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Option option = new Option(entry.getKey().toString(), entry
                    .getValue().toString());
            getOptionList().add(option);
        }
    }

    /**
     * Add the given array of string options to the Select option list. <p/> The
     * options array string value will be used for the {@link Option#value} and
     * {@link Option#label}.
     *
     * @param options the array of option values to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(String[] options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        for (int i = 0; i < options.length; i++) {
            String value = options[i];
            getOptionList().add(new Option(value, value));
        }
    }

    /**
     * Add the given collection of objects to the CheckList, creating new Option
     * instances based on the object properties specified by value and label.
     *
     * <pre class="codeJava">
     *   CheckList list = <span class="kw">new</span> CheckList(<span class="st">"type"</span>, <span class="st">"Type:"</span>);
     *   list.addAll(getCustomerService().getCustomerTypes(), <span class="st">"id"</span>, <span class="st">"name"</span>);
     *   form.add(select); </pre>
     *
     * @param objects the collection of objects to render as options
     * @param value the name of the object property to render as the Option value
     * @param label the name of the object property to render as the Option label
     * @throws IllegalArgumentException if options, value or label parameter is null
     */
    public void addAll(Collection objects, String value, String label) {
        if (objects == null) {
            String msg = "objects parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "value parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        if (label == null) {
            String msg = "label parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }

        if (objects.isEmpty()) {
            return;
        }

        Map cache = new HashMap();

        for (Iterator i = objects.iterator(); i.hasNext();) {
            Object object = i.next();

            try {
                Object valueResult = PropertyUtils.getValue(object, value, cache);
                Object labelResult = PropertyUtils.getValue(object, label, cache);

                Option option = null;

                if (labelResult != null) {
                    option = new Option(valueResult, labelResult.toString());
                } else {
                    option = new Option(valueResult.toString());
                }

                getOptionList().add(option);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Adds the given style-value pair to the style attribute of the outer div.
     * Does not check whether the style is already set.
     * <p/>
     * Typically used for the width:
     *
     * <pre class="codeJava">
     * list.addStyle(<span class="st">"&quot;width: 100%; height: 25em&quot;"</span>); </pre>
     *
     * @param style the style name:value pair without a ending ;
     *
     * @deprecated use @{link #setStyle(String, String)}
     */
    public void addStyle(String style) {
        if (StringUtils.isBlank(style)) {
            throw new IllegalArgumentException("The style is empty");
        }
        style = style.trim();

        if (style.charAt(style.length() - 1) == ';') {
            style = style + ";";
        }

        String old = getAttribute("style");
        if (old == null || (old = old.trim()).length() == 0) {
            setAttribute("style", style);
        } else {
            if (old.charAt(old.length() - 1) != ';') {
                old = old + ';';
            }
            old = old + style;
            setAttribute("style", old);
        }
    }

    /**
     * Return the Field focus JavaScript.
     *
     * @return the Field focus JavaScript
     */
    public String getFocusJavaScript() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.append("setFocus('");
        buffer.append(getName() + "_0");
        buffer.append("');");

        return buffer.toString();
    }

    /**
     * The css height attribute-value.
     * If null no height is set and the CheckList is not scrollable.
     *
     * @param height one of css height values (ie 40px) or null.
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * The css-height attribute.
     *
     * @return Returns the height or null.
     */
    public String getHeight() {
        return height;
    }

    /**
     * Set the given html class. The class will be set on the select list
     * together with the {@link #STYLE_CLASS}. Ie class="checkList my-class"
     * where my-class is the set class. The default value is null.
     *
     * @deprecated use {@link #addStyleClass(String)} instead
     *
     * @param clazz the class to set or null
     */
    public void setHtmlClass(String clazz) {
        addStyleClass(clazz);
    }

    /**
     * The html class to set on this control.
     *
     * @see #setHtmlClass(String)
     *
     * @deprecated use {@link #getAttribute(String)} instead
     *
     * @return the class or null (default null)
     */
    public String getHtmlClass() {
        return getAttribute("class");
    }

    /**
     * Returns the CSS and JavaScript imports and adds the script the checklist
     * onload event.
     *
     * @see net.sf.click.control.Field#getHtmlImports()
     *
     * @return the two import tags
     */
    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(400);

        buffer.append(ClickUtils.createHtmlImport(HTML_IMPORTS, getContext()));

        if (isSortable()) {
            buffer.append(ClickUtils.createHtmlImport(JS_SORT_HTML_IMPORTS,
                getContext()));

            // Script to execute
            HtmlStringBuffer script = new HtmlStringBuffer(50);
            script.append("Sortable.create('");
            script.append(StringEscapeUtils.escapeJavaScript(getId()));
            script.append("_ul'");

            if (getHeight() != null) {
                script.append(", { scroll : '");
                script.append(StringEscapeUtils.escapeJavaScript(getId()));
                script.append("'}");
            }
            script.append(");");

            buffer.append("<script type=\"text/javascript\">");
            if (getHeight() != null) {
                buffer.append("Position.includeScrollOffset = true;");
            }

            buffer.append("addLoadEvent(function() { ");
            buffer.append(script);
            buffer.append(" });</script>\n");

        } else {
            buffer.append("<script type=\"text/javascript\">");
            buffer.append("addLoadEvent(function() { initChecklist('");
            buffer.append(StringEscapeUtils.escapeJavaScript(getId()));
            buffer.append("_ul'); });</script>\n");
        }

        return buffer.toString();
    }

    /**
     * Return the Option list.
     *
     * @return a list of Option objects
     */
    public List getOptionList() {
        if (optionList == null) {
            optionList = new ArrayList();
        }
        return optionList;
    }

    /**
     * Set the Option list. Note: if the CheckList is sortable
     * than the List <b>must be fully modifiable</b>, because
     * it will be sorted according to the order chosen by the
     * user.
     *
     * @param options a list of Option objects
     */
    public void setOptionList(List options) {
        optionList = options;
    }
    /**
     * Whether the list should be drag-drop sortable. This is supported by
     * scriptaculous. Note when the list also has a size than this might not work
     * on different browsers.
     *
     * @param sortable default is false.
     */
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    /**
     * Whether the list is also sortable.
     *
     * @return Returns the sortable.
     */
    public boolean isSortable() {
        return sortable;
    }

    /**
     * A list of the values transmitted in the order they are present in the
     * list. This is only available if the list is sortable
     *
     * @return Returns list of strings of the option values.
     */
    public List getSortorder() {
        return sortorder;
    }

    /**
     * Return the list of selected values. The values are the values of the
     * Options selected.
     *
     * @deprecated use {@link #getSelectedValues()} instead
     *
     * @return a list of Strings
     */
    public List getValues() {
        return getSelectedValues();
    }

    /**
     * Return the list of selected values. The values are the values of the
     * Options selected.
     *
     * @return the list of selected values
     */
    public List getSelectedValues() {
        if (selectedValues != null) {
            return selectedValues;

        } else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Set the list of selected values. The specified values must match
     * the values of the Options
     *
     * @deprecated use {@link #setSelectedValues(List)} instead
     *
     * @param values a list of strings or null
     */
    public void setValues(List values) {
        this.selectedValues = values;
    }

    /**
     * Set the list of selected values. The specified values must match
     * the values of the Options.
     * <p/>
     * For example:
     * <pre class="prettyprint">
     * CheckList checkList = new CheckList("checkList");
     *
     * public void onInit() {
     *     List options = new ArrayList();
     *     options.add(new Option("1", "Option 1");
     *     options.add(new Option("2", "Option 2");
     *     options.add(new Option("3", "Option 3");
     *     checkList.setOptionList(options);
     * }
     *
     * public void onRender() {
     *     // Preselect some Options.
     *     List selected = new ArrayList();
     *     selected.add("1"));
     *     selected.add("3");
     *     checkList.setSelectedValues(selected);
     * } </pre>
     *
     * @param selectedValues a list of strings or null
     */
    public void setSelectedValues(List selectedValues) {
        this.selectedValues = selectedValues;
    }

    /**
     * Returns the values list {@link #getValues()} return a list or String.
     *
     * @see net.sf.click.control.Field#getValueObject()
     *
     * @return List of selected values (Strings)
     */
    public Object getValueObject() {
        return getValues();
    }

    /**
     * Set the value the value must be a List of String.
     *
     * @see net.sf.click.control.Field#setValueObject(java.lang.Object)
     *
     * @param object a List or null
     */
    public void setValueObject(Object object) {
        if (object instanceof List) {
            setValues((List) object);
        }
    }

    /**
     * Return the CheckList JavaScript client side validation function.
     *
     * @return the field JavaScript client side validation function
     */
    public String getValidationJavaScript() {
        Object[] args = new Object[4];
        args[0] = getId();
        args[1] = "document." + getForm().getName() + "." + getName();
        args[2] = String.valueOf(isRequired());
        args[3] = getMessage("field-required-error", getErrorLabel());

        return MessageFormat.format(VALIDATE_CHECKLIST_FUNCTION, args);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Bind the request submission, setting the {@link #selectedValues} and
     * sort order if the checkList is sortable.
     */
    public void bindRequestValue() {

        // Page developer has not initialized options, which are required
        // to support sorting
        if (getOptionList().isEmpty()) {
            return;
        }

        // Load the selected items.
        this.selectedValues = new ArrayList();

        String[] values = getContext().getRequestParameterValues(getName());

        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                this.selectedValues.add(values[i]);
            }
        }

        if (isSortable()) {
            String[] order = getContext().getRequest().getParameterValues(
                    getName() + "_order");
            if (order != null) {
                this.sortorder = new ArrayList(order.length);
                for (int i = 0; i < order.length; i++) {
                    String value = order[i];
                    if (value != null) {
                        sortorder.add(value);
                    }
                }
                sortOptions(order);
            }
        }
    }

    /**
     * Sorts the current Options List. This method is called
     * in {@link #bindRequestValue()} when the CheckList
     * is sortable.
     *
     * @param order values in the order to sort the list.
     */
    protected void sortOptions(String[] order) {
        final List options = getOptionList();
        final List orderList = new ArrayList(options.size());

        for (int i = 0, size = order.length; i < size; i++) {
            String value = order[i];
            if (value != null) {
                int oI = -1;
                for (int j = 0, jSize = options.size(); j < jSize; j++) {
                    Option optT = (Option) options.get(j);
                    if (value.equals(optT.getValue())) {
                        oI = j;
                    }
                }
                if (oI != -1) {
                    orderList.add(options.remove(oI));
                }
            }
        }
        options.addAll(0, orderList);
    }

    /**
     * Deploys the style-sheet 'checklist.css' to the /click directory.
     *
     * @see net.sf.click.control.Field#onDeploy(javax.servlet.ServletContext)
     *
     * @param servletContext the context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFiles(servletContext,
                               CHECKLIST_RESOURCES,
                               "click/checklist");

        ClickUtils.deployFiles(servletContext,
                               PROTOTYPE_RESOURCES,
                               "click/prototype");
    }

    /**
     * Return a HTML rendered CheckList.
     *
     * @return a HTML rendered CheckList string
     */
    public String toString() {

        int bufferSize = 50;
        if (!getOptionList().isEmpty()) {
            bufferSize = bufferSize + (optionList.size() * 48);
        }
        final HtmlStringBuffer buffer = new HtmlStringBuffer(bufferSize);
        final boolean sortable = isSortable();

        // the div element
        buffer.elementStart("div");

        buffer.appendAttribute("id", getId());

        // style class
        addStyleClass(STYLE_CLASS);
        /*
        buffer.append(" class=\"");
        buffer.append(STYLE_CLASS);
        String classAttr = getAttribute("class");
        if (classAttr != null) {
            setAttribute("class", null);
            buffer.append(" ");
            buffer.append(classAttr);
        }
       */
        if (isValid()) {
            removeStyleClass("error");
            //buffer.append(" error");
        } else {
            addStyleClass("error");
        }

        // set the style
        setStyle("height", getHeight());

        if (sortable && getHeight() == null) {
            setStyle("overflow", "hidden");
        } else {
            setStyle("overflow", "auto");
        }

        appendAttributes(buffer);

        buffer.closeTag();

        // the ul tag
        buffer.elementStart("ul");
        buffer.appendAttribute("id", getId() + "_ul");
        buffer.closeTag();

        // the options
        List optionsList = getOptionList();
        if (!optionsList.isEmpty()) {
            int i = -1;
            for (Iterator it = optionsList.iterator(); it.hasNext();) {
                Option option = (Option) it.next();
                i++;
                final String liId = getName() + "_" + i;

                buffer.append("<li>");
                if (sortable) {
                    buffer.elementStart("div");
                    buffer.appendAttribute("style", "cursor:move;");
                } else {
                    buffer.elementStart("label");
                    buffer.appendAttribute("for", liId);
                }
                buffer.appendAttribute("class", "checkListLabel");
                buffer.closeTag();

                buffer.append("<input type=\"checkbox\" ");
                buffer.appendAttribute("value", option.getValue());
                buffer.appendAttribute("id", liId);
                buffer.appendAttribute("name", getName());

                if (sortable) {
                    buffer.appendAttribute("style", "cursor:default;");
                }

                // set checked status
                List values = getSelectedValues();
                for (int k = 0; k < values.size(); k++) {
                    if (String.valueOf(values.get(k)).equals(option.getValue())) {
                        buffer.appendAttribute("checked", "checked");
                    }
                }

                if (isDisabled()) {
                    buffer.appendAttributeDisabled();
                }
                if (isReadonly()) {
                    buffer.appendAttributeReadonly();
                }
                buffer.elementEnd();

                buffer.append(option.getLabel());

                if (sortable) {
                    buffer.append("</div>");
                } else {
                    buffer.append("</label>");
                }

                // hiddenfield if sortable

                if (sortable) {
                    buffer.append("<input type=\"hidden\"");
                    buffer.appendAttribute("name", getName() + "_order");
                    buffer.appendAttribute("value", option.getValue());
                    buffer.elementEnd();
                }

                buffer.append("</li>");
            }
        }
        buffer.append("</ul>");
        buffer.append("</div>");

        return buffer.toString();
    }

    /**
     * Validate the CheckList request submission.
     * <p/>
     * If a CheckList is {@link #required} then the user must select a value,
     * otherwise the Select will have a validation error. If the Select is not
     * required then no validation errors will occur.
     * <p/>
     * A field error message is displayed if a validation error occurs. These
     * messages are defined in the resource bundle: <blockquote>
     *
     * <pre class="codeConfig>
     *  /click-control.properties </pre>
     *
     * </blockquote> <p/> Error message bundle key names include: <blockquote>
     * <ul>
     * <li>select-error</li>
     * </ul>
     * </blockquote>
     */
    public void validate() {
        if (isRequired()) {
            if (getSelectedValues().isEmpty()) {
                setErrorMessage("select-error");
            }
        }
    }

}
