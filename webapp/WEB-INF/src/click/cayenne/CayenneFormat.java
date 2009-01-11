/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package click.cayenne;

import java.util.List;
import java.util.Map;

import org.apache.click.util.Format;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.util.Util;

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
