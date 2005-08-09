package net.sf.click.panel;

import net.sf.click.Panel;

/**
 * Provides a method of adding multiple panels that will be listed out in a
 * vertical fashion, each panel being evaluated via Velocity's #parse(), and by
 * default, surrounded with a &lt;div&gt; with the id of the list panel when
 * instantiated.
 * <p/>
 * <pre class="codeHtml">
 * &lt;div id="$_lp_id"&gt;
 * #foreach( $panel in $_lp_panels )
 * &lt;div id="$panel.id"&gt;
 * #parse($panel)
 * &lt;/div&gt;
 * #end
 * &lt;/div&gt;
 * </pre>
 * <p/>
 * As you can see, by default, the 'id' for this list panel will be put into the
 * model context as "_lp_id" and the list of sub-panels will be put in as
 * "_lp_panels" (the odd naming convention is to avoid conflict with user model
 * context objects)
 *
 * @author Phil Barnes
 * @since Jul 9, 2005
 */
public class ListPanel extends BasicPanel {
	/** The context key used to lookup the ID assocaited with this panel. */
	protected static final String INTERNAL_ID_KEY = "_lp_id";

	/**
	 * The context key used to lookup the sub-panel list associated with this
	 * panel.
	 */
	protected static final String INTERNAL_PANEL_LIST_KEY = "_lp_panels";

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
	 * Overridden method to capture and add the panels to the model with an
	 * internal name ("_lp_panels") so that the template may iterate over this
	 * name for each panel that was added to the list.
	 *
	 * @param panel
	 */
	public void addPanel(Panel panel) {
		super.addPanel(panel);
		// this should continually override the existing _lp_panels entry
		removeModel(INTERNAL_PANEL_LIST_KEY);
		addModel(INTERNAL_PANEL_LIST_KEY, getPanels());
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

