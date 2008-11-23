package net.sf.click.pages;

import net.sf.click.Page;

/**
 * This page redirects to itself.
 */
public class RedirectToSelfPage extends Page {

    /**
     * Redirect to self.
     */
    public void onInit() {
        // Redirect to self
        setRedirect(RedirectToSelfPage.class);
    }
}
