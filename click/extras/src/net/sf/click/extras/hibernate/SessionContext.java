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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Provides a thread local Hibernate Session context class. The Hibernate
 * configuration should be defined in class path file:
 * <pre class="codeConfig">
 * /hibernamte.cfg.xml </pre>
 *
 * Or alternatively by using System properties.
 * <p/>
 * The Hibernate initialization code used by <tt>SessionContext</tt> is:
 *
 * <pre class="codeJava">
 * Configuration configuration = <span class="kw">new</span> Configuration();
 * configuration.setProperties(System.getProperties());
 * configuration.configure();
 * SessionFactory sessionFactory = configuration.buildSessionFactory(); </pre>
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class SessionContext {

    /** The Hibernate session factory. */
    private static final SessionFactory sessionFactory;

    /** The ThreadLocal session holder. */
    private static final ThreadLocal sessionHolder = new ThreadLocal();

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.setProperties(System.getProperties());
            configuration.configure();
            sessionFactory = configuration.buildSessionFactory();

        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Get the Session for the current Thread, creating one if neccessary.
     *
     * @return the Session fro the current Thread.
     * @throws HibernateException if an error occurs opening the session
     */
    public static Session getSession() throws HibernateException {
        Session session = (Session) sessionHolder.get();

        if (session == null) {
            session = getSessionFactory().openSession();
            sessionHolder.set(session);
        }

        return session;
    }

    /**
     * Close the Session held by the current Thread. The close session will
     * also be removed from the ThreadLocal variable.
     *
     * @throws HibernateException if an error occurs closing the session
     */
    public static void close() throws HibernateException {
        Session session = (Session) sessionHolder.get();

        if (session != null && session.isOpen()) {
            session.close();
        }

        sessionHolder.set(null);
    }

    /**
     * Return true if a session is open.
     *
     * @return true if a session is currently open.
     */
    public static boolean hasSession() {
        return (sessionHolder.get() != null);
    }

    /**
     * Return the Hibernate SesssionFactory.
     *
     * @return the Hibernate SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Create a new instance of Query for the given query string.
     * <p/>
     * This method provides a convenience wrapper around the corresponding
     * <tt>Session</tt> method.
     *
     * @param queryString a Hibernate query
     * @return a Hibernate query
     * @throws HibernateException if an error occurs creating the query
     */
    public static Query createQuery(String queryString)
        throws HibernateException {

        return getSession().createQuery(queryString);
    }

    /**
     * Obtain an instance of Query for a named query string defined in the
     * mapping file.
     * <p/>
     * This method provides a convenience wrapper around the corresponding
     * <tt>Session</tt> method.
     *
     * @param queryName the name of a query defined externally
     * @return a Hibernate query
     * @throws HibernateException if an error occurs creating the query
     */
    public static Query getNamedQuery(String queryName)
        throws HibernateException {

        return getSession().getNamedQuery(queryName);
    }

}
