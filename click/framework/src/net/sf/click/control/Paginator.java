/*
 * Copyright 2008 Malcolm A. Edgar
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

import java.io.Serializable;

import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides an interface for rendering the table pagination controls.
 *
 * @author Malcolm Edgar
 */
public interface Paginator extends Serializable {

    /**
     * Return the paginator's parent table.
     *
     * @return the paginators parent table
     */
    public Table getTable();

    /**
     * Set the paginator's parent table. This method will be invoked when the
     * paginator is set on the parent table.
     *
     * @param table the paginator's parent table
     */
    public void setTable(Table table);

    /**
     * Render the table pagination controls to the given buffer.
     *
     * @param buffer the string buffer to render the paginator to
     */
    public void render(HtmlStringBuffer buffer);

}
