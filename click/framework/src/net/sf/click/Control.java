/*
 * Copyright 2004 Malcolm A. Edgar
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

/**
 * Provides the interface for Page controls. When a Page request
 * event is processed Controls may perform server side event processing
 * through their {@link #onProcess()} method.
 * <p/>
 * Controls are generally rendered in a Page by calling their 
 * <tt>toString()</tt> method. 
 * 
 * @author Malcolm Edgar
 */
public interface Control {
 
    /**
     * Return the Page request Context of the Control.
     * 
     * @return the Page request Context
     */
    public Context getContext();
    
    /**
     * Set the Page request Context of the Control.
     * 
     * @param context the Page request Context
     * @throws IllegalArgumentException if the Context is null
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
     * <blockquote><pre>
     * public boolean onClick() {
     *     System.out.println("onClick called");
     *     return true;
     * }
     * </pre></blockquote>
     * 
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method);
    
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
     * The on process event handler. Each Page control will be processed when 
     * the Page is requested.
     * <p/>
     * These controls may be processed by the ClickServlet, as with the 
     * {@link net.sf.click.control.ActionLink} and {@link net.sf.click.control.Form}
     * controls), or they maybe processed by the Form, in the case of  
     * {@link net.sf.click.control.Field} controls.
     * <p/>
     * When a control is processed it should return true if the Page should 
     * continue event processing, or false no other controls should be processed
     * and the {@link Page#onGet()} or {@link Page#onPost()} methods should
     * not be invoked.
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess();

}
