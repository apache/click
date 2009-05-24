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
package org.apache.click.control;

import java.util.Iterator;
import java.util.Map;

import org.apache.click.Context;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a Page Link control: &nbsp; &lt;a href="" &gt;&lt;/a&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <a href='' style='{text-decoration:underline;}' title='PageLink Control'>Page Link</a>
 * </td></tr>
 * </table>
 *
 * The <tt>PageLink</tt> control is used to create links to other pages in
 * your application.
 *
 * See also the W3C HTML reference:
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/struct/links.html#h-12.2">A Links</a>
 *
 * @see org.apache.click.control.AbstractLink
 * @see org.apache.click.control.ActionLink
 *
 * @author Malcolm Edgar
 */
public class PageLink extends AbstractLink {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The target page class. */
    protected Class pageClass;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an PageLink for the given name.
     *
     * @param name the page link name
     * @throws IllegalArgumentException if the name is null
     */
    public PageLink(String name) {
        setName(name);
    }

    /**
     * Create an PageLink for the given name and target Page class.
     *
     * @param name the page link name
     * @param targetPage the target page class
     * @throws IllegalArgumentException if the name is null
     */
    public PageLink(String name, Class targetPage) {
        setName(name);
        if (targetPage == null) {
            throw new IllegalArgumentException("Null targetPage parameter");
        }
        pageClass = targetPage;
    }

    /**
     * Create an PageLink for the given name, label and target Page class.
     *
     * @param name the page link name
     * @param label the page link label
     * @param targetPage the target page class
     * @throws IllegalArgumentException if the name is null
     */
    public PageLink(String name, String label, Class targetPage) {
        setName(name);
        setLabel(label);
        if (targetPage == null) {
            throw new IllegalArgumentException("Null targetPage parameter");
        }
        pageClass = targetPage;
    }

    /**
     * Create an PageLink for the given target Page class.
     *
     * @param targetPage the target page class
     * @throws IllegalArgumentException if the name is null
     */
    public PageLink(Class targetPage) {
        if (targetPage == null) {
            throw new IllegalArgumentException("Null targetPage parameter");
        }
        pageClass = targetPage;
    }

    /**
     * Create an PageLink with no name defined.
     * <p/>
     * <b>Please note</b> the control's name and target pageClass must be
     * defined before it is valid.
     */
    public PageLink() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the PageLink anchor &lt;a&gt; tag href attribute.
     * This method will encode the URL with the session ID
     * if required using <tt>HttpServletResponse.encodeURL()</tt>.
     *
     * @return the PageLink HTML href attribute
     */
    public String getHref() {
        if (getPageClass() == null) {
            throw new IllegalStateException("target pageClass is not defined");
        }

        Context context = getContext();
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.append(context.getRequest().getContextPath());

        String pagePath = context.getPagePath(getPageClass());

        if (pagePath != null && pagePath.endsWith(".jsp")) {
            pagePath = StringUtils.replace(pagePath, ".jsp", ".htm");
        }

        buffer.append(pagePath);

        if (hasParameters()) {
            buffer.append("?");

            Iterator i = getParameters().entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                String name = entry.getKey().toString();
                String value = entry.getValue().toString();

                buffer.append(name);
                buffer.append("=");
                buffer.append(ClickUtils.encodeUrl(value, context));
                if (i.hasNext()) {
                    buffer.append("&amp;");
                }
            }
        }

        return context.getResponse().encodeURL(buffer.toString());
    }

    /**
     * Return the target Page class.
     *
     * @return the target Page class
     */
    public Class getPageClass() {
        return pageClass;
    }

    /**
     * Set the target Page class. The page link href URL attribute will be
     * to the path of the target page.
     *
     * @param targetPage the target Page class
     */
    public void setPageClass(Class targetPage) {
        pageClass = targetPage;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method will return true.
     *
     * @see org.apache.click.Control#onProcess()
     *
     * @return true
     */
    public boolean onProcess() {
        return true;
    }

}
