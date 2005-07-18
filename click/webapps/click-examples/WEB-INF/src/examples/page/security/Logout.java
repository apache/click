package examples.page.security;

import net.sf.click.Page;
import examples.domain.User;
import examples.page.BorderedPage;

/**
 * Provides an user session logout Page.
 *
 * @author Malcolm Edgar
 */
public class Logout extends BorderedPage {

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
