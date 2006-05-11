package examples.page;

import java.util.List;

import examples.domain.CustomerDAO;
import net.sf.click.control.Button;
import net.sf.click.control.Checkbox;
import net.sf.click.control.FieldSet;
import net.sf.click.control.FileField;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.ImageSubmit;
import net.sf.click.control.Label;
import net.sf.click.control.PasswordField;
import net.sf.click.control.Radio;
import net.sf.click.control.RadioGroup;
import net.sf.click.control.Reset;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;

public class StandardControlsForm extends BorderedPage {

    private Form form = new Form("form");
    private Select select = new Select("select");

    public StandardControlsForm() {

        FieldSet fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        fieldSet.add(new Checkbox("checkbox"));
        fieldSet.add(new FileField("fileField"));
        fieldSet.add(new HiddenField("hiddenField", String.class));
        fieldSet.add(new Label("label"));
        fieldSet.add(new PasswordField("passwordField"));
        fieldSet.add(new Radio("radio", "Radio", "radio"));
        RadioGroup radioGroup = new RadioGroup("radioGroup");
        radioGroup.add(new Radio("A"));
        radioGroup.add(new Radio("B"));
        radioGroup.add(new Radio("C"));
        fieldSet.add(radioGroup);
        fieldSet.add(select);
        fieldSet.add(new TextArea("textArea"));
        fieldSet.add(new TextField("textField"));

        form.add(new Button("button"));
        form.add(new ImageSubmit("image", "images/edit-button.gif"));
        form.add(new Reset("reset"));
        form.add(new Submit("submit"));

        addControl(form);
    }

    public void onInit() {
        List customers = CustomerDAO.getCustomersSortedByName();
        select.addAll(customers, "id", "name");
    }

}
