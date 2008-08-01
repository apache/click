/*
 * Copyright 2004-2008 Malcolm A. Edgar
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

import javax.servlet.ServletContext;

import net.sf.click.Control;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Panel;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.lang.math.NumberUtils;

/**
 * Provides a tabbed panel with multiple sub-panels in 'tabs'.
 * <p/>
 * This panel comes with a default template that will render the panels in CSS
 * customizable table tags.
 * <p/>
 * A listener {@link #setTabListener(Object, String)} may be attached
 * (similar to the control listeners) that will be called on tab switch.
 * This could be useful to load (or reload) model related information for which
 * ever panel is selected by the user.
 * <p/>
 * The classpath <tt>TabbedPanel.htm</tt> template is illustrated below:
 *
 * <pre class="codeHtml">
 * &lt;div id='<span class="blue">$this.id</span>'&gt;
 *  &lt;table class="<span class="green">tp_tab</span>"&gt;
 *   &lt;tr class="<span class="green">tp_tab</span>"&gt;
 *    <span class="red">#foreach</span> (<span class="blue">$panel</span> <span class="red">in</span> <span class="blue">$this.panels</span>)
 *     <span class="red">#if</span> (<span class="blue">$panel.id</span> == <span class="blue">$this.activePanel.id</span>)
 *      &lt;td class="<span class="green">tp_tab_on</span>"&gt;
 *       <span class="blue">$panel.label</span>
 *      &lt;/td&gt;
 *      &lt;td class="<span class="green">tp_tab_space</span>"&gt;&lt;/td&gt;
 *     <span class="red">#else</span>
 *      &lt;td class="<span class="green">tp_tab_off</span>"&gt;
 *       &lt;a href="<span class="blue">$this.tabLink.getHref($panel.name)</span>"
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
 *     <span class="blue">$this.activePanel</span>
 *    &lt;/td&gt;
 *   &lt;/tr&gt;
 *  &lt;/table&gt;
 * &lt;/div&gt; </pre>
 *
 * Also, as show above, there are a number of CSS attributes that allow some
 * customization of the output. These CSS attributes are defined in the
 * auto deployed <tt>TabbedPanel.css</tt>. The TabbedPanel CSS attributes
 * are:
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
 * @author Malcolm Edgar
 */
public class TabbedPanel extends Panel {

    private static final long serialVersionUID = 1L;

    /** The TabbedPanel.css style sheet import link. */
    public static final String HTML_IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/TabbedPanel{1}.css\"/>\n";

    // ----------------------------------------------------- Instance Variables

    /** The currently active panel. */
    protected Panel activePanel;

    /** The tab switch action link. */
    protected ActionLink tabLink =
        new ActionLink("tabLink", this, "onTabSwitch");

    // ----------------------------------------------------------- Constructors

    /**
     * Create a TabbedPanel with the given name.
     *
     * @param name the name of the panel
     */
    public TabbedPanel(String name) {
        super(name);
        add(tabLink);
    }

    /**
     * Create a Panel with the given name and template path.
     *
     * @param name the name of the panel
     * @param template the Velocity template
     */
    public TabbedPanel(String name, String template) {
        super(name, template);
        add(tabLink);
    }

    /**
     * Create a TabbedPanel with the given name, id attribute and template path.
     *
     * @param name the name of the panel
     * @param template the Velocity template path
     * @param id the id HTML attribute value
     */
    public TabbedPanel(String name, String template, String id) {
        super(name, template, id);
        add(tabLink);
    }

    /**
     * Create a TabbedPanel with no name or template defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public TabbedPanel() {
        super();
        add(tabLink);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * @see #add(net.sf.click.Control)
     *
     * @param control the control to add to the container
     * @return the control that was added to the container
     * @throws IllegalArgumentException if the control is null, if the name
     *     of the control is not defined, the container already contains a
     *     control with the same name, or if the control's parent is a Page
     */
    public Control addControl(Control control) {
        return add(control);
     }

    /**
     * Add the control to the panel.
     * <p/>
     * If the control added is the first panel it will be made the active panel.
     *
     * @see net.sf.click.control.Panel#add(net.sf.click.Control)
     *
     * @param control the control to add to the container
     * @return the control that was added to the container
     * @throws IllegalArgumentException if the control is null, if the name
     *     of the control is not defined, the container already contains a
     *     control with the same name, or if the control's parent is a Page
     */
    public Control add(Control control) {
        super.add(control);

        if (control instanceof Panel && getPanels().size() == 1) {
            setActivePanel((Panel) control);
        }

        return control;
    }

    /**
     * Return the currently active panel.
     *
     * @return the currently active panel
     */
    public Panel getActivePanel() {
        return activePanel;
    }

    /**
     * Set the currently active panel to the given panel.
     *
     * @param panel the panel to set as the current active panel
     */
    public void setActivePanel(Panel panel) {
        activePanel = panel;
    }

    /**
     * Return the HTML head import statements for the CSS stylesheet file:
     * <tt>click/TabbedPanel.css</tt>.
     *
     * @return the HTML head import statements for the control stylesheet
     */
    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(512);

        buffer.append(ClickUtils.createHtmlImport(HTML_IMPORTS, getContext()));

        if (hasControls()) {
            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);

                if (control instanceof Panel) {
                    Panel panel = (Panel) control;
                    if (panel == getActivePanel()) {
                        String htmlImports = panel.getHtmlImports();
                        if (htmlImports != null) {
                            buffer.append(htmlImports);
                        }
                    }

                } else {
                    String htmlImports = control.getHtmlImports();
                    if (htmlImports != null) {
                        buffer.append(htmlImports);
                    }
                }
            }
        }

        return buffer.toString();
    }

    /**
     * @deprecated use setListener(java.lang.Object, java.lang.String) instead
     *
     * @param listener the listener object with the named method to invoke
     * @param listenerMethod the name of the method to invoke
     */
    public void setTabListener(Object listener, String listenerMethod) {
        setListener(listener, listenerMethod);
    }

    /**
     * Set the tab switch listener.  If the listener <b>and</b> method are
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
     *     System.out.println(<span class="st">"onClick called"</span>);
     *     <span class="kw">return true</span>;
     * } </pre>
     *
     * @param listener the listener object with the named method to invoke
     * @param listenerMethod the name of the method to invoke
     */
    public void setListener(Object listener, String listenerMethod) {
        this.listener = listener;
        this.listenerMethod = listenerMethod;
    }

    /**
     * Return the tab switching action link.
     *
     * @return the tab switching action link
     */
    public ActionLink getTabLink() {
        return tabLink;
    }

    /**
     * Return the tabbed panel content table HTML width attribute if defined.
     *
     * @return the tabbed panel content table HTML width attribute if defined
     */
    public String getWidth() {
        return (String) getModel().get("width");
    }

    /**
     * Set the tabbed panel content table HTML width attribute if defined.
     *
     * @param width the tabbed panel content table HTML width attribute
     */
    public void setWidth(String width) {
        if (width != null) {
            getModel().put("width", width);

        } else {
            getModel().remove("width");
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Deploy the <tt>TabbedPanel.css</tt> file to the <tt>click</tt> web
     * directory when the application is initialized.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFile(servletContext,
                              "/net/sf/click/extras/panel/TabbedPanel.css",
                              "click");
    }

    /**
     * Initialize the child controls contained in the panel. Note with the child
     * panels only the active panel will be initialized.
     * <p/>
     * If <tt>tabPanelIndex</tt> request parameter present this value will be
     * used to specify the active panel. The panel index is a zero based integer.
     *
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
        // Select panel specified by tabPanelIndex if defined
        String tabPanelIndex = getContext().getRequestParameter("tabPanelIndex");
        if (NumberUtils.isNumber(tabPanelIndex)) {
            int tabIndex = Integer.parseInt(tabPanelIndex);
            if (tabIndex >= 0 && tabIndex < getPanels().size()) {
                Panel panel = (Panel) getPanels().get(tabIndex);
                if (!panel.isDisabled()) {
                    setActivePanel(panel);
                }
            }
        }

        for (int i = 0, size = getControls().size(); i < size; i++) {
            Control control = (Control) getControls().get(i);
            if (control instanceof Panel) {
                if (control == getActivePanel()) {
                    control.onInit();
                }
            } else {
                control.onInit();
            }
        }
    }

    /**
     * Process the request and invoke the <tt>onProcess()</tt> method of any
     * child controls. Note with the child panels only the active panel will be
     * processed.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true or false to abort further processing
     */
    public boolean onProcess() {
        for (int i = 0, size = getControls().size(); i < size; i++) {
            Control control = (Control) getControls().get(i);
            if (control instanceof Panel) {
                if (control == getActivePanel()) {
                    if (!control.onProcess()) {
                        return false;
                    }
                }
            } else {
                if (!control.onProcess()) {
                    return false;
                }
            }
        }
         return true;
    }

    /**
     * Perform any pre rendering logic and invoke the <tt>onRender()</tt> method
     * of any child controls. Note with the child panels only the active panel
     * will have its <tt>onRender()</tt> method invoked.
     *
     * @see net.sf.click.Control#onRender()
     */
    public void onRender() {
        for (int i = 0, size = getControls().size(); i < size; i++) {
            Control control = (Control) getControls().get(i);
            if (control instanceof Panel) {
                if (control == getActivePanel()) {
                    control.onRender();
                }
            } else {
                control.onRender();
            }
        }
    }

    /**
     * The tab switch event handler.  This method will invoke the
     * listener.method() as set by {@link #setTabListener(Object, String)} if
     * available, otherwise will just continue processing, therefore
     * assume that all the information needed to "switch tabs" is already
     * available to the model.
     *
     * @return true if processing should continue, false if it should halt
     */
    public boolean onTabSwitch() {
        for (int i = 0; i < getPanels().size(); i++) {
            Panel panel = (Panel) getPanels().get(i);

            if (tabLink.getValue().equals(panel.getName())
                && !panel.isDisabled()
                && panel != getActivePanel()) {

                setActivePanel(panel);
                panel.onInit();
            }
        }

        registerActionEvent();

        return true;
    }

}
