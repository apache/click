package net.sf.click.sandbox.chrisichris.prototype;

import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Submit;
import net.sf.click.sandbox.chrisichris.control.ControlUtils;
import net.sf.click.util.HtmlStringBuffer;

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
    protected final AjaxRequest ajaxReq = new AjaxRequest();

    /**
     * the ajaxAction.
     */

    /**
     * Create a AjaxSubmit button with the given name, listener object and
     * listener method.
     *
     * @param name the button name
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if listener is null or if the method
     * is blank
     */
    public AjaxSubmit(String name, Object listener, String method) {
        super(name, listener, method);
    }

    /**
     * Create a Submit button with the given name, label, listener object and
     * listener method.
     *
     * @param name the button name
     * @param label the button display label
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if listener is null or if the method
     * is blank
     */
    public AjaxSubmit(String name, String label, 
                        Object listener, String method) {
        super(name, label, listener, method);
    }

    /**
     * Create a Submit button with the given name and label.
     *
     * @param name the button name
     * @param label the button display label
     */
    public AjaxSubmit(String name, String label) {
        super(name, label);
    }

    /**
     * Create a Submit button with the given name.
     *
     * @param name the button name
     */
    public AjaxSubmit(String name) {
        super(name);
    }

    /**
     * Create an Submit button with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public AjaxSubmit() {
        super();
    }

    /**
     * The JS builder for the ajax call.
     * @return JS builder
     */
    public AjaxRequest getAjaxRequest() {
        return ajaxReq;
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

    public String getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");
        } else {
            String id = ControlUtils.getId(this);
            if(id != null) {
                setAttribute("id",id);
            }
            return id;
        }
    }
    
    public String getOnclick() {
        // set the action
        if (ajaxReq.getUrl() == null) {
            ajaxReq.setUrl(getContext().getResponse().encodeURL(getContext().getRequest().getRequestURI()));

        }
        ajaxReq.addParameter(getName(), getLabel());
        if (ajaxReq.getPostWith() == null && getForm() != null) {
            ajaxReq.setPostWithCurrentForm();
        } else {
            ajaxReq.addParameter(ActionLink.ACTION_LINK,this.getId());
        }

        String onClick = getAttribute("onclick");
        if (onClick != null) {
            return ajaxReq.toJS() + ";" + onClick;
        } else {
            return ajaxReq.toJS()+";return false;";
        }
        
    }
    /**
     * renders input html-tag.
     * @return input html-tag
     */
    public String toString() {
        String onClick = getAttribute("onclick");
        setOnClick(getOnclick());
        try {
            return super.toString();
        } finally {
            setOnClick(onClick);
        }
    }
    
    public boolean onProcess() {
        if(Prototype.isAjax(getContext())) {
            return super.onProcess();
        } else {
            return true;
        }
    }

    /**
     * Retruns the prototype JS link.
     * @return prototpye JS link
     * @see net.sf.click.control.Field#getHtmlImports()
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


}
