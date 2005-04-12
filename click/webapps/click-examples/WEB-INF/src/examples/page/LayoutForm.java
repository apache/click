package examples.page;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;

/**
 * Provides a example Page to demonstrate Form layout options.
 *
 * @author Malcolm Edgar
 */
public class LayoutForm extends EditCustomer {

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

    static final String[] COLUMNS = { "1", "2", "3", "4" };

    HiddenField errorsPositionHidden = new HiddenField("errorsPosition", Integer.class);
    HiddenField labelsPositionHidden = new HiddenField("labelsPosition", Integer.class);
    HiddenField labelAlignHidden= new HiddenField("labelAlign", String.class);
    HiddenField columnsHidden = new HiddenField("columns", Integer.class);

    Form styleForm;
    Select labelsPositionSelect;
    Select errorsPositionSelect;
    Select labelAlignSelect;
    Select columnsSelect;

    public void onInit() {
        super.onInit();

        // Add hidden form style field
        errorsPositionHidden.setValue(new Integer(Form.MIDDLE));
        form.add(errorsPositionHidden);
        labelsPositionHidden.setValue(new Integer(Form.LEFT));
        form.add(labelsPositionHidden);
        labelAlignHidden.setValue("left");
        form.add(labelAlignHidden);
        columnsHidden.setValue(new Integer(1));
        form.add(columnsHidden);

        // Unset EditCustomer.onOkClick() listener
        okButton.setListener(null, null);
        emailField.setRequired(true);

        // Add style form to modify the original forms layout
        styleForm = new Form("styleForm", getContext());
        addControl(styleForm);

        labelAlignSelect = new Select("Label Align");
        labelAlignSelect.addAll(ALIGN_OPTIONS);
        labelAlignSelect.setTitle("Field label alignment");
        labelAlignSelect.setValue("left");
        styleForm.add(labelAlignSelect);

        labelsPositionSelect = new Select("Labels Position");
        labelsPositionSelect.add(new Select.Option("left", "Label on Left"));
        labelsPositionSelect.add(new Select.Option("top", "Label on Top"));
        labelsPositionSelect.setTitle("Form labels position");
        styleForm.add(labelsPositionSelect);

        errorsPositionSelect = new Select("Errors Position");
        errorsPositionSelect.addAll(POSITION_OPTIONS);
        errorsPositionSelect.setTitle("Form errors position");
        errorsPositionSelect.setValue("middle");
        styleForm.add(errorsPositionSelect);

        columnsSelect = new Select("Columns");
        columnsSelect.addAll(COLUMNS);
        columnsSelect.setTitle("Form columns");
        columnsSelect.setValue("1");
        styleForm.add(columnsSelect);

        Submit applyButton = new Submit("   Apply Layout   ");
        applyButton.setTitle("Apply the layout to the form");
        applyButton.setListener(this, "onApplyClick");
        styleForm.add(applyButton);
    }

    /**
     * Apply the hidden field form styles to the form and the style display
     * controls.
     */
    public void onPost() {
        Integer errorsPosition = (Integer) errorsPositionHidden.getValueObject();
        Integer labelsPosition = (Integer) labelsPositionHidden.getValueObject();
        Integer columns = (Integer) columnsHidden.getValueObject();

        form.setErrorsPosition(errorsPosition.intValue());
        form.setLabelsPosition(labelsPosition.intValue());
        form.setLabelAlign(labelAlignHidden.getValue());
        form.setColumns(columns.intValue());

        if (errorsPosition.intValue() == Form.TOP) {
            errorsPositionSelect.setValue("top");
        } else if (errorsPosition.intValue() == Form.MIDDLE) {
            errorsPositionSelect.setValue("middle");
        } else {
            errorsPositionSelect.setValue("bottom");
        }

        if (labelsPosition.intValue() == Form.LEFT) {
            labelsPositionSelect.setValue("left");
        } else {
            labelsPositionSelect.setValue("top");
        }
        labelAlignSelect.setValue(labelAlignHidden.getValue());
        columnsSelect.setValue(String.valueOf(columns.intValue()));
    }

    /**
     * Apply the layout to the form.
     *
     * @return true
     */
    public boolean onApplyClick() {
        if (errorsPositionSelect.getValue().equals("top")) {
            errorsPositionHidden.setValue(new Integer(Form.TOP));

        } else  if (errorsPositionSelect.getValue().equals("middle")) {
            errorsPositionHidden.setValue(new Integer(Form.MIDDLE));

        } else {
            errorsPositionHidden.setValue(new Integer(Form.BOTTOM));
        }

        if (labelsPositionSelect.getValue().equals("left")) {
            labelsPositionHidden.setValue(new Integer(Form.LEFT));

        } else {
            labelsPositionHidden.setValue(new Integer(Form.TOP));
        }

        labelAlignHidden.setValue(labelAlignSelect.getValue());
        columnsHidden.setValue(Integer.valueOf(columnsSelect.getValue()));

        return true;
    }

    /**
     * On a POST Cancel button submit redirect to "examples.html"
     * <p/>
     * Override <tt>EditCustomer.onCancelClick()</tt> method
     *
     * @return false to stop processing
     */
    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}
