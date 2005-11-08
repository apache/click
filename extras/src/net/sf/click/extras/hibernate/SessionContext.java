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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

/**
 * Provides a thread local Hibernate Session context class.
 * <p/>
 * The hibernate configuration must be defined in class path file:
 * <pre class="codeConfig">
 * /hibernamte.cfg.xml </pre>
 *
 * This class was adapted from <tt>HibernateUtil</tt> helper class.
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class SessionContext {

    /** The Hibernate session factory. */
    private static final SessionFactory sessionFactory;

    /** The ThreadLocal session holder.. */
    private static final ThreadLocal sessionHolder = new ThreadLocal();

    static {
        try {
            Configuration cfg = new Configuration();
            cfg.configure("/hibernate.cfg.xml");
            sessionFactory = cfg.buildSessionFactory();
 
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Get the Session for the current Thread, creating one if neccessary.
     *
     * @return the Session fro the current Thread.
     */
    public static Session getSession() {
        Session session = (Session) sessionHolder.get();

        if (session == null) {
            try {
                session = sessionFactory.openSession();
            } catch (HibernateException he) {
                throw new RuntimeException(he);
            }
            sessionHolder.set(session);
        }

        return session;
    }

    /**
     * Close the Session held by the current Thread. The close session will
     * also be removed from the ThreadLocal variable.
     */
    public static void close() {
        Session session = (Session) sessionHolder.get();

        if (session != null) {
            try {
                session.close();
            } catch (HibernateException he) {
                throw new RuntimeException(he);
            }
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

}
