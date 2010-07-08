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
package org.apache.click.extras.hibernate;

import javax.servlet.ServletContext;
import org.apache.click.util.ClickUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Provides a thread local Hibernate Session context class. The Hibernate
 * configuration should be defined in class path file:
 * <pre class="codeConfig">
 * /hibernate.cfg.xml </pre>
 *
 * Or alternatively by using System properties.
 * <p/>
 * The Hibernate initialization code used by <tt>SessionContext</tt> is:
 *
 * <pre class="prettyprint">
 * Configuration configuration = createConfiguration();
 * configuration.setProperties(System.getProperties());
 * configuration.configure();
 * SessionFactory sessionFactory = configuration.buildSessionFactory(); </pre>
 *
 * To support the SessionContext class configure a {@link SessionFilter} in
 * your web application.
 *
 * @see SessionFilter
 * @see HibernateForm
 */
public class SessionContext {

    /** The Hibernate session factory. */
    private static SessionFactory sessionFactory;

    /** The ThreadLocal session holder. */
    private static final ThreadLocal<Session> SESSION_HOLDER = new ThreadLocal<Session>();

    /**
     * Initializes the SessionContext instance.
     * <p/>
     * This includes creating a new Hibernate Configuration and building
     * the SessionFactory.
     * <p/>
     * This method first creates a new Configuration by invoking
     * {@link #createConfiguration} and then initializes the configuration
     * by invoking {@link #initConfiguration(org.hibernate.cfg.Configuration)}.
     *
     * @param servletContext the servlet context
     */
    public void onInit(ServletContext servletContext) {
        Configuration configuration = createConfiguration();
        initConfiguration(configuration);
        sessionFactory = configuration.buildSessionFactory();
    }

    /**
     * Creates and returns a new Configuration instance.
     * <p/>
     * <b>Note:</b> as annotations have become popular the last couple of
     * years, this method will try and detect if Hibernate's
     * AnnotationConfiguration is available on the classpath. If it is a new
     * AnnotationConfiguration instance is created otherwise a Configuration
     * instance is created.
     *
     * @return new Hibernate Configuration instance.
     */
    public Configuration createConfiguration() {
        try {
            // Try to instantiate AnnotationConfiguration by reflection
            Class<?> clazz = ClickUtils.classForName("org.hibernate.cfg.AnnotationConfiguration");
            Configuration configuration = (Configuration) clazz.newInstance();
            return configuration;
        } catch (ClassNotFoundException e) {
            // Fall back to normal configuration
            return new Configuration();
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Initialize the configuration instance.
     * <p/>
     * You can override this method and manually setup the configuration:
     * <pre class="prettyprint">
     * public Configuration createConfiguration() {
     *     configuration.setProperties(System.getProperties());
     *     configuration.configure();
     * }</pre>
     *
     * @param configuration the configuration to initialize
     */
    public void initConfiguration(Configuration configuration) {
        configuration.setProperties(System.getProperties());
        configuration.configure();
    }

    /**
     * Get the Session for the current Thread, creating one if necessary.
     *
     * @return the Session fro the current Thread.
     * @throws HibernateException if an error occurs opening the session
     */
    public static Session getSession() throws HibernateException {
        Session session = SESSION_HOLDER.get();

        if (session == null) {
            session = getSessionFactory().openSession();
            SESSION_HOLDER.set(session);
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
        Session session = SESSION_HOLDER.get();

        if (session != null && session.isOpen()) {
            session.close();
        }

        SESSION_HOLDER.set(null);
    }

    /**
     * Return true if a session is open.
     *
     * @return true if a session is currently open.
     */
    public static boolean hasSession() {
        return (SESSION_HOLDER.get() != null);
    }

    /**
     * Return the Hibernate SessionFactory.
     *
     * @return the Hibernate SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
