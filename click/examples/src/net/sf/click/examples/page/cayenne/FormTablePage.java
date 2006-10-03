package net.sf.click.examples.page.cayenne;

import java.util.List;

import net.sf.click.control.ActionLink;
import net.sf.click.control.Column;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Submit;
import net.sf.click.control.Table;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.cayenne.CayenneForm;
import net.sf.click.extras.control.LinkDecorator;

import org.objectstyle.cayenne.DataObject;

/**
 * Provides an abstract CayenneForm and Table Page for creating and editing
 * DataObjects.
 * <p/>
 * Subclasses must implement the abstract methods:
 * <ul>
 * <li>{@link #getDataObjectClass()} &nbsp; - to define the DataObject class to edit</li>
 * <li>{@link #getDataObject(Object)} &nbsp; - to look up the DataObject for the given id</li>
 * <li>{@link #getRowList()} &nbsp; - to provide the table the list of DataObject to display</li>
 * </ul>
 *
 * @author Malcolm Edgar
 */
public abstract class FormTablePage extends BorderPage {

    public CayenneForm form;
    public Table table = new Table();
    public ActionLink editLink = new ActionLink("edit", this, "onEditClick");
    public ActionLink removeLink = new ActionLink("remove", this, "onRemoveClick");

    /**
     * Create a FormTablePage instance and initialize the form and table
     * properties.
     */
    public FormTablePage() {
        form = createForm();
        form.setDataObjectClass(getDataObjectClass());
        form.setErrorsPosition(Form.POSITION_TOP);

        // Table
        table.setAttribute("class", "simple");
        table.setAttribute("width", "500px;");
        table.setShowBanner(true);
        table.setPageSize(getMaxTableSize());
    }

    // ------------------------------------------------------- Abstract Methods

    /**
     * Return the DataObject for the given id.
     *
     * @param id the DataObject identifier
     * @return the DataObject for the given id
     */
    public abstract DataObject getDataObject(Object id);

    /**
     * Return the DataObject class to edit and display.
     *
     * @return the DataObject class to edit and display
     */
    public abstract Class getDataObjectClass();

    /**
     * Return the list of DataObjects to display in the table.
     *
     * @return the
     */
    public abstract List getRowList();

    // --------------------------------------------------------- Public Methods

    /**
     * Return a new CayenneForm instance. This method is invoked in the
     * FormTablePage constructor.
     *
     * @return a new CayenneForm instance
     */
    public CayenneForm createForm() {
        return new CayenneForm();
    }

    /**
     * Save the given DataObject.
     *
     * @param dataObject the DataObject to save
     */
    public void saveDataObject(DataObject dataObject) {
        if (dataObject != null) {
            getDataContext().commitChanges();
        }
    }

    /**
     * Delete the given DataObject.
     *
     * @param dataObject the DataObject to delete
     */
    public void deleteDataObject(DataObject dataObject) {
        if (dataObject != null) {
            getDataContext().deleteObject(dataObject);
            getDataContext().commitChanges();
        }
    }

    /**
     * Complete the initialization of the form and table controls.
     *
     * @see net.sf.click.Page
     */
    public void onInit() {
        // Complete form initialization
        form.add(new Submit("new", " New ", this, "onCancelClick"));
        form.add(new Submit("save", " Save ", this, "onSaveClick"));
        form.add(new Submit("cancel", "Cancel", this, "onCancelClick"));
        form.add(new HiddenField("pageNumber", String.class));

        // Complete table initialization
        Column column = new Column("Action");
        column.setAttribute("width", "100px;");
        ActionLink[] links = new ActionLink[]{editLink, removeLink};
        column.setDecorator(new LinkDecorator(table, links, "id"));
        table.addColumn(column);

        removeLink.setAttribute("onclick", "return window.confirm('Are you sure you want to delete this record?');");
    }

    /**
     * Perform a form submission check to ensure the form was not double posted.
     *
     * @see net.sf.click.Page#onSecurityCheck()
     */
    public boolean onSecurityCheck() {
        String pagePath = getContext().getPagePath(getClass());

        return  form.onSubmitCheck(this, pagePath);
    }

    /**
     * Return the maximum number of rows to display in the table. Subclasses
     * should override this method to display a different number of rows.
     *
     * @return the maximum number of rows to display in the table
     */
    public int getMaxTableSize() {
        return 10;
    }

    public boolean onSaveClick() {
        if (form.isValid()) {
            DataObject dataObject = form.getDataObject();

            saveDataObject(dataObject);

            onCancelClick();
        }
        return true;
    }

    public boolean onCancelClick() {
        form.setDataObject(null);
        form.clearErrors();
        return true;
    }

    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        if (id != null) {
            DataObject dataObject = getDataObject(id);
            form.setDataObject(dataObject);
        }
        return true;
    }

    public boolean onRemoveClick() {
        Integer id = removeLink.getValueInteger();
        if (id != null) {
            DataObject dataObject = getDataObject(id);

            deleteDataObject(dataObject);

            onCancelClick();
        }
        return true;
    }

    /**
     * @see net.sf.click.Page#onGet()
     */
    public void onGet() {
        form.getField("pageNumber").setValue("" + table.getPageNumber());
    }

    /**
     * @see net.sf.click.Page#onRender()
     */
    public void onRender() {
        List list = getClientService().getClients();
        table.setRowList(list);

        if (list.size() <= getMaxTableSize()) {
            table.setShowBanner(false);
            table.setPageSize(0);
        }
    }

}
