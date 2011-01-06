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
package org.apache.click.examples.page.general;

import org.apache.click.Page;
import org.apache.click.control.ActionLink;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides examples of the Click Exception handling.
 */
public class ExceptionPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private ActionLink nullPointerLink = new ActionLink("nullPointerLink", this, "onNullPointerClick");
    private ActionLink illegalArgumentLink = new ActionLink("illegalArgumentLink", this, "onIllegalArgumentExceptionClick");
    private ActionLink missingMethodLink = new ActionLink("missingMethodLink", this, "onMissingMethodClick");
    private ActionLink brokenRendererLink = new ActionLink("brokenRendererLink", this, "onBrokenRendererClick");
    private ActionLink brokenBorderLink = new ActionLink("brokenBorderLink", this, "onBrokenBorderClick");
    private ActionLink brokenContentLink = new ActionLink("brokenContentLink", this, "onBrokenContentClick");

    private String templateValue;

    public ExceptionPage() {
        addControl(nullPointerLink);
        addControl(illegalArgumentLink);
        addControl(missingMethodLink);
        addControl(brokenRendererLink);
        addControl(brokenBorderLink);
        addControl(brokenContentLink);
    }

    // Event Handlers ---------------------------------------------------------

    @SuppressWarnings("null")
    public boolean onNullPointerClick() {
        Object object = null;
        object.hashCode();
        return true;
    }

    public boolean onIllegalArgumentExceptionClick() {
        // Null model value should throw IllegalArgumentException
        addModel("param-1", null);
        return true;
    }

    public boolean onBrokenRendererClick() {
        addModel("brokenRenderer", new BrokenRenderer());
        return true;
    }

    public boolean onBrokenBorderClick() {
        setPath("broken-border.htm");
        templateValue = "/general/broken-border.htm";
        return true;
    }

    public boolean onBrokenContentClick() {
        setPath("/general/broken-content.htm");
        return true;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Override getTemplate so we can stuff things up.
     *
     * @see Page#getTemplate()
     */
    @Override
    public String getTemplate() {
        return (templateValue != null) ? templateValue : super.getTemplate();
    }

    // Inner Classes ----------------------------------------------------------

    /**
     * Provides a rendering object which will throw a NPE when merged by
     * velocity in the template.
     */
    public static class BrokenRenderer {

        /**
         * Guaranteed to fail, or you money back.
         *
         * @see Object#toString()
         */
        @SuppressWarnings("null")
        @Override
        public String toString() {
            Object object = null;
            return object.toString();
        }
    }

}
