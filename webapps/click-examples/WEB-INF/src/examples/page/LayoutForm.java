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
    
    HiddenField labelsPositionHidden = new HiddenField("formLayout", Integer.class);
    HiddenField labelAlignHidden= new HiddenField("labelAlign", String.class);
    
    Form styleForm;
    Select labelsPositionSelect;
    Select labelAlignSelect;

    public void onInit() {
        super.onInit();
        
        // Add hidden form style field
        labelsPositionHidden.setValue(new Integer(Form.LEFT));
        form.add(labelsPositionHidden);
        labelAlignHidden.setValue("left");
        form.add(labelAlignHidden);
        
        // Unset EditCustomer.onOkClick() listener
        okButton.setListener(null, null);
        emailField.setRequired(true);
        
        // Add style form to modify the original forms layout
        styleForm = new Form("styleForm", getContext());
        addControl(styleForm);
        
        labelsPositionSelect = new Select("Labels Position");
        labelsPositionSelect.add(new Select.Option("left", "Label on Left"));
        labelsPositionSelect.add(new Select.Option("top", "Label on Top"));
        styleForm.add(labelsPositionSelect);
        
        labelAlignSelect = new Select("Label Align");
        labelAlignSelect.addAll(ALIGN_OPTIONS);
        styleForm.add(labelAlignSelect);
        
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
        Integer formLayout = (Integer) labelsPositionHidden.getValueObject();
        
        form.setLabelsPosition(formLayout.intValue());
        form.setLabelAlign(labelAlignHidden.getValue());
        
        if (formLayout.intValue() == Form.LEFT) {
            labelsPositionSelect.setValue("left");
        } else {
            labelsPositionSelect.setValue("top");
        }
        labelAlignSelect.setValue(labelAlignHidden.getValue());
    }
    
    /**
     * Apply the layout to the form.
     * 
     * @return true
     */
    public boolean onApplyClick() {
        if (labelsPositionSelect.getValue().equals("left")) {
            labelsPositionHidden.setValue(new Integer(Form.LEFT));     
        } else {
            labelsPositionHidden.setValue(new Integer(Form.TOP));               
        }
        
        labelAlignHidden.setValue(labelAlignSelect.getValue());

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
