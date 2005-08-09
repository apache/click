package net.sf.click.panel;

import net.sf.click.Page;
import net.sf.click.Panel;
import net.sf.click.control.ActionLink;
import net.sf.click.util.ClickLogger;
import net.sf.click.util.ClickUtils;

/**
 * Provides a way to add multiple sub-panels in 'tabs'.  This panel comes with a
 * default template that will render the panels in CSS customizable table tags.
 * Additionally, a listener may be attached (similar to the control listeners)
 * that will be called on tab switch. This could be useful to load (or reload)
 * model related information for which ever panel is selected by the user.
 * <p/>
 * <pre class="codeHtml">
 * &lt;div id='$_tp_id'&gt;
 * &lt;table class="tp_tab"&gt;
 * &lt;tr class="tp_tab"&gt;
 * #foreach( $panel in $_tp_panels )
 * #if( $panel.id == $_tp_activePanel.id )
 * &lt;td class="tp_tab_on"&gt;
 * $panel.label
 * &lt;/td&gt;
 * &lt;td class="tp_tab_space"&gt;&lt;/td&gt;
 * #else
 * &lt;td class="tp_tab_off"&gt;
 * &lt;a href="$_tp_tabLink.getHref($panel.name)" id="$panel.id" class="tp_tab_link"&gt;$panel.label&lt;/a&gt;
 * &lt;/td&gt;
 * &lt;td class="tp_tab_space"&gt;&lt;/td&gt;
 * #end
 * #end
 * &lt;/tr&gt;
 * &lt;/table&gt;
 * &lt;table class="tp_content"&gt;
 * &lt;tr class="tp_content"&gt;
 * &lt;td class="tp_content"&gt;
 * #parse($_tp_activePanel)
 * &lt;/td&gt;
 * &lt;/tr&gt;
 * &lt;/table&gt;
 * &lt;/div&gt;
 * </pre>
 * <p/>
 * As shown above, the the model context variables associated with a tabbed
 * panel are as follows:
 * <ul>
 * <li>_tp_id - the id associated with this tabbed panel</li>
 * <li>_tp_panels - the sub-panels added to this panel</li>
 * <li>_tp_activePanel - the currently active panel (aka tab)</li>
 * <li>_tp_tabLink - an {@link ActionLink} control that handles the switching
 * between the various panels.</li>
 * </ul>
 * <p/>
 * Also, as show above, there are a number of CSS attributes that allow some
 * customization of the output.  These are as follows (these are the exact
 * colors show in the example for tabbed panels):
 * <pre class="codeCss">
 * table.tp_tab { border-collapse: collapse; }
 * tr.tp_tab { }
 * td.tp_tab_on {  background: #336699; color: #ffffff; border-left: 1px solid #336699; border-top: 1px solid #336699; border-right: 1px solid #336699; padding: 5px; }
 * td.tp_tab_off { background: #cccccc; color: #000000; border-left: 1px solid #336699; border-top: 1px solid #336699; border-right: 1px solid #336699; padding: 5px; }
 * table.tp_content { border: 1px solid #336699; }
 * tr.tp_content { }
 * td.tp_content { background: #efefef; }
 * <pre>
 *
 * @author Phil Barnes
 * @since Jul 4, 2005 @ 3:59:49 PM
 */
public class TabbedPanel extends BasicPanel {
	/** The logger associated with this tabbed panel. */
	protected static ClickLogger log = new ClickLogger();

	private Object listener;
	private String method;
	private ActionLink tabActionLink;

	public TabbedPanel(String id) {
		super(id);
		addModel("_tp_id", id);
	}

	/**
	 * <b>NOTE:</b>  This method should <b>not</b> be called, in favor of
	 * addPanel(Panel, boolean), to ensure the "active" panel is defined.  This
	 * method will by default set the active panel to the current panel being
	 * passed, to ensure that at least one active panel has been set.
	 *
	 * @param panel
	 */
	public void addPanel(Panel panel) {
		log.warn("WARNING: TabbedPanel should have panels added ONLY via" +
				" addPanel(panel, boolean) to ensure the active panel" +
				" is correctly set.  Setting active panel to *this* panel ('" +
				panel.getName() + "') as a result.");
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
			if (log.isDebugEnabled()) {
				log.debug("Adding panel with id " + panel.getId() +
						" as the active panel.");
			}
			setActivePanel(panel);
		}
		if (getPanels().size() > 1 && tabActionLink == null) {
			if (log.isDebugEnabled()) {
				log.debug("Two or more panels detected, enabling " +
						"tabActionLink. Current listener status = " +
						this.listener + "." + this.method + "()");
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
	 * <p/>
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

	public void setPage(Page page) {
		super.setPage(page);
		// add the context to the tabActionLink control - TODO: is there a better way to do set the context?
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
		if (this.listener != null && this.method != null) {
			log.debug(
					"Invoking listener " + this.listener + "." + this.method + "()");
			return ClickUtils.invokeListener(listener, method);
		}
		// this implies that everything needed to render the next tab has been
		// added to the "model" context already
		else {
			log.debug("No listener.method() found, continuing processing...");
			return true;
		}
	}

	/**
	 * Removes the current 'active panel' from the model, and adds the passed
	 * panel as the new active panel.
	 *
	 * @param panel
	 */
	private void setActivePanel(Panel panel) {
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
}
