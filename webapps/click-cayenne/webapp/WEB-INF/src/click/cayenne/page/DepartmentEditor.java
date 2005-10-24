package click.cayenne.page;

import click.cayenne.entity.Department;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import net.sf.click.extras.cayenne.CayenneForm;

/**
 * Provides a Department Editor page which can be used to create or edit
 * Department data objects. This page uses the data aware CayenneForm control 
 * for simplified editing data objects.
 *
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class DepartmentEditor extends BorderedPage {

    public static final String DEPARTMENT_EDIT_KEY = 
        DepartmentEditor.class.getName();

    /** The Department editing CayenneForm. */
    protected CayenneForm form = new CayenneForm("form", Department.class);

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        form.setButtonAlign("right");
        addControl(form);

        form.add(new TextField("name", "Department Name", 35));
        form.add(new TextArea("Description", 35, 6));

        form.add(new Submit("   OK   ", this, "onOkClick"));
        form.add(new Submit("Cancel", this, "onCancelClick"));
    }

    /**
     * If the Department has been passed to the page as a request attribute,
     * then bind the Department object to the data aware CayenneForm.
     * 
     * @see net.sf.click.Page#onGet()
     */
    public void onGet() {
        Department department = (Department) 
            getContext().getRequestAttribute(DEPARTMENT_EDIT_KEY);
        
        if (department != null) {
            form.setDataObject(department);
        }
    }

    /**
     * Handle the OK button click, saving the Department if valid and 
     * redirecting to the <tt>DepartmentsViewer</tt> page. If the Department
     * is not valid display form errors.
     */
    public boolean onOkClick() {
        if (form.isValid()) {
            if (form.saveChanges()) {
                setRedirect("departments-viewer.htm");
                return false;
            }
        }
        return true;
    }

    /**
     * Handle the Cancel button click, redirecting to the 
     * <tt>DepartmentsViwer</tt> page.
     * 
     * @return false
     */
    public boolean onCancelClick() {
        setRedirect("departments-viewer.htm");
        return false;
    }
}
