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

import org.apache.cayenne.DataObject;
import org.apache.click.control.ActionLink;
import click.cayenne.entity.Person;

/**
 * An AJAX Person viewer that implements a number of actions on person.
 */
public class PersonAjaxViewer extends CayennePage {

    /** The edit Person ActionLink. */
    protected ActionLink editLink;

    /** The delete Person ActionLink. */
    protected ActionLink deleteLink;

    public PersonAjaxViewer() {
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
     * Display the Person AJAX table by rendering a Rico AJAX response.
     *
     * @see org.apache.click.Page#onGet()
     */
    public void onGet() {
        String id = getContext().getRequest().getParameter("id");

        if (id != null) {
            DataObject dataObject = getDataObject(Person.class, id);
            addModel("person", dataObject);
        }
    }

    /**
     * Handle an edit Person click, forwarding to the
     * <tt>PersonEditor</tt> page.
     *
     * @return false
     */
    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        if (id != null) {
            Person person = (Person) getDataObject(Person.class, id);

            PersonEditor personEditor = (PersonEditor)
                getContext().createPage(PersonEditor.class);
            personEditor.setPerson(person);

            setForward(personEditor);
        }

        return false;
    }

    /**
     * Handle an delete Person click, forwarding to the
     * <tt>PersonsViewer</tt> page.
     *
     * @return false
     */
    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        if (id != null) {
            DataObject dataObject = getDataObject(Person.class, id);

            // handle stale links
            if (dataObject != null) {
                getDataContext().deleteObject(dataObject);
                getDataContext().commitChanges();
            }

            setForward("persons-viewer.htm");
        }

        return false;
    }

}
