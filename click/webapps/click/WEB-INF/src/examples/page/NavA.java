package examples.page;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;

/**
 * Provides an navigation example Page demonstrating forward and redirect 
 * page navigation.
 *
 * @author Malcolm Edgar
 */
public class NavA extends Page {

    /**
     * @see Page#onInit()
     */
    public void onInit() {
        ActionLink forwardLink = new ActionLink("forwardLink");
        forwardLink.setListener(this, "onForwardClick");
        addControl(forwardLink);

        ActionLink redirectLink = new ActionLink("redirectLink");
        redirectLink.setListener(this, "onRedirectClick");
        addControl(redirectLink);
    }
    
    public boolean onForwardClick() {
        setForward("navigation-b.htm");
        
        return true;
    }
    
    public boolean onRedirectClick() {
        setRedirect("navigation-b.htm");
        
        return true;
    }
}
