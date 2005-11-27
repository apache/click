/*
 * Copyright 2004-2005 Malcolm A. Edgar
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
package net.sf.click;

import java.io.Serializable;
import java.util.Map;

/**
 * Provides the interface for Page controls. When a Page request
 * event is processed Controls may perform server side event processing
 * through their {@link #onProcess()} method.
 * <p/>
 * Controls are generally rendered in a Page by calling their
 * <tt>toString()</tt> method.
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public interface Control extends ContextAware, Serializable {
 
    /**
     * @see ContextAware#getContext()
     */
    public Context getContext();

    /**
     * @see ContextAware#setContext(Context)
     */
    public void setContext(Context context);

    /**
     * Set the controls event listener.
     * <p/>
     * The method signature of the listener is:<ul>
     * <li>must hava a valid Java method name</li>
     * <li>takes no arguments</li>
     * <li>returns a boolean value</li>
     * </ul>
     * <p/>
     * An example event listener method would be:
     *
     * <pre class="codeJava">
     * <span class="kw">public boolean</span> onClick() {
     *     System.out.println(<span class="st">"onClick called"</span>);
     *     <span class="kw">return true</span>;
     * } </pre>
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method);

    /**
     * Return HTML element identifier attribute "id" value.
     *
     * @return HTML element identifier attribute "id" value
     */
    public String getId();

    /**
     * Return the name of the Control. Each control name must be unique in the
     * containing Page model or the containing Form.
     *
     * @return the name of the control
     */
    public String getName();
 
    /**
     * Set the name of the Control. Each control name must be unique in the
     * containing Page model or the containing Form.
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    public void setName(String name);

    /**
     * Return the localized messages <tt>Map</tt> of the Control's parent.
     *
     * @return the localization <tt>Map</tt> of the Control's parent
     */
    public Map getParentMessages();

    /**
     * Set the parent's localized messages <tt>Map</tt> for the  Control.
     *
     * @param messages the parent's the localized messages <tt>Map</tt>
     */
    public void setParentMessages(Map messages);

    /**
     * The on process event handler. Each Page control will be processed when
     * the Page is requested.
     * <p/>
     * These controls may be processed by the ClickServlet, as with the
     * {@link net.sf.click.control.ActionLink} and {@link net.sf.click.control.Form}
     * controls, or they maybe processed by the Form, in the case of
     * {@link net.sf.click.control.Field} controls.
     * <p/>
     * When a control is processed it should return true if the Page should
     * continue event processing, or false if no other controls should be
     * processed and the {@link Page#onGet()} or {@link Page#onPost()} methods
     * should not be invoked.
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess();

}
