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
package click.cayenne.page;

import java.util.List;

import org.apache.click.control.ActionLink;

/**
 * Provides a Departments viewer page with an ActonLink to create new 
 * departments.
 * 
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class DepartmentsViewer extends BorderedPage {

    public DepartmentsViewer() {
        addModel("head-include", "ajax-head.htm");
        addModel("body-onload", "registerAjaxStuff();");

        addControl(new ActionLink("newLink", this, "onNewClick"));
    }

    /**
     * Perform a configured "DepartmentSearch" and add the results to the pages
     * model for display.
     *  
     * @see org.apache.click.Page#onGet()
     */
    public void onGet() {
        List departmentList = 
            getDataContext().performQuery("DepartmentSearch", true);
        addModel("departments", departmentList);
    }

    /**
     * Handle the create new Department click, forwarding to the 
     * <tt>DepartmentEditor</tt> page.
     * 
     * @return false
     */
    public boolean onNewClick() {
        setForward("department-editor.htm");
        return false;
    }
}
