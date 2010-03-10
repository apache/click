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
package org.apache.click.examples.control;

import java.net.MalformedURLException;
import org.apache.click.Context;
import org.apache.click.control.Panel;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.ClassUtils;

/**
 * A panel with fallback render functionality which allows the panel controls
 * to be rendered if its template is not available.
 * <p/>
 * By default Panel will render its template or throw exception if template is
 * not found. Thus if you do not define a template for the Panel, an exception
 * is raised.
 * <p/>
 * SimplePanel on the other hand first checks if the template is
 * available on the servlet context path or classpath and if template is not
 * found, the panel controls are simply rendered in the order they were added.
 * <p/>
 * In effect SimplePanel falls back to the default rendering of
 * AbstractContainer if the Panel template is not found.
 */
public class SimplePanel extends Panel {

    private static final long serialVersionUID = 1L;

    /**
     * Default empty constructor.
     */
    public SimplePanel() {
    }

    /**
     * Construct a new panel with the specified name.
     *
     * @param name name of the panel
     */
    public SimplePanel(String name) {
        super(name);
    }

    /**
     * Render the AbstractTablePanel's internal HtmlTable.
     * <p/>
     * However like its superclass {@link org.apache.click.control.Panel} it is
     * possible to override the default rendering by either specifying a
     * {@link #template} or specifying a template based on the
     * {@link #getClass() classes} name.
     *
     * @see org.apache.click.control.Panel#render(org.apache.click.util.HtmlStringBuffer)
     *
     * @param buffer the specified buffer to render the Panel's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {

        if (getTemplate() != null) {
            super.render(buffer);

        } else {
            boolean hasTemplate = hasTemplate();
            if (hasTemplate) {
                super.render(buffer);
            } else {
                ClickUtils.getLogService().trace("   No template was found for"
                    + " panel " + ClassUtils.getShortClassName(getClass())
                    + ". Rendering controls in the order they were added.");
                renderContainer(buffer);
            }
        }
    }

    /**
     * Return true if the panel template is available, false otherwise.
     *
     * @return true if the panel template is available, false otherwise
     */
    protected boolean hasTemplate() {
        boolean hasTemplate = false;

        if (getTemplate() != null) {
            hasTemplate = true;

        } else {
            String templatePath = getClass().getName();
            templatePath = '/' + templatePath.replace('.', '/') + ".htm";

            try {
                Context context = getContext();

                // First check on the servlet context path
                hasTemplate = context.getServletContext().getResource(templatePath) != null;
                if (!hasTemplate) {
                    // Second check on the classpath
                    hasTemplate = ClickUtils.getResource(templatePath, getClass()) != null;
                }

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return hasTemplate;
    }

    /**
     * Render the panel as a normal Container.
     *
     * @param buffer to buffer to render output to
     */
    protected void renderContainer(HtmlStringBuffer buffer) {
        // If a template cannot be found for the panel, use default container
        // rendering
        if (getTag() != null) {
            renderTagBegin(getTag(), buffer);
            buffer.closeTag();
            if (hasControls()) {
                buffer.append("\n");
            }
            renderContent(buffer);
            renderTagEnd(getTag(), buffer);

        } else {

            //render only content because no tag is specified
            renderContent(buffer);
        }
    }
}
