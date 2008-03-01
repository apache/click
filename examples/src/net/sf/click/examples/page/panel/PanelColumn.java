package net.sf.click.examples.page.panel;

import net.sf.click.Context;
import net.sf.click.control.Column;
import net.sf.click.control.Panel;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a custom Column that wraps a Panel for display in a Table.
 *
 * @author rlecheta
 */
public class PanelColumn extends Column {

    private static final long serialVersionUID = 1L;
    
    private final Panel panel;

    // ------------------------------------------------------------ Constructor
    
    public PanelColumn(String name, Panel panel) {
        super(name);
        this.panel = panel;
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * @see Column#renderTableDataContent(Object, HtmlStringBuffer, Context, int)
     */
    protected void renderTableDataContent(Object row, HtmlStringBuffer buffer,
        Context context, int rowIndex) {

        // We use the name of the column in the model. So if the name is
        // "customer" we can access $customer.name in the "panel.htm" template
        panel.getModel().remove(name);
        panel.addModel(name, row);
        buffer.append(panel.toString());
    }
}
