package click.cayenne.page;

import java.util.List;

import net.sf.click.control.ActionLink;

/**
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class DepartmentsViewer extends BorderedPage {

    public void onInit() {
        addModel("head-include", "ajax-head.htm");
        addModel("body-onload", "registerAjaxStuff();");

        addControl(new ActionLink("newLink", this, "onNewClick"));
    }

    public void onGet() {
        List departmentList = 
            getDataContext().performQuery("DepartmentSearch", true);
        addModel("departments", departmentList);
    }

    public boolean onNewClick() {
        setForward("department-editor.htm");
        return false;
    }
}
