package net.sf.click.sandbox.chrisichris.prototype;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;

/**
 * A Page base class which does handle AjaxActions. Additional there is the possibility to set the
 * contenttype and the template.
 * <p>
 * To handle AjaxActions the [@link #onInit()} method and the onSecurityCheck 
 * methods are overridden. For intilazition of 
 * the page use either {@link #onInitAlways()} or {@link #onInitPage()}. For securitycheck use 
 * {@link #onSecurityCheckImpl()}
 * </p>
 * @author Christian Essl
 *
 */
public class AjaxPage extends Page{
    
    protected String contentType;
    
    private boolean invokePage = true;
    protected String template;
    
    public AjaxPage() {
    }
    
    /**
     * Overrides {@link Page#onInit()} to add special behaviour for handling AjaxActions. 
     * {@link #onInitAlways()} is always called first.  Page initilization code, 
     * which should always happen,
     * should be placed in this method.
     * This code is execeuted independently wheter an AjaxAction is called or not.
     * Than it is checked wheter an AjaxAction is called. If so first {@link #onSecurityCheckImpl()}
     * is called if it returns true the AjaxAction is executed and the normal
     * Page execution is skipped.
     * If there is no AjaxAction {@link #onInitPage()} is called. onInitPage() can 
     * be used for setting up the page for a normal request.
     */
    public void onInit() {
        super.onInit();
        onInitAlways();
        
        //check the ajax
        AjaxAction ac = AjaxAction.getAjaxAction(this);
        if(ac != null){
            invokePage = false;
            if(onSecurityCheckImpl()){
                ac.execute(this);
            }
        }
        if(invokePage) {
            onInitPage();
        }
    }
    
    /**
     * Overriden to skip normal page processing in case of an AjaxAction. Use {@link #onSecurityCheckImpl()}
     * to add custom securityCheck.
     */
    public boolean onSecurityCheck() {
        if(!invokePage){
            return false;
        }
        return onSecurityCheckImpl();
    }
    
    /**
     * Used instead of {@link Page#onSecurityCheck()}
     * @return true if the current request is secure
     */
    public boolean onSecurityCheckImpl(){
        return true;
    }
    
    /**
     * Allways called from onInit before ajax init or onInitPage
     */
    public void onInitAlways() {
    }

    /**
     * Used to initialize the Page for non AjaxAction calls.
     */
    public void onInitPage() {
    }

    /**
     * Returns the content type. If none was set returns {@link Page#getContentType()}
     * @return content type string appended with charset.
     */
    public String getContentType() {
        if(contentType == null) {
             return super.getContentType();
        } else {
            String charset = context.getRequest().getCharacterEncoding();
            if(charset != null
                    && contentType.startsWith("text")
                    && contentType.indexOf("charset=") == -1) {
                return contentType+"; charset="+charset;
            }else{
                return contentType;
            }
            
        }
    }
    
    /**
     * Set the content type.
     * @param contentType content type
     */
    public void setContentType(String contentType){
        this.contentType = contentType;
    }
    
    /**
     * The template to render, if no template was set returns {@link Page#getTemplate()}. 
     * @return the template path
     */
    public String getTemplate(){
        if(template == null){
            return super.getTemplate();
        }
        return template;
    }
    
    
    /**
     * Set the template to render
     * @param path to to template or null
     */
    public void setTemplate(String path){
        template = path;
    }
    
    

}
