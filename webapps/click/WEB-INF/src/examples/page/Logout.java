package examples.page;

import examples.domain.User;
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
        User user = (User) getContext().getSessionAttribute("user");
        addModel("user", user);
        
        getContext().getSession().removeAttribute("user");
        getContext().getSession().invalidate();  
    }
}
