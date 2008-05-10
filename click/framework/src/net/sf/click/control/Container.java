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
package net.sf.click.control;

import java.util.List;
import net.sf.click.Control;

/**
 * Container extends {@link net.sf.click.Control} and enables the creation of
 * nested controls.
 * <p/>
 * Container allows one to add, remove and retrieve controls.
 * <p/>
 * <b>Please note</b> {@link AbstractContainer} provides
 * a default implementation of the Container interface and allows easy creation
 * of new containers.
 *
 * @see net.sf.click.util.ContainerUtils
 *
 * @author Bob Schellink
 */
public interface Container extends Control {

    /**
     * Add the control to the container and return the added instance. In some
     * instances the returned control might be a different control from the
     * control that was added.
     * <p/>
     * This method implementation must adhere to the following:
     * <ul>
     *  <li>
     *   If the control's name is set, the control must be retrievable from
     *   the container by invoking {@link #getControl(java.lang.String)}.
     *  </li>
     *  <li>
     *   The control must be added to the containers list of controls and be
     *   included in the list returned by the method
     *   {@link #getControls()}.
     *  </li>
     *  <li>
     *   The control's parent must be set to this container, so that invoking
     *   {@link net.sf.click.Control#getParent()} returns this container
     *   instance.
     *  </li>
     * </ul>
     *
     * @param control the control to add to the container and return
     * @return the control that was added to the container
     */
    Control add(Control control);

    /**
     * Add the control to the container at the specified index, and return the
     * added instance. In some instances the returned control might be a
     * different control from the control that was added.
     * <p/>
     * This method implementation must adhere to the following:
     * <ul>
     *  <li>
     *   If the control's name is set, the control must be retrievable from
     *   the container by invoking {@link #getControl(java.lang.String)}.
     *  </li>
     *  <li>
     *   The control must be added to the containers list of controls and be
     *   included in the list returned by the method
     *   {@link #getControls()}.
     *  </li>
     *  <li>
     *   The control's parent must be set to this container, so that invoking
     *   {@link net.sf.click.Control#getParent()} returns this container
     *   instance.
     *  </li>
     * </ul>
     *
     * @param index the index at which the control is to be inserted
     * @param control the control to add to the container and return
     * @return the control that was added to the container
     */
    Control add(int index, Control control);

    /**
     * Remove the given control from the container, returning true if the
     * control was found in the container and removed, or false if the control
     * was not found.
     * <p/>
     * This method implementation must adhere to the following:
     * <ul>
     *  <li>
     *   If the control's name is set, the control must <b>not</b> be
     *   retrievable from the container when invoking
     *   {@link #getControl(java.lang.String)}.
     *  </li>
     *  <li>
     *   The control must be removed from the containers list of controls and
     *   must <b>not</b> be included in the list returned by the method
     *   {@link #getControls()}.
     *  </li>
     *  <li>
     *   The control's parent must be set to <tt>null</tt>, so that invoking
     *   {@link net.sf.click.Control#getParent()} returns <tt>null</tt>.
     *  </li>
     * </ul>
     *
     * @param control the control to remove from the container
     * @return true if the control was removed from the container
     */
    boolean remove(Control control);

    /**
     * Return the sequential list of controls held by the container.
     *
     * @return the sequential list of controls held by the container
     */
    List getControls();

    /**
     * Return the named control from the container if found or null otherwise.
     *
     * @param controlName the name of the control to get from the container
     * @return the named control from the container if found or null otherwise
     */
    Control getControl(String controlName);

    /**
     * Return true if the container contains the specified control.
     *
     * @param control the control whose presence in this container is to be tested
     * @return true if the container contains the specified control
     */
    boolean contains(Control control);

    /**
     * Returns true if this container has existing controls, false otherwise.
     *
     * @return true if the container has existing controls, false otherwise.
     */
    public boolean hasControls();
}
