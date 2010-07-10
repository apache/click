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
 * Provides a callback for Control life cycle events. These events can be used
 * to further process the Control or its children.
 * <p/>
 * The following callback events are defined:
 * <ul>
 * <li>preResponse - occurs before the control markup is written to the response</li>
 * <li>preGetHeadElements - occurs after <tt>preResponse</tt> but before the control
 * {@link Control#getHeadElements() HEAD elements} are written to the response</li>
 * <li>preDestroy - occurs before the Control {@link Control#onDestroy() onDestroy} event handler.</li>
 * </ul>
 *
 * The events defined by Callback is useful to decorate or manipulate its source
 * control, for example:
 * <ul>
 * <li>add/remove Control HEAD elements such as JavaScript and CSS dependencies and setup scripts</li>
 * <li>add/remove Control attributes such as <tt>"class"</tt>, <tt>"style"</tt> etc</li>
 * </ul>
 */
public interface Callback {

    /**
     * This event occurs before the markup is written to the HttpServletResponse.
     *
     * @param source the control the callback is registered with
     */
    public void preResponse(Control source);

    /**
     * This event occurs after {@link #preResponse(org.apache.click.Control)},
     * but before the Control's {@link Control#getHeadElements()} is called.
     *
     * @param source the control the callback is registered with
     */
    public void preGetHeadElements(Control source);

    /**
     * This event occurs before the Control {@link Control#onDestroy() onDestroy}
     * event handler. This event allows the Callback to cleanup or store Control
     * state in the Session.
     *
     * @param source the control the callback is registered with
     */
    public void preDestroy(Control source);
}
