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
 * Behaviors are added to Controls to provide enhanced features such as
 * Ajax. Behaviors extend the {@link Callback} interface which
 * allows them to <tt>decorate</tt> their associated Controls.
 *
 * <p/>
 * Behaviors expose a listener method through
 * {@link #onAction(org.apache.click.Control)}, which Click will invoke if
 * the method {@link #isRequestTarget(org.apache.click.Context)} returns true.
 */
public interface Behavior extends Callback {

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
     * Return true if the behavior is the requeset target, false otherwise.
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
    // TODO move this method to another interface/class?
    public boolean isRequestTarget(Context context);
}
