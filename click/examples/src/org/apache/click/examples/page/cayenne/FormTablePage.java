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
package org.apache.click.examples.page.cayenne;

import java.util.List;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.access.DataContext;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.cayenne.CayenneForm;
import org.apache.click.extras.control.LinkDecorator;

/**
 * Provides an abstract CayenneForm and Table Page for creating and editing
 * DataObjects.
 * <p/>
 * Subclasses must implement the abstract methods:
 * <ul>
 * <li>{@link #getDataObjectClass()} &nbsp; - to define the DataObject class to edit</li>
 * <li>{@link #getDataObject(Object)} &nbsp; - to look up the DataObject for the given id</li>
 * <li>{@link #getRowList()} &nbsp; - to provide the table the list of DataObject to display</li>
 * </ul>
 */
public abstract class FormTablePage extends BorderPage {

    private static final long serialVersionUID = 1L;

    protected CayenneForm form;
    protected Table table = new Table("table");
    protected ActionLink editLink = new ActionLink("edit", this, "onEditClick");
    protected ActionLink removeLink = new ActionLink("remove", this, "onRemoveClick");

    // Constructor ------------------------------------------------------------

    /**
     * Create a FormTablePage instance and initialize the form and table
     * properties.
     */
    public FormTablePage() {
        form = createForm();
        form.setName("form");
        addControl(form);
        addControl(table);
        addControl(editLink);
        addControl(removeLink);

        form.setDataObjectClass(getDataObjectClass());
        form.setErrorsPosition(Form.POSITION_TOP);

        // Table
        table.addStyleClass("simple");
        table.setAttribute("width", "500px;");
        table.setShowBanner(true);
        table.setPageSize(getMaxTableSize());

        editLink.setImageSrc("/assets/images/edit.gif");
        removeLink.setImageSrc("/assets/images/delete.gif");
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * Perform a form submission check to ensure the form was not double posted.
     *
     * @see org.apache.click.Page#onSecurityCheck()
     */
    @Override
    public boolean onSecurityCheck() {
        String pagePath = getContext().getPagePath(getClass());

        if (form.onSubmitCheck(this, pagePath)) {
            return true;
        } else {
            getContext().setFlashAttribute("error", getMessage("invalid.form.submit"));
            return false;
        }
    }

    /**
     * Complete the initialization of the form and table controls.
     *
     * @see org.apache.click.Page#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();

        // Complete form initialization
        form.add(new Submit("save", " Save ", this, "onSaveClick"));
        form.add(new Submit("cancel", "Cancel", this, "onCancelClick"));

        // Complete table initialization
        Column column = new Column("Action");
        column.setSortable(false);
        column.setAttribute("width", "100px;");
        ActionLink[] links = new ActionLink[]{editLink, removeLink};
        column.setDecorator(new LinkDecorator(table, links, "id"));
        table.addColumn(column);

        removeLink.setAttribute("onclick", "return window.confirm('Are you sure you want to delete this record?');");
    }

    /**
     * The save event handler.
     *
     * @return true if processing should continue, false otherwise
     */
    public boolean onSaveClick() {
        if (form.isValid()) {
            DataObject dataObject = form.getDataObject();

            saveDataObject(dataObject);

            clear();
        }
        return true;
    }

    /**
     * The cancel event handler.
     *
     * @return true if processing should continue, false otherwise
     */
    public boolean onCancelClick() {
        clear();
        return true;
    }

    /**
     * The edit event handler.
     *
     * @return true if processing should continue, false otherwise
     */
    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        if (id != null) {
            DataObject dataObject = getDataObject(id);
            form.setDataObject(dataObject);
        }
        return true;
    }

    /**
     * The remove event handler.
     *
     * @return true if processing should continue, false otherwise
     */
    public boolean onRemoveClick() {
        Integer id = removeLink.getValueInteger();
        if (id != null) {
            DataObject dataObject = getDataObject(id);

            deleteDataObject(dataObject);

            clear();
        }
        return true;
    }

    /**
     * Clear the form dataObject and any errors it might have.
     */
    public void clear() {
        form.setDataObject(null);
        form.clearErrors();
    }

    /**
     * @see org.apache.click.Page#onRender()
     */
    @Override
    public void onRender() {
        List list = getRowList();
        table.setRowList(list);

        if (list.size() <= getMaxTableSize()) {
            table.setShowBanner(false);
            table.setPageSize(0);
        }
    }

    // ------------------------------------------------------- Abstract Methods

    /**
     * Return the DataObject for the given id.
     *
     * @param id the DataObject identifier
     * @return the DataObject for the given id
     */
    public abstract DataObject getDataObject(Object id);

    /**
     * Return the DataObject class to edit and display.
     *
     * @return the DataObject class to edit and display
     */
    @SuppressWarnings("unchecked")
    public abstract Class getDataObjectClass();

    /**
     * Return the list of DataObjects to display in the table.
     *
     * @return the list of DataObject to display in the table
     */
    @SuppressWarnings("unchecked")
    public abstract List getRowList();

    // --------------------------------------------------------- Public Methods

    /**
     * Return a new CayenneForm instance. This method is invoked in the
     * FormTablePage constructor.
     *
     * @return a new CayenneForm instance
     */
    public CayenneForm createForm() {
        return new CayenneForm();
    }

    /**
     * Save the given DataObject.
     *
     * @param dataObject the DataObject to save
     */
    public void saveDataObject(DataObject dataObject) {
        if (dataObject != null) {
            DataContext.getThreadDataContext().commitChanges();
        }
    }

    /**
     * Delete the given DataObject.
     *
     * @param dataObject the DataObject to delete
     */
    public void deleteDataObject(DataObject dataObject) {
        if (dataObject != null) {
            DataContext.getThreadDataContext().deleteObject(dataObject);
            DataContext.getThreadDataContext().commitChanges();
        }
    }

    /**
     * Return the maximum number of rows to display in the table. Subclasses
     * should override this method to display a different number of rows.
     *
     * @return the maximum number of rows to display in the table
     */
    public int getMaxTableSize() {
        return 10;
    }

}
