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
package net.sf.click.examples.page.security;

import net.sf.click.Page;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an <tt>onSecurityCheck</tt> example secure Page, which other secure
 * pages can extend.
 *
 * @author Malcolm Edgar
 */
public class Secure extends BorderPage {

    /**
     * @see Page#onSecurityCheck()
     */
    public boolean onSecurityCheck() {
        if (getContext().hasSessionAttribute("user")) {
            return true;

        } else {
            String path = getContext().getPagePath(Login.class);
            path += "?redirect=" + getPath();
            setRedirect(path);
            return false;
        }
    }

}
