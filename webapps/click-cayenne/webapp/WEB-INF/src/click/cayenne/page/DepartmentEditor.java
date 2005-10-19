package click.cayenne.page;

import click.cayenne.entity.Department;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import net.sf.click.extras.cayenne.CayenneForm;

public class DepartmentEditor extends BorderedPage {

    public static final String DEPARTMENT_EDIT_KEY = 
        DepartmentEditor.class.getName();

    protected CayenneForm form = new CayenneForm("form", Department.class);

    /**
     * @see Page#onInit()
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
     * @see Page#onGet()
     */
    public void onGet() {
        Department department = (Department) 
            getContext().getRequestAttribute(DEPARTMENT_EDIT_KEY);
        
        if (department != null) {
            form.setDataObject(department);
        }
    }

    public boolean onOkClick() {
        if (form.isValid()) {
            if (form.saveChanges()) {
                setRedirect("departments-viewer.htm");
                return false;
            }
        }
        return true;
    }

    public boolean onCancelClick() {
        setRedirect("departments-viewer.htm");
        return false;
    }
}
