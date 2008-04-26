/*
 * Copyright 2004-2008 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.extras.control;

import javax.servlet.ServletContext;

import net.sf.click.control.TextField;
import net.sf.click.util.ClickUtils;

/**
 * Provides a graphical Vitural Keyboard interface text field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2'src='virtual-keyboard.gif' title='Virtual Keyboard'/>
 * </td>
 * </tr>
 * </table>
 *
 * VirtualKeyboard enables text fields to be filled in using a mouse only. Password
 * and textarea fields will be supported in later releases.
 * <p/>
 * <b>Virtual keyboard interfaces</b> are generally used in websites where the highest
 * level of security is a must like online banking applications.
 * Virtual keyboards help to prevent any keylogging activies and/or provide
 * users a special keyboard which they don’t already have (like a keyboard of
 * another language).
 *
 * <h3>Keyboard Layout Support</h3>
 * This controls comes with support for Arabic, Belgian, Dutch, Dvorak, French, German,
 * Greek, Hebrew, Hungarian, Italian, Lithuanian, Norwegian, Number Pad,
 * Polish Programmers, Portuguese, Russian, Slovenian, Spanish (Spain),
 * Turkish-F, Turkish-QWERTY, UK, US Standard and US International keyboard layouts,
 * dynamically selectable.
 *
 * <h4>Credits</h4>
 * This control based on the <a href="http://www.greywyvern.com/code/js/keyboard.html">Greywyvern</a> JavaScript library.
 *
 * @author Ahmed Mohombe
 *
 * @todo: add support for password fields and textareas too.
 */
public class VirtualKeyboard extends TextField {

    private static final long serialVersionUID = 1L;

     /** The HTML import statements. */
    public static final String HTML_IMPORTS =
          "<script type=\"text/javascript\">var keyboard_png_path=\"{0}/click/keyboard{1}.png\";</script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/keyboard{1}.js\" charset=\"UTF-8\"></script>\n"
        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"{0}/click/keyboard{1}.css\"/>\n";

    // ----------------------------------------------------------- Constructors

    /**
     * Constructs a new VirtualKeyboard Field object with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public VirtualKeyboard() {
        super();
        addStyleClass("keyboardInput");
    }

    /**
     * Constructs the VirtualKeyboard Field with the given name.
     *
     * @param name the name of the VirtualKeyboard field
     */
    public VirtualKeyboard(String name) {
        super(name);
        addStyleClass("keyboardInput");
    }

    /**
     * Constructs the VirtualKeyboard Field with the given name and label.
     *
     * @param name the name of the VirtualKeyboard field
     * @param label the label of the VirtualKeyboard field
     */
    public VirtualKeyboard(String name, String label) {
        super(name, label);
        addStyleClass("keyboardInput");
    }

    // --------------------------------------------------------- Public Methods

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
     *
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
