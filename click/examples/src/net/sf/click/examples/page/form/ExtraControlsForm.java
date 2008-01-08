package net.sf.click.examples.page.form;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Checkbox;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.CheckList;
import net.sf.click.extras.control.ColorPicker;
import net.sf.click.extras.control.CreditCardField;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;
import net.sf.click.extras.control.LongField;
import net.sf.click.extras.control.NumberField;
import net.sf.click.extras.control.PageSubmit;
import net.sf.click.extras.control.RegexField;
import net.sf.click.extras.control.TelephoneField;
import net.sf.click.util.ClickUtils;

/**
 * Provides a form containing all the Click Extras Controls.
 *
 * @author Malcolm Edgar
 */
public class ExtraControlsForm extends BorderPage {

    /** Form options holder. */
    public static class Options implements Serializable {
        static final long serialVersionUID = 1L;
        boolean allFieldsRequired = false;
        boolean javaScriptValidate = false;
    }

    public Form form = new Form();
    public Form optionsForm = new Form();

    private CheckList checkList = new CheckList("checkList");
    private Checkbox allFieldsRequired = new Checkbox("allFieldsRequired");
    private Checkbox jsValidate = new Checkbox("jsValidate", "JavaScript Validate");

    // ------------------------------------------------------------ Constructor

    public ExtraControlsForm() {
        form.setErrorsPosition(Form.POSITION_TOP);
        form.setColumns(2);

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
        form.add(new PageSubmit("cancel", HomePage.class));

        // Settings Form
        FieldSet fieldSet = new FieldSet("options", "Form Options");
        allFieldsRequired.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(allFieldsRequired);
        jsValidate.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(jsValidate);
        optionsForm.add(fieldSet);
        optionsForm.setListener(this, "onOptionsSubmit");
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        super.onInit();

        List customers = getCustomerService().getCustomersSortedByName();
        checkList.addAll(customers, "id", "name");
        applyOptions();
    }

    public boolean onOptionsSubmit() {
        Options options = new Options();
        options.allFieldsRequired = allFieldsRequired.isChecked();
        options.javaScriptValidate = jsValidate.isChecked();
        setSessionObject(options);
        applyOptions();
        return true;
    }

    // -------------------------------------------------------- Private Methods

    private void applyOptions() {
        Options options = (Options) getSessionObject(Options.class);

        form.setJavaScriptValidation(options.javaScriptValidate);
        List formFiels = ClickUtils.getFormFields(form);
        for (Iterator i = formFiels.iterator(); i.hasNext();) {
            Field field = (Field) i.next();
            field.setRequired(options.allFieldsRequired);
        }

        allFieldsRequired.setChecked(options.allFieldsRequired);
        jsValidate.setChecked(options.javaScriptValidate);
    }

}
