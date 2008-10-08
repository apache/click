package net.sf.click.examples.page.table;

import java.util.List;

import net.sf.click.control.Checkbox;
import net.sf.click.control.Column;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.Table;
import net.sf.click.control.TextField;
import net.sf.click.examples.control.InvestmentSelect;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.FieldColumn;
import net.sf.click.extras.control.FormTable;
import net.sf.click.extras.control.NumberField;

/**
 * This demo shows how to integrate a Form and FormTable.
 * <p/>
 * By default FormTable creates an internal Form for submissions. However it is
 * possible to use the FormTable constructor which accepts a Form so that
 * FormTable can be added to this "external" Form.
 *
 * @author Malcolm Edgar
 * @author Bob Schellink
 */
public class IntegratedFormTable extends BorderPage {

    private static final int NUM_ROWS = 20;

    private FormTable table;

    private Form form = new Form("form") {

        /**
         * PLEASE NOTE: FormTable will only be processed by form if the
         * Form is submitted. Thus paging and sorting won't work by default.
         *
         * Here we override the default behavior and explicitly process
         * FormTable (table) so that paging and sorting will still work, even
         * if the Form was not submitted.
         */
        public boolean onProcess() {
            if (form.isFormSubmission()) {
                return super.onProcess();
            } else {
                return table.onProcess();
            }
        }
    };

    // ------------------------------------------------------------ Constructor

    public IntegratedFormTable() {
        form.add(new TextField("firstname")).setRequired(true);
        form.add(new TextField("lastname")).setRequired(true);

        // Set form columns to 2, so that fields are rendered in pairs next to
        // each other
        int formColumns = 2;
        form.setColumns(formColumns);

        // Create the FormTable and pass in the existing Form into the constructor
        // This lets FormTable know it should not create an internal Form
        table = new FormTable("table", form);

        // Assemble the FormTable columns
        table.setClass(Table.CLASS_SIMPLE);
        table.setWidth("700px");
        table.setPageSize(5);
        table.setShowBanner(true);

        table.addColumn(new Column("id"));

        FieldColumn column = new FieldColumn("name", new TextField());
        column.getField().setReadonly(true);
        column.setVerticalAlign("baseline");
        table.addColumn(column);

        column = new FieldColumn("email", new EmailField());
        column.getField().setRequired(true);
        table.addColumn(column);

        column = new FieldColumn("investments", new InvestmentSelect());
        column.getField().setRequired(true);
        table.addColumn(column);

        NumberField numberField = new NumberField();
        numberField.setSize(5);
        column = new FieldColumn("holdings", numberField);
        column.setTextAlign("right");
        table.addColumn(column);

        column = new FieldColumn("dateJoined", new DateField());
        column.setDataStyle("white-space", "nowrap");
        table.addColumn(column);

        column = new FieldColumn("active", new Checkbox());
        column.setTextAlign("center");
        table.addColumn(column);

        table.getForm().add(new Submit("ok", "  OK  ", this, "onOkClick"));
        table.getForm().add(new Submit("cancel", this, "onCancelClick"));

        table.setSortable(true);

        // Set FormTable width (colspan) to the number of Form columns
        int colspan = formColumns;

        // In order to integrate FormTable, add it to the Form
        form.add(table, colspan);

        addControl(form);
    }

    // --------------------------------------------------------- Event Handlers

    public boolean onSecurityCheck() {
        String pagePath = getContext().getPagePath(getClass());

        // In this demo we protect against duplicate post submissions
        if (form.onSubmitCheck(this, pagePath)) {
            return true;
        } else {
            getContext().setFlashAttribute("error", getMessage("invalid.form.submit"));
            return false;
        }
    }

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        super.onInit();

        List customers = getCustomerService().getCustomersSortedByName(NUM_ROWS);
        table.setRowList(customers);
    }

    public boolean onOkClick() {
        if (table.getForm().isValid()) {
            // Please note with Cayenne ORM this will persist any changes
            // to data objects submitted by the form.
            getDataContext().commitChanges();
        }
        return true;
    }

    public boolean onCancelClick() {
        // Rollback any changes made to the customers, which are stored in
        // the data context
        getDataContext().rollbackChanges();

        List customers = getCustomerService().getCustomersSortedByName(NUM_ROWS);

        table.setRowList(customers);
        table.setRenderSubmittedValues(false);

        form.clearErrors();

        return true;
    }
}
