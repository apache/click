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

import net.sf.click.control.Form;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

/**
 * Provides Hibernate data aware Form control: &nbsp; &lt;form method='POST'&gt;.
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 *
 * <table class='fields'>
 * <tr>
 * <td align='left'><label>Department Name</label><span class="red">*</span></td>
 * <td align='left'><input type='text' name='name' value='' size='35' /></td>
 * </tr>
 * <tr>
 * <td align='left'><label>Description</label></td>
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
 * <a href="http://www.hibernate.org/">Hibernate</a> is an Object Relational
 * Mapping (ORM) framework.
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class HibernateForm extends Form {

    private static final long serialVersionUID = -7134198516606088333L;

    /** The Hibernate session. */
    protected Session session;

    /** The Hibernate session factory. */
    protected SessionFactory sessionFactory;

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


}
