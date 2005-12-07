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

import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.util.ClickUtils;

/**
 * Provides a way to add multiple sub-panels in 'tabs'.  This panel comes with a
 * default template that will render the panels in CSS customizable table tags.
 * Additionally, a listener may be attached (similar to the control listeners)
 * that will be called on tab switch. This could be useful to load (or reload)
 * model related information for which ever panel is selected by the user.
 *
 * <pre class="codeHtml">
 * &lt;div id='<span class="blue">$_tp_id</span>'&gt;
 *  &lt;table class="<span class="green">tp_tab</span>"&gt;
 *   &lt;tr class="<span class="green">tp_tab</span>"&gt;
 *    <span class="red">#foreach</span> (<span class="blue">$panel</span> <span class="red">in</span> <span class="blue">$_tp_panels</span>)
 *     <span class="red">#if</span> (<span class="blue">$panel.id</span> == <span class="blue">$_tp_activePanel.id</span>)
 *      &lt;td class="<span class="green">tp_tab_on</span>"&gt;
 *       <span class="blue">$panel.label</span>
 *      &lt;/td&gt;
 *      &lt;td class="<span class="green">tp_tab_space</span>"&gt;&lt;/td&gt;
 *     <span class="red">#else</span>
 *      &lt;td class="<span class="green">tp_tab_off</span>"&gt;
 *       &lt;a href="<span class="blue">$_tp_tabLink.getHref($panel.name)</span>"
 *          id="<span class="blue">$panel.id</span>"
 *          class="<span class="green">tp_tab_link</span>"&gt;<span class="blue">$panel.label</span>&lt;/a&gt;
 *      &lt;/td&gt;
 *      &lt;td class="<span class="green">tp_tab_space</span>"&gt;&lt;/td&gt;
 *     <span class="red">#end</span>
 *    <span class="red">#end</span>
 *   &lt;/tr&gt;
 *  &lt;/table&gt;
 *  &lt;table class="<span class="green">tp_content</span>"&gt;
 *   &lt;tr class="<span class="green">tp_content</span>"&gt;
 *    &lt;td class="<span class="green">tp_content</span>"&gt;
 *     <span class="red">#parse</span>(<span class="blue">$_tp_activePanel</span>)
 *    &lt;/td&gt;
 *   &lt;/tr&gt;
 *  &lt;/table&gt;
 * &lt;/div&gt; </pre>
 *
 * As shown above, the the model context variables associated with a tabbed
 * panel are as follows:
 * <ul>
 * <li><tt>_tp_id</tt> &nbsp; - the id associated with this tabbed panel</li>
 * <li><tt>_tp_panels</tt> &nbsp; - the sub-panels added to this panel</li>
 * <li><tt>_tp_activePanel</tt> &nbsp; - the currently active panel (aka tab)</li>
 * <li><tt>_tp_tabLink</tt> &nbsp; - an {@link ActionLink} control that handles the switching
 * between the various panels.</li>
 * </ul>
 * <p/>
 * Also, as show above, there are a number of CSS attributes that allow some
 * customization of the output.  These are as follows (these are the exact
 * colors show in the example for tabbed panels):
 *
 * <pre class="codeHtml">
 * <span class="green">table.tp_tab</span> {
 *   border-collapse: collapse;
 * }
 * <span class="green">tr.tp_tab</span> {
 * }
 * <span class="green">td.tp_tab_on</span> {
 *   background: #336699;
 *   color: #ffffff;
 *   border-left: 1px solid #336699;
 *   border-top: 1px solid #336699;
 *   border-right: 1px solid #336699;
 *   padding: 5px;
 * }
 * <span class="green">td.tp_tab_off</span> {
 *   background: #cccccc;
 *   color: #000000;
 *   border-left: 1px solid #336699;
 *   border-top: 1px solid #336699;
 *   border-right: 1px solid #336699;
 *   padding: 5px;
 * }
 * <span class="green">table.tp_content</span> {
 *   border: 1px solid #336699;
 * }
 * <span class="green">tr.tp_content</span> {
 * }
 * <span class="green">td.tp_content</span> {
 *   background: #efefef;
 * } </pre>
 *
 * @author Phil Barnes
 * @version $Id$
 */
public class TabbedPanel extends BasicPanel {

    protected boolean debug;

    protected Object listener;

    protected String method;

    protected ActionLink tabActionLink;

    // ----------------------------------------------------------- Constructors

    public TabbedPanel(String id) {
        super(id);
        addModel("_tp_id", id);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * <b>NOTE:</b> This method should <b>not</b> be called, in favor of
     * addPanel(Panel, boolean), to ensure the "active" panel is defined.  This
     * method will by default set the active panel to the current panel being
     * passed, to ensure that at least one active panel has been set.
     *
     * @param panel the panel to add
     */
    public void addPanel(Panel panel) {
        debug("WARNING: TabbedPanel should have panels added ONLY via" +
              " addPanel(panel, boolean) to ensure the active panel" +
              " is correctly set.  Setting active panel to *this* |" +
              " panel ('" + panel.getName() + "') as a result");
        this.addPanel(panel, true);
    }

    /**
     * Add's a "sub-panel" that will be rendered in a tab if the passed boolean
     * is true.  Otherwise, only the panel name and an associated link will be
     * added.  For all panels, an actionLink will be added that has a listener
     * (handleTabSwitch) noted in this class that will handle the cycling
     * through the various panels according to the clicked link.
     *
     * @param panel
     * @param isActivePanel
     */
    public void addPanel(Panel panel, boolean isActivePanel) {
        super.addPanel(panel);
        if (isActivePanel) {
            if (isDebugEnabled()) {
                debug("Adding panel with id " + panel.getId() +
                      " as the active panel");
            }
            setActivePanel(panel);
        }
        if (getPanels().size() > 1 && tabActionLink == null) {
            if (isDebugEnabled()) {
                debug("Two or more panels detected, enabling " +
                       "tabActionLink. Current listener status = " +
                       listener + "." + method + "()");
            }
            tabActionLink = new ActionLink("_tp_tabLink");
            tabActionLink.setListener(this, "handleTabSwitch");
            addControl(tabActionLink);
            addModel("_tp_panels", getPanels());
        }
    }

    /**
     * Set the controls event listener.  If the listener <b>and</b> method are
     * non-null, then the listener will be called whenever a request to switch
     * tabs is placed by clicking the link associated with that tab.
     * <p/>
     * The method signature of the listener is:<ul>
     * <li>must hava a valid Java method name</li>
     * <li>takes no arguments</li>
     * <li>returns a boolean value</li>
     * </ul>
     * <p/>
     * An example event listener method would be:
     *
     * <pre class="codeJava">
     * <span class="kw">public boolean</span> onClick() {
     * System.out.println(<span class="st">"onClick called"</span>);
     * <span class="kw">return true</span>;
     * } </pre>
     *
     * @param listener the listener object with the named method to invoke
     * @param method   the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
        this.listener = listener;
        this.method = method;
    }

    /**
     * @see Panel#setPage(Page)
     */
    public void setPage(Page page) {
        super.setPage(page);
        // add the context to the tabActionLink control
        // TODO: is there a better way to do set the context?
        tabActionLink.setContext(page.getContext());
    }

    /**
     * Handle the switching between tabs.  This method will invoke the
     * listener.method() as set by {@link #setListener(Object, String)} if
     * available, otherwise will just continue processing, therefore
     * assume that all the information needed to "switch tabs" is already
     * available to the model.
     *
     * @return true if processing should continue, false if it should halt
     */
    public boolean handleTabSwitch() {
        for (int i = 0; i < panels.size(); i++) {
            Panel panel = (Panel) panels.get(i);
            if (tabActionLink.getValue().equals(panel.getName())) {
                setActivePanel(panel);
            }
        }

        // if a listener has been explicitely set to handle a tab switch,
        // then invoke it
        if (listener != null && method != null) {
            if (isDebugEnabled()) {
                debug("Invoking listener " + listener + "." + method + "()");
            }
            return ClickUtils.invokeListener(listener, method);
        }
        // this implies that everything needed to render the next tab has been
        // added to the "model" context already
        else {
            debug("No listener method found, continuing processing");
            return true;
        }
    }

    /**
     * Removes the current 'active panel' from the model, and adds the passed
     * panel as the new active panel.
     *
     * @param panel
     */
    protected void setActivePanel(Panel panel) {
        // remove the existing 'active panel'
        removeModel("_tp_activePanel");
        // add the passed in panel as the 'new' active panel
        addModel("_tp_activePanel", panel);
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

    /**
     * Return true if debug logging is enabled.
     *
     * @return true if debug loggin is enabled
     */
    public boolean isDebugEnabled() {
        return debug;
    }

    /**
     * Set the debug logging status.
     *
     * @param debug the debug logging status
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Log the given message to <tt>System.out</tt> if debug logging is enabled.
     *
     * @param msg the debug message to log write to <tt>System.out</tt>
     */
    public void debug(String msg) {
        if (isDebugEnabled()) {
            System.out.println("[Click] [debug] TabledPannel " + msg);
        }
    }
}
