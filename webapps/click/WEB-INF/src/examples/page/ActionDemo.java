package examples.page;

import java.util.Date;

import net.sf.click.Page;

/**
 * Provides an ActionDemo example Page.
 * 
 * @author Malcolm Edgar
 */
public class ActionDemo extends Page {

    net.sf.click.control.ActionLink actionLink;
    
    /**
     * @see Page#onInit()
     */
    public void onInit() {
        actionLink = new net.sf.click.control.ActionLink("link");
        actionLink.setListener(this, "onLinkClick");
        addControl(actionLink);
    }
    
    public boolean onLinkClick() {
        String msg = getClass().getName() + ".onLinkClick invoked at " + (new Date());
        addModel("clicked", msg);
        
        return true;
    }
}
