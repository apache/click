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

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * Mock implementation of a user {@link java.security.Principal principal}.
 * This class also provides convenient methods for specifying the user principal
 * roles.
 * <p/>
 * Example usage with MockContext:
 * <pre class="prettyprint">
 * MockContext mockContext = MockContext.initContext();
 *
 * // Create a new user principal with the roles "user" and "manager"
 * MockPrincipal principal = new MockPrincipal("Bob", "user", "manager");
 *
 * // Set the user principal on the request object
 * mockContext.getMockRequest().setUserPrincipal(principal); </pre>
 *
 * <p/>
 * Example usage with MockContainer:
 * <pre class="prettyprint">
 * MockContainer container = new MockContainer("c:/dev/myapp/web");
 *
 * // Create a new user principal with the roles "user" and "manager"
 * MockPrincipal principal = new MockPrincipal("Bob", "user", "manager");
 *
 * // Set the user principal on the request object
 * container.getRequest().setUserPrincipal(principal); </pre>
 */
public class MockPrincipal implements Principal {

    // Variables --------------------------------------------------------------

    /** The principal name. */
    private String name;

    /** The principal roles. */
    private Set<String> roles;

    // Constructors -----------------------------------------------------------

    /**
     * Constructs a new MockPrincipal instance.
     */
    public MockPrincipal() {
    }

    /**
     * Constructs a new MockPrincipal instance for the given name.
     *
     * @param name the name of the principal
     */
    public MockPrincipal(String name) {
        setName(name);
    }

    /**
     * Constructs a new MockPrincipal instance for the given name and roles.
     *
     * @param name the name of the principal
     * @param roles the principal roles
     */
    public MockPrincipal(String name, Set<String> roles) {
        setName(name);
        setRoles(roles);
    }

    /**
     * Constructs a new MockPrincipal instance for the given name and roles.
     *
     * @param name the name of the principal
     * @param roles the principal roles
     */
    public MockPrincipal(String name, String... roles) {
        setName(name);
        addRoles(roles);
    }

    // Public methods ---------------------------------------------------------

    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this principal.
     *
     * @param name the name of the princpal
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the roles of this principal.
     *
     * @return the roles of this principal.
     */
    public Set<String> getRoles() {
        if (roles == null) {
             roles = new HashSet<String>();
        }
        return roles;
    }

    /**
     * Sets the roles of this principal.
     *
     * @param roles set the roles of this principal.
     */
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    /**
     * Add the roles of this principal.
     *
     * @param roles set the roles of this principal.
     */
    public void addRoles(String... roles) {
        for (String role : roles) {
            getRoles().add(role);
        }
    }
}
