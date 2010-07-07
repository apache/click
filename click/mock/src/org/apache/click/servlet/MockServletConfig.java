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
package org.apache.click.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Mock implementation of {@link javax.servlet.ServletConfig}.
 * <p/>
 * Implements all of the methods from the standard ServletConfig class plus
 * helper methods to aid setting up a config.
 */
public class MockServletConfig implements ServletConfig {

    // -------------------------------------------------------- Instance Variables

    /** The servlet context. */
    private ServletContext servletContext;

    /** The servlet name. */
    private String servletName = "mock click servlet";

    /** The servlet initialization parameters. */
    private final Map<String, String> initParameters = new HashMap<String, String>();

    // -------------------------------------------------------- Constructors

    /**
     * Create a new MockServletConfig instance.
     */
    public MockServletConfig() {
        this(null, null);
    }

    /**
     * Create a new MockServletConfig instance with the specified servletName.
     *
     * @param servletName the servlet name
     */
    public MockServletConfig(String servletName) {
        this(servletName, null);
    }

    /**
     * Create a new MockServletConfig instance with the specified servletContext.
     *
     * @param servletContext the servletContext
     */
    public MockServletConfig(ServletContext servletContext) {
        this(null, servletContext);
    }

    /**
     * Create a new MockServletConfig instance with the specified servletName
     * and servletContext.
     *
     * @param servletName the servlet name
     * @param servletContext the servlet context
     */
    public MockServletConfig(String servletName, ServletContext servletContext) {
        this(servletName, servletContext, null);
    }

    /**
     * Create a new MockServletConfig instance with the specified servletName,
     * servletContext and initialization parameters.
     *
     * @param servletName the servlet name
     * @param servletContext the servlet context
     * @param initParameters the initialization parameters
     */
    public MockServletConfig(String servletName, ServletContext servletContext, Map<String, String> initParameters) {
        this.servletContext = servletContext;
        this.servletName = servletName;
        addInitParameters(initParameters);
    }

    // -------------------------------------------------------- Mock convenience methods

    /**
     * Add an init parameter.
     *
     * @param name The parameter name
     * @param value The parameter value
     */
    public void addInitParameter(final String name, final String value) {
        initParameters.put(name, value);
    }

    /**
     * Add the map of init parameters.
     *
     * @param initParameters A map of init parameters
     */
    public void addInitParameters(final Map<String, String> initParameters) {
        if (initParameters == null) {
            return;
        }
        initParameters.putAll(initParameters);
    }

    /**
     * Set the servlet name.
     *
     * @param servletName the new servletName
     */
    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    /**
     * Set the servlet context instance.
     *
     * @param servletContext a servletContext instance
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    // -------------------------------------------------------- ServletConfig Methods

    /**
     * Return the servlet name.
     *
     * @return the servlet name
     */
    public String getServletName() {
        return servletName;
    }

    /**
     * Return the servlet context.
     *
     * @return the servletContext
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * Returns the names of the servlet's initialization parameters as an
     * Enumeration of String objects, or an empty Enumeration if the servlet
     * has no initialization parameters.
     *
     * @return enumeration of initialization parameters
     */
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }

    /**
     * Returns a String containing the value of the named initialization
     * parameter, or null if the parameter does not exist.
     *
     * @param name a String specifying the name of the initialization parameter
     * @return a String containing the value of the initialization parameter
     */
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }
}
