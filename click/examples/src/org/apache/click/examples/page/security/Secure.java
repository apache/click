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
package org.apache.click.examples.page.security;

import org.apache.click.Page;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides an <tt>onSecurityCheck</tt> example secure Page, which other secure
 * pages can extend.
 */
public class Secure extends BorderPage {

    private static final long serialVersionUID = 1L;

    /**
     * @see Page#onSecurityCheck()
     */
    @Override
    public boolean onSecurityCheck() {
        if (getContext().hasSessionAttribute("user")) {
            return true;

        } else {
            String pagePath = getContext().getPagePath(Login.class);
            pagePath += "?redirect=" + getPath();
            setRedirect(pagePath);
            return false;
        }
    }

}
