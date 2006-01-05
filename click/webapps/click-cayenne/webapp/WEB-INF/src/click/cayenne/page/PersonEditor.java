package click.cayenne.page;

import net.sf.click.extras.cayenne.CayenneForm;
import net.sf.click.extras.cayenne.RelationshipSelect;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.control.*;
import net.sf.click.Context;
import click.cayenne.entity.Person;
import click.cayenne.entity.Department;
import org.objectstyle.cayenne.query.SelectQuery;

import java.util.List;

/**
 * Provides a Person Editor page which can be used to create or edit
 * Person data objects. This page uses the data aware CayenneForm control
 * for simplified editing data objects.
 * 
 * @author Ahmed Mohombe 
 */
public class PersonEditor  extends BorderedPage {

    /** The Person editing CayenneForm. */
    protected CayenneForm form = new CayenneForm("form", Person.class);

    protected RelationshipSelect department;
    /**
     * Create a new Person Editor.
     */
    public PersonEditor() {
        form.setButtonAlign("right");
        addControl(form);

        form.add(new TextField("fullName", "Full Name", 35));
        form.add(new DateField("dateHired","Date Hired"));
        form.add(new DoubleField("baseSalary","Base Salary"));


        form.addRelation("department",Department.class,"department");
        department = new RelationshipSelect("department","Department");
        department.setEmptyOption(true);
        department.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                Department departmentDataObject = (Department) row;
                return departmentDataObject.getName();
            }
        });
        form.add(department);

        SelectQuery query = new SelectQuery(Department.class);
        List departmentList = getDataContext().performQuery(query);
        setDepartmentRelation(departmentList);


        form.add(new Submit("ok", "   OK   ", this, "onOkClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));
    }

    /**
     * Set the person object to edit.
     *
     * @param person the department to edit
     */
    public void setPerson(Person person) {
        form.setDataObject(person);
    }

    public void setDepartmentRelation(List departments){
//        System.out.println("*-----> PersonEditor.setDepartmentRelation");
//        for (int i = 0; i < departments.size(); i++) {
//            Object o = departments.get(i);
//            System.out.println("*----> Element in Departments:"+o);
//        }
        department.setRelations(departments);
    }

/*
    public void onGet() {
        SelectQuery query = new SelectQuery(Department.class);
        List departmentList = getDataContext().performQuery(query);
        setDepartmentRelation(departmentList);
    }
*/
    
    /**
     * Handle the OK button click, saving the Person if valid and
     * redirecting to the <tt>PersonsViewer</tt> page. If the Person
     * is not valid display form errors.
     */
    public boolean onOkClick() {
        if (form.isValid()) {
            if (form.saveChanges()) {
                setRedirect("persons-viewer.htm");
                return false;
            }
        }
        return true;
    }

    /**
     * Handle the Cancel button click, redirecting to the
     * <tt>PersonsViwer</tt> page.
     *
     * @return false
     */
    public boolean onCancelClick() {
        setRedirect("persons-viewer.htm");
        return false;
    }

}
