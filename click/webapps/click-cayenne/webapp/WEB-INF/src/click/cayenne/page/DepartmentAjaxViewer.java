package click.cayenne.page;

import net.sf.click.control.ActionLink;

import org.objectstyle.cayenne.DataObject;

import click.cayenne.entity.Department;


/**
 * An AJAX department viewer that implements a number of actions on department.
 * 
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class DepartmentAjaxViewer extends CayennePage {

    public static final String DEPARTMENT_VELOKEY = "department";

    protected ActionLink editLink;
    protected ActionLink deleteLink;

    public void onInit() {
        editLink = new ActionLink("editLink", this, "onEditClick");
        addControl(editLink);

        deleteLink = new ActionLink("deleteLink", this, "onDeleteClick");
        addControl(deleteLink);
    }
    
    public String getContentType() {
        return "text/xml";
    }

    public void onGet() {
        String id = getContext().getRequest().getParameter("id");

        if (id != null) {
            DataObject dataObject = getDataObject(Department.class, id);
            addModel(DEPARTMENT_VELOKEY, dataObject);
        }
    }

    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        if (id != null) {
            DataObject dataObject = getDataObject(Department.class, id);
            getContext().setRequestAttribute(DepartmentEditor.DEPARTMENT_EDIT_KEY, 
                                             dataObject);
            
            setForward("department-editor.htm");
        }

        return false;
    }

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
        
        return true;
    }
}
