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

import java.io.IOException;

import javax.servlet.ServletContext;

import net.sf.click.Deployable;
import net.sf.click.util.ClickUtils;

/**
 * Provides a method of adding multiple panels that will be listed out in a
 * vertical fashion, each panel being evaluated via Velocity's #parse(), and by
 * default, surrounded with a &lt;div&gt; with the id of the list panel when
 * instantiated.
 *
 * <pre class="codeHtml">
 * &lt;div id="$_lp_id"&gt;
 *   <span class="red">#foreach</span> ($panel in $_lp_panels)
 *     &lt;div id="$panel.id"&gt;
 *       <span class="red">#parse</span>($panel)
 *     &lt;/div&gt;
 *   <span class="red">#end</span>
 * &lt;/div&gt; </pre>
 *
 * As you can see, by default, the 'id' for this list panel will be put into the
 * model context as "_lp_id" and the list of sub-panels will be put in as
 * "_lp_panels" (the odd naming convention is to avoid conflict with user model
 * context objects)
 *
 * @author Phil Barnes
 */
public class ListPanel extends BasicPanel implements Deployable {

    /** The context key used to lookup the ID assocaited with this panel. */
    protected static final String INTERNAL_ID_KEY = "_lp_id";

    /**
     * The context key used to lookup the sub-panel list associated with this
     * panel.
     */
    protected static final String INTERNAL_PANEL_LIST_KEY = "_lp_panels";

    // ----------------------------------------------------------- Constructors

    /**
     * Default constructor includes the id of this list panel.  This id will be
     * used to wrap the entire list of sub-panels in a &lt;div&gt; element so
     * that the list itself may be stylized with CSS.  This id will be made
     * available in the internal name "_lp_id".
     *
     * @param id
     */
    public ListPanel(String id) {
        super(id);
        addModel(INTERNAL_ID_KEY, getId());
    }

    /**
     * Default no-args constructor used to deploy panel resources.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for resource deployment and are not
     * intended for general use. </div>
     */
    public ListPanel() {
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Overridden method to capture and add the panels to the model with an
     * internal name ("_lp_panels") so that the template may iterate over this
     * name for each panel that was added to the list.
     *
     * @param panel the panel to add
     */
    public void addPanel(Panel panel) {
        super.addPanel(panel);

        // this should continually override the existing _lp_panels entry
        removeModel(INTERNAL_PANEL_LIST_KEY);
        addModel(INTERNAL_PANEL_LIST_KEY, getPanels());
    }

    /**
     * Deploy the <tt>ListPanel.htm</tt> template to the <tt>click</tt> web
     * directory when the application is initialized.
     *
     * @see Deployable#onDeploy(ServletContext)
     */
    public void onDeploy(ServletContext servletContext) throws IOException {
        ClickUtils.deployFile
            (servletContext, "/net/sf/click/extras/panel/ListPanel.htm", "click");

    }

    /**
     * Overridden toString to add the path '/click/' to the beginning of the
     * template name, as the template file will be copied to this directory
     * upon first time start of ClickServlet
     *
     * @return the default template path and name of '/click/ListPanel.htm'
     */
    public String toString() {
        return "/click/" + super.toString();
    }

}

