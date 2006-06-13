package net.sf.click.sandbox.chrisichris.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.Format;
import net.sf.click.util.SessionMap;

public class ControlUtils {
    public static String getId(Control ctrl) {
        Object parent = ctrl.getParent();
        if (ctrl.getParent() != null) {
            String id;
            if (parent instanceof Page) {
                id = ctrl.getName();
            } else {
                String parentId = ((Control) parent).getId();
                if (parentId == null) {
                    return null;
                }
                id = ((Control) parent).getId() + "_" + ctrl.getName();
            }
            return id;
        } else {
            return null;
        }
        
    }
    
    public static void populateModelMap(Map model,Page page, Map messages) {
        if (page == null) {
            throw new IllegalStateException("Not attached to a page yet");
        }

        final HttpServletRequest request = page.getContext().getRequest();

        model.put("request", request);

        model.put("response", page.getContext().getResponse());

        SessionMap sessionMap = new SessionMap(request.getSession(false));
        model.put("session", sessionMap);

        model.put("context", request.getContextPath());

        Format format = page.getFormat();
        if (format != null) {
            model.put("format", format);
        }

        if(messages == null) {
            messages = page.getMessages();
        }
        model.put("messages", messages);
    }
    
    public static boolean isValidName(String name) {
        if(StringUtils.isBlank(name)) {
            return false;
        }
        if(!StringUtils.containsNone(name,"_ \t\n\r")) {
            return false;
        }
        return true;
        
    }
    
}
