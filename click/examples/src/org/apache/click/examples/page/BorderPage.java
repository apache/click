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
package org.apache.click.examples.page;

import org.apache.click.Page;
import org.apache.click.extras.control.Menu;
import org.apache.click.util.ClickUtils;

/**
 * Provides a page border template. This Page returns the template
 * <tt>"border-template.htm"</tt>, and sets the Page model values <tt>$title</tt> and
 * <tt>$srcPath</tt>.
 * <p/>
 * Please note this page is designed for extending by Page subclasses and will
 * not be auto mapped as the template name <tt>"border-template.htm"</tt> does
 * not match the Pages class name <tt>BorderPage</tt>.
 *
 * @author Malcolm Edgar
 */
public class BorderPage extends Page {

    /**
     * The root menu. Note this transient variable is reinitialized in onInit()
     * to support serialized stateful pages.
     */
    public transient Menu rootMenu;

    // ------------------------------------------------------------ Constructor

    /**
     * Create a BorderedPage and set the model attributes <tt>$title</tt> and
     * <tt>$srcPath</tt>.
     * <ul>
     * <li><tt>$title</tt> &nbsp; - &nbsp; the Page title from classname</li>
     * <li><tt>$srcPath</tt> &nbsp; - &nbsp; the Page Java source path</li>
     * </ul>
     */
    public BorderPage() {
        String className = getClass().getName();

        String shortName = className.substring(className.lastIndexOf('.') + 1);
        String title = ClickUtils.toLabel(shortName);
        addModel("title", title);

        String srcPath = className.replace('.', '/') + ".java";
        addModel("srcPath", srcPath);
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * @see org.apache.click.Page#onInit()
     */
    public void onInit() {
        super.onInit();

        rootMenu = Menu.getRootMenu();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.htm"</tt>
     * <p/>
     * Please note this page is designed for extending by Page subclasses and will
     * not be auto mapped as the template name <tt>"border-template.htm"</tt> does
     * not match the Pages class name <tt>BorderPage</tt>.
     *
     * @see org.apache.click.Page#getTemplate()
     */
    public String getTemplate() {
        return "/border-template.htm";
    }

    // ------------------------------------------------------ Protected Methods

    @SuppressWarnings("unchecked")
    protected Object getSessionObject(Class aClass) {
        if (aClass == null) {
            throw new IllegalArgumentException("Null class parameter.");
        }
        Object object = getContext().getSessionAttribute(aClass.getName());
        if (object == null) {
            try {
                object = aClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return object;
    }

    protected void setSessionObject(Object object) {
        if (object != null) {
            getContext().setSessionAttribute(object.getClass().getName(), object);
        }
    }

    @SuppressWarnings("unchecked")
    protected void removeSessionObject(Class aClass) {
        if (getContext().hasSession() && aClass != null) {
            getContext().getSession().removeAttribute(aClass.getName());
        }
    }

}
