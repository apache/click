/*
 * Copyright 2007 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.extras.control;

import java.util.Iterator;
import java.util.Map;

import net.sf.click.control.AbstractLink;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides an External Link control: &nbsp; &lt;a href="" &gt;&lt;/a&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <a href='' title='External Control'>External Link</a>
 * </td></tr>
 * </table>
 *
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/links.html#h-12.2">A Links</a>
 *
 * @author Malcolm Edgar
 */
public class ExternalLink extends AbstractLink {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The target path. */
    protected String targetPath;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an ExternalLink for the given name.
     *
     * @param name the page link name
     * @throws IllegalArgumentException if the name is null
     */
    public ExternalLink(String name) {
        setName(name);
    }

    /**
     * Create an ExternalLink for the given name and target Page class.
     *
     * @param name the page link name
     * @param targetPath the href target path
     * @throws IllegalArgumentException if the name is null
     */
    public ExternalLink(String name, String targetPath) {
        setName(name);
        if (targetPath == null) {
            throw new IllegalArgumentException("Null targetPath parameter");
        }
        this.targetPath = targetPath;
    }

    /**
     * Create an ExternalLink with no name defined.
     * <p/>
     * <b>Please note</b> the control's name and target path must be
     * defined before it is valid.
     */
    public ExternalLink() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the ExternalLink anchor &lt;a&gt; tag href attribute.
     * This method will encode the URL with the session ID
     * if required using <tt>HttpServletResponse.encodeURL()</tt>.
     *
     * @return the ExternalLink HTML href attribute
     */
    public String getHref() {
        if (getTargetPath() == null) {
            throw new IllegalStateException("targetPath is not defined");
        }

        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.append(getTargetPath());

        if (hasParameters()) {
            buffer.append("?");

            Iterator i = getParameters().entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                String name = entry.getKey().toString();
                String value = entry.getValue().toString();

                buffer.append(name);
                buffer.append("=");
                buffer.append(ClickUtils.encodeUrl(value, getContext()));
                if (i.hasNext()) {
                    buffer.append("&");
                }
            }
        }

        return buffer.toString();
    }

    /**
     * This method does nothing.
     *
     * @see net.sf.click.Control#setListener(Object, String)
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
    }

    /**
     * Return the link href target path.
     *
     * @return the link href target path
     */
    public String getTargetPath() {
        return targetPath;
    }

    /**
     * Set the link href target path.
     *
     * @param targetPath the link href target path
     */
    public void setPageClass(String targetPath) {
        this.targetPath = targetPath;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method will return true.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true
     */
    public boolean onProcess() {
        return true;
    }

}
