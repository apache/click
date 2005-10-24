package click.cayenne.page;

import java.util.List;

import net.sf.click.control.ActionLink;

/**
 * Provides a Departments viewer page with an ActonLink to create new 
 * departments.
 * 
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class DepartmentsViewer extends BorderedPage {

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        addModel("head-include", "ajax-head.htm");
        addModel("body-onload", "registerAjaxStuff();");

        addControl(new ActionLink("newLink", this, "onNewClick"));
    }

    /**
     * Perform a configured "DepartmentSearch" and add the results to the pages
     * model for display.
     *  
     * @see net.sf.click.Page#onGet()
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
