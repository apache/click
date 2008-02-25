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

    private final Panel panel;

    public PanelColumn(String name, Panel panel) {
        super(name);
        this.panel = panel;
    }

    /**
     * @see net.sf.click.control.Column#renderTableDataContent(java.lang.Object, net.sf.click.util.HtmlStringBuffer, net.sf.click.Context, int)
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
