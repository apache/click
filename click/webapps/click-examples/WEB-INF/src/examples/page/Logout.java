package examples.page;

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
            addModel("user", getContext().getSessionAttribute("user"));
            getContext().getSession().invalidate(); 
        }          
    }
}
