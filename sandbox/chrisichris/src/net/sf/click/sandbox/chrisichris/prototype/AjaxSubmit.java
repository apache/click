package net.sf.click.sandbox.chrisichris.prototype;

import net.sf.click.control.Submit;

import org.apache.commons.lang.StringUtils;

/**
 * A Submit which does submit the Form it is contained in
 * through AJAX.
 * <p>
 * By default the button will submit like a normal Submit
 * to the page it came from and will invoke its Listener.
 * Just like a normal Submit does. The listener method is
 * however responsible to render back the ajax response.
 * </p>
 *
 * @author Christian Essl
 *
 */
public class AjaxSubmit extends Submit {

    /**
     * builds JS.
     */
    protected final PrototypeAjax ajaxReq = new PrototypeAjax();

    /**
     * the ajaxAction.
     */
    protected AjaxAction ajaxAction;

    /**
     * @param name
     * @param listener
     * @param method
     */
    public AjaxSubmit(String name, Object listener, String method) {
        super(name, listener, method);
    }

    /**
     * @param name
     * @param label
     * @param listener
     * @param method
     */
    public AjaxSubmit(String name, String label, Object listener, String method) {
        super(name, label, listener, method);
    }

    /**
     * @param name
     * @param label
     */
    public AjaxSubmit(String name, String label) {
        super(name, label);
    }

    /**
     * @param name
     */
    public AjaxSubmit(String name) {
        super(name);
    }

    public AjaxSubmit() {
        super();
    }

    /**
     * The JS builder for the ajax call.
     * @return JS builder
     */
    public PrototypeAjax getPrototypeAjax() {
        return ajaxReq;
    }

    /**
     * An AjaxAction where the request should
     * be send to instead of the original Page.
     * @param ac AjaxAction or null
     */
    public void setAjaxAction(AjaxAction ac) {
        this.ajaxAction = ac;
    }

    /**
     * The set AjaxAction default is null.
     * @return can return null
     */
    public AjaxAction getAjaxAction() {
        return this.ajaxAction;
    }

    /**
     * Return the image src path attribute.
     *
     * @return the image src attribute
     */
    public String getImageSrc() {
        return getAttribute("src");
    }

    /**
     * Set the image src path attribute.
     * If set the button will render as an image button.
     *
     * @param src
     *            the image src attribute
     */
    public void setImageSrc(String src) {
        setAttribute("src", src);
    }

    /**
     * Return the input type: 'submit' or
     * 'image' depending wheter
     * {@link #getImageSrc()} returns null or not.
     *
     *
     * @return the input type: '<tt>submit</tt>'
     */
    public String getType() {
        if (StringUtils.isBlank(getImageSrc())) {
            return "submit";
        } else {
            return "image";
        }
    }

    /**
     * renders input html-tag.
     * @return input html-tag
     */
    public String toString() {
        // set the action
        if (ajaxReq.getUrl() == null) {
            if (ajaxAction != null) {
                ajaxReq.setUrl(ajaxAction.getUrl(getContext()));
            } else {
                ajaxReq.setUrl(getForm().getActionURL());
            }
        }
        ajaxReq.setMethod(getForm().getMethod());
        ajaxReq.addParameter(getName(), getLabel());
        if (ajaxReq.getPostForm() == null && ajaxReq.getPostWith() == null) {
            ajaxReq.setPostWithCurrentForm();
        }

        String onClick = getAttribute("onclick");
        if (onClick != null) {
            setOnClick(ajaxReq.toJS() + ";" + onClick);
        } else {
            setOnClick(ajaxReq.onClickJS());
        }
        try {
            return super.toString();
        } finally {
            setOnClick(onClick);
        }
    }

    /**
     * Retruns the prototype JS link.
     * @return prototpye JS link
     * @see net.sf.click.control.Field#getHtmlImports()
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
     * In case of update where to insert the request-result.
     * @param str one of the {@link Prototype} INSERTION constants
     */
    public void setInsertion(String str) {
        ajaxReq.setInsertion(str);
    }

    /**
     * JS to execute when the AJAX request is complete.
     * @param onComplete JS
     */
    public void setOnComplete(String onComplete) {
        ajaxReq.setOnComplete(onComplete);
    }

    /**
     * JS to execute when the AJAX request returned a failure.
     * @param onFailure JS
     */
    public void setOnFailure(String onFailure) {
        ajaxReq.setOnFailure(onFailure);
    }

    /**
     * JS to execute when the AJAX request was successful.
     * @param onSucess JS
     */
    public void setOnSuccess(String onSucess) {
        ajaxReq.setOnSuccess(onSucess);
    }

    /**
     * HTML-Element-Id where the result will be put. How
     * the result is put in there depends on the
     * {@link #setInsertion(String)} by default it
     * is set as innerhtml.
     * @param elementId of the element to update.
     */
    public void setUpdate(String elementId) {
        ajaxReq.setUpdate(elementId);
    }

    /**
     * HTML-Element-Id which should be updated in case of
     * failur.
     * @param elementId html-id
     */
    public void setUpdateFailure(String elementId) {
        ajaxReq.setUpdateFailure(elementId);
    }

    /**
     * HTML-Element-Id which should be updated in case of
     * success.
     * @param elementId element id
     */
    public void setUpdateSuccess(String elementId) {
        ajaxReq.setUpdateSuccess(elementId);
    }

    /**
     * An html tag which should be made visible during
     * the request running.
     * @param id HTML-Element-Id of the tag
     */
    public void setProgressImage(String id) {
        ajaxReq.setProgressImage(id);
    }

}
