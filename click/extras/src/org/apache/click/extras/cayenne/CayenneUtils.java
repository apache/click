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
package org.apache.click.extras.cayenne;

import java.util.Collection;
import java.util.List;
import org.apache.cayenne.BaseContext;

import org.apache.click.util.ClickUtils;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.Query;
import org.apache.click.control.Checkbox;
import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.click.control.TextArea;
import org.apache.click.control.TextField;
import org.apache.commons.lang.Validate;

/**
 * Provides Cayenne DataObject utility methods to looking up object by their
 * primary key.
 * <p/>
 * This class was derived from the Cayenne <tt>DataObjectUtils</tt> class.
 */
public final class CayenneUtils {


    /**
     * Applies the <tt>DataObject class</tt> validation database meta data to the
     * form fields.
     * <p/>
     * The field validation attributes include:
     * <ul>
     * <li>required - is a mandatory field and cannot be null</li>
     * <li>maxLength - the maximum length of the field</li>
     * </ul>
     *
     * @param form the form which fields to apply metadata to
     * @param dataObjectClass the dataObject class which metadata to apply
     */
    public static void applyMetaData(Form form, Class<?> dataObjectClass) {
        Validate.notNull(dataObjectClass, "Null dataObjectClass parameter");

        try {
            ObjectContext oc = BaseContext.getThreadObjectContext();
            ObjEntity objEntity =
                oc.getEntityResolver().lookupObjEntity(dataObjectClass);

            setObjEntityFieldConstraints(form, null, objEntity);

            for (ObjRelationship objRelationship : objEntity.getRelationships()) {
                String relName = objRelationship.getName();
                ObjEntity relObjEntity =
                        (ObjEntity) objRelationship.getTargetEntity();

                setObjEntityFieldConstraints(form, relName, relObjEntity);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a new ObjectId for the given ObjectContext, data object class and
     * primary key.
     *
     * @param objectContext the Cayenne ObjectContext
     * @param dataObjectClass the class of the Cayenne DataObject
     * @param pk the data object primary key
     * @return a new Cayenne ObjectId
     */
    public static ObjectId createObjectId(ObjectContext objectContext,
            Class dataObjectClass, Object pk) {

        Validate.notNull(objectContext, "Null objectContext parameter");
        Validate.notNull(objectContext, "Null dataObjectClass parameter");
        Validate.notNull(objectContext, "Null pk parameter");

        ObjEntity entity =
            objectContext.getEntityResolver().lookupObjEntity(dataObjectClass);

        if (entity == null) {
            String msg = "Non-existent ObjEntity for class: " + dataObjectClass;
            throw new RuntimeException(msg);
        }

        DbEntity dbEntity = entity.getDbEntity();
        if (dbEntity == null) {
            String msg = "No DbEntity for ObjEntity: " + entity.getName();
            throw new RuntimeException(msg);
        }

        Collection<DbAttribute> pkAttributes = dbEntity.getPrimaryKeys();
        if (pkAttributes.size() != 1) {
            String msg = "PK contains " + pkAttributes.size()
                + " columns, expected 1.";
            throw new RuntimeException(msg);
        }

        DbAttribute attr = pkAttributes.iterator().next();

        return new ObjectId(entity.getName(), attr.getName(), pk);
    }

    /**
     * Return the DataObject for the given context, data object class and
     * primary key value. This method will refresh the
     *
     * @param objectContext the Cayenne ObjectContext for the data object
     * @param dataObjectClass the class of the Cayenne DataObject
     * @param pk the primary key of the DataObject
     * @return the resolved DataObject value for the primary key
     */
    public static DataObject getObjectForPK(ObjectContext objectContext,
            Class dataObjectClass, Object pk) {

        Validate.notNull(objectContext, "Null objectContext parameter");
        Validate.notNull(objectContext, "Null dataObjectClass parameter");
        Validate.notNull(objectContext, "Null pk parameter");

        ObjectId objectId = createObjectId(objectContext, dataObjectClass, pk);

        Query query = new ObjectIdQuery(objectId, false, ObjectIdQuery.CACHE_REFRESH);

        List objects = objectContext.performQuery(query);

        if (objects.isEmpty()) {
            return null;

        } else if (objects.size() > 1) {
            String msg = "Expected zero or one object, instead query matched: "
                + objects.size();
            throw new RuntimeException(msg);
        }

        return (DataObject) objects.get(0);
    }

    /**
     * Return the primary key class for the given DataObject class.
     *
     * @param objectContext the Cayenne ObjectContext for the data object
     * @param dataObjectClass the DataObject class to get the primary key for
     * @return the primary key class for the given DataObject class
     */
    public static Class getPkClass(ObjectContext objectContext, Class dataObjectClass) {

        Validate.notNull(objectContext, "Null objectContext parameter.");
        Validate.notNull(dataObjectClass, "Null dataObjectClass parameter.");

        ObjEntity objEntity =
            objectContext.getEntityResolver().lookupObjEntity(dataObjectClass);

        if (objEntity == null) {
            throw new CayenneRuntimeException("Unmapped DataObject Class: "
                    + dataObjectClass.getName());
        }

        DbEntity dbEntity = objEntity.getDbEntity();
        if (dbEntity == null) {
            throw new CayenneRuntimeException("No DbEntity for ObjEntity: "
                    + objEntity.getName());
        }

        Collection<DbAttribute> pkAttributes = dbEntity.getPrimaryKeys();
        if (pkAttributes.size() != 1) {
            throw new CayenneRuntimeException("PK contains "
                    + pkAttributes.size()
                    + " columns, expected 1.");
        }

        DbAttribute attr = pkAttributes.iterator().next();

        String className = TypesMapping.getJavaBySqlType(attr.getType());

        try {
            return ClickUtils.classForName(className);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Return the database primary key column name for the given data object.
     *
     * @param objectContext the Cayenne ObjectContext for the data object
     * @param dataObjectClass the class of the data object
     * @return the primary key column name
     */
    public static String getPkName(ObjectContext objectContext, Class dataObjectClass) {

        Validate.notNull(objectContext, "Null objectContext parameter.");
        Validate.notNull(dataObjectClass, "Null dataObjectClass parameter.");

        ObjEntity objEntity =
            objectContext.getEntityResolver().lookupObjEntity(dataObjectClass);

        if (objEntity == null) {
            throw new CayenneRuntimeException("Unmapped DataObject Class: "
                    + dataObjectClass.getName());
        }

        DbEntity dbEntity = objEntity.getDbEntity();
        if (dbEntity == null) {
            throw new CayenneRuntimeException("No DbEntity for ObjEntity: "
                    + objEntity.getName());
        }

        Collection<DbAttribute> pkAttributes = dbEntity.getPrimaryKeys();
        if (pkAttributes.size() != 1) {
            throw new CayenneRuntimeException("PK contains "
                    + pkAttributes.size()
                    + " columns, expected 1.");
        }

        DbAttribute attr = pkAttributes.iterator().next();

        return attr.getName();
    }

    /**
     * Set the <tt>ObjEntity</tt> meta data constraints on the form fields.
     *
     * @param form the form on which to set the field constraints
     * @param relationshipName the object relationship name, null if the
     *      ObjEntity of the CayenneForm
     * @param objEntity the ObjEntity to lookup meta data from
     */
    private static void setObjEntityFieldConstraints(Form form, String relationshipName, ObjEntity objEntity) {

        for (ObjAttribute objAttribute : objEntity.getAttributes()) {
            DbAttribute dbAttribute = objAttribute.getDbAttribute();

            String fieldName = objAttribute.getName();
            if (relationshipName != null) {
                fieldName = relationshipName + "." + fieldName;
            }

            Field field = form.getField(fieldName);

            if (field != null) {
                if (!field.isRequired() && dbAttribute.isMandatory()) {
                    if (!(field instanceof Checkbox)) {
                        field.setRequired(true);
                    }
                }

                int maxlength = dbAttribute.getMaxLength();
                if (maxlength != -1) {
                    if (field instanceof TextField) {
                        TextField textField = (TextField) field;
                        if (textField.getMaxLength() == 0) {
                            textField.setMaxLength(maxlength);
                        }
                    } else if (field instanceof TextArea) {
                        TextArea textArea = (TextArea) field;
                        if (textArea.getMaxLength() == 0) {
                            textArea.setMaxLength(maxlength);
                        }
                    }
                }
            }
        }
    }
}
