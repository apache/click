package net.sf.click.sandbox.chrisichris.prototype;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;

/**
 * Control which only has the purpose to deploy the prototype and scriptacoulous
 * JS files. Additional there are some helper methods. The control itself can
 * not be used as a normal control because it does not implement any of the
 * normal control functionality.
 *
 * @author Christian Essl
 *
 */
public class DeployControl implements Control {

    /**
     * Default constructor.
     *
     */
    public DeployControl() {
        super();
    }

    /**
     * Throws unsupported op exception.
     *
     * @see net.sf.click.Control#getContext()
     */
    public Context getContext() {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws unsupported op exception.
     * 
     * @see net.sf.click.Control#setContext(net.sf.click.Context)
     */
    public void setContext(Context context) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws unsupported op exception.
     *
     *
     * @see net.sf.click.Control#getHtmlImports()
     */
    public String getHtmlImports() {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws unsupported op exception.
     *
     * @see net.sf.click.Control#getId()
     */
    public String getId() {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws unsupported op exception.
     *
     * @see net.sf.click.Control#setListener(java.lang.Object, java.lang.String)
     */
    public void setListener(Object listener, String method) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws unsupported op exception.
     *
     * @see net.sf.click.Control#getName()
     */
    public String getName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws unsupported op exception.
     *
     * @see net.sf.click.Control#setName(java.lang.String)
     */
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws unsupported op exception.
     *
     * @see net.sf.click.Control#getParentMessages()
     */
    public Map getParentMessages() {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws unsupported op exception.
     *
     * @see net.sf.click.Control#setParentMessages(java.util.Map)
     */
    public void setParentMessages(Map messages) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws unsupported op exception.
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        throw new UnsupportedOperationException();
    }

    /**
     * copies the scriptacoulus and prototype files.
     *
     * @param servletContext the serlvetContext
     * @throws IOException in case something went wrong
     *
     * @see net.sf.click.Control#onDeploy(javax.servlet.ServletContext)
     */
    public void onDeploy(ServletContext servletContext) throws IOException {
        // prototype
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/sandbox/christian/prototype/prototype.js",
                "click/prototype");

        // spinner image
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/sandbox/christian/prototype/progress-bar.gif",
                "click/prototype");
        ClickUtils
                .deployFile(
                        servletContext,
                        "/net/sf/click/sandbox/christian/"
                        + "prototype/progress-spinner.gif",
                        "click/prototype");

        // Scriptaculous
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/sandbox/christian/prototype/scriptaculous.js",
                "click/scriptaculous");
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/sandbox/christian/prototype/builder.js",
                "click/scriptaculous");
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/sandbox/christian/prototype/effects.js",
                "click/scriptaculous");
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/sandbox/christian/prototype/slider.js",
                "click/scriptaculous");
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/sandbox/christian/prototype/controls.js",
                "click/scriptaculous");
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/sandbox/christian/prototype/dragdrop.js",
                "click/scriptaculous");
        // autocomplete css
        ClickUtils.deployFile(servletContext,
                "/net/sf/click/sandbox/christian/prototype/autocomplete.css",
                "click/scriptaculous");
    }

    /**
     * The script tag for prototype import.
     * @param ctxt current context
     * @return script tag
     */
    public static String getPrototypeImport(Context ctxt) {
        String contextPath = ctxt.getRequest().getContextPath();
        String ret = "<script type=\"text/javascript\" src=\"";
        ret = ret + contextPath;
        ret = ret + "/click/prototype/prototype.js\"></script>\n";
        return ret;
    }

    /**
     * The script tags for scriptaculous imports and the
     * css link for the autocomplete div.
     * @param ctxt the current context
     * @return script tags
     */
    public static String getScriptaculousImport(Context ctxt) {
        String contextPath = ctxt.getRequest().getContextPath();
        StringBuffer stb = new StringBuffer();
        appendScriptImport(stb, contextPath, "scriptaculous/scriptaculous.js");
        appendScriptImport(stb, contextPath, "scriptaculous/builder.js");
        appendScriptImport(stb, contextPath, "scriptaculous/effects.js");
        appendScriptImport(stb, contextPath, "scriptaculous/slider.js");
        appendScriptImport(stb, contextPath, "scriptaculous/controls.js");
        appendScriptImport(stb, contextPath, "scriptaculous/dragdrop.js");
        // the autocomplete css
        stb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
        stb.append(contextPath);
        stb.append("/click/scriptaculous/autocomplete.css\"/>\n");

        return stb.toString();
    }

    /**
     * Helper to make a script tag.
     * @param stB
     * @param contextPath
     * @param scriptName
     */
    private static void appendScriptImport(StringBuffer stB,
            String contextPath, String scriptName) {
        stB.append("<script type=\"text/javascript\" src=\"");
        stB.append(contextPath);
        stB.append("/click/");
        stB.append(scriptName);
        stB.append("\"></script>\n");
    }

}
