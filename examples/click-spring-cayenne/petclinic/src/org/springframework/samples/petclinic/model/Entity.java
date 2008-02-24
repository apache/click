package org.springframework.samples.petclinic.model;

import org.objectstyle.cayenne.CayenneDataObject;
import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.DataObject;
import org.objectstyle.cayenne.DataObjectUtils;
import org.objectstyle.cayenne.PersistenceState;

/**
 * Superclass of Pet Clinic entity objects.
 */
public class Entity extends CayenneDataObject {
    public boolean isNew() {
        return getPersistenceState() == PersistenceState.TRANSIENT
                || getPersistenceState() == PersistenceState.NEW;
    }

    /**
     * Convenience method to get an id that may be used by the view. There is no
     * setter as id is managed by Cayenne.
     */
    public int getId() {
        return isNew() ? -1 : DataObjectUtils.intPKForObject(this);
    }

    // persistence by reacability support...
    // the code below is here for Cayenne 1.1 compatibility...
    // CayenneDataObject in 1.2 includes this already

    public void setToOneTarget(String relationshipName,
                               DataObject value,
                               boolean setReverse) {

        willConnect(relationshipName, value);
        super.setToOneTarget(relationshipName, value, setReverse);
    }

    public void addToManyTarget(String relName,
                                DataObject value,
                                boolean setReverse) {
        willConnect(relName, value);
        super.addToManyTarget(relName, value, setReverse);
    }

    protected void willConnect(String relationshipName, DataObject dataObject) {
        // first handle most common case - both objects are in the same
        // DataContext or target is null
        if (dataObject == null
                || this.getDataContext() == dataObject.getDataContext()) {
            return;
        } else if (this.getDataContext() == null
                && dataObject.getDataContext() != null) {
            dataObject.getDataContext().registerNewObject(this);
        } else if (this.getDataContext() != null
                && dataObject.getDataContext() == null) {
            this.getDataContext().registerNewObject(dataObject);
        } else {
            throw new CayenneRuntimeException(
                    "Cannot set object as destination of relationship "
                            + relationshipName
                            + " because it is in a different DataContext");
        }
    }
}