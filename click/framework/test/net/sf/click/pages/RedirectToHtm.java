package net.sf.click.pages;

import net.sf.click.Page;

/**
 * Page which redirects to another Page through its path.
 */
public class RedirectToHtm extends Page {
 
    /**
     * Redirect to test.htm path.
     */
    public void onInit() {
        setRedirect("/test.htm");
    }
}
