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
package org.apache.click.examples.page.jsp;

/**
 * Provides an navigation example Page demonstrating forward and redirect
 * page navigation. See NavigationA page for details.
 */
public class NavigationB extends NavigationA {

    private static final long serialVersionUID = 1L;

    /**
     * Target template to forward to.
     * <p/>
     * In order to forward to a Page with a JSP template, we specify the target
     * with an htm extension so that ClickServlet will process the Page.
     * After the Page NavigationA.java is processed, Click will forward to the
     * underlying template /jsp/navigation-a.jsp.
     */
    @Override
    public String getTarget() {
        return "/jsp/navigation-a.htm";
    }

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.jsp"</tt>
     *
     * @see org.apache.click.Page#getTemplate()
     */
    @Override
    public String getTemplate() {
        return "/border-template.jsp";
    }

}
