package examples.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.click.control.Checkbox;
import net.sf.click.control.DateField;
import net.sf.click.control.DoubleField;
import net.sf.click.control.EmailField;
import net.sf.click.control.Form;
import net.sf.click.control.Label;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import examples.control.InvestmentSelect;

/**
 * Provides a example Page to demonstrate Form properties and layout options.
 *
 * @author Malcolm Edgar
 */
public class FormProperties extends BorderedPage {

    static final List ALIGN_OPTIONS = new ArrayList();
    static {
        ALIGN_OPTIONS.add(new Select.Option("left", "Left"));
        ALIGN_OPTIONS.add(new Select.Option("center", "Center"));
        ALIGN_OPTIONS.add(new Select.Option("right", "Right"));
    }

    static final List POSITION_OPTIONS = new ArrayList();
    static {
        POSITION_OPTIONS.add(new Select.Option("top", "Top"));
        POSITION_OPTIONS.add(new Select.Option("middle", "Middle"));
        POSITION_OPTIONS.add(new Select.Option("bottom", "Buttom"));
    }
    
    /** Form options holder. */
    static class Options implements Serializable {
        String buttonAlign = Form.LEFT;
        int columns = 1;
        boolean disabled = false;
        String errorsAlign = Form.LEFT;
        String errorsPosition = Form.MIDDLE;
        String labelAlign = Form.LEFT;
        String labelsPosition = Form.LEFT;
        boolean readonly = false;
        boolean showBorders = false;
        boolean validate = true;
    }
    
    /** Form values holder.*/
    static class Values implements Serializable {
        String name;
        String email;
        String holdings;
        String investments;
        String dateJoined;
    }
    
    Form form;
    Form optionsForm;
    
    Select buttonAlignSelect;
    Select errorsAlignSelect;
    Select errorsPositionSelect;
    Select labelAlignSelect;
    Select labelsPositionSelect;
    Select columnsSelect;
    Checkbox showBordersCheckbox;
    Checkbox disabledCheckbox;
    Checkbox readonlyCheckbox;
    Checkbox validateCheckbox;

    public void onInit() {
        // Setup demonstration form.
        form = new Form("form", getContext());
        addControl(form);

        TextField nameField = new TextField("Name");
        nameField.setMinLength(5);
        nameField.setTitle("Customer full name");
        nameField.setFocus(true);
        form.add(nameField);

        EmailField emailField = new EmailField("Email");
        emailField.setRequired(true);
        emailField.setTitle("Customers email address");
        form.add(emailField);

        DoubleField holdingsField = new DoubleField("Holdings");
        holdingsField.setTitle("Total investment holdings");
        form.add(holdingsField);

        InvestmentSelect investmentsField = new InvestmentSelect("Investments");
        form.add(investmentsField);

        DateField dateJoinedField = new DateField("Date Joined");
        dateJoinedField.setTitle("Date customer joined fund");
        dateJoinedField.setRequired(true);
        form.add(dateJoinedField);

        form.add(new Submit("    OK    "));

        Submit cancelButton = new Submit(" Cancel ");
        cancelButton.setListener(this, "onCancelClick");
        form.add(cancelButton);
        
        // Apply saved options to the form
        Options options = getOptions();
        form.setButtonAlign(options.buttonAlign);
        form.setColumns(options.columns);
        form.setDisabled(options.disabled);
        form.setErrorsAlign(options.errorsAlign);
        form.setErrorsPosition(options.errorsPosition);
        form.setLabelAlign(options.labelAlign);
        form.setLabelsPosition(options.labelsPosition);
        form.setReadonly(options.readonly);
        form.setValidate(options.validate);

        // Setup control form
        optionsForm = new Form("optionsForm", getContext());
        optionsForm.setColumns(2);
        optionsForm.setLabelAlign("right");
        addControl(optionsForm);
        
        buttonAlignSelect = new Select("Button Align");
        buttonAlignSelect.addAll(ALIGN_OPTIONS);
        buttonAlignSelect.setTitle("Buttons horizontal alignment");
        optionsForm.add(buttonAlignSelect);
        
        disabledCheckbox = new Checkbox("Disabled");
        optionsForm.add(disabledCheckbox);

        columnsSelect = new Select("Columns");
        columnsSelect.addAll(new String[] { "1", "2", "3", "4", "5" });
        columnsSelect.setTitle("Form columns");
        optionsForm.add(columnsSelect);
        
        readonlyCheckbox = new Checkbox("Readonly");
        optionsForm.add(readonlyCheckbox);
        
        errorsAlignSelect = new Select("Errors Align");
        errorsAlignSelect.addAll(ALIGN_OPTIONS);
        errorsAlignSelect.setTitle("Errors block horizontal alignment");
        optionsForm.add(errorsAlignSelect);

        showBordersCheckbox = new Checkbox("Show Borders");
        optionsForm.add(showBordersCheckbox);
        
        errorsPositionSelect = new Select("Errors Position");
        errorsPositionSelect.addAll(POSITION_OPTIONS);
        errorsPositionSelect.setTitle("Form errors position");
        optionsForm.add(errorsPositionSelect);

        validateCheckbox = new Checkbox("Validate");
        optionsForm.add(validateCheckbox);
        
        labelAlignSelect = new Select("Label Align");
        labelAlignSelect.addAll(ALIGN_OPTIONS);
        labelAlignSelect.setTitle("Field label alignment");
        optionsForm.add(labelAlignSelect);
        
        optionsForm.add(new Label(""));
        
        labelsPositionSelect = new Select("Labels Position");
        labelsPositionSelect.add(new Select.Option("left", "Left"));
        labelsPositionSelect.add(new Select.Option("top", "Top"));
        labelsPositionSelect.setTitle("Form labels position");
        optionsForm.add(labelsPositionSelect);

        Submit applyButton = new Submit("   Apply  ");
        applyButton.setTitle("Apply the layout to the form");
        applyButton.setListener(this, "onApplyClick");
        optionsForm.add(applyButton);
        
        Submit resetButton = new Submit("   Reset  ");
        resetButton.setTitle("Restore default form properties");
        resetButton.setListener(this, "onResetClick");
        optionsForm.add(resetButton);

        // Setup checkbox Javascript
        addModel("javascript-include", "form-properties.js");
        addModel("body-onload", "toggleBorders(document.optionsForm.showBorders);");
        
        // Apply any saved options to optionsForm
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
    
    public boolean onOkClick() {
        // TODO: save submitted values
        return true;
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }

    public boolean onApplyClick() {
        Options options = getOptions();
        
        options.buttonAlign = buttonAlignSelect.getValue();
        form.setButtonAlign(options.buttonAlign);
        
        options.columns = Integer.parseInt(columnsSelect.getValue());
        form.setColumns(options.columns);
        
        options.errorsAlign = errorsAlignSelect.getValue();
        form.setErrorsAlign(options.errorsAlign);
        
        options.errorsPosition = errorsPositionSelect.getValue();
        form.setErrorsPosition(options.errorsPosition);
        
        options.labelAlign = labelAlignSelect.getValue();
        form.setLabelAlign(options.labelAlign);
        
        options.labelsPosition = labelsPositionSelect.getValue();
        form.setLabelsPosition(options.labelsPosition);
        
        options.disabled = disabledCheckbox.isChecked();
        form.setDisabled(options.disabled);
        
        options.readonly = readonlyCheckbox.isChecked();
        form.setReadonly(options.readonly);
        
        options.validate = validateCheckbox.isChecked();
        form.setValidate(options.validate);
        
        options.showBorders = showBordersCheckbox.isChecked();
        
        getContext().setSessionAttribute("options", options);

        return true;
    }
    
    public boolean onResetClick() {
        Options options = new Options();
        
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
        
        getContext().setSessionAttribute("options", options);
        
        return true;
    }
    
    private Options getOptions() {
        Options options = (Options) getContext().getSessionAttribute("options");
        if (options == null) {
            options = new Options();
        }
        return options;
    }

}
