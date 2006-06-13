package net.sf.click.sandbox.chrisichris.control;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.util.HtmlStringBuffer;

/**
 * A page which stores its state in a (hidden) form. NOTE: this is highly expiremently.
 * If you use this page all your links
 * should {@link net.sf.click.sandbox.chrisichris.control.ChildActionLink} and forms
 * {@link net.sf.click.sandbox.chrisichris.control.BaseForm}.
 * @author Christian
 *
 */
public class StatePage extends Page {

    public static final String STATE_FORM_MODEL_NAME = "state_Form";

    public static final String FLASH_COUNT_SESS_ATT = StatePage.class.getName()+":FLASH_COUNT";
    public static final String FLASH_VALUE_SESS_ATT = StatePage.class.getName()+":FLASH_VALUE:";
    public static final String FLASH_COUNT_REQ_PARA = "click_flash";
    
    private final StateForm stateForm = new StateForm();
    private final StateField stateField = 
        new StateField(this.getClass());
    private final HashMap stateMap = new HashMap();

    public StatePage() {
        super();
        addModel(STATE_FORM_MODEL_NAME,stateForm);
    }
    
    public void setContext(Context context) {
        stateField.setContextInt(context);
        super.setContext(context);
    }

    public boolean onSecurityCheck() {
        //TODO: work with multipart.
        //TODO: make ajax cacheing
        boolean isForward = 
            getContext().getRequest().
                getAttribute("javax.servlet.forward.request_uri") != null;
        
        doStateMapping(context,isForward);
        doFlashRead(context,isForward);
        return super.onSecurityCheck();
    }
    
    
    private void doStateMapping(Context context, boolean isForward) {
        if(!isForward) {
            if("post".equalsIgnoreCase(context.getRequest().getMethod())) {
                stateField.bindRequestValue();
                HashMap sM = (HashMap) stateField.getValueObject();
                if(sM != null) {
                    //sM.keySet().removeAll(stateMap.keySet());
                    stateMap.putAll(sM);
                } 
            }
        }
        this.stateField.setValueObject(stateMap);
    }
    
    private void doFlashRead(Context context, boolean isForward) {
        if("get".equalsIgnoreCase(context.getRequest().getMethod())){
            //if flash
            String flS = context.getRequestParameter(FLASH_COUNT_REQ_PARA);
            if(flS != null) {
                Long l;
                try{
                    l = Long.valueOf(flS);
                }catch(NumberFormatException e) {
                    throw new RuntimeException("request-param ["+FLASH_COUNT_REQ_PARA+"] is no long");
                }
                HttpSession session = context.getRequest().getSession(false);
                if(session != null) {
                    HashMap fM = (HashMap) session.getAttribute(getFlashSessionDataName(l));
                    if(fM != null) {
                        session.removeAttribute(getFlashSessionDataName(l));
                        this.stateMap.putAll(fM);
                    } else {
                        throw new IllegalStateException("Flash session has expired.");
                    }
                } else {
                    throw new IllegalStateException("Flash session has expired.");
                }
            }
        }
    }
    
    public void setStateAttribute(String name, Serializable value) {
        if(name == null) {
            throw new NullPointerException("No name param");
        }
        if(value == null) {
            this.stateMap.remove(name);
        } else {
            this.stateMap.put(name,value);
        }
    }
    
    public Serializable getStateAttribute(String name) {
        if(name == null) {
            throw new NullPointerException("No name param");
        }
        Serializable ret = (Serializable) this.stateMap.get(name);
        return ret;
    }
    
    public static void setStateAttribute(Control ctrl, String name, Serializable value) {
        if(name == null) {
            throw new NullPointerException("No name param");
        }
        StatePage page = getStatePage(ctrl);
        if(page == null) {
            throw new IllegalStateException("No state page found for control");
        }
        String cName = getComposedName(ctrl,name);
        page.setStateAttribute(cName,value);
    }
    
    public static Serializable getStateAttribute(Control ctrl, String name) {
        if(name == null) {
            throw new NullPointerException("No name param");
        }
        StatePage page = getStatePage(ctrl);
        if(page == null) {
            throw new IllegalStateException("No state page found for control");
        }
        String cName = getComposedName(ctrl,name);
        return page.getStateAttribute(cName);
        
    }
    
    private static String getComposedName(Control ctrl,String name) {
        return ctrl.getId() + "|" + name;
    }

    public HiddenField getStateField() {
        return stateField;
    }
    
    
    public StateForm getStateForm() {
        return stateForm;
    }
    
    
    
    public static StatePage getStatePage(Control ctrl) {
        Object parent = ctrl.getParent();
        Page page = null;
        while(parent != null) {
            if(parent instanceof Page) {
                page = (Page) parent;
                break;
            }
            if(parent instanceof Control) {
                parent = ((Control)parent).getParent();
            } else {
                break;
            }
        }
        if(page instanceof StatePage) {
            return (StatePage) page;
        }
        return null;
    }
    
    //flash memory things
    public void setFlashRedirect(StatePage page) {
        if(page == null) {
            throw new NullPointerException("No page param");
        }
        
        HttpSession session = getContext().getRequest().getSession(true);
        
        //the counter
        Long l;
        synchronized(session) {
            l = (Long) session.getAttribute(FLASH_COUNT_SESS_ATT);
            if(l == null) {
                l = new Long(System.currentTimeMillis());
            }
            l = new Long(l.longValue()+1);
            session.setAttribute(FLASH_COUNT_SESS_ATT,l);
        }
        //the data
        Map data = new HashMap(page.stateMap);
        session.setAttribute(getFlashSessionDataName(l),data);
        
        String url = getContext().getPagePath(page.getClass());
        String redirectUrl = url + (url.indexOf('?') == -1 ? '?' : '&');
        redirectUrl = redirectUrl + FLASH_COUNT_REQ_PARA +"="+l;
        super.setRedirect(redirectUrl);
    }
    
    private String getFlashSessionDataName(Long l) {
        return FLASH_VALUE_SESS_ATT+l;
    }
    
 
    
    private static class StateField extends HiddenField {
        StateField(Class statePageClass) {
            super("click-state-"+statePageClass.getName(),HashMap.class);
        }
        
        public boolean onProcess() {
            return true;
        }
        
        public void setContext() {
            return;
        }
        
        private void setContextInt(Context context) {
            super.setContext(context);
        }
    }
    
    public class StateForm {
        
        public String getName() {
            return "click-state-form";
        }
        
        public String getId() {
            return getName();
        }
        
        public String toString() {
            HtmlStringBuffer buffer = new HtmlStringBuffer();
            buffer.append("<form method=\"POST\" ");
            buffer.appendAttribute("name", getName());
            buffer.appendAttribute("id", getId());
            
            HttpServletRequest request = getContext().getRequest();
            HttpServletResponse response = getContext().getResponse();
            String actionURL = response.encodeURL(request.getRequestURI());
            buffer.appendAttribute("action", actionURL);
            
            buffer.append(">");
            
            buffer.append(getStateField().toString());

            buffer.append("<input type=\"hidden\" name=\"");
            buffer.append(ActionLink.ACTION_LINK);
            buffer.append("\" value=\"\"/>");
            
            buffer.append("<input type=\"hidden\" name=\"");
            buffer.append(ActionLink.VALUE);
            buffer.append("\" value=\"\"/>");
            
            buffer.append("</form>");
            String ret = buffer.toString();
            return ret;
        }
    }
    
    
    
}
