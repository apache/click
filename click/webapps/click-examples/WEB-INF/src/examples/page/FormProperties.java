package examples.page;

import java.io.Serializable;

import net.sf.click.control.Checkbox;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Label;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.EmailField;
import examples.control.InvestmentSelect;

/**
 * Provides a example Page to demonstrate Form properties and layout options.
 *
 * @author Malcolm Edgar
 */
public class FormProperties extends BorderedPage {

    /** Form options holder. */
    public static class Options implements Serializable {
        static final long serialVersionUID = 1L;
        String buttonAlign = Form.ALIGN_LEFT;
        int columns = 1;
        boolean disabled = false;
        String errorsAlign = Form.ALIGN_LEFT;
        String errorsPosition = Form.POSITION_MIDDLE;
        String labelAlign = Form.ALIGN_LEFT;
        String labelsPosition = Form.POSITION_LEFT;
        boolean readonly = false;
        boolean showBorders = false;
        boolean validate = true;
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
    private Select errorsAlignSelect;
    private Select errorsPositionSelect;
    private Select labelAlignSelect;
    private Select labelsPositionSelect;
    private Select columnsSelect;
    private Checkbox showBordersCheckbox;
    private Checkbox disabledCheckbox;
    private Checkbox readonlyCheckbox;
    private Checkbox validateCheckbox;

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

        form.add(new Submit("ok", "    OK    ", this, "onOkClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));

        addControl(form);

        // Setup control form

        optionsForm = new Form("optionsForm");
        optionsForm.setColumns(2);
        optionsForm.setLabelAlign("right");
        optionsForm.setListener(this, "onApplyChanges");

        fieldSet = new FieldSet("props", "<b>Form Properites</b>");
        optionsForm.add(fieldSet);

        buttonAlignSelect = new Select("buttonAlign");
        buttonAlignSelect.addAll(new String[] { "left", "center", "right" });
        buttonAlignSelect.setTitle("Buttons horizontal alignment");
        buttonAlignSelect.setAttribute("onChange", "optionsForm.submit();");
        fieldSet.add(buttonAlignSelect);

        showBordersCheckbox = new Checkbox("showBorders");
        showBordersCheckbox.setAttribute("onClick", "optionsForm.submit();");
        fieldSet.add(showBordersCheckbox);

        columnsSelect = new Select("columns");
        columnsSelect.addAll(new String[] { "1", "2", "3", "4" });
        columnsSelect.setTitle("Number of Form table columns");
        columnsSelect.setAttribute("onChange", "optionsForm.submit();");
        fieldSet.add(columnsSelect);

        disabledCheckbox = new Checkbox("disabled");
        disabledCheckbox.setAttribute("onClick", "optionsForm.submit();");
        fieldSet.add(disabledCheckbox);

        errorsAlignSelect = new Select("errorsAlign");
        errorsAlignSelect.addAll(new String[] { "left", "center", "right" });
        errorsAlignSelect.setTitle("Errors block horizontal alignment");
        errorsAlignSelect.setAttribute("onChange", "optionsForm.submit();");
        fieldSet.add(errorsAlignSelect);

        readonlyCheckbox = new Checkbox("readonly");
        readonlyCheckbox.setAttribute("onClick", "optionsForm.submit();");
        fieldSet.add(readonlyCheckbox);

        errorsPositionSelect = new Select("errorsPosition");
        errorsPositionSelect.addAll(new String[] { "top", "middle", "bottom" });
        errorsPositionSelect.setTitle("Form errors position");
        errorsPositionSelect.setAttribute("onChange", "optionsForm.submit();");
        fieldSet.add(errorsPositionSelect);

        validateCheckbox = new Checkbox("validate");
        validateCheckbox.setAttribute("onClick", "optionsForm.submit();");
        fieldSet.add(validateCheckbox);

        labelAlignSelect = new Select("labelAlign");
        labelAlignSelect.addAll(new String[] { "left", "center", "right" });
        labelAlignSelect.setTitle("Field label alignment");
        labelAlignSelect.setAttribute("onChange", "optionsForm.submit();");
        fieldSet.add(labelAlignSelect);

        fieldSet.add(new Label("&nbsp;"));

        labelsPositionSelect = new Select("labelsPosition");
        labelsPositionSelect.addAll(new String[] {"left", "top"});
        labelsPositionSelect.setTitle("Form labels position");
        labelsPositionSelect.setAttribute("onChange", "optionsForm.submit();");
        fieldSet.add(labelsPositionSelect);

        optionsForm.add(new Submit("restoreDefaults", this, "onRestoreDefaults"));

        addControl(optionsForm);

        // Setup showBorders checkbox Javascript using HTML head include and
        // setting the body onload function.
        addModel("head-include", "form-head.htm");
        addModel("body-onload", "toggleBorders(document.optionsForm.showBorders);");
    }

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        // Apply saved options to the demo form and the optionsForm
        Options options = (Options) getContext().getSessionObject(Options.class);
        applyOptions(options);
    }

    public boolean onOkClick() {
        Values values = (Values) getContext().getSessionObject(Values.class);

        values.name = nameField.getValue();
        values.email = emailField.getValue();
        values.investments = investmentsField.getValue();
        values.dateJoined = dateJoinedField.getValue();

        getContext().setSessionObject(values);

        return true;
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }

    public boolean onApplyChanges() {
        Options options = new Options();

        options.buttonAlign = buttonAlignSelect.getValue();
        options.columns = Integer.parseInt(columnsSelect.getValue());
        options.errorsAlign = errorsAlignSelect.getValue();
        options.errorsPosition = errorsPositionSelect.getValue();
        options.labelAlign = labelAlignSelect.getValue();
        options.labelsPosition = labelsPositionSelect.getValue();
        options.disabled = disabledCheckbox.isChecked();
        options.readonly = readonlyCheckbox.isChecked();
        options.validate = validateCheckbox.isChecked();
        options.showBorders = showBordersCheckbox.isChecked();

        applyOptions(options);

        getContext().setSessionObject(options);

        // Apply any saved form values to demo form.
        Values values = (Values) getContext().getSessionObject(Values.class);

        nameField.setValue(values.name);
        emailField.setValue(values.email);
        investmentsField.setValue(values.investments);
        dateJoinedField.setValue(values.dateJoined);

        return true;
    }

    public boolean onRestoreDefaults() {
        getContext().removeSessionObject(Options.class);
        getContext().removeSessionObject(Values.class);

        applyOptions(new Options());

        return true;
    }

    private void applyOptions(Options options) {
        form.setButtonAlign(options.buttonAlign);
        form.setColumns(options.columns);
        form.setDisabled(options.disabled);
        form.setErrorsAlign(options.errorsAlign);
        form.setErrorsPosition(options.errorsPosition);
        form.setLabelAlign(options.labelAlign);
        form.setLabelsPosition(options.labelsPosition);
        form.setReadonly(options.readonly);
        form.setValidate(options.validate);

        buttonAlignSelect.setValue(options.buttonAlign);
        columnsSelect.setValue(String.valueOf(options.columns));
        errorsAlignSelect.setValue(options.errorsAlign);
        errorsPositionSelect.setValue(options.errorsPosition);
        labelAlignSelect.setValue(options.labelAlign);
        labelsPositionSelect.setValue(options.labelsPosition);
        showBordersCheckbox.setChecked(options.showBorders);
        readonlyCheckbox.setChecked(options.readonly);
        disabledCheckbox.setChecked(options.disabled);
        validateCheckbox.setChecked(options.validate);
    }
}
