package net.sf.click.examples.control.cayenne;

import net.sf.click.extras.control.IntegerField;

/**
 * This IntegerField class preserves its value when it is copied from
 * an entity property that is null.
 * 
 * @author Bob Schellink
 */
public class CayenneIntegerField extends IntegerField {

    /**
     * Construct a new field for the specified name and label.
     *
     * @param name the field name
     * @param label the field label
     */
    public CayenneIntegerField(String name, String label) {
        super(name, label);
    }

    /**
     * #setValueObject is invoked when a Form attempts to bind an Object to its
     * fields.
     * 
     * When a bean property with value 'null' is bound to IntegerField,
     * setValueObject will nullify the Integer value. This is not always
     * desirable as sometimes we want the value to be preserved.
     * 
     * Here we override #setValueObject and explicitly check if the passed in
     * object is null. If it is we return without changing the value object.
     * 
     * @param object the object value to set
     */
    public void setValueObject(Object object) {
        if (object == null) {
            return;
        }
        super.setValueObject(object);
    }
}
