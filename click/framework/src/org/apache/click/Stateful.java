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
 * Provides an interface that controls can implement that need to preserve
 * state across multiple requests.
 * <p/>
 * <b>Please note:</b> Control state is not saved and restored automatically by
 * Click. Instead, state saving and restoring is under full control of the
 * developer through a public API.
 * <p/>
 * Controls implementing this interface are expected to {@link #getState() save}
 * and {@link #setState(java.lang.Object) restore} their internal state, as well
 * as the state of their child controls. Controls can further expose a public
 * API for saving and restoring their state.
 * <p/>
 * An example implementation is shown below:
 *
 * <pre class="prettyprint">
 * public class MyControl extends AbstractControl implements Stateful {
 *
 *     private String value;
 *
 *     ...
 *
 *     // Return the Control internal state
 *     public Object getState() {
 *         return getValue();
 *     }
 *
 *     // Set the Control internal state
 *     public void setState(Object state) {
 *         String fieldState = (String) state;
 *         setValue(fieldState);
 *     }
 *
 *    // A save state helper method
 *     public void saveState(Context context) {
 *         // Save the control state in the session
 *         ClickUtils.saveState(this, getName(), context);
 *     }
 *    // A restore state helper method
 *     public void restoreState(Context context) {
 *         // Load the control state from the session
           ClickUtils.restoreState(this, getName(), context);
 *     }
 *
 *    // A remove state helper method
 *     public void removeState(Context context) {
 *         // Remove the control state from the session
*          ClickUtils.removeState(this, getName(), context);
 *     }
 *
 * } </pre>
 *
 * Saving and restoring Control state is controlled by the developer.
 * <p/>
 * For example:
 *
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *    private MyControl control = new MyControl("mycontrol");
 *
 *     public MyPage() {
 *         // Load the control state from the session
 *         control.loadState(getContext());
 *     }
 *
 *     public void onPost() {
 *
 *         Context context = getContext();
 *
 *         String id = context.getParameter("id");
 *         control.setValue(id);
 *
 *         // Save control state to the session
 *         control.saveState(context);
 *     }
 * }
 * </pre>
 */
public interface Stateful {

    /**
     * Return the Control internal state. State will generally be stored in the
     * Session, so it is recommended to ensure the state is
     * {@link java.io.Serializable serializable}.
     * <p/>
     * Example implementation below:
     * <pre class="prettyprint">
     * public Object getState() {
     *     Object stateArray[] = new Object[3];
     *     stateArray[0] = getValue();
     *     stateArray[1] = Number.valueOf(getNumber());
     *     stateArray[2] = Boolean.valueOf(getBoolean());
     *     return stateArray;
     * } </pre>
     *
     * @return the control internal state
     */
    public Object getState();

    /**
     * Restore the Control internal state from the given state object.
     * <p/>
     * Example below:
     * <pre class="prettyprint">
     * public void setState(Object state) {
     *
     *     Object[] stateArray = (Object[]) state;
     *     String storedValue = stateArray[0];
     *     setValue(storedValue);
     *
     *     int storedNumber = ((Integer) stateArray[1]).intValue();
     *     setNumber(storedNumber);
     *
     *     boolean storedBoolen = ((Boolean) stateArray[2]).booleanValue();
     *     setBoolean(storedBoolean);
     * } </pre>
     *
     * @param state the control state to restore
     */
    public void setState(Object state);
}
