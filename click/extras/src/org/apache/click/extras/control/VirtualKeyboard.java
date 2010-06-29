/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.control;

import java.util.List;
import org.apache.click.Context;
import org.apache.click.control.TextField;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a graphical Virtual Keyboard interface text field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2' src='virtual-keyboard.gif' title='Virtual Keyboard'/>
 * </td>
 * </tr>
 * </table>
 *
 * VirtualKeyboard enables text fields to be filled in using a mouse only. Password
 * and textarea fields will be supported in later releases.
 * <p/>
 * <b>Virtual keyboard interfaces</b> are generally used in websites where the highest
 * level of security is a must like online banking applications.
 * Virtual keyboards help to prevent any keylogging activities and/or provide
 * users a special keyboard which they don't already have (like a keyboard of
 * another language).
 *
 * <h3>Keyboard Layout Support</h3>
 * This controls comes with support for Arabic, Belgian, Dutch, Dvorak, French, German,
 * Greek, Hebrew, Hungarian, Italian, Lithuanian, Norwegian, Number Pad,
 * Polish Programmers, Portuguese, Russian, Slovenian, Spanish (Spain),
 * Turkish-F, Turkish-QWERTY, UK, US Standard and US International keyboard layouts,
 * dynamically selectable.
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * The VirtualKeyboard control makes use of the following resources
 * (which Click automatically deploys to the application directory, <tt>/click</tt>):
 *
 * <ul>
 * <li><tt>click/keyboard.css</tt></li>
 * <li><tt>click/keyboard.js</tt></li>
 * <li><tt>click/keyboard.png</tt></li>
 * </ul>
 *
 * To import these VirtualKeyboard files simply reference the variables
 * <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> in the page template.
 *
 * <h4>Credits</h4>
 * This control based on the <a href="http://www.greywyvern.com/code/js/keyboard.html">Greywyvern</a> JavaScript library.
 */
public class VirtualKeyboard extends TextField {

    private static final long serialVersionUID = 1L;

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
     * Return the VirtualKeyboard HTML HEAD elements for the following
     * resources:
     * <p/>
     * <ul>
     * <li><tt>click/keyboard.css</tt></li>
     * <li><tt>click/keyboard.js</tt></li>
     * <li><tt>click/keyboard.png</tt></li>
     * </ul>
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML HEAD elements for the control
     */
    @Override
    public List<Element> getHeadElements() {
        Context context = getContext();
        String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

        if (headElements == null) {
            headElements = super.getHeadElements();

            JsImport jsImport = new JsImport("/click/keyboard.js", versionIndicator);
            jsImport.setAttribute("charset", "UTF-8");
            headElements.add(jsImport);
            headElements.add(new CssImport("/click/keyboard.css", versionIndicator));
        }

        String fieldId = getId();
        JsScript script = new JsScript();
        script.setId(fieldId + "-js-setup");

        if (!headElements.contains(script)) {
            HtmlStringBuffer buffer = new HtmlStringBuffer(150);
            buffer.append("var keyboard_png_path=\"");
            buffer.append(context.getRequest().getContextPath());
            buffer.append("/click/keyboard");
            buffer.append(versionIndicator);
            buffer.append(".png\"");
            script.setContent(buffer.toString());
            headElements.add(script);
        }
        return headElements;
    }
}
