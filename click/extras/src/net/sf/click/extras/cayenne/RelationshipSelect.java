package net.sf.click.extras.cayenne;

import java.util.List;

import net.sf.click.Context;
import net.sf.click.control.Decorator;
import net.sf.click.control.Option;
import net.sf.click.control.Select;

import org.objectstyle.cayenne.DataObject;
import org.objectstyle.cayenne.DataObjectUtils;

/**
 * Provides a Cayenne data object Relationship Select control: &nbsp; &lt;select&gt;&lt;/select&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Relationship Select</td>
 * <td>
 * <select title='RelationshipSelect Control'>
 * <option value='11'>Department of Finance</option>
 * <option value='23'>Department of Trading</option>
 * <option value='32'>Department of Housing</option>
 * </select>
 * </td>
 * </tr>
 * </table>
 *
 * The data object primary keys are uses at the values for the Select. The
 * labels can be rendered using a provided {@link net.sf.click.control.Decorator}
 * or will render the primary key value otherwise.
 * <p/>
 * The example below add a RelationshipSelect for the department property of
 * the <tt>Person</tt> data object.
 * <pre class="codeJava">
 * RelationshipSelect departmentSelect =
 *     <span class="kw">new</span> RelationshipSelect(<span class="st">"departmentSelect"</span>, <span class="st">"Department"</span>);
 *
 * departmentSelect.setDecorator(<span class="kw">new</span> Decorator() {
 *     <span class="kw">public</span> String render(Object row, Context context) {
 *         <span class="kw">return</span> ((Department) row).getName();
 *     }
 * });
 *
 * SelectQuery query = <span class="kw">new</span> SelectQuery(Department.<span class="kw">class</span>);
 * List departmentList = getDataContext().performQuery(query);
 * departmentSelect.addRelationshipList(departmentList);
 *
 * CayenneForm form = <span class="kw">new</span> CayenneForm(<span class="st">"form"</span>, Person.<span class="kw">class</span>);
 * form.add(departmentSelect);
 * form.addRelation(<span class="st">"department"</span>, Department.<span class="kw">class</span>, departmentSelect); </pre>
 *
 * <b>PLEASE NOTE</b>: this control  is undergoing preliminary development and
 * is subject to significant change
 *
 * @see CayenneForm
 *
 * @author Ahmed Mohombe
 */
public class RelationshipSelect extends Select {

    private static final long serialVersionUID = -9051242942584573702L;

    /** The empty select empty option. */
    protected static final Option EMPTY_OPTION = new Option("", "");

    /** The add empty option to select flag, default value is true. */
    protected boolean emptyOption = true;

    /** The option label rendering decorator. */
    protected Decorator decorator;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a data object Relationship Select for the given name.
     *
     * @param name the name of the select
     */
    public RelationshipSelect(String name) {
        super(name);
    }

    /**
     * Create a data object Relationship Select for the given name and label.
     *
     * @param name the name of the select
     * @param label the label of the select
     */
    public RelationshipSelect(String name, String label) {
        super(name, label);
    }

    /**
     * Create a Relationship Select field with no name defined,
     * <b>please note</b> the control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public RelationshipSelect() {
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Add the given list of data objects to the select. The data object values
     * will be converted into <tt>Option</tt> objects and added to the select.
     * The data object primary key is used as the options value, while the
     * provided renderer is used to render the option label.
     *
     * @param relationshipList the list of data object which will provide the
     *      select options
     */
    public void addRelationshipList(List relationshipList) {
        if (relationshipList == null) {
            throw new IllegalArgumentException("Null relations parameter");
        }

        if (isEmptyOption()) {
            add(EMPTY_OPTION);
        }

        Decorator decorator = getDecorator();

        for (int i = 0; i < relationshipList.size(); i++) {
            DataObject relation = (DataObject) relationshipList.get(i);
            int pk = DataObjectUtils.intPKForObject(relation);

            String label = decorator.render(relation, getContext());
            Option option = new Option(String.valueOf(pk), label);

            add(option);
        }
    }

    /**
     * Return the option label rendering decorator.
     *
     * @return the option label rendering decorator
     */
    public Decorator getDecorator() {
        if (decorator == null) {
            decorator = new DefaultDecorator();
        }
        return decorator;
    }

    /**
     * Set the decorator to render the select values.
     *
     * @param decorator the decorator to render the select values with
     */
    public void setDecorator(Decorator decorator) {
        this.decorator = decorator;
    }

    /**
     * Return true if an empty value is a valid option.
     *
     * @return true if an empty value is a valid option
     */
    public boolean isEmptyOption() {
        return emptyOption;
    }

    /**
     * Set the empty value option flag. If true the option will render the first
     * select value as empty.
     *
     * @param emptyOption the empty option value flag
     */
    public void setEmptyOption(boolean emptyOption) {
        this.emptyOption = emptyOption;
    }

    // -------------------------------------------------------- Private Classes

    /**
     * The default Decorator renders the data object primary key as the label.
     */
    private class DefaultDecorator implements Decorator {

        public String render(Object row, Context context) {
            DataObject dataObject = (DataObject) row;
            int pk = DataObjectUtils.intPKForObject(dataObject);
            return String.valueOf(pk);
        }

    }
}

