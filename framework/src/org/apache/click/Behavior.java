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
package org.apache.click;

/**
 * Behaviors are added to Controls to provide more advanced features such as
 * Ajax support.
 * <p/>
 * To handle an Ajax request, Behaviors expose a listener method,
 * {@link #onAction(org.apache.click.Control) onAction}, which Click will
 * invoke if the Behavior method {@link #isRequestTarget(org.apache.click.Context) isRequestTarget}
 * returns true.
 * <p/>
 * <b>Please note:</b> a behavior <tt>onAction</tt> method is only invoked if the
 * Control is the Ajax target, in other words,
 * {@link org.apache.click.Control#isAjaxTarget(org.apache.click.Context) isAjaxTarget}
 * must return true.
 * <p/>
 * Behaviors also provide interceptor methods for specific Control life
 * cycle events. These interceptor methods can be used to further process the
 * control or the control children.
 * <p/>
 * The following interceptor methods are defined:
 *
 * <ul>
 * <li>preResponse - occurs before the control markup is written to the response</li>
 * <li>preGetHeadElements - occurs after <tt>preResponse</tt> but before the control
 * {@link Control#getHeadElements() HEAD elements} are written to the response</li>
 * <li>preDestroy - occurs before the Control {@link Control#onDestroy() onDestroy}
 * event handler.</li>
 * </ul>
 *
 * These events allow the Behavior to <tt>decorate</tt> a control, for example:
 * <ul>
 * <li>add/remove Control HEAD elements such as JavaScript and CSS dependencies
 * and setup scripts</li>
 * <li>add/remove Control attributes such as <tt>"class"</tt>, <tt>"style"</tt> etc</li>
 * </ul>
 */
public interface Behavior {

    /**
     * The behavior action method.
     *
     * TODO: javadoc
     *
     * @param source the control the behavior is attached to
     * @return the partial
     */
    public Partial onAction(Control source);

    /**
     * Return true if the behavior is the request target, false otherwise.
     * <p/>
     * This method is queried by Click to determine if the behavior's
     * {@link #onAction(org.apache.click.Control)} method should be called in
     * response to a request.
     * <p/>
     * By exposing this method through the Behavior interface it provides
     * implementers fine grained control over when a behavior should be the
     * request target.
     * <p/>
     * Example below:
     *
     * <pre class="prettyprint">
     * public CustomBehavior implements Behavior {
     *
     *     private String eventType;
     *
     *     public CustomBehavior(String eventType) {
     *         // The event type of the behavior
     *         super(eventType);
     *     }
     *
     *     public boolean isRequestTarget(Context context) {
     *         // Retrieve the eventType parameter from the incoming request
     *         String eventType = context.getRequestParameter("type");
     *
     *         // Check if this Behavior's eventType matches the request
     *         // "type" parameter
     *         return StringUtils.equalsIgnoreCase(this.eventType, eventType);
     *     }
     *
     *     public Partial onAction(Control source) {
     *         // If isRequestTarget returned true, the onAction method will be
     *         // invoked
     *         ...
     *     }
     * } </pre>
     *
     * @param context the request context
     * @return true if the behavior is the request target, false otherwise
     */
    public boolean isRequestTarget(Context context);

    /**
     * This event occurs before the markup is written to the HttpServletResponse.
     *
     * @param source the control the behavior is registered with
     */
    public void preResponse(Control source);

    /**
     * This event occurs after {@link #preResponse(org.apache.click.Control)},
     * but before the Control's {@link Control#getHeadElements()} is called.
     *
     * @param source the control the behavior is registered with
     */
    public void preGetHeadElements(Control source);

    /**
     * This event occurs before the Control {@link Control#onDestroy() onDestroy}
     * event handler. This event allows the behavior to cleanup or store Control
     * state in the Session.
     *
     * @param source the control the behavior is registered with
     */
    public void preDestroy(Control source);
}
