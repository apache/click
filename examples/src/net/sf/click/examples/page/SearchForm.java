package net.sf.click.examples.page;

import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.domain.Customer;

/**
 * Provides a search form example demonstrating how to layout a form manually
 * in the page template.
 *
 * @author Malcolm Edgar
 */
public class SearchForm extends BorderPage {

    private Form form;
    private TextField textField;
    private Select typeSelect;

    public SearchForm() {
        form = new Form("form");
        addControl(form);

        textField = new TextField("search");
        form.add(textField);

        typeSelect = new Select("type");
        typeSelect.addAll(new String[] {"ID", "Name", "Age"});
        typeSelect.setValue("Name");
        form.add(typeSelect);

        form.add(new Submit("go"));
    }

    /**
     * @see net.sf.click.Page#onPost()
     */
    public void onPost() {
        Customer customer = null;
        String value = textField.getValue().trim();
        String type = typeSelect.getValue().toLowerCase();

        if (type.equals("id")) {
            customer = getCustomerService().findCustomerByID(value);
        }
        else if (type.equals("name")) {
            List list = getCustomerService().findCustomersByName(value);
            if (!list.isEmpty()) {
                customer = (Customer) list.get(0);
            }
        }
        else if (type.equals("age")) {
            List list = getCustomerService().findCustomersByAge(value);
            if (!list.isEmpty()) {
                customer = (Customer) list.get(0);
            }
        }

        if (customer != null) {
            addModel("customerDetail", customer);
        }
        else {
            addModel("message", "Customer not found");
        }
    }
}
