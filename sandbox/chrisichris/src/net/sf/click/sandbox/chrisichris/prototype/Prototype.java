package net.sf.click.sandbox.chrisichris.prototype;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Helper which creates commonly used JS code for prototype. This class has no
 * state. Because of velocity the mothods are not static but you can use
 * Prototype.INSTANCE to get a Singleton.
 *
 * @author chris
 *
 */
public class Prototype  {


    /**
     * Insert AJAX response before the element.
     */
    public static final String INSERTION_BEFORE = "Insertion.Before";

    /**
     * Insert AJAX response at the beginning of the
     * element content.
     */
    public static final String INSERTION_TOP = "Insertion.Top";

    /**
     * Insert AJAX response at the end of the element
     * content.
     */
    public static final String INSERTION_BOTTOM = "Insertion.Bottom";

    /**
     * Insert the AJAX response after the element.
     */
    public static final String INSERTION_AFTER = "Insertion.After";


    /**
     * Wraps the given JS content in a script tag.
     * @param content JS
     * @return html script tag
     */
    static public String javascriptTag(String content) {
        if (content == null) {
            content = "";
        }
        HtmlStringBuffer stB = new HtmlStringBuffer(50 + content.length());
        stB.elementStart("script");
        stB.appendAttribute("type", "text/javascript");
        stB.appendAttribute("language", "javascript");
        stB.closeTag();

        // use cdata for xhtml and hide it from jscript through jscript comments
        stB.append("\n// <![CDATA{ \n");
        stB.append(content);
        stB.append("\n// ]]>\n");
        stB.elementEnd("script");
        return stB.toString();
    }

    /**
     * Creates JS which updateds the given html-element with the given
     * content.
     * @param elementId Html-Element-id to update
     * @param content xhtml content to update with
     * @param position one of the the INSERTION constants or null to replace
     * the current content.
     * @return JS
     */
    static public String updateElementJS(String elementId, String content,
            String position) {
        if (elementId == null) {
            throw new NullPointerException("param elementId");
        }
        elementId = escapeJS(elementId);

        if (content == null) {
            content = "";
        } else {
            content = StringEscapeUtils.escapeJavaScript(content);
        }

        final String ret;
        if (position != null) {
            ret = "new Insertion." + StringUtils.capitalize(position) + "('"
                    + elementId + "','" + content + "');\n";
        } else {
            ret = "$('" + elementId + ").innerHTML = '" + content + "';\n";
        }

        return ret;
    }
    
    static public String insertTop(String elementId,String content) {
        return updateElementJS(elementId,content,INSERTION_TOP);
    }

    static public String insertBefore(String elementId,String content) {
        return updateElementJS(elementId,content,INSERTION_BEFORE);
    }
    
    static public String insertBottom(String elementId,String content) {
        return updateElementJS(elementId,content,INSERTION_BOTTOM);
    }

    static public String insertAfter(String elementId,String content) {
        return updateElementJS(elementId,content,INSERTION_AFTER);
    }
    
    static public String innerHtml(String elementId, String content) {
        return updateElementJS(elementId,content,null);
    }
    
    static public String clearElement(String elementId) {
        return innerHtml(elementId,null);
    }
    
    static public String setAttribute(String elementId, String attribute, String atValue) {
        return "${'"+escapeJS(elementId)+"'}['"+escapeJS(attribute)+"']='"+escapeJS(atValue)+"';";
    }
    
    static public String escapeJS(String string) {
       return StringEscapeUtils.escapeJavaScript(string);
    }
    
    static public String effectFade(String elementId) {
        return "Effect.Fade($('"+escapeJS(elementId)+"'), {duration : 0.15});";
    }
    
    static public String effectAppear(String elementId) {
        return "Effect.Appear($('"+escapeJS(elementId)+"'), {duration : 0.15});";
    }

 
    /**
     * Returns JS which remove the html element with the given id. Can be ie
     * used in PrototypeAjax.onComplete
     *
     * @param elementId html-id of the element to clear
     * @return JS
     */
    static public String removeElementJS(String elementId) {
        if (elementId == null) {
            throw new NullPointerException("param elementId");
        }
        return "Element.remove('" + elementId + "');\n";
    }

    static public boolean isAjax(Context context) {
        if(context.isMultipartRequest()) {
            return false;
        }
        
        String ajaR = context.getRequestParameter("_clickAjax");
        if("true".equalsIgnoreCase(ajaR)) {
            return true;
        } else {
            return false;
        }
    }
    
    static public void respondeDirectly(Page page, String content, String contentType) {
        page.setRedirect((String)null);
        page.setForward((String)null);
        page.setPath(null);
        
        HttpServletResponse res = page.getContext().getResponse();
        setNoCacheHeaders(res);
        
        //contenttype
        if(contentType.indexOf("charset") == -1) {
            String charset = page.getContext().getRequest().getCharacterEncoding();
            if (charset != null) {
                contentType = "text/javascript; charset=" + charset;
            }
        }
        res.setContentType(contentType);
        
        
        
        try {
            res.getWriter().write(content);
        } catch (IOException e) {
            res.setStatus(201);
            RuntimeException ex = new RuntimeException("Error wirting ajax response: "+e);
            ex.initCause(e);
            throw ex;
        }finally {
            try {
                res.getWriter().close();
            } catch (IOException e) {
                res.setStatus(201);
                throw new RuntimeException("Could not close response");
            }
        }
    }
    
    public static void respondeDirectlyJS(Page page, String javaScript) {
        respondeDirectly(page,javaScript,"text/javascript");
    }
    
    public static Page getPage(Control ctrl) {
        Object parent = ctrl.getParent();
        while (parent != null) {
            if(parent instanceof Page) {
                return (Page) parent;
            }
            if (parent instanceof Control) {
                parent = ((Control)parent).getParent();
            }
            return null;
        }
        return null;
    }
    
    private static void setNoCacheHeaders(HttpServletResponse res) {
        res.setHeader("Pragma","no-cache");
        res.setHeader("Cache-Control","no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
        res.setDateHeader("Expires",1);
    }

}
