package net.sf.click.extras.cayenne;

import net.sf.click.control.Select;
import net.sf.click.control.Option;
import net.sf.click.control.Decorator;
import net.sf.click.Context;

import java.util.List;

import org.objectstyle.cayenne.DataObject;
import org.objectstyle.cayenne.DataObjectUtils;

/**
 * Control used to select Object relationships.<br/>
 * PKs are used as values for the Select, and the labels are rendered
 * using at RelationDecorator.<br/>
 * Note: <u><i>This is still experimental</i></u>
 *
 * @author Ahmed Mohombe
 */
public class RelationshipSelect extends Select {
    public static final Option EMPTY_OPTION = new Option("","");
    boolean emptyOption = true;
    Decorator decorator = new DefaultDecorator();

    public RelationshipSelect(String name) {
        super(name);
    }

    public RelationshipSelect(String name, String label) {
        super(name, label);
    }

    public void setDecorator(Decorator decorator) {
        this.decorator = decorator;
    }

    public boolean isEmptyOption() {
        return emptyOption;
    }

    public void setEmptyOption(boolean emptyOption) {
        this.emptyOption = emptyOption;
    }

    /**
     * Sets the objects that will be choosable as relations.<br/>
     * Note:<i>This method was used instead of an automatic one because in many cases
     * not all the available DataObjects are allowed to be seleted as relations.</i>
     *
     * @param relations the list with relations that will be choosable.
     */
    public void setRelations(List relations){
//        System.out.println("*-------> RelationshipSelect.setRelations");
        if(emptyOption) {
            this.add(EMPTY_OPTION);
        }
        for (int i = 0; i < relations.size(); i++) {
            DataObject relation = (DataObject) relations.get(i);
            int pk = DataObjectUtils.intPKForObject(relation);
//            System.out.println("*-------> PK for Relation:"+pk);
            Option option = new Option(String.valueOf(pk),decorator.render(relation,this.getContext()));
            add(option);
        }
    }

    /**
     * The default decorator renders the ID as label too.
     */
    private class DefaultDecorator implements Decorator {
        public String render(Object row, Context context) {
            DataObject dataObject = (DataObject) row;
            int pk = DataObjectUtils.intPKForObject(dataObject);
            return String.valueOf(pk);
        }
    }
}

