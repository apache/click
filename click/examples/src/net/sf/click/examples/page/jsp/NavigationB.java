package net.sf.click.examples.page.jsp;

/**
 * Provides an navigation example Page demonstrating forward and redirect
 * page navigation. See NavigationA page for details.
 *
 * @author Malcolm Edgar
 */
public class NavigationB extends NavigationA {

    public String getTarget() {
        return "/jsp/navigation-a.jsp";
    }

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.jsp"</tt>
     *
     * @see net.sf.click.Page#getTemplate()
     */
    public String getTemplate() {
        return "/border-template.jsp";
    }
}
