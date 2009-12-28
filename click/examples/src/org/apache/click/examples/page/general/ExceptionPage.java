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
import org.apache.click.util.Bindable;

/**
 * Provides examples of the Click Exception handling.
 *
 * @author Malcolm Edgar
 */
public class ExceptionPage extends BorderPage {

    @Bindable protected ActionLink nullPointerLink = new ActionLink(this, "onNullPointerClick");
    @Bindable protected ActionLink illegalArgumentLink = new ActionLink(this, "onIllegalArgumentExceptionClick");
    @Bindable protected ActionLink missingMethodLink = new ActionLink(this, "onMissingMethodClick");
    @Bindable protected ActionLink brokenRendererLink = new ActionLink(this, "onBrokenRendererClick");
    @Bindable protected ActionLink brokenBorderLink = new ActionLink(this, "onBrokenBorderClick");
    @Bindable protected ActionLink brokenContentLink = new ActionLink(this, "onBrokenContentClick");

    private String template;

    public boolean onNullPointerClick() {
        Object object = null;
        object.hashCode();
        return true;
    }

    public boolean onIllegalArgumentExceptionClick() {
        addModel("param-1", "First Parameter");
        addModel("param-1", "Second Parameter");
        return true;
    }

    public boolean onBrokenRendererClick() {
        addModel("brokenRenderer", new BrokenRenderer());
        return true;
    }

    public boolean onBrokenBorderClick() {
        setPath("broken-border.htm");
        template = "broken-border.htm";
        return true;
    }

    public boolean onBrokenContentClick() {
        setPath("broken-content.htm");
        return true;
    }

    /**
     * Override getTemplate so we can stuff things up.
     *
     * @see Page#getTemplate()
     */
    @Override
    public String getTemplate() {
        return (template != null) ? template : super.getTemplate();
    }

    /**
     * Provides a rendering ojbect which will throw a NPE when merged by
     * velocity in the template.
     *
     * @author Malcolm Edgar
     */
    public static class BrokenRenderer {

        /**
         * Guaranteed to fail, or you money back.
         *
         * @see Object#toString()
         */
        public String toString() {
            Object object = null;
            return object.toString();
        }
    }

}
