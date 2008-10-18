package net.sf.click.examples.page.jsp;

import net.sf.click.control.ActionLink;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an navigation example Page demonstrating forward and redirect
 * page navigation.
 *
 * @author Malcolm Edgar
 */
public class NavigationA extends BorderPage {

    public ActionLink forwardLink = new ActionLink("forwardLink", this, "onForwardClick");
    public ActionLink forwardParamLink = new ActionLink("forwardParamLink", this, "onForwardParamClick");
    public ActionLink redirectLink = new ActionLink("redirectLink", this, "onRedirectClick");
    public ActionLink redirectParamLink = new ActionLink("redirectParamLink", this, "onRedirectParamClick");

    // --------------------------------------------------------- Event Handlers

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        super.onInit();

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

    // --------------------------------------------------------- Public Methods

    /**
     * Target template to forward to.
     * <p/>
     * In order to forward to a Page with a JSP template, we specify the target
     * with an htm extension so that ClickServlet will process the Page.
     * After the Page NavigationB.java is processed, Click will forward to the
     * underlying template /jsp/navigation-b.jsp.
     */
    public String getTarget() {
        return "/jsp/navigation-b.htm";
    }

    /**
     * Note one can also forward and redirect using the Page class instead of
     * the path as seen below.
     */
    /*
    public Class getTargetPageClass() {
        return NavigationB.class;
    }*/

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.jsp"</tt>
     *
     * @see net.sf.click.Page#getTemplate()
     */
    public String getTemplate() {
        return "/border-template.jsp";
    }
}