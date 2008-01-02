package net.sf.click.examples.domain;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.PersistenceState;

/**
 * Provides an base entity CayenneDataObject class with id getter and isNew
 * methods. This class should be extended by the auto package classes.
 *
 * @author Malcolm Edgar
 */
public class BaseEntity extends CayenneDataObject {

    private static final long serialVersionUID = 1L;

    /**
     * Convenience method to get an id that may be used by the view. There is
     * no setter as id is managed by Cayenne.
     */
    public Integer getId() {
        return (Integer) DataObjectUtils.pkForObject(this);
    }

    /**
     * Return true if the object is new or transient object.
     *
     * @return true if the object is new or transient object
     */
    public boolean isNew() {
        return getPersistenceState() == PersistenceState.TRANSIENT
                || getPersistenceState() == PersistenceState.NEW;
    }

}
