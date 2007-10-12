package net.sf.click.webcode;

import net.sf.click.control.TextArea;
import net.sf.click.util.ClickUtils;

import javax.servlet.ServletContext;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Click Control for doing syntax highlight for source code snippets dispayed in webpages,
 * using the http://code.google.com/p/syntaxhighlighter/ library. 
 */
public class SyntaxHighlight extends TextArea {

    /** The SyntaxHighlight CSS and JavaScript import. */
    protected static final String HTML_IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/syntax_highlight/SyntaxHighlighter.css\"></link> \n"+    
        "<script type=\"text/javascript\" src=\"{0}/click/syntax_highlight/shCore.js\"></script>\n";

    // Cpp.Aliases	    = ['cpp', 'c', 'c++'];
    // CSharp.Aliases	= ['c#', 'c-sharp', 'csharp'];
    // CSS.Aliases	    = ['css'];
    // Delphi.Aliases	= ['delphi', 'pascal'];
    // Java.Aliases	    = ['java'];
    // JScript.Aliases	= ['js', 'jscript', 'javascript'];
    // Php.Aliases	    = ['php'];
    // Python.Aliases   = ['py', 'python'];
    // Ruby.Aliases     = ['ruby', 'rails', 'ror'];
    // Sql.Aliases	    = ['sql'];
    // Vb.Aliases	    = ['vb', 'vb.net'];
    // Xml.Aliases	    = ['xml', 'xhtml', 'xslt', 'html', 'xhtml'];
    public static final String[][] highlighters = {
            {"Cpp",      "cpp","c","c++"},
            {"CSharp",   "c#","c-sharp","csharp"},
            {"CSS",      "css"},
            {"Delphi",   "delphi","pascal"},
            {"Java",     "java"},
            {"JScript",  "js","jscript","javascript"},
            {"Php",      "php"},
            {"Python",   "py","python"},
            {"Ruby",     "ruby","rails","ror"},
            {"Sql",      "sql"},
            {"Vb",       "vb","vb.net"},
            {"Xml",      "xml","xhtml","xslt","html"}
    };
    //-------------------------------------------------------------------------
    // API specific to the JS Library. False or 0 means the property won't be included
    /** Display the right gutter with the line numbering of the source code */
    protected boolean noGutter;
    /** Display the top line with JS <b>controls</b>: ["view plain", "copy to clipboard","print", "?"] */
    protected boolean noControls;
    /** Display the column numbering too at the top of control */
    protected boolean showColumns;
    /** If code is showed collapsed on page load */
    protected boolean collapseCode;
    /** The line number where to start the gutter numbering.*/
    protected int firstLine = 0;
    /** Syntax to use for highlight*/
    protected String syntax = "html";

    /**
     * Default no-args constructor used to deploy control resources.
     */    
    public SyntaxHighlight() {
    }

    /**
     * Create an SyntaxHighlight source code display control with the given name.
     *
     * @param name the name of the control
     */
    public SyntaxHighlight(String name) {
        super(name);
    }

    public void setNoGutter(boolean noGutter) {
        this.noGutter = noGutter;
    }

    public void setNoControls(boolean noControls) {
        this.noControls = noControls;
    }

    public void setShowColumns(boolean showColumns) {
        this.showColumns = showColumns;
    }

    public void setCollapseCode(boolean collapseCode) {
        this.collapseCode = collapseCode;
    }

    public void setFirstLine(int firstLine) {
        this.firstLine = firstLine;
    }

    /**
     * Sets the syntax to be used for highlight. Use the right alias for the lighlighting:
     * <pre>
     *   // Cpp.Aliases	      = ['cpp', 'c', 'c++'];
     *   // CSharp.Aliases	  = ['c#', 'c-sharp', 'csharp'];
     *   // CSS.Aliases	      = ['css'];
     *   // Delphi.Aliases	  = ['delphi', 'pascal'];
     *   // Java.Aliases	  = ['java'];
     *   // JScript.Aliases	  = ['js', 'jscript', 'javascript'];
     *   // Php.Aliases	      = ['php'];
     *   // Python.Aliases    = ['py', 'python'];
     *   // Ruby.Aliases      = ['ruby', 'rails', 'ror'];
     *   // Sql.Aliases	      = ['sql'];
     *   // Vb.Aliases	      = ['vb', 'vb.net'];
     *   // Xml.Aliases	      = ['xml', 'xhtml', 'xslt', 'html', 'xhtml'];
     * </pre>
     * 
     * @param syntax an alias for the syntax to be applied in this control
     */
    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    /**
     * Return the CSS and JavaScript includes: &nbsp; <tt>"click/syntax_highlight/SyntaxHighlighter.css"</tt>
     * and <tt>"click/syntax_highlight/shCore.js"</tt>
     *
     * @see net.sf.click.control.Field#getHtmlImports()
     */
    public String getHtmlImports() {
        String[] args = { getContext().getRequest().getContextPath() };
        return MessageFormat.format(HTML_IMPORTS, args);
    }

    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFileList(servletContext,SyntaxHighlight.class,"click");
    }

    /**
     * This method overrides the TextArea <tt>toString()</tt> method to
     * add SyntaxHighlight JavaScript initialization code.
     *
     * @see TextArea#toString()
     */    
    public String toString() {

        // this JS lib is using most of it's options in form of a class selector list
        String classAttr = syntax;
        classAttr += noControls     ? ":nocontrols" : "";
        classAttr += noGutter       ? ":nogutter" : "";
        classAttr += showColumns    ? ":showcolumns" : "";
        classAttr += collapseCode   ? ":collapse" : "";
        classAttr += firstLine != 0 ? ":firstline[" + firstLine + "]" : "";
        this.setAttribute("class",classAttr);

        Map model = new HashMap();
        model.put("textArea", super.toString());
        model.put("name", getName()); // this JS lib uses "name" not "id" to identify what to highlight.
        model.put("syntaxUrl",getContext().getRequest().getContextPath()+"/click/syntax_highlight/shBrush"+getHighlighter(syntax)+".js");
        model.put("swfUrl",getContext().getRequest().getContextPath()+"/click/syntax_highlight/clipboard.swf"); // swf trick for working copy and paste

        return getContext().renderTemplate(getClass(), model);
    }

    /**
     * Searches the <code>highlighter</code> data structure for a match.
     * 
     * @param alias the alias name for the desired language. See javadoc for allowed.
     * @return the highlighter for the specified <code>alias</code> 
     */
    private String getHighlighter(String alias){
        boolean found = false;
        for (int i = 0; i < highlighters.length; i++) {
            String[] highlighter = highlighters[i];

            // the first column is the searched highlighter (the rest are aliases) 
            for (int j = 1; j < highlighter.length; j++) {
                String s = highlighter[j];
                if(s.equals(alias)) {
                    found = true;
                    break;
                }
            }
            if(found) {
                return highlighter[0];
            }
        }
        return null;
    }
}
