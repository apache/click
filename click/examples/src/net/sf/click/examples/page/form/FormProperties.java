package net.sf.click.examples.page.form;

import java.io.Serializable;

import net.sf.click.control.Checkbox;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.control.InvestmentSelect;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.PageSubmit;

/**
 * Provides a example Page to demonstrate Form properties and layout options.
 *
 * @author Malcolm Edgar
 */
public class FormProperties extends BorderPage {

    /** Form options holder. */
    public static class Options implements Serializable {
        static final long serialVersionUID = 1L;
        String buttonAlign = Form.ALIGN_LEFT;
        String buttonStyle = "";
        int columns = 1;
        boolean disabled = false;
        String errorsAlign = Form.ALIGN_LEFT;
        String errorsPosition = Form.POSITION_MIDDLE;
        String errorsStyle = "";
        String fieldStyle = "";
        String labelAlign = Form.ALIGN_LEFT;
        String labelsPosition = Form.POSITION_LEFT;
        String labelStyle ="";
        boolean readonly = false;
        boolean showBorders = false;
        boolean validate = true;
        boolean javaScriptValidate = false;
    }

    /** Form values holder.*/
    public static class Values implements Serializable {
        static final long serialVersionUID = 1L;
        String name = "";
        String email = "";
        String investments = "";
        String dateJoined = "";
    }

    private Form form;
    private TextField nameField;
    private EmailField emailField;
    private InvestmentSelect investmentsField;
    private DateField dateJoinedField;

    private Form optionsForm;
    private Select buttonAlignSelect;
    private TextField buttonStyle;
    private Select errorsAlignSelect;
    private Select errorsPositionSelect;
    private TextField errorsStyle;
    private TextField fieldStyle;
    private Select labelAlignSelect;
    private Select labelsPositionSelect;
    private TextField labelStyle;
    private Select columnsSelect;
    private Checkbox showBordersCheckbox;
    private Checkbox disabledCheckbox;
    private Checkbox readonlyCheckbox;
    private Checkbox validateCheckbox;
    private Checkbox javaScriptValidateCheckbox;

    public FormProperties() {

        // Setup demonstration form

        form = new Form("form");

        FieldSet fieldSet = new FieldSet("demo", "<b>Demonstration Form</b>");
        form.add(fieldSet);

        nameField = new TextField("name");
        nameField.setRequired(true);
        nameField.setFocus(true);
        fieldSet.add(nameField);

        emailField = new EmailField("email");
        emailField.setRequired(true);
        fieldSet.add(emailField);

        investmentsField = new InvestmentSelect("investments");
        fieldSet.add(investmentsField);

        dateJoinedField = new DateField("dateJoined");
        fieldSet.add(dateJoinedField);

        form.add(new Submit("ok", "  OK  ", this, "onOkClick"));

        Submit cancel = new PageSubmit("cancel", HomePage.class);
        cancel.setCancelJavaScriptValidation(true);
        form.add(cancel);

        addControl(form);

        // Setup control form

        optionsForm = new Form("optionsForm");
        optionsForm.setColumns(3);
        optionsForm.setLabelAlign("right");
        optionsForm.setListener(this, "onApplyChanges");
        optionsForm.setLabelStyle("padding-left:2em;");

        fieldSet = new FieldSet("props", "<b>Form Properites</b>");
        optionsForm.add(fieldSet);

        buttonAlignSelect = new Select("buttonAlign");
        buttonAlignSelect.addAll(new String[] { "left", "center", "right" });
        buttonAlignSelect.setTitle("Buttons horizontal alignment");
        buttonAlignSelect.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(buttonAlignSelect);

        columnsSelect = new Select("columns");
        columnsSelect.addAll(new String[] { "1", "2", "3", "4" });
        columnsSelect.setTitle("Number of Form table columns");
        columnsSelect.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(columnsSelect);

        buttonStyle = new TextField("buttonStyle");
        buttonStyle.setTitle("Button td style attribute");
        buttonStyle.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(buttonStyle);

        errorsAlignSelect = new Select("errorsAlign");
        errorsAlignSelect.addAll(new String[] { "left", "center", "right" });
        errorsAlignSelect.setTitle("Errors block horizontal alignment");
        errorsAlignSelect.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(errorsAlignSelect);

        errorsPositionSelect = new Select("errorsPosition");
        errorsPositionSelect.addAll(new String[] { "top", "middle", "bottom" });
        errorsPositionSelect.setTitle("Form errors position");
        errorsPositionSelect.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(errorsPositionSelect);

        errorsStyle = new TextField("errorsStyle");
        errorsStyle.setTitle("Errors td style attribute");
        errorsStyle.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(errorsStyle);

        labelAlignSelect = new Select("labelAlign");
        labelAlignSelect.addAll(new String[] { "left", "center", "right" });
        labelAlignSelect.setTitle("Field label alignment");
        labelAlignSelect.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(labelAlignSelect);

        labelsPositionSelect = new Select("labelsPosition");
        labelsPositionSelect.addAll(new String[] {"left", "top"});
        labelsPositionSelect.setTitle("Form labels position");
        labelsPositionSelect.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(labelsPositionSelect);

        labelStyle = new TextField("labelStyle");
        labelStyle.setTitle("Label td style attribute");
        labelStyle.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(labelStyle);

        disabledCheckbox = new Checkbox("disabled");
        disabledCheckbox.setAttribute("onClick", "optionsForm.submit();");
        fieldSet.add(disabledCheckbox);

        readonlyCheckbox = new Checkbox("readonly");
        readonlyCheckbox.setAttribute("onClick", "optionsForm.submit();");
        fieldSet.add(readonlyCheckbox);

        fieldStyle = new TextField("fieldStyle");
        fieldStyle.setTitle("Field td style attribute");
        fieldStyle.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(fieldStyle);

        validateCheckbox = new Checkbox("validate");
        validateCheckbox.setAttribute("onClick", "optionsForm.submit();");
        fieldSet.add(validateCheckbox);

        javaScriptValidateCheckbox = new Checkbox("javaScriptValidate", "JavaScript Validate");
        javaScriptValidateCheckbox.setAttribute("onClick", "optionsForm.submit();");
        fieldSet.add(javaScriptValidateCheckbox);

        showBordersCheckbox = new Checkbox("showBorders");
        showBordersCheckbox.setAttribute("onClick", "optionsForm.submit();");
        fieldSet.add(showBordersCheckbox);

        optionsForm.add(new Submit("restoreDefaults", this, "onRestoreDefaults"));

        addControl(optionsForm);

        // Setup showBorders checkbox Javascript using HTML head include and
        // setting the body onload function.
        addModel("headInclude", "form-head.htm");
        addModel("bodyOnload", "toggleBorders(document.optionsForm.showBorders);");
    }

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        // Apply saved options to the demo form and the optionsForm
        Options options = (Options) getSessionObject(Options.class);
        applyOptions(options);
    }

    public boolean onOkClick() {
        Values values = (Values) getSessionObject(Values.class);

        values.name = nameField.getValue();
        values.email = emailField.getValue();
        values.investments = investmentsField.getValue();
        values.dateJoined = dateJoinedField.getValue();

        setSessionObject(values);

        return true;
    }

    public boolean onApplyChanges() {
        Options options = new Options();

        options.buttonAlign = buttonAlignSelect.getValue();
        options.buttonStyle = buttonStyle.getValue();
        options.columns = Integer.parseInt(columnsSelect.getValue());
        options.errorsAlign = errorsAlignSelect.getValue();
        options.errorsPosition = errorsPositionSelect.getValue();
        options.errorsStyle = errorsStyle.getValue();
        options.fieldStyle = fieldStyle.getValue();
        options.labelAlign = labelAlignSelect.getValue();
        options.labelsPosition = labelsPositionSelect.getValue();
        options.labelStyle = labelStyle.getValue();
        options.disabled = disabledCheckbox.isChecked();
        options.readonly = readonlyCheckbox.isChecked();
        options.validate = validateCheckbox.isChecked();
        options.javaScriptValidate = javaScriptValidateCheckbox.isChecked();
        options.showBorders = showBordersCheckbox.isChecked();

        applyOptions(options);

        setSessionObject(options);

        // Apply any saved form values to demo form.
        Values values = (Values) getSessionObject(Values.class);

        nameField.setValue(values.name);
        emailField.setValue(values.email);
        investmentsField.setValue(values.investments);
        dateJoinedField.setValue(values.dateJoined);

        return true;
    }

    public boolean onRestoreDefaults() {
        removeSessionObject(Options.class);
        removeSessionObject(Values.class);

        applyOptions(new Options());

        return true;
    }

    private void applyOptions(Options options) {
        form.setButtonAlign(options.buttonAlign);
        form.setButtonStyle(options.buttonStyle);
        form.setColumns(options.columns);
        form.setDisabled(options.disabled);
        form.setErrorsAlign(options.errorsAlign);
        form.setErrorsPosition(options.errorsPosition);
        form.setErrorsStyle(options.errorsStyle);
        form.setFieldStyle(options.fieldStyle);
        form.setLabelAlign(options.labelAlign);
        form.setLabelsPosition(options.labelsPosition);
        form.setLabelStyle(options.labelStyle);
        form.setReadonly(options.readonly);
        form.setValidate(options.validate);
        form.setJavaScriptValidation(options.javaScriptValidate);

        buttonAlignSelect.setValue(options.buttonAlign);
        buttonStyle.setValue(options.buttonStyle);
        columnsSelect.setValue(String.valueOf(options.columns));
        errorsAlignSelect.setValue(options.errorsAlign);
        errorsPositionSelect.setValue(options.errorsPosition);
        errorsStyle.setValue(options.errorsStyle);
        fieldStyle.setValue(options.fieldStyle);
        labelAlignSelect.setValue(options.labelAlign);
        labelsPositionSelect.setValue(options.labelsPosition);
        labelStyle.setValue(options.labelStyle);
        showBordersCheckbox.setChecked(options.showBorders);
        readonlyCheckbox.setChecked(options.readonly);
        disabledCheckbox.setChecked(options.disabled);
        validateCheckbox.setChecked(options.validate);
        javaScriptValidateCheckbox.setChecked(options.javaScriptValidate);
    }

}
