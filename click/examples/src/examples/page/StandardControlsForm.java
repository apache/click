package examples.page;

import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Button;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.FileField;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.ImageSubmit;
import net.sf.click.control.Label;
import net.sf.click.control.Option;
import net.sf.click.control.PasswordField;
import net.sf.click.control.Radio;
import net.sf.click.control.RadioGroup;
import net.sf.click.control.Reset;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import examples.domain.CustomerDAO;

/**
 * Provides a form containing all the Standard Click Controls.
 *
 * @author Malcolm Edgar
 */
public class StandardControlsForm extends BorderedPage {

    private Form form = new Form("form");
    private FieldSet fieldSet = new FieldSet("fieldSet");
    private Select select = new Select("select");
    private Checkbox allFieldsRequired = new Checkbox("allFieldsRequired");
    private Checkbox jsValidate = new Checkbox("jsValidate", "JavaScript Validate");

    public StandardControlsForm() {

        // Controls FieldSet
        form.add(fieldSet);

        fieldSet.add(new Checkbox("checkbox"));
        fieldSet.add(new FileField("fileField"));
        fieldSet.add(new HiddenField("hiddenField", String.class));
        String labelText = "<span style='color:blue;font-style:italic'>Label - note how label text spans both columns</span>";
        fieldSet.add(new Label("label", labelText));
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
        ImageSubmit imageSubmit = new ImageSubmit("image", "images/edit-button.gif");
        imageSubmit.setTitle("ImageSubmit");
        form.add(imageSubmit);
        form.add(new Reset("reset"));
        form.add(new Submit("submit"));

        // Settings FieldSet
        FieldSet settingsFieldSet = new FieldSet("Settings");
        settingsFieldSet.add(allFieldsRequired);
        settingsFieldSet.add(jsValidate);
        form.add(settingsFieldSet);

        form.setErrorsPosition(Form.POSITION_TOP);

        addControl(form);
    }

    public void onInit() {
        List customers = CustomerDAO.getCustomersSortedByName();
        select.add(new Option("[Select]"));
        select.addAll(customers, "id", "name");

        applySettings();
    }

    /**
     * Apply the settings to the form and its fields before it is processed.
     */
    private void applySettings() {
        allFieldsRequired.setContext(getContext());
        allFieldsRequired.onProcess();
        for (Iterator i = fieldSet.getFieldList().iterator(); i.hasNext();) {
            Field field = (Field) i.next();
            field.setRequired(allFieldsRequired.isChecked());
        }

        jsValidate.setContext(getContext());
        jsValidate.onProcess();
        form.setJavaScriptValidation(jsValidate.isChecked());
    }

}
