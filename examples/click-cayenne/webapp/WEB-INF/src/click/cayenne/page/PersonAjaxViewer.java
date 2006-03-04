package click.cayenne.page;

import org.objectstyle.cayenne.DataObject;
import net.sf.click.control.ActionLink;
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
     * @see net.sf.click.Page#getContentType()
     */
    public String getContentType() {
        return "text/xml";
    }

    /**
     * Display the Person AJAX table by rendering a Rico AJAX response.
     *
     * @see net.sf.click.Page#onGet()
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
