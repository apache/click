package click.cayenne;

import java.util.List;
import java.util.Map;

import net.sf.click.util.Format;

import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.DataObject;
import org.objectstyle.cayenne.DataObjectUtils;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.map.DbAttribute;
import org.objectstyle.cayenne.map.ObjEntity;
import org.objectstyle.cayenne.util.Util;

/**
 * Provides a Cayenne customised Click <tt>Format</tt> object.
 * 
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class CayenneFormat extends Format {

    /**
     * Return the ObjectId for the given DataObject. Currently limited to 
     * single in primary key objects.
     * 
     * @return the string value of the ObjectId for the given DataObject
     */
    public String id(DataObject object) {
        if (object != null) {
            return "" + DataObjectUtils.intPKForObject(object);
        } else {
            return null;
        }
    }

    /**
     * Return the object id string for the given data row and entity name.
     * 
     * @param dataRow the current data row map
     * @param entityName the name of the entity
     * @return the object id string
     */
    public String id(Map dataRow, String entityName) {
        DataContext dataContext = DataContext.getThreadDataContext(); 
        ObjEntity entity =
            dataContext.getEntityResolver().lookupObjEntity(entityName);

        List pk = entity.getDbEntity().getPrimaryKey();
        if (pk.size() == 1) {
            DbAttribute attribute = (DbAttribute) pk.get(0);
            return String.valueOf(dataRow.get(attribute.getName()));
            
        } else {
            String msg = "Multi-column keys are not yet supported as ids"; 
            throw new CayenneRuntimeException(msg);
        }
    }

    /**
     * Trims long strings substituting middle part with "...".
     * 
     * @param value the string value to limit the length of, must be at least
     *  5 characters long, or an IllegalArgumentException is thrown.
     * @param maxlength the maximum string length
     * @return a length limited string
     */
    public String prettyTrim(String value, int maxlength) {
        return Util.prettyTrim(value, maxlength);
    }

}
