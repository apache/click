package net.sf.click.sandbox.chrisichris.prototype;

import org.apache.commons.lang.StringUtils;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;

public class AjaxSubmit extends Submit{

    protected final PrototypeAjax ajaxReq = new PrototypeAjax();
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
    
    public PrototypeAjax getPrototypeAjax() {
        return ajaxReq;
    }
    
    public AjaxSubmit setAjaxAction(AjaxAction ac){
        this.ajaxAction = ac;
        return this;
        
    }
    
    public AjaxAction getAjaxAction(){
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
     *
     * @param src the image src attribute
     */
    public void setImageSrc(String src) {
        setAttribute("src", src);
    }

    /**
     * Return the input type: '<tt>submit</tt>'.
     *
     * @return the input type: '<tt>submit</tt>'
     */
    public String getType() {
        if(StringUtils.isBlank(getImageSrc())) {
            return "submit";
        } else {
            return "image";
        }
    }
    
    public String toString() {
        //set the action
        if(ajaxReq.getUrl() == null) {
            if(ajaxAction != null) {
                ajaxReq.setUrl(ajaxAction.getUrl(getContext()));
            } else {
                ajaxReq.setUrl(getForm().getActionURL());
            }
        }
        ajaxReq.setMethod(getForm().getMethod());
        ajaxReq.addParameter(getName(),getLabel());
        if(ajaxReq.getPostForm() == null && ajaxReq.getPostWith() == null) {
            ajaxReq.setPostWithCurrentForm();
        }
        
        String onClick = getAttribute("onclick");
        if(onClick != null) {
            setOnClick(ajaxReq.toJS()+";"+onClick);
        } else {
            setOnClick(ajaxReq.onClickJS());
        }
        try{
            return super.toString();
        }finally{
            setOnClick(onClick);
        }
    }
    
    /* (non-Javadoc)
     * @see net.sf.click.control.Field#getHtmlImports()
     */
    public String getHtmlImports() {
        String ret = DeployControl.getPrototypeImport(getContext());
        String su = super.getHtmlImports();
        if(su != null) {
            ret = ret + su;
        }
        return ret;
    }
    
    public void setInsertion(String str) {
        ajaxReq.setInsertion(str);
    }

    public void setOnComplete(String onComplete) {
        ajaxReq.setOnComplete(onComplete);
    }

    public void setOnFailure(String onFailure) {
        ajaxReq.setOnFailure(onFailure);
    }

    public void setOnSuccess(String onSucess) {
        ajaxReq.setOnSuccess(onSucess);
    }

    public void setUpdate(String elementId) {
        ajaxReq.setUpdate(elementId);
    }

    public void setUpdateFailure(String elementId) {
        ajaxReq.setUpdateFailure(elementId);
    }

    public void setUpdateSuccess(String elementId) {
        ajaxReq.setUpdateSuccess(elementId);
    }

    public void setAjaxUrl(String url) {
        ajaxReq.setUrl(url);
    }
    
    public void setProgressImage(String id) {
        ajaxReq.setProgressImage(id);
    }

}
