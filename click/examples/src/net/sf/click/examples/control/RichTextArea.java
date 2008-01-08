package net.sf.click.examples.control;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.click.control.TextArea;

/**
 * Provides a HTML Rich TextArea editor control using the
 * <a href="http://sourceforge.net/projects/tinymce/">TinyMCE</a>
 * JavaScript library.
 * <p/>
 * To utilize this control in your application include <tt>tiny_mce</tt>
 * JavaScript library in the web apps root directory.
 *
 * @see TextArea
 *
 * @author Malcolm Edgar
 */
public class RichTextArea extends TextArea {

    private static final long serialVersionUID = 1L;

    /** The TinyMCE JavaScript import. */
    protected static final String HTML_IMPORTS =
        "<script type=\"text/javascript\" src=\"{0}/tiny_mce/tiny_mce.js\"></script>\n";

    /**
     * The textarea TinyMCE theme [<tt>simple</tt> | <tt>advanced</tt>],
     * default value: &nbsp; <tt>"simple"</tt>
     */
    protected String theme = "simple";

    // ----------------------------------------------------------- Constructors

    /**
     * Create a TinyMCE rich TextArea control with the given name.
     *
     * @param name the name of the control
     */
    public RichTextArea(String name) {
        super(name);
    }

    /**
     * Default no-args constructor used to deploy control resources.
     */
    public RichTextArea() {
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the textarea TinyMCE theme.
     *
     * @return the textarea TinyMCE theme
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Return the JavaScript include: &nbsp; <tt>"tiny_mce/tiny_mce.js"</tt>
     *
     * @see net.sf.click.control.Field#getHtmlImports()
     */
    public String getHtmlImports() {
        String[] args = { getContext().getRequest().getContextPath() };
        return MessageFormat.format(HTML_IMPORTS, args);
    }

    /**
     * This method overrides the TextArea <tt>toString()</tt> method to
     * add TinyMCE JavaScript initialization code.
     *
     * @see TextArea#toString()
     */
    public String toString() {
        Map model = new HashMap();
        model.put("textArea", super.toString());
        model.put("theme", getTheme());
        model.put("id", getId());

        return getContext().renderTemplate(getClass(), model);
    }
}
