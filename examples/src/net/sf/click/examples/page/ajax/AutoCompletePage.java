package net.sf.click.examples.page.ajax;

import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.AutoCompleteTextField;

public class AutoCompletePage extends BorderPage {

    public Form form = new Form();

    private TextField nameField;

    public AutoCompletePage() {
        nameField = new AutoCompleteTextField("name", true) {
            public List getAutoCompleteList(String criteria) {
                return getCustomerService().getCustomerNamesLike(criteria);
            }
        };

        form.add(nameField);

        form.add(new Submit(" OK "));
    }

    public boolean onOkClick() {

        return true;
    }

}
