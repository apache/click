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
package org.apache.click.extras.panel;

import org.apache.click.control.Panel;

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
 */
public class ListPanel extends Panel {

    private static final long serialVersionUID = 1L;

    /**
     * Create a ListPanel with the given name.
     *
     * @param name the name of the panel
     */
    public ListPanel(String name) {
        super(name);
    }

    /**
     * Create a Panel with the given name and template path.
     *
     * @param name the name of the panel
     * @param template the Velocity template
     */
    public ListPanel(String name, String template) {
        super(name, template);
    }

    /**
     * Create a ListPanel with the given name, id attribute and template path.
     *
     * @param name the name of the panel
     * @param template the Velocity template path
     * @param id the id HTML attribute value
     */
    public ListPanel(String name, String template, String id) {
        super(name, template, id);
    }

    /**
     * Create a ListPanel with no name or template defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public ListPanel() {
        super();
    }
}
