package examples.page;

import net.sf.click.Page;

/**
 * Provides an <tt>onSecurityCheck</tt> example secure Page, which other secure
 * pages can extend.
 *
 * @author Malcolm Edgar
 */
public class Secure extends BorderedPage {

    /**
     * @see Page#onSecurityCheck()
     */
    public boolean onSecurityCheck() {
        if (getContext().hasSessionAttribute("user")) {
            return true;

        } else {
            setRedirect("login.htm");
            return false;
        }
    }

}
