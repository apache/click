package examples.page;

import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.extras.control.CheckList;
import net.sf.click.extras.control.ColorPicker;
import net.sf.click.extras.control.CreditCardField;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;
import net.sf.click.extras.control.LongField;
import net.sf.click.extras.control.NumberField;
import net.sf.click.extras.control.RegexField;
import net.sf.click.extras.control.TelephoneField;
import examples.domain.CustomerDAO;

public class ExtraControlsForm extends BorderedPage {

    private Form form = new Form("form");
    private CheckList checkList = new CheckList("checkList");

    public ExtraControlsForm() {

        checkList.setHeight("5em");
        form.add(checkList);
        form.add(new CreditCardField("creditCardField"));
        form.add(new ColorPicker("colorPicker"));
        form.add(new DateField("dateField"));
        form.add(new DoubleField("doubleField"));
        form.add(new EmailField("emailField"));
        form.add(new IntegerField("integerField"));
        form.add(new LongField("longField"));
        form.add(new NumberField("numberField"));
        form.add(new RegexField("regexField"));
        form.add(new TelephoneField("telephoneField"));

        form.add(new Submit("submit"));

        addControl(form);
    }

    public void onInit() {
        List customers = CustomerDAO.getCustomersSortedByName();
        checkList.addAll(customers, "id", "name");
    }

}
