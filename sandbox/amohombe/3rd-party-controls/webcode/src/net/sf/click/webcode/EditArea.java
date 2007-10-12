package net.sf.click.webcode;

import net.sf.click.control.Field;
import net.sf.click.control.TextArea;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.ControlUtils;

import javax.servlet.ServletContext;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Click Control representing an <i>EditArea</i> - the  free javascript editor for source code.
 * It allows to write well formated source code. That's no way a WYSIWYG editor.
 * 
 */
public class EditArea extends TextArea {

    /** The EditArea JavaScript import. */
    protected static final String HTML_IMPORTS =
        "<script type=\"text/javascript\" src=\"{0}/click/edit_area/edit_area_full.js\"></script>\n";

    //-------------------------------------------------------------------------
    // API specific to the JS Library. If default is null than the defaults of the JS lib are taken.   
    /** Code of the syntax definition file that must be used for the highlight mode */
    protected String syntax = "";
    /** Set if the editor should start with highlighted syntax displayed */
    protected Boolean startHighlight = Boolean.TRUE;
    /** Define one with axis the editor can be resized by the user.
     * String ("no" (no resize allowed), "both" (x and y axis), "x", "y"). Default: "both". */
    protected String allowResize;
    /** Comma separated list of plugins to load. */
    protected String plugins;

    
    /**
     * Default no-args constructor used to deploy control resources.
     */
    public EditArea() {
    }

    /**
     * Create an EditArea source code editing control with the given name.
     *
     * @param name the name of the control
     */
    public EditArea(String name) {
        super(name);
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public Boolean getStartHighlight() {
        return startHighlight;
    }

    public void setStartHighlight(Boolean startHighlight) {
        this.startHighlight = startHighlight;
    }

    /**
     * Return the JavaScript include: &nbsp; <tt>"click/edit_area/edit_area_full.js"</tt>
     *
     * @see Field#getHtmlImports()
     */
    public String getHtmlImports() {
        String[] args = { getContext().getRequest().getContextPath() };
        return MessageFormat.format(HTML_IMPORTS, args);
    }

    public void onDeploy(ServletContext servletContext) {
        ControlUtils.deployFileList(servletContext,EditArea.class,"click");
    }

    /**
     * This method overrides the TextArea <tt>toString()</tt> method to
     * add EditArea JavaScript initialization code.
     *
     * @see TextArea#toString()
     */
    public String toString() {
        Map model = new HashMap();
        model.put("textArea", super.toString());
        model.put("id", getId());
        model.put("syntax",getSyntax());
        model.put("start_highlight", getStartHighlight().toString());
        
        return getContext().renderTemplate(getClass(), model);
    }
}
