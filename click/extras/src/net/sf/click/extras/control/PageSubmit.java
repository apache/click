/*
 * Copyright 2006 Malcolm A. Edgar
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

import net.sf.click.Context;
import net.sf.click.Page;
import net.sf.click.control.Submit;
import net.sf.click.util.ClickUtils;

/**
 * Provides a Page redirect Submit control: &nbsp; &lt;input type='submit'&gt;.
 *
 * <table class='htmlHeader cellspacing='6'>
 * <tr>
 * <td><input type='submit' value='Page Submit' title='PageSubmit Control'/></td>
 * </tr>
 * </table>
 *
 * The PageSubmit is a Submit button which enables you to redirect to another
 * page from a Form, without having to define a listener method.
 * <p/>
 * This control is typically used for Cancel buttons.
 *
 * <h3>PageSubmit Example</h3>
 *
 * The example code below will redirect the request to the <tt>HomePage</tt> if
 * the cancel button is pressed.
 *
 * <pre class="javaCode">
 *    form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">" OK "</span>, <span class="kw">this</span>, <span class="st">"onOkClick"</span>));
 *    form.add(<span class="kw">new</span> PageSubmit(<span class="st">"cancel"</span>, <span class="st">"Cancel"</span>, HomePage.<span class="kw">class</span>));
 * </pre>
 *
 * <p/>
 * See also the W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class PageSubmit extends Submit {

    private static final long serialVersionUID = 1L;

    /** The target page to redirect to. */
    protected Class pageClass;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a PageSubmit button with the given name.
     *
     * @param name the button name
     */
    public PageSubmit(String name) {
        super(name);
    }

    /**
     * Create a PageSubmit button with the given name and label.
     *
     * @param name the button name
     * @param label the button display label
     */
    public PageSubmit(String name, String label) {
        super(name, label);
    }

    /**
     * Create a PageSubmit button with the given name and target pageClass.
     *
     * @param name the button name
     * @param pageClass the target page class
     */
    public PageSubmit(String name, Class pageClass) {
        super(name);
        setPageClass(pageClass);
    }

    /**
     * Create a PageSubmit button with the given name, label and target
     * pageClass.
     *
     * @param name the button name
     * @param label the button display label
     * @param pageClass the target page class
     */
    public PageSubmit(String name, String label, Class pageClass) {
        super(name, label);
        setPageClass(pageClass);
    }

    /**
     * Create an PageSubmit button with no name or target page class
     * defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public PageSubmit() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Set the context value and initialize the onClick listener.
     *
     * @see net.sf.click.Control#setContext(Context)
     *
     * @param context the Page request Context
     * @throws IllegalArgumentException if the Context is null
     */
    public void setContext(Context context) {
        super.setContext(context);
        setListener(this, "onClick");
    }

    /**
     * Return the target page class to redirect to.
     *
     * @return the target page class to redirect to
     */
    public Class getPageClass() {
        return pageClass;
    }

    /**
     * Set the target page class to redirect to.
     *
     * @param pageClass the target page class to redirect to
     */
    public void setPageClass(Class pageClass) {
        if (pageClass == null) {
            throw new IllegalArgumentException("null pageClass parameter");
        }
        this.pageClass = pageClass;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * The submit buttons callback listener, which will redirect the page to
     * the defined target page class and return false to abort any further
     * processing.
     *
     * @return false to abort any further processing
     */
    public boolean onClick() {
        Page page = ClickUtils.getParentPage(this);
        if (page == null) {
            throw new RuntimeException("parent page not available");
        }
        if (pageClass == null) {
            throw  new RuntimeException("target pageClass is not defined");
        }
        page.setRedirect(pageClass);
        return false;
    }

}
