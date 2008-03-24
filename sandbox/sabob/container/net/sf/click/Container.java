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
package net.sf.click;

import java.util.List;
import net.sf.click.util.HtmlStringBuffer;

public interface Container extends Control {

    Control addControl(Control control);

    boolean removeControl(Control control);

    List getControls();

    boolean contains(String controlName);

    Control getControl(String controlName);

    /**
     * NOTE: #toString delegates to #render for major performance boost by 
     * rendering from the same buffer. Also AbstractContainer#getContainerSizeEst is useful
     * here.
     * Belongs to Control interface rather than Container.
     */
    void render(HtmlStringBuffer buffer);
}
