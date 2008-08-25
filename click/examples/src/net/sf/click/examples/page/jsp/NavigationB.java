package net.sf.click.examples.page.jsp;

/**
 * Provides an navigation example Page demonstrating forward and redirect
 * page navigation. See NavigationA page for details.
 *
 * @author Malcolm Edgar
 */
public class NavigationB extends NavigationA {

    /**
     * Target template to forward to.
     * <p/>
     * In order to forward to a Page with a JSP template, we specify the target
     * with an htm extension so that ClickServlet will process the Page.
     * After the Page NavigationA.java is processed, Click will forward to the
     * underlying template /jsp/navigation-a.jsp.
     */
    public String getTarget() {
        return "/jsp/navigation-a.htm";
    }

    /**
     * Note one can also forward and redirect using the Page class instead of
     * the path as seen below.
     */
    /*
    public Class getTargetPageClass() {
        return NavigationA.class;
    }*/

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.jsp"</tt>
     *
     * @see net.sf.click.Page#getTemplate()
     */
    public String getTemplate() {
        return "/border-template.jsp";
    }
}
