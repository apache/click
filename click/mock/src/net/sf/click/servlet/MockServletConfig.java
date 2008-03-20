/*
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
package net.sf.click.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Mock implementation of {@link javax.servlet.ServletConfig}.
 *
 * @author Bob Schellink
 */
public class MockServletConfig implements ServletConfig {

    // -------------------------------------------------------- Instance Variables

    /** The servlet context. */
    private ServletContext servletContext;

    /** The servlet name. */
    private String servletName = "mock click servlet";

    /** The servlet initialization parameters. */
    private final Map initParameters = new HashMap();

    // -------------------------------------------------------- Constructors

    public MockServletConfig() {
        this(null, null);
    }

    public MockServletConfig(String servletName) {
        this(servletName, null);
    }

    public MockServletConfig(ServletContext servletContext) {
        this(null, servletContext);
    }

    public MockServletConfig(String servletName, ServletContext servletContext) {
        this(servletName, servletContext, null);
    }

    public MockServletConfig(String servletName, ServletContext servletContext, Map initParameters) {
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
    public void addInitParameters(final Map initParameters) {
        if (initParameters == null) {
            return;
        }
        initParameters.putAll(initParameters);
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    // -------------------------------------------------------- ServletConfig Methods

    public String getServletName() {
        return servletName;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public Enumeration getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }

    public String getInitParameter(String key) {
        return (String) initParameters.get(key);
    }
}
