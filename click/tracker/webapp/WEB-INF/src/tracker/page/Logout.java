package tracker.page;

import net.sf.click.Page;

/**
 * Provides an user session logout Page.
 *
 * @author Malcolm Edgar
 */
public class Logout extends Page {

    /**
     * @see Page#onInit()
     */
    public void onInit() {
        if (getContext().hasSession()) {
            String remoteUser = getContext().getRequest().getRemoteUser();
            addModel("user", remoteUser);
            getContext().getSession().invalidate();
        }
    }
}
