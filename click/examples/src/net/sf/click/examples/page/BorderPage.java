package net.sf.click.examples.page;

import net.sf.click.extras.control.Menu;
import net.sf.click.util.ClickUtils;

/**
 * Provides a page border template. This Page returns the template
 * <tt>"border-template.htm"</tt>, and sets the Page model values <tt>$title</tt> and
 * <tt>$srcPath</tt>.
 * <p/>
 * Please note this page is designed for extending by Page subclasses and will
 * not be auto mapped as the template name <tt>"border-template.htm"</tt> does
 * not match the Pages class name <tt>BorderPage</tt>.
 *
 * @author Malcolm Edgar
 */
public class BorderPage extends SpringPage {

    /**
     * The root menu. Note this variable is initialized in onInit() to support
     * serialized stateful pages.
     */
    public transient Menu rootMenu;

    // ------------------------------------------------------------ Constructor

    /**
     * Create a BorderedPage and set the model attributes <tt>$title</tt> and
     * <tt>$srcPath</tt>.
     * <ul>
     * <li><tt>$title</tt> &nbsp; - &nbsp; the Page title from classname</li>
     * <li><tt>$srcPath</tt> &nbsp; - &nbsp; the Page Java source path</li>
     * </ul>
     */
    public BorderPage() {
        String className = getClass().getName();

        String shortName = className.substring(className.lastIndexOf('.') + 1);
        String title = ClickUtils.toLabel(shortName);
        addModel("title", title);

        String srcPath = className.replace('.', '/') + ".java";
        addModel("srcPath", srcPath);
    }

    // --------------------------------------------------------- Event Handlers

    public void onInit() {
        super.onInit();

        rootMenu = Menu.getRootMenu();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.htm"</tt>
     * <p/>
     * Please note this page is designed for extending by Page subclasses and will
     * not be auto mapped as the template name <tt>"border-template.htm"</tt> does
     * not match the Pages class name <tt>BorderPage</tt>.
     *
     * @see Page#getTemplate()
     */
    public String getTemplate() {
        return "/border-template.htm";
    }

    // ------------------------------------------------------ Protected Methods

    protected Object getSessionObject(Class aClass) {
        if (aClass == null) {
            throw new IllegalArgumentException("Null class parameter.");
        }
        Object object = getContext().getSessionAttribute(aClass.getName());
        if (object == null) {
            try {
                object = aClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return object;
    }

    protected void setSessionObject(Object object) {
        if (object != null) {
            getContext().setSessionAttribute(object.getClass().getName(), object);
        }
    }

    protected void removeSessionObject(Class aClass) {
        if (getContext().hasSession() && aClass != null) {
            getContext().getSession().removeAttribute(aClass.getName());
        }
    }

}
