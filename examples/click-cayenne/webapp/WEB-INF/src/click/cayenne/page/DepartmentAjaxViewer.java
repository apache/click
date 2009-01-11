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

import org.apache.click.control.ActionLink;

import org.apache.cayenne.DataObject;

import click.cayenne.entity.Department;

/**
 * An AJAX Department viewer that implements a number of actions on department.
 * 
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class DepartmentAjaxViewer extends CayennePage {

    /** The edit Department ActionLink. */    
    protected ActionLink editLink;
    
    /** The delete Department ActionLink. */    
    protected ActionLink deleteLink;

    public DepartmentAjaxViewer() {
        editLink = new ActionLink("editLink", this, "onEditClick");
        addControl(editLink);

        deleteLink = new ActionLink("deleteLink", this, "onDeleteClick");
        addControl(deleteLink);
    }
    
    /**
     * Return AJAX response content type of "text/xml".
     * 
     * @see org.apache.click.Page#getContentType()
     */
    public String getContentType() {
        return "text/xml; charset=UTF-8";
    }

    /**
     * Display the Department AJAX table by rendering a Rico AJAX response. 
     * 
     * @see org.apache.click.Page#onGet()
     */
    public void onGet() {
        String id = getContext().getRequest().getParameter("id");

        if (id != null) {
            DataObject dataObject = getDataObject(Department.class, id);
            addModel("department", dataObject);
        }
    }

    /**
     * Handle an edit Department click, forwarding to the 
     * <tt>DepartmentEditor</tt> page.
     * 
     * @return false
     */
    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        if (id != null) {
            Department department = 
                (Department) getDataObject(Department.class, id);
            
            DepartmentEditor departmentEditor = (DepartmentEditor) 
                getContext().createPage(DepartmentEditor.class);
            departmentEditor.setDepartment(department);
            
            setForward(departmentEditor);
        }

        return false;
    }

    /**
     * Handle an delete Department click, forwarding to the 
     * <tt>DepartmentsViewer</tt> page.
     * 
     * @return false
     */
    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        if (id != null) {
            DataObject dataObject = getDataObject(Department.class, id);

            // handle stale links
            if (dataObject != null) {
                getDataContext().deleteObject(dataObject);
                getDataContext().commitChanges();
            }
            
            setForward("departments-viewer.htm");
        }
        
        return false;
    }
}
