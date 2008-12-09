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
package net.sf.click.examples.page.jsp;

import java.util.List;

import net.sf.click.examples.page.BorderPage;

/**
 * Provides JSP Page example where a JSP page and JSP border template is used to
 * render a table.
 *
 * @author Malcolm Edgar
 */
public class CustomerTable extends BorderPage {

    public List customers = null;

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        customers = getCustomerService().getCustomersSortedByName(10);
    }

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.jsp"</tt>
     *
     * @see net.sf.click.Page#getTemplate()
     */
    public String getTemplate() {
        return "/border-template.jsp";
    }
}
