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

    /**
     * @see Page#onInit()
     */
    public void onInit() {
        User user = (User) getContext().getSessionAttribute("user");
        if (user != null) {
            addModel("user", user);
            getContext().removeSessionAttribute("user");
        }
    }
}
