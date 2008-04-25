package net.sf.click.extras.control;

import net.sf.click.control.Field;
import net.sf.click.control.TextField;
import net.sf.click.util.ClickUtils;

import javax.servlet.ServletContext;

/**
 * <code>VirtualKeyboard</code> adds a graphical keyboard interface to text fields
 * <i>(later password fields and textareas)</i> so they can be filled with mouse only:<p/>
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2'src='virtual-keyboard.gif' title='Virtual Keyboard'/>
 * </td>
 * </tr>
 * </table>
 *
 * <b>Virtual keyboard interfaces</b> are generally used in websites where the highest
 * level of security is a must like online banking applications.<p/>
 * <b>Virtual keyboards</b> help to prevent any keylogging activies and/or provide
 * users a special keyboard which they don’t already have (like a keyboard of
 * another language).<p/>
 *
 * <b>Note:</b> Comes with Arabic, Belgian, Dutch, Dvorak, French, German,
 * Greek, Hebrew, Hungarian, Italian, Lithuanian, Norwegian, Number Pad,
 * Polish Programmers, Portuguese, Russian, Slovenian, Spanish (Spain),
 * Turkish-F, Turkish-QWERTY, UK, US Standard and US International keyboard layouts,
 * dynamically selectable. <p/>
 *
 * <b>Source:</b> Based on the javascript library from: http://www.greywyvern.com/code/js/keyboard.html <p/>
 *
 *
 * @author Ahmed Mohombe
 * TODO: add support for password fields and textareas too.
 */
public class VirtualKeyboard extends TextField {

    public static final String HTML_IMPORTS =
            "<script type=\"text/javascript\">var keyboard_png_path=\"{0}/click/keyboard.png\";</script>"+
            "<script type=\"text/javascript\" src=\"{0}/click/keyboard.js\" charset=\"UTF-8\"></script>\n" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"{0}/click/keyboard.css\">";
    /**
     * Constructs a new VirtualKeyboard Field object with no name defined.<p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public VirtualKeyboard() {
        super();
        addStyleClass("keyboardInput");
    }

    /**
     * Constructs the VirtualKeyboard Field with the given name.
     *
     * @param name the name of the VirtualKeyboard Field
     */
    public VirtualKeyboard(String name) {
        super(name);
        addStyleClass("keyboardInput");
    }

    /**
     * Constructs the VirtualKeyboard Field with the given name and label.
     *
     * @param name the name of the VirtualKeyboard Field
     * @param label the label of the VirtualKeyboard Field
     */
    public VirtualKeyboard(String name, String label) {
        super(name, label);
        addStyleClass("keyboardInput");
    }

    /**
     * Returns the HTML head import statements for the JavaScript
     * (<tt>click/keyboard.js</tt>) and CSS (<tt>click/keyboard.css</tt>) files.
     *
     * @see net.sf.click.Control#getHtmlImports()
     *
     * @return the HTML head import statements for the JavaScript and CSS files
     */
    public String getHtmlImports() {
        return ClickUtils.createHtmlImport(HTML_IMPORTS, getContext());
    }

    /**
     * Deploy the static resource files in the VirtualKeyboard control.
     *
     * @see net.sf.click.control.Field#onDeploy(javax.servlet.ServletContext)
     * @param servletContext the ServletContext
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFiles(servletContext,
                new String[]{
                        "/net/sf/click/extras/control/keyboard.css",
                        "/net/sf/click/extras/control/keyboard.js",
                        "/net/sf/click/extras/control/keyboard.png"},
                "click");
    }
}
