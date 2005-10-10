/*
 * Copyright 2004-2005 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.extras.cayenne;

import java.util.Iterator;

import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;

import org.apache.commons.lang.StringUtils;
import org.objectstyle.cayenne.DataObject;
import org.objectstyle.cayenne.DataObjectUtils;
import org.objectstyle.cayenne.PersistenceState;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.map.DbAttribute;
import org.objectstyle.cayenne.map.ObjAttribute;
import org.objectstyle.cayenne.map.ObjEntity;
import org.objectstyle.cayenne.validation.ValidationException;
import org.objectstyle.cayenne.validation.ValidationResult;

/**
 * Provides Cayenne data aware Form control.
 *
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class CayenneForm extends Form {
    
    private static final long serialVersionUID = 1L;
    
    protected HiddenField pkField = new HiddenField("DOPK", Integer.class);
    protected HiddenField classField = new HiddenField("DOCLASS", String.class);
    protected boolean metaDataApplied = false;

    public CayenneForm(String name, Class dataClass) {
        super(name);
        add(pkField);
        add(classField);
        
        if (!DataObject.class.isAssignableFrom(dataClass)) {
            String msg = "Not a DataObject class: " + dataClass;
            throw new IllegalArgumentException(msg);
        }
        
        classField.setValue(dataClass.getName());
    }
    
    public DataObject getDataObject() {
        if (StringUtils.isNotBlank(classField.getValue())) {
            try {
                Class dataClass = Class.forName(classField.getValue());
                DataObject dataObject = null;
                
                Integer id = (Integer) pkField.getValueObject();
                if (id != null) {
                    dataObject = DataObjectUtils.objectForPK
                        (getDataContext(), dataClass,  id);
                } else {
                    dataObject = 
                        getDataContext().createAndRegisterNewObject(dataClass);
                }
                
                copyTo(dataObject, true);
                
                return dataObject;
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    
    public void setDataObject(DataObject dataObject) {
        if (dataObject != null) {
            if (dataObject.getPersistenceState() != PersistenceState.TRANSIENT) {
                int pk = DataObjectUtils.intPKForObject(dataObject);
                pkField.setValue(new Integer(pk));                
            }
            copyFrom(dataObject, true);
        }
    }
    
    public boolean saveChanges() {
        // Load data object into data context
        getDataObject();

        try {
            getDataContext().commitChanges();
            return true;
        }
        catch (ValidationException e) {
            getDataContext().rollbackChanges();

            ValidationResult validation = e.getValidationResult();

            setError(validation != null
                     ? validation.toString()
                     : "Unknown validation error on save.");
            return false;
        }
    }
    
    public boolean onProcess() {
        applyMetaData();
        return super.onProcess();
    }
    
    public String toString() {
        applyMetaData();
        return super.toString();
    }
    
    protected void applyMetaData() {
        if (metaDataApplied) {
            return;
        }
        
        try {
            Class dataClass = Class.forName(classField.getValue());
            
            ObjEntity objEntity = 
                getDataContext().getEntityResolver().lookupObjEntity(dataClass);

            Iterator attributes = objEntity.getAttributes().iterator();
            while (attributes.hasNext()) {
                ObjAttribute objAttribute = (ObjAttribute) attributes.next();
                DbAttribute dbAttribute = objAttribute.getDbAttribute();
                
                Field field = getField(dbAttribute.getName());
                if (field != null) {
                    field.setRequired(dbAttribute.isMandatory());

                    int maxlength = dbAttribute.getMaxLength();
                    if (maxlength != -1) {
                        if (field instanceof TextField) {
                            ((TextField) field).setMaxLength(maxlength);
                        } else if (field instanceof TextArea) {
                            ((TextArea) field).setMaxLength(maxlength);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        metaDataApplied = true;
    }
    
    protected DataContext getDataContext() {
        return DataContext.getThreadDataContext();
    }
}
