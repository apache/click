package net.sf.click.examples.page;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;

/**
 * Provides examples of the Click Exception handling.
 *
 * @author Malcolm Edgar
 */
public class ExceptionDemo extends BorderPage {

    private String template;

    public ExceptionDemo() {
        ActionLink nullPointerLink = new ActionLink("nullPointerLink");
        nullPointerLink.setListener(this, "onNullPointerClick");
        addControl(nullPointerLink);

        ActionLink missingMethodLink = new ActionLink("missingMethodLink");
        missingMethodLink.setListener(this, "onMissingMethodClick");
        addControl(missingMethodLink);

        ActionLink brokenRendererLink = new ActionLink("brokenRendererLink");
        brokenRendererLink.setListener(this, "onBrokenRendererClick");
        addControl(brokenRendererLink);

        ActionLink brokenBorderLink = new ActionLink("brokenBorderLink");
        brokenBorderLink.setListener(this, "onBrokenBorderClick");
        addControl(brokenBorderLink);

        ActionLink brokenContentLink = new ActionLink("brokenContentLink");
        brokenContentLink.setListener(this, "onBrokenContentClick");
        addControl(brokenContentLink);
    }

    public boolean onNullPointerClick() {
        Object object = null;
        object.hashCode();
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
