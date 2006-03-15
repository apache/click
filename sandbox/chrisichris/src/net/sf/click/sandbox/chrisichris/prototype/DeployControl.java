package net.sf.click.sandbox.chrisichris.prototype;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Control which only has the purpose to deploy the prototype and scriptacoulous JS files. Additional 
 * there are some helper methods. The control itself can not be used as a normal control because
 * it does not implement any of the normal control functionality.
 * @author chris
 *
 */
public class DeployControl implements Control{


    
    public DeployControl() {
        super();
    }
    

    /** retunrs always null 
     * @see net.sf.click.Control#getContext()
     */
    public Context getContext() {
        throw new UnsupportedOperationException();
    }

    /** has no effect
     * @see net.sf.click.Control#setContext(net.sf.click.Context)
     */
    public void setContext(Context context) {
        throw new UnsupportedOperationException();
    }

    /** returns the prototype and scriptacoulus imports
     * @see net.sf.click.Control#getHtmlImports()
     */
    public String getHtmlImports() {
        throw new UnsupportedOperationException();
    }

    /** return null
     * @see net.sf.click.Control#getId()
     */
    public String getId() {
        throw new UnsupportedOperationException();
    }

    /** No effect
     * @see net.sf.click.Control#setListener(java.lang.Object, java.lang.String)
     */
    public void setListener(Object listener, String method) {
        throw new UnsupportedOperationException();
    }

    /** returns null
     * @see net.sf.click.Control#getName()
     */
    public String getName() {
        throw new UnsupportedOperationException();
    }

    /** No effect
     * @see net.sf.click.Control#setName(java.lang.String)
     */
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    /** retuns null
     * @see net.sf.click.Control#getParentMessages()
     */
    public Map getParentMessages() {
        throw new UnsupportedOperationException();
    }

    /** no effect
     * @see net.sf.click.Control#setParentMessages(java.util.Map)
     */
    public void setParentMessages(Map messages) {
        throw new UnsupportedOperationException();
    }

    /** returns true
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        throw new UnsupportedOperationException();
    }
    
    
    /** copies the scriptacoulus  and prototype files
     * @see net.sf.click.Control#onDeploy(javax.servlet.ServletContext)
     */
    public void onDeploy(ServletContext servletContext) throws IOException {
        //prototype
        ClickUtils.deployFile(servletContext, "/net/sf/click/sandbox/christian/prototype/prototype.js", "click/prototype");

        //spinner image
        ClickUtils.deployFile(servletContext, "/net/sf/click/sandbox/christian/prototype/progress-bar.gif", "click/prototype");
        ClickUtils.deployFile(servletContext, "/net/sf/click/sandbox/christian/prototype/progress-spinner.gif", "click/prototype");

        //Scriptaculous
        ClickUtils.deployFile(servletContext, "/net/sf/click/sandbox/christian/prototype/scriptaculous.js", "click/scriptaculous");
        ClickUtils.deployFile(servletContext, "/net/sf/click/sandbox/christian/prototype/builder.js", "click/scriptaculous");
        ClickUtils.deployFile(servletContext, "/net/sf/click/sandbox/christian/prototype/effects.js", "click/scriptaculous");
        ClickUtils.deployFile(servletContext, "/net/sf/click/sandbox/christian/prototype/slider.js", "click/scriptaculous");
        ClickUtils.deployFile(servletContext, "/net/sf/click/sandbox/christian/prototype/controls.js", "click/scriptaculous");
        ClickUtils.deployFile(servletContext, "/net/sf/click/sandbox/christian/prototype/dragdrop.js", "click/scriptaculous");
        //autocomplete css
        ClickUtils.deployFile(servletContext, "/net/sf/click/sandbox/christian/prototype/autocomplete.css", "click/scriptaculous");
    }
    
    public static String getPrototypeImport(Context ctxt){
        String contextPath = ctxt.getRequest().getContextPath();
        String ret = "<script type=\"text/javascript\" src=\"";
        ret = ret + contextPath;
        ret = ret + "/click/prototype/prototype.js\"></script>\n";
        return ret;
    }
    
    public static String getScriptaculousImport(Context ctxt) {
        String contextPath = ctxt.getRequest().getContextPath();
        StringBuffer stb = new StringBuffer();
        appendScriptImport(stb, contextPath, "scriptaculous/scriptaculous.js");
        appendScriptImport(stb, contextPath, "scriptaculous/builder.js");
        appendScriptImport(stb, contextPath, "scriptaculous/effects.js");
        appendScriptImport(stb, contextPath, "scriptaculous/slider.js");
        appendScriptImport(stb, contextPath, "scriptaculous/controls.js");
        appendScriptImport(stb, contextPath, "scriptaculous/dragdrop.js");
        //the autocomplete css
        stb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
        stb.append(contextPath);
        stb.append("/click/scriptaculous/autocomplete.css\"/>\n");
        
        return stb.toString();
    }
    
    private static void appendScriptImport(StringBuffer stB, String contextPath, String scriptName) {
        stB.append("<script type=\"text/javascript\" src=\"");
        stB.append(contextPath);
        stB.append("/click/");
        stB.append(scriptName);
        stB.append("\"></script>\n");
    }
    
    
    

}
