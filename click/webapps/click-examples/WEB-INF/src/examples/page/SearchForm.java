package examples.page;

import net.sf.click.control.Form;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import examples.domain.Customer;
import examples.domain.CustomerDAO;

/**
 * Provides a search form example demonstrating how to layout a form manually
 * in the page template.
 *
 * @author Malcolm Edgar
 */
public class SearchForm extends BorderedPage {

    TextField textField;
    Select typeSelect;

    public void onInit() {
        Form form = new Form("form");
        addControl(form);

        textField = new TextField("Search");
        textField.setTitle("Enter customer info");
        form.add(textField);

        typeSelect = new Select("type");
        typeSelect.addAll(new String[] {"ID", "Name", "Age"});
        typeSelect.setValue("Name");
        typeSelect.setTitle("Type of search");
        form.add(typeSelect);

        Submit submit = new Submit(" Go ");
        submit.setTitle("Click Go to search");
        form.add(submit);
    }

    public void onPost() {
        Customer customer = null;
        String value = textField.getValue().trim();
        String type = typeSelect.getValue().toLowerCase();

        if (type.equals("id")) {
            customer = CustomerDAO.findCustomerByID(value);
        }
        else if (type.equals("name")) {
            customer = CustomerDAO.findCustomerByName(value);
        }
        else if (type.equals("age")) {
            customer = CustomerDAO.findCustomerByAge(value);
        }

        if (customer != null) {
            addModel("customerDetail", customer);
        }
        else {
            addModel("message", "Customer not found");
        }
    }
}
