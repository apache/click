package net.sf.click.pages;

/**
 * Page which redirects to JspPage by specifying the JSP path.
 */
public class JspRedirectPage extends RedirectToJsp {

    /**
     * Initialize page.
     */
    public void onInit() {
        setRedirect("/test.jsp");
    }
}
