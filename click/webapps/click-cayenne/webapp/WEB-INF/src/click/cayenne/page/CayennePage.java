package click.cayenne.page;

import net.sf.click.Page;

import org.objectstyle.cayenne.DataObject;
import org.objectstyle.cayenne.DataObjectUtils;
import org.objectstyle.cayenne.access.DataContext;

/**
 * A superclass of Click pages that supports storing and retrieving DataObjects 
 * in the session. It stores ObjectIds and retrieves DataObjects in a context of 
 * request processing relying on thread-bound DataContext.
 * 
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class CayennePage extends Page {
    
    /**
     * Return the DataContext for the current thread.
     * 
     * @return the DataContext for the current thread
     */
    public DataContext getDataContext() {
        return DataContext.getThreadDataContext();
    }
    
    /**
     * Return the DataObject for the given class and primary key.
     * 
     * @param aClass the DataObject class
     * @param primaryKey the DataObject primary key
     * @return the DataObject for the given class and primary key
     */
    public DataObject getDataObject(Class aClass, int primaryKey) {
        return DataObjectUtils.objectForPK(getDataContext(),  
                                           aClass,
                                           primaryKey);
    }
    
    /**
     * Return the DataObject for the given class and primary key.
     * 
     * @param aClass the DataObject class
     * @param primaryKey the DataObject primary key
     * @return the DataObject for the given class and primary key
     */
    public DataObject getDataObject(Class aClass, Integer primaryKey) {
        return DataObjectUtils.objectForPK(getDataContext(),  
                                           aClass,
                                           primaryKey.intValue());
    } 
    
    /**
     * Return the DataObject for the given class and primary key.
     * 
     * @param aClass the DataObject class
     * @param primaryKey the DataObject primary key
     * @return the DataObject for the given class and primary key
     */
    public DataObject getDataObject(Class aClass, String primaryKey) {
        int objectPk = Integer.parseInt(primaryKey);
        System.out.println(objectPk + "," + getDataContext() + "," + aClass);
        return DataObjectUtils.objectForPK(getDataContext(),  
                                           aClass,
                                           objectPk);
    } 

}
