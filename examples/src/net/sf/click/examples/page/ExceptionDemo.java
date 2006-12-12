package net.sf.click.examples.page;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;

/**
 * Provides examples of the Click Exception handling.
 *
 * @author Malcolm Edgar
 */
public class ExceptionDemo extends BorderPage {

    public ActionLink nullPointerLink = new ActionLink(this, "onNullPointerClick");
    public ActionLink illegalArgumentLink = new ActionLink(this, "onIllegalArgumentExceptionClick");
    public ActionLink missingMethodLink = new ActionLink(this, "onMissingMethodClick");
    public ActionLink brokenRendererLink = new ActionLink(this, "onBrokenRendererClick");
    public ActionLink brokenBorderLink = new ActionLink(this, "onBrokenBorderClick");
    public ActionLink brokenContentLink = new ActionLink(this, "onBrokenContentClick");

    private String template;

    public boolean onNullPointerClick() {
        Object object = null;
        object.hashCode();
        return true;
    }

    public boolean onIllegalArgumentExceptionClick() {
        addModel("param-1", "First Parameter");
        addModel("param-1", "Second Parameter");
        return true;
    }

    public boolean onBrokenRendererClick() {
        addModel("brokenRenderer", new BrokenRenderer());
        return true;
    }

    public boolean onBrokenBorderClick() {
        setPath("broken-border.htm");
        template = "broken-border.htm";
        return true;
    }

    public boolean onBrokenContentClick() {
        setPath("broken-content.htm");
        return true;
    }

    /**
     * Override getTemplate so we can stuff things up.
     *
     * @see Page#getTemplate()
     */
    public String getTemplate() {
        return (template != null) ? template : super.getTemplate();
    }

    /**
     * Provides a rendering ojbect which will throw a NPE when merged by
     * velocity in the template.
     *
     * @author Malcolm Edgar
     */
    public static class BrokenRenderer {

        /**
         * Guaranteed to fail, or you money back.
         *
         * @see Object#toString()
         */
        public String toString() {
            Object object = null;
            return object.toString();
        }
    }

}
