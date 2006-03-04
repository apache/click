package click.cayenne.page;

import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.extras.cayenne.CayenneForm;
import net.sf.click.extras.cayenne.PropertySelect;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;

import org.objectstyle.cayenne.query.SelectQuery;

import click.cayenne.entity.Department;
import click.cayenne.entity.Person;

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
    
    /**
     * Create a new Person Editor.
     */
    public PersonEditor() {
        form.add(new TextField("fullName", "Full Name", 35));
        form.add(new DateField("dateHired", "Date Hired"));
        form.add(new DoubleField("baseSalary", "Base Salary"));
        
        PropertySelect department = new PropertySelect("department", true);
        department.setOptionLabel("name");
        department.setSelectQuery(new SelectQuery(Department.class));
        form.add(department);

        form.add(new Submit("ok", "   OK   ", this, "onOkClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));
        
        form.setButtonAlign("right");
        addControl(form);
    }

    /**
     * Set the person object to edit.
     *
     * @param person the department to edit
     */
    public void setPerson(Person person) {
        form.setDataObject(person);
    }
    
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
