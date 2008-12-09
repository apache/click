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
package net.sf.click.examples.page.general;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides examples of the Click Exception handling.
 *
 * @author Malcolm Edgar
 */
public class ExceptionPage extends BorderPage {

    public ActionLink nullPointerLink = new ActionLink(this, "onNullPointerClick");
    public ActionLink illegalArgumentLink = new ActionLink(this, "onIllegalArgumentExceptionClick");
    public ActionLink missingMethodLink = new ActionLink(this, "onMissingMethodClick");
    public ActionLink brokenRendererLink = new ActionLink(this, "onBrokenRendererClick");
    public ActionLink brokenBorderLink = new ActionLink(this, "onBrokenBorderClick");
    public ActionLink brokenContentLink = new ActionLink(this, "onBrokenContentClick");

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
