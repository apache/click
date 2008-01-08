package net.sf.click.examples.page.security;

import net.sf.click.Page;
import net.sf.click.examples.domain.User;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an user session logout Page.
 *
 * @author Malcolm Edgar
 */
public class Logout extends BorderPage {

    public User user;

    /**
     * @see Page#onInit()
     */
    public void onInit() {
        super.onInit();

        user = (User) getContext().getSessionAttribute("user");
        if (user != null) {
            getContext().removeSessionAttribute("user");
        }
    }
}
