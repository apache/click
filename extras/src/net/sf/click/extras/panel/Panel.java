/*
 * Copyright 2004-2005 Malcolm A. Edgar
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
package net.sf.click.extras.panel;

import net.sf.click.Control;
import net.sf.click.Page;

/**
 * Provides for a "Panel" which is simply a template and adds pass through
 * methods to the Page for adding Control and Model objects.
 * <p/>
 * The Panel class allows a Click applications to customize the output of
 * sections on a page, by providing a "template" to layout controls in multiple
 * locations on the page.
 * <p/>
 * The default template name will be the 'simple name' of the class (the class
 * name without the package information) plus the default template extension
 * (currently ".htm").  It is important to remember that since panels contain
 * their own templates, panels must be evaluated using Velocity's #parse()
 * directive.
 * <p/>
 * When a Panel is instantiated, it should automatically add all the information
 * required to render the Panel to the Model (via addModel) and pass controls
 * directly to the Page (via addControl).
 *
 * @author Phil Barnes
 */
public interface Panel extends Control {

    /**
     * Sets the page associated with this panel.  This call is necessary to
     * ensure any associated model or control elements that are added to the
     * panel are ultimately added to the page, so that the rendering of the page
     * has all available information necessary to render.
     *
     * @param page the page associated with this panel
     */
    public void setPage(Page page);

    /**
     * The path and name of the template to render.  This is the method that
     * should be called via Velocity's #parse() directive (either directly or
     * indirectly through toString())
     *
     * @return the path/name of the template to use when rendering this panel
     */
    public String getTemplate();

    /**
     * The "context name" that this Panel is associated with.  This name will be
     * used to uniquely identify this panel in the Page's model context.
     *
     * The  setter is not part of the interface, as this name can be specified
     * by multiple means: a constructor, a setter, or inferred from any number
     * of other methods (getTemplate(), getId(), getLabel(), etc.).  Thus the
     * implementation is left up to the Panel creator.
     *
     * @return the name of this panel, to be used to uniquely identify it in the
     * model context
     */
    public String getName();

    /**
     * The "id" associated with this panel.  This id is typically used to
     * uniquely identify this panel in the rendered HTML.
     *
     * For example:
     * <pre class="codeHtml">
     *  &lt;div id="$panel.id"&gt;
     *      ## content here
     *  &lt;/div&gt; </pre>
     *
     * The setter is not part of the interface, as this id can be specified by
     * multiple means: a constructor, a setter, or inferred from any number of
     * other methods (getTemplate(), getName(), getLabel(), etc.).  Thus the
     * implementation is left up to the Panel creator.
     *
     * @return the id of this panel, to be used to uniquely identify it in the
     * HTML page
     */
    public String getId();

    /**
     * Returns text label assocaited with this panel.  This should be an already
     * internationalized label set via setLabel().  Typically this is used as a
     * header for a particular panel.
     *
     * For example:
     * <pre class="codeHtml">
     *  &lt;div id="$panel.id"&gt;
     *      &lt;h1&gt;$panel.label&lt;/h1&gt;
     *      ## content here
     *  &lt;/div&gt; </pre>
     *
     * @return the internationalized label associated with this control
     */
    public String getLabel();

    /**
     * Set the internationalized label associated with this panel.  This method
     * is necessary to ensure proper internationalization of text for a panel.
     *
     * @param label the internationalized label for this panel
     */
    public void setLabel(String label);
}
