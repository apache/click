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
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

/**
 * Mock implementation of {@link javax.servlet.http.HttpSession}.
 * <p/>
 * Implements all of the methods from the standard HttpSession class plus
 * helper methods to aid setting up a session.
 */
public class MockSession implements HttpSession {

    // -------------------------------------------------------- Instance Variables

    /** The session servletContext. */
    private ServletContext servletContext = null;

    /** The session attributes. */
    private Map<String, Object> attributes = new HashMap<String, Object>();

    /** The session unique id. */
    private String id = Long.toString(new Random().nextLong());

    /** The session creationTime in milliseconds. */
    private long creationTime = System.currentTimeMillis();

    /**
     * Specifies the amount of time the session will remain active between
     * client requests.
     */
    private int maxInactiveInterval = -1;

    // -------------------------------------------------------- Constructors

    /**
     * Create a default MockSession.
     */
    public MockSession() {
    }

    /**
     * Create a MockSession for the specified session identifier.
     *
     * @param id session unique identifier.
     */
    public MockSession(String id) {
        this(id, null);
    }

    /**
     * Create a MockSession for the specified servletContext.
     *
     * @param servletContext the servletContext to which this session belong
     */
    public MockSession(ServletContext servletContext) {
        this(null, servletContext);
    }

    /**
     * Create a MockSession for the specified id and servletContext.
     *
     * @param id session unique identifier.
     * @param servletContext the servletContext to which this session belong
     */
    public MockSession(String id, ServletContext servletContext) {
        if (StringUtils.isNotBlank(id)) {
            setId(id);
        }
        setServletContext(servletContext);
    }

    // -------------------------------------------------------- Test Configuration Methods

    /**
     * Sets session unique identifier.
     *
     * @param id a unique session identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the servletContext to which the session belong.
     *
     * @param servletContext the servletContext to which this session belong
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    // -------------------------------------------------------- Session Methods

    /**
     * @see javax.servlet.http.HttpSession#getAttributeNames()
     *
     * @return an Enumeration of String objects specifying the names of all the
     * objects bound to this session
     */
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    /**
     * @see javax.servlet.http.HttpSession#getCreationTime()
     *
     * @return a long specifying when this session was created, expressed in
     * milliseconds since 1/1/1970 GMT
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * @see javax.servlet.http.HttpSession#getId()
     *
     * @return a string specifying the identifier assigned to this session
     */
    public String getId() {
        return id;
    }

    /**
     * This method will return the current time by invoking
     * System.currentTimeMillis().
     *
     * @see javax.servlet.http.HttpSession#getLastAccessedTime()
     *
     * @return a long representing the last time the client sent a request
     * associated with this session, expressed in milliseconds since 1/1/1970 GMT
     */
    public long getLastAccessedTime() {
        return System.currentTimeMillis();
    }

    /**
     * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
     *
     * @return an integer specifying the number of seconds this session remains
     * open between client requests
     */
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    /**
     * @see javax.servlet.http.HttpSession#getServletContext()
     *
     * @return the session servletContext
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * @see javax.servlet.http.HttpSession#getSessionContext()
     *
     * @return the session sessionContext
     */
    @SuppressWarnings("deprecation")
    public javax.servlet.http.HttpSessionContext getSessionContext() {
        return null;
    }

    /**
     * @see javax.servlet.http.HttpSession#getValueNames()
     *
     * @return an array of String  objects specifying the names of all the
     * objects bound to this session
     */
    public String[] getValueNames() {
        return attributes.keySet().toArray(new String[attributes.size()]);
    }

    /**
     * @see javax.servlet.http.HttpSession#invalidate()
     */
    public void invalidate() {
        attributes.clear();
    }

    /**
     * @see javax.servlet.http.HttpSession#isNew()
     *
     * @return true if the server has created a session, but the client has not
     * yet joined
     */
    public boolean isNew() {
        return false;
    }

    /**
     * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
     *
     * @param maxInterval an integer specifying the number of seconds
     */
    public void setMaxInactiveInterval(int maxInterval) {
        this.maxInactiveInterval = maxInterval;
    }

    /**
     * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
     *
     * @param name the name of the object to remove from this session
     */
    public void removeValue(String name) {
        attributes.remove(name);
    }

    /**
     * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
     *
     * @param name the name of the object to remove from this session
     */
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    /**
     * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
     *
     * @param name a string specifying the name of the object
     * @return the object with the specified name
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
     *
     * @param name a string specifying the name of the object
     * @return the object with the specified name
     */
    public Object getValue(String name) {
        return attributes.get(name);
    }

    /**
     * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
     *
     * @param name the name to which the object is bound; cannot be null
     * @param value the object to be bound
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
     *
     * @param name the name to which the object is bound; cannot be null
     * @param value the object to be bound; cannot be null
     */
    public void putValue(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * Return a string representation of the session.
     * <p/>
     * The string will print all attributes of the session.
     *
     * @return string representation of the session
     */
    @Override
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        buffer.append("Session attributes {");
        for (Enumeration<String> en = getAttributeNames(); en.hasMoreElements();) {
            String name = en.nextElement();
            Object value = getAttribute(name);
            buffer.append(name);
            buffer.append("=");
            buffer.append(value);
            if (en.hasMoreElements()) {
                buffer.append(", ");
            }
        }
        buffer.append("}");
        return buffer.toString();
    }
}
