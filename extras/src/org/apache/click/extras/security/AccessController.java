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
package org.apache.click.extras.security;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides an interface for controlling request access to security
 * resources. A security resource is an abstract term which may refer to a
 * role, permission or authority.
 */
public interface AccessController {

    /**
     * Return true if the user request has access to the specified security
     * resource (role, permission, authority).
     * <p/>
     * <b>Please note:</b> this method must cater for a <tt>null</tt> resource
     * argument. The given resource can be <tt>null</tt> for anonymous or public
     * resources and allows this implementation to allow or deny access.
     *
     * @param request the user request
     * @param resource the security resource (role, permission, authority)
     * @return true if the specified user request has access to the security
     *         resource
     */
    public boolean hasAccess(HttpServletRequest request, String resource);

}
