package net.sf.click.examples.page.security;

import net.sf.click.Page;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an <tt>onSecurityCheck</tt> example secure Page, which other secure
 * pages can extend.
 *
 * @author Malcolm Edgar
 */
public class Secure extends BorderPage {

    /**
     * @see Page#onSecurityCheck()
     */
    public boolean onSecurityCheck() {
        if (getContext().hasSessionAttribute("user")) {
            return true;

        } else {
            String path = getContext().getPagePath(Login.class);
            path += "?redirect=" + getPath();
            setRedirect(path);
            return false;
        }
    }

}
