package examples.page;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;

/**
 * Provides an navigation example Page demonstrating forward and redirect 
 * page navigation.
 *
 * @author Malcolm Edgar
 */
public class NavigationA extends Page {
    
    ActionLink forwardLink;
    ActionLink forwardParamLink;
    ActionLink redirectLink;
    ActionLink redirectParamLink;
    
    public String getTarget() {
        return "navigation-b.htm";
    }

    /**
     * @see Page#onInit()
     */
    public void onInit() {
        // Initialize action links
        forwardLink = new ActionLink("forwardLink");
        forwardLink.setListener(this, "onForwardClick");
        addControl(forwardLink);
        
        forwardParamLink = new ActionLink("forwardParamLink");
        forwardParamLink.setListener(this, "onForwardParamClick");
        addControl(forwardParamLink);

        redirectLink = new ActionLink("redirectLink");
        redirectLink.setListener(this, "onRedirectClick");
        addControl(redirectLink);
        
        redirectParamLink = new ActionLink("redirectParamLink");
        redirectParamLink.setListener(this, "onRedirectParamClick");
        addControl(redirectParamLink);
        
        // Initialise param ActionLink values from any parameters passed through
        // from other pages via forwards or redirects.
        Integer number = new Integer(1);
        
        // If request has been forwarded
        if (getContext().isForward()) {
            // If a request attribute was passed increment its value.
            Integer param = (Integer) getContext().getRequestAttribute("param");
            if (param != null) {
                number = new Integer(param.intValue() + 1);
            }
            
        // Else request may have been redirected
        } else {
            String param = getContext().getRequest().getParameter("param");
            if (param != null) {
                number = new Integer(Integer.parseInt(param) + 1);
            }
        }
        
        forwardParamLink.setValue(number.toString());
        redirectParamLink.setValue(number.toString());
    }
    
    public boolean onForwardClick() {
        setForward(getTarget());
        return false;
    }
    
    public boolean onForwardParamClick() {
        Integer param = forwardParamLink.getValueInteger();
        getContext().setRequestAttribute("param", param);
        setForward(getTarget());
        return false;
    }
    
    public boolean onRedirectClick() {
        setRedirect(getTarget());        
        return false;
    }
    
    public boolean onRedirectParamClick() {
        setRedirect(getTarget() + "?param=" + redirectParamLink.getValue());
        return false;
    }
}
