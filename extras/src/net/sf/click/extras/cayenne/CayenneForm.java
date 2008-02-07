/*
 * Copyright 2004-2007 Malcolm A. Edgar
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

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Checkbox;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import net.sf.click.util.ClickUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.validation.ValidationException;
import org.apache.cayenne.validation.ValidationResult;

/**
 * Provides Cayenne data aware Form control: &nbsp; &lt;form method='POST'&gt;.
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 *
 * <table class='fields'>
 * <tr>
 * <td align='left'><label>Organisation Name:</label><span class="red">*</span></td>
 * <td align='left'><input type='text' name='name' value='' size='35' /></td>
 * </tr>
 * <tr>
 * <td align='left'><label>Type:</label><span class="red">*</span></td>
 * <td align='left'><select><option value="PR">Private Company</option><option value="PU">Public Company</option><option value="NP">Non Profit</option></select></td>
 * </tr>
 * <tr>
 * <td align='left'><label>Description:</label></td>
 * <td align='left'><textarea name='description' cols='35' rows='3'></textarea></td>
 * </tr>
 * </table>
 * <table class="buttons" align='right'>
 * <tr><td>
 * <input type='submit' name='ok' value='  OK  '/>&nbsp;<input type='submit' name='cancel' value='Cancel'/>
 * </td></tr>
 * </table>
 *
 * </td>
 * </tr>
 * </table>
 *
 * <a href="http://objectstyle.org/cayenne/">Cayenne</a> is an Object Relational
 * Mapping (ORM) framework. The CayenneForm supports creating (inserting) and
 * saving (updating) Cayenne {@link DataObject} instances. This form will
 * automatically apply the given data objects required and max length validation
 * constraints to the form fields.
 * <p/>
 * The CayenneForm uses the thread local <tt>DataContext</tt> obtained via
 * <tt>DataContext.getThreadDataContext()</tt> for all object for persistence
 * operations.
 *
 * <h3>CayenneForm Examples</h3>
 *
 * The example below provides a <tt>Department</tt> data object creation
 * and editing page. To edit an existing department object, the object is passed
 * to the page as a request parameter. Otherwise a new department object will
 * be created when {@link #saveChanges()} is called.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> OrganisationEdit <span class="kw">extends</span> Page {
 *
 *   <span class="kw">private</span> CayenneForm form = <span class="kw">new</span> CayenneForm(<span class="st">"form"</span>, Organisation.<span class="kw">class</span>);
 *
 *    <span class="kw">public</span> OrganisationEdit() {
 *        form.add(<span class="kw">new</span> TextField(<span class="st">"name"</span>, <span class="st">"Organisation Name:"</span>, 35);
 *
 *        QuerySelect type = <span class="kw">new</span> QuerySelect(<span class="st">"type"</span>, <span class="st">"Type:"</span>);
 *        type.setQueryValueLabel(<span class="st">"organisation-types"</span>, <span class="st">"VALUE"</span>, <span class="st">"LABEL"</span>);
 *        form.add(type);
 *
 *        form.add(<span class="kw">new</span> TextArea(<span class="st">"description"</span>, <span class="st">"Description:"</span>, 35, 2);
 *
 *        form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"   OK   "</span>, <span class="kw">this</span>, <span class="st">"onOkClicked"</span>);
 *        form.add(<span class="kw">new</span> Submit(<span class="st">"cancel"</span>, <span class="kw">this</span>, <span class="st">"onCancelClicked"</span>);
 *
 *        form.setButtonAlign(<span class="st">"right"</span>);
 *        addControl(form);
 *    }
 *
 *    <span class="kw">public void</span> onGet() {
 *        Organisation organisation = (Organisation)
 *           getContext().getRequestAttribute(<span class="st">"organisation"</span>);
 *
 *        <span class="kw">if</span> (organisation != <span class="kw">null</span>) {
 *            form.setDataObject(organisation);
 *        }
 *    }
 *
 *    <span class="kw">public boolean</span> onOkClicked() {
 *        <span class="kw">if</span> (form.isValid()) {
 *           <span class="kw">if</span> (form.saveChanges()) {
 *               Organisation organisation = (Organisation) form.getDataObject(<span class="kw">false</span>);
 *               String url = getContext().getPagePath(OrganisationViewer.<span class="kw">class</span>);
 *               setRedirect(url + <span class="st">"?id="</span> + organisation.getId());
 *               <span class="kw">return false</span>;
 *           }
 *        }
 *        <span class="kw">return true</span>;
 *    }
 *
 *    <span class="kw">public boolean</span> onCancelClicked() {
 *        Organisation organisation = (Organisation) form.getDataObject(<span class="kw">false</span>);
 *        String url = getContext().getPagePath(OrganisationViewer.<span class="kw">class</span>);
 *        setRedirect(url + <span class="st">"?id="</span> + organisation.getId());
 *        <span class="kw">return false</span>;
 *    }
 * } </pre>
 *
 * Note the <tt>getDataObject(false)</tt> method is used to obtain the
 * DataObject from the Form without applying the field values to the data object.
 * This is very important when dealing with already persistent objects and you
 * dont want to apply any form changes.
 * <p/>
 * Alternatively you can save a submitted DataObject using a Service or DAO
 * pattern. For example:
 *
 * <pre class="codeJava">
 *    <span class="kw">public boolean</span> onOkClicked() {
 *        <span class="kw">if</span> (form.isValid()) {
 *           Organisation organisation = (Organisation) form.getDataObject();
 *
 *           getOrganisationService().save(organisation);
 *
 *           String url = getContext().getPagePath(OrganisationViewer.<span class="kw">class</span>);
 *           setRedirect(url + <span class="st">"?id="</span> + organisation.getId());
 *           <span class="kw">return false</span>;
 *        }
 *        <span class="kw">return true</span>;
 *    } </pre>
 *
 * <b>Please Note</b> if you are using this pattern with objects already saved,
 * take care to ensure that the form submission is valid before calling
 * {@link #getDataObject()} as this method changes the DataObject's properties
 * using the submitted form field values.
 * <p/>
 * If you don't commit the changes at this point they will still be present in
 * the session {@link DataContext} and will be applied in the next
 * <tt>commitChanges()</tt> call, which may happen in a subsequent request.
 * In these exceptional situations the object should be removed from the cache
 * DataContext using <tt>invalidateObjects()</tt> method or by reloading the
 * object from the database.
 * <p/>
 * <b>Alternatively</b> use the {@link DataContextFilter} which will
 * automatically rollback any uncommitted changes at the end of each request.
 *
 * @author Malcolm Edgar
 * @author Andrus Adamchik
 */
public class CayenneForm extends Form {

    private static final long serialVersionUID = 1;

    /** The form data object classname parameter name. */
    protected static final String FO_CLASS = "FO_CLASS";

    /** The form data object id parameter name. */
    protected static final String FO_ID = "FO_ID";

    // ----------------------------------------------------- Instance Variables

    /** The data object class name hidden field. */
    protected HiddenField classField = new HiddenField(FO_CLASS, String.class);

    /** The data object id hidden field. */
    protected HiddenField oidField = new HiddenField(FO_ID, String.class);

    /**
     * The flag specifying that object validation meta data has been applied to
     * the form fields.
     */
    protected boolean metaDataApplied = false;

    /** A transient dataObject handle for detecting a committed object. */
    protected transient DataObject dataObject;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a new CayenneForm with the given form name and <tt>DataObject</tt>
     * class.
     *
     * @param name the form name
     * @param dataObjectClass the <tt>DataObject</tt> class
     */
    public CayenneForm(String name, Class dataObjectClass) {
        super(name);

        add(oidField);
        add(classField);

        setDataObjectClass(dataObjectClass);
    }

    /**
     * Create a new CayenneForm with the given <tt>DataObject</tt> class.
     *
     * @param dataObjectClass the <tt>DataObject</tt> class
     */
    public CayenneForm(Class dataObjectClass) {
        super();

        add(oidField);
        add(classField);

        setDataObjectClass(dataObjectClass);
    }

    /**
     * Create an CayenneForm with no name or dataObjectClass.
     * <p/>
     * <b>Important Note</b> the forms's name and dataObjectClass must be defined
     * before it is valid.
     */
    public CayenneForm() {
        add(oidField);
        add(classField);
    }

    // --------------------------------------------------------- Public Methods

//    public void copyFrom(Object object) {
//        super.copyFrom(object);
//
//        final List fieldList = ClickUtils.getFormFields(this);
//
//        for (int i = 0; i < fieldList.size(); i++) {
//            Field field = (Field) fieldList.get(i);
//            if (field instanceof PropertySelect) {
//                PropertySelect select = (PropertySelect) field;
//
//                Set properties = ClickUtils.getObjectPropertyNames(object);
//
//                if (ClickUtils.hasMatchingProperty(select, properties)) {
//                    // Get property id and apply to field
//
//
//                        DataObject dataObject =
//                            DataObjectUtils.objectForPK(dataContext, doClass, doPk);
//
//                        String getterName = ClickUtils.toGetterName(getName());
//
//                        try {
//                            Method method = doClass.getMethod(getterName, null);
//
//                            DataObject property =
//                                (DataObject) method.invoke(dataObject, null);
//
//                            if (property != null) {
//                                Object propPk = DataObjectUtils.pkForObject(property);
//                                select.setValue(propPk.toString());
//                            }
//
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//
//
//
//                }
//            }
//        }
//    }

    /**
     * Return the thread local <tt>DataContext</tt> obtained via
     * <tt>DataContext.getThreadDataContext()</tt>.
     *
     * @return the thread local <tt>DataContext</tt>
     */
    public DataContext getDataContext() {
        return DataContext.getThreadDataContext();
    }

    /**
     * Return a <tt>DataObject</tt> from the form, with the form field values
     * set on the object if the copyTo parameter is true.
     * <p/>
     * Once the data object has been obtained it will be cached for the duration
     * of the request so that subsequent calls to this method will return the
     * same instance rather than creating new data object instances.
     *
     * @param copyTo option to copy the form properties to the returned data
     *  object
     * @return the data object from the form with the form field values applied
     *  to the data object properties.
     */
    public DataObject getDataObject(boolean copyTo) {
        if (dataObject != null) {
            if (copyTo) {
                copyTo(dataObject);
            }

            return dataObject;
        }

        if (StringUtils.isNotBlank(classField.getValue())) {
            try {
                Class dataClass = getDataObjectClass();

                String id = oidField.getValue();
                if (StringUtils.isNotBlank(id)) {
                    dataObject = (DataObject) DataObjectUtils.objectForPK(getDataContext(),
                                                             dataClass,
                                                             id);

                } else {
                    if (copyTo) {
                        dataObject = getDataContext().createAndRegisterNewObject(dataClass);

                    } else {
                        dataObject = (DataObject) dataClass.newInstance();
                    }
                }

                if (copyTo) {
                    copyTo(dataObject);
                }

                return dataObject;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            return null;
        }
    }

    /**
     * Return a <tt>DataObject</tt> from the form with the form field values
     * set on the object's properties.
     * <p/>
     * Once the data object has been obtained it will be cached for the duration
     * of the request so that subsequent calls to this method will return the
     * same instance rather than creating new data object instances.
     *
     * @return the <tt>DataObject</tt> with the Form field values applied to
     *      the object
     */
    public DataObject getDataObject() {
        return getDataObject(true);
    }

    /**
     * Set the given <tt>DataObject</tt> in the form, copying the object's
     * properties into the form field values. If the given data object is null
     * any form field values will be cleared, excluding hidden fields.
     *
     * @param dataObject the <tt>DataObject</tt> to set
     */
    public void setDataObject(DataObject dataObject) {

        this.dataObject = dataObject;

        if (dataObject != null) {

            if (dataObject.getClass().getName().equals(classField.getValue())) {
                if (isPersistent(dataObject)) {
                    Object pk = DataObjectUtils.pkForObject(dataObject);
                    oidField.setValue(pk.toString());
                }

                copyFrom(dataObject);

            } else {
                String msg = "Given dataObject class "
                    + dataObject.getClass().getName() + " does not match form "
                    + " class " + classField.getValue();
                throw new IllegalArgumentException(msg);
            }

        } else {
            // Clear any form data
            oidField.setValueObject(null);

            List fields = ClickUtils.getFormFields(this);
            for (int i = 0; i < fields.size(); i++) {
                Field field = (Field) fields.get(i);
                if (field instanceof HiddenField == false) {
                    field.setValue("");
                }
            }
        }
    }

    /**
     * Return the Class of the form <tt>DataObject</tt>.
     *
     * @return the Class of the form <tt>DataObject</tt>.
     */
    public Class getDataObjectClass() {
        String className = null;
        if (classField.getValueObject() != null) {
            className = classField.getValue();

        } else if (getContext() != null) {
            className = getContext().getRequestParameter(FO_CLASS);
        }

        try {
            return ClickUtils.classForName(className);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the <tt>DataObject</tt> class.
     *
     * @param dataObjectClass the <tt>DataObject</tt> class
     */
    public void setDataObjectClass(Class dataObjectClass) {
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("Null dataClass parameter");
        }
        if (!DataObject.class.isAssignableFrom(dataObjectClass)) {
            String msg = "Not a DataObject class: " + dataObjectClass;
            throw new IllegalArgumentException(msg);
        }

        classField.setValue(dataObjectClass.getName());
    }

    /**
     * Return the <tt>DataObject</tt> primary key.
     *
     * @return the <tt>DataObject</tt> primary key
     */
    public Object getDataObjectPk() {
        String value = oidField.getValue();

        if (StringUtils.isNotBlank(value)) {

            Class doClass = getDataObjectClass();

            Class pkClass = getPkClass(doClass);

            if (pkClass.isAssignableFrom(String.class)) {
                return value;

            } else if (pkClass.isAssignableFrom(Integer.class)) {
                return new Integer(value);

            } else if (pkClass.isAssignableFrom(Long.class)) {
                return new Long(value);

            } else if (pkClass.isAssignableFrom(BigDecimal.class)) {
                return new BigDecimal(value);

            } else {
                // TODO: support to other PK types
                return value;
            }

        } else {
            return null;
        }
    }

    /**
     * Save the object to the database committing all changes in the
     * <tt>DataContext</tt> and return true.
     * If a <tt>ValidationException</tt>
     * occurred then all <tt>DataContext</tt> changes will be rolled back,
     * the validation error will be set as the Form's error and the method will
     * return false.
     * <p/>
     * If no <tt>DataObject</tt> is added to the form using <tt>setDataObject()</tt>
     * then this method will: <ul>
     * <li>create and register a new object instance with the
     *    <tt>DataContext</tt></li>
     * <li>copy the form's field values to the objects properties</li>
     * <li>insert a new object record in the database</li>
     * </ul>
     * <p/>
     * If an existing persistent <tt>DataObject</tt> is added to the form using
     * <tt>setDataObject()</tt> then this method will: <ul>
     * <li>load the persistent object record from the database</li>
     * <li>copy the form's field values to the objects properties</li>
     * <li>update the object record in the database</li>
     * </ul>
     *
     * @return true if the <tt>DataObject</tt> was saved or false otherwise
     */
    public boolean saveChanges() {
        // Load data object into data context
        DataObject dataObject = getDataObject();

        if (!isPersistent(dataObject)) {
            getDataContext().registerNewObject(dataObject);
        }

        try {
            getDataContext().commitChanges();

            // update oidField with new/generated primary key value
            Object pk = DataObjectUtils.pkForObject(dataObject);
            oidField.setValue(pk.toString());
            
            return true;

        } catch (ValidationException e) {
            getDataContext().rollbackChanges();

            ValidationResult validation = e.getValidationResult();

            setError(validation != null
                     ? validation.toString()
                     : "Unknown validation error on save.");
            return false;
        }
    }

    /**
     * This method applies the object meta data to the form fields and then
     * invokes the <tt>super.onProcess()</tt> method.
     *
     * @see Form#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        applyMetaData();
        return super.onProcess();
    }

    /**
     * Clear the cached dataObject and destroy the form fields.
     *
     * @see Form#onDestroy()
     */
    public void onDestroy() {
        super.onDestroy();
        dataObject = null;
    }

    /**
     * This method applies the object meta data to the form fields and then
     * invokes the <tt>super.toString()</tt> method.
     *
     * @see Form#toString()
     *
     * @return the HTML string representation of the form
     */
    public String toString() {
        applyMetaData();

        // Ensure OID hidden field is set if available after a commit
        if (dataObject != null
            && isPersistent(dataObject)
            && oidField.getValueObject() == null) {

            Object pk = DataObjectUtils.pkForObject(dataObject);
            oidField.setValueObject(pk.toString());
        }

        return super.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Applies the <tt>DataObject</tt> validation database meta data to the
     * form fields.
     * <p/>
     * The field validation attributes include:
     * <ul>
     * <li>required - is a mandatory field and cannot be null</li>
     * <li>maxLength - the maximum length of the field</li>
     * </ul>
     */
    protected void applyMetaData() {
        if (metaDataApplied) {
            return;
        }

        try {
            Class dataClass = ClickUtils.classForName(classField.getValue());

            ObjEntity objEntity =
                getDataContext().getEntityResolver().lookupObjEntity(dataClass);

            setObjEntityFieldConstrains(null, objEntity);

            Iterator relationships = objEntity.getRelationships().iterator();
            while (relationships.hasNext()) {
                ObjRelationship objRelationship =
                    (ObjRelationship) relationships.next();

                String relName = objRelationship.getName();
                ObjEntity relObjEntity =
                    (ObjEntity) objRelationship.getTargetEntity();

                setObjEntityFieldConstrains(relName, relObjEntity);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        metaDataApplied = true;
    }

    /**
     * Return true if the given dataObject is persistent.
     *
     * @param dataObject the DataObject to test
     * @return true if the given dataObject is persistent
     */
    protected boolean isPersistent(DataObject dataObject) {
        return dataObject.getPersistenceState() != PersistenceState.TRANSIENT
                && dataObject.getPersistenceState() != PersistenceState.NEW;
    }

    /**
     * Set the <tt>ObjEntity</tt> meta data constraints on the form fields.
     *
     * @param relationshipName the object relationship name, null if the
     *      ObjEntity of the CayenneForm
     * @param objEntity the ObjEntity to lookup meta data from
     */
    protected void setObjEntityFieldConstrains(String relationshipName,
            ObjEntity objEntity) {

        Iterator attributes = objEntity.getAttributes().iterator();
        while (attributes.hasNext()) {
            ObjAttribute objAttribute = (ObjAttribute) attributes.next();
            DbAttribute dbAttribute = objAttribute.getDbAttribute();

            String fieldName = objAttribute.getName();
            if (relationshipName != null) {
                fieldName = relationshipName + "." + fieldName;
            }

            Field field = getField(fieldName);

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

    /**
     * Return the primary key class for the given DataObject class.
     *
     * @param dataObjectClass the DataObject class to get the primary key for
     * @return the primary key class for the given DataObject class
     */
    protected Class getPkClass(Class dataObjectClass) {
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("Null dataObjectClass parameter.");
        }

        ObjEntity objEntity =
            getDataContext().getEntityResolver().lookupObjEntity(dataObjectClass);

        if (objEntity == null) {
            throw new CayenneRuntimeException("Unmapped DataObject Class: "
                    + dataObjectClass.getName());
        }

        DbEntity dbEntity = objEntity.getDbEntity();
        if (dbEntity == null) {
            throw new CayenneRuntimeException("No DbEntity for ObjEntity: "
                    + objEntity.getName());
        }

        List pkAttributes = dbEntity.getPrimaryKey();
        if (pkAttributes.size() != 1) {
            throw new CayenneRuntimeException("PK contains "
                    + pkAttributes.size()
                    + " columns, expected 1.");
        }

        DbAttribute attr = (DbAttribute) pkAttributes.get(0);

        String className = TypesMapping.getJavaBySqlType(attr.getType());

        try {
            return ClickUtils.classForName(className);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
