/*
 * Copyright 2004-2006 Malcolm A. Edgar
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
package net.sf.click.extras.panel;

import net.sf.click.control.Panel;

/**
 * Provides a panel which lists out multiple sub-panels as div elements.
 * <p/>
 * The classpath <tt>ListPanel.htm</tt> template is illustrated below:
 *
 * <pre class="codeHtml">
 * &lt;div id="<span class="blue">$this.id</span>"&gt;
 *   <span class="red">#foreach</span> (<span class="blue">$panel</span> <span class="red">in</span> <span class="blue">$this.panels</span>)
 *     &lt;div id="<span class="blue">$panel.id</span>"&gt;
 *       <span class="blue">$panel</span>
 *     &lt;/div&gt;
 *   <span class="red">#end</span>
 * &lt;/div&gt; </pre>
 *
 * @author Phil Barnes
 * @author Malcolm Edgar
 */
public class ListPanel extends Panel {

    private static final long serialVersionUID = 1L;

}
