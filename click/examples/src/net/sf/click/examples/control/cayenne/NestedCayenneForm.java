package net.sf.click.examples.control.cayenne;

import net.sf.click.extras.cayenne.CayenneForm;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.access.DataContext;

/**
 * Provides a CayenneForm which uses a Nested DataContext that is created as a
 * child of the DataContext bound to the current Thread.
 * <p/>
 * Using a nested DataContext allows the form to keep its state across multiple
 * requests even if <tt>net.sf.click.extras.cayenne.DataContextFilter</tt> is
 * configured to rollback changes after each request, which is the case for
 * click-examples.
 *
 * @author Bob Schellink
 */
public class NestedCayenneForm extends CayenneForm {

    /** A transient reference to a nested DataContext. */
    protected transient DataContext nestedDataContext;

    /**
     * Construct a form for the specified name and DataObject class.
     * 
     * @param name the form name
     * @param dataObjectClass the DataObject class
     */
    public NestedCayenneForm(String name, Class dataObjectClass) {
        super(name, dataObjectClass);
    }
    
    /**
     * Construct a form for the specified DataObject class.
     * 
     * @param dataObjectClass the DataObject class
     */
    public NestedCayenneForm(Class dataObjectClass) {
        super(dataObjectClass);
    }

    /**
     * Default constructor.
     */
    public NestedCayenneForm() {
    }

    /**
     * Return the DataContext for this Form using the following heuristics:
     * 
     * #1. return the dataObject DataContext if set
     * #2. return the {@link #nestedDataContext} instance if set
     * #3. return a nested DataContext of the thread local DataContext
     * 
     * @return a DataContext based on heuristics
     */
    public DataContext getDataContext() {
        // #1 Use DataContext associated with DataObject
        if (dataObject != null) {
            if (dataObject.getDataContext() != null) {
                return dataObject.getDataContext();
            }
        }
        // #2 Use nestedDataContext
        if (nestedDataContext != null) {
            return nestedDataContext;
        }

        DataContext dc = super.getDataContext();

        // #3 Create a nested DataContext and cache the reference
        return nestedDataContext = dc.createChildDataContext();
    }

    /**
     * Override onDestroy not to nullify the dataObject instance each request.
     */
    public void onDestroy() {
        // CayenneForm.onDestroy nullifies the dataObject. Below a
        // reference is kept to the cached dataObject
        DataObject temp = dataObject;
        super.onDestroy();
        dataObject = temp;
    }
}
