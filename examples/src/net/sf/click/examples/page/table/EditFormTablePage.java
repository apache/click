package net.sf.click.examples.page.table;

import java.util.Date;
import java.util.List;

import net.sf.click.control.ActionLink;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Column;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.Table;
import net.sf.click.control.TextField;
import net.sf.click.examples.control.InvestmentSelect;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.FieldColumn;
import net.sf.click.extras.control.FormTable;
import net.sf.click.extras.control.LinkDecorator;
import net.sf.click.extras.control.NumberField;

/**
 * Provides a CRUD demonstration using Form and FormTable.
 * <p/>
 * Note the following:
 *
 * #1. Form child controls are only processed on Form submission. In order to
 * process Form child controls when form is *not* submitted, Form.onProcess
 * must be overridden and the controls explicitly processed.
 *
 * #2. By default FormTable creates an internal Form for submissions. However
 * it is possible to use the FormTable constructor which accepts a Form so that
 * FormTable can be added to this "external" Form.
 *
 * 
 * @author Malcolm Edgar
 * @author Bob Schellink
 */
public class EditFormTablePage extends BorderPage {

    private static final int NUM_ROWS = 20;

    private FormTable table;

    private ActionLink deleteCustomer = new ActionLink("delete", "Delete", this, "onDeleteClick");
    
    private Form customerForm = new Form("customerForm");

    private Form form = new Form("form") {

        /**
         * #1. PLEASE NOTE: FormTable will only be processed by form if the
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
                deleteCustomer.onProcess();
                return table.onProcess();
            }
        }
    };

    // ------------------------------------------------------------ Constructor

    public EditFormTablePage() {
        // Setup customers form
        FieldSet fieldSet = new FieldSet("customer");
        fieldSet.add(new TextField("name")).setRequired(true);
        fieldSet.add(new EmailField("email")).setRequired(true);
        fieldSet.add(new InvestmentSelect("investments")).setRequired(true);
        fieldSet.add(new DoubleField("holdings"));
        DateField dateJoined = new DateField("dateJoined");
        dateJoined.setDate(new Date());
        fieldSet.add(dateJoined);
        customerForm.add(fieldSet);
        customerForm.add(new Submit("add", "Add Customer", this, "onAddClick"));

        // * #2. Create the FormTable and pass in the existing Form into the
        // constructor. FormTable now knows it should not create an internal
        // Form instance.
        table = new FormTable("table", form);

        // Assemble the FormTable columns
        table.setClass(Table.CLASS_SIMPLE);
        table.setWidth("700px");
        table.setPageSize(5);
        table.setShowBanner(true);

        table.addColumn(new Column("id"));

        FieldColumn column = new FieldColumn("name", new TextField());
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

        Column actionColumn = new Column("Action");
        actionColumn.setSortable(false);
        ActionLink[] links = new ActionLink[]{deleteCustomer};
        actionColumn.setDecorator(new LinkDecorator(table, links, "id"));
        table.addColumn(actionColumn);

        deleteCustomer.setAttribute("onclick", "return window.confirm('Please confirm delete');");

        table.getForm().add(new Submit("update", "Update Customers", this, "onUpdateCustomersClick"));
        table.getForm().add(new Submit("cancel", this, "onCancelClick"));

        table.setSortable(true);

        fieldSet = new FieldSet("customers");
        form.add(fieldSet);

        // Add FormTable to FieldSet which is attached to Form
        fieldSet.add(table);

        addControl(customerForm);
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

        refreshTableCustomers();
    }

    public boolean onUpdateCustomersClick() {
        if (form.isValid()) {
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

        refreshTableCustomers();

        table.setRenderSubmittedValues(false);

        form.clearErrors();

        return true;
    }

    public boolean onDeleteClick() {
        Integer id = deleteCustomer.getValueInteger();
        getCustomerService().deleteCustomer(id);

        // The FormTable customer were already set in the onInit phase. Because
        // a customer was deleted we refresh the FormTable row list
        refreshTableCustomers();

        return true;
    }

     public boolean onInsertClick() {
        Customer customer = new Customer();
        customer.setName("Alpha");
        customer.setDateJoined(new Date());
        getCustomerService().saveCustomer(customer);

        // The FormTable customer were already set in the onInit phase. Because
        // a customer was deleted we refresh the FormTable row list
        refreshTableCustomers();

        return true;
    }

    public boolean onAddClick() {
        if (customerForm.isValid()) {
            Customer customer = new Customer();
            customerForm.copyTo(customer);
            getCustomerService().saveCustomer(customer);

            // The FormTable customer was set in the onInit phase. Since we just
            // added a new customer we refresh the FormTable row list
            refreshTableCustomers();
        }
        return true;
    }
    
    private void refreshTableCustomers() {
        List allCustomers = getCustomerService().getCustomersSortedBy(Customer.DATE_JOINED_PROPERTY, false);
        List customers = allCustomers.subList(0, NUM_ROWS);
        table.setRowList(customers);
    }
}
