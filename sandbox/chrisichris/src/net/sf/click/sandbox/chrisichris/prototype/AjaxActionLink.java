package net.sf.click.sandbox.chrisichris.prototype;

import net.sf.click.control.ActionLink;
import net.sf.click.sandbox.chrisichris.control.ChildActionLink;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * An ActionLink which sends an ajax-request instead of a normal request.
 *
 * @author Christian Essl
 *
 */
public class AjaxActionLink extends ChildActionLink {

    /**
     * prototype ajax helper.
     */
    protected final AjaxRequest ajaxReq = new AjaxRequest();



    /**
     * Create an AjaxActionLink. Maily for tools.
     */
    public AjaxActionLink() {
        super();
    }

    /**
     * Create an AjaxActionLink for the given name, listener object and listener
     * method.
     *
     * @param name the action link name
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public AjaxActionLink(String name, Object listener, String method) {
        super(name, listener, method);
    }

    /**
     * Create an AjaxActionLink for the given name, label, listener object and
     * listener method.
     *
     * @param name the action link name
     * @param label the action link label
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public AjaxActionLink(String name, String label, Object listener,
            String method) {
        super(name, label, listener, method);
    }

    /**
     * Create an AjaxActionLink for the given name and label.
     *
     * @param name the action link name
     * @param label the action link label
     * @throws IllegalArgumentException if the name is null
     */
    public AjaxActionLink(String name, String label) {
        super(name, label);
    }

    /**
     * Create an ActionLink for the given name.
     *
     * @param name the action link name
     * @throws IllegalArgumentException if the name is null
     */
    public AjaxActionLink(String name) {
        super(name);
    }

    /**
     * The PrototypeAjax used by this AjaxActionLink to create the
     * Ajax JS code.
     * @return PrototypeAjax
     */
    public AjaxRequest getAjaxRequest() {
        return ajaxReq;
    }




    /**
     * The html imports for prototype.
     * @return script import to prototype
     */
    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        String path = getContext().getRequest().getContextPath();
        buffer.append("<script type=\"text/javascript\" src=\"");
        buffer.append(path);
        buffer.append("/click/prototype/prototype.js\"></script>\n");

        buffer.append("<script type=\"text/javascript\" src=\"");
        buffer.append(path);
        buffer.append("/click/prototype/scriptaculous.js\"></script>\n");
        
        String ret = buffer.toString();
        return ret;
    }

    /**
     * Same as {@link ActionLink#getHref()} or '#' if {@link #getEnableHref()}
     * is false.
     *
     * @param value a possible value to send with the ActionLink
     * @return the ActionLink url or #
     */
    public String getHref(Object value) {
         return "#";
    }

    /**
     * The JS script for the ajax request which will trigger this AjaxAction. If
     * {@link #getEnableAjax()} is false returns an empty string:
     *
     * <pre>
     *
     *    Usage:
     *    [template}
     *    lt;a href=&quot;#&quot; onclick=&quot;$ajaxlink.getOnClick(&quot;someValue&quot;)&quot;&gt;
     *
     * </pre>
     *
     * @param value
     *            value to send as ActionValue (toString() is used).
     * @return JS script
     */
    public String getOnclick(Object value) {

            String uri = ajaxReq.getUrl();
            if (ajaxReq.getUrl() == null) {
                ajaxReq.setUrl(getContext().getResponse().encodeURL(getContext().getRequest().getRequestURI()));
            }

            ajaxReq.addParameter(ACTION_LINK, getId());
            if (value != null) {
                ajaxReq.addParameter(VALUE, value.toString());
            }

            String ret = ajaxReq.toString()+";return false;";
            String onClick = getAttribute("onclick");
            if (onClick != null) {
                ret = ret + ";" + onClick;
            }
            return ret;
    }

    /**
     * The JS script for the ajax request which will trigger this AjaxAction. If
     * {@link #getEnableAjax()} is false returns an empty string
     *
     * @return the onClickJS for the currently set value
     */
    public String getOnclick() {
        return getOnclick(getValue());
    }
    
    public boolean onProcess() {
        if(Prototype.isAjax(getContext())) {
            return super.onProcess();
        } else {
            return true;
        }
    }

    /**
     * Renders the ActionLink.
     *
     * @return a html-tag
     * @see net.sf.click.control.ActionLink#toString()
     */
    public String toString() {
        String oncl = getOnclick();
        try {
            setAttribute("onclick", getOnclick());
            return super.toString();
        } finally {
            setAttribute("onclick", oncl);
        }
    }

}
