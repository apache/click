package net.sf.click.sandbox.chrisichris.prototype;

import net.sf.click.control.ActionLink;

/**
 * An ActionLink which sends an ajax-request instead of a normal request.
 *
 * @author Christian Essl
 *
 */
public class AjaxActionLink extends ActionLink {

    /**
     * prototype ajax helper.
     */
    protected final PrototypeAjax ajaxReq = new PrototypeAjax();

    /**
     * ajaxAction to call onClick.
     */
    protected AjaxAction ajaxAction;

    /**
     * wheter href is should be printed.
     */
    protected boolean enableHref = true;

    /**
     * wheter onClick should be printed.
     */
    protected boolean enableAjax = true;

    /**
     *
     */
    public AjaxActionLink() {
        super();
    }

    /**
     * @param name
     * @param listener
     * @param method
     */
    public AjaxActionLink(String name, Object listener, String method) {
        super(name, listener, method);
    }

    /**
     * @param name
     * @param label
     * @param listener
     * @param method
     */
    public AjaxActionLink(String name, String label, Object listener,
            String method) {
        super(name, label, listener, method);
    }

    /**
     * @param name
     * @param label
     */
    public AjaxActionLink(String name, String label) {
        super(name, label);
    }

    /**
     * @param name
     */
    public AjaxActionLink(String name) {
        super(name);
    }

    public PrototypeAjax getPrototypeAjax() {
        return ajaxReq;
    }

    /**
     * AjaxAction to be executed by the link.
     *
     * @param ac
     *            the AjaxAction or null
     * @return this
     */
    public AjaxActionLink setAjaxAction(AjaxAction ac) {
        this.ajaxAction = ac;
        return this;

    }

    /**
     * An AjaxAciton set.
     *
     * @return ajax action set
     */
    public AjaxAction getAjaxAction() {
        return this.ajaxAction;
    }

    /**
     * Wheter also the normal href action url should be rendered.
     *
     * @return true or false
     */
    public boolean getEnableHref() {
        return this.enableHref;
    }

    /**
     * Wheter also the normal href action url sould be rendered.
     *
     * @param value
     *            true or false
     */
    public void setEnableHref(boolean value) {
        this.enableHref = value;
    }

    /**
     * Wheter the onClick for Ajax should be rendered.
     *
     * @return true or false
     */
    public boolean getEnableAjax() {
        return this.enableAjax;
    }

    /**
     * Wheter the onClick for Ajax should be rendered.
     *
     * @param enable true /false
     */
    public void setEnableAjax(boolean enable) {
        this.enableAjax = enable;
    }

    /**
     * The html imports for prototype.
     * @return script import to prototype
     */
    public String getHtmlImports() {
        String ret = DeployControl.getPrototypeImport(getContext());
        String su = super.getHtmlImports();
        if (su != null) {
            ret = ret + su;
        }
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
        if (!enableHref) {
            return "#";
        }
        return super.getHref(value);
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
    public String getOnClick(Object value) {
        if (getEnableAjax()) {

            String uri = ajaxReq.getUrl();
            if (uri == null) {
                if (ajaxAction != null) {
                    uri = ajaxAction.getUrl(getContext());
                } else {
                    uri = getContext().getRequest().getRequestURI();
                }
            }
            ajaxReq.setUrl(uri);

            ajaxReq.addParameter(ACTION_LINK, getName());
            if (value != null) {
                ajaxReq.addParameter(VALUE, value.toString());
            }

            String ret = ajaxReq.onClickJS();
            String onClick = getAttribute("onclick");
            if (onClick != null) {
                ret = ret + ";" + onClick;
            }
            return ret;
        } else {
            return "";
        }
    }

    /**
     * The JS script for the ajax request which will trigger this AjaxAction. If
     * {@link #getEnableAjax()} is false returns an empty string
     *
     * @return the onClickJS for the currently set value
     */
    public String getOnClick() {
        return getOnClick(getValue());
    }

    /**
     * Renders the ActionLink.
     *
     * @return a html-tag
     * @see net.sf.click.control.ActionLink#toString()
     */
    public String toString() {
        if (getEnableAjax()) {
            String oncl = getOnClick();
            try {
                setAttribute("onclick", getOnClick());
                return super.toString();
            } finally {
                setAttribute("onclick", oncl);
            }
        } else {
            return super.toString();
        }
    }

    /**
     * optional parameter to add to the ajax request.
     *
     * @param name paramter name
     * @param value parameter value
     */
    public void addParameter(String name, String value) {
        ajaxReq.addParameter(name, value);
    }

    /**
     * In case of update where to insert the response.
     *
     * @param str
     *            one of the static values of {@link Prototype}
     */
    public void setInsertion(String str) {
        ajaxReq.setInsertion(str);
    }

    /**
     * JS script to execute when the ajax-request is complete.
     *
     * @param onComplete JS to execute when the AJAXRequest is complete
     */
    public void setOnComplete(String onComplete) {
        ajaxReq.setOnComplete(onComplete);
    }

    /**
     * JS script to execute when ajax-request returned an html-failure code.
     *
     * @param onFailure JS
     */
    public void setOnFailure(String onFailure) {
        ajaxReq.setOnFailure(onFailure);
    }

    /**
     * JS script to execute when the ajax-request was successful.
     *
     * @param onSucess JS
     */
    public void setOnSuccess(String onSucess) {
        ajaxReq.setOnSuccess(onSucess);
    }

    /**
     * Html element-id of the element which should be updated with the html
     * returned from the ajax-response.
     *
     * @param elementId
     *            html-element id
     */
    public void setUpdate(String elementId) {
        ajaxReq.setUpdate(elementId);
    }

    /**
     * Html element-id of the element which should be updated with the html
     * returned from the ajax-response with a failure.
     *
     * @param elementId
     *            html-element id
     */
    public void setUpdateFailure(String elementId) {
        ajaxReq.setUpdateFailure(elementId);
    }

    /**
     * Html element-id of the element which should be updated with the html
     * returned from s succesful ajax-response.
     *
     * @param elementId
     *            html-element id
     */
    public void setUpdateSuccess(String elementId) {
        ajaxReq.setUpdateSuccess(elementId);
    }

    /**
     * Id of an html tag which should be made visible during the execution of a
     * request.
     *
     * @param id
     *            element id
     */
    public void setProgressImage(String id) {
        ajaxReq.setProgressImage(id);
    }
}
