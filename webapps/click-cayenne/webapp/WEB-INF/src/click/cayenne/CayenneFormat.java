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
 * Provides a Cayenne adapted Click <tt>Format</tt> object.
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
        return (object != null) ? "" + 
                DataObjectUtils.intPKForObject(object) : null;
    }

    public String id(Map dataRow, String entityName) {
        ObjEntity entity = DataContext
                .getThreadDataContext()
                .getEntityResolver()
                .lookupObjEntity(entityName);

        List pk = entity.getDbEntity().getPrimaryKey();
        if (pk.size() == 1) {
            DbAttribute attribute = (DbAttribute) pk.get(0);
            return String.valueOf(dataRow.get(attribute.getName()));
        }

        throw new CayenneRuntimeException(
                "Multi-column keys are not yet supported as ids");
    }

    public String prettyTrim(String string, int maxlength) {
        return Util.prettyTrim(string, maxlength);
    }
    
    /**
     * Return an emptry string value.
     * 
     * @see Format#getEmptyString()
     */
    public String getEmptyString() {
        return "";
    }
}
