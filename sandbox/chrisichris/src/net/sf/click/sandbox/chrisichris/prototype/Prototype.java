package net.sf.click.sandbox.chrisichris.prototype;

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
public class Prototype implements Cloneable {

    /**
     * Singelton INSTANCE which is safe to use,
     * because this class has no state.
     */
    public static final Prototype INSTANCE = new Prototype();

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
     * Default Contstructor.
     */
    public Prototype() {
        super();
    }

    /**
     * Wraps the given JS content in a script tag.
     * @param content JS
     * @return html script tag
     */
    public String javascriptTag(String content) {
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
    public String updateElementJS(String elementId, String content,
            String position) {
        if (elementId == null) {
            throw new NullPointerException("param elementId");
        }

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

    /**
     * Returns JS which makes the html element with the given id empty. Can be
     * ie used in PrototypeAjax.onComplete
     *
     * @param elementId html-id of the element to clear
     * @return JS
     */
    public String clearElementJS(String elementId) {
        if (elementId == null) {
            throw new NullPointerException("param elementId");
        }
        return "$('" + elementId + "').innerHTML = '';\n";
    }

    /**
     * Returns JS which remove the html element with the given id. Can be ie
     * used in PrototypeAjax.onComplete
     *
     * @param elementId html-id of the element to clear
     * @return JS
     */
    public String removeElementJS(String elementId) {
        if (elementId == null) {
            throw new NullPointerException("param elementId");
        }
        return "Element.remove('" + elementId + "');\n";
    }

    /**
     * Return the given in parameter + ";return false;".
     *
     * @param js
     *            to execute
     * @return the js parameter + ";return false;"
     */
    public String onClick(String js) {
        return js + "; return false;";
    }

}
