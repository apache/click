/*
 * Copyright 2005 Malcolm A. Edgar
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
package tracker.util;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import org.apache.log4j.Logger;

/**
 * A Hibernate Session Provider utility class, which stores the session in a
 * ThreadLocal variable.
 * <p>
 * Adapted from <tt>HibernateUtil</tt> helper class.
 * 
 * @author Malcolm Edgar
 */
public class SessionProvider {

    private static final SessionFactory sessionFactory;
    
    private static final ThreadLocal sessionHolder = new ThreadLocal();

    static {
        try {
            Configuration cfg = new Configuration();
            cfg.configure("/hibernate.cfg.xml");
            sessionFactory = cfg.buildSessionFactory();
 
        } catch (Throwable e) {
            Logger log = Logger.getLogger(SessionProvider.class);
            log.error("Initial SessionFactory creation failed.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Get the Session for the current Thread, creating one if neccessary.
     * 
     * @return the Session fro the current Thread.
     * @throws HibernateException if an error occurs
     */
    public static Session getSession() throws HibernateException {
        Session session = (Session) sessionHolder.get();
        
        if (session == null) {
            session = sessionFactory.openSession();
            sessionHolder.set(session);
        }
        
        return session;
    }

    /**
     * Close the Session held by the current Thread. The close session will
     * also be removed from the ThreadLocal variable.
     * 
     * @throws HibernateException if an error occurs
     */
    public static void closeSession() throws HibernateException {
        Session session = (Session) sessionHolder.get();
        
        if (session != null) {
            session.close();
        }
        
        sessionHolder.set(null);
    }
}
