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
package net.sf.click.extras.hibernate;

import java.io.Serializable;

import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;

import org.apache.commons.lang.StringUtils;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

/**
 * Provides Hibernate data aware Form control: &nbsp; &lt;form method='POST'&gt;.
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 *
 * <table class='fields'>
 * <tr>
 * <td align='left'><b><label>First Name:</label></b></td>
 * <td align='left'><input type='text' name='name' value='' size='20' /></td>
 * </tr>
 * <tr>
 * <td align='left'><label>Middle Names:</label></td>
 * <td align='left'><input type='text' name='name' value='' size='20' /></td>
 * </tr>
 * <tr>
 * <td align='left'><b><label>Family Name:</label></b></td>
 * <td align='left'><input type='text' name='name' value='' size='20' /></td>
 * </tr>
 * </table>
 * <table class="buttons" align='right'>
 * <tr><td>
 * <input type='submit' name='ok' value='   OK   '/>&nbsp;<input type='submit' name='cancel' value='Cancel'/>
 * </td></tr>
 * </table>
 *
 * </td>
 * </tr>
 * </table>
 *
 * <a href="http://www.hibernate.org/">Hibernate</a> is an Object Relational
 * Mapping (ORM) framework. The HibernatteForm supports creating (inserting) and
 * saving (updating) POJO instances. This form will automatically apply the
 * given objects property required validation constraints to the form fields.
 * <p/>
 * The HibernatteForm uses the thread local <tt>Session</tt> obtained via
 * <tt>SessionContext.getSession()</tt> for all object for persistence
 * operations. To use an alternative Session source override set the forms
 * getSession() method.
 * <p/>
 * The example below provides a <tt>User</tt> data object creation
 * and editing page. To edit an existing user object, the object is passed
 * to the page as a request parameter. Otherwise a new user object will
 * be created when {@link #saveChanges()} is called.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> UserEdit <span class="kw">extends</span> Page {
 *
 *   <span class="kw">private</span> HibernateForm form = <span class="kw">new</span> HibernateForm(<span class="st">"form"</span>, User.<span class="kw">class</span>);
 *
 *    <span class="kw">public</span> UserEdit() {
 *        form.add(<span class="kw">new</span> TextField(<span class="st">"firstName"</span>);
 *        form.add(<span class="kw">new</span> TextField(<span class="st">"middleNames"</span>);
 *        form.add(<span class="kw">new</span> TextField(<span class="st">"FamilyName"</span>);
 *
 *        form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"   OK   "</span>, <span class="kw">this</span>, <span class="st">"onOkClicked"</span>);
 *        form.add(<span class="kw">new</span> Submit(<span class="st">"cancel"</span>, <span class="kw">this</span>, <span class="st">"onCancelClicked"</span>);
 *
 *        form.setButtonAlign(<span class="st">"right"</span>);
 *        form.setLabelRequiredPrefix(<span class="st">"&lt;b&gt;"</span>);
 *        form.setLabelRequiredSuffix(<span class="st">"&lt;/b&gt;"</span>);
 *        addControl(form);
 *    }
 *
 *    <span class="kw">public void</span> setUser(User user) {
 *        form.setValueObject(user);
 *    }
 *
 *    <span class="kw">public boolean</span> onOkClicked() {
 *        <span class="kw">if</span> (form.isValid()) {
 *           <span class="kw">if</span> (form.saveChanges()) {
 *               setRedirect(<span class="st">"user-list.htm"</span>);
 *           }
 *        }
 *        <span class="kw">return true</span>;
 *    }
 *
 *    <span class="kw">public boolean</span> onCancelClicked() {
 *        setRedirect(<span class="st">"user-list.htm"</span>);
 *        <span class="kw">return false</span>;
 *    }
 * } </pre>
 *
 * @see SessionContext
 * @see SessionFilter
 *
 * @author Malcolm Edgar
 */
public class HibernateForm extends Form {

    private static final long serialVersionUID = -7134198516606088333L;

    /** The Hibernate session. */
    protected Session session;

    /** The Hibernate session factory. */
    protected SessionFactory sessionFactory;

    /** The value object identifier hidden field. */
    protected HiddenField oidField;

    /** The value object class name hidden field. */
    protected HiddenField classField;

    /**
     * The flag specifying that object validation meta data has been applied to
     * the form fields.
     */
    protected boolean metaDataApplied = false;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a new HibernateForm with the given form name and value object
     * class.
     *
     * @param name the form name
     * @param valueClass the value object class
     */
    public HibernateForm(String name, Class valueClass) {
        super(name);

        if (valueClass == null) {
            throw new IllegalArgumentException("Null valueClass parameter");
        }

        classField = new HiddenField("VOCLASS", String.class);
        classField.setValue(valueClass.getName());
        add(classField);

        ClassMetadata classMetadata =
            getSessionFactory().getClassMetadata(valueClass);
        Type identifierType = classMetadata.getIdentifierType();
        oidField = new HiddenField("VOID", identifierType.getReturnedClass());
        add(oidField);
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Return the form Hibernate <tt>Session</tt>. If form session is not
     * defined this method will obtain a session from the
     * {@link SessionContext}.
     * <p/>
     * Applications using alternative Hibernate <tt>Session</tt> sources should
     * set the form's session using the {@link #setSession(Session)} method.
     *
     * @return the form Hibernate session
     */
    public Session getSession() {
        if (session == null) {
            session = SessionContext.getSession();
        }
        return session;
    }

    /**
     * Set the user's Hibernate <tt>Session</tt>.
     *
     * @param session the user's Hibernate session
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Return the application Hibernate <tt>SessionFactory</tt>.
     * If session factory is not defined this method will obtain the session
     * factory from the {@link SessionContext}.
     * <p/>
     * Applications using an alternative Hibernate <tt>SessionFactory</tt>
     * sources should set the form's session factory using the
     * {@link #setSessionFactory(SessionFactory)} method.
     *
     * @return the user's Hibernate session
     */
    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = SessionContext.getSessionFactory();
        }
        return sessionFactory;
    }

    /**
     * Set the form Hibernate <tt>SessionFactory</tt>.
     *
     * @param sessionFactory the Hibernate SessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Return a Hibernate value object from the form with the form field values
     * copied into the object's properties.
     *
     * @return the Hibernate object from the form with the form field values
     * applied to the object properties.
     */
    public Object getValueObject() {
        if (StringUtils.isNotBlank(classField.getValue())) {
            try {
                Class valueClass = Class.forName(classField.getValue());

                Serializable oid = (Serializable) oidField.getValueObject();

                Object valueObject = null;
                if (oid != null) {
                    valueObject = getSession().load(valueClass, oid);
                } else {
                    valueObject = valueClass.newInstance();
                }

                copyTo(valueObject);

                return valueObject;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Set the given Hibernate value object in the form, copying the object's
     * properties into the form field values.
     *
     * @param valueObject the Hibernate value object to set
     */
    public void setValueObject(Object valueObject) {
        if (valueObject != null) {
            ClassMetadata classMetadata =
                getSessionFactory().getClassMetadata(valueObject.getClass());

            Object identifier =
                classMetadata.getIdentifier(valueObject, EntityMode.POJO);
            oidField.setValueObject(identifier);

            copyFrom(valueObject);
        }
    }

    /**
     * Save or update the object to the database and return true.
     * If a <tt>HibernateException</tt> occurs the <tt>Transaction</tt> will be
     * rolled back the exception will be raised.
     * <p/>
     * If no object is added to the form using <tt>setValueObject()</tt>
     * then this method will: <ul>
     * <li>create a new instance of the Class</li>
     * <li>copy the form's field values to the objects properties</li>
     * <li>save the new object to the database</li>
     * </ul>
     * <p/>
     * If an existing persistent object is added to the form using
     * <tt>setValueObject()</tt> then this method will: <ul>
     * <li>load the persistent object record from the database</li>
     * <li>copy the form's field values to the objects properties</li>
     * <li>update the object in the database</li>
     * </ul>
     *
     * @return true if the object was saved or false otherwise
     * @throws HibernateException if a persistence error occured
     */
    public boolean saveChanges() throws HibernateException {
        Object valueObject = getValueObject();

        Transaction transaction = null;
        try {
            Session session = getSession();

            transaction = session.beginTransaction();

            session.saveOrUpdate(valueObject);

            transaction.commit();

            return true;

        } catch (HibernateException he) {
            if (transaction != null) {
                try {
                   transaction.rollback();
                } catch (HibernateException re) {
                    // ignore
                }
            }
            throw he;
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
     * This method applies the object meta data to the form fields and then
     * invokes the <tt>super.toString()</tt> method.
     *
     * @see Form#toString()
     *
     * @return the HTML string representation of the form
     */
    public String toString() {
        applyMetaData();
        return super.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Applies the <tt>ClassMetadata</tt> validation database meta data to the
     * form fields.
     * <p/>
     * The field validation attributes include:
     * <ul>
     * <li>required - is a mandatory field and cannot be null</li>
     * </ul>
     */
    protected void applyMetaData() {
        if (metaDataApplied) {
            return;
        }

        try {
            Class valueClass = Class.forName(classField.getValue());

            ClassMetadata metadata =
                getSessionFactory().getClassMetadata(valueClass);

            String[] propertyNames = metadata.getPropertyNames();

            boolean[] propertyNullability = metadata.getPropertyNullability();

            for (int i = 0; i < propertyNames.length; i++) {
                Field field = getField(propertyNames[i]);
                if (field != null) {
                    field.setRequired(propertyNullability[i]);
                }
            }

        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        }

        metaDataApplied = true;
    }

}
