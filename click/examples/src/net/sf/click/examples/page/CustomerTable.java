package net.sf.click.examples.page;

import java.util.List;

import net.sf.click.Page;

/**
 * Provides JSP Page example where a JSP page and JSP border template is used to
 * render a table.
 *
 * @author Malcolm Edgar
 */
public class CustomerTable extends BorderPage {

    public List customers = null;

    /**
     * @see Page#onRender()
     */
    public void onRender() {
        customers = getCustomerService().getCustomersSortedByName(10);
    }

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.jsp"</tt>
     *
     * @see Page#getTemplate()
     */
    public String getTemplate() {
        return "/border-template.jsp";
    }
}
