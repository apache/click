package click.cayenne.page;

import net.sf.click.control.ActionLink;

import org.objectstyle.cayenne.DataObject;

import click.cayenne.entity.Department;

/**
 * An AJAX Department viewer that implements a number of actions on department.
 * 
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class DepartmentAjaxViewer extends CayennePage {

    public static final String DEPARTMENT_VELOKEY = "department";

    /** The edit Department ActionLink. */    
    protected ActionLink editLink;
    
    /** The delete Department ActionLink. */    
    protected ActionLink deleteLink;

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
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
     * Display the Department AJAX table by rendering a Rico AJAX response. 
     * 
     * @see net.sf.click.Page#onGet()
     */
    public void onGet() {
        String id = getContext().getRequest().getParameter("id");

        if (id != null) {
            DataObject dataObject = getDataObject(Department.class, id);
            addModel(DEPARTMENT_VELOKEY, dataObject);
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
