package net.sf.click.extras.control;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import net.sf.click.control.Field;
import net.sf.click.control.Option;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.Format;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a couple of the multiple select box to pick up items.
 * 
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <table width="400" class="picklist">
 * <tr>
 *   <th>Languages</th>
 *   <td></td>
 *   <th>Selected</th>
 * </tr>
 * <tr>
 *   <td width="50%">
 *     <select size="8" style="width:100%;" multiple>
 *       <option>Ruby</option>
 *       <option>Perl</option>
 *     </select>
 *   </td>
 *   <td>
 *     <input type="button" value="&gt;" style="width:60px;"/><br>
 *     <input type="button" value="&lt;" style="width:60px;"/><br>
 *     <input type="button" value="&gt;&gt;" style="width:60px;"/><br>
 *     <input type="button" value="&lt;&lt;" style="width:60px;"/>
 *   </td>
 *   <td width="50%">
 *     <select size="8" style="width:100%;" multiple>
 *       <option>Java</option>
 *     </select>
 *   </td>
 * </tr>
 * </table>
 * </td></tr></table>
 * 
 * The values of the <code>PickList</code> are provided by <code>Option</code> 
 * objects like for a <code>Select</code>. 
 * 
 * <p/>
 * 
 * The following code shows the previous rendering example:
 * 
 * <pre class="codeJava">
 * PickList pickList = <span class="kw">new</span> PickList(<span class="st">"languages"</span>);
 * pickList.setHeaderLabel(<span class="st">"Languages"</span>, <span class="st">"Selected"</span>);
 * 
 * pickList.add(<span class="kw">new</span> Option(<span class="st">"001"</span>, <span class="st">"Java"</span>));
 * pickList.add(<span class="kw">new</span> Option(<span class="st">"002"</span>, <span class="st">"Ruby"</span>));
 * pickList.add(<span class="kw">new</span> Option(<span class="st">"003"</span>, <span class="st">"Perl"</span>));
 * 
 * pickList.setSelectedValue(<span class="st">"001"</span>); </pre>
 * 
 * The selected values can be retrieved from {@link getSelectedValues()}.
 * 
 * <pre class="codeJava">
 * Set selectedValues = pickList.getSelectedValues();
 * 
 * <span class="kw">for</span>(Iterator ite = set.iterator(); ite.hasNext();){
 *   String value = (String) ite.next();
 *   ...
 * } </pre>
 * 
 * @author Naoki Takezoe
 */
public class PickList extends Field {
    
    private static final long serialVersionUID = 1L;
    
    // -------------------------------------------------------------- Constants

    /**
     * The <tt>Palette.js</tt> imports statement.
     */
    public static final String PICKLIST_IMPORTS =
        "<script type=\"text/javascript\" src=\"$/click/PickList.js\"></script>\n";
    
    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the Field required status</li>
     * <li>2 - is the localized error message for required validation</li>
     * </ul>
     */
    protected final static String VALIDATE_PICKLIST_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validatePickList(\n"
        + "         ''{0}'',{1}, [''{2}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";
    
    // ----------------------------------------------------- Instance Variables
    
    /**
     * The label text for the unselected list.
     */
    protected String unselectedLabel;
    
    /**
     * The label text for the selected list.
     */
    protected String selectedLabel;
    
    /**
     * The selected values.
     */
    protected Set selectedValues;
    
    /**
     * The Option list.
     */
    protected List optionList;
    
    /**
     * The component size. The default size is 400px.
     */
    protected int size = 400;
    
    /**
     * The list height. The default height is 8.
     */
    protected int height = 8;
    
    // ----------------------------------------------------------- Constructors
    
    /**
     * Create a PickList with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public PickList() {
    }
    
    /**
     * Create a PickList field with the given name.
     *
     * @param name the name of the field
     */
    public PickList(String name) {
        super(name);
    }
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Set the component size.
     *
     * @param  size the component size
     */
    public void setSize(int size) {
        this.size = size;
    }
    
    /**
     * Return the component size.
     *
     * @return the component size
     */
    public int getSize() {
        return this.size;
    }
    
    /**
     * Set the list height.
     *
     * @param  height the list height
     */
    public void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * Return the list height.
     * 
     * @return the list height
     */
    public int getHeight() {
        return this.height;
    }
    
    /**
     * Return the HTML head import statements for the PickList.js.
     *
     * @return the HTML head import statements for the PickList.js
     */
    public String getHtmlImports() {
        String path = context.getRequest().getContextPath();

        return StringUtils.replace(PICKLIST_IMPORTS, "$", path);
    }
    
    /**
     * Bind the request submission, setting the {@link #selectedValues} 
     * property if defined in the request.
     */
    public void bindRequestValue() {

        // Page developer has not initialized options
        if (getOptionList().isEmpty()) {
            return;
        }

        // Load the selected items.
        this.selectedValues = new HashSet();

        String[] values =
            getContext().getRequest().getParameterValues(getName());

        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                selectedValues.add(values[i]);
            }
        }
    }
    
    /**
     * Deploy the <tt>PickList.js</tt> file to the <tt>click</tt> web
     * directory when the application is initialized.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFile(servletContext,
                              "/net/sf/click/extras/control/PickList.js",
                              "click");
    }
    
    /**
     * Add the given Option to the PickList.
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
     * Return the Option list.
     *
     * @return the Option list
     */
    public List getOptionList() {
        if (optionList == null) {
            optionList = new ArrayList();
        }
        return optionList;
    }
    
    /**
     * Return selected values.
     * 
     * @return selected values
     */
    public Set getSelectedValues() {
        if (selectedValues == null) {
            selectedValues = new HashSet();
        }
        return selectedValues;
    }
    
    /**
     * Add the selected value.
     * 
     * @param value the selected value
     * @throws IllegalArgumentException if the value is null
     */
    public void addSelectedValue(String value) {
        if (value == null) {
            String msg = "value parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getSelectedValues().add(value);
    }
    
    /**
     * Set the header label text for the selected list and the unselected list.
     * The specified text is displayed at the top of the list.
     * 
     * @param unselectedLabel the label text for the unselected list
     * @param selectedLabel the label text for the selected list
     */
    public void setHeaderLabel(String unselectedLabel, String selectedLabel) {
        this.unselectedLabel = unselectedLabel;
        this.selectedLabel = selectedLabel;
    }
    
    /**
     * Return the field JavaScript client side validation function.
     * <p/>
     * The function name must follow the format <tt>validate_[id]</tt>, where
     * the id is the DOM element id of the fields focusable HTML element, to
     * ensure the function has a unique name.
     *
     * @return the field JavaScript client side validation function
     */
    public String getValidationJavaScript() {
        Object[] args = new Object[7];
        args[0] = getId();
        args[1] = String.valueOf(isRequired());
        args[2] = getMessage("field-required-error", getErrorLabel());
        return MessageFormat.format(VALIDATE_PICKLIST_FUNCTION, args);
    }
    
    /**
     * Validate the PickList request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>net.sf.click.control.MessageProperties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>field-required-error</li>
     * </ul></blockquote>
     */
    public void validate() {
        setError(null);
        Set value = getSelectedValues();

        if (value.size() > 0) {
            return;

        } else {
            if (isRequired()) {
                setErrorMessage("field-required-error");
            }
        }
    }
    
    /**
     * Return a HTML rendered PickList string.
     *
     * @return a HTML rendered PickList string
     */
    public String toString() {
        Map model = new HashMap();
        
        List optionList      = getOptionList();
        Set  selectedValues  = getSelectedValues();
        List selectedItems   = new ArrayList();
        List unselectedItems = new ArrayList();
        
        for (int i = 0; i < optionList.size(); i++) {
            Option option = (Option) optionList.get(i);
            if (selectedValues.contains(option.getValue())) {
                selectedItems.add(option);
            } else {
                unselectedItems.add(option);
            }
        }
        
        model.put("id", getId());
        model.put("name", getName());
        model.put("selectedItems", selectedItems);
        model.put("unselectedItems", unselectedItems);
        model.put("selectedLabel", selectedLabel);
        model.put("unselectedLabel", unselectedLabel);
        model.put("format", new Format(getContext().getLocale()));
        model.put("size", new Integer(getSize()));
        model.put("height", new Integer(getHeight()));
        model.put("valid", new Boolean(isValid()));
        model.put("disabled", new Boolean(isDisabled()));
        model.put("readOnly", new Boolean(isReadonly()));
        
        return getContext().renderTemplate(
                "/net/sf/click/extras/control/PickList.htm", model);
    }
    
}
