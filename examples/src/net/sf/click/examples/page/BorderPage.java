package net.sf.click.examples.page;

import net.sf.click.Page;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a page border template. This Page returns the template
 * <tt>"border.htm"</tt>, and sets the Page model values <tt>$title</tt> and
 * <tt>$srcPath</tt>.
 *
 * @author Malcolm Edgar
 */
public class BorderPage extends SpringPage {
	
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
        HtmlStringBuffer title = new HtmlStringBuffer();
        title.append(shortName.charAt(0));
        for (int i = 1; i < shortName.length(); i++) {
            char aChar = shortName.charAt(i);
            if (Character.isUpperCase(aChar)) {
                title.append(' ');
            }
            title.append(aChar);
        }
        addModel("title", title);

        String srcPath = className.replace('.', '/') + ".java";
        addModel("srcPath", srcPath);
    }
    
    // --------------------------------------------------------- Public Methods

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.htm"</tt>
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
