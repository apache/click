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
package net.sf.click.control;

import net.sf.click.Context;

/**
 * Provides a decorator interface for rendering Table column values.
 * <p/>
 * The following example illustrates how to render a email hyperlink in a
 * email table column.
 *
 * <pre class="codeJava">
 * Column column = <span class="kw">new</span> Column(<span class="st">"email"</span>);
 *
 * column.setDecorator(<span class="kw">new</span> Decorator() {
 *     <span class="kw">public</span> String render(Object row, Context context) {
 *         Customer customer = (Customer) row;
 *         String email = customer.getEmail();
 *         String fullName = customer.getFullName();
 *         <span class="kw">return</span> <span class="st">"&lt;a href='mailto:"</span> + email + <span class="st">"'&gt;"</span> + fullName + <span class="st">"&lt;/a&gt;"</span>;
 *     }
 * });
 *
 * table.addColumn(column); </pre>
 *
 * @see Column
 * @see Table
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public interface Decorator {

    /**
     * Returns a decorated string representation of the given object.
     *
     * @param row the Table row object to render
     * @param context the request context
     * @return a decorated string representation of the given object
     */
    public String render(Object row, Context context);
}
