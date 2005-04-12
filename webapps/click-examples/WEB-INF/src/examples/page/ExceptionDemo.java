package examples.page;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;

/**
 * Provides examples of the Click Exception handling which includes a broken
 * Velocity template and a broken event listener.
 *
 * @author Malcolm Edgar
 */
public class ExceptionDemo extends BorderedPage {

    String template;

    /**
     * @see Page#onInit()
     */
    public void onInit() {
        ActionLink brokenContentLink = new ActionLink("brokenContentLink");
        brokenContentLink.setListener(this, "onBrokenContentClick");
        addControl(brokenContentLink);

        ActionLink brokenTemplateLink = new ActionLink("brokenTemplateLink");
        brokenTemplateLink.setListener(this, "onBrokenTemplateClick");
        addControl(brokenTemplateLink);

        ActionLink exceptionLink = new ActionLink("exceptionLink");
        exceptionLink.setListener(this, "onExceptionClick");
        addControl(exceptionLink);

        ActionLink missingMethodLink = new ActionLink("missingMethodLink");
        missingMethodLink.setListener(this, "missingMethodClick");
        addControl(missingMethodLink);
    }

    public boolean onBrokenContentClick() {
        setPath("broken-content.htm");
        return true;
    }

    public boolean onBrokenTemplateClick() {
        setPath("broken-template.htm");
        template = "broken-template.htm";
        return true;
    }

    public boolean onExceptionClick() {
        throw new NullPointerException("Oh No...");
    }

    /**
     * Override getTemplate so we can stuff things up.
     *
     * @see Page#getTemplate()
     */
    public String getTemplate() {
        return (template != null) ? template : super.getTemplate();
    }

}
