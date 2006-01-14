package examples.page.table;

import java.util.Iterator;
import java.util.List;

import net.sf.click.Context;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Column;
import net.sf.click.control.Decorator;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.Table;
import net.sf.click.control.TextField;
import examples.control.InvestmentSelect;
import examples.domain.Customer;
import examples.domain.CustomerDAO;
import examples.page.BorderedPage;

/**
 * Demonstration of a Table containing Form Fields.
 *
 * @author Christian Essl
 */
public class TableForm extends BorderedPage {

    private Form form = new Form("form");
    private Table formTable = new Table("formTable");
    private Table dataTable = new Table("dataTable");

    public TableForm() {

        // Populate the form

        List customers = getCustomers();
        for (Iterator it = customers.iterator(); it.hasNext();) {
            Customer cust = (Customer) it.next();
            Long id = cust.getId();

            // Stores the id
            HiddenField idField = new HiddenField("id-" + id, Long.class);
            idField.setValueObject(cust.getId());
            form.add(idField);

            // The name
            TextField name = new TextField("name-" + id, true);
            name.setValue(cust.getName());
            form.add(name);

            // The investment
            Select investment = new InvestmentSelect("investment-" + id);
            investment.setRequired(true);
            investment.setValue(cust.getInvestments());
            form.add(investment);

            // Active or not
            Checkbox active = new Checkbox("active-" + id);
            active.setTitle("");
            active.setLabel("");
            active.setValueObject(cust.getActive());
            form.add(active);
        }

        form.add(new Submit("okSubmit", "Save", this, "onOkClick"));
        form.add(new Submit("cancelSubmit", "Cancel", this, "onCancelClick"));

        addControl(form);

        // Polulate the formTable

        formTable.setAttribute("class", "simple");

        // The id with hidden field
        Column column = new Column("id");
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                Long id = ((Customer) row).getId();
                return form.getField("id-" + id).toString() + id;
            }
        });
        formTable.addColumn(column);

        // The name
        column = new Column("name");
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                Long id = ((Customer) row).getId();
                return form.getField("name-" + id).toString();
            }
        });
        formTable.addColumn(column);

        // The investments select
        column = new Column("investments");
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                Long id = ((Customer) row).getId();
                return form.getField("investment-" + id).toString();
            }
        });
        formTable.addColumn(column);

        // The active checkbox
        column = new Column("active");
        column.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                Long id = ((Customer) row).getId();
                return form.getField("active-" + id).toString();

            }
        });
        formTable.addColumn(column);

        addControl(formTable);

        // A table which just shows the data

        dataTable.setAttribute("class", "simple");

        dataTable.addColumn(new Column("id"));
        dataTable.addColumn(new Column("name"));
        dataTable.addColumn(new Column("investments"));
        dataTable.addColumn(new Column("active"));

        addControl(dataTable);
    }

    public boolean onOkClick() {
        if (form.isValid()) {
            tableRowsIterator(new RowCallback() {
                public void process(Customer customer) {
                    TextField name =
                        (TextField) form.getField("name-" + customer.getId());
                    customer.setName(name.getValue());

                    Select investment =
                        (Select) form.getField("investment-" + customer.getId());
                    customer.setInvestments(investment.getValue());

                    Checkbox active =
                        (Checkbox) form.getField("active-" + customer.getId());
                    customer.setActive((Boolean)active.getValueObject());
                }
            });
        }

        formTable.setRowList(getCustomers());

        return true;
    }

    public boolean onCancelClick() {
        tableRowsIterator(new RowCallback() {
            public void process(Customer customer) {
                TextField name =
                    (TextField) form.getField("name-" + customer.getId());
                name.setValue(customer.getName());

                Select investment =
                    (Select) form.getField("investment-" + customer.getId());
                investment.setValue(customer.getInvestments());

                Checkbox active =
                    (Checkbox) form.getField("active-" + customer.getId());
                active.setValueObject(customer.getActive());
            }
        });

        formTable.setRowList(getCustomers());

        return true;
    }

    /**
     * @see net.sf.click.Page#onGet()
     */
    public void onGet() {
        List customers = getCustomers();
        formTable.setRowList(customers);
        dataTable.setRowList(customers);
    }

    /**
     * @see net.sf.click.Page#onPost()
     */
    public void onPost() {
        dataTable.setRowList(getCustomers());
    }

    // -------------------------------------------------------- Private Methods

    private List getCustomers() {
        return CustomerDAO.getCustomersSortedByName(6);
    }

    private void tableRowsIterator(RowCallback rowCallback) {
        for (Iterator i = form.getFieldList().iterator(); i.hasNext();) {
            Field field = (Field) i.next();
            if (field instanceof HiddenField) {
                HiddenField hiddenField = (HiddenField) field;

                if (hiddenField.getName().startsWith("id-")) {
                    Long id = (Long) hiddenField.getValueObject();
                    Customer customer = CustomerDAO.getCustomer(id);

                    if (customer != null) {
                        rowCallback.process(customer);
                    }
                }
            }
        }
    }

    private static interface RowCallback {
        public void process(Customer customer);
    }

}
